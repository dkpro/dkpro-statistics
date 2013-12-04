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
package de.tudarmstadt.ukp.dkpro.statistics.unitizing;

import static org.junit.Assert.*;

import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.statistics.unitizing.AlphaUnitizedAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.unitizing.UnitizingStudy;

public class TestAlphaUnitizedAgreement {

	@Test
	public void testAlphaUnitizedAgreementObservedDisagreement() {
		UnitizingStudy study = StudyFactory.getStudy();
		AlphaUnitizedAgreement alpha = new AlphaUnitizedAgreement(study);
		assertEquals(0.0144, alpha.getObservedDisagreement("c"), 0.0001);
		assertEquals(0.0, alpha.getObservedDisagreement("k"), 0.0);
	}

	@Test
	public void testAlphaUnitizedAgreementExpectedDisagreement() {
		UnitizingStudy study = StudyFactory.getStudy();
		AlphaUnitizedAgreement alpha = new AlphaUnitizedAgreement(study);
		assertEquals(0.0532, alpha.getExpectedDisagreement("c"), 0.0001);
		assertEquals(0.0490, alpha.getExpectedDisagreement("k"), 0.0001);
	}
	
	@Test
	public void testAlphaUnitizedCategoryAgreement() {
		UnitizingStudy study = StudyFactory.getStudy();
		AlphaUnitizedAgreement alpha = new AlphaUnitizedAgreement(study);
		// The expected value differs from the value 
		// of Krippendorf's paper because there 
		// all values are rounded to 4 decimal places
		assertEquals(0.7285, alpha.estimateCategoryAgreement("c"), 0.0001);
		assertEquals(1.0, alpha.estimateCategoryAgreement("k"), 0.0001);
	}
	
	@Test
	public void testAlphaUnitizedJointAgreement() {
		UnitizingStudy study = StudyFactory.getStudy();
		AlphaUnitizedAgreement alpha = new AlphaUnitizedAgreement(study);
		// The expected value differs from the value 
		// of Krippendorf's paper because there 
		// all values are rounded to 4 decimal places
		assertEquals(0.8587, alpha.estimateJointAgreement(), 0.0001);
		
	}
}
