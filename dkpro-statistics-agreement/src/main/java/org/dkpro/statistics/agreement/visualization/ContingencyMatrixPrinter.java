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
import java.util.LinkedHashMap;
import java.util.Map;

import org.dkpro.statistics.agreement.coding.ICodingAnnotationItem;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;

/**
 * Plain-text visualization for a contingency matrix as defined by 
 * Krippendorff (1980: p. 133). That is, a table showing the frequencies
 * of each combination of categories used by two raters. A contigency table
 * is only defined for coding studies with exactly two raters. For the example 
 * by Krippendorff (1980: p. 133), the implementation displays:<pre>     0   1   Σ
 * 0   5   3   8
 * 1   1   1   2
 * Σ   6   4  10</pre><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see ICodingAnnotationStudy
 * @author Christian M. Meyer
 */
public class ContingencyMatrixPrinter {

	/** Print the contingency matrix for the given coding study. 
	 *  @throws IllegalArgumentException if the given study has more than
	 *    two raters. */
	public void print(final PrintStream out, final ICodingAnnotationStudy study) {
		if (study.getRaterCount() > 2)
			throw new IllegalArgumentException("Contingency tables are only applicable for two rater studies.");
		
		//TODO: measure length of cats. maybe cut them.
		Map<Object, Integer> categories = new LinkedHashMap<Object, Integer>();
		for (Object cat : study.getCategories())
			categories.put(cat, categories.size());
		
		int[][] frequencies = new int[study.getCategoryCount()][study.getCategoryCount()];
		for (ICodingAnnotationItem item : study.getItems()) { 
			int cat1 = categories.get(item.getUnit(0).getCategory());
			int cat2 = categories.get(item.getUnit(1).getCategory());
			frequencies[cat1][cat2]++;
		}
		
		final String DIVIDER = "\t";
		for (Object category : categories.keySet())
			out.print(DIVIDER + category);
		out.print(DIVIDER + "Σ");
		out.println();
		int i = 0;
		int[] colSum = new int[study.getCategoryCount()];
		for (Object category1 : categories.keySet()) {
			out.print(category1);
			int rowSum = 0;
			for (int j = 0; j < categories.size(); j++) {
				out.printf(DIVIDER + "%3d", frequencies[i][j]);
				rowSum += frequencies[i][j];
				colSum[j] += frequencies[i][j];
			}
			out.printf(DIVIDER + "%3d", rowSum);
			out.println();
			i++;
		}
		
		out.print("Σ");
		int rowSum = 0;
		for (int j = 0; j < categories.size(); j++) {
			out.printf(DIVIDER + "%3d", colSum[j]);
			rowSum += colSum[j];
		}
		out.printf(DIVIDER + "%3d", rowSum);
		out.println();
	}
	
}
