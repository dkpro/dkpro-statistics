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
package org.dkpro.statistics.agreement.coding;

import org.dkpro.statistics.agreement.IAnnotationItem;
import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Data model for coding studies. Coding studies are characterized by
 * a set of fixed annotation items which a number of (human) raters are 
 * asked code with a certain category. The individual annotations represent
 * the annotation units. The set of annotation units of an annotation
 * item is represented by the {@link IAnnotationItem} interface. Note that
 * this interface allows reusing existing data representations by implementing
 * the required methods. Use {@link CodingAnnotationStudy} as a default
 * implementation if no existing data representation exists. The default
 * implementation facilitates adding the individual units.
 * @see CodingAnnotationStudy
 * @see ICodingAnnotationItem
 * @author Christian M. Meyer
 */
public interface ICodingAnnotationStudy extends IAnnotationStudy {

    // -- Categories --

    /** Returns true if, and only if, the annotation study contains at least
     *  one item with a missing value (i.e., an annotation item containing 
     *  an annotation unit with category null). */
    public boolean hasMissingValues();
    
    // -- Items --

    /** Returns the annotation item with the given index. The first 
     *  item has index 0. */
    public ICodingAnnotationItem getItem(int index);
    
    /** Allows iterating all annotation items of this study. */
    public Iterable<ICodingAnnotationItem> getItems();

    /** Returns the number of annotation items defined by the study. */
    public int getItemCount();
        
    // -- Units --

    /** Returns the number of annotation units defined by the study. That is, 
     *  the number of annotations coded by the raters. If there are no 
     *  missing values, the unit count equals the item count multiplied 
     *  with the number of raters. */
    public int getUnitCount();

}
