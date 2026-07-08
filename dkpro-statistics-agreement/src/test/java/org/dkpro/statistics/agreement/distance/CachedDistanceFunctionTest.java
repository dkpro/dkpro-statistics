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

import static org.assertj.core.api.Assertions.assertThat;

import org.dkpro.statistics.agreement.IAnnotationStudy;
import org.junit.jupiter.api.Test;

public class CachedDistanceFunctionTest
{
    /** Records how often the wrapped function is actually consulted. */
    private static final class CountingDistanceFunction
        implements IDistanceFunction
    {
        private final IDistanceFunction delegate = new NominalDistanceFunction();
        private int invocations = 0;

        @Override
        public double measureDistance(IAnnotationStudy study, Object category1, Object category2)
        {
            invocations++;
            return delegate.measureDistance(study, category1, category2);
        }
    }

    @Test
    public void delegatesToTheWrappedFunction()
    {
        CachedDistanceFunction sut = new CachedDistanceFunction(new NominalDistanceFunction());

        assertThat(sut.measureDistance(null, "A", "A")).isEqualTo(0.0);
        assertThat(sut.measureDistance(null, "A", "B")).isEqualTo(1.0);
    }

    @Test
    public void repeatedCallsAreServedFromTheCache()
    {
        CountingDistanceFunction wrappee = new CountingDistanceFunction();
        CachedDistanceFunction sut = new CachedDistanceFunction(wrappee);

        assertThat(sut.measureDistance(null, "A", "B")).isEqualTo(1.0);
        assertThat(sut.measureDistance(null, "A", "B")).isEqualTo(1.0);

        assertThat(wrappee.invocations).isEqualTo(1);
    }

    @Test
    public void distinctCategoriesWithEqualHashCodesDoNotCollide()
    {
        CachedDistanceFunction sut = new CachedDistanceFunction(new NominalDistanceFunction());

        // "Aa" and "BB" are distinct but share the same String hashCode (2112).
        assertThat("Aa".hashCode()).isEqualTo("BB".hashCode());

        assertThat(sut.measureDistance(null, "Aa", "Aa")).isEqualTo(0.0);
        assertThat(sut.measureDistance(null, "Aa", "BB")).isEqualTo(1.0);
    }

    @Test
    public void nullCategoriesAreCachedRatherThanThrowing()
    {
        CountingDistanceFunction wrappee = new CountingDistanceFunction();
        CachedDistanceFunction sut = new CachedDistanceFunction(wrappee);

        assertThat(sut.measureDistance(null, null, null)).isEqualTo(0.0);
        assertThat(sut.measureDistance(null, null, null)).isEqualTo(0.0);
        assertThat(sut.measureDistance(null, null, "A")).isEqualTo(1.0);
        assertThat(sut.measureDistance(null, "A", null)).isEqualTo(1.0);

        // (null, null) served once from cache; (null, "A") and ("A", null) are distinct keys.
        assertThat(wrappee.invocations).isEqualTo(3);
    }
}
