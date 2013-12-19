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
 * Implementation of Scott's (1955) pi-measure for calculating a
 * chance-corrected inter-rater agreement for two raters. The measure 
 * assumes the same probability distribution for all raters.<br><br>
 * References:<ul>
 * <li>Scott, W. A.: Reliability of content analysis: The case of nominal 
 *   scale coding Public Opinion Quaterly 19(3):321-325, 
 *   Princeton, NJ: Princeton University, 1955.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class TwoRaterPiAgreement extends TwoRaterSAgreement {

	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public TwoRaterPiAgreement(final IAnnotationStudy study) {
		super(study);
	}
	
	/** Calculates the expected inter-rater agreement that assumes the same
	 *  distribution for all raters and annotations. 
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
		
		TreeMap<Object, Integer> annotationsPerCategory = new TreeMap<Object, Integer>();
		for (IAnnotationItem item : study.getItems())
			for (Object annotation : item.getAnnotations()) {
				Integer count = annotationsPerCategory.get(annotation);
				if (count == null)
					annotationsPerCategory.put(annotation, 1);
				else
					annotationsPerCategory.put(annotation, count + 1);
			}
			
		double result = 0;
		for (Object category : study.getCategories())
			result += annotationsPerCategory.get(category)
					* annotationsPerCategory.get(category);
		result /= (double) (4 * study.getItemCount() * study.getItemCount());
		return result;
	}
	
	protected double calculateExpectedAgreementStable() {
		TreeMap<Object, Long> annotationsPerCategory = new TreeMap<Object, Long>();
		for (IAnnotationItem item : study.getItems())
			for (Object annotation : item.getAnnotations()) {
				Long count = annotationsPerCategory.get(annotation);
				if (count == null)
					annotationsPerCategory.put(annotation, 1L);
				else
					annotationsPerCategory.put(annotation, count + 1);
			}
			
		// SUM(annotationsPerCategory^2) / (4 * i * i)
		// = EXP(LN(SUM(annotationsPerCategory^2) / (4 * i * i)))
		// = EXP(LN(SUM(annotationsPerCategory^2)) - (LN(4) + LN(i) + LN(i)))
		double result = 0;
		for (Object category : study.getCategories())
			result += Math.exp(Math.log(annotationsPerCategory.get(category))
					+ Math.log(annotationsPerCategory.get(category)));
		result = Math.exp(Math.log(result) - (Math.log(4) 
				+ Math.log(study.getItemCount()) + Math.log(study.getItemCount())));
		return result;
	}
	
}
