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
 * Original source: https://github.com/fab-bar/TextGammaTool.git
 */
package org.dkpro.statistics.agreement.aligning.dissimilarity;

import java.util.Objects;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.AlignableAnnotationTextUnit;

/**
 * Variation of {@link NominalFeatureDissimilarity} that considers the covered text as an additional
 * implicit feature. It requires {@link AlignableAnnotationTextUnit AlignableAnnotationTextUnits}.
 */
public class NominalFeatureTextDissimilarity
    extends NominalFeatureDissimilarity
{
    @Override
    public double dissimilarity(AlignableAnnotationUnit aUnit1, AlignableAnnotationUnit aUnit2)
    {
        if (aUnit1 == null && aUnit2 == null) {
            return 0;
        }

        if (aUnit1 == null) {
            return 1;
        }

        if (aUnit2 == null) {
            return 1;
        }

        if (aUnit1 instanceof AlignableAnnotationTextUnit unit1
                && aUnit2 instanceof AlignableAnnotationTextUnit unit2) {
            var textDiff = Objects.equals(unit1.getText(), unit2.getText()) ? 0 : 1;

            return positionDissimilarity(aUnit1, aUnit2)
                    + (numberOfDissimilarFeatures(aUnit1, aUnit2) + textDiff)
                            / ((float) getFeatureNames(aUnit1, aUnit2).size() + 1);
        }

        throw new IllegalArgumentException("Units have to be TextUnits");
    }
}
