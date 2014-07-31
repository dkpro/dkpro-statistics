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
package de.tudarmstadt.ukp.dkpro.statistics.agreement;

/**
 * Default implementation of the {@link IAgreementMeasure} interface providing
 * computational hooks for calculating the observed and the expected 
 * disagreement. The values for observed and expected disagreement are 
 * combined using Krippendorff's (1980)<center>1 - (D_O / D_E)</center> formula 
 * (where D_O denotes the observed disagreement and D_E denotes the expected 
 * disagreement. See also {@link AgreementMeasure} for the analogous 
 * definition of a measure based on the observed and expected agreement.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see IAgreementMeasure
 * @see AgreementMeasure
 * @author Christian M. Meyer
 */
public abstract class DisagreementMeasure implements IAgreementMeasure {

	/* Calculates the inter-rater agreement for the annotation 
	 *  study that was passed to the class constructor and the currently
	 *  assigned distance function. 
	 *  @throws NullPointerException if the study is null.
	 *  @throws ArithmeticException if the study does not contain any item or
	 *  	the number of raters is smaller than 2. */
	@Override
	public double calculateAgreement() {
		double D_O = calculateObservedDisagreement();
		double D_E = calculateExpectedDisagreement();
		if (D_O == D_E)
			return 0.0;
		else
			return 1.0 - (D_O / D_E);
	}

	protected abstract double calculateObservedDisagreement();

	protected double calculateExpectedDisagreement() {
		return 0.0;
	}

}
