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
 * Original file: pygamma_agreement/dissimilarity.py (AbsoluteCategoricalDissimilarity).
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

/**
 * Basic categorical dissimilarity. It is {@code 0.0} when the two units carry identical feature
 * sets (same names and same values) and {@code deltaEmpty} otherwise. The feature comparison
 * follows the same semantics as {@link NominalFeatureDissimilarity}. Corresponds to the Python
 * class {@code AbsoluteCategoricalDissimilarity} in pygamma-agreement.
 * <p>
 * Deviation from the original: pygamma compares a single scalar category index per unit, whereas
 * the units in this project carry a feature map, so this implementation compares the full feature
 * sets (name/value pairs) of the two units, matching {@link NominalFeatureDissimilarity}.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public class AbsoluteCategoricalDissimilarity
    extends AbstractDissimilarity
{
    public AbsoluteCategoricalDissimilarity()
    {
        this(1.0);
    }

    public AbsoluteCategoricalDissimilarity(double aDeltaEmpty)
    {
        super(aDeltaEmpty);
    }

    @Override
    protected double dissimilarityInternal(AlignableAnnotationUnit aUnit1,
            AlignableAnnotationUnit aUnit2)
    {
        Set<String> featureNames = new HashSet<String>();
        featureNames.addAll(aUnit1.getFeatureNames());
        featureNames.addAll(aUnit2.getFeatureNames());

        for (String feature : featureNames) {
            if (!Objects.equals(aUnit1.getFeatureValue(feature), aUnit2.getFeatureValue(feature))) {
                return deltaEmpty;
            }
        }

        return 0.0;
    }
}
