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
package org.dkpro.statistics.agreement.aligning.disorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.GammaAgreement;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.junit.jupiter.api.Test;

class StatisticalContinuumDisorderSamplerTest
{
    private static final Rater ANNOTATOR_A = new Rater("A", 0);
    private static final Rater ANNOTATOR_B = new Rater("B", 1);

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd, String aCat)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, Map.of("category", aCat));
    }

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd,
            Map<String, String> aFeatures)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, aFeatures);
    }

    private static GammaAgreement measure(AnnotationSet aSet, long aSeed)
    {
        return GammaAgreement.builder().withAnnotationSet(aSet).withSeed(aSeed).build();
    }

    @Test
    void statisticsAreExtractedFromReferenceContinuum()
    {
        // Rater A: [1,5] a, [10,14] b ; Rater B: [3,7] a
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, "a"), //
                unit(ANNOTATOR_A, 10, 14, "b"), //
                unit(ANNOTATOR_B, 3, 7, "a")));

        var sampler = new StatisticalContinuumDisorderSampler(measure(set, 1));

        // Units per rater: A=2, B=1 -> [2,1]. mean 1.5, population std sqrt(0.25) = 0.5.
        assertThat(sampler.getAverageNumberOfUnitsPerRater()).isCloseTo(1.5, offset(1e-12));
        assertThat(sampler.getStandardDeviationOfUnitsPerRater()).isCloseTo(0.5, offset(1e-12));

        // Gaps: seed [0]; A consecutive 10-5=5; B has no consecutive gap; first-starts A=1, B=3.
        // gaps = [0, 5, 1, 3]. mean 9/4 = 2.25 ; population std = sqrt(14.75/4) = sqrt(3.6875).
        assertThat(sampler.getAverageGap()).isCloseTo(2.25, offset(1e-12));
        assertThat(sampler.getStandardDeviationOfGap()).isCloseTo(Math.sqrt(3.6875), offset(1e-12));

        // Durations: all three units span 4 -> mean 4, std 0.
        assertThat(sampler.getAverageUnitDuration()).isCloseTo(4.0, offset(1e-12));
        assertThat(sampler.getStandardDeviationOfUnitDuration()).isCloseTo(0.0, offset(1e-12));

        // Categories over all units: a x2, b x1 -> sorted [a, b], weights [2/3, 1/3].
        assertThat(sampler.getCategories()).containsExactly("a", "b");
        assertThat(sampler.getCategoryWeights()).containsExactly(2.0 / 3.0, 1.0 / 3.0);

        assertThat(sampler.getFeatureName()).isEqualTo("category");
    }

    @Test
    void zeroStandardDeviationDoesNotThrowAndProducesDeterministicMeans()
    {
        // Identical counts (2 each) and identical durations (4 each) across both raters.
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 0, 4, "a"), //
                unit(ANNOTATOR_A, 10, 14, "a"), //
                unit(ANNOTATOR_B, 0, 4, "a"), //
                unit(ANNOTATOR_B, 10, 14, "a")));

        var sampler = new StatisticalContinuumDisorderSampler(measure(set, 7));

        // std of unit-count and duration are both 0 (would make commons-math NormalDistribution throw).
        assertThat(sampler.getStandardDeviationOfUnitsPerRater()).isCloseTo(0.0, offset(1e-12));
        assertThat(sampler.getStandardDeviationOfUnitDuration()).isCloseTo(0.0, offset(1e-12));

        // Sampling must not throw; each rater gets exactly the (deterministic) mean count of 2 units,
        // each of the deterministic mean duration 4.
        for (int i = 0; i < 20; i++) {
            var sample = sampler.sampleContinuum();
            assertThat(sample.getRaterCount()).isEqualTo(2);
            assertThat(sample.getUnitsWithRater(ANNOTATOR_A)).hasSize(2);
            assertThat(sample.getUnitsWithRater(ANNOTATOR_B)).hasSize(2);
            for (var u : sample.getUnits()) {
                assertThat(u.getEnd() - u.getBegin()).isEqualTo(4L);
            }
        }
    }

    @Test
    void sameSeedProducesIdenticalDisorderSequences()
    {
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, "a"), //
                unit(ANNOTATOR_A, 10, 14, "b"), //
                unit(ANNOTATOR_B, 3, 7, "a"), //
                unit(ANNOTATOR_B, 12, 16, "b")));

        var s1 = new StatisticalContinuumDisorderSampler(measure(set, 42));
        var s2 = new StatisticalContinuumDisorderSampler(measure(set, 42));

        var seq1 = new ArrayList<Double>();
        var seq2 = new ArrayList<Double>();
        for (int i = 0; i < 10; i++) {
            seq1.add(s1.sampleDisorder());
            seq2.add(s2.sampleDisorder());
        }

        assertThat(seq1).isEqualTo(seq2);
    }

    @Test
    void differentSeedsProduceDifferentDisorderSequences()
    {
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, "a"), //
                unit(ANNOTATOR_A, 10, 14, "b"), //
                unit(ANNOTATOR_B, 3, 7, "a"), //
                unit(ANNOTATOR_B, 12, 16, "b")));

        var s1 = new StatisticalContinuumDisorderSampler(measure(set, 1));
        var s2 = new StatisticalContinuumDisorderSampler(measure(set, 2));

        var seq1 = new ArrayList<Double>();
        var seq2 = new ArrayList<Double>();
        for (int i = 0; i < 10; i++) {
            seq1.add(s1.sampleDisorder());
            seq2.add(s2.sampleDisorder());
        }

        assertThat(seq1).isNotEqualTo(seq2);
    }

    @Test
    void sampledContinuaAreWellFormed()
    {
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, "a"), //
                unit(ANNOTATOR_A, 10, 14, "b"), //
                unit(ANNOTATOR_A, 20, 25, "c"), //
                unit(ANNOTATOR_B, 3, 7, "a"), //
                unit(ANNOTATOR_B, 12, 16, "b")));

        var sampler = new StatisticalContinuumDisorderSampler(measure(set, 123));
        var referenceCategories = List.of("a", "b", "c");

        for (int i = 0; i < 50; i++) {
            var sample = sampler.sampleContinuum();

            // Every rater of the reference is present in every sample.
            assertThat(sample.getRaters()).containsExactlyInAnyOrder(ANNOTATOR_A, ANNOTATOR_B);

            for (var u : sample.getUnits()) {
                // Well-formed spans.
                assertThat(u.getBegin()).isLessThan(u.getEnd());

                // The category feature is actually retained on the unit (guards the 4-arg-constructor
                // trap that silently drops the features map).
                var category = u.getFeatureValue("category");
                assertThat(category).isNotNull();
                assertThat(category).isIn(referenceCategories);
                assertThat(u.getFeatureNames()).containsExactly("category");
            }
        }
    }

    @Test
    void featureNameIsAutoDetectedWhenUnique()
    {
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, "a"), //
                unit(ANNOTATOR_B, 3, 7, "b")));

        var sampler = new StatisticalContinuumDisorderSampler(measure(set, 1));
        assertThat(sampler.getFeatureName()).isEqualTo("category");
    }

    @Test
    void multipleFeatureNamesRequireExplicitName()
    {
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, Map.of("cat", "a", "pos", "x")), //
                unit(ANNOTATOR_B, 3, 7, Map.of("cat", "b", "pos", "y"))));

        var m = measure(set, 1);

        // Auto-detection fails with more than one distinct feature name.
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new StatisticalContinuumDisorderSampler(m));

        // ... but an explicit feature name works and is used as the category.
        var sampler = new StatisticalContinuumDisorderSampler(m, "cat");
        assertThat(sampler.getFeatureName()).isEqualTo("cat");
        assertThat(sampler.getCategories()).containsExactly("a", "b");

        var sample = sampler.sampleContinuum();
        for (var u : sample.getUnits()) {
            assertThat(u.getFeatureNames()).containsExactly("cat");
            assertThat(u.getFeatureValue("cat")).isIn("a", "b");
        }
    }

    @Test
    void wiresInThroughSamplerFactory()
    {
        var set = new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 1, 5, "a"), //
                unit(ANNOTATOR_A, 10, 14, "b"), //
                unit(ANNOTATOR_B, 3, 7, "a"), //
                unit(ANNOTATOR_B, 12, 16, "b")));

        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(set) //
                .withDisorderSampler(StatisticalContinuumDisorderSampler::new) //
                .withSeed(99) //
                .withNumberOfSamples(30) //
                .build();

        double agreement = gamma.calculateAgreement();
        assertThat(Double.isFinite(agreement)).isTrue();
        assertThat(agreement).isLessThanOrEqualTo(1.0);
    }
}
