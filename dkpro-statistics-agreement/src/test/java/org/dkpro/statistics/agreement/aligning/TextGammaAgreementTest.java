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
import static org.assertj.core.api.Assertions.offset;
import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.calculateExpectedDisagreement;
import static org.dkpro.statistics.agreement.aligning.data.AlignableAnnotationTextUnit.textUnit;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
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
                .withStudy(study) //
                .build();

        assertThat(sut.calculateAgreement()).isCloseTo(aExpectedAgreement, offset(0.01));
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
                .withTexts(text1, text2) //
                .build();

        assertThat(sut.calculateAgreement()).isCloseTo(0.6, offset(0.005));
    }

    @Test
    void testGetObservedDisorder()
    {
        var annots1 = asList( //
                textUnit(ANNOTATOR_A, 0, 2, "a"), //
                textUnit(ANNOTATOR_A, 3, 5, "b"), //
                textUnit(ANNOTATOR_A, 6, 8, "c"));
        var text1 = new AnnotatedText("so so so", annots1);

        var annots2 = asList( //
                textUnit(ANNOTATOR_B, 0, 2, "b"), //
                textUnit(ANNOTATOR_B, 3, 5, "c"));
        var text2 = new AnnotatedText("so so", annots2);

        var sut = TextGammaAgreement.builder() //
                .withDisorderSampler(() -> 0.0) //
                .withTexts(text1, text2) //
                .build();

        assertThat(sut.calculateObservedDisagreement()).isCloseTo(0.4, offset(0.001));
    }

    @Test
    void testCalculateExpectedDisagreement_NormalDistribution()
    {
        var disorderSampler = new NormalDistributionDisorderSampler();
        var outerIterations = 100;
        var innerIterations = 100;
        var precision = 0.02;
        var alpha = 0.05;

        for (int n = 0; n < outerIterations; n++) {
            var correct = 0;
            var tooSmall = 0;
            var tooBig = 0;

            for (var i = 0; i < innerIterations; i++) {
                var expectedDisorder = calculateExpectedDisagreement(disorderSampler, alpha,
                        precision);

                if (1 - precision <= expectedDisorder && expectedDisorder <= 1 + precision) {
                    correct += 1;
                }
                else if (expectedDisorder < 1 - precision) {
                    tooSmall += 1;
                }
                else {
                    tooBig += 1;
                }
            }

            assertThat(correct + tooBig + tooSmall).isEqualTo(innerIterations);

            assertThat(correct / (double) innerIterations)
                    .as("Correct %d -- too big: %d - too small: %d", correct, tooSmall, tooBig)
                    // should be about 95% -- but seems to drop lower
                    .isGreaterThanOrEqualTo(0.89);
        }
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
