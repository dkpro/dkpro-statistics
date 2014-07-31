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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.unitizing;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationUnit;

/**
 * Extension of the {@link IAnnotationUnit} interface for representing the
 * annotation units of {@link IUnitizingAnnotationStudy}s. That is, an 
 * annotation unit that models the position of the unit within the 
 * continuum of an annotation study and the category assigned to this
 * unit by a certain rater. Implement this interface when measuring
 * inter-rater agreement using a {@link IUnitizingAgreementMeasure}.
 * @see IAnnotationUnit
 * @see IUnitizingAgreementMeasure
 * @see IUnitizingAnnotationStudy
 * @author Christian M. Meyer
 */
public interface IUnitizingAnnotationUnit extends IAnnotationUnit, 
		Comparable<IUnitizingAnnotationUnit> {

	/** Returns the offset of the annotation unit (i.e., the start position 
	 *  of the identified segment). */
	public long getOffset();
	
	/** Returns the length of the annotation unit (i.e., the difference between
	 *  the end and start position of the identified segment). */
	public long getLength();

	/** Returns the right delimiter of the annotation unit (i.e., the end 
	 *  position of the identified segment). The method is a shorthand for
	 *  {@link #getOffset()} + {@link #getLength()}. */
	public long getEndOffset();

}
