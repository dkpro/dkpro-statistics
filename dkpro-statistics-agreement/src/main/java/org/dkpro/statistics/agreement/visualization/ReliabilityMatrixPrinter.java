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

import org.dkpro.statistics.agreement.IAnnotationUnit;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationItem;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;

/**
 * Plain-text visualization for a reliability matrix as defined by 
 * Krippendorff (1980: p. 136). That is, a table showing one annotation
 * item per column and the categories assigned to them by a certain
 * rater. In addition to that, the reliability matrix sums the number 
 * of units with identical categories. For the example by Krippendorff 
 * (1980: p. 139), the implementation displays:<pre>
 *    1  2  3  4  5  6  7  8  9   Σ
 * 1  1  1  2  4  1  2  1  3  2 
 * 2  1  2  2  4  4  2  2  3  2 
 * 3  1  2  2  4  4  2  3  3  2 
 *                              
 * 1  3  1        1     1         6
 * 2     2  3        3  1     3  12
 * 3                    1  3      4
 * 4           3  2               5</pre><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see ICodingAnnotationStudy
 * @author Christian M. Meyer
 */
public class ReliabilityMatrixPrinter {

	/** Print the reliability matrix for the given coding study. */
	public void print(final PrintStream out, final ICodingAnnotationStudy study) {
		//TODO: measure length of cats. maybe cut them.
		Map<Object, Integer> categories = new LinkedHashMap<Object, Integer>();
		for (Object cat : study.getCategories())
			categories.put(cat, categories.size());
		
		final String DIVIDER = "\t";
		
		for (int i = 0; i < study.getItemCount(); i++)
			out.print(DIVIDER + (i + 1));
		out.print(DIVIDER + "Σ");
		out.println();
		
		for (int r = 0; r < study.getRaterCount(); r++) {
			out.print(r + 1);
			for (ICodingAnnotationItem item : study.getItems())
				out.print(DIVIDER + item.getUnit(r).getCategory());
			out.println();
		}
		out.println();
		
		for (Object category : study.getCategories()) {
			out.print(category);
			int catSum = 0;
			for (ICodingAnnotationItem item : study.getItems()) {
				int catCount = 0;
				for (IAnnotationUnit unit : item.getUnits())
					if (category.equals(unit.getCategory()))
						catCount++;
				out.print(DIVIDER + (catCount > 0 ? catCount : ""));
				catSum += catCount;
			}
			out.println(DIVIDER + catSum);
		}
		
/*		
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
		out.println();*/
	}
	
}
