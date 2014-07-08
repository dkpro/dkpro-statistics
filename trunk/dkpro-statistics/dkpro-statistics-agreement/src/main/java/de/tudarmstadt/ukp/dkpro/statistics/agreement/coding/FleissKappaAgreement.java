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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.coding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationUnit;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.ICategorySpecificAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IChanceCorrectedAgreement;

/**
 * Generalization of Scott's (1955) pi-measure for calculating a
 * chance-corrected inter-rater agreement for multiple raters, which is known
 * as Fleiss' (1971) kappa and Carletta's (1996) K. The basic idea is to 
 * average over all pairwise agreements. The measure assumes the same 
 * probability distribution for all raters.<br><br>
 * References:<ul>
 * <li>Fleiss, J.L.: Measuring nominal scale agreement among many raters.
 *   Psychological Bulletin 76(5):378-381, 1971.</li>
 * <li>Carletta, J.: Assessing agreement on classification tasks: The 
 *   kappa statistic. Computational Linguistics 22(2):249-254, 1996.</li>
 * <li>Scott, W.A.: Reliability of content analysis: The case of nominal 
 *   scale coding. Public Opinion Quaterly 19(3):321-325, 1955.</li>
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li></ul>
 * @author Christian M. Meyer
 */
public class FleissKappaAgreement extends CodingAgreementMeasure
		implements IChanceCorrectedAgreement, ICategorySpecificAgreement {

	/** Initializes the instance for the given annotation study. The study 
	 *  may never be null. */
	public FleissKappaAgreement(final ICodingAnnotationStudy study) {
		super(study);
	}
	
	/** Calculates the expected inter-rater agreement that assumes the same
	 *  distribution for all raters and annotations. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items in the 
	 *  	annotation study. */
	@Override
	public double calculateExpectedAgreement() {
		Map<Object, BigDecimal> categoryProbability = new HashMap<Object, BigDecimal>();
		for (ICodingAnnotationItem item : study.getItems()) {
			Map<Object, Integer> annotationsPerCategory 
					= CodingAnnotationStudy.countTotalAnnotationsPerCategory(item);
			for (Entry<Object, Integer> counts : annotationsPerCategory.entrySet()) {
				BigDecimal p = new BigDecimal(counts.getValue()).divide(
						new BigDecimal(item.getRaterCount()), 
						MathContext.DECIMAL128);
				BigDecimal value = categoryProbability.get(counts.getKey());
				if (value != null)
					p = p.add(value);
				categoryProbability.put(counts.getKey(), p);
			}
		}

		BigDecimal result = new BigDecimal(0.0);
		for (BigDecimal p : categoryProbability.values())
			result = result.add(p.pow(2));
		result = result.divide(
				new BigDecimal(study.getItemCount()).pow(2), 
				MathContext.DECIMAL128);
		return result.doubleValue();
	}
	
	/*
	 * Modified version of Fleiss' kappa for measuring a chance-corrected
	 * inter-rater agreement for a certain category. Note that the generalized
	 * kappa for multiple raters is a generalized pi (see (Artstein/Poesio, 2008)
	 * for details). A prerequisite of this measure is an annotation study 
	 * that involves at least three categories. For studies with only two 
	 * categories, the measure is equivalent to the general kappa.
	 * <br><br>
	 * References:<ul>
	 * <li>Fleiss, J. L.: Measuring nominal scale agreement among many raters.
	 *   Psychological Bulletin 76(5):378-381, 
	 *   Washington, DC: American Psychological Association, 1971.
	 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
	 *   Linguistics. Computational Linguistics 34(4):555-596, 
	 *   Cambridge, MA: The MIT Press, 2008.</ul>
	 * @author Christian M. Meyer
	 * @date 04.03.2011
	 */
	/* Calculates the inter-rater agreement for the given annotation category
	 *  based on the object's annotation study that has been passed to the 
	 *  class constructor.
	 *  @throws NullPointerException if the study is null or the given
	 *  	category is null.
	 *  @throws ArrayIndexOutOfBoundsException if the study does not contain 
	 *  	the given category.
	 *  @throws ArithmeticException if the study does not
	 *  	contain annotations for the given category. */
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
			for (IAnnotationUnit annotation : item.getUnits())
				if (annotation.getCategory().equals(category))
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
