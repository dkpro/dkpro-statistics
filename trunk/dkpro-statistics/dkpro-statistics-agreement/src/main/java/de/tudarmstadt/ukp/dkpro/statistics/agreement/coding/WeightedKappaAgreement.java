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

import java.util.Map;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IChanceCorrectedDisagreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Generalization of Cohen's (1960) kappa-measure for calculating a
 * chance-corrected inter-rater agreement for multiple raters with arbitrary
 * distance/weighting functions. Before an inter-rater agreement can be 
 * calculated, an {@link IDistanceFunction} instance needs to 
 * be assigned.<br><br>
 * References:<ul>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. 
 *   Educational and Psychological Measurement 20(1):37-46, 
 *   Beverly Hills, CA: Sage Publications, 1960.
 * <li>Cohen, J.: Weighted kappa: Nominal scale agreement with provision 
 *   for scaled disagreement or partial credit. Psychological Bulletin 
 *   70(4):213-220, Washington, DC: American Psychological Association, 1968.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class WeightedKappaAgreement extends WeightedAgreement
		implements IChanceCorrectedDisagreement {

	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public WeightedKappaAgreement(final ICodingAnnotationStudy study,
			final IDistanceFunction distanceFunction) {
		super(study);
		this.distanceFunction = distanceFunction;
	}	

	/** Calculates the expected inter-rater agreement using the defined 
	 *  distance function to infer the assumed probability distribution. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items or raters in the 
	 *  	annotation study. */
	public double calculateExpectedDisagreement() {
		ensureDistanceFunction();
		
		double result = 0.0;
		Map<Object, Integer> annotationsPerCategory 
				= CodingAnnotationStudy.countTotalAnnotationsPerCategory(study);
		
		for (Object category1 : study.getCategories())
			for (Object category2 : study.getCategories())
				result += annotationsPerCategory.get(category1)
						* annotationsPerCategory.get(category2)
						* distanceFunction.measureDistance(study, category1, category2);
			
		result /= (double) (study.getItemCount() * study.getRaterCount() 
				* (study.getItemCount() * study.getRaterCount() - 1));
		return result;
	}
	
}
