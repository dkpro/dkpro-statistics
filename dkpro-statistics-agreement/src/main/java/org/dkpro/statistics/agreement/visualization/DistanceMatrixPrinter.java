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
package org.dkpro.statistics.agreement.visualization;

import java.io.PrintStream;

import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Plain-text visualization for distance functions showing the distance
 * scores between each pair of categories.
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public class DistanceMatrixPrinter {

	/** Print a matrix of the distances between each pair of 
	 *  categories. */
	public void print(final PrintStream out, final Iterable<Object> categories,
			final IDistanceFunction distanceFunction) {
		doPrint(out, categories, null, distanceFunction);
	}
	
	/** Print a matrix representation of the distances between each pair of 
	 *  categories of the given study. */
	public void print(final PrintStream out, final ICodingAnnotationStudy study,
			final IDistanceFunction distanceFunction) {
		doPrint(out, study.getCategories(), study, distanceFunction);
	}
	
	protected void doPrint(final PrintStream out, final Iterable<Object> categories,
			final ICodingAnnotationStudy study,
			final IDistanceFunction distanceFunction) {
		//TODO: measure length of cats. maybe cut them.
		final String DIVIDER = "\t";
		
		for (Object category : categories)
			out.print(DIVIDER + category);
		out.println();
		
		for (Object category1 : categories) {
			out.print(category1);
			for (Object category2 : categories) 
				out.printf(DIVIDER + "%1.4f", distanceFunction.measureDistance(study, category1, category2));
			out.println();
		}
	}
	
}
