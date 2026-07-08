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

public class MASISetAnnotationDistanceFunctionTest
{
    private final MASISetAnnotationDistanceFunction sut = new MASISetAnnotationDistanceFunction();

    @Test
    public void identicalSetsAreZero()
    {
        assertThat(
                sut.measureDistance(null, new SetAnnotation("A", "B"), new SetAnnotation("A", "B")))
                        .isEqualTo(0.0);
    }

    @Test
    public void subsetIsScaledByOneThird()
    {
        // Jaccard 0.5 weighted by the MASI monotonicity factor 1/3 = 1/6.
        assertThat(sut.measureDistance(null, new SetAnnotation("A"), new SetAnnotation("A", "B")))
                .isCloseTo(1.0 / 6.0, offset(0.0001));
    }

    @Test
    public void partialOverlapIsScaledByTwoThirds()
    {
        // Jaccard 2/3 weighted by the MASI intersection factor 2/3 = 4/9.
        assertThat(
                sut.measureDistance(null, new SetAnnotation("A", "B"), new SetAnnotation("B", "C")))
                        .isCloseTo(4.0 / 9.0, offset(0.0001));
    }

    @Test
    public void disjointSetsAreMaximal()
    {
        assertThat(sut.measureDistance(null, new SetAnnotation("A"), new SetAnnotation("B")))
                .isEqualTo(1.0);
    }

    @Test
    public void nonSetCategoriesFallBackToNominal()
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
        // The NULL_CATEGORY sentinel from calculateCategoryAgreement is not a SetAnnotation.
        assertThat(sut.measureDistance(null, null, new SetAnnotation("A"))).isEqualTo(1.0);
        assertThat(sut.measureDistance(null, new SetAnnotation("A"), null)).isEqualTo(1.0);
    }
}
