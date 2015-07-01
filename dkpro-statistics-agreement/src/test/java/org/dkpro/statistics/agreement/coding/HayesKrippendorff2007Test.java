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

import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;
import org.dkpro.statistics.agreement.distance.OrdinalDistanceFunction;

import junit.framework.TestCase;

/**
 * Tests based on Hayes & Krippendorff (2007) for measuring
 * {@link KrippendorffAlphaAgreement} with an
 * {@link OrdinalDistanceFunction}.<br><br>
 * References:<ul>
 * <li>Hayes, A.F. & Krippendorff, K.: Answering the call for a standard
 *   reliability measure for coding data. Communication Methods and Measures
 *   1(1):77–89, 2007.</li></ul>
 * @author Christian M. Meyer
 */
public class HayesKrippendorff2007Test extends TestCase {

	/***/
	public void testAgreement() {
		ICodingAnnotationStudy study = createExample();

		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
				new OrdinalDistanceFunction());
		assertEquals(0.7598, alpha.calculateAgreement(), 0.0001);
	}

	/** Creates an example annotation study introduced by
	 *  Hayes&Krippendorff (2007: p. 84). */
	public static ICodingAnnotationStudy createExample() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(5);
		study.addItem(1, 1, 2, null, 2);
		study.addItem(1, 1, 0, 1, null);
		study.addItem(2, 3, 3, 3, null);
		study.addItem(null, 0, 0, null, 0);
		study.addItem(0, 0, 0, null, 0);
		study.addItem(0, 0, 0, null, 0);
		study.addItem(1, 0, 2, null, 1);
		study.addItem(1, null, 2, 0, null);
		study.addItem(2, 2, 2, null, 2);
		study.addItem(2, 1, 1, 1, null);
		study.addItem(null, 1, 0, 0, null);
		study.addItem(0, 0, 0, 0, null);
		study.addItem(1, 2, 2, 2, null);
		study.addItem(3, 3, 2, 2, 3);
		study.addItem(1, 1, 1, null, 1);
		study.addItem(1, 1, 1, null, 1);
		study.addItem(2, 1, 2, null, 2);
		study.addItem(1, 2, 3, 3, null);
		study.addItem(1, 1, 0, 1, null);
		study.addItem(0, 0, 0, null, 0);
		study.addItem(0, 0, 1, 1, null);
		study.addItem(0, 0, null, 0, 0);
		study.addItem(2, 3, 3, 3, null);
		study.addItem(0, 0, 0, 0, null);
		study.addItem(1, 2, null, 2, 2);
		study.addItem(0, 1, 1, 1, null);
		study.addItem(0, 0, 0, 1, 0);
		study.addItem(1, 2, 1, 2, null);
		study.addItem(1, 1, 2, 2, null);
		study.addItem(1, 1, 2, null, 2);
		study.addItem(1, 1, 0, null, 0);
		study.addItem(2, 1, 2, 1, null);
		study.addItem(2, 2, null, 2, 2);
		study.addItem(3, 2, 2, 2, null);
		study.addItem(2, 2, 2, null, 2);
		study.addItem(2, 2, 3, null, 2);
		study.addItem(2, 2, 2, null, 2);
		study.addItem(2, 2, null, 1, 2);
		study.addItem(2, 2, 2, 2, null);
		study.addItem(1, 1, 1, null, 1);
		return study;
	}

}
