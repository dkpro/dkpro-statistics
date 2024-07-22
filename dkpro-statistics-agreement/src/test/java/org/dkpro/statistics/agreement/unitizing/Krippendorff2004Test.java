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
package org.dkpro.statistics.agreement.unitizing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.Test;

/**
 * Tests based on Krippendorff (2004) for measuring {@link KrippendorffAlphaUnitizingAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology. Thousand Oaks, CA:
 * Sage Publications, 2004.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 * @author Christian Stab
 */
public class Krippendorff2004Test
{
    @Test
    public void testDistanceMetric1()
    {
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(2, 3, null, 2, 2, "X")
                + KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 4, 7, null))
                        .isEqualTo(40.0);

        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(3, 2, null, 3, 2, "X")
                + KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 5, 6, null))
                        .isEqualTo(40.0);

        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 4, 2, "X"))
                .isEqualTo(26.0);

        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 5, 2, "X"))
                .isEqualTo(16.0);
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 6, 2, "X"))
                .isEqualTo(10.0);
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 7, 2, "X"))
                .isEqualTo(8.0);
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 6, 4, "X"))
                .isEqualTo(2.0);
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(5, 6, "X", 5, 6, "X"))
                .isEqualTo(0.0);

    }

    @Test
    public void testDistanceMetric2()
    {
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(225, 70, "c", 220, 80, "c"))
                .isEqualTo(50.0);
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(370, 30, "c", 355, 20, "c"))
                .isEqualTo(850.0);
        assertThat(KrippendorffAlphaUnitizingAgreement.measureDistance(400, 50, null, 400, 20, "c"))
                .isEqualTo(400.0);
    }

    @Test
    public void testAgreement()
    {
        IUnitizingAnnotationStudy study = createExample();

        KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateAgreement()).isCloseTo(0.8591, offset(0.0005));
    }

    @Test
    public void testCategoryAgreement()
    {
        IUnitizingAnnotationStudy study = createExample();

        KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateObservedCategoryDisagreement("c")).isCloseTo(0.0144,
                offset(0.0001));
        assertThat(alpha.calculateExpectedCategoryDisagreement("c")).isCloseTo(0.0532,
                offset(0.0001));
        assertThat(alpha.calculateCategoryAgreement("c")).isCloseTo(0.7286, offset(0.0001));

        assertThat(alpha.calculateObservedCategoryDisagreement("k")).isCloseTo(0.0000,
                offset(0.0001));
        assertThat(alpha.calculateExpectedCategoryDisagreement("k")).isCloseTo(0.0490,
                offset(0.0001));
        assertThat(alpha.calculateCategoryAgreement("k")).isCloseTo(1.0000, offset(0.0001));
    }

    /**
     * Creates an example annotation study introduced by Krippendorff (2004: p. 254).
     */
    public static UnitizingAnnotationStudy createExample()
    {
        UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(2, 150, 300);
        // observer i is annotator 1
        // observer j is annotator 2

        // Gaps are automatically created!
        // study.addItem(180, 60, 0, "k");
        // study.addItem(180, 60, 1, "k");
        // study.addItem(220, 80, 1, "c");
        // study.addItem(225, 70, 0, "c");
        // study.addItem(355, 20, 1, "c");
        // study.addItem(370, 30, 0, "c");
        // study.addItem(400, 20, 1, "c");
        // study.addItem(300, 50, 0, "k");
        // study.addItem(300, 50, 1, "k");

        study.addUnit(225, 70, 0, "c");
        study.addUnit(370, 30, 0, "c");
        study.addUnit(220, 80, 1, "c");
        study.addUnit(355, 20, 1, "c");
        study.addUnit(400, 20, 1, "c");
        study.addUnit(180, 60, 0, "k");
        study.addUnit(300, 50, 0, "k");
        study.addUnit(180, 60, 1, "k");
        study.addUnit(300, 50, 1, "k");
        return study;
    }

}
