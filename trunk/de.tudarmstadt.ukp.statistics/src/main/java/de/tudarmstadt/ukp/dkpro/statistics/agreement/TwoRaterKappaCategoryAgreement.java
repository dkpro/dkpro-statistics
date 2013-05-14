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


/**
 * Modified version of Cohen's kappa for measuring a chance-corrected
 * inter-rater agreement for a certain category. A prerequisite of this 
 * measure is an annotation study that involves at least three categories. 
 * For studies with only two categories, the measure is equivalent to 
 * the general kappa. This implementation is intended for studies with two 
 * raters, see {@see MultiRaterPiCategoryAgreement} for an implementation 
 * involving multiple raters.
 * <br><br>
 * References:<ul>
 * <li>Fleiss, J. L.: Measuring nominal scale agreement among many raters.
 *   Psychological Bulletin 76(5):378-381, 
 *   Washington, DC: American Psychological Association, 1971.
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 
 *   Cambridge, MA: The MIT Press, 2008.</ul>
 * @author Christian M. Meyer
 * @date 04.03.2011
 */
public class TwoRaterKappaCategoryAgreement extends MultiRaterPiCategoryAgreement {

	protected IAnnotationStudy study;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public TwoRaterKappaCategoryAgreement(final IAnnotationStudy study) {
		super(study);
	}
	
	// Same calculation for multiple raters!
	
}
