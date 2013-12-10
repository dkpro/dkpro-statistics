/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.dkpro.statistics.agreement;

import junit.framework.TestCase;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.MultiRaterObservedAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.MultiRaterPiAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterKappaCategoryAgreement;

/**
 * Tests for {@link MultiRaterObservedAgreement} and 
 * {@link MultiRaterPiAgreement}.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class MultiRaterAgreementTest extends TestCase {

	/***/
	public void testArtsteinPoesio2008_1() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_1();
		
		MultiRaterObservedAgreement raw = new MultiRaterObservedAgreement(study);
		assertEquals(0.7, raw.calculateAgreement(), 0.001);
	}
	
	/***/
	public void testArtsteinPoesio2008_2() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_2();
				
		MultiRaterObservedAgreement raw = new MultiRaterObservedAgreement(study);
		assertEquals(0.88, raw.calculateAgreement(), 0.001);
	}
	
	/***/
	public void testMeyer2009Example() {
		AnnotationStudy study = new AnnotationStudy(3);
		study.addItem("high", "high", "high");
		study.addItem("high", "high", "low");
		study.addItem("high", "low", "high");
		study.addItem("low", "high", "high");
		study.addItem("low", "low", "high");
		study.addItem("low", "low", "low");
		study.addItem("low", "low", "low");
		study.addItem("low", "high", "low");
		study.addItem("low", "low", "high");
		study.addItem("low", "low", "high");
				
		// Generalized percentage of agreement.
		MultiRaterObservedAgreement raw = new MultiRaterObservedAgreement(study);
		double agreement = raw.calculateAgreement();
		assertEquals(0.533, agreement, 0.001);
		/*double se = raw.standardError(agreement);
		double[] ci = raw.confidenceInterval(agreement, se, RawAgreement.CONFIDENCE_95);
		assertEquals(0.045, se, 0.001);
		assertEquals(0.610, ci[0], 0.001);
		assertEquals(0.789, ci[1], 0.001);*/		
		
		// Fleiss' multi-pi.
		MultiRaterPiAgreement pi = new MultiRaterPiAgreement(study);
		assertEquals(0.533, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.508, pi.calculateExpectedAgreement(), 0.001);
		agreement = pi.calculateAgreement();
		assertEquals(0.049, agreement, 0.001);
	}

	/***/
	public void testFleiss1971Example() {
		AnnotationStudy study = new AnnotationStudy(6);
		study.addItem(4, 4, 4, 4, 4, 4);
		study.addItem(2, 2, 2, 5, 5, 5);
		study.addItem(2, 3, 3, 3, 3, 5);
		study.addItem(5, 5, 5, 5, 5, 5);
		study.addItem(2, 2, 2, 4, 4, 4);
		study.addItem(1, 1, 3, 3, 3, 3);
		study.addItem(3, 3, 3, 3, 5, 5);
		study.addItem(1, 1, 3, 3, 3, 4);
		study.addItem(1, 1, 4, 4, 4, 4);
		study.addItem(5, 5, 5, 5, 5, 5);
		
		study.addItem(1, 4, 4, 4, 4, 4);
		study.addItem(1, 2, 4, 4, 4, 4);
		study.addItem(2, 2, 2, 3, 3, 3);
		study.addItem(1, 4, 4, 4, 4, 4);
		study.addItem(2, 2, 4, 4, 4, 5);
		study.addItem(3, 3, 3, 3, 3, 5);
		study.addItem(1, 1, 1, 4, 5, 5);
		study.addItem(1, 1, 1, 1, 1, 2);
		study.addItem(2, 2, 4, 4, 4, 4);
		study.addItem(1, 3, 3, 5, 5, 5);
		
		study.addItem(5, 5, 5, 5, 5, 5);
		study.addItem(2, 4, 4, 4, 4, 4);
		study.addItem(2, 2, 4, 5, 5, 5);
		study.addItem(1, 1, 4, 4, 4, 4);
		study.addItem(1, 4, 4, 4, 4, 5);
		study.addItem(2, 2, 2, 2, 2, 4);
		study.addItem(1, 1, 1, 1, 5, 5);
		study.addItem(2, 2, 4, 4, 4, 4);
		study.addItem(1, 3, 3, 3, 3, 3);
		study.addItem(5, 5, 5, 5, 5, 5);
		assertEquals(30, study.getItemCount());
				
		MultiRaterPiAgreement pi = new MultiRaterPiAgreement(study);
		assertEquals(0.5556, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.2201, pi.calculateExpectedAgreement(), 0.001);
		double agreement = pi.calculateAgreement();
		assertEquals(0.430, agreement, 0.001);
		// Var = 0.000759 = 2/n*m(m-1) * (AE - (2m-3)AE^2 + 2(m-2)AE / (1-AE)^2)
		// SE = 0.028
		/*double se = raw.standardError(agreement);
		double[] ci = raw.confidenceInterval(agreement, se, RawAgreement.CONFIDENCE_95);
		assertEquals(0.028, se, 0.001);
		assertEquals(0.610, ci[0], 0.001);
		assertEquals(0.789, ci[1], 0.001);*/		
	}

	/***/
	public void testFleiss1971Example_2() {
		IAnnotationStudy study = AgreementTestExamples.createFleiss1972();
		assertEquals(30, study.getItemCount());
				
		MultiRaterPiAgreement pi = new MultiRaterPiAgreement(study);
		assertEquals(0.5556, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.2201, pi.calculateExpectedAgreement(), 0.001);
		double agreement = pi.calculateAgreement();
		assertEquals(0.430, agreement, 0.001);
		
		// Categories.
		TwoRaterKappaCategoryAgreement catKappa = new TwoRaterKappaCategoryAgreement(study);
		assertEquals(0.248, catKappa.calculateAgreement(1), 0.005);
		assertEquals(0.248, catKappa.calculateAgreement(2), 0.005);
		assertEquals(0.517, catKappa.calculateAgreement(3), 0.005);
		assertEquals(0.470, catKappa.calculateAgreement(4), 0.005);
		assertEquals(0.565, catKappa.calculateAgreement(5), 0.005);		
	}

}
