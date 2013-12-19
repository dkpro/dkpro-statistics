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
 * Generalization of Cohen's (1960) kappa-measure for calculating a
 * chance-corrected inter-rater agreement for multiple raters with arbitrary
 * distance/weighting functions. Before an inter-rater agreement can be 
 * calculated, an {@link IDistanceFunction} instance needs to 
 * be assigned.<br><br>
 * References:<ul>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. 
 *   Educational and Psychological Measurement 20(1):37-46, 
 *   Beverly Hills, CA: Sage Publications, 1960.
 * <li>Cohen, J.: Weighted kappa: Nominal scale agreement with provision 
 *   for scaled disagreement or partial credit. Psychological Bulletin 
 *   70(4):213-220, Washington, DC: American Psychological Association, 1968.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class WeightedKappaAgreement {

	protected IAnnotationStudy study;
	protected IDistanceFunction distanceFunction;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public WeightedKappaAgreement(final IAnnotationStudy study) {
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
			
			for (Object category1 : study.getCategories())
				for (Object category2 : study.getCategories()) {
					Integer cat1Count = annotationsPerCategory.get(category1);
					Integer cat2Count = annotationsPerCategory.get(category2);
					result += (cat1Count != null ? cat1Count : 0)
							* (cat2Count != null ? cat2Count : 0)
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
