/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Reference implementation: pygamma-agreement
 * (https://github.com/bootphon/pygamma-agreement, MIT license).
 */
package org.dkpro.statistics.agreement.aligning.dissimilarity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.junit.jupiter.api.Test;

class PositionalSporadicDissimilarityTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, null);
    }

    @Test
    void testIdenticalSpansAreZero()
    {
        var sut = new PositionalSporadicDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10);
        var v = unit(ANNOTATOR_2, 0, 10);

        assertThat(sut.dissimilarity(u, v)).isEqualTo(0.0);
    }

    @Test
    void testHandComputedShiftedSpan()
    {
        var sut = new PositionalSporadicDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10);
        var v = unit(ANNOTATOR_2, 5, 15);

        // ((|0-5| + |10-15|) / (10 + 10))^2 = (10/20)^2 = 0.25
        assertThat(sut.dissimilarity(u, v)).isEqualTo(0.25);
    }

    @Test
    void testHandComputedAsymmetricSpans()
    {
        var sut = new PositionalSporadicDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10);
        var v = unit(ANNOTATOR_2, 0, 20);

        // ((|0-0| + |10-20|) / (10 + 20))^2 = (10/30)^2 = 0.1111...
        assertThat(sut.dissimilarity(u, v)).isCloseTo(1.0 / 9.0, offset(1e-12));
    }

    @Test
    void testDeltaEmptyScaling()
    {
        var sut = new PositionalSporadicDissimilarity(2.0);
        var u = unit(ANNOTATOR_1, 0, 10);
        var v = unit(ANNOTATOR_2, 5, 15);

        // 0.25 * deltaEmpty(2.0) = 0.5
        assertThat(sut.dissimilarity(u, v)).isEqualTo(0.5);
    }

    @Test
    void testSelfIsZeroAndSymmetric()
    {
        var sut = new PositionalSporadicDissimilarity();
        var u = unit(ANNOTATOR_1, 2, 12);
        var v = unit(ANNOTATOR_2, 7, 20);

        assertThat(sut.dissimilarity(u, u)).isEqualTo(0.0);
        assertThat(sut.dissimilarity(u, v)).isEqualTo(sut.dissimilarity(v, u));
    }

    @Test
    void testNullUnitsCostDeltaEmpty()
    {
        var sut = new PositionalSporadicDissimilarity(1.5);
        var u = unit(ANNOTATOR_1, 0, 10);

        assertThat(sut.dissimilarity(u, null)).isEqualTo(1.5);
        assertThat(sut.dissimilarity(null, u)).isEqualTo(1.5);
        assertThat(sut.dissimilarity(null, null)).isEqualTo(1.5);
    }
}
