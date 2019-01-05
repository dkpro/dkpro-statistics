/*******************************************************************************
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
 ******************************************************************************/
package org.dkpro.statistics.agreement.unitizing;

import junit.framework.TestCase;

/**
 * Tests for measuring {@link KrippendorffAlphaUnitizingAgreement}.<br><br>
 * @author Christian M. Meyer
 * @author Ivan Habernal
 */
public class UnitizingAgreementTest extends TestCase {
    
    /***/
    public void testAgreement() {
        IUnitizingAnnotationStudy study = createExample();
        
        KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.9100, alpha.calculateObservedCategoryDisagreement("A"), 0.0001);
        assertEquals(0.5351, alpha.calculateExpectedCategoryDisagreement("A"), 0.0001);
        assertEquals(-0.7003, alpha.calculateCategoryAgreement("A"), 0.0001);
    }
    
    /***/
    public void testSingleAnnotationUnit() {
        UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(3, 20);
        study.addUnit(0, 1, 0, "X");

        KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.0, alpha.calculateAgreement());
    }
    
    /***/
    public void testEmptyAnnotationSet() {
        UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(3, 20);

        KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertTrue(Double.isNaN(alpha.calculateAgreement()));
    }

    /***/
    public void testDistanceMetric() {
        assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                0, 1, null, 0, 2, null));
        assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 0, 2, null));
        assertEquals(37.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 2, 1, "A"));
        assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 3, 1, null));
        assertEquals(25.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 4, 1, "A"));
        assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 5, 1, null));
        assertEquals(29.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 6, 1, "A"));
        assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                1, 8, "A", 7, 4, null));
        assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
                9, 2, null, 7, 4, null));
    }

    /** Creates an example annotation study. */
    public static UnitizingAnnotationStudy createExample() {
        // r0: -11111111-
        // r1: --1-1-1---        
        UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(2, 10);
        study.addUnit(1, 8, 0, "A");
        study.addUnit(2, 1, 1, "A");
        study.addUnit(4, 1, 1, "A");
        study.addUnit(6, 1, 1, "A");
        return study;
    }

}
