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
 * Original file: pygamma_agreement/dissimilarity.py (AbstractDissimilarity).
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

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

/**
 * Base class for the pygamma-style dissimilarities. It centralizes the handling of "empty units":
 * in the gamma measure any pair of units in which at least one unit is empty (i.e. {@code null})
 * costs {@code deltaEmpty}. This mirrors {@code dissimilarity.py:_compute_alignment_disorders} in
 * pygamma-agreement (https://github.com/bootphon/pygamma-agreement, MIT license), where a pair
 * involving an empty unit always contributes {@code delta_empty}.
 * <p>
 * Corresponds to the Python class {@code AbstractDissimilarity}. Deviation from the original: in
 * pygamma the empty-unit cost is applied by the alignment-disorder loop (which never calls the
 * dissimilarity for an empty unit), whereas here the guard is centralized in this base class so
 * that {@link #dissimilarity} itself is well-defined for {@code null} arguments.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public abstract class AbstractDissimilarity
    implements IDissimilarity
{
    /** Distance between a unit and a "null"/empty unit. Defaults to 1.0 in pygamma. */
    protected final double deltaEmpty;

    protected AbstractDissimilarity(double aDeltaEmpty)
    {
        deltaEmpty = aDeltaEmpty;
    }

    public double getDeltaEmpty()
    {
        return deltaEmpty;
    }

    @Override
    public final double dissimilarity(AlignableAnnotationUnit aUnit1,
            AlignableAnnotationUnit aUnit2)
    {
        // Any pair involving an empty (null) unit - including a pair of two empty units - costs
        // deltaEmpty. See pygamma dissimilarity.py:_compute_alignment_disorders.
        if (aUnit1 == null || aUnit2 == null) {
            return deltaEmpty;
        }

        return dissimilarityInternal(aUnit1, aUnit2);
    }

    /**
     * Computes the dissimilarity for two guaranteed non-{@code null} units.
     */
    protected abstract double dissimilarityInternal(AlignableAnnotationUnit aUnit1,
            AlignableAnnotationUnit aUnit2);
}
