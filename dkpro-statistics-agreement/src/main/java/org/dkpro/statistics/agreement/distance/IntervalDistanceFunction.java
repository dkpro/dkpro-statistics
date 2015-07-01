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
 * distance between interval-scaled categories. That is to say, categories 
 * represent a numerical value whose difference can be measured. A typical
 * example is date as measures, for instance, using the Gregorian calendar.
 * We are able to measure the difference between the years 2010 and 2000
 * and to say that the difference equals the difference between he years
 * 1980 and 1970. However, we cannot say that 2000 is twice the year 1000,
 * since there is no absolute zero (note that the year 0 is arbitrarily 
 * defined and that we need to cater for years such as 200 BCE). For
 * natural language processing tasks, judging the similarity between two 
 * entities on a linear scale of, say, 1 to 6 is another example for 
 * interval-scaled data. Mathematically, the interval scale only allows for 
 * the equality, comparison, and addition operations, but prohibits 
 * multiplication. The distance function returns the squared difference 
 * between the numerical representation of two categories. It thus assumes 
 * the categories to be integers or doubles and falls back to a 
 * {@link NominalDistanceFunction} for other data types.<br><br> 
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public class IntervalDistanceFunction implements IDistanceFunction {
	
	@Override
	public double measureDistance(final IAnnotationStudy study, 
			final Object category1, final Object category2) {
		if (category1 instanceof Integer && category2 instanceof Integer)
			return (((Integer) category1) - ((Integer) category2))
					* (((Integer) category1) - ((Integer) category2));
		
		if (category1 instanceof Double && category2 instanceof Double)
			return (((Double) category1) - ((Double) category2))
					* (((Double) category1) - ((Double) category2));
		
		return (category1.equals(category2) ? 0.0 : 1.0);
	}
	
}
