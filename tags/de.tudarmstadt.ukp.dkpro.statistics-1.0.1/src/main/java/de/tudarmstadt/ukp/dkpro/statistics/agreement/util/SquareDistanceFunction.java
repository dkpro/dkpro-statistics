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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.util;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Simple implementation of the {@link IDistanceFunction} interface that
 * interprets the annotation categories as integer or float values and 
 * returns their squared difference as weighting/distance factor.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class SquareDistanceFunction implements IDistanceFunction {
	
	public double measureDistance(final IAnnotationStudy study, 
			final Object category1, final Object category2) {
		if (category1 instanceof Integer && category2 instanceof Integer)
			return (((Integer) category1) - ((Integer) category2))
					* (((Integer) category1) - ((Integer) category2));
		
		if (category1 instanceof Double && category2 instanceof Double)
			return (((Double) category1) - ((Double) category2))
					* (((Double) category1) - ((Double) category2));
		
		return (category1.equals(category2) ? 0 : 1);
	}
	
}
