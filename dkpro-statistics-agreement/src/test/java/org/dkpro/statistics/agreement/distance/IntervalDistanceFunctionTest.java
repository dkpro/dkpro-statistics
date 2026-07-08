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

import org.junit.jupiter.api.Test;

public class IntervalDistanceFunctionTest
{
    private final IntervalDistanceFunction sut = new IntervalDistanceFunction();

    @Test
    public void integerDistanceIsSquaredDifference()
    {
        assertThat(sut.measureDistance(null, 1, 4)).isEqualTo(9.0);
        assertThat(sut.measureDistance(null, 3, 3)).isEqualTo(0.0);
    }

    @Test
    public void doubleDistanceIsSquaredDifference()
    {
        assertThat(sut.measureDistance(null, 1.0, 3.0)).isEqualTo(4.0);
    }

    @Test
    public void nonNumericCategoriesFallBackToNominal()
    {
        assertThat(sut.measureDistance(null, "A", "A")).isEqualTo(0.0);
        assertThat(sut.measureDistance(null, "A", "B")).isEqualTo(1.0);
    }

    @Test
    public void nullEqualsNullIsZero()
    {
        assertThat(sut.measureDistance(null, null, null)).isEqualTo(0.0);
    }

    @Test
    public void nullVersusValueIsMaximal()
    {
        assertThat(sut.measureDistance(null, null, "A")).isEqualTo(1.0);
        assertThat(sut.measureDistance(null, "A", null)).isEqualTo(1.0);
    }
}
