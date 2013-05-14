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


/**
 * Implementation of Cohen's (1960) kappa-measure for calculating a
 * chance-corrected inter-rater agreement for two raters. The measure 
 * assumes a different probability distribution for all raters.<br><br>
 * References:<ul>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. 
 *   Educational and Psychological Measurement 20(1):37-46,
 *   Thousand Oaks, CA: Sage Publications, 1960.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class TwoRaterKappaAgreement extends TwoRaterSAgreement {

	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public TwoRaterKappaAgreement(IAnnotationStudy study) {
		super(study);
	}
	
	/** Calculates the expected inter-rater agreement that assumes a 
	 *  different probability distribution for all raters. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items in the 
	 *  	annotation study. */
	@Override
	public double calculateExpectedAgreement() {
		// If there are lots of items, use the numerically stable version.
		// Note: Shouldn't be used generally, as it introduces small rounding
		// errors for small examples.
		if (study.getItemCount() > 1000)
			return calculateExpectedAgreementStable();
		
		TreeMap<Object, int[]> annotationsPerCategory = new TreeMap<Object, int[]>();
		for (IAnnotationItem item : study.getItems())
			for (int annotator = 0; annotator < study.getAnnotatorCount(); annotator++) {
				Object annotation = item.getAnnotation(annotator);
				int[] counts = annotationsPerCategory.get(annotation);
				if (counts == null)
					counts = new int[study.getAnnotatorCount()];
				counts[annotator]++;
				annotationsPerCategory.put(annotation, counts);
			}
		
		double result = 0;
		for (Object category : study.getCategories()) {
			int[] annotations = annotationsPerCategory.get(category);
			double prod = 1;
			for (int annotator = 0; annotator < study.getAnnotatorCount(); annotator++)
				prod *= annotations[annotator];
			result += prod;
		}
		result /= (double) (study.getItemCount() * study.getItemCount());
		return result;
	}

	protected double calculateExpectedAgreementStable() {
		TreeMap<Object, long[]> annotationsPerCategory = new TreeMap<Object, long[]>();
		for (IAnnotationItem item : study.getItems())
			for (int annotator = 0; annotator < study.getAnnotatorCount(); annotator++) {
				Object annotation = item.getAnnotation(annotator);
				long[] counts = annotationsPerCategory.get(annotation);
				if (counts == null)
					counts = new long[study.getAnnotatorCount()];
				counts[annotator]++;
				annotationsPerCategory.put(annotation, counts);
			}
		
		// SUM(prod) / (i * i)
		// = EXP(LN(SUM(prod) / (i * i)))
		// = EXP(LN(SUM(prod)) - (LN(i) + LN(i)))
		double result = 0;
		for (Object category : study.getCategories()) {
			long[] annotations = annotationsPerCategory.get(category);
			double prod = 0;
			for (int annotator = 0; annotator < study.getAnnotatorCount(); annotator++)
				prod += Math.log(annotations[annotator]);
			result += Math.exp(prod);
		}
		result = Math.exp(Math.log(result) 
				- (Math.log(study.getItemCount()) + Math.log(study.getItemCount())));
		return result;
	}
	
}
