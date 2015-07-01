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

import org.dkpro.statistics.agreement.IAnnotationUnit;
import org.dkpro.statistics.agreement.ICategorySpecificAgreement;

/**
 * Implementation of a simple percentage of agreement measure for calculating
 * the inter-rater agreement for two or more raters. The measure is neither
 * chance-corrected nor weighted.<br><br>
 * References:<ul>
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li></ul>
 * @author Christian M. Meyer
 */
//TODO: Holsti.
public class PercentageAgreement extends CodingAgreementMeasure
		implements ICodingItemSpecificAgreement, ICategorySpecificAgreement/*, IRaterAgreement*/ {

	/** Initializes the instance for the given annotation study. The study
	 *  should never be null. */
	public PercentageAgreement(final ICodingAnnotationStudy study) {
		super(study);
	}

	/** Calculates the inter-rater agreement for the given annotation item.
	 *  This is the basic step that is performed for each item of an
	 *  annotation study, when calling {@link #calculateAgreement()}.
	 *  @throws NullPointerException if the given item is null. */
	public double calculateItemAgreement(final ICodingAnnotationItem item) {
		return doCalculateItemAgreement(item) / item.getRaterCount();
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
	/*public double calculateAgreement(final Object category) {
	  // This is positive and negative agreement (Feinstein90)
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
	}*/
	public double calculateCategoryAgreement(final Object category) {
		double result = 0;
		for (ICodingAnnotationItem item : study.getItems()) {
			int catCount = 0;
			int otherCatCount = 0;
			for (IAnnotationUnit annotation : item.getUnits())
				if (category.equals(annotation.getCategory()))
					catCount++;
				else
					otherCatCount++;
			result += catCount * (catCount - 1) + otherCatCount * (otherCatCount - 1);
		}
		return result / (double) (study.getItemCount()
				* study.getRaterCount() * (study.getRaterCount() - 1));
	}

}
