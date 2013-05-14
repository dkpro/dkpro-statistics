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

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.MultiRaterPiCategoryAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterKappaCategoryAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterObservedCategoryAgreement;
import junit.framework.TestCase;

/**
 * Tests for {@link TwoRaterObservedCategoryAgreement} 
 * and {@link MultiRaterPiCategoryAgreement}.
 * @author Christian M. Meyer
 * @date 04.03.2011
 */
public class CategoryAgreementTest extends TestCase {

	/***/
	public void testArtsteinPoesio2008_1() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_1();
		
		TwoRaterObservedCategoryAgreement cat = new TwoRaterObservedCategoryAgreement(study);
		assertEquals(0.571, cat.calculateAgreement("STAT"), 0.001);
		assertEquals(0.769, cat.calculateAgreement("IReq"), 0.001);
	}
	
	/***/
	public void testFleiss1971Example() {
		IAnnotationStudy study = AgreementTestExamples.createFleiss1972();
		assertEquals(30, study.getItemCount());
				
		TwoRaterKappaCategoryAgreement catKappa = new TwoRaterKappaCategoryAgreement(study);
		assertEquals(0.248, catKappa.calculateAgreement(1), 0.005);
		assertEquals(0.248, catKappa.calculateAgreement(2), 0.005);
		assertEquals(0.517, catKappa.calculateAgreement(3), 0.005);
		assertEquals(0.470, catKappa.calculateAgreement(4), 0.005);
		assertEquals(0.565, catKappa.calculateAgreement(5), 0.005);		
		
		MultiRaterPiCategoryAgreement catKappa2 = new MultiRaterPiCategoryAgreement(study);
		assertEquals(0.248, catKappa2.calculateAgreement(1), 0.005);
		assertEquals(0.248, catKappa2.calculateAgreement(2), 0.005);
		assertEquals(0.517, catKappa2.calculateAgreement(3), 0.005);
		assertEquals(0.470, catKappa2.calculateAgreement(4), 0.005);
		assertEquals(0.565, catKappa2.calculateAgreement(5), 0.005);		
	}  
	
}
