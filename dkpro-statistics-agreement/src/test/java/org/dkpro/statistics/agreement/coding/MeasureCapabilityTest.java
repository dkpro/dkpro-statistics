/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.statistics.agreement.coding;

import static org.assertj.core.api.Assertions.assertThat;

import org.dkpro.statistics.agreement.IAgreementMeasure;
import org.dkpro.statistics.agreement.IMissingValueSupport;
import org.dkpro.statistics.agreement.IMultiRaterAgreement;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests for the capability markers {@link IMissingValueSupport} and {@link IMultiRaterAgreement}
 * and the {@link IAgreementMeasure#canHandle(org.dkpro.statistics.agreement.IAnnotationStudy)}
 * query that lets applications ask a measure whether it can process a given study (see issue #16).
 */
public class MeasureCapabilityTest
{
    @Test
    public void testMissingValueSupportMarker()
    {
        // Measures that can cope with missing values.
        assertSupportsMissingValues(KrippendorffAlphaAgreement.class, true);
        assertSupportsMissingValues(PercentageAgreement.class, true);
        assertSupportsMissingValues(RandolphKappaAgreement.class, true);
        assertSupportsMissingValues(BennettSAgreement.class, true);
        assertSupportsMissingValues(WeightedKappaAgreement.class, true);

        // Measures that require complete data (they warn on missing values).
        assertSupportsMissingValues(CohenKappaAgreement.class, false);
        assertSupportsMissingValues(ScottPiAgreement.class, false);
        assertSupportsMissingValues(FleissKappaAgreement.class, false);
        assertSupportsMissingValues(HubertKappaAgreement.class, false);
        assertSupportsMissingValues(DiceAgreement.class, false);
        assertSupportsMissingValues(GwetAC1Agreement.class, false);
        assertSupportsMissingValues(GwetAC2Agreement.class, false);
        assertSupportsMissingValues(MaxPercentageAgreement.class, false);
    }

    @Test
    public void testMultiRaterMarker()
    {
        // Measures applicable to studies with more than two raters.
        assertSupportsMultipleRaters(FleissKappaAgreement.class, true);
        assertSupportsMultipleRaters(HubertKappaAgreement.class, true);
        assertSupportsMultipleRaters(PercentageAgreement.class, true);
        assertSupportsMultipleRaters(RandolphKappaAgreement.class, true);
        assertSupportsMultipleRaters(KrippendorffAlphaAgreement.class, true);
        assertSupportsMultipleRaters(WeightedKappaAgreement.class, true);
        assertSupportsMultipleRaters(GwetAC2Agreement.class, true);

        // Measures restricted to exactly two raters.
        assertSupportsMultipleRaters(CohenKappaAgreement.class, false);
        assertSupportsMultipleRaters(ScottPiAgreement.class, false);
        assertSupportsMultipleRaters(BennettSAgreement.class, false);
        assertSupportsMultipleRaters(DiceAgreement.class, false);
        assertSupportsMultipleRaters(GwetAC1Agreement.class, false);
        assertSupportsMultipleRaters(MaxPercentageAgreement.class, false);
    }

    private static void assertSupportsMissingValues(Class<?> measure, boolean expected)
    {
        assertThat(IMissingValueSupport.class.isAssignableFrom(measure))
                .as("%s supports missing values", measure.getSimpleName()).isEqualTo(expected);
    }

    private static void assertSupportsMultipleRaters(Class<?> measure, boolean expected)
    {
        assertThat(IMultiRaterAgreement.class.isAssignableFrom(measure))
                .as("%s supports multiple raters", measure.getSimpleName()).isEqualTo(expected);
    }

    @Test
    public void testCanHandleRaterCount()
    {
        ICodingAnnotationStudy twoRaters = TwoRaterAgreementTest.createExample();
        ICodingAnnotationStudy threeRaters = createThreeRaterStudy();
        ICodingAnnotationStudy oneRater = createOneRaterStudy();

        // A two-rater-only measure accepts two raters but rejects any other number.
        IAgreementMeasure cohen = new CohenKappaAgreement(twoRaters);
        assertThat(cohen.canHandle(twoRaters)).isTrue();
        assertThat(cohen.canHandle(threeRaters)).isFalse();
        assertThat(cohen.canHandle(oneRater)).isFalse();

        // A multi-rater measure accepts two or more raters but not a single rater.
        IAgreementMeasure fleiss = new FleissKappaAgreement(threeRaters);
        assertThat(fleiss.canHandle(twoRaters)).isTrue();
        assertThat(fleiss.canHandle(threeRaters)).isTrue();
        assertThat(fleiss.canHandle(oneRater)).isFalse();
    }

    @Test
    public void testCanHandleMissingValues()
    {
        ICodingAnnotationStudy complete = TwoRaterAgreementTest.createExample();
        ICodingAnnotationStudy withMissingValues = createMissingValueStudy();
        assertThat(withMissingValues.hasMissingValues()).isTrue();

        // A measure that does not support missing values can process the complete study but not the
        // one with missing values ...
        IAgreementMeasure cohen = new CohenKappaAgreement(complete);
        assertThat(cohen.canHandle(complete)).isTrue();
        assertThat(cohen.canHandle(withMissingValues)).isFalse();

        // ... whereas a measure that supports missing values can process both.
        IAgreementMeasure percentage = new PercentageAgreement(complete);
        assertThat(percentage.canHandle(complete)).isTrue();
        assertThat(percentage.canHandle(withMissingValues)).isTrue();

        IAgreementMeasure alpha = new KrippendorffAlphaAgreement(complete,
                new NominalDistanceFunction());
        assertThat(alpha.canHandle(withMissingValues)).isTrue();
    }

    private static ICodingAnnotationStudy createThreeRaterStudy()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("A", "A", "B");
        study.addItem("B", "B", "B");
        study.addItem("A", "B", "A");
        return study;
    }

    private static ICodingAnnotationStudy createOneRaterStudy()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(1);
        study.addItem("A");
        study.addItem("B");
        return study;
    }

    private static ICodingAnnotationStudy createMissingValueStudy()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem("A", "B");
        study.addItem("A", "A");
        study.addItem("B", null);
        return study;
    }
}
