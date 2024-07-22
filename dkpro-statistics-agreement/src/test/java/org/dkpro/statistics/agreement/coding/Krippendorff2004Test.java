/*
 * Copyright 2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 * 
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
import static org.assertj.core.api.Assertions.offset;

import org.dkpro.statistics.agreement.distance.IDistanceFunction;
import org.dkpro.statistics.agreement.distance.IntervalDistanceFunction;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.dkpro.statistics.agreement.distance.OrdinalDistanceFunction;
import org.dkpro.statistics.agreement.distance.RatioDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests based on Krippendorff (2004) for measuring {@link KrippendorffAlphaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology. Thousand Oaks, CA:
 * Sage Publications, 2004.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class Krippendorff2004Test
{
    @Test
    public void testDichotomy()
    {
        ICodingAnnotationStudy study = createExample1();

        PercentageAgreement pa = new PercentageAgreement(study);
        assertThat(pa.calculateAgreement()).isCloseTo(0.600, offset(0.001));

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.400, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.442, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.095, offset(0.001));
    }

    @Test
    public void testMultipleCategories()
    {
        ICodingAnnotationStudy study = createExample2();

        PercentageAgreement pa = new PercentageAgreement(study);
        assertThat(pa.calculateAgreement()).isCloseTo(0.833, offset(0.001));

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.1667, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.6268, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.734, offset(0.001));
    }

    @Test
    public void testMultipleRatersMissingValues()
    {
        ICodingAnnotationStudy study = createExample3();

        PercentageAgreement pa = new PercentageAgreement(study);
        assertThat(pa.calculateAgreement()).isCloseTo(0.800, offset(0.001));

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.200, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.779, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.743, offset(0.001));
    }

    @Test
    public void testOrdinalMetric()
    {
        CodingAnnotationStudy study = createExample3a(0);

        IDistanceFunction distFunc = new OrdinalDistanceFunction();

        final double[][] EXPECTED = new double[][] { { 0.0, 11.0, 22.5, 30.0, 32.5, 34.0 },
                { 11.0, 0.0, 11.5, 19.0, 21.5, 23.0 }, { 22.5, 11.5, 0.0, 7.5, 10.0, 11.5 },
                { 30.0, 19.0, 7.5, 0.0, 2.5, 4.0 }, { 32.5, 21.5, 10.0, 2.5, 0.0, 1.5 },
                { 34.0, 23.0, 11.5, 4.0, 1.5, 0.0 } };
        int i = 0;
        int j = 0;
        for (Object category1 : study.getCategories()) {
            for (Object category2 : study.getCategories()) {
                assertThat(distFunc.measureDistance(study, category1, category2))
                        .as("item " + category1 + "," + category2)
                        .isCloseTo(EXPECTED[i][j] * EXPECTED[i][j], offset(0.001));
                j++;
            }
            i++;
            j = 0;
        }
    }

    @Test
    public void testIntervallMetric()
    {
        CodingAnnotationStudy study = createExample3a(-2);

        IDistanceFunction distFunc = new IntervalDistanceFunction();

        final double[][] EXPECTED = new double[][] { { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 },
                { 1.0, 0.0, 1.0, 2.0, 3.0, 4.0 }, { 2.0, 1.0, 0.0, 1.0, 2.0, 3.0 },
                { 3.0, 2.0, 1.0, 0.0, 1.0, 2.0 }, { 4.0, 3.0, 2.0, 1.0, 0.0, 1.0 },
                { 5.0, 4.0, 3.0, 2.0, 1.0, 0.0 } };
        int i = 0;
        int j = 0;
        for (Object category1 : study.getCategories()) {
            for (Object category2 : study.getCategories()) {
                assertThat(distFunc.measureDistance(study, category1, category2))
                        .as("item " + category1 + "," + category2)
                        .isCloseTo(EXPECTED[i][j] * EXPECTED[i][j], offset(0.001));
                j++;
            }
            i++;
            j = 0;
        }
    }

    @Test
    public void testRatioMetric()
    {
        CodingAnnotationStudy study = createExample3a(-1);

        IDistanceFunction distFunc = new RatioDistanceFunction();

        final double[][] EXPECTED = new double[][] {
                { 0.0, 1.0 / 1.0, 2.0 / 2.0, 3.0 / 3.0, 4.0 / 4.0, 5.0 / 5.0 },
                { 1.0 / 1.0, 0.0 / 2.0, 1.0 / 3.0, 2.0 / 4.0, 3.0 / 5.0, 4.0 / 6.0 },
                { 2.0 / 2.0, 1.0 / 3.0, 0.0 / 4.0, 1.0 / 5.0, 2.0 / 6.0, 3.0 / 7.0 },
                { 3.0 / 3.0, 2.0 / 4.0, 1.0 / 5.0, 0.0 / 6.0, 1.0 / 7.0, 2.0 / 8.0 },
                { 4.0 / 4.0, 3.0 / 5.0, 2.0 / 6.0, 1.0 / 7.0, 0.0 / 8.0, 1.0 / 9.0 },
                { 5.0 / 5.0, 4.0 / 6.0, 3.0 / 7.0, 2.0 / 8.0, 1.0 / 9.0, 0.0 / 10.0 } };

        int i = 0;
        int j = 0;
        for (Object category1 : study.getCategories()) {
            for (Object category2 : study.getCategories()) {
                assertThat(distFunc.measureDistance(study, category1, category2))
                        .as("item " + category1 + "," + category2)
                        .isCloseTo(EXPECTED[i][j] * EXPECTED[i][j], offset(0.001));
                j++;
            }
            i++;
            j = 0;
        }
    }

    @Test
    public void testIntervallAgreement()
    {
        CodingAnnotationStudy study = createExample3b();

        IDistanceFunction distFunc = new IntervalDistanceFunction();

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, distFunc);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.433, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(2.872, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.849, offset(0.001));

    }

    @Test
    public void testOtherCoefficients()
    {
        CodingAnnotationStudy study = createExample4a();

        PercentageAgreement pa = new PercentageAgreement(study);
        assertThat(pa.calculateAgreement()).isCloseTo(0.460, offset(0.001));
        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateAgreement()).isCloseTo(0.190, offset(0.001));
        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(0.186, offset(0.001));
        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateAgreement()).isCloseTo(0.186, offset(0.001));

        study = createExample4b();

        pa = new PercentageAgreement(study);
        assertThat(pa.calculateAgreement()).isCloseTo(0.460, offset(0.001));
        s = new BennettSAgreement(study);
        assertThat(s.calculateAgreement()).isCloseTo(0.190, offset(0.001));
        pi = new ScottPiAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(0.186, offset(0.001));
        kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateAgreement()).isCloseTo(0.258, offset(0.001));
    }

    /**
     * Creates an example annotation study introduced by Krippendorff (2004: p. 224).
     */
    public static CodingAnnotationStudy createExample1()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(1, 0);
        study.addItem(1, 1);
        study.addItem(0, 1);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 1);
        study.addItem(0, 0);
        study.addItem(0, 1);
        study.addItem(0, 0);
        study.addItem(0, 0);
        return study;
    }

    /**
     * Creates an example annotation study introduced by Krippendorff (2004: p. 227).
     */
    public static CodingAnnotationStudy createExample2()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem("a", "a");
        study.addItem("a", "b");
        study.addItem("b", "b");
        study.addItem("b", "b");
        study.addItem("b", "b");
        study.addItem("b", "b");
        study.addItem("b", "c");
        study.addItem("c", "c");
        study.addItem("c", "c");
        study.addItem("c", "c");
        study.addItem("c", "c");
        study.addItem("c", "c");
        return study;
    }

    /**
     * Creates an example annotation study introduced by Krippendorff (2004: p. 230).
     */
    public static CodingAnnotationStudy createExample3()
    {
        // B = book symbol, L = letter symbol, P = phone symbol,
        // C = computer symbol, F = folder symbol.
        CodingAnnotationStudy study = new CodingAnnotationStudy(4);
        study.addItem("B", "B", null, "B");
        study.addItem("L", "L", "P", "L");
        study.addItem("P", "P", "P", "P");
        study.addItem("P", "P", "P", "P");
        study.addItem("L", "L", "L", "L");
        study.addItem("B", "L", "P", "C");
        study.addItem("C", "C", "C", "C");
        study.addItem("B", "B", "L", "B");
        study.addItem("L", "L", "L", "L");
        study.addItem(null, "F", "F", "F");
        study.addItem(null, null, "B", "B");
        study.addItem(null, null, "P", null);
        return study;
    }

    /**
     * Creates a variant of the annotation study introduced by Krippendorff (2004: p. 230).
     */
    public static CodingAnnotationStudy createExample3a(int catOffset)
    {
        // 1 = book symbol, 2 = letter symbol, 3 = phone symbol,
        // 4 = computer symbol, 5 = new empty symbol, 6 = folder symbol.
        CodingAnnotationStudy study = new CodingAnnotationStudy(4);
        study.addCategory(1 + catOffset);
        study.addCategory(2 + catOffset);
        study.addCategory(3 + catOffset);
        study.addCategory(4 + catOffset);
        study.addCategory(5 + catOffset);
        study.addCategory(6 + catOffset);
        study.addItem(1 + catOffset, 1 + catOffset, null, 1 + catOffset);
        study.addItem(2 + catOffset, 2 + catOffset, 3 + catOffset, 2 + catOffset);
        study.addItem(3 + catOffset, 3 + catOffset, 3 + catOffset, 3 + catOffset);
        study.addItem(3 + catOffset, 3 + catOffset, 3 + catOffset, 3 + catOffset);
        study.addItem(2 + catOffset, 2 + catOffset, 2 + catOffset, 2 + catOffset);
        study.addItem(1 + catOffset, 2 + catOffset, 3 + catOffset, 4 + catOffset);
        study.addItem(4 + catOffset, 4 + catOffset, 4 + catOffset, 4 + catOffset);
        study.addItem(1 + catOffset, 1 + catOffset, 2 + catOffset, 1 + catOffset);
        study.addItem(2 + catOffset, 2 + catOffset, 2 + catOffset, 2 + catOffset);
        study.addItem(null, 6 + catOffset, 6 + catOffset, 6 + catOffset);
        study.addItem(null, null, 1 + catOffset, 1 + catOffset);
        study.addItem(null, null, 3 + catOffset, null);
        return study;
    }

    /**
     * Creates a variant of the annotation study introduced by Krippendorff (2004: p. 230).
     */
    public static CodingAnnotationStudy createExample3b()
    {
        // B = book symbol, L = letter symbol, P = phone symbol,
        // C = computer symbol, F = folder symbol.
        CodingAnnotationStudy study = new CodingAnnotationStudy(4);
        study.addItem(1, 1, null, 1);
        study.addItem(2, 2, 3, 2);
        study.addItem(3, 3, 3, 3);
        study.addItem(3, 3, 3, 3);
        study.addItem(2, 2, 2, 2);
        study.addItem(1, 2, 3, 4);
        study.addItem(4, 4, 4, 4);
        study.addItem(1, 1, 2, 1);
        study.addItem(2, 2, 2, 2);
        study.addItem(null, 5, 5, 5);
        study.addItem(null, null, 1, 1);
        study.addItem(null, null, 3, null);
        return study;
    }

    /**
     * Creates a variant of the annotation study introduced by Krippendorff (2004: p. 246).
     */
    public static CodingAnnotationStudy createExample4a()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(12, "a", "a");
        study.addMultipleItems(9, "a", "b");
        study.addMultipleItems(9, "a", "c");
        study.addMultipleItems(9, "b", "a");
        study.addMultipleItems(14, "b", "b");
        study.addMultipleItems(9, "b", "c");
        study.addMultipleItems(9, "c", "a");
        study.addMultipleItems(9, "c", "b");
        study.addMultipleItems(20, "c", "c");
        return study;
    }

    /**
     * Creates a variant of the annotation study introduced by Krippendorff (2004: p. 246).
     */
    public static CodingAnnotationStudy createExample4b()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(12, "a", "a");
        study.addMultipleItems(18, "a", "b");
        study.addMultipleItems(18, "a", "c");
        study.addMultipleItems(0, "b", "a");
        study.addMultipleItems(14, "b", "b");
        study.addMultipleItems(18, "b", "c");
        study.addMultipleItems(0, "c", "a");
        study.addMultipleItems(0, "c", "b");
        study.addMultipleItems(20, "c", "c");
        return study;
    }

}
