/*
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
 */
package org.dkpro.statistics.agreement.coding;

import java.util.ArrayList;
import java.util.List;

import org.dkpro.statistics.agreement.IAnnotationUnit;

/**
 * Default implementation of {@link ICodingAnnotationItem} holding the set of annotation units for
 * this item (i.e., the categories assigned to this item by all raters). When using the default
 * implementation, it is recommended to use {@link CodingAnnotationStudy#addItem(Object...)} instead
 * of instantiating this type.
 * 
 * @see CodingAnnotationStudy
 * @see ICodingAnnotationItem
 * @author Christian M. Meyer
 */
public class CodingAnnotationItem
    implements ICodingAnnotationItem
{
    private static final long serialVersionUID = 3447650373912260846L;
    
    protected List<IAnnotationUnit> units;
    protected int nonNullCount;

    /**
     * Initializes the item for the given number of units. Normally, the method
     * {@link CodingAnnotationStudy#addItem(Object...)} should be used to define the annotation
     * units of an item.
     */
    protected CodingAnnotationItem(int unitCount)
    {
        units = new ArrayList<IAnnotationUnit>(unitCount);
        nonNullCount = 0;
    }

    /**
     * Adds the given unit to the coding study. Normally, the method
     * {@link CodingAnnotationStudy#addItem(Object...)} should be used to define the annotation
     * units of an item.
     */
    protected void addUnit(final IAnnotationUnit unit)
    {
        int raterIdx = unit.getRaterIdx();
        if (raterIdx >= units.size()) {
            for (int i = units.size(); i < raterIdx; i++) {
                units.add(null);
            }
            units.add(unit);
        }
        else {
            units.set(raterIdx, unit);
        }

        if (unit.getCategory() != null) {
            nonNullCount++;
        }
    }

    @Override
    public IAnnotationUnit getUnit(int raterIdx)
    {
        return units.get(raterIdx);
    }

    @Override
    public Iterable<IAnnotationUnit> getUnits()
    {
        return units;
    }

    @Override
    public int getRaterCount()
    {
        return nonNullCount;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (IAnnotationUnit unit : units) {
            result.append(result.length() == 0 ? "" : ", ").append(unit.toString());
        }
        return result.toString();
    }
}
