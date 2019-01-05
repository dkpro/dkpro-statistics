/*******************************************************************************
 * Copyright 2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.dkpro.statistics.agreement.unitizing;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.dkpro.statistics.agreement.AnnotationStudy;

/**
 * Default implementation of the {@link IUnitizingAnnotationStudy} interface.
 * Instantiate this class for representing the annotation units of a
 * unitizing study (i.e., an annotation setup in which the (human) raters
 * are asked to identify the boundaries of the annotation units themselves).
 * The standard way of representing the annotation units is using the
 * {@link #addUnit(long, long, int, Object)} method for each identified
 * segment by each rater.
 * @see IUnitizingAnnotationStudy
 * @see IUnitizingAnnotationUnit
 * @author Christian M. Meyer
 */
public class UnitizingAnnotationStudy extends AnnotationStudy
        implements IUnitizingAnnotationStudy {

    protected Set<IUnitizingAnnotationUnit> units;
    protected long begin;
    protected long length;
//    protected Set<Integer> sections;

    /** Initializes and empty annotation study for a unitizing task with the
     *  given number of raters. The basic setup of a unitizing study is
     *  identifying units within a given continuum. The continuum is initialized
     *  to start at position 0 and end at the given length. */
    public UnitizingAnnotationStudy(int raterCount, int length) {
        this(raterCount, 0, length);
    }

    /** Initializes and empty annotation study for a unitizing task with the
     *  given number of raters. The basic setup of a unitizing study is
     *  identifying units within a given continuum. The continuum is initialized
     *  to start at the given position and end after the specified length
     *  (i.e., at position start + length). */
    public UnitizingAnnotationStudy(int raterCount, long begin, long length) {
//        this.sections = new TreeSet<Integer>();
        units = new TreeSet<IUnitizingAnnotationUnit>();
        for (int raterIdx = 0; raterIdx < raterCount; raterIdx++) {
            addRater(Integer.toString(raterIdx));
        }
        this.begin = begin;
        this.length = length;
    }

    /** Add the given annotation unit to this study. The specified unit
     *  should never be null. When relying on the default implementation
     *  {@link UnitizingAnnotationUnit}, it is recommended to use
     *  {@link #addUnit(long, long, int, Object)} instead. */
    public void addUnit(final IUnitizingAnnotationUnit unit) {
        units.add(unit);
    }

    /** Creates a new {@link UnitizingAnnotationUnit} and adds it to the
     *  present annotation study. The parameters define the position
     *  of the unit within the continuum (indicated by the offset/start position
     *  and the length of the unit), the rater who identified this unit and
     *  the category assigned to it. If the unitzing study does not
     *  distinguish any categories, use some dummy object, such as "X"
     *  to distinguish identified units from gaps (i.e., units whose
     *  category is null). */
    public IUnitizingAnnotationUnit addUnit(long offset, long length,
            int raterIdx, final Object category) {
        IUnitizingAnnotationUnit unit = createUnit(offset, length,
                raterIdx, category);
        units.add(unit);
        return unit;
    }

    protected IUnitizingAnnotationUnit createUnit(long offset, long length,
            int raterIdx, final Object category) {
        IUnitizingAnnotationUnit result = new UnitizingAnnotationUnit(offset, length, raterIdx, category);
        if (result.getCategory() != null) {
            categories.add(result.getCategory());
        }
//        sections.add(offset);
//        sections.add(offset + length);
        return result;
    }

    @Override
    public Iterable<IUnitizingAnnotationUnit> getUnits() {
        return units;
    }

    @Override
    public int getUnitCount() {
        return units.size();
    }

//    public int getItemCount() {
//        return items.size();
//    }

    /** Utility method for moving on the cursor of the given iterator until
     *  a unit of the specified rater is returned. */
    public static IUnitizingAnnotationUnit findNextUnit(
            final Iterator<IUnitizingAnnotationUnit> units, int raterIdx) {
        return findNextUnit(units, raterIdx, null);
    }

    /** Utility method for moving on the cursor of the given iterator until
     *  a unit of the specified category is returned. */
    public static IUnitizingAnnotationUnit findNextUnit(
            final Iterator<IUnitizingAnnotationUnit> units, final Object category) {
        return findNextUnit(units, -1, category);
    }

    /** Utility method for moving on the cursor of the given iterator until
     *  a unit of the specified rater and category is returned. Both
     *  the rater index and the category may be null if those filter
     *  conditions are to be ignored. */
    public static IUnitizingAnnotationUnit findNextUnit(
            final Iterator<IUnitizingAnnotationUnit> units, int raterIdx,
            final Object category) {
        while (units.hasNext()) {
            IUnitizingAnnotationUnit result = units.next();
            if (category != null && !category.equals(result.getCategory())) {
                continue;
            }
            if (raterIdx < 0 || result.getRaterIdx() == raterIdx) {
                return result;
            }
        }
        return null;
    }

    /*@Deprecated
    public int getSectionCount() {
        return sections.size();
    }

    @Deprecated
    public Iterable<Integer> getSectionBoundaries() {
        return sections;
    }*/

    @Override
    public long getContinuumBegin() {
        return begin;
    }

    @Override
    public long getContinuumLength() {
        return length;
    }

//TODO    public ICodingAnnotationStudy digitize();

    /*public ICodingAnnotationStudy digitize() {
        CodingAnnotationStudy result = new CodingAnnotationStudy(raters.size());
        for (int i = begin; i < begin + length; i++)
            result.addItem(annotations)
        return result;
    }*/

}
