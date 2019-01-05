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
package org.dkpro.statistics.agreement.distance;

import java.util.Hashtable;

import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Wrapper class for arbitrary distance functions that caches the distance
 * scores between two categories in a hash table. This is useful, if the
 * calculation of a distance value is computationally complex, for instance,
 * for set-valued categories as assumed by the
 * {@link SetAnnotationDistanceFunction}.
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public class CachedDistanceFunction implements IDistanceFunction {

    protected Hashtable<String, Double> cache;
    protected IDistanceFunction wrappee;

    /** Instantiates the wrapper for the given distance function. */
    public CachedDistanceFunction(final IDistanceFunction wrappee) {
        this.wrappee = wrappee;
        cache = new Hashtable<String, Double>();
    }

    @Override
    public double measureDistance(final IAnnotationStudy study,
            final Object category1, final Object category2) {
        String key = category1.hashCode() + "|" + category2.hashCode();
        Double result = cache.get(key);
        if (result == null) {
            result = wrappee.measureDistance(study, category1, category2);
            cache.put(key, result);
        }
        return result;
    }

}
