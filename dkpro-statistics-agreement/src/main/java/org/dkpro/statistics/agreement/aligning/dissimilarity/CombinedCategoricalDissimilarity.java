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
 * Original file: pygamma_agreement/dissimilarity.py (CombinedCategoricalDissimilarity).
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
 * Combined categorical dissimilarity taking both positioning and categorization of annotations into
 * account:
 *
 * <pre>
 * d(u, v) = alpha * positional.d(u, v) + beta * categorical.d(u, v)
 * </pre>
 *
 * Defaults follow pygamma-agreement (https://github.com/bootphon/pygamma-agreement, MIT license):
 * {@code alpha = 1}, {@code beta = 1}, {@code deltaEmpty = 1}, the positional part is a
 * {@link PositionalSporadicDissimilarity} and the categorical part is an
 * {@link AbsoluteCategoricalDissimilarity}, each constructed with {@code deltaEmpty}. Consistent
 * with the other dissimilarities, any pair involving an empty ({@code null}) unit costs
 * {@code deltaEmpty}. Corresponds to the Python class {@code CombinedCategoricalDissimilarity} in
 * pygamma-agreement.
 * <p>
 * Deviation from the original: pygamma mutates the categorical sub-dissimilarity's
 * {@code delta_empty} to match the combined value; here the sub-dissimilarities are immutable, so
 * the default sub-parts are simply constructed with the combined {@code deltaEmpty} (custom
 * sub-parts are used as given). The empty-unit guard (inherited from {@link AbstractDissimilarity})
 * returns the combined {@code deltaEmpty} directly rather than {@code alpha}/{@code beta}-weighting
 * the sub-parts.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public class CombinedCategoricalDissimilarity
    extends AbstractDissimilarity
{
    private final double alpha;
    private final double beta;
    private final IDissimilarity positionalDissimilarity;
    private final IDissimilarity categoricalDissimilarity;

    public CombinedCategoricalDissimilarity()
    {
        this(builder());
    }

    public CombinedCategoricalDissimilarity(double aAlpha, double aBeta, double aDeltaEmpty,
            IDissimilarity aPositionalDissimilarity, IDissimilarity aCategoricalDissimilarity)
    {
        super(aDeltaEmpty);
        alpha = aAlpha;
        beta = aBeta;
        positionalDissimilarity = aPositionalDissimilarity != null ? aPositionalDissimilarity
                : new PositionalSporadicDissimilarity(aDeltaEmpty);
        categoricalDissimilarity = aCategoricalDissimilarity != null ? aCategoricalDissimilarity
                : new AbsoluteCategoricalDissimilarity(aDeltaEmpty);
    }

    private CombinedCategoricalDissimilarity(Builder aBuilder)
    {
        this(aBuilder.alpha, aBuilder.beta, aBuilder.deltaEmpty, aBuilder.positionalDissimilarity,
                aBuilder.categoricalDissimilarity);
    }

    public double getAlpha()
    {
        return alpha;
    }

    public double getBeta()
    {
        return beta;
    }

    @Override
    protected double dissimilarityInternal(AlignableAnnotationUnit aUnit1,
            AlignableAnnotationUnit aUnit2)
    {
        return alpha * positionalDissimilarity.dissimilarity(aUnit1, aUnit2)
                + beta * categoricalDissimilarity.dissimilarity(aUnit1, aUnit2);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private double alpha = 1.0;
        private double beta = 1.0;
        private double deltaEmpty = 1.0;
        private IDissimilarity positionalDissimilarity;
        private IDissimilarity categoricalDissimilarity;

        private Builder()
        {
        }

        public Builder withAlpha(double aAlpha)
        {
            alpha = aAlpha;
            return this;
        }

        public Builder withBeta(double aBeta)
        {
            beta = aBeta;
            return this;
        }

        public Builder withDeltaEmpty(double aDeltaEmpty)
        {
            deltaEmpty = aDeltaEmpty;
            return this;
        }

        public Builder withPositionalDissimilarity(IDissimilarity aPositionalDissimilarity)
        {
            positionalDissimilarity = aPositionalDissimilarity;
            return this;
        }

        public Builder withCategoricalDissimilarity(IDissimilarity aCategoricalDissimilarity)
        {
            categoricalDissimilarity = aCategoricalDissimilarity;
            return this;
        }

        public CombinedCategoricalDissimilarity build()
        {
            return new CombinedCategoricalDissimilarity(this);
        }
    }
}
