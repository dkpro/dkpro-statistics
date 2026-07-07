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

class AbsoluteCategoricalDissimilarityTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd, String aLabel)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, Map.of("label", aLabel));
    }

    @Test
    void testIdenticalCategoriesAreZero()
    {
        var sut = new AbsoluteCategoricalDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 5, 20, "A");

        assertThat(sut.dissimilarity(u, v)).isEqualTo(0.0);
    }

    @Test
    void testDifferentCategoriesCostDeltaEmpty()
    {
        var sut = new AbsoluteCategoricalDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 0, 10, "B");

        assertThat(sut.dissimilarity(u, v)).isEqualTo(1.0);
    }

    @Test
    void testDeltaEmptyScaling()
    {
        var sut = new AbsoluteCategoricalDissimilarity(3.0);
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 0, 10, "B");

        assertThat(sut.dissimilarity(u, v)).isEqualTo(3.0);
    }

    @Test
    void testSelfIsZeroAndSymmetric()
    {
        var sut = new AbsoluteCategoricalDissimilarity();
        var u = unit(ANNOTATOR_1, 0, 10, "A");
        var v = unit(ANNOTATOR_2, 0, 10, "B");

        assertThat(sut.dissimilarity(u, u)).isEqualTo(0.0);
        assertThat(sut.dissimilarity(u, v)).isEqualTo(sut.dissimilarity(v, u));
    }

    @Test
    void testNullUnitsCostDeltaEmpty()
    {
        var sut = new AbsoluteCategoricalDissimilarity(2.0);
        var u = unit(ANNOTATOR_1, 0, 10, "A");

        assertThat(sut.dissimilarity(u, null)).isEqualTo(2.0);
        assertThat(sut.dissimilarity(null, u)).isEqualTo(2.0);
        assertThat(sut.dissimilarity(null, null)).isEqualTo(2.0);
    }
}
