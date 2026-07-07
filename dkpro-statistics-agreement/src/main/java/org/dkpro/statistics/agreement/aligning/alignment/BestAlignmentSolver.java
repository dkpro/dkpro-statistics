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
 * Ported from the pygamma-agreement project:
 * https://github.com/bootphon/pygamma-agreement - version 0.5.9, commit 44587ef.
 * Original files: pygamma_agreement/dissimilarity.py (_get_all_valid_alignments),
 * pygamma_agreement/continuum.py (get_best_alignment) and
 * pygamma_agreement/numba_utils.py (build_A, iter_tuples).
 * Original authors: Rachid Riad, Hadrien Titeux, Léopold Favre.
 *
 * The original code is distributed under the MIT license, reproduced verbatim below:
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 CoML
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Paper: Mathet, Widlöcher, and Métivier (2015), "The Unified and Holistic Method
 * Gamma for Inter-Annotator Agreement Measure and Alignment"
 * (https://aclanthology.org/J15-3003.pdf).
 */
package org.dkpro.statistics.agreement.aligning.alignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.AbstractDissimilarity;
import org.dkpro.statistics.agreement.aligning.dissimilarity.IDissimilarity;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/**
 * Computes the exact best (minimal-disorder) alignment of a continuum, mirroring pygamma-agreement.
 * <p>
 * The computation has two stages:
 * <ol>
 * <li><b>Candidate generation and pruning</b> - a port of
 * {@code dissimilarity.py:_get_all_valid_alignments}. It enumerates the cartesian product over the
 * units of each rater (each extended by an "empty" unit) and keeps a candidate unitary alignment iff
 * its raw pair-sum disorder is at most {@code C(n,2) * deltaEmpty * n} (the criterion from Mathet et
 * al. 2015, section 5.1.1). The all-empty tuple is discarded. Kept disorders are divided by
 * {@code C(n,2)}.</li>
 * <li><b>Exact best-alignment computation</b> - a port of {@code continuum.py:get_best_alignment}
 * (with the constraint matrix of {@code numba_utils.py:build_A}). A binary integer linear program is
 * solved with ojAlgo: one binary variable per candidate, one equality constraint per (rater, unit)
 * requiring that unit to appear in exactly one chosen candidate, minimizing the total candidate
 * disorder.</li>
 * </ol>
 * <p>
 * Deviations from the original: units carry {@code long} offsets and computations are performed in
 * {@code double} rather than pygamma's {@code float32}; the ILP is solved with ojAlgo (pure-Java MIP)
 * instead of cvxpy/CBC/GLPK; candidates are enumerated lazily with an odometer instead of numba's
 * {@code iter_tuples}; the all-empty tuple is skipped explicitly rather than by dropping the last
 * enumerated element. Only the optimal disorder <em>value</em> is guaranteed to match the reference -
 * the concrete chosen alignment may differ when there are ties.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public final class BestAlignmentSolver
{
    /**
     * Upper bound on the size of the cartesian product {@code Π (n_i + 1)} that the exact solver is
     * willing to enumerate. Beyond this the continuum is rejected as too large.
     */
    public static final long MAX_PRODUCT_SIZE = 50_000_000L;

    private BestAlignmentSolver()
    {
        // utility class
    }

    /**
     * Result of an exact best-alignment computation.
     *
     * @param alignment
     *            the chosen best alignment (a valid partition of the annotation set).
     * @param disorder
     *            the observed disorder, i.e. the sum of the chosen candidate disorders divided by the
     *            continuum's average number of annotations per rater. This equals
     *            {@code alignment.getDisorder(dissimilarity)} up to floating-point error.
     */
    public record BestAlignment(Alignment alignment, double disorder)
    {
    }

    /**
     * Convenience overload deriving {@code deltaEmpty} from the given dissimilarity, which must be an
     * {@link AbstractDissimilarity}.
     */
    public static BestAlignment solve(AnnotationSet aAnnotationSet, AbstractDissimilarity aDissimilarity)
    {
        return solve(aAnnotationSet, aDissimilarity, aDissimilarity.getDeltaEmpty());
    }

    /**
     * Computes the exact best alignment of the given annotation set.
     *
     * @param aAnnotationSet
     *            the continuum; must contain at least two raters.
     * @param aDissimilarity
     *            the dissimilarity used to score unit-to-unit disorder.
     * @param aDeltaEmpty
     *            the cost of pairing a unit with the empty unit.
     * @return the best alignment and its disorder.
     * @throws IllegalArgumentException
     *             if there are fewer than two raters or the continuum is too large for the exact
     *             solver.
     * @throws IllegalStateException
     *             if the ILP solver does not reach an optimal solution.
     */
    public static BestAlignment solve(AnnotationSet aAnnotationSet, IDissimilarity aDissimilarity,
            double aDeltaEmpty)
    {
        var candidates = generateCandidates(aAnnotationSet, aDissimilarity, aDeltaEmpty);
        return buildBestAlignment(aAnnotationSet, candidates);
    }

    /**
     * Holds the pruned candidate unitary alignments for a continuum. Each candidate is a tuple of unit
     * indices (one per rater, in the annotation set's canonical rater order); an index equal to
     * {@code sizes[i]} denotes the empty unit for rater {@code i}. The parallel {@code disorders} list
     * holds each candidate's unitary-alignment disorder (raw pair-sum divided by {@code C(n,2)}).
     */
    record CandidateSet(List<Rater> raters, List<List<AlignableAnnotationUnit>> unitsPerRater,
            int[] sizes, List<int[]> tuples, List<Double> disorders)
    {
    }

    /**
     * Generates and prunes the candidate unitary alignments, mirroring
     * {@code dissimilarity.py:_get_all_valid_alignments}.
     */
    static CandidateSet generateCandidates(AnnotationSet aAnnotationSet,
            IDissimilarity aDissimilarity, double aDeltaEmpty)
    {
        var raters = new ArrayList<Rater>(aAnnotationSet.getRaters());
        int n = raters.size();
        if (n < 2) {
            throw new IllegalArgumentException(
                    "The best alignment cannot be computed with fewer than two raters.");
        }

        // Units per rater, in the annotation set's canonical order.
        List<List<AlignableAnnotationUnit>> unitsPerRater = new ArrayList<>(n);
        int[] sizes = new int[n];
        for (int i = 0; i < n; i++) {
            var units = aAnnotationSet.getUnitsWithRater(raters.get(i));
            unitsPerRater.add(units);
            sizes[i] = units.size();
        }

        // Guard against a combinatorial explosion: Π (n_i + 1).
        long product = 1;
        for (int i = 0; i < n; i++) {
            product *= (sizes[i] + 1L);
            if (product > MAX_PRODUCT_SIZE) {
                throw new IllegalArgumentException("The continuum is too large for the exact solver: "
                        + "the candidate cartesian product exceeds " + MAX_PRODUCT_SIZE
                        + " tuples. Consider reducing the number of units per rater.");
            }
        }

        long c2n = (long) n * (n - 1) / 2;
        double criterion = c2n * aDeltaEmpty * n;

        // Precompute, for each rater pair (a > b), the (sizes[a]+1) x (sizes[b]+1) dissimilarity
        // matrix. The last row/column (index sizes[x]) holds the empty-unit cost deltaEmpty.
        // Mirrors the precomputation block of _get_all_valid_alignments.
        double[][][][] precomp = new double[n][][][];
        for (int a = 0; a < n; a++) {
            precomp[a] = new double[a][][];
            var unitsA = unitsPerRater.get(a);
            for (int b = 0; b < a; b++) {
                var unitsB = unitsPerRater.get(b);
                double[][] matrix = new double[sizes[a] + 1][sizes[b] + 1];
                for (int ia = 0; ia < sizes[a]; ia++) {
                    for (int ib = 0; ib < sizes[b]; ib++) {
                        matrix[ia][ib] = aDissimilarity.dissimilarity(unitsA.get(ia), unitsB.get(ib));
                    }
                }
                for (int ib = 0; ib <= sizes[b]; ib++) {
                    matrix[sizes[a]][ib] = aDeltaEmpty;
                }
                for (int ia = 0; ia <= sizes[a]; ia++) {
                    matrix[ia][sizes[b]] = aDeltaEmpty;
                }
                precomp[a][b] = matrix;
            }
        }

        // Lazily enumerate the cartesian product (odometer over sizes[i]+1), keeping valid
        // candidates. Index sizes[i] denotes the empty unit for rater i.
        List<int[]> candidateTuples = new ArrayList<>();
        List<Double> candidateDisorders = new ArrayList<>();
        int[] tuple = new int[n];
        boolean done = false;
        while (!done) {
            double rawSum = 0;
            for (int a = 0; a < n; a++) {
                for (int b = 0; b < a; b++) {
                    rawSum += precomp[a][b][tuple[a]][tuple[b]];
                }
            }

            boolean allEmpty = true;
            for (int i = 0; i < n; i++) {
                if (tuple[i] != sizes[i]) {
                    allEmpty = false;
                    break;
                }
            }

            if (!allEmpty && rawSum <= criterion) {
                candidateTuples.add(tuple.clone());
                candidateDisorders.add(rawSum / c2n);
            }

            // Increment the odometer (little-endian), exactly like iter_tuples.
            done = true;
            for (int i = 0; i < n; i++) {
                tuple[i]++;
                if (tuple[i] <= sizes[i]) {
                    done = false;
                    break;
                }
                tuple[i] = 0;
            }
        }

        return new CandidateSet(raters, unitsPerRater, sizes, candidateTuples, candidateDisorders);
    }

    private static BestAlignment buildBestAlignment(AnnotationSet aAnnotationSet,
            CandidateSet aCandidates)
    {
        List<Rater> raters = aCandidates.raters();
        List<List<AlignableAnnotationUnit>> unitsPerRater = aCandidates.unitsPerRater();
        int[] sizes = aCandidates.sizes();
        List<int[]> candidateTuples = aCandidates.tuples();
        List<Double> candidateDisorders = aCandidates.disorders();
        int n = raters.size();
        int numCandidates = candidateTuples.size();

        var model = new ExpressionsBasedModel();

        Variable[] vars = new Variable[numCandidates];
        for (int p = 0; p < numCandidates; p++) {
            vars[p] = model.addVariable("x" + p).binary().weight(candidateDisorders.get(p));
        }

        // One equality constraint per (rater, unit): the unit must appear in exactly one chosen
        // candidate. Mirrors the constraint matrix A of build_A with A * x == 1.
        var constraints = new Expression[n][];
        for (int i = 0; i < n; i++) {
            constraints[i] = new Expression[sizes[i]];
            for (int j = 0; j < sizes[i]; j++) {
                constraints[i][j] = model.addExpression("c_" + i + "_" + j).level(1.0);
            }
        }
        for (int p = 0; p < numCandidates; p++) {
            int[] t = candidateTuples.get(p);
            for (int i = 0; i < n; i++) {
                int unitId = t[i];
                if (unitId != sizes[i]) { // non-empty unit
                    constraints[i][unitId].set(vars[p], 1.0);
                }
            }
        }

        Optimisation.Result result = model.minimise();
        if (!result.getState().isOptimal()) {
            throw new IllegalStateException(
                    "The linear solver could not find an optimal alignment (state: "
                            + result.getState() + ").");
        }

        Set<UnitaryAlignment> unitaryAlignments = new HashSet<>();
        double sumDisorder = 0;
        var raterSet = aAnnotationSet.getRaters();
        for (int p = 0; p < numCandidates; p++) {
            // cvxpy compares against 0.9 because the solver may return values like 1.0 or ~1e-14.
            if (result.doubleValue(p) <= 0.9) {
                continue;
            }

            int[] t = candidateTuples.get(p);
            var units = new ArrayList<AlignableAnnotationUnit>();
            for (int i = 0; i < n; i++) {
                if (t[i] != sizes[i]) { // non-empty unit
                    units.add(unitsPerRater.get(i).get(t[i]));
                }
            }
            unitaryAlignments.add(new UnitaryAlignment(units, raterSet));
            sumDisorder += candidateDisorders.get(p);
        }

        var alignment = new Alignment(unitaryAlignments, aAnnotationSet);
        double disorder = sumDisorder / aAnnotationSet.getAverageNumberOfAnnotations();
        return new BestAlignment(alignment, disorder);
    }
}
