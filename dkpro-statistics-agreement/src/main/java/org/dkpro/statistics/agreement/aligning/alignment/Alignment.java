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
package org.dkpro.statistics.agreement.aligning.alignment;

import static java.lang.String.join;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.dissimilarity.IDissimilarity;

public class Alignment
{
    private final Set<UnitaryAlignment> alignments;
    private final AnnotationSet annotationSet;

    public Alignment(Set<UnitaryAlignment> aAlignments, AnnotationSet aAnnotationSet)
    {
        if (aAnnotationSet.getRaterCount() == 1) {
            throw new IllegalArgumentException("An Alignment needs to have at least 2 annotators.");
        }

        var raters = aAnnotationSet.getRaters();

        var units = new HashSet<AlignableAnnotationUnit>();

        for (var alignment : aAlignments) {
            if (!raters.equals(alignment.getRaters())) {
                throw new IllegalArgumentException(
                        "Not all unitary alignments have the same set of annotors.");
            }

            for (var creator : raters) {
                var unit = alignment.getUnit(creator);
                if (unit != null) {
                    if (units.contains(unit)) {
                        throw new IllegalArgumentException(
                                "A unit is contained twice in the unitary alignments.");
                    }
                    else if (!aAnnotationSet.contains(unit)) {
                        throw new IllegalArgumentException(
                                "A unit is contained the unitary alignments but not in the annotation set.");
                    }
                    else {
                        units.add(unit);
                    }
                }
            }
        }

        if (!units.containsAll(aAnnotationSet.getUnits())) {
            throw new IllegalArgumentException(
                    "Not all units from the set are contained in the unitary alignments.");
        }

        alignments = aAlignments;
        annotationSet = aAnnotationSet;
    }

    public double getDisorder(IDissimilarity d)
    {
        double disorder = 0;

        for (var alignment : alignments) {
            disorder += alignment.getDisorder(d);
        }

        return disorder / annotationSet.getAverageNumberOfAnnotations();
    }

    @Override
    public String toString()
    {
        return toString(new ArrayList<String>());
    }

    public String toString(List<String> attributes)
    {
        var units_this = new ArrayList<UnitaryAlignment>(
                new TreeSet<UnitaryAlignment>(this.alignments));
        var uas = new ArrayList<String>(units_this.size());
        for (var ua : units_this) {
            uas.add(ua.toString(attributes));
        }

        return join("\n", uas);
    }
}
