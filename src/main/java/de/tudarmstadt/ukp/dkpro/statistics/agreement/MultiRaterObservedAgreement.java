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
 * Implementation of a simple percentage of agreement measure for calculating 
 * the inter-rater agreement for multiple raters. The measure is neither 
 * chance-corrected nor weighted.<br><br>
 * References:<ul>
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class MultiRaterObservedAgreement {

	protected IAnnotationStudy study;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public MultiRaterObservedAgreement(final IAnnotationStudy study) {
		this.study = study;
	}

	/** Calculates the inter-rater agreement for the annotation 
	 *  study that was passed to the class constructor. 
	 *  @throws NullPointerException if the study is null.
	 *  @throws ArithmeticException if the study does not contain any item or
	 *  	the number of raters is smaller than 2. */
	public double calculateAgreement() {
		int result = 0;
		for (IAnnotationItem item : study.getItems())
			result += calculateAgreement(item);
		return result / (double) (study.getItemCount() 
				* study.getAnnotatorCount()
				* (study.getAnnotatorCount() - 1));
	}

	/** Calculates the inter-rater agreement for the given annotation item. 
	 *  This is the basic step that is performed for each item of an 
	 *  annotation study, when calling {@link #calculateAgreement()}. 
	 *  @throws NullPointerException if the given item is null. */
	public double calculateAgreement(final IAnnotationItem item) {
		TreeMap<Object, Integer> annotationsPerCategory = new TreeMap<Object, Integer>();
		for (Object annotation : item.getAnnotations()) {
			Integer count = annotationsPerCategory.get(annotation);
			if (count == null)
				annotationsPerCategory.put(annotation, 1);
			else
				annotationsPerCategory.put(annotation, count + 1);
		}
		double result = 0;
		for (Integer count : annotationsPerCategory.values())
			result += count * (count - 1);
		return result;
	}

}
