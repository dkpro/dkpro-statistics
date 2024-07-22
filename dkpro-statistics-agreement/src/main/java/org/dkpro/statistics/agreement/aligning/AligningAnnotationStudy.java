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
package org.dkpro.statistics.agreement.aligning;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dkpro.statistics.agreement.aligning.data.Rater;

/**
 * a set of units created by a given set of annotators; all units are linked to the same (implicit)
 * continuum
 */
public class AligningAnnotationStudy
    implements IAligningAnnotationStudy
{
    private static final long serialVersionUID = 1892754946013211747L;

    private final Set<Rater> raters = new TreeSet<Rater>();
    private final Set<AlignableAnnotationUnit> units = new TreeSet<AlignableAnnotationUnit>();
    private final Set<Object> categories = new HashSet<Object>();

    private List<AlignableAnnotationUnit> unitsListCache = null;

    public AligningAnnotationStudy()
    {
        // Nothing to do
    }

    public AligningAnnotationStudy(Collection<? extends AlignableAnnotationUnit> aUnits)
    {
        for (var unit : aUnits) {
            addUnit(unit);
        }
    }

    public void addUnits(Collection<? extends AlignableAnnotationUnit> aUnits) {
        for (var unit : aUnits) {
            addUnit(unit);
        }
    }

    public void addUnits(AlignableAnnotationUnit... aUnits) {
        for (var unit : aUnits) {
            addUnit(unit);
        }
    }

    public void addUnit(AlignableAnnotationUnit unit)
    {
        categories.add(unit.getCategory());

        raters.add(unit.getRater());
        units.add(unit);

        unitsListCache = null;
    }

    @Override
    public int getRaterCount()
    {
        return raters.size();
    }

    @Override
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

    @Override
    public int findRater(String aName)
    {
        return raters.stream() //
                .filter(a -> aName.equals(a.getName())) //
                .mapToInt(a -> a.getIndex()) //
                .findFirst().orElse(-1);
    }

    @Override
    public Iterable<Object> getCategories()
    {
        return unmodifiableCollection(categories);
    }

    @Override
    public int getCategoryCount()
    {
        return categories.size();
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

        AligningAnnotationStudy comp = (AligningAnnotationStudy) o;
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
