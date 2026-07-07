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

import java.util.Map;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.junit.jupiter.api.Test;

class CombinedCategoricalDissimilarityTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd, String aLabel)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, Map.of("label", aLabel));
    }

    @Test
    void testDefaultsWeightBothEqually()
    {
        var sut = new CombinedCategoricalDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 5, 15, "B");

        // positional = 0.25, categorical = 1.0 (different), alpha=beta=1 -> 0.25 + 1.0 = 1.25
        assertThat(sut.dissimilarity(u, v)).isEqualTo(1.25);
    }

    @Test
    void testAlphaBetaWeighting()
    {
        var sut = CombinedCategoricalDissimilarity.builder() //
                .withAlpha(3.0) //
                .withBeta(2.0) //
                .build();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 5, 15, "B");

        // 3 * 0.25 (positional) + 2 * 1.0 (categorical) = 0.75 + 2.0 = 2.75
        assertThat(sut.dissimilarity(u, v)).isEqualTo(2.75);
    }

    @Test
    void testConstructorMatchesBuilder()
    {
        var sut = new CombinedCategoricalDissimilarity(3.0, 2.0, 1.0,
                new PositionalSporadicDissimilarity(1.0),
                new AbsoluteCategoricalDissimilarity(1.0));
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 5, 15, "B");

        assertThat(sut.dissimilarity(u, v)).isEqualTo(2.75);
    }

    @Test
    void testSameCategorySameSpanIsZero()
    {
        var sut = new CombinedCategoricalDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 0, 10, "A");

        assertThat(sut.dissimilarity(u, v)).isEqualTo(0.0);
    }

    @Test
    void testSelfIsZeroAndSymmetric()
    {
        var sut = CombinedCategoricalDissimilarity.builder().withAlpha(3.0).withBeta(2.0).build();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 5, 15, "B");

        assertThat(sut.dissimilarity(u, u)).isEqualTo(0.0);
        assertThat(sut.dissimilarity(u, v)).isEqualTo(sut.dissimilarity(v, u));
    }

    @Test
    void testNullUnitsCostDeltaEmpty()
    {
        var sut = CombinedCategoricalDissimilarity.builder() //
                .withAlpha(3.0) //
                .withBeta(2.0) //
                .withDeltaEmpty(1.5) //
                .build();
        var u = unit(ANNOTATOR_1, 0, 10, "A");

        // Any pair involving an empty unit costs the combined deltaEmpty, NOT alpha/beta-weighted.
        assertThat(sut.dissimilarity(u, null)).isEqualTo(1.5);
        assertThat(sut.dissimilarity(null, u)).isEqualTo(1.5);
        assertThat(sut.dissimilarity(null, null)).isEqualTo(1.5);
    }
}
