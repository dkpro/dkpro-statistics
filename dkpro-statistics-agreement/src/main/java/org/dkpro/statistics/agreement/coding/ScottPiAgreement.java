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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

import org.dkpro.statistics.agreement.IChanceCorrectedAgreement;

/**
 * Implementation of Scott's pi (1955) for calculating a chance-corrected
 * inter-rater agreement for two raters. The measure assumes the same
 * probability distribution for all raters.<br><br>
 * References:<ul>
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li>
 * <li>Scott, W.A.: Reliability of content analysis: The case of nominal
 *   scale coding. Public Opinion Quaterly 19(3):321-325, 1955.</li></ul>
 * @author Christian M. Meyer
 */
public class ScottPiAgreement extends CodingAgreementMeasure
		implements IChanceCorrectedAgreement {

	/** Initializes the instance for the given annotation study. The study
	 *  may never be null. */
	public ScottPiAgreement(final ICodingAnnotationStudy study) {
		super(study);
		ensureTwoRaters();
		warnIfMissingValues();
	}

	/** Calculates the expected inter-rater agreement that assumes the same
	 *  distribution for all raters and annotations.
	 *  @throws NullPointerException if the annotation study is null.
	 *  @throws ArithmeticException if there are no items in the
	 *  	annotation study. */
	@Override
	public double calculateExpectedAgreement() {
		Map<Object, Integer> annotationsPerCategory
				= CodingAnnotationStudy.countTotalAnnotationsPerCategory(study);

		BigDecimal result = new BigDecimal(0);
		for (Object category : study.getCategories()) {
			Integer catCount = annotationsPerCategory.get(category);
			if (catCount != null)
				result = result.add(new BigDecimal(catCount).pow(2));
		}
		result = result.divide(new BigDecimal(4).multiply(
				new BigDecimal(study.getItemCount()).pow(2)), MathContext.DECIMAL128);
		return result.doubleValue();
	}

}
