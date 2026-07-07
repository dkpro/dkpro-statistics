/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.statistics.agreement.aligning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

import org.dkpro.statistics.agreement.aligning.alignment.BestAlignmentSolver;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.disorder.IDisorderSampler;
import org.dkpro.statistics.agreement.aligning.dissimilarity.CombinedCategoricalDissimilarity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Cross-validation of {@link GammaAgreement} against pygamma-agreement fixtures.
 * <ul>
 * <li><b>Tier 1</b>: observed disagreement equals the reference best-alignment disorder.</li>
 * <li><b>Tier 2</b>: for every sampled continuum, the disorder computed on its scaled integer units
 * equals the reference; feeding all sampled continua through a replay sampler reproduces the expected
 * disorder (their mean) and the resulting gamma.</li>
 * <li><b>Tier 3</b>: sanity - gamma is finite, and exactly 1.0 for the identical-annotator
 * fixture.</li>
 * </ul>
 */
public class GammaAgreementFixtureTest
{
    /** pygamma computes in float32 -> relative tolerance ~1e-5 on disorders. */
    private static final double REL_TOL = 1e-5;

    static Stream<String> fixtures() throws IOException
    {
        return PygammaFixtures.fixtures();
    }

    /**
     * Replays the fixture's scaled sampled continua: each {@code sampleDisorder()} call pops the next
     * continuum and computes its best-alignment disorder with the real solver. This exercises the
     * full sampler -> solver -> mean pipeline.
     */
    private static final class ReplaySampler
        implements IDisorderSampler
    {
        private final Deque<AnnotationSet> queue;
        private final CombinedCategoricalDissimilarity dissimilarity;

        ReplaySampler(Deque<AnnotationSet> aQueue, CombinedCategoricalDissimilarity aDissimilarity)
        {
            queue = aQueue;
            dissimilarity = aDissimilarity;
        }

        @Override
        public Double sampleDisorder()
        {
            return BestAlignmentSolver.solve(queue.removeFirst(), dissimilarity).disorder();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void tier1ObservedDisagreementMatches(String aFixture)
    {
        JsonNode root = PygammaFixtures.load(aFixture);
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(PygammaFixtures.buildContinuum(root)) //
                .withDissimilarity(PygammaFixtures.buildDissimilarity(root)) //
                .build();

        double expected = root.get("tier1").get("bestAlignmentDisorder").asDouble();
        double tolerance = Math.max(REL_TOL * Math.abs(expected), 1e-9);
        assertThat(gamma.calculateObservedDisagreement()).isCloseTo(expected, offset(tolerance));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void tier2ScaledSampleDisordersMatch(String aFixture)
    {
        JsonNode root = PygammaFixtures.load(aFixture);
        var dissimilarity = PygammaFixtures.buildDissimilarity(root);

        for (JsonNode sample : root.get("tier2").get("sampledContinua")) {
            JsonNode scaled = sample.get("scaled");
            var set = PygammaFixtures.buildAnnotationSet(scaled.get("units"));
            double expected = scaled.get("bestAlignmentDisorder").asDouble();

            double actual = BestAlignmentSolver.solve(set, dissimilarity).disorder();

            double tolerance = Math.max(REL_TOL * Math.abs(expected), 1e-9);
            assertThat(actual).isCloseTo(expected, offset(tolerance));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void tier2ReplayReproducesExpectedDisorderAndGamma(String aFixture)
    {
        JsonNode root = PygammaFixtures.load(aFixture);
        var dissimilarity = PygammaFixtures.buildDissimilarity(root);

        JsonNode samples = root.get("tier2").get("sampledContinua");
        var queue = new ArrayDeque<AnnotationSet>();
        double sumScaled = 0;
        for (JsonNode sample : samples) {
            JsonNode scaled = sample.get("scaled");
            queue.add(PygammaFixtures.buildAnnotationSet(scaled.get("units")));
            sumScaled += scaled.get("bestAlignmentDisorder").asDouble();
        }
        int nSamples = samples.size();
        double meanScaled = sumScaled / nSamples;

        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(PygammaFixtures.buildContinuum(root)) //
                .withDissimilarity(dissimilarity) //
                .withDisorderSampler(new ReplaySampler(queue, dissimilarity)) //
                .withNumberOfSamples(nSamples) //
                .withPrecisionLevel(null) //
                .build();

        double expectedDisorder = gamma.calculateExpectedDisagreement();
        assertThat(expectedDisorder)
                .isCloseTo(meanScaled, offset(Math.max(REL_TOL * Math.abs(meanScaled), 1e-9)));
        assertThat(gamma.getExpectedDisagreementSampleCount()).isEqualTo(nSamples);

        double tier1Obs = root.get("tier1").get("bestAlignmentDisorder").asDouble();
        double expectedGamma = tier1Obs == 0.0 ? 1.0 : 1.0 - tier1Obs / meanScaled;

        // Fresh instance so the observed disorder is recomputed from scratch alongside the replay.
        var replayQueue = new ArrayDeque<AnnotationSet>();
        for (JsonNode sample : samples) {
            replayQueue.add(PygammaFixtures.buildAnnotationSet(sample.get("scaled").get("units")));
        }
        var gammaForAgreement = GammaAgreement.builder() //
                .withAnnotationSet(PygammaFixtures.buildContinuum(root)) //
                .withDissimilarity(dissimilarity) //
                .withDisorderSampler(new ReplaySampler(replayQueue, dissimilarity)) //
                .withNumberOfSamples(nSamples) //
                .withPrecisionLevel(null) //
                .build();

        double actualGamma = gammaForAgreement.calculateAgreement();
        assertThat(actualGamma)
                .isCloseTo(expectedGamma, offset(Math.max(1e-4 * Math.abs(expectedGamma), 1e-9)));

        // Tier 3 sanity: gamma is finite.
        assertThat(Double.isFinite(actualGamma)).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void tier3GammaIsFinite(String aFixture)
    {
        JsonNode root = PygammaFixtures.load(aFixture);
        var dissimilarity = PygammaFixtures.buildDissimilarity(root);

        JsonNode samples = root.get("tier2").get("sampledContinua");
        var queue = new ArrayDeque<AnnotationSet>();
        for (JsonNode sample : samples) {
            queue.add(PygammaFixtures.buildAnnotationSet(sample.get("scaled").get("units")));
        }

        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(PygammaFixtures.buildContinuum(root)) //
                .withDissimilarity(dissimilarity) //
                .withDisorderSampler(new ReplaySampler(queue, dissimilarity)) //
                .withNumberOfSamples(samples.size()) //
                .build();

        double value = gamma.calculateAgreement();
        assertThat(Double.isFinite(value)).isTrue();

        // fixture_03 has identical annotators -> observed disorder 0 -> gamma exactly 1.0.
        if (aFixture.startsWith("fixture_03")) {
            assertThat(value).isEqualTo(1.0);
        }
    }
}
