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
 * Implementation of a simple percentage of agreement measure for calculating 
 * the inter-rater agreement for two raters. The measure is neither 
 * chance-corrected nor weighted.<br><br>
 * References:<ul>
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class TwoRaterObservedAgreement {

	/** Critical z-value for a confidence of 90%. */
	public static final double CONFIDENCE_90 = 1.645;
	/** Critical z-value for a confidence of 95%. */
	public static final double CONFIDENCE_95 = 1.96;
	
	protected IAnnotationStudy study;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public TwoRaterObservedAgreement(final IAnnotationStudy study) {
		this.study = study;
	}

	/** Calculates the inter-rater agreement for the annotation 
	 *  study that was passed to the class constructor. 
	 *  @throws NullPointerException if the study is null.
	 *  @throws ArithmeticException if the study does not contain any item.
	 *  @throws ArrayIndexOutOfBoundsException if the study does not
	 *  	contain annotations for two raters. */
	public double calculateAgreement() {
		int result = 0;
		for (IAnnotationItem item : study.getItems())
			result += calculateAgreement(item);
		return result / (double) study.getItemCount();
	}

	/** Calculates the inter-rater agreement for the given annotation item. 
	 *  This is the basic step that is performed for each item of an 
	 *  annotation study, when calling {@link #calculateAgreement()}. 
	 *  @throws NullPointerException if the given item is null.
	 *  @throws ArrayIndexOutOfBoundsException if the given item does not 
	 *      have two annotations for two raters. */
	public double calculateAgreement(final IAnnotationItem item) {
		return (item.getAnnotation(0).equals(item.getAnnotation(1)) ? 1 : 0);
	}

	
	/** Returns the standard error for the given agreement. Use 
	 *  {@link #calculateAgreement()} to calculate the parameter. */
	public double standardError(final double agreement) {
		return Math.sqrt(agreement * (1 - agreement)) 
				/ (double) study.getItemCount();
	}
	
	/** Calculates the confidence interval for the given agreement. The 
	 *  required parameters can be produced by invoking 
	 *  {@link #calculateAgreement()} and {@link #standardError(double)}, 
	 *  while the critical z-values are available as constants for the
	 *  most common confidence levels. The result is returned as a 
	 *  float array with the lower interval bound as first element and
	 *  the upper bound as second element. */
	public double[] confidenceInterval(final double agreement,
			final double standardError, final double zCrit) {
		double deviation = standardError * zCrit;
		return new double[]{agreement - deviation, agreement + deviation};
	}
	
}
