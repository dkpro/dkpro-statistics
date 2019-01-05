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

import org.dkpro.statistics.agreement.IItemSpecificAgreement;

/**
 * A diagnostic device for analyzing the agreement separately for each 
 * annotation item. This is useful, for instance, to identify a certain type 
 * of item that has been very easy or very difficult to annotate.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @see ICodingAnnotationStudy
 * @author Christian M. Meyer
 */
public interface ICodingItemSpecificAgreement 
        extends IItemSpecificAgreement<ICodingAnnotationItem> {
    
}
