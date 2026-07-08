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
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.Test;

public class RatioDistanceFunctionTest
{
    private final RatioDistanceFunction sut = new RatioDistanceFunction();

    @Test
    public void integerDistanceIsSquaredRelativeDifference()
    {
        // ((1 - 3) / (1 + 3))^2 = (-0.5)^2 = 0.25
        assertThat(sut.measureDistance(null, 1, 3)).isCloseTo(0.25, offset(0.0001));
    }

    @Test
    public void largeIntegerDistanceDoesNotOverflow()
    {
        // Integer.MAX_VALUE + 1 overflows int arithmetic to a negative sum, making the
        // positivity guard fail and the result fall back to the nominal 1.0. The true
        // ((MAX - 1) / (MAX + 1))^2 is very close to 1.0 but not exactly 1.0.
        assertThat(sut.measureDistance(null, Integer.MAX_VALUE, 1)).isCloseTo(1.0, offset(0.0001))
                .isLessThan(1.0);
    }

    @Test
    public void doubleDistanceIsSquaredRelativeDifference()
    {
        assertThat(sut.measureDistance(null, 1.0, 3.0)).isCloseTo(0.25, offset(0.0001));
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
