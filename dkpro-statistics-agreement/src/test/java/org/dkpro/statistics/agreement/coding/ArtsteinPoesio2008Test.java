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

import org.dkpro.statistics.agreement.IAnnotationStudy;
import org.dkpro.statistics.agreement.coding.BennettSAgreement;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.CohenKappaAgreement;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;
import org.dkpro.statistics.agreement.coding.PercentageAgreement;
import org.dkpro.statistics.agreement.coding.ScottPiAgreement;
import org.dkpro.statistics.agreement.coding.WeightedKappaAgreement;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;

import junit.framework.TestCase;

/**
 * Tests based on Artstein & Poesio (2008) for several inter-rater agreement
 * measures.<br><br>
 * References:<ul>
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li></ul>
 * @author Christian M. Meyer
 */
public class ArtsteinPoesio2008Test extends TestCase {

	/***/
	public void testExample1() {
		ICodingAnnotationStudy study = createExample1();

		// Two raters, observed agreement.
		PercentageAgreement poa = new PercentageAgreement(study);
		assertEquals(0.7, poa.calculateAgreement(), 0.001);

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
	public void testExample2() {
		ICodingAnnotationStudy study = createExample2();

		PercentageAgreement poa = new PercentageAgreement(study);
		assertEquals(0.88, poa.calculateAgreement(), 0.001);

		BennettSAgreement s = new BennettSAgreement(study);
		assertEquals(0.88, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.333, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.82, s.calculateAgreement(), 0.001);

		ScottPiAgreement pi = new ScottPiAgreement(study);
		assertEquals(0.88, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.401, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.799, pi.calculateAgreement(), 0.001);

		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.88, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.396, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.801, kappa.calculateAgreement(), 0.001);
	}

	/*
	public void testCategoryAgreement() {
		//TODO positive and negative agreement!
		ICodingAnnotationStudy study = createExample1();

		PercentageAgreement cat = new PercentageAgreement(study);
		assertEquals(0.571, cat.calculateCategoryAgreement("STAT"), 0.001);
		assertEquals(0.769, cat.calculateCategoryAgreement("IReq"), 0.001);
	}*/

	/***/
	public void testWeightedAgreement() {
		ICodingAnnotationStudy study = createExample2();
		IDistanceFunction weightedDistanceFunction = new IDistanceFunction() {
			@Override
            public double measureDistance(final IAnnotationStudy study,
					final Object category1, final Object category2) {
				if (category1.equals(category2)) {
                    return 0.0;
                }
				if ("Chck".equals(category1) || "Chck".equals(category2)) {
                    return 0.5;
                }
				return 1.0;
			}
		};

		// Unweighted coefficients.
		PercentageAgreement poa = new PercentageAgreement(study);
		assertEquals(0.880, poa.calculateAgreement(), 0.001);

		BennettSAgreement s = new BennettSAgreement(study);
		assertEquals(0.880, s.calculateObservedAgreement(), 0.001);
		assertEquals(0.333, s.calculateExpectedAgreement(), 0.001);
		assertEquals(0.820, s.calculateAgreement(), 0.001);
		ScottPiAgreement pi = new ScottPiAgreement(study);
		assertEquals(0.880, pi.calculateObservedAgreement(), 0.001);
		assertEquals(0.4014, pi.calculateExpectedAgreement(), 0.001);
		assertEquals(0.7995, pi.calculateAgreement(), 0.001);
		CohenKappaAgreement kappa = new CohenKappaAgreement(study);
		assertEquals(0.880, kappa.calculateObservedAgreement(), 0.001);
		assertEquals(0.396, kappa.calculateExpectedAgreement(), 0.001);
		assertEquals(0.8013, kappa.calculateAgreement(), 0.001);

		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
				new NominalDistanceFunction());
		assertEquals(0.120, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.601, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.800, alpha.calculateAgreement(), 0.001);

		alpha.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.090, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.4879, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.8156, alpha.calculateAgreement(), 0.001);

		WeightedKappaAgreement kappaW = new WeightedKappaAgreement(study,
				new NominalDistanceFunction());
		assertEquals(0.120, kappaW.calculateObservedDisagreement(), 0.001);
		assertEquals(0.604, kappaW.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.8013, kappaW.calculateAgreement(), 0.001);

		kappaW.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.090, kappaW.calculateObservedDisagreement(), 0.001);
		assertEquals(0.490, kappaW.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.8163, kappaW.calculateAgreement(), 0.001);
	}

	/** Creates an example annotation study introduced by
	 *  Artstein&Poesio (2008: p. 558). */
	public static ICodingAnnotationStudy createExample1() {
		/*		STAT	IReq	  Σ
		STAT	 20		 10		 30
		IReq	 20		 50		 70
		Σ   	 40		 60		100 */
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(20, "STAT", "STAT");
		study.addMultipleItems(20, "IReq", "STAT");
		study.addMultipleItems(10, "STAT", "IReq");
		study.addMultipleItems(50, "IReq", "IReq");
		return study;
	}

	/** Creates an example annotation study introduced by
	 *  Artstein&Poesio (2008: p. 568). */
	public static ICodingAnnotationStudy createExample2() {
		/*  	STAT	IReq	Chck	  Σ
		STAT	 46		  0		  0		 46
		IReq	  6		 32		  6		 44
		Chck	  0		  0		 10		 10
		Σ   	 52		 32		 16		100 */
		CodingAnnotationStudy study = new CodingAnnotationStudy(2);
		study.addMultipleItems(46, "STAT", "STAT");
		study.addMultipleItems( 6, "IReq", "STAT");
		study.addMultipleItems(32, "IReq", "IReq");
		study.addMultipleItems( 6, "IReq", "Chck");
		study.addMultipleItems(10, "Chck", "Chck");
		return study;
	}

}
