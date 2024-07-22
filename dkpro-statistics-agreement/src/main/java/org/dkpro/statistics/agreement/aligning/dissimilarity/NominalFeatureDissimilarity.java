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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

public class NominalFeatureDissimilarity
    implements IDissimilarity
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

        if (!Objects.equals(aUnit1.getType(), aUnit2.getType())) {
            return 1;
        }

        return positionDissimilarity(aUnit1, aUnit2) + featureDissimilarity(aUnit1, aUnit2);
    }

    protected Set<String> getFeatureNames(AlignableAnnotationUnit... aUnits)
    {
        var featureNames = new HashSet<String>();

        for (var unit : aUnits) {
            featureNames.addAll(unit.getFeatureNames());
        }

        return featureNames;
    }

    protected int numberOfDissimilarFeatures(AlignableAnnotationUnit u, AlignableAnnotationUnit v)
    {
        var featureNames = getFeatureNames(u, v);

        int diffs = 0;
        for (var attr : featureNames) {
            if (!Objects.equals(u.getFeatureValue(attr), v.getFeatureValue(attr))) {
                diffs += 1;
            }
        }

        return diffs;
    }

    private double featureDissimilarity(AlignableAnnotationUnit u, AlignableAnnotationUnit v)
    {
        var featureNames = getFeatureNames(u, v);

        if (featureNames.isEmpty()) {
            return 0;
        }

        return this.numberOfDissimilarFeatures(u, v) / ((float) featureNames.size());
    }

    protected double positionDissimilarity(AlignableAnnotationUnit u, AlignableAnnotationUnit v)
    {
        if (u.getBegin() == v.getBegin() && u.getEnd() == v.getEnd()) {
            return 0;
        }

        return 1;
    }
}
