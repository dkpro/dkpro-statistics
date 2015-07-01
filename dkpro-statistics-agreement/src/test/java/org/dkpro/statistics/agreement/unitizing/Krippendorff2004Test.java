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

import org.dkpro.statistics.agreement.unitizing.IUnitizingAnnotationStudy;
import org.dkpro.statistics.agreement.unitizing.KrippendorffAlphaUnitizingAgreement;
import org.dkpro.statistics.agreement.unitizing.UnitizingAnnotationStudy;

import junit.framework.TestCase;

/**
 * Tests based on Krippendorff (2004) for measuring 
 * {@link KrippendorffAlphaUnitizingAgreement}.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Thousand Oaks, CA: Sage Publications, 2004.</li></ul>
 * @author Christian M. Meyer
 * @author Christian Stab
 */
public class Krippendorff2004Test extends TestCase {
	
	/***/
	public void testDistanceMetric1() {
		assertEquals(40.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				2, 3, null, 2, 2, "X") 
				+ KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 4, 7, null));
		assertEquals(40.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				3, 2, null, 3, 2, "X")
				+ KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 5, 6, null));
		assertEquals(26.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 4, 2, "X"));
		assertEquals(16.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 5, 2, "X"));
		assertEquals(10.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 6, 2, "X"));
		assertEquals(8.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 7, 2, "X"));
		assertEquals(2.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 6, 4, "X"));
		assertEquals(0.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				5, 6, "X", 5, 6, "X"));
	}

	/***/
	public void testDistanceMetric2() {
		assertEquals(50.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				225, 70, "c", 220, 80, "c"));
		assertEquals(850.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				370, 30, "c", 355, 20, "c"));
		assertEquals(400.0, KrippendorffAlphaUnitizingAgreement.measureDistance(
				400, 50, null, 400, 20, "c"));
	}

	/***/
	public void testAgreement() {
		IUnitizingAnnotationStudy study = createExample();

		KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
		assertEquals(0.8591, alpha.calculateAgreement(), 0.0005);		
	}
	
	/***/
	public void testCategoryAgreement() {
		IUnitizingAnnotationStudy study = createExample();
		
		KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
		assertEquals(0.0144, alpha.calculateObservedCategoryDisagreement("c"), 0.0001);
		assertEquals(0.0532, alpha.calculateExpectedCategoryDisagreement("c"), 0.0001);
		assertEquals(0.7286, alpha.calculateCategoryAgreement("c"), 0.0001);
		
		assertEquals(0.0000, alpha.calculateObservedCategoryDisagreement("k"), 0.0001);
		assertEquals(0.0490, alpha.calculateExpectedCategoryDisagreement("k"), 0.0001);
		assertEquals(1.0000, alpha.calculateCategoryAgreement("k"), 0.0001);
	}
	
	
	/** Creates an example annotation study introduced by  
	 *  Krippendorff (2004: p. 254). */
	public static UnitizingAnnotationStudy createExample() {
		UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(2, 150, 300);
		// observer i is annotator 1
		// observer j is annotator 2
		
		// Gaps are automatically created!
//		study.addItem(180, 60, 0, "k");
//		study.addItem(180, 60, 1, "k");
//		study.addItem(220, 80, 1, "c");
//		study.addItem(225, 70, 0, "c");
//		study.addItem(355, 20, 1, "c");
//		study.addItem(370, 30, 0, "c");
//		study.addItem(400, 20, 1, "c");
//		study.addItem(300, 50, 0, "k");
//		study.addItem(300, 50, 1, "k");
		
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
