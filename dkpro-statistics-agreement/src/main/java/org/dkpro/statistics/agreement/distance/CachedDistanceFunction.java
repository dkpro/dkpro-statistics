/*
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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Wrapper class for arbitrary distance functions that caches the distance scores between two
 * categories in a hash table. This is useful, if the calculation of a distance value is
 * computationally complex, for instance, for set-valued categories as assumed by the
 * {@link SetAnnotationDistanceFunction}.<br>
 * <br>
 * <b>Important:</b> the cache is keyed solely on the pair of categories; the annotation study is
 * <i>not</i> part of the key. A single instance must therefore not be reused across different
 * studies when the wrapped distance function is study-dependent (i.e., returns different distances
 * for the same category pair depending on the study, as {@link OrdinalDistanceFunction} does).
 * Doing so would serve the first study's cached distances for subsequent studies and silently
 * produce wrong agreement scores. Create a fresh instance per study, or only cache
 * study-independent distance functions (e.g., {@link NominalDistanceFunction},
 * {@link SetAnnotationDistanceFunction}).
 *
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public class CachedDistanceFunction
    implements IDistanceFunction
{
    protected Map<List<Object>, Double> cache;
    protected IDistanceFunction wrappee;

    /** Instantiates the wrapper for the given distance function. */
    public CachedDistanceFunction(final IDistanceFunction wrappee)
    {
        this.wrappee = wrappee;
        cache = new Hashtable<List<Object>, Double>();
    }

    @Override
    public double measureDistance(final IAnnotationStudy study, final Object category1,
            final Object category2)
    {
        List<Object> key = Arrays.asList(category1, category2);
        Double result = cache.get(key);
        if (result == null) {
            result = wrappee.measureDistance(study, category1, category2);
            cache.put(key, result);
        }
        return result;
    }
}
