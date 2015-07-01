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
package org.dkpro.statistics.agreement.coding;

import junit.framework.TestCase;

/**
 * Tests for numerical stability.
 * @author Christian M. Meyer
 */
public class NumericalStabilityTest extends TestCase {

	/***/
	public void testExample1() {
		ICodingAnnotationStudy study = createExample1();

		PercentageAgreement raw = new PercentageAgreement(study);
		assertEquals(0.9, raw.calculateAgreement(), 0.001);

		BennettSAgreement s = new BennettSAgreement(study);
		assertEquals(0.9, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, s.calculateAgreement(), 0.001);

		ScottPiAgreement pi = new ScottPiAgreement(study);
		assertEquals(0.9, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, pi.calculateAgreement(), 0.001);

		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.9, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, kappa.calculateAgreement(), 0.001);
	}

	/***/
	public void testExample2() {
		ICodingAnnotationStudy study = createExample2();
		CodingAnnotationStudy tmpStudy = new CodingAnnotationStudy(2);
		for (int i = 0; i < 81001; i++)
			tmpStudy.addItem(1, 1);
		for (int i = 0; i < 9000; i++)
			tmpStudy.addItem(1, 0);
		for (int i = 0; i < 1000; i++)
			tmpStudy.addItem(0, 1);
		for (int i = 0; i < 9000; i++)
			tmpStudy.addItem(0, 0);
		study = tmpStudy;

		PercentageAgreement raw = new PercentageAgreement(study);
		assertEquals(0.9, raw.calculateAgreement(), 0.001);

		BennettSAgreement s = new BennettSAgreement(study);
		assertEquals(0.9, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, s.calculateAgreement(), 0.001);

		ScottPiAgreement pi = new ScottPiAgreement(study);
		assertEquals(0.9, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.759, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.585, pi.calculateAgreement(), 0.001);

		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.9, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.756, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.590, kappa.calculateAgreement(), 0.001);
	}

	/***/
	public void testScaledArtsteinPoesio() {
		ICodingAnnotationStudy study = createExample2(500);

		// Two raters, observed agreement.
		PercentageAgreement pa = new PercentageAgreement(study);
		assertEquals(0.7, pa.calculateAgreement(), 0.001);

		// Two raters, chance-corrected agreement.
		BennettSAgreement s = new BennettSAgreement(study);
		assertEquals(0.7, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.4, s.calculateAgreement(), 0.001);

		ScottPiAgreement pi = new ScottPiAgreement(study);
		assertEquals(0.7, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.545, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.341, pi.calculateAgreement(), 0.001);

		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.7, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.54, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.348, kappa.calculateAgreement(), 0.001);
	}

	/***/
	public void testScaledFleiss() {
		ICodingAnnotationStudy study = createExample3(2000);

		FleissKappaAgreement pi = new FleissKappaAgreement(study);
		assertEquals(0.5556, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.2201, pi.calculateExpectedAgreement(), 0.001);
		double agreement = pi.calculateAgreement();
		assertEquals(0.430, agreement, 0.001);
	}

	/** Creates an example annotation study with many items. Such a study
	 *  can easily yield problems related to numerical stability. */
	public static ICodingAnnotationStudy createExample1() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(45000, 1, 1);
		study.addMultipleItems( 5000, 1, 0);
		study.addMultipleItems( 4999, 0, 1);
		study.addMultipleItems(45000, 0, 0);
		return study;
	}

	/** Creates an example annotation study with many items. Such a study
	 *  can easily yield problems related to numerical stability. */
	public static ICodingAnnotationStudy createExample2() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(81001, 1, 1);
		study.addMultipleItems( 9000, 1, 0);
		study.addMultipleItems( 1000, 0, 1);
		study.addMultipleItems( 9000, 0, 0);
		return study;
	}

	/** Creates an example annotation study introduced by Artstein &amp;
	 *  Poesio (2008: p. 558) that is scaled by the given factor. */
	public static ICodingAnnotationStudy createExample2(int scale) {
		/*		STAT	IReq	  Σ
		STAT	 20		 10		 30
		IReq	 20		 50		 70
		Σ   	 40		 60		100 */
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(20 * scale, "STAT", "STAT");
		study.addMultipleItems(10 * scale, "IReq", "STAT");
		study.addMultipleItems(20 * scale, "STAT", "IReq");
		study.addMultipleItems(50 * scale, "IReq", "IReq");
		return study;
	}

	/** Creates an example annotation study introduced by
	 *  Fleiss (1971: 379) that is scaled by the given factor.. */
	public static ICodingAnnotationStudy createExample3(int scale) {
		CodingAnnotationStudy study = new CodingAnnotationStudy(6);
		study.addMultipleItems(scale, 4, 4, 4, 4, 4, 4);
		study.addMultipleItems(scale, 2, 2, 2, 5, 5, 5);
		study.addMultipleItems(scale, 2, 3, 3, 3, 3, 5);
		study.addMultipleItems(scale, 5, 5, 5, 5, 5, 5);
		study.addMultipleItems(scale, 2, 2, 2, 4, 4, 4);
		study.addMultipleItems(scale, 1, 1, 3, 3, 3, 3);
		study.addMultipleItems(scale, 3, 3, 3, 3, 5, 5);
		study.addMultipleItems(scale, 1, 1, 3, 3, 3, 4);
		study.addMultipleItems(scale, 1, 1, 4, 4, 4, 4);
		study.addMultipleItems(scale, 5, 5, 5, 5, 5, 5);

		study.addMultipleItems(scale, 1, 4, 4, 4, 4, 4);
		study.addMultipleItems(scale, 1, 2, 4, 4, 4, 4);
		study.addMultipleItems(scale, 2, 2, 2, 3, 3, 3);
		study.addMultipleItems(scale, 1, 4, 4, 4, 4, 4);
		study.addMultipleItems(scale, 2, 2, 4, 4, 4, 5);
		study.addMultipleItems(scale, 3, 3, 3, 3, 3, 5);
		study.addMultipleItems(scale, 1, 1, 1, 4, 5, 5);
		study.addMultipleItems(scale, 1, 1, 1, 1, 1, 2);
		study.addMultipleItems(scale, 2, 2, 4, 4, 4, 4);
		study.addMultipleItems(scale, 1, 3, 3, 5, 5, 5);

		study.addMultipleItems(scale, 5, 5, 5, 5, 5, 5);
		study.addMultipleItems(scale, 2, 4, 4, 4, 4, 4);
		study.addMultipleItems(scale, 2, 2, 4, 5, 5, 5);
		study.addMultipleItems(scale, 1, 1, 4, 4, 4, 4);
		study.addMultipleItems(scale, 1, 4, 4, 4, 4, 5);
		study.addMultipleItems(scale, 2, 2, 2, 2, 2, 4);
		study.addMultipleItems(scale, 1, 1, 1, 1, 5, 5);
		study.addMultipleItems(scale, 2, 2, 4, 4, 4, 4);
		study.addMultipleItems(scale, 1, 3, 3, 3, 3, 3);
		study.addMultipleItems(scale, 5, 5, 5, 5, 5, 5);
		return study;
	}

}
