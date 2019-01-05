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
 * A diagnostic device for analyzing the agreement separately for each 
 * category. This is useful, for instance, to identify a certain category
 * that is frequently confused with another one and thus yields unreliable
 * annotations.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   Beverly Hills, CA: Sage Publications, 1980.</li></ul>
 * @author Christian M. Meyer
 */
public interface ICategorySpecificAgreement {
    
    /** Calculates the inter-rater agreement for the given category.
     *  @see ICategorySpecificAgreement */
    /*  TODO @throws NullPointerException if the study is null or the given
     *      category is null.
     *  @throws ArrayIndexOutOfBoundsException if the study does not contain 
     *      the given category.
     *  @throws ArithmeticException if the study does not
     *      contain annotations for the given category. */
    public double calculateCategoryAgreement(final Object category);
    
}
