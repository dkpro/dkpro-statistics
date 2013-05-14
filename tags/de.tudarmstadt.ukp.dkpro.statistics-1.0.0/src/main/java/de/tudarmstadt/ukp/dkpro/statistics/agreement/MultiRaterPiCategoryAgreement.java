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
public class MultiRaterPiCategoryAgreement {

	protected IAnnotationStudy study;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public MultiRaterPiCategoryAgreement(final IAnnotationStudy study) {
		this.study = study;
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
	public double calculateAgreement(final Object category) {
		// N = # subjects = #items -> index i
		// n = # ratings/subject = #raters
		// k = # categories -> index j
		// n_ij = # raters that annotated item i as category j
		// 		
		// k_j = (P_j - p_j) / (1 - p_j)
		// P_j = (sum( n_ij^2 ) - N n p_j) / (N n (n-1) p_j )
		// p_j = 1/Nn sum n_ij  

		int N = study.getItemCount();
		int n = study.getAnnotatorCount();
		int sum_nij = 0;
		int sum_nij_2 = 0;
		for (IAnnotationItem item : study.getItems()) {
			int nij = 0;
			for (int r = 0; r < n; r++) 
				if (item.getAnnotation(r).equals(category))
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
