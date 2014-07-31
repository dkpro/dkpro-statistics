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

import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.FleissKappaAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.PercentageAgreement;
import junit.framework.TestCase;

/**
 * Tests for {@link PercentageAgreement} and {@link FleissKappaAgreement}.
 * @author Christian M. Meyer
 */
public class MultiRaterAgreementTest extends TestCase {
	
	/***/
	public void testExample() {
		ICodingAnnotationStudy study = createExample();
				
		// Generalized percentage of agreement.
		PercentageAgreement pa = new PercentageAgreement(study);
		double agreement = pa.calculateAgreement();
		assertEquals(0.533, agreement, 0.001);
		//TODO
		/*double se = poa.standardError(agreement);
		double[] ci = poa.confidenceInterval(agreement, se, RawAgreement.CONFIDENCE_95);
		assertEquals(0.045, se, 0.001);
		assertEquals(0.610, ci[0], 0.001);
		assertEquals(0.789, ci[1], 0.001);*/		
		
		// Fleiss' multi-pi.
		FleissKappaAgreement pi = new FleissKappaAgreement(study);
		assertEquals(0.533, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.508, pi.calculateExpectedAgreement(), 0.001);
		agreement = pi.calculateAgreement();
		assertEquals(0.049, agreement, 0.001);
	}

	/** Creates an example annotation study. */
	public static ICodingAnnotationStudy createExample() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(3);
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
		return study;
	}
	
}
