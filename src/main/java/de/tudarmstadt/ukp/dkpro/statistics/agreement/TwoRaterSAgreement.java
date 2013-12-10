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


/**
 * Implementation of Bennett et al. (1954)'s S-measure for calculating a
 * chance-corrected inter-rater agreement for two raters. The measure 
 * assumes a uniform probability distribution for all raters and annotation
 * items.<br><br>
 * References:<ul>
 * <li>Bennett, E. M.; Alpert, R. & Goldstein, A. C.: Communications 
 *   through limited response questioning. Public Opinion 
 *   Quarterly 18(3):303-308, Princeton, NJ: Princeton University, 1954.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class TwoRaterSAgreement extends TwoRaterObservedAgreement {
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public TwoRaterSAgreement(final IAnnotationStudy study) {
		super(study);
	}

	@Override
	public double calculateAgreement() {
		double AO = calculateObservedAgreement();
		double AE = calculateExpectedAgreement();
		return (AO - AE) / (1.0 - AE);
	}
	
	/** Calculates the observed inter-rater agreement as defined by 
	 *  {@link TwoRaterObservedAgreement#calculateAgreement()}. */
	public double calculateObservedAgreement() {
		return super.calculateAgreement();
	}

	/** Calculates the expected inter-rater agreement that assumes a 
	 *  uniform distribution over all raters and annotations. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no annotation categories. */
	public double calculateExpectedAgreement() {
		return 1.0 / (double) study.getCategoryCount();
	}
	
}
