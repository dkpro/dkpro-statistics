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

import java.util.TreeMap;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy.IAnnotationItem;

/**
 * Generalization of Scott's (1955) pi-measure for calculating a
 * chance-corrected inter-rater agreement for multiple raters, which is known
 * as Fleiss's (1971) kappa and Carletta's (1996) K. The basic idea is to 
 * average over all pairwise agreements. The measure assumes the same 
 * probability distribution for all raters.<br><br>
 * References:<ul>
 * <li>Fleiss, J. L.: Measuring nominal scale agreement among many raters.
 *   Psychological Bulletin 76(5):378-381, 
 *   Washington, DC: American Psychological Association, 1971.
 * <li>Carletta, J.: Assessing agreement on classification tasks: The 
 *   kappa statistic. Computational Linguistics 22(2):249-254, 
 *   Cambridge, MA: The MIT Press, 1996.
 * <li>Scott, W. A.: Reliability of content analysis: The case of nominal 
 *   scale coding Public Opinion Quaterly 19(3):321-325, 
 *   Princeton, NJ: Princeton University, 1955.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class MultiRaterPiAgreement extends MultiRaterObservedAgreement {
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public MultiRaterPiAgreement(final IAnnotationStudy study) {
		super(study);
	}

	@Override
	public double calculateAgreement() {
		double AO = calculateObservedAgreement();
		double AE = calculateExpectedAgreement();
		return (AO - AE) / (1.0 - AE);
	}
	
	/** Calculates the observed inter-rater agreement as defined by 
	 *  {@link TwoRaterObservedAgreement#calculateAgreement()}. */
	public double calculateObservedAgreement() {
		return super.calculateAgreement();
	}

	/** Calculates the expected inter-rater agreement that assumes the same
	 *  distribution for all raters and annotations. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items in the 
	 *  	annotation study. */
	public double calculateExpectedAgreement() {
		TreeMap<Object, Integer> annotationsPerCategory = new TreeMap<Object, Integer>();
		for (IAnnotationItem item : study.getItems())
			for (Object annotation : item.getAnnotations()) {
				Integer count = annotationsPerCategory.get(annotation);
				if (count == null)
					annotationsPerCategory.put(annotation, 1);
				else
					annotationsPerCategory.put(annotation, count + 1);
			}
			
		double result = 0;
		for (Object category : study.getCategories()) {
			int count = annotationsPerCategory.get(category);
			result += count * count;
		}
		double den = study.getAnnotatorCount() * study.getItemCount();
		result /= (den * den);
		return result;
	}
	
}
