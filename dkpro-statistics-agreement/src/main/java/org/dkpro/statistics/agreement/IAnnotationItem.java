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
package org.dkpro.statistics.agreement;

/**
 * Represents a single annotation item of an {@link IAnnotationStudy}. Note that the definition of
 * an annotation item depends on the annotation setup: In coding tasks, annotation items are fixed,
 * and each rater is asked to code each item. The category assigned by a certain rater is
 * represented as annotation units. Thus, an annotation item of a coding study consists of multiple
 * annotation units. In unitizing studies, there is no fixed definition of an item. The items
 * roughly correspond to the smallest possible segments within the continuum of the study.
 * Theoretically, an item would thus allow accessing the identified annotation units at a specific
 * position of the continuum. For efficiency reasons, this is, however, not explicitly modeled.
 * 
 * @see IAnnotationUnit
 * @see IAnnotationStudy
 * @author Christian M. Meyer
 */
public interface IAnnotationItem
{

}
