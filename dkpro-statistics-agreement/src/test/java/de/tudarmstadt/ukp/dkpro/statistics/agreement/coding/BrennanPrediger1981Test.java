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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.coding;

import junit.framework.TestCase;

/**
 * Tests based on Brennan & Prediger (1981) for measuring
 * {@link BennettSAgreement} and {@link CohenKappaAgreement}.<br><br>
 * References:<ul>
 * <li>Brennan, R.L. & Prediger, D.J.: Coefficient kappa: Some uses, misuses,
 *   and alternatives. Educational and Psychological Measurement
 *   41(3):687–699, 1981.</li></ul>
 * @author Christian M. Meyer
 */
public class BrennanPrediger1981Test extends TestCase {

	/***/
	public void testExample1() {
		ICodingAnnotationStudy study = createExample1();

		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.60, kappa.calculateObservedAgreement(), 0.01);
		assertEquals(0.45, kappa.calculateExpectedAgreement(), 0.01);
		assertEquals(0.27, kappa.calculateAgreement(), 0.01);

		BennettSAgreement S = new BennettSAgreement(study);
		assertEquals(0.60, S.calculateObservedAgreement(), 0.01);
		assertEquals(0.33, S.calculateExpectedAgreement(), 0.01);
		assertEquals(0.40, S.calculateAgreement(), 0.01);

		//max A_O = sum min{marginals} -> 0.7
//		assertEquals(0.60, kappa / max-kappa, 0.01);

//		assertEquals(-1.00, kappa_b, 0.01);
	}

	/***/
	public void testExample2() {
		ICodingAnnotationStudy study = createExample2();

		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.60, kappa.calculateObservedAgreement(), 0.01);
		assertEquals(0.44, kappa.calculateExpectedAgreement(), 0.01);
		assertEquals(0.29, kappa.calculateAgreement(), 0.01);

		BennettSAgreement S = new BennettSAgreement(study);
		assertEquals(0.60, S.calculateObservedAgreement(), 0.01);
		assertEquals(0.33, S.calculateExpectedAgreement(), 0.01);
		assertEquals(0.40, S.calculateAgreement(), 0.01);

		//max A_O = sum min{marginals} -> 1
//		assertEquals(0.29, kappa / max-kappa, 0.01);

		// A_E = max. marginal (majority class)
//		assertEquals(0.00, kappa_b, 0.01);
	}

	/** Creates an example annotation study introduced by
	 *  Brennan&Prediger (1981: p. 689). Proportions are scaled by 100. */
	public static ICodingAnnotationStudy createExample1() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(50, 1, 1);
		study.addMultipleItems(10, 2, 1);
		study.addMultipleItems(10, 2, 2);
		study.addMultipleItems(10, 2, 3);
		study.addMultipleItems(20, 3, 1);
		return study;
	}

	/** Creates an example annotation study introduced by
	 *  Brennan&Prediger (1981: p. 691). Proportions are scaled by 100. */
	public static ICodingAnnotationStudy createExample2() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(50, 1, 1);
		study.addMultipleItems(10, 1, 3);
		study.addMultipleItems(10, 2, 2);
		study.addMultipleItems(10, 2, 3);
		study.addMultipleItems(10, 3, 1);
		study.addMultipleItems(10, 3, 2);
		return study;
	}

}
