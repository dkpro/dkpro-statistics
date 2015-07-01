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

import org.dkpro.statistics.agreement.IAnnotationUnit;
import org.dkpro.statistics.agreement.ICategorySpecificAgreement;
import org.dkpro.statistics.agreement.IChanceCorrectedAgreement;

/**
 * Implementation of Cohen's kappa (1960) for calculating a chance-corrected 
 * inter-rater agreement for two raters. The measure assumes a different 
 * probability distribution for all raters.<br><br>
 * References:<ul>
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. 
 *   Educational and Psychological Measurement 20(1):37-46, 1960.</li></ul>
 * @author Christian M. Meyer
 */
public class CohenKappaAgreement extends CodingAgreementMeasure
		implements IChanceCorrectedAgreement, ICategorySpecificAgreement {

	/** Initializes the instance for the given annotation study. The study 
	 *  may never be null. */
	public CohenKappaAgreement(final ICodingAnnotationStudy study) {
		super(study);
		ensureTwoRaters();
		warnIfMissingValues();
	}

	/** Calculates the expected inter-rater agreement that assumes a 
	 *  different probability distribution for all raters. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items in the 
	 *  	annotation study. */
	@Override
	public double calculateExpectedAgreement() {
		Map<Object, int[]> annotationsPerCategory 
				= CodingAnnotationStudy.countAnnotationsPerCategory(study);
		
		BigDecimal result = new BigDecimal(0);
		for (Object category : study.getCategories()) {
			int[] annotations = annotationsPerCategory.get(category);
			BigDecimal prod = new BigDecimal(1);
			for (int rater = 0; rater < study.getRaterCount(); rater++)
				prod = prod.multiply(new BigDecimal(annotations[rater]));
			result = result.add(prod);
		}
		result = result.divide(new BigDecimal(study.getItemCount()).pow(2), MathContext.DECIMAL128);
		return result.doubleValue();
	}
	
	protected double calculateMaximumObservedAgreement() {
		Map<Object, int[]> annotationsPerCategory 
				= CodingAnnotationStudy.countAnnotationsPerCategory(study);

		BigDecimal result = new BigDecimal(0);
		for (Object category : study.getCategories()) {
			int[] annotations = annotationsPerCategory.get(category);
			int min = -1;
			for (int rater = 0; rater < study.getRaterCount(); rater++)
				if (annotations[rater] < min || min < 0)
					min = annotations[rater];
			if (min > 0)
				result = result.add(new BigDecimal(min));
		}
		result = result.divide(new BigDecimal(study.getItemCount()), MathContext.DECIMAL128);
		return result.doubleValue();
	}

	/** Computes the maximum possible value of the kappa coefficient for the 
	 *  provided study. In case of balanced off-marginals (i.e., an equal
	 *  disagreement for each pair of categories), the maximum kappa is 1.
	 *  In other cases, it decreases with a higher discrepancy of the 
	 *  distribution of disagreements.  */
	public double calculateMaximumAgreement() {
		double A_O = calculateMaximumObservedAgreement();
		double A_E = calculateExpectedAgreement();
		if (A_E == 0.0)
			return A_O;
		else
			return (A_O - A_E) / (1.0 - A_E);
	}
	
	/* Calculates the inter-rater agreement for the given annotation category
	 *  based on the object's annotation study that has been passed to the 
	 *  class constructor.
	 *  @throws NullPointerException if the study is null or the given
	 *  	category is null.
	 *  @throws ArrayIndexOutOfBoundsException if the study does not contain 
	 *  	the given category.
	 *  @throws ArithmeticException if the study does not
	 *  	contain annotations for the given category. */
	// Attention: This follows Agresti (1992)!
	// Artstein&Poesio have a different definition!
	@Override
	public double calculateCategoryAgreement(final Object category) {
		// N = # subjects = #items -> index i
		// n = # ratings/subject = #raters
		// k = # categories -> index j
		// n_ij = # raters that annotated item i as category j
		// 		
		// k_j = (P_j - p_j) / (1 - p_j)
		// P_j = (sum( n_ij^2 ) - N n p_j) / (N n (n-1) p_j )
		// p_j = 1/Nn sum n_ij  

		int N = study.getItemCount();
		int n = study.getRaterCount();
		int sum_nij = 0;
		int sum_nij_2 = 0;
		for (ICodingAnnotationItem item : study.getItems()) {
			int nij = 0;
			for (IAnnotationUnit unit : item.getUnits())
				if (unit.getCategory().equals(category))
					nij++;
			sum_nij += nij;
			sum_nij_2 += (nij * nij);
		}
			
		double pj = 1 / (double) (N * n) * sum_nij;
		double Pj = (sum_nij_2 - N * n * pj) / (double) (N * n * (n - 1) * pj);
		double kappaj = (Pj - pj) / (double) (1 - pj);
		return kappaj;
	}
	
}
