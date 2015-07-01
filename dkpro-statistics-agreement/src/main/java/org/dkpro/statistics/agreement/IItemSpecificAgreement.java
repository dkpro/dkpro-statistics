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
package org.dkpro.statistics.agreement;

import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.ICodingItemSpecificAgreement;
import org.dkpro.statistics.agreement.unitizing.IUnitizingAnnotationStudy;

/**
 * A diagnostic device for analyzing the agreement separately for each 
 * annotation item. This is useful, for instance, to identify a certain type 
 * of item that has been very easy or very difficult to annotate. Note that
 * the definition of an annotation item depends on the actual annotation task
 * (i.e., the type of {@link IAnnotationStudy}): Coding tasks use fixed items
 * which are provided for each rater to annotate. In unitizing tasks, the 
 * notion of an annotation item corresponds to a certain segment within
 * the given continuum. This segment may contain none, one, or multiple
 * annotation units by no, one, or multiple raters.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see ICodingAnnotationStudy
 * @see ICodingItemSpecificAgreement
 * @see IUnitizingAnnotationStudy
 * @author Christian M. Meyer
 */
// TODO @see IUnitizingItemSpecificAgreement
public interface IItemSpecificAgreement<ItemType extends IAnnotationItem> {
	
	/** Calculates the inter-rater agreement for the given annotation item.
	 *  @see IItemSpecificAgreement */
	public double calculateItemAgreement(final ItemType item);
	
}
