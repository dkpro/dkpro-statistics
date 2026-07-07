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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Map;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.junit.jupiter.api.Test;

class PrecomputedCategoricalDissimilarityTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);

    private static AlignableAnnotationUnit unit(Rater aRater, String aLabel)
    {
        return new AlignableAnnotationUnit(aRater, null, 0, 10, Map.of("label", aLabel));
    }

    // Matrix laid out in alphabetical category order: a, b, c.
    // a b c
    // a [ 0.0 0.5 0.9 ]
    // b [ 0.5 0.0 0.2 ]
    // c [ 0.9 0.2 0.0 ]
    private static double[][] matrix()
    {
        return new double[][] { //
                { 0.0, 0.5, 0.9 }, //
                { 0.5, 0.0, 0.2 }, //
                { 0.9, 0.2, 0.0 } };
    }

    @Test
    void testMatrixLookupWithNonAlphabeticalCategoryInput()
    {
        // Categories passed in non-alphabetical order; the matrix must still be interpreted in
        // sorted order (a, b, c).
        var categories = asList("c", "a", "b");
        var sut = new PrecomputedCategoricalDissimilarity("label", categories, matrix(), 1.0);

        // a<->c -> index 0,2 -> 0.9
        assertThat(sut.dissimilarity(unit(ANNOTATOR_1, "a"), unit(ANNOTATOR_2, "c")))
                .isEqualTo(0.9);
        // b<->c -> index 1,2 -> 0.2
        assertThat(sut.dissimilarity(unit(ANNOTATOR_1, "b"), unit(ANNOTATOR_2, "c")))
                .isEqualTo(0.2);
        // a<->b -> index 0,1 -> 0.5
        assertThat(sut.dissimilarity(unit(ANNOTATOR_1, "a"), unit(ANNOTATOR_2, "b")))
                .isEqualTo(0.5);
    }

    @Test
    void testDeltaEmptyScaling()
    {
        var sut = new PrecomputedCategoricalDissimilarity("label", asList("a", "b", "c"), matrix(),
                2.0);

        // a<->c -> 0.9 * deltaEmpty(2.0) = 1.8
        assertThat(sut.dissimilarity(unit(ANNOTATOR_1, "a"), unit(ANNOTATOR_2, "c")))
                .isEqualTo(1.8);
    }

    @Test
    void testSelfIsZeroAndSymmetric()
    {
        var sut = new PrecomputedCategoricalDissimilarity("label", asList("a", "b", "c"), matrix(),
                1.0);

        assertThat(sut.dissimilarity(unit(ANNOTATOR_1, "b"), unit(ANNOTATOR_2, "b")))
                .isEqualTo(0.0);
        assertThat(sut.dissimilarity(unit(ANNOTATOR_1, "a"), unit(ANNOTATOR_2, "c")))
                .isEqualTo(sut.dissimilarity(unit(ANNOTATOR_1, "c"), unit(ANNOTATOR_2, "a")));
    }

    @Test
    void testNullUnitsCostDeltaEmpty()
    {
        var sut = new PrecomputedCategoricalDissimilarity("label", asList("a", "b", "c"), matrix(),
                1.5);
        var u = unit(ANNOTATOR_1, "a");

        assertThat(sut.dissimilarity(u, null)).isEqualTo(1.5);
        assertThat(sut.dissimilarity(null, u)).isEqualTo(1.5);
        assertThat(sut.dissimilarity(null, null)).isEqualTo(1.5);
    }

    @Test
    void testUnknownCategoryThrows()
    {
        var sut = new PrecomputedCategoricalDissimilarity("label", asList("a", "b", "c"), matrix(),
                1.0);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> sut.dissimilarity(unit(ANNOTATOR_1, "z"), unit(ANNOTATOR_2, "a")));
    }

    @Test
    void testNonSquareMatrixThrows()
    {
        double[][] nonSquare = { { 0.0, 0.5 }, { 0.5, 0.0 }, { 0.9, 0.2 } };
        List<String> categories = asList("a", "b", "c");

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> new PrecomputedCategoricalDissimilarity("label", categories, nonSquare, 1.0));
    }

    @Test
    void testDimensionMismatchThrows()
    {
        double[][] twoByTwo = { { 0.0, 0.5 }, { 0.5, 0.0 } };
        List<String> categories = asList("a", "b", "c");

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> new PrecomputedCategoricalDissimilarity("label", categories, twoByTwo, 1.0));
    }

    @Test
    void testAsymmetricMatrixThrows()
    {
        double[][] asymmetric = { //
                { 0.0, 0.5, 0.9 }, //
                { 0.4, 0.0, 0.2 }, //
                { 0.9, 0.2, 0.0 } };
        List<String> categories = asList("a", "b", "c");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PrecomputedCategoricalDissimilarity("label", categories,
                        asymmetric, 1.0));
    }

    @Test
    void testNonZeroDiagonalThrows()
    {
        double[][] nonZeroDiag = { //
                { 0.1, 0.5, 0.9 }, //
                { 0.5, 0.0, 0.2 }, //
                { 0.9, 0.2, 0.0 } };
        List<String> categories = asList("a", "b", "c");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PrecomputedCategoricalDissimilarity("label", categories,
                        nonZeroDiag, 1.0));
    }
}
