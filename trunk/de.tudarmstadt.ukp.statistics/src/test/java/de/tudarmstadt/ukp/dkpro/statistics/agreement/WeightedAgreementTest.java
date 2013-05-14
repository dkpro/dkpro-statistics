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

import java.util.Hashtable;

import junit.framework.TestCase;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.KrippendorffAlphaAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.WeightedKappaAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.util.CachedDistanceFunction;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.util.IDistanceFunction;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.util.SquareDistanceFunction;

/**
 * Tests for {@link WeightedKappaAgreement} and 
 * {@link KrippendorffAlphaAgreement}.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class WeightedAgreementTest extends TestCase {
	
	/***/
	public void testArtsteinPoesio2008_2() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_2();
		IDistanceFunction weightedDistanceFunction = new IDistanceFunction() {
			
			public double measureDistance(final IAnnotationStudy study, 
					final Object category1, final Object category2) {
				if (category1.equals(category2))
					return 0.0;
				if ("Chck".equals(category1) || "Chck".equals(category2))
					return 0.5;
				return 1.0;
			}
			
		};
		
		// More or less the same results as multi-pi.
		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study);
		alpha.setDistanceFunction(new SquareDistanceFunction());
		assertEquals(0.12, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.601, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.800, alpha.calculateAgreement(), 0.001);		
		
		alpha.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.09, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.4879, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.8156, alpha.calculateAgreement(), 0.001);
		
		// More or less the same results as multi-pi.
		WeightedKappaAgreement kappa = new WeightedKappaAgreement(study);
		kappa.setDistanceFunction(new SquareDistanceFunction());
		assertEquals(0.12, kappa.calculateObservedDisagreement(), 0.001);
		assertEquals(0.601, kappa.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.800, kappa.calculateAgreement(), 0.001);		
		
		kappa.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.09, kappa.calculateObservedDisagreement(), 0.001);
		assertEquals(0.49, kappa.calculateExpectedDisagreement(), 0.01);
		assertEquals(0.8163, kappa.calculateAgreement(), 0.001);
	} 

	/***/
	public void testMeyer2009_3() {
		IAnnotationStudy study = new AnnotationStudy(2);
		study.addItem("", "");
		study.addItem("A", "");
		study.addItem("A", "A");
		study.addItem("A", "B");
		study.addItem("AB", "A");
		study.addItem("AB", "AC");
		study.addItem("ABC", "ABC");
		study.addItem("C", "AC");
		study.addItem("C", "AC");
		
		// Example for set annotation study with simple distance function.
		IDistanceFunction weightedDistanceFunction = new IDistanceFunction() {
			
			public double measureDistance(final IAnnotationStudy study, 
					final Object category1, final Object category2) {
				final double[][] WEIGHTS = new double[][]{
						{0, 1, 1, 1, 1, 1, 1},
						{1, 0, 3, 1, 1, 1, 3},
						{1, 3, 0, 1, 3, 1, 3},
						{1, 1, 1, 0, 2, 1, 3},
						{1, 1, 3, 2, 0, 1, 1},
						{1, 1, 1, 1, 1, 0, 1},
						{1, 3, 3, 3, 1, 1, 0}
				};
				final Hashtable<Object, Integer> idx = new Hashtable<Object, Integer>();
				idx.put("", 0);
				idx.put("A", 1);
				idx.put("B", 2);
				idx.put("AB", 3);
				idx.put("AC", 4);
				idx.put("ABC", 5);
				idx.put("C", 6);
				
				return WEIGHTS[idx.get(category1)][idx.get(category2)] / 3.0;
			}
			
		};
		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study);
		alpha.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.333, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.409, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.186, alpha.calculateAgreement(), 0.001);	
		
		// Example for set annotation study with MASI distance function.
		weightedDistanceFunction = new IDistanceFunction() {
			
			public double measureDistance(final IAnnotationStudy study, 
					final Object category1, final Object category2) {
				final double[][] WEIGHTS = new double[][]{
						{0/3.0, 1/3.0, 1/3.0, 1/3.0, 1/3.0, 1/3.0, 1/3.0},
						{1/3.0, 0/3.0, 3/3.0, 1/6.0, 1/6.0, 2/9.0, 3/3.0},
						{1/3.0, 3/3.0, 0/3.0, 1/6.0, 3/3.0, 2/9.0, 3/3.0},
						{1/3.0, 1/6.0, 1/6.0, 0/3.0, 4/9.0, 1/9.0, 3/3.0},
						{1/3.0, 1/6.0, 3/3.0, 4/9.0, 0/3.0, 1/9.0, 1/6.0},
						{1/3.0, 2/9.0, 2/9.0, 1/9.0, 1/9.0, 0/3.0, 2/9.0},
						{1/3.0, 3/3.0, 3/3.0, 3/3.0, 1/6.0, 2/9.0, 0/3.0}
				};
				final Hashtable<Object, Integer> idx = new Hashtable<Object, Integer>();
				idx.put("", 0);
				idx.put("A", 1);
				idx.put("B", 2);
				idx.put("AB", 3);
				idx.put("AC", 4);
				idx.put("ABC", 5);
				idx.put("C", 6);
				
				return WEIGHTS[idx.get(category1)][idx.get(category2)];
			}
			
		};
		alpha.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.253, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.338, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.252, alpha.calculateAgreement(), 0.001);		
	} 

	/***/
	public void testArtsteinPoesio2008_2_chachedDistanceFunction() {
		IAnnotationStudy study = AgreementTestExamples.createArtsteinPoesio2008_2();
		IDistanceFunction weightedDistanceFunction = new IDistanceFunction() {
			
			public double measureDistance(final IAnnotationStudy study, 
					final Object category1, final Object category2) {
				if (category1.equals(category2))
					return 0.0;
				if ("Chck".equals(category1) || "Chck".equals(category2))
					return 0.5;
				return 1.0;
			}
			
		};
		weightedDistanceFunction = new CachedDistanceFunction(weightedDistanceFunction);
		
		// More or less the same results as multi-pi.
		KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study);
		alpha.setDistanceFunction(new SquareDistanceFunction());
		assertEquals(0.12, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.601, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.800, alpha.calculateAgreement(), 0.001);		
		
		alpha.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.09, alpha.calculateObservedDisagreement(), 0.001);
		assertEquals(0.4879, alpha.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.8156, alpha.calculateAgreement(), 0.001);
		
		// More or less the same results as multi-pi.
		WeightedKappaAgreement kappa = new WeightedKappaAgreement(study);
		kappa.setDistanceFunction(new SquareDistanceFunction());
		assertEquals(0.12, kappa.calculateObservedDisagreement(), 0.001);
		assertEquals(0.601, kappa.calculateExpectedDisagreement(), 0.001);
		assertEquals(0.800, kappa.calculateAgreement(), 0.001);		
		
		kappa.setDistanceFunction(weightedDistanceFunction);
		assertEquals(0.09, kappa.calculateObservedDisagreement(), 0.001);
		assertEquals(0.49, kappa.calculateExpectedDisagreement(), 0.01);
		assertEquals(0.8163, kappa.calculateAgreement(), 0.001);
	} 
	
}
