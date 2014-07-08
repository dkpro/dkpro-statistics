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
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.MASISetAnnotationDistanceFunction;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.SetAnnotation;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.SetAnnotationDistanceFunction;

/**
 * Tests for {@link KrippendorffAlphaAgreement} with
 * {@link SetAnnotationDistanceFunction} and 
 * {@link MASISetAnnotationDistanceFunction}.
 * @author Christian M. Meyer
 */
public class SetAnnotationsTest extends TestCase {
	
	/***/
	public void testSetDistanceFunction() {
		ICodingAnnotationStudy study = createExample();
		
		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, null);
		alpha.setDistanceFunction(new SetAnnotationDistanceFunction());
		assertEquals(0.333, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.409, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.186, alpha.calculateAgreement(), 0.001);
	}

	/***/
	public void testMASIDistanceFunction() {
		ICodingAnnotationStudy study = createExample();
		
		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, null);
		alpha.setDistanceFunction(new MASISetAnnotationDistanceFunction());
		assertEquals(0.253, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.338, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.252, alpha.calculateAgreement(), 0.001);
	}

	/** Creates an example annotation study. */
	public ICodingAnnotationStudy createExample() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addItem(new SetAnnotation(), new SetAnnotation());
		study.addItem(new SetAnnotation("A"), new SetAnnotation());
		study.addItem(new SetAnnotation("A"), new SetAnnotation("A"));
		study.addItem(new SetAnnotation("A"), new SetAnnotation("B"));
		study.addItem(new SetAnnotation("A", "B"), new SetAnnotation("A"));
		study.addItem(new SetAnnotation("A", "B"), new SetAnnotation("A", "C"));
		study.addItem(new SetAnnotation("A", "B", "C"), new SetAnnotation("A", "B", "C"));
		study.addItem(new SetAnnotation("C"), new SetAnnotation("A", "C"));
		study.addItem(new SetAnnotation("C"), new SetAnnotation("A", "C"));
		return study;
	}
}
