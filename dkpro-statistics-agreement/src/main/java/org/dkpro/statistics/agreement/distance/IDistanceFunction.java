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
package org.dkpro.statistics.agreement.distance;

import org.dkpro.statistics.agreement.IAnnotationStudy;
import org.dkpro.statistics.agreement.IWeightedAgreement;
import org.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;
import org.dkpro.statistics.agreement.coding.WeightedKappaAgreement;

/**
 * Interface for a distance function to be used in any {@link IWeightedAgreement} measure, such as
 * {@link WeightedKappaAgreement} or {@link KrippendorffAlphaAgreement}. These measures use the
 * distance function for quantifying the degree of disagreement between a pair of categories (i.e.,
 * two annotations assigned to a certain item by different raters). If annotation units are, for
 * instance, coded using integers, the distance function might assign a smaller distance to the
 * category pair (1, 2) then to the category pair (2, 9). See implementations of this interface for
 * more detailed examples.
 * 
 * @see IWeightedAgreement
 * @see WeightedKappaAgreement
 * @see KrippendorffAlphaAgreement
 * @author Christian M. Meyer
 */
public interface IDistanceFunction
{

    /**
     * Returns a distance value for the given pair of categories used within the given annotation
     * study. Normally, the measure should return a distance of 0 if, and only if, the two
     * categories are equal and a positive number otherwise.
     */
    public double measureDistance(final IAnnotationStudy study, final Object category1,
            final Object category2);

}
