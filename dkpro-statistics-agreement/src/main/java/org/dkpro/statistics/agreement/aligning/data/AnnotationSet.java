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
package org.dkpro.statistics.agreement.aligning.data;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

/**
 * a set of units created by a given set of annotators; all units are linked to the same (implicit)
 * continuum
 */
public class AnnotationSet
{
    private final Set<Rater> raters = new TreeSet<Rater>();
    private final Set<AlignableAnnotationUnit> units = new TreeSet<AlignableAnnotationUnit>();
    private final Set<String> featureNames = new HashSet<String>();
    private final Set<Object> categories = new HashSet<Object>();

    private List<AlignableAnnotationUnit> unitsListCache = null;

    protected long lowestOffset = MAX_VALUE;
    protected long highestOffset = 0;

    public AnnotationSet(Collection<? extends AlignableAnnotationUnit> aUnits)
    {
        for (var unit : aUnits) {
            addUnit(unit);
        }
    }

    public void addUnit(AlignableAnnotationUnit unit)
    {
        if (unit.getBegin() < lowestOffset) {
            lowestOffset = unit.getBegin();
        }

        if (unit.getEnd() > highestOffset) {
            highestOffset = unit.getEnd();
        }

        categories.add(unit.getCategory());
        featureNames.addAll(unit.getFeatureNames());

        raters.add(unit.getRater());
        units.add(unit);

        unitsListCache = null;
    }

    public int getRaterCount()
    {
        return raters.size();
    }

    public int getUnitCount()
    {
        return units.size();
    }

    public double getAverageNumberOfAnnotations()
    {
        return getUnitCount() / ((double) getRaterCount());
    }

    public Set<Rater> getRaters()
    {
        return unmodifiableSet(raters);
    }

    public List<AlignableAnnotationUnit> getUnits()
    {
        if (unitsListCache == null) {
            unitsListCache = unmodifiableList(
                    asList(units.toArray(AlignableAnnotationUnit[]::new)));
        }

        return unitsListCache;
    }

    public String[] getFeatureNames()
    {
        return featureNames.toArray(new String[0]);
    }

    public List<AlignableAnnotationUnit> getUnitsWithType(String aType)
    {
        return units.stream() //
                .filter(annot -> Objects.equals(annot.getType(), aType)) //
                .toList();
    }

    public List<AlignableAnnotationUnit> getUnitsWithRater(Rater aRater)
    {
        return units.stream() //
                .filter(annot -> Objects.equals(annot.getRater(), aRater)) //
                .toList();
    }

    public boolean contains(AlignableAnnotationUnit u)
    {
        return units.contains(u);
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

        AnnotationSet comp = (AnnotationSet) o;
        boolean no_diff = true;

        var unitArray = getUnits().toArray(AlignableAnnotationUnit[]::new);
        var otherUnitArray = comp.getUnits().toArray(AlignableAnnotationUnit[]::new);
        for (int i = 0; i < unitArray.length; i++) {
            no_diff = no_diff && unitArray[i].equals(otherUnitArray[i]);
        }

        return no_diff;
    }

    @Override
    public int hashCode()
    {
        return units.hashCode() + raters.hashCode();
    }
}
