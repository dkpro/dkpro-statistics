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

import org.dkpro.statistics.agreement.ICategorySpecificAgreement;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;
import org.dkpro.statistics.agreement.distance.IntervalDistanceFunction;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.dkpro.statistics.agreement.distance.OrdinalDistanceFunction;
import org.dkpro.statistics.agreement.distance.RatioDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests based on Krippendorff (1980) for measuring {@link KrippendorffAlphaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology. Beverly Hills, CA:
 * Sage Publications, 1980.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class Krippendorff1980Test
{
    @Test
    public void testDichotomy()
    {
        ICodingAnnotationStudy study = createExample1();

        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.600, offset(0.001));

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.400, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.442, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.095, offset(0.001));
    }

    @Test
    public void testMultiCategoryMultiRater()
    {
        ICodingAnnotationStudy study = createExample2();

        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.740, offset(0.001));

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.259, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.724, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.642, offset(0.001));
    }

    @Test
    public void testOrdinalMetric()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(1, 1);
        study.addItem(2, 2);
        study.addItem(3, 3);
        study.addItem(3, 3);
        study.addItem(4, 4);
        study.addItem(4, 4);
        study.addItem(4, 4);
        study.addItem(4, 4);
        study.addItem(5, 5);
        study.addItem(10, 10);

        IDistanceFunction distFunc = new OrdinalDistanceFunction();

        final double[][] EXPECTED = new double[][] { { 0.0, 2.0, 5.0, 11.0, 16.0, 18.0 },
                { 2.0, 0.0, 3.0, 9.0, 14.0, 16.0 }, { 5.0, 3.0, 0.0, 6.0, 11.0, 13.0 },
                { 11.0, 9.0, 6.0, 0.0, 5.0, 7.0 }, { 16.0, 14.0, 11.0, 5.0, 0.0, 2.0 },
                { 18.0, 16.0, 13.0, 7.0, 2.0, 0.0 } };
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
    public void testNominalMetric()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(1);
        for (int i = 0; i <= 7; i++) {
            study.addCategory(i);
        }

        IDistanceFunction distFunc = new NominalDistanceFunction();

        final double[][] EXPECTED = new double[][] { { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
                { 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
                { 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
                { 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0 },
                { 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0 },
                { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0 },
                { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0 },
                { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 } };
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
        CodingAnnotationStudy study = new CodingAnnotationStudy(1);
        for (int i = 0; i <= 7; i++) {
            study.addCategory(i);
        }

        IDistanceFunction distFunc = new IntervalDistanceFunction();

        final double[][] EXPECTED = new double[][] { { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 },
                { 1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0 },
                { 2.0, 1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 },
                { 3.0, 2.0, 1.0, 0.0, 1.0, 2.0, 3.0, 4.0 },
                { 4.0, 3.0, 2.0, 1.0, 0.0, 1.0, 2.0, 3.0 },
                { 5.0, 4.0, 3.0, 2.0, 1.0, 0.0, 1.0, 2.0 },
                { 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0, 1.0 },
                { 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0 } };
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

        study = createExample2();
        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new IntervalDistanceFunction());
        assertThat(alpha.calculateAgreement()).isCloseTo(0.547, offset(0.001));
    }

    @Test
    public void testRatioMetric()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(1);
        for (int i = 0; i <= 7; i++) {
            study.addCategory(i);
        }

        IDistanceFunction distFunc = new RatioDistanceFunction();

        final double[][] EXPECTED = new double[][] {
                { 0.0, 1.0 / 1.0, 2.0 / 2.0, 3.0 / 3.0, 4.0 / 4.0, 5.0 / 5.0, 6.0 / 6.0,
                        7.0 / 7.0 },
                { 1.0 / 1.0, 0.0 / 2.0, 1.0 / 3.0, 2.0 / 4.0, 3.0 / 5.0, 4.0 / 6.0, 5.0 / 7.0,
                        6.0 / 8.0 },
                { 2.0 / 2.0, 1.0 / 3.0, 0.0 / 4.0, 1.0 / 5.0, 2.0 / 6.0, 3.0 / 7.0, 4.0 / 8.0,
                        5.0 / 9.0 },
                { 3.0 / 3.0, 2.0 / 4.0, 1.0 / 5.0, 0.0 / 6.0, 1.0 / 7.0, 2.0 / 8.0, 3.0 / 9.0,
                        4.0 / 10.0 },
                { 4.0 / 4.0, 3.0 / 5.0, 2.0 / 6.0, 1.0 / 7.0, 0.0 / 8.0, 1.0 / 9.0, 2.0 / 10.0,
                        3.0 / 11.0 },
                { 5.0 / 5.0, 4.0 / 6.0, 3.0 / 7.0, 2.0 / 8.0, 1.0 / 9.0, 0.0 / 10.0, 1.0 / 11.0,
                        2.0 / 12.0 },
                { 6.0 / 6.0, 5.0 / 7.0, 4.0 / 8.0, 3.0 / 9.0, 2.0 / 10.0, 1.0 / 11.0, 0.0 / 12.0,
                        1.0 / 13.0 },
                { 7.0 / 7.0, 6.0 / 8.0, 5.0 / 9.0, 4.0 / 10.0, 3.0 / 11.0, 2.0 / 12.0, 1.0 / 13.0,
                        0.0 / 14.0 } };

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

        study = createExample2();
        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new RatioDistanceFunction());
        assertThat(alpha.calculateAgreement()).isCloseTo(0.483, offset(0.001));
    }

    /*
     * ** /
     * 
     * @Test public void testItemAgreement() { ICodingAnnotationStudy study = createExample2();
     * 
     * ICodingItemSpecificAgreement agreement = new PercentageAgreement(study);
     * Iterator<ICodingAnnotationItem> iter = study.getItems().iterator();
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.333, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.333, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertFalse(iter.hasNext());
     * 
     * agreement = new KrippendorffAlphaAgreement(study, new NominalDistanceFunction()); iter =
     * study.getItems().iterator();
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001)); //
     * Next line -- error in D_O within original publication?
     * //assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.539, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.078, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001)); //
     * Next line -- error in D_O within original publication?
     * //assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.539, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.078, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertEquals(-.382, agreement.calculateItemAgreement(iter.next()), 0.001);
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
     * assertFalse(iter.hasNext()); }
     */

    @Test
    public void testCategoryAgreement()
    {
        ICodingAnnotationStudy study = createExample2();

        ICategorySpecificAgreement agreement = new PercentageAgreement(study);
        assertThat(agreement.calculateCategoryAgreement(1)).isCloseTo(0.777, offset(0.001));
        assertThat(agreement.calculateCategoryAgreement(2)).isCloseTo(0.852, offset(0.001));
        assertThat(agreement.calculateCategoryAgreement(3)).isCloseTo(0.926, offset(0.001));
        assertThat(agreement.calculateCategoryAgreement(4)).isCloseTo(0.926, offset(0.001));

        agreement = new KrippendorffAlphaAgreement(study, new NominalDistanceFunction());
        assertThat(agreement.calculateCategoryAgreement(1)).isCloseTo(0.381, offset(0.001));
        assertThat(agreement.calculateCategoryAgreement(2)).isCloseTo(0.711, offset(0.001));
        assertThat(agreement.calculateCategoryAgreement(3)).isCloseTo(0.717, offset(0.001));
        assertThat(agreement.calculateCategoryAgreement(4)).isCloseTo(0.764, offset(0.001));
    }

    /*
     * ** /
     * 
     * @Test public void testRaterAgreement() { ICodingAnnotationStudy study = createExample2();
     * 
     * IRaterSpecificAgreement agreement = new PercentageAgreement(study);
     * assertThat(agreement.calculateRaterAgreement(1)).isCloseTo(0.684, offset(0.001));
     * 
     * agreement = new KrippendorffAlphaAgreement(study, new NominalDistanceFunction());
     * assertThat(agreement.calculateRaterAgreement(0)).isCloseTo(0.564, offset(0.001)); }
     */

    /**
     * Creates an example annotation study introduced by Krippendorff (1980: p. 133).
     */
    public static CodingAnnotationStudy createExample1()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(0, 0);
        study.addItem(1, 1);
        study.addItem(0, 1);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 1);
        study.addItem(0, 0);
        study.addItem(0, 1);
        study.addItem(1, 0);
        study.addItem(0, 0);
        return study;
    }

    /**
     * Creates an example annotation study introduced by Krippendorff (1980: p. 139).
     */
    public static CodingAnnotationStudy createExample2()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem(1, 1, 1);
        study.addItem(1, 2, 2);
        study.addItem(2, 2, 2);
        study.addItem(4, 4, 4);
        study.addItem(1, 4, 4);
        study.addItem(2, 2, 2);
        study.addItem(1, 2, 3);
        study.addItem(3, 3, 3);
        study.addItem(2, 2, 2);
        return study;
    }

}
