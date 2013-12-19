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

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy.IAnnotationItem;

/**
 * Extension for the simple percentage of agreement measure for calculating 
 * the inter-rater agreement of a certain category only in an annotation 
 * study of two raters. The measure is not chance-corrected.<br><br>
 * References:<ul>
 * <li>Cicchetti, D. V. & Feinstein, A. R.: High agreement but low kappa: 
 *   II. Resolving the paradoxes. Journal of Clinical Epidemiology 
 *   43(6):551-558, Amsterdam: Elsevier, 1990.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class TwoRaterObservedCategoryAgreement {

	protected IAnnotationStudy study;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public TwoRaterObservedCategoryAgreement(final IAnnotationStudy study) {
		this.study = study;
	}
	
	/** Calculates the inter-rater agreement for the given annotation category
	 *  based on the object's annotation study that has been passed to the 
	 *  class constructor.
	 *  @throws NullPointerException if the study is null or the given
	 *  	category is null.
	 *  @throws ArrayIndexOutOfBoundsException if the study does not contain 
	 *  	the given category.
	 *  @throws ArithmeticException if the study does not
	 *  	contain annotations for the given category. */
	public double calculateAgreement(final Object category) {
		int agreements = 0;
		int annotations = 0;
		for (IAnnotationItem item : study.getItems()) {
			if (category.equals(item.getAnnotation(0)) && category.equals(item.getAnnotation(1)))
				agreements++;
			if (category.equals(item.getAnnotation(0)))
				annotations++;
			if (category.equals(item.getAnnotation(1)))
				annotations++;
		}
		return (2 * agreements) / (double) annotations;
	}
		
}
