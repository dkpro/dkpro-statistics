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
import java.util.stream.Stream;

import org.dkpro.statistics.agreement.aligning.disorder.StatisticalContinuumDisorderSampler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tier-3 statistical validation of the full gamma pipeline (observed disorder + statistical
 * continuum sampler + expected disorder). Both the Java side and the pygamma reference are
 * 30-sample Monte-Carlo estimates over independent PRNG streams, so gamma values are only
 * <em>statistically</em> comparable, not bit-identical.
 */
class GammaAgreementEndToEndTest
{
    private static final long SEED = 20240704L;

    /** Loose Monte-Carlo band: both sides are independent 30-sample estimates. */
    private static final double GAMMA_BAND = 0.2;

    static Stream<String> fixtures() throws IOException
    {
        return PygammaFixtures.fixtures();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void gammaMatchesPygammaWithinMonteCarloBand(String aFixture)
    {
        var root = PygammaFixtures.load(aFixture);
        var continuum = PygammaFixtures.buildContinuum(root);
        var dissimilarity = PygammaFixtures.buildDissimilarity(root);

        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(continuum) //
                .withDissimilarity(dissimilarity) //
                .withDisorderSampler(StatisticalContinuumDisorderSampler::new) //
                .withSeed(SEED) //
                .build();

        double gammaJava = gamma.calculateAgreement();
        double gammaPy = root.get("tier3").get("gamma").asDouble();

        assertThat(Double.isFinite(gammaJava)).as("%s: gamma must be finite", aFixture).isTrue();
        assertThat(gammaJava).as("%s: gamma <= 1", aFixture).isLessThanOrEqualTo(1.0);

        if ("fixture_03_identical_perfect.json".equals(aFixture)) {
            // Observed disorder is exactly 0 for a perfect continuum -> gamma == 1 without
            // sampling.
            assertThat(gammaJava).as("%s: perfect continuum gamma", aFixture).isEqualTo(1.0);
        }
        else {
            assertThat(gammaJava)
                    .as("%s: gamma_java=%s vs tier3 gamma=%s", aFixture, gammaJava, gammaPy)
                    .isCloseTo(gammaPy, offset(GAMMA_BAND));
        }
    }

    @Test
    void expectedDisorderMatchesPygammaWithLargeSampleCount()
    {
        // A mid-size fixture (32 units, 2 raters, 3 categories) with a tighter statistical check:
        // 300 samples on each side should agree on the expected disorder within 15% relative.
        var root = PygammaFixtures.load("fixture_05_larger_two_annotators.json");
        var continuum = PygammaFixtures.buildContinuum(root);
        var dissimilarity = PygammaFixtures.buildDissimilarity(root);

        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(continuum) //
                .withDissimilarity(dissimilarity) //
                .withDisorderSampler(StatisticalContinuumDisorderSampler::new) //
                .withSeed(SEED) //
                .withNumberOfSamples(300) //
                .build();

        double expectedJava = gamma.calculateExpectedDisagreement();
        double expectedPy = root.get("tier3").get("expectedDisorder").asDouble();

        double relative = Math.abs(expectedJava - expectedPy) / expectedPy;

        // Do NOT widen this band: a failure indicates a sampler-distribution mismatch worth
        // investigating, not a flaky test.
        assertThat(relative).as("expected disorder java=%s vs pygamma=%s (relative diff %s)",
                expectedJava, expectedPy, relative).isLessThan(0.15);
    }

    @Test
    void sameSeedProducesIdenticalGamma()
    {
        var root = PygammaFixtures.load("fixture_01_two_annotators_simple.json");
        var continuum = PygammaFixtures.buildContinuum(root);
        var dissimilarity = PygammaFixtures.buildDissimilarity(root);

        double first = GammaAgreement.builder().withAnnotationSet(continuum)
                .withDissimilarity(dissimilarity)
                .withDisorderSampler(StatisticalContinuumDisorderSampler::new).withSeed(SEED)
                .build().calculateAgreement();
        double second = GammaAgreement.builder().withAnnotationSet(continuum)
                .withDissimilarity(dissimilarity)
                .withDisorderSampler(StatisticalContinuumDisorderSampler::new).withSeed(SEED)
                .build().calculateAgreement();

        assertThat(first).isEqualTo(second);
    }
}
