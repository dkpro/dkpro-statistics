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

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.IDissimilarity;

public class UnitaryAlignment
    implements Comparable<UnitaryAlignment>
{
    private final long begin;
    private final long end;

    private final Map<Rater, AlignableAnnotationUnit> units;

    public UnitaryAlignment(Collection<AlignableAnnotationUnit> aUnits, Set<Rater> aAnnotators)
    {
        var b = MAX_VALUE;
        var e = MIN_VALUE;

        units = new HashMap<Rater, AlignableAnnotationUnit>();
        for (var unit : aUnits) {
            if (units.containsKey(unit.getRater())) {
                throw new IllegalArgumentException(
                        "Unitary alignment may not contain two units from the same creator");
            }
            if (!aAnnotators.contains(unit.getRater())) {
                throw new IllegalArgumentException(
                        "Unitary alignment may only contain units created by annotators from the given set.");
            }

            if (unit.getBegin() < b) {
                b = unit.getBegin();
            }
            if (unit.getEnd() > e) {
                e = unit.getEnd();
            }

            units.put(unit.getRater(), unit);
        }

        // add empty elements
        for (Rater annot : aAnnotators) {
            if (!units.containsKey(annot)) {
                units.put(annot, null);
            }
        }

        begin = b;
        end = e;
    }

    public int arity()
    {
        return units.size();
    }

    public long getBegin()
    {
        return this.begin;
    }

    public long getEnd()
    {
        return this.end;
    }

    public Set<Rater> getRaters()
    {
        return this.units.keySet();
    }

    public AlignableAnnotationUnit getUnit(Rater creator)
    {
        return units.get(creator);
    }

    public double getDisorder(IDissimilarity d)
    {
        double dissim = 0;

        AlignableAnnotationUnit[] annots = units.values().toArray(new AlignableAnnotationUnit[0]);

        for (int i = 0; i < annots.length; i++) {
            for (int j = i + 1; j < annots.length; j++) {
                dissim += d.dissimilarity(annots[i], annots[j]);
            }
        }

        return dissim / (double) CombinatoricsUtils.binomialCoefficient(this.arity(), 2);

    }

    @Override
    public int hashCode()
    {
        return this.units.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        UnitaryAlignment comp = (UnitaryAlignment) o;

        if (this.arity() != comp.arity()) {
            return false;
        }

        if (!Objects.equals(this.getRaters(), comp.getRaters())) {
            return false;
        }

        for (Rater creator : this.getRaters()) {
            if (!Objects.equals(this.getUnit(creator), comp.getUnit(creator))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int compareTo(UnitaryAlignment ua)
    {

        if (this.equals(ua)) {
            return 0;
        }

        if (ua == null) {
            return -1;
        }

        // order by arity
        if (this.arity() != ua.arity()) {
            return Integer.compare(this.arity(), ua.arity());
        }

        // order by annotator sets if they are not equal
        if (!Objects.equals(this.getRaters(), ua.getRaters())) {

            // size of the sets are equal (due to equal arity)
            // order by the first differing Annotator

            List<Rater> annot_this = new ArrayList<Rater>(new TreeSet<Rater>(this.getRaters()));
            List<Rater> annot_comp = new ArrayList<Rater>(new TreeSet<Rater>(ua.getRaters()));

            for (int i = 0; i < annot_this.size(); i++) {
                if (!Objects.equals(annot_this.get(i), annot_comp.get(i))) {
                    return annot_this.get(i).compareTo(annot_comp.get(i));
                }
            }
        }

        // order by the span covered by the units in this alignment
        if (this.getBegin() != ua.getBegin()) {
            return Long.compare(this.getBegin(), ua.getBegin());
        }
        if (this.getEnd() != ua.getEnd()) {
            return Long.compare(this.getEnd(), ua.getEnd());
        }

        // the numbers of units and the span covered are the same
        // annotations must differ
        // order by the first differing annotation
        for (Rater creator : new ArrayList<Rater>(new TreeSet<Rater>(this.getRaters()))) {
            if (!Objects.equals(this.getUnit(creator), ua.getUnit(creator))) {
                if (this.getUnit(creator) == null) {
                    return 1;
                }
                else {
                    return this.getUnit(creator).compareTo(ua.getUnit(creator));
                }
            }
        }

        // should never be reached
        return 0;
    }

    @Override
    public String toString()
    {
        return this.toString(new ArrayList<String>());
    }

    public String toString(List<String> attributes)
    {
        var us = new ArrayList<String>(this.arity());

        var empty = new StringBuilder();
        empty.append("--");
        for (int i = 0; i < attributes.size(); i++) {
            empty.append("\t--");
        }
        String empty_unit = empty.toString();

        for (var creator : new ArrayList<Rater>(new TreeSet<Rater>(getRaters()))) {
            if (this.getUnit(creator) != null) {
                us.add(this.getUnit(creator).toString(attributes));
            }
            else {
                us.add(empty_unit);
            }
        }

        return String.join("\t", us);
    }
}
