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
package de.tudarmstadt.ukp.dkpro.statistics.agreement;

/**
 * Represents a single annotation unit of an {@link IAnnotationStudy}
 * identified and coded by a certain rater. This basic interface concentrates
 * on modeling the rater (represented by its running index) and the 
 * category she/he assigned to this unit. Extend this interface to 
 * model additional properties of an unit, such as its content (e.g., the
 * covered text) and the position of the unit within the continuum of 
 * an annotation study.
 * @see IAnnotationStudy   
 * @author Christian M. Meyer
 */
public interface IAnnotationUnit {

	/** Returns the index of the rater who coded this unit (in case of a coding
	 *  study) or defined the boundaries of this unit (in case of a 
	 *  unitizing study). The first rater has index 0.  */
	public int getRaterIdx();

	/** Returns the category assigned to this unit by one of the raters. The
	 *  category might be null if, and only if, the unit represents a missing
	 *  value (in case of a coding study) or a gap (in case of a unitizing 
	 *  study). */
	public Object getCategory();

}
