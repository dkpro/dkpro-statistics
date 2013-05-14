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

import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterKappaAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterObservedAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterObservedCategoryAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterPiAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterSAgreement;
import junit.framework.TestCase;

/**
 * Tests for {@link TwoRaterObservedAgreement}, {@link TwoRaterSAgreement}, 
 * {@link TwoRaterPiAgreement} and {@link TwoRaterKappaAgreement}.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class TwoRaterAgreementTest extends TestCase {

	/***/
	public void testArtsteinPoesio2008_1() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_1();
		
		// Two raters, observed agreement.
		TwoRaterObservedAgreement raw = new TwoRaterObservedAgreement(study);
		assertEquals(0.7, raw.calculateAgreement(), 0.001);
		
		TwoRaterObservedCategoryAgreement cat = new TwoRaterObservedCategoryAgreement(study);
		assertEquals(0.571, cat.calculateAgreement("STAT"), 0.001);
		assertEquals(0.769, cat.calculateAgreement("IReq"), 0.001);
		
		// Two raters, chance-corrected agreement.
		TwoRaterSAgreement s = new TwoRaterSAgreement(study);
		assertEquals(0.7, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.4, s.calculateAgreement(), 0.001);
		
		TwoRaterPiAgreement pi = new TwoRaterPiAgreement(study);
		assertEquals(0.7, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.545, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.341, pi.calculateAgreement(), 0.001);
		
		TwoRaterKappaAgreement kappa = new TwoRaterKappaAgreement(study);
		assertEquals(0.7, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.54, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.348, kappa.calculateAgreement(), 0.001);
	}
	
	/***/
	public void testArtsteinPoesio2008_2() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_2();

		TwoRaterObservedAgreement raw = new TwoRaterObservedAgreement(study);
		assertEquals(0.88, raw.calculateAgreement(), 0.001);
		
		TwoRaterSAgreement s = new TwoRaterSAgreement(study);
		assertEquals(0.88, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.333, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.82, s.calculateAgreement(), 0.001);
		
		TwoRaterPiAgreement pi = new TwoRaterPiAgreement(study);
		assertEquals(0.88, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.401, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.799, pi.calculateAgreement(), 0.001);
		
		TwoRaterKappaAgreement kappa = new TwoRaterKappaAgreement(study);
		assertEquals(0.88, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.396, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.801, kappa.calculateAgreement(), 0.001);
	}

	/***/
	public void testMeyer2009_1() {
		AnnotationStudy study = AgreementTestExamples.createMeyer2009_1();
						
		TwoRaterObservedAgreement raw = new TwoRaterObservedAgreement(study);
		double agreement = raw.calculateAgreement();
		double se = raw.standardError(agreement);
		double[] ci = raw.confidenceInterval(agreement, se, TwoRaterObservedAgreement.CONFIDENCE_95);
		assertEquals(0.7, agreement);
		assertEquals(0.045, se, 0.001);
		assertEquals(0.610, ci[0], 0.001);
		assertEquals(0.789, ci[1], 0.001);	
		
		TwoRaterSAgreement s = new TwoRaterSAgreement(study);
		assertEquals(0.7, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.4, s.calculateAgreement(), 0.001);
		
		TwoRaterPiAgreement pi = new TwoRaterPiAgreement(study);
		assertEquals(0.7, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.545, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.341, pi.calculateAgreement(), 0.001);
		
		TwoRaterKappaAgreement kappa = new TwoRaterKappaAgreement(study);
		assertEquals(0.7, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.54, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.348, kappa.calculateAgreement(), 0.001);
	}
	
	/***/
	public void testNumericalStability() {
		IAnnotationStudy study = AgreementTestExamples.createNumericallyInstable();
		
		TwoRaterObservedAgreement raw = new TwoRaterObservedAgreement(study);
		assertEquals(0.9, raw.calculateAgreement(), 0.001);
		
		TwoRaterSAgreement s = new TwoRaterSAgreement(study);
		assertEquals(0.9, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, s.calculateAgreement(), 0.001);
		
		TwoRaterPiAgreement pi = new TwoRaterPiAgreement(study);
		assertEquals(0.9, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, pi.calculateAgreement(), 0.001);
		
		TwoRaterKappaAgreement kappa = new TwoRaterKappaAgreement(study);
		assertEquals(0.9, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, kappa.calculateAgreement(), 0.001);
		
		
		study = new AnnotationStudy(2);
		for (int i = 0; i < 81001; i++)
			study.addItem(1, 1);
		for (int i = 0; i < 9000; i++)
			study.addItem(1, 0);
		for (int i = 0; i < 1000; i++)
			study.addItem(0, 1);
		for (int i = 0; i < 9000; i++)
			study.addItem(0, 0);
		
		raw = new TwoRaterObservedAgreement(study);
		assertEquals(0.9, raw.calculateAgreement(), 0.001);
		
		s = new TwoRaterSAgreement(study);
		assertEquals(0.9, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8, s.calculateAgreement(), 0.001);
		
		pi = new TwoRaterPiAgreement(study);
		assertEquals(0.9, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.759, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.585, pi.calculateAgreement(), 0.001);
		
		kappa = new TwoRaterKappaAgreement(study);
		assertEquals(0.9, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.756, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.590, kappa.calculateAgreement(), 0.001);
	}

}
