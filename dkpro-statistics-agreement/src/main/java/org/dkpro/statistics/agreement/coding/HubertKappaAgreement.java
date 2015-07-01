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

/**
 * <li>Conger, A.J.: Integration and generalization of kappas for multiple
 *   raters. Psychological Bulletin 88(2):322-328, 1980.</li>
 * <li>Davies, M. &amp; Fleiss, J.L.: Measuring agreement for multinomial data.
 *   Biometrics 38(4):1047–1051, 1982.</li>
 * <li>Hubert, L.: Kappa revisited. Psychological Bulletin
 *   84(2):289-297, 1977.</li>
 * @author Christian M. Meyer
 */
//TODO: Check Popping (1983) and Heuvelmans and Sanders (1993).
public class HubertKappaAgreement extends CodingAgreementMeasure {

	/** Initializes the instance for the given annotation study. The study
	 *  should never be null. */
	public HubertKappaAgreement(final ICodingAnnotationStudy study) {
		super(study);
		warnIfMissingValues();
	}

	/** Calculates the expected inter-rater agreement that assumes the same
	 *  distribution for all raters and annotations.
	 *  @throws NullPointerException if the annotation study is null.
	 *  @throws ArithmeticException if there are no items in the
	 *  	annotation study. */
	@Override
	public double calculateExpectedAgreement() {
		Map<Object, int[]> annotationsPerCategory
				= CodingAnnotationStudy.countAnnotationsPerCategory(study);
		BigDecimal result = new BigDecimal(0);
		for (Object category : study.getCategories()) {
			int[] annotationCounts = annotationsPerCategory.get(category);
			for (int m = 0; m < study.getRaterCount(); m++)
				for (int n = m + 1; n < study.getRaterCount(); n++)
					result = result.add(new BigDecimal(annotationCounts[m])
							.multiply(new BigDecimal(annotationCounts[n])));
		}
		result = result.multiply(new BigDecimal(2));
		result = result.divide(new BigDecimal(study.getRaterCount())
				.multiply(new BigDecimal(study.getRaterCount() - 1))
				.multiply(new BigDecimal(study.getItemCount()).pow(2)),
				MathContext.DECIMAL128);
		return result.doubleValue();
	}

}
