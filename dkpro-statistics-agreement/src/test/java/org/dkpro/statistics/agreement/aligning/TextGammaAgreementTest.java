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
 * 
 * Original source: https://github.com/fab-bar/TextGammaTool.git
 */
package org.dkpro.statistics.agreement.aligning;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;
import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.calculateExpectedDisagreement;
import static org.dkpro.statistics.agreement.aligning.data.AlignableAnnotationTextUnit.textUnit;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.dkpro.statistics.agreement.InsufficientDataException;
import org.dkpro.statistics.agreement.aligning.data.AlignableAnnotationTextUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotatedText;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.disorder.IDisorderSampler;
import org.dkpro.statistics.agreement.aligning.disorder.SimpleDisorderSampler;
import org.dkpro.statistics.agreement.aligning.dissimilarity.NominalFeatureTextDissimilarity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TextGammaAgreementTest
{
    private static final Rater ANNOTATOR_A = new Rater("A", 0);
    private static final Rater ANNOTATOR_B = new Rater("B", 1);

    // Expected-disorder estimator guarantee: the estimate is within EXPECTED_DISORDER_PRECISION
    // (relative) of the true mean with probability (1 - ALPHA).
    private static final double ALPHA = 0.05;
    private static final double EXPECTED_DISORDER_PRECISION = 0.01;

    // The chance model is an unseeded Monte-Carlo estimate. Rather than pinning a seed, we average
    // this many independent measurements; the mean's standard error shrinks by sqrt(repeats).
    private static final int MEASUREMENT_REPEATS = 25;

    // Head-room between the true agreement and the tolerance. The (1 - ALPHA) guarantee is only ~2
    // sigma, so the original offset(0.01) (~2 sigma on a single measurement) failed ~5% of the time.
    private static final double AGREEMENT_TOLERANCE_STD_ERRORS = 8.0;

    private static double meanAgreement(TextGammaAgreement aMeasure)
    {
        var sum = 0.0;
        for (var i = 0; i < MEASUREMENT_REPEATS; i++) {
            sum += aMeasure.calculateAgreement();
        }
        return sum / MEASUREMENT_REPEATS;
    }

    /**
     * Assertion tolerance for {@link #meanAgreement}, derived from the estimator's guarantee. Since
     * agreement is {@code 1 - observed/expected}, the estimator's relative standard error
     * ({@code EXPECTED_DISORDER_PRECISION / z(1 - alpha/2)}) shows up amplified by
     * {@code |1 - agreement|} (0 for full agreement, largest for total disagreement) and reduced by
     * {@code sqrt(MEASUREMENT_REPEATS)}.
     */
    private static double agreementTolerance(double aExpectedAgreement)
    {
        var z = new NormalDistribution(0, 1).inverseCumulativeProbability(1 - ALPHA / 2);
        var meanStdError = Math.abs(1.0 - aExpectedAgreement) * EXPECTED_DISORDER_PRECISION / z
                / Math.sqrt(MEASUREMENT_REPEATS);
        return AGREEMENT_TOLERANCE_STD_ERRORS * meanStdError;
    }
    private static final List<AlignableAnnotationTextUnit> TOTAL_TEXT_DISAGREEMENT = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a"), //
            textUnit(ANNOTATOR_A, 3, 5, "b"), //
            textUnit(ANNOTATOR_A, 6, 8, "c"), //
            textUnit(ANNOTATOR_B, 0, 2, "c"), //
            textUnit(ANNOTATOR_B, 3, 5, "a"), //
            textUnit(ANNOTATOR_B, 6, 8, "b"));

    private static final List<AlignableAnnotationTextUnit> SOME_TEXT_DISAGREEMENT = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a"), //
            textUnit(ANNOTATOR_A, 3, 5, "b"), //
            textUnit(ANNOTATOR_A, 6, 8, "c"), //
            textUnit(ANNOTATOR_B, 0, 2, "a"), //
            textUnit(ANNOTATOR_B, 3, 5, "c"));
    private static final List<AlignableAnnotationTextUnit> MISSING_ANNOTATION = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a"), //
            textUnit(ANNOTATOR_A, 3, 5, "b"), //
            textUnit(ANNOTATOR_A, 6, 8, "c"), //
            textUnit(ANNOTATOR_B, 3, 5, "b"), //
            textUnit(ANNOTATOR_B, 6, 8, "c"));

    private static final List<AlignableAnnotationTextUnit> FULL_TEXT_AGREEMENT = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a"), //
            textUnit(ANNOTATOR_A, 3, 5, "b"), //
            textUnit(ANNOTATOR_A, 6, 8, "c"), //
            textUnit(ANNOTATOR_B, 0, 2, "a"), //
            textUnit(ANNOTATOR_B, 3, 5, "b"), //
            textUnit(ANNOTATOR_B, 6, 8, "c"));

    static List<Arguments> data_NormalDistributionDisorder_NominalFeatureTextDissimilarity()
    {
        return asList( //
                arguments("Full text agreement", 1.0, "so so so", FULL_TEXT_AGREEMENT), //
                arguments("Missing annotation", 0.6, "so so so", MISSING_ANNOTATION), //
                arguments("Some text disagreement", 0.2, "so so so", SOME_TEXT_DISAGREEMENT), //
                arguments("Total text disagreement", 0.0, "so so so", TOTAL_TEXT_DISAGREEMENT) //
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("data_NormalDistributionDisorder_NominalFeatureTextDissimilarity")
    void testCalculateTextAgreement_NormalDistributionDisorder_NominalFeatureTextDissimilarity(
            String aLabel, double aExpectedAgreement, String aText,
            List<AlignableAnnotationTextUnit> aData)
    {
        var study = new TextAligningAnnotationStudy(aText);
        study.addUnits(aData);

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(new NormalDistributionDisorderSampler()) //
                .withDissimilarity(new NominalFeatureTextDissimilarity()) //
                .withPrecision(EXPECTED_DISORDER_PRECISION) //
                .withStudy(study) //
                .build();

        assertThat(meanAgreement(sut)).isCloseTo(aExpectedAgreement,
                offset(agreementTolerance(aExpectedAgreement)));
    }

    private static final List<AlignableAnnotationTextUnit> FULL_LABEL_AGREEMENT = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a", Map.of("label", "A")), //
            textUnit(ANNOTATOR_A, 3, 5, "b", Map.of("label", "B")), //
            textUnit(ANNOTATOR_A, 6, 8, "c", Map.of("label", "C")), //
            textUnit(ANNOTATOR_B, 0, 2, "a", Map.of("label", "A")), //
            textUnit(ANNOTATOR_B, 3, 5, "b", Map.of("label", "B")), //
            textUnit(ANNOTATOR_B, 6, 8, "c", Map.of("label", "C")));

    private static final List<AlignableAnnotationTextUnit> SOME_LABEL_DISAGREEMENT = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a", Map.of("label", "A")), //
            textUnit(ANNOTATOR_A, 3, 5, "b", Map.of("label", "B")), //
            textUnit(ANNOTATOR_A, 6, 8, "c", Map.of("label", "C")), //
            textUnit(ANNOTATOR_B, 0, 2, "a", Map.of("label", "A")), //
            textUnit(ANNOTATOR_B, 3, 5, "b", Map.of("label", "A")), //
            textUnit(ANNOTATOR_B, 6, 8, "c", Map.of("label", "C")));

    private static final List<AlignableAnnotationTextUnit> MISSING_LABEL = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a", Map.of("label", "A")), //
            textUnit(ANNOTATOR_A, 3, 5, "b", Map.of("label", "B")), //
            textUnit(ANNOTATOR_A, 6, 8, "c", Map.of("label", "C")), //
            textUnit(ANNOTATOR_B, 0, 2, "a"), //
            textUnit(ANNOTATOR_B, 3, 5, "b", Map.of("label", "B")), //
            textUnit(ANNOTATOR_B, 6, 8, "c", Map.of("label", "C")));

    private static final List<AlignableAnnotationTextUnit> TOTAL_LABEL_DISAGREEMENT = asList( //
            textUnit(ANNOTATOR_A, 0, 2, "a", Map.of("label", "A")), //
            textUnit(ANNOTATOR_A, 3, 5, "b", Map.of("label", "B")), //
            textUnit(ANNOTATOR_A, 6, 8, "c", Map.of("label", "C")), //
            textUnit(ANNOTATOR_B, 0, 2, "a", Map.of("label", "B")), //
            textUnit(ANNOTATOR_B, 3, 5, "b", Map.of("label", "C")), //
            textUnit(ANNOTATOR_B, 6, 8, "c", Map.of("label", "A")));
    
    static List<Arguments> data_SimpleDisorder_NominalFeatureTextDissimilarity()
    {
        return asList( //
                arguments("Full label agreement", 1.0, "so so so", FULL_LABEL_AGREEMENT), //
                arguments("Missing label", 0.53, "so so so", MISSING_LABEL), //
                arguments("Some label disagreement", 0.45, "so so so", SOME_LABEL_DISAGREEMENT), //
                arguments("Total label disagreement", -0.5, "so so so", TOTAL_LABEL_DISAGREEMENT) //
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("data_SimpleDisorder_NominalFeatureTextDissimilarity")
    void testCalculateTextAgreement_SimpleDisorder_NominalFeatureTextDissimilarity(
            String aLabel, double aExpectedAgreement, String aText,
            List<AlignableAnnotationTextUnit> aData)
    {
        var study = new TextAligningAnnotationStudy(aText);
        study.addUnits(aData);

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(m -> new SimpleDisorderSampler(m, 0.0, 0.0)) //
                .withDissimilarity(new NominalFeatureTextDissimilarity()) //
                .withStudy(study) //
                .build();

        assertThat(sut.calculateAgreement()).isCloseTo(aExpectedAgreement, offset(0.02));
    }    
    
    @Test
    void testCalculateTextAgreementWithTexts_LabelTextPositionDisagreement()
    {
        var text1 = new AnnotatedText("so so so", asList( //
                textUnit(ANNOTATOR_A, 0, 2, "a"), //
                textUnit(ANNOTATOR_A, 3, 5, "b"), //
                textUnit(ANNOTATOR_A, 6, 8, "c")));

        var text2 = new AnnotatedText("so so", asList( //
                textUnit(ANNOTATOR_B, 0, 2, "b"), //
                textUnit(ANNOTATOR_B, 3, 5, "c")));

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(new NormalDistributionDisorderSampler()) //
                .withDissimilarity(new NominalFeatureTextDissimilarity()) //
                .withPrecision(EXPECTED_DISORDER_PRECISION) //
                .withTexts(text1, text2) //
                .build();

        assertThat(meanAgreement(sut)).isCloseTo(0.6, offset(agreementTolerance(0.6)));
    }

    @Test
    void testGetObservedDisorder()
    {
        var annots1 = asList( //
                textUnit(ANNOTATOR_A, 0, 2, "a"), //
                textUnit(ANNOTATOR_A, 3, 5, "b"), //
                textUnit(ANNOTATOR_A, 6, 8, "c"), //
                textUnit(ANNOTATOR_A, 10, 13, "abc"));
        var text1 = new AnnotatedText("so so so so so", annots1);

        var annots2 = asList( //
                textUnit(ANNOTATOR_B, 0, 2, "b"), //
                textUnit(ANNOTATOR_B, 3, 5, "c"), // 
                textUnit(ANNOTATOR_B, 7, 9, "bc"));
        var text2 = new AnnotatedText("so so so ", annots2);

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(() -> 0.0) //
                .withTexts(text1, text2) //
                .build();

        // The min-disorder alignment leaves A's "a" unmatched (gap = 1.0) and pairs "abc"/"bc"
        // (text mismatch = 1.0), i.e. 2.0 / 3.5 average annotations = 4/7.
        assertThat(sut.calculateObservedDisagreement()).isCloseTo(4.0 / 7.0, offset(0.001));
    }

    @Test
    void testBaseText_absentByDefaultForStudy()
    {
        var study = new TextAligningAnnotationStudy("so so so");
        study.addUnits(FULL_TEXT_AGREEMENT);

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(() -> 0.0) //
                .withStudy(study) //
                .build();

        // No base text is resolved automatically: without an explicit reference we do not assume a
        // reference segmentation, so the sampler stays symmetric over both raters.
        assertThat(sut.getBaseText()).isEmpty();
    }

    @Test
    void testBaseText_absentByDefaultForDifferentTexts()
    {
        var text1 = new AnnotatedText("so so so", asList(textUnit(ANNOTATOR_A, 0, 2, "a")));
        var text2 = new AnnotatedText("so so", asList(textUnit(ANNOTATOR_B, 0, 2, "b")));

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(() -> 0.0) //
                .withTexts(text1, text2) //
                .build();

        assertThat(sut.getBaseText()).isEmpty();
    }

    @Test
    void testBaseText_explicitOverride()
    {
        var text1 = new AnnotatedText("so so so", asList(textUnit(ANNOTATOR_A, 0, 2, "a")));
        var text2 = new AnnotatedText("so so", asList(textUnit(ANNOTATOR_B, 0, 2, "b")));
        var base = new AnnotatedText("base text", asList(textUnit(ANNOTATOR_A, 0, 4, "base")));

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(() -> 0.0) //
                .withTexts(text1, text2) //
                .withBaseText(base) //
                .build();

        // An explicitly supplied base text always wins, even when the texts differ.
        assertThat(sut.getBaseText()).isPresent();
        assertThat(sut.getBaseText().get().getText()).isEqualTo("base text");
    }

    @Test
    void testCalculateExpectedDisagreement_NormalDistribution()
    {
        var disorderSampler = new NormalDistributionDisorderSampler();
        var iterations = 10_000;
        var precision = 0.02;
        var alpha = 0.05;

        var withinMargin = 0;
        var tooSmall = 0;
        var tooBig = 0;

        for (var i = 0; i < iterations; i++) {
            var expectedDisorder = calculateExpectedDisagreement(disorderSampler, alpha, precision);

            if (1 - precision <= expectedDisorder && expectedDisorder <= 1 + precision) {
                withinMargin += 1;
            }
            else if (expectedDisorder < 1 - precision) {
                tooSmall += 1;
            }
            else {
                tooBig += 1;
            }
        }

        // The estimator promises that its result lies within +/- precision of the true mean with
        // probability (1 - alpha). We verify that guarantee directly by pooling all results and
        // checking that at least (1 - alpha) of them fall within the margin.
        //
        // Note: we deliberately do NOT slice the runs into sub-batches and require each batch to
        // clear the bar. The (1 - alpha) guarantee means ~alpha of the results are expected to fall
        // outside the margin; a per-batch minimum check fishes across many small, noisy batches for
        // the one where those expected outliers happened to cluster, and so fails intermittently
        // even when the guarantee holds. Pooling uses all the evidence at once (standard error here
        // ~0.0018 over 10,000 runs), so the measured ~0.965 coverage clears the 0.95 floor by ~8
        // sigma - no flakiness - while still catching a genuine regression below ~0.945.
        assertThat(withinMargin / (double) iterations)
                .as("within margin: %d -- too big: %d - too small: %d", withinMargin, tooBig,
                        tooSmall)
                .isGreaterThanOrEqualTo(1 - alpha);
    }

    private static double agreementWithSeed(long aSeed)
    {
        var study = new TextAligningAnnotationStudy("so so so");
        study.addUnits(SOME_LABEL_DISAGREEMENT);
        return TextGammaAgreement.builder() //
                .withSeed(aSeed) //
                .withDisorderSampler(m -> new SimpleDisorderSampler(m, 0.0, 0.0)) //
                .withDissimilarity(new NominalFeatureTextDissimilarity()) //
                .withStudy(study) //
                .build() //
                .calculateAgreement();
    }

    @Test
    void testDegenerateChanceModelThrowsInsufficientData()
    {
        // Text-only annotations (no categories to shuffle) combined with a sampler that applies no
        // text or segmentation changes means the chance model can never introduce any disorder, so
        // the expected disorder is zero. There is then no chance baseline to correct against, and
        // the agreement is an undefined division by zero - which must surface as a clear exception
        // rather than -Infinity.
        var study = new TextAligningAnnotationStudy("so so so");
        study.addUnits(SOME_TEXT_DISAGREEMENT);

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(m -> new SimpleDisorderSampler(m, 0.0, 0.0)) //
                .withDissimilarity(new NominalFeatureTextDissimilarity()) //
                .withStudy(study) //
                .build();

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(sut::calculateAgreement);
    }

    @Test
    void testSeedMakesMeasurementReproducible()
    {
        // A fixed seed must make the (otherwise Monte-Carlo) measurement fully reproducible: the
        // same study yields exactly the same agreement value on every run.
        assertThat(agreementWithSeed(42)).isEqualTo(agreementWithSeed(42));

        // A different seed explores a different set of random annotators, so the estimate differs.
        assertThat(agreementWithSeed(42)).isNotEqualTo(agreementWithSeed(99));
    }

    private class NormalDistributionDisorderSampler
        implements IDisorderSampler
    {
        private final NormalDistribution sn = new NormalDistribution(1, 1);

        @Override
        public Double sampleDisorder()
        {
            return sn.sample();
        }
    }
}
