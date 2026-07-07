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
 * Original file: pygamma_agreement/dissimilarity.py (PositionalSporadicDissimilarity).
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
 * Positional-sporadic dissimilarity. Takes only the position (begin/end offsets) of the annotations
 * into account:
 *
 * <pre>
 * d(u, v) = ((|begin_u - begin_v| + |end_u - end_v|) / ((end_u - begin_u) + (end_v - begin_v)))^2 * deltaEmpty
 * </pre>
 *
 * It is {@code 0} for identical spans, {@code < deltaEmpty} when the spans fully overlap and
 * {@code > deltaEmpty} when the spans are disjoint. Corresponds to the Python class
 * {@code PositionalSporadicDissimilarity} in pygamma-agreement.
 * <p>
 * Deviation from the original: the units here expose {@code long} begin/end offsets (rather than
 * pygamma's {@code float32} start/end/duration), so the arithmetic is performed in {@code double};
 * the segment durations are derived as {@code end - begin} instead of being carried separately.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public class PositionalSporadicDissimilarity
    extends AbstractDissimilarity
{
    public PositionalSporadicDissimilarity()
    {
        this(1.0);
    }

    public PositionalSporadicDissimilarity(double aDeltaEmpty)
    {
        super(aDeltaEmpty);
    }

    @Override
    protected double dissimilarityInternal(AlignableAnnotationUnit aUnit1,
            AlignableAnnotationUnit aUnit2)
    {
        double distance = Math.abs((double) aUnit1.getBegin() - aUnit2.getBegin())
                + Math.abs((double) aUnit1.getEnd() - aUnit2.getEnd());
        double durations = (double) (aUnit1.getEnd() - aUnit1.getBegin())
                + (double) (aUnit2.getEnd() - aUnit2.getBegin());

        double ratio = distance / durations;

        return ratio * ratio * deltaEmpty;
    }
}
