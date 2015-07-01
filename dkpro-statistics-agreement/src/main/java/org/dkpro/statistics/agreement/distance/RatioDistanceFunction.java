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
package org.dkpro.statistics.agreement.distance;

import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Implementation of the {@link IDistanceFunction} interface for scoring the 
 * distance between ratio-scaled categories. That is to say, categories 
 * represent a numerical value whose difference and distance to a zero element
 * can be measured. A typical example is measuring the time needed for a 
 * certain task: it makes a large difference if one accomplishes the task
 * in 1 minute or in 5 minutes, but there is not much difference between
 * requiring 3 full hours and 3 hours plus 5 minutes. Mathematically, the 
 * ratio scale allows for the equality, comparison, addition, and 
 * multiplication operations, which is why probabilities and measuring entropy
 * is an often-used example for ratio scaled values (cf. Resnik&Lin, 2010). 
 * The distance function assumes the categories to be integers or doubles 
 * and falls back to a {@link NominalDistanceFunction} for other data 
 * types.<br><br> 
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li>
 * <li>Resnik, P. & Lin, J.: Evaluation of NLP Systems. In: The Handbook of 
 *   Computational Licensesnguistics and Natural Language Processing, 
 *   p. 271–295, Chichester/Malden: Wiley-Blackwell, 2010.</li></ul>
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public class RatioDistanceFunction implements IDistanceFunction {
	
	@Override
	public double measureDistance(final IAnnotationStudy study, 
			final Object category1, final Object category2) {
		if (category1 instanceof Integer && category2 instanceof Integer
				&& (((Integer) category1) + ((Integer) category2) > 0.0)) {
			double result = (((Integer) category1) - ((Integer) category2))
					/ (double) (((Integer) category1) + ((Integer) category2));
			return result * result;
		}
		
		if (category1 instanceof Double && category2 instanceof Double
				&& (((Double) category1) + ((Double) category2) > 0.0)) {
			double result = (((Double) category1) - ((Double) category2))
					/ (((Double) category1) + ((Double) category2));
			return result * result;
		}
		
		return (category1.equals(category2) ? 0.0 : 1.0);
	}
	
}
