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

import org.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Interface for all weighted inter-rater agreement measures. A weighted 
 * measure makes use of a user-defined {@link IDistanceFunction}, which 
 * is used to score the degree of disagreement between two categories. See
 * Javadoc of  {@link IDistanceFunction} for more information on weighted 
 * agreement measures.
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public interface IWeightedAgreement {

    /** Returns the distance function that is used to measure the distance
     *  between two annotation categories. */
    public IDistanceFunction getDistanceFunction();
    
}
