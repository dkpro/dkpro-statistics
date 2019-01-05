/*
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
 */
package org.dkpro.statistics.agreement.coding;

import org.dkpro.statistics.agreement.IAnnotationItem;
import org.dkpro.statistics.agreement.IAnnotationUnit;

/**
 * Represents a single annotation item of an {@link ICodingAnnotationStudy}.
 * In coding tasks, annotation items are fixed, and each rater is
 * asked to code each item. The category assigned by a certain rater is 
 * represented as annotation units. Thus, an annotation item of a coding
 * study consists of multiple annotation units.
 * @see IAnnotationUnit
 * @see ICodingAnnotationStudy
 * @author Christian M. Meyer
 */
public interface ICodingAnnotationItem extends IAnnotationItem {

    /** Returns the annotation unit of the rater with the specified 
     *  index. That is, the object holding the category assigned 
     *  to the item by the specified rater. */
    public IAnnotationUnit getUnit(int raterIdx);
    
    /** Returns all coding units for this annotation item (i.e., 
     *  the categories assigned by the individual raters). */
    public Iterable<IAnnotationUnit> getUnits();

    /** Returns the number of raters who annotated this item with a 
     *  non-null category. */
    public int getRaterCount();
    
}
