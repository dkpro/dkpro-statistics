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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.visualization;

import java.io.PrintStream;
import java.util.Map;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.ICodingAnnotationItem;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;

/**
 * Plain-text visualization for a coincidence matrix as defined by 
 * Krippendorff (1980: p. 140). That is, a symmetrical table of 
 * co-occurrences -- each element of the table denotes the number of 
 * pairs X_{b,c} (i.e., the number of times an item has been annotated
 * as category b and as category c by two different raters). For the example by Krippendorff 
 * (1980: p. 139), the implementation displays:<pre>     1   2   3   4   Σ
 * 1   6   3   1   2  12
 * 2   3  20   1      24
 * 3   1   1   6       8
 * 4   2           8  10
 * Σ  12  24   8  10  54</pre><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see ICodingAnnotationStudy
 * @author Christian M. Meyer
 */
public class CoincidenceMatrixPrinter {

	/** Print the coincidence matrix for the given annotation item. */
	public void print(final PrintStream out, final ICodingAnnotationStudy study,
			final ICodingAnnotationItem item) {
		Map<Object, Map<Object, Double>> coincidence = 
				CodingAnnotationStudy.countCategoryCoincidence(item);
		doPrint(out, study, coincidence);
	}
	
	/** Print the coincidence matrix for the given coding study. */
	public void print(final PrintStream out, final ICodingAnnotationStudy study) {
		Map<Object, Map<Object, Double>> coincidence = 
				CodingAnnotationStudy.countCategoryCoincidence(study);
		doPrint(out, study, coincidence);
	}
	
	protected void doPrint(final PrintStream out,
			final ICodingAnnotationStudy study,
			final Map<Object, Map<Object, Double>> coincidence) {
		//TODO: measure length of cats. maybe cut them.
		final String DIVIDER = "\t";
		final int LENGTH = 10;
		final String DOUBLE_FORMAT = "%" + LENGTH + ".3f";
		final String STRING_FORMAT = "%" + LENGTH + "s";
		
		out.printf(STRING_FORMAT, "");
		for (Object category : study.getCategories())
			out.printf(DIVIDER + STRING_FORMAT, category);
		out.printf(DIVIDER + STRING_FORMAT, "Σ");
		out.println();
				
		int idx = 0;
		double[] sums = new double[study.getCategoryCount()]; 
		for (Object category1 : study.getCategories()) {
			Map<Object, Double> c1 = coincidence.get(category1);
			out.printf(STRING_FORMAT, category1);
			double sum = 0;
			for (Object category2 : study.getCategories()) {
				Double value = 0.0;
				if (c1 != null)
					value = c1.get(category2);
				if (value != null)
					value *= 2.0;
				
				if (value != null && value > 0) {
					out.printf(DIVIDER + DOUBLE_FORMAT, value);
					sum += value;
				} else
					out.printf(DIVIDER + STRING_FORMAT, "");
			}
			out.printf(DIVIDER + DOUBLE_FORMAT, sum);
			sums[idx] = sum;
			idx++;
			out.println();
		}
		
		double sum = 0;
		out.printf(STRING_FORMAT, "Σ");
		for (int i = 0; i < sums.length; i++) {
			out.printf(DIVIDER + DOUBLE_FORMAT, sums[i]);
			sum += sums[i];
		}
		out.printf(DIVIDER + DOUBLE_FORMAT, sum);
		out.println();
	}
/*
	public void printPercentage(final PrintStream out, final IItemAnnotationStudy study) {
		//TODO: measure length of cats. maybe cut them.
		Map<Object, Map<Object, Integer>> coincidence = 
				AgreementUtils.countCategoryCoincidence(study);
		
		final String DIVIDER = "\t";
		
		double normalizer = 0.0;
		for (Object category1 : study.getCategories()) {
			Map<Object, Integer> c1 = coincidence.get(category1);
			for (Object category2 : study.getCategories())
				if (c1 != null && c1.get(category2) != null)
					normalizer += c1.get(category2);
		}
		
		for (Object category : study.getCategories())
			out.print(DIVIDER + category);
		out.print(DIVIDER + "Σ");
		out.println();
				
		int idx = 0;
		double[] sums = new double[study.getCategoryCount()]; 
		for (Object category1 : study.getCategories()) {
			Map<Object, Integer> c1 = coincidence.get(category1);
			out.print(category1);
			double sum = 0;
			for (Object category2 : study.getCategories()) {
				Double value = 0.0;
				if (c1 != null && c1.get(category2) != null)
					value = new Double(c1.get(category2));
				value /= normalizer;
				//value *= study.getItemCount() * study.getRaterCount();
				
				if (value != null && value > 0.0) {
					out.printf(DIVIDER + "%1.3f", value);
					sum += value;
				} else
					out.print(DIVIDER + "     ");
			}
			out.printf(DIVIDER + "%1.3f", sum);
			sums[idx] = sum;
			idx++;
			out.println();
		}
		
		double sum = 0;
		out.print("Σ");
		for (int i = 0; i < sums.length; i++) {
			out.printf(DIVIDER + "%1.3f", sums[i]);
			sum += sums[i];
		}
		out.printf(DIVIDER + "%1.3f", sum);
		out.println();
	}
	*/
}
