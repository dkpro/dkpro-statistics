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
 */
package org.dkpro.statistics.agreement.aligning.alignment;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.CombinedCategoricalDissimilarity;
import org.junit.jupiter.api.Test;

public class BestAlignmentSolverTest
{
    private static final Rater ANN1 = new Rater("Ann1", 0);
    private static final Rater ANN2 = new Rater("Ann2", 1);

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd, String aCategory)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, Map.of("category", aCategory));
    }

    private static CombinedCategoricalDissimilarity dissimilarity()
    {
        return CombinedCategoricalDissimilarity.builder().build();
    }

    @Test
    void testTwoIdenticalUnitsDisorderIsZero()
    {
        var units = asList( //
                unit(ANN1, 0, 10, "a"), //
                unit(ANN2, 0, 10, "a"));
        var set = new AnnotationSet(units);

        var result = BestAlignmentSolver.solve(set, dissimilarity());

        // The two units are paired, no empty pairing -> disorder 0.
        assertThat(result.disorder()).isCloseTo(0.0, offset(1e-12));
    }

    @Test
    void testDisjointUnitsBestIsEmptyPairing()
    {
        // Far-apart units: pairing them is not even a valid candidate (positional dissim >> criterion),
        // so the best alignment pairs each with the empty unit. Each singleton costs deltaEmpty = 1;
        // sum = 2, divided by avg annotations (2/2 = 1) -> disorder 2.
        var units = asList( //
                unit(ANN1, 0, 10, "a"), //
                unit(ANN2, 1000, 1010, "b"));
        var set = new AnnotationSet(units);

        var result = BestAlignmentSolver.solve(set, dissimilarity());

        assertThat(result.disorder()).isCloseTo(2.0, offset(1e-12));
    }

    @Test
    void testPairingBeatsEmptyPairing()
    {
        // Close units, same category: pairing disorder = ((1+1)/20)^2 = 0.01, far below the
        // two-singletons cost of 2. Best disorder = 0.01.
        var units = asList( //
                unit(ANN1, 0, 10, "a"), //
                unit(ANN2, 1, 11, "a"));
        var set = new AnnotationSet(units);

        var result = BestAlignmentSolver.solve(set, dissimilarity());

        assertThat(result.disorder()).isCloseTo(0.01, offset(1e-9));
    }

    @Test
    void testInternalConsistencyDisorderMatchesAlignment()
    {
        var units = asList( //
                unit(ANN1, 0, 10, "a"), //
                unit(ANN1, 20, 30, "b"), //
                unit(ANN2, 1, 11, "a"), //
                unit(ANN2, 100, 110, "b"));
        var set = new AnnotationSet(units);
        var d = dissimilarity();

        var result = BestAlignmentSolver.solve(set, d);

        assertThat(result.alignment().getDisorder(d)).isCloseTo(result.disorder(), offset(1e-9));
    }

    @Test
    void testPrunedCandidatesRespectCriterion()
    {
        // A far pairing (Ann1[0,10] with Ann2[1000,1010]) must be pruned: its unitary-alignment
        // disorder exceeds n * deltaEmpty = 2.
        var far1 = unit(ANN1, 0, 10, "a");
        var far2 = unit(ANN2, 1000, 1010, "b");
        var near1 = unit(ANN1, 100, 110, "a");
        var near2 = unit(ANN2, 101, 111, "a");
        var set = new AnnotationSet(asList(far1, far2, near1, near2));
        double deltaEmpty = 1.0;
        int n = 2;

        var candidates = BestAlignmentSolver.generateCandidates(set, dissimilarity(), deltaEmpty);

        // Every kept candidate must satisfy the pruning criterion.
        for (double disorder : candidates.disorders()) {
            assertThat(disorder).isLessThanOrEqualTo(n * deltaEmpty + 1e-9);
        }

        // Specifically, no candidate must pair far1 with far2. far1 is Ann1's unit at index 0 (begin
        // 0 < 100), far2 is Ann2's unit at index 1 (begin 1000 > 101). A candidate pairing them would
        // have Ann1 index 0 and Ann2 index 1 both non-empty.
        var raters = candidates.raters();
        int ann1Idx = raters.indexOf(ANN1);
        int ann2Idx = raters.indexOf(ANN2);
        int far1Index = candidates.unitsPerRater().get(ann1Idx).indexOf(far1);
        int far2Index = candidates.unitsPerRater().get(ann2Idx).indexOf(far2);
        for (int[] tuple : candidates.tuples()) {
            boolean pairsFarUnits = tuple[ann1Idx] == far1Index && tuple[ann2Idx] == far2Index;
            assertThat(pairsFarUnits).isFalse();
        }
    }

    @Test
    void testTooLargeContinuumIsRejected()
    {
        // Build a continuum whose cartesian product Π(n_i + 1) exceeds MAX_PRODUCT_SIZE (50M). With
        // three raters of 400 units each, the product is 401^3 ~= 64.5M, so the guard must trip
        // before any enumeration happens.
        var ann3 = new Rater("Ann3", 2);
        var units = new ArrayList<AlignableAnnotationUnit>();
        for (int i = 0; i < 400; i++) {
            long begin = i * 10L;
            units.add(unit(ANN1, begin, begin + 5, "a"));
            units.add(unit(ANN2, begin, begin + 5, "a"));
            units.add(new AlignableAnnotationUnit(ann3, null, begin, begin + 5,
                    Map.of("category", "a")));
        }
        var set = new AnnotationSet(units);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> BestAlignmentSolver.solve(set, dissimilarity()));
    }

    @Test
    void testBruteForceCrossCheck()
    {
        // 2 raters x 3 units: compare the ILP optimum against an exhaustive enumeration of all
        // partial matchings.
        var a0 = unit(ANN1, 0, 10, "a");
        var a1 = unit(ANN1, 30, 40, "b");
        var a2 = unit(ANN1, 55, 65, "a");
        var b0 = unit(ANN2, 2, 12, "a");
        var b1 = unit(ANN2, 33, 43, "a");
        var b2 = unit(ANN2, 200, 210, "b");
        var aUnits = asList(a0, a1, a2);
        var bUnits = asList(b0, b1, b2);
        var set = new AnnotationSet(asList(a0, a1, a2, b0, b1, b2));
        var d = dissimilarity();
        double deltaEmpty = 1.0;

        var result = BestAlignmentSolver.solve(set, d);

        double bruteForce = bruteForceBestDisorder(aUnits, bUnits, d, deltaEmpty);
        assertThat(result.disorder()).isCloseTo(bruteForce, offset(1e-9));
    }

    /**
     * Exhaustively enumerates all partial matchings between two raters' units and returns the minimal
     * alignment disorder. For n = 2 the unitary-alignment disorder divides by C(2,2) = 1, so a matched
     * pair costs d(a, b) and an unmatched unit costs deltaEmpty; the alignment disorder is the sum
     * divided by the average number of annotations (numUnits / 2).
     */
    private static double bruteForceBestDisorder(List<AlignableAnnotationUnit> aUnits,
            List<AlignableAnnotationUnit> bUnits, CombinedCategoricalDissimilarity d, double deltaEmpty)
    {
        double avg = (aUnits.size() + bUnits.size()) / 2.0;
        boolean[] usedB = new boolean[bUnits.size()];
        double best = matchRec(0, aUnits, bUnits, usedB, d, deltaEmpty);
        return best / avg;
    }

    private static double matchRec(int aIdx, List<AlignableAnnotationUnit> aUnits,
            List<AlignableAnnotationUnit> bUnits, boolean[] usedB, CombinedCategoricalDissimilarity d,
            double deltaEmpty)
    {
        if (aIdx == aUnits.size()) {
            // remaining unmatched b-units become singletons
            double rest = 0;
            for (boolean used : usedB) {
                if (!used) {
                    rest += deltaEmpty;
                }
            }
            return rest;
        }

        // Option 1: a-unit stays a singleton.
        double best = deltaEmpty + matchRec(aIdx + 1, aUnits, bUnits, usedB, d, deltaEmpty);

        // Option 2: a-unit paired with an available b-unit.
        for (int bIdx = 0; bIdx < bUnits.size(); bIdx++) {
            if (usedB[bIdx]) {
                continue;
            }
            usedB[bIdx] = true;
            double cost = d.dissimilarity(aUnits.get(aIdx), bUnits.get(bIdx))
                    + matchRec(aIdx + 1, aUnits, bUnits, usedB, d, deltaEmpty);
            usedB[bIdx] = false;
            best = Math.min(best, cost);
        }

        return best;
    }
}
