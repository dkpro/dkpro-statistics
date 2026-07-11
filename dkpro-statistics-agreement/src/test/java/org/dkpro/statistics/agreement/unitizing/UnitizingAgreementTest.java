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
import static org.dkpro.statistics.agreement.unitizing.KrippendorffAlphaUnitizingAgreement.measureDistance;

import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for measuring {@link KrippendorffAlphaUnitizingAgreement}.<br>
 * <br>
 * 
 * @author Christian M. Meyer
 * @author Ivan Habernal
 */
public class UnitizingAgreementTest
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void testAgreement()
    {
        var study = createExample();

        var alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateObservedCategoryDisagreement("A")).isCloseTo(0.9100,
                offset(0.0001));
        assertThat(alpha.calculateExpectedCategoryDisagreement("A")).isCloseTo(0.5351,
                offset(0.0001));
        assertThat(alpha.calculateCategoryAgreement("A")).isCloseTo(-0.7003, offset(0.0001));
    }

    @Test
    public void testSingleAnnotationUnit()
    {
        var study = new UnitizingAnnotationStudy(3, 20);
        study.addUnit(0, 1, 0, "X");

        var alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateAgreement()).isEqualTo(0.0);
    }

    @Test
    public void testEmptyAnnotationSet()
    {
        var study = new UnitizingAnnotationStudy(3, 20);

        var alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateAgreement()).isNaN();
    }

    /**
     * Two raters annotate the same position with a zero-length unit, but assign different
     * categories. Alpha_U weights all (dis)agreement by unit length, so the measure cannot see the
     * units (zero mass): the study is degenerate and must not yield 1.0 even though the observed
     * and the expected disagreement are both zero. See issue #35.
     */
    @Test
    public void testOnlyZeroLengthUnitsWithDifferingCategories()
    {
        var study = new UnitizingAnnotationStudy(2, 15);
        study.addUnit(0, 0, 0, "A");
        study.addUnit(0, 0, 1, "B");

        var alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.0, offset(0.001));
    }

    /**
     * Two raters annotate the same position with a zero-length unit of the same category. Although
     * this looks like agreement, the units carry no mass and the study is just as degenerate as in
     * the differing-categories case -- the measure cannot tell the two situations apart.
     */
    @Test
    public void testOnlyZeroLengthUnitsWithSameCategory()
    {
        var study = new UnitizingAnnotationStudy(2, 15);
        study.addUnit(0, 0, 0, "A");
        study.addUnit(0, 0, 1, "A");

        var alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.0, offset(0.001));
    }

    /**
     * A zero-length unit next to units of positive length must not mark the study as degenerate.
     * The raters fully agree on the positive-length units, so the agreement is 1.0 via the regular
     * formula (the expected disagreement is positive).
     */
    @Test
    public void testZeroLengthUnitsMixedWithPositiveLengthUnits()
    {
        var study = new UnitizingAnnotationStudy(2, 15);
        study.addUnit(0, 0, 0, "A");
        study.addUnit(2, 5, 0, "A");
        study.addUnit(2, 5, 1, "A");

        var alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isGreaterThan(0.0);
        assertThat(alpha.calculateAgreement()).isCloseTo(1.0, offset(0.001));
    }

    @Test
    public void testDistanceMetric()
    {
        assertThat(measureDistance(0, 1, null, 0, 2, null)).isEqualTo(0.0);
        assertThat(measureDistance(1, 8, "A", 0, 2, null)).isEqualTo(0.0);
        assertThat(measureDistance(1, 8, "A", 2, 1, "A")).isEqualTo(37.0);
        assertThat(measureDistance(1, 8, "A", 3, 1, null)).isEqualTo(0.0);
        assertThat(measureDistance(1, 8, "A", 4, 1, "A")).isEqualTo(25.0);
        assertThat(measureDistance(1, 8, "A", 5, 1, null)).isEqualTo(0.0);
        assertThat(measureDistance(1, 8, "A", 6, 1, "A")).isEqualTo(29.0);
        assertThat(measureDistance(1, 8, "A", 7, 4, null)).isEqualTo(0.0);
        assertThat(measureDistance(9, 2, null, 7, 4, null)).isEqualTo(0.0);
    }

    /** Creates an example annotation study. */
    public static UnitizingAnnotationStudy createExample()
    {
        // r0: -11111111-
        // r1: --1-1-1---
        var study = new UnitizingAnnotationStudy(2, 10);
        study.addUnit(1, 8, 0, "A");
        study.addUnit(2, 1, 1, "A");
        study.addUnit(4, 1, 1, "A");
        study.addUnit(6, 1, 1, "A");
        return study;
    }
}
