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
package org.dkpro.statistics.agreement;

/**
 * Generic interface to be implemented by all inter-rater agreement measures
 * that perform a chance correction and thus distinguish between observed
 * agreement and expected agreement. The basic idea is that if raters made
 * a random decision on each annotation unit, then the final inter-rater
 * agreement should be zero. See also {@link IChanceCorrectedDisagreement}
 * for the analogous definition of chance corrected measures with observed
 * and expected disagreement.<br><br>
 * References:<ul>
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li></ul>
 * @see IAgreementMeasure
 * @see AgreementMeasure
 * @see IChanceCorrectedDisagreement
 * @author Christian M. Meyer
 */
public interface IChanceCorrectedAgreement extends IAgreementMeasure {

	/** Returns the observed agreement of an annotation study. The observed
	 *  agreement is basically the proportion of annotation units that the
	 *  raters agree on divided by the number of units in the given study. */
	public double calculateObservedAgreement();

	/** Returns the expected agreement of an annotation study. The expected
	 *  agreement is the proportion of agreement that would be expected by
	 *  chance alone. The expected agreement should be equal to the observed
	 *  agreement if each rater makes a random decision for each unit. */
	public double calculateExpectedAgreement();

}
