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

import java.util.TreeMap;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy.IAnnotationItem;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.util.IDistanceFunction;

/**
 * Implementation of Krippendorff's (1980) alpha-measure for calculating a
 * chance-corrected inter-rater agreement for multiple raters with arbitrary
 * distance/weighting functions. The basic idea is to divide the estimated
 * variance of within the items by the estimated total variance. Before an
 * inter-rater agreement can be calculated, an {@link IDistanceFunction}
 * instance needs to be assigned.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Estimating the reliability, systematic error and 
 *   random error of interval data. Educational and Psychological Measurement
 *   30(1):61-70, Beverly Hills, CA: Sage Publications, 1970.
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   2nd edition, Thousand Oaks, CA: Sage Publications, 2004.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class KrippendorffAlphaAgreement {

	protected IAnnotationStudy study;
	protected IDistanceFunction distanceFunction;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public KrippendorffAlphaAgreement(final IAnnotationStudy study) {
		this.study = study;
	}
	
	/** Returns the distance function that is used to measure the distance
	 *  between two annotation categories. */
	public IDistanceFunction getDistanceFunction() {
		return distanceFunction;
	}
	
	/** Uses the given distance function for upcoming calculations of the
	 *  inter-rater agreement. */
	public void setDistanceFunction(final IDistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}
	
	/** Calculates the inter-rater agreement for the annotation 
	 *  study that was passed to the class constructor and the currently
	 *  assigned distance function. 
	 *  @throws NullPointerException if the study is null.
	 *  @throws ArithmeticException if the study does not contain any item or
	 *  	the number of raters is smaller than 2. */
	public double calculateAgreement() {
		double DO = calculateObservedDisagreement();
		double DE = calculateExpectedDisagreement();
		return 1.0 - (DO / DE);
	}
	
	/** Calculates the observed inter-rater agreement for the annotation 
	 *  study that was passed to the class constructor and the currently
	 *  assigned distance function.
	 *  @throws NullPointerException if the study is null.
	 *  @throws ArithmeticException if the study does not contain any item or
	 *  	the number of raters is smaller than 2. */
	public double calculateObservedDisagreement() {
		double result = 0;
		for (IAnnotationItem item : study.getItems()) {
			TreeMap<Object, Integer> annotationsPerCategory 
					= new TreeMap<Object, Integer>();
			for (Object annotation : item.getAnnotations()) {
				Integer count = annotationsPerCategory.get(annotation);
				if (count == null)
					annotationsPerCategory.put(annotation, 1);
				else
					annotationsPerCategory.put(annotation, count + 1);
			}
			
			/*for (Object category1 : study.getCategories())
				if (annotationsPerCategory.get(category1)!=null)
				System.out.println(category1 + " = " + annotationsPerCategory.get(category1));
			System.out.println("----------" + Arrays.toString(item.getAnnotations()));*/
				
			for (Object category1 : annotationsPerCategory.keySet())
				for (Object category2 : annotationsPerCategory.keySet()) {
					Integer cat1Count = annotationsPerCategory.get(category1);
					if (cat1Count == null)
						continue;
					Integer cat2Count = annotationsPerCategory.get(category2);
					if (cat2Count == null)
						continue;
					
					result += cat1Count * cat2Count 
							* distanceFunction.measureDistance(study, category1, category2);
				}
		}

		result /= (double) (study.getItemCount() * study.getAnnotatorCount() 
				* (study.getAnnotatorCount() - 1));
		return result;
	}

	/** Calculates the expected inter-rater agreement using the defined 
	 *  distance function to infer the assumed probability distribution. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items or raters in the 
	 *  	annotation study. */
	public double calculateExpectedDisagreement() {
		double result = 0;
		TreeMap<Object, Integer> annotationsPerCategory 
				= new TreeMap<Object, Integer>();
		for (IAnnotationItem item : study.getItems())
			for (Object annotation : item.getAnnotations()) {
				Integer count = annotationsPerCategory.get(annotation);
				if (count == null)
					annotationsPerCategory.put(annotation, 1);
				else
					annotationsPerCategory.put(annotation, count + 1);
			}
		
		for (Object category1 : study.getCategories())
			for (Object category2 : study.getCategories())
				result += annotationsPerCategory.get(category1)
						* annotationsPerCategory.get(category2)
						* distanceFunction.measureDistance(study, category1, category2);
			
		result /= (double) (study.getItemCount() * study.getAnnotatorCount() 
				* (study.getItemCount() * study.getAnnotatorCount() - 1));
		return result;
	}
	
}
