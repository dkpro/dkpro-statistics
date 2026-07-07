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
 * Original file: pygamma_agreement/dissimilarity.py (PrecomputedCategoricalDissimilarity).
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
package org.dkpro.statistics.agreement.aligning.dissimilarity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

/**
 * Categorical dissimilarity backed by a precomputed category-to-category dissimilarity matrix:
 *
 * <pre>
 * d(u, v) = matrix[index(cat_u)][index(cat_v)] * deltaEmpty
 * </pre>
 *
 * where the category of a unit is the value of a configured feature. The rows and columns of the
 * matrix follow the categories in <b>alphabetical order</b>: the categories passed to the
 * constructor are sorted internally and each category is indexed by its position in that sorted
 * order, exactly like the Python class {@code PrecomputedCategoricalDissimilarity} in
 * pygamma-agreement. Callers must therefore lay out the matrix in alphabetical category order
 * regardless of the order in which they pass the categories.
 * <p>
 * Deviation from the original: pygamma resolves the category via a scalar annotation index, whereas
 * here the category is the value of a configurable unit feature; an unknown category value raises
 * an {@link IllegalArgumentException}. The matrix is validated (square, matching dimensions,
 * symmetric, zero diagonal) and defensively copied at construction time.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public class PrecomputedCategoricalDissimilarity
    extends AbstractDissimilarity
{
    private final String featureName;
    private final double[][] matrix;
    // Maps a category value to its row/column index in the (alphabetically sorted) matrix.
    private final Map<String, Integer> categoryIndex;

    public PrecomputedCategoricalDissimilarity(String aFeatureName, List<String> aCategories,
            double[][] aMatrix, double aDeltaEmpty)
    {
        super(aDeltaEmpty);

        if (aFeatureName == null) {
            throw new IllegalArgumentException("Feature name must not be null.");
        }
        if (aCategories == null || aCategories.isEmpty()) {
            throw new IllegalArgumentException("At least one category must be provided.");
        }
        if (aMatrix == null) {
            throw new IllegalArgumentException("Dissimilarity matrix must not be null.");
        }

        featureName = aFeatureName;

        // Sort categories alphabetically (defensively, de-duplicating) and index into the matrix by
        // position in the sorted order. The matrix rows/columns are interpreted in this order.
        List<String> sortedCategories = new ArrayList<String>(
                new LinkedHashSet<String>(aCategories));
        sortedCategories.sort(null);

        categoryIndex = new TreeMap<String, Integer>();
        for (int i = 0; i < sortedCategories.size(); i++) {
            categoryIndex.put(sortedCategories.get(i), i);
        }

        int n = sortedCategories.size();

        // Validate: square matrix with dimensions matching the number of (distinct) categories.
        if (aMatrix.length != n) {
            throw new IllegalArgumentException(
                    "Matrix has " + aMatrix.length + " rows but there are " + n + " categories.");
        }
        for (int i = 0; i < n; i++) {
            if (aMatrix[i] == null || aMatrix[i].length != n) {
                throw new IllegalArgumentException(
                        "Matrix must be square (" + n + "x" + n + "). Offending row: " + i);
            }
        }

        // Validate: zero diagonal and symmetry.
        for (int i = 0; i < n; i++) {
            if (aMatrix[i][i] != 0.0) {
                throw new IllegalArgumentException(
                        "Matrix diagonal must be zero. Offending entry: [" + i + "][" + i + "] = "
                                + aMatrix[i][i]);
            }
            for (int j = i + 1; j < n; j++) {
                if (aMatrix[i][j] != aMatrix[j][i]) {
                    throw new IllegalArgumentException(
                            "Matrix must be symmetric. Offending entries: [" + i + "][" + j + "] = "
                                    + aMatrix[i][j] + " vs [" + j + "][" + i + "] = "
                                    + aMatrix[j][i]);
                }
            }
        }

        // Defensive copy.
        matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aMatrix[i], 0, matrix[i], 0, n);
        }
    }

    private int indexOf(AlignableAnnotationUnit aUnit)
    {
        String category = aUnit.getFeatureValue(featureName);
        Integer idx = categoryIndex.get(category);
        if (idx == null) {
            throw new IllegalArgumentException("Unknown category [" + category + "] for feature ["
                    + featureName + "]. Known categories: " + categoryIndex.keySet());
        }
        return idx;
    }

    @Override
    protected double dissimilarityInternal(AlignableAnnotationUnit aUnit1,
            AlignableAnnotationUnit aUnit2)
    {
        return matrix[indexOf(aUnit1)][indexOf(aUnit2)] * deltaEmpty;
    }
}
