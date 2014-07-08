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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.distance;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Implementation of the {@link IDistanceFunction} interface for scoring the 
 * distance between nominal-scaled categories. That is to say, categories 
 * are either equal (distance 0) or unequal (distance 1). A typical example 
 * for nominal-scaled categories are the part of speech tags NOUN, VERB, and
 * ADJECTIVE. Mathematically, the nominal scale only allows for the equality
 * operation, but prohibits comparison, addition, and multiplication. The 
 * distance function makes no assumption regarding the data type and thus 
 * allows for strings, enums, integers, etc.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see IDistanceFunction
 * @author Kostadin Cholakov
 * @author Christian M. Meyer
 */
public class NominalDistanceFunction implements IDistanceFunction {

	@Override
	public double measureDistance(final IAnnotationStudy study, 
			final Object category1, final Object category2) {
		return (category1.equals(category2) ? 0.0 : 1.0);
	}

}
