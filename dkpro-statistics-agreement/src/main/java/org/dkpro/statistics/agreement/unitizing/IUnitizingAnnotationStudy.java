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
package org.dkpro.statistics.agreement.unitizing;

import java.util.Collection;

import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Data model for unitizing studies. Unitizing studies are characterized by a continuum in which a
 * number of (human) raters are asked to identify the boundaries of annotation units and optionally
 * code the units with a certain category. The continuum is represented on a sampled, discretized
 * integer scale starting with 0 (unless otherwise specified). Note that this interface allows
 * reusing existing data representations by implementing the required methods. Use
 * {@link UnitizingAnnotationStudy} as a default implementation if no existing data representation
 * exists. The default implementation facilitates adding the individual units.
 * 
 * @see UnitizingAnnotationStudy
 * @see IUnitizingAnnotationUnit
 * @author Christian M. Meyer
 */
public interface IUnitizingAnnotationStudy
    extends IAnnotationStudy
{

    // -- Units --

    /** Allows iterating all annotation units of this study. */
    Collection<IUnitizingAnnotationUnit> getUnits();

    // -- Continuum --

    // @Deprecated
    // public int getSectionCount();
    //
    // @Deprecated
    // public Iterable<Integer> getSectionBoundaries();

    /**
     * Returns the begin of the continuum (i.e., the first offset that is considered valid for
     * annotation units).
     */
    long getContinuumBegin();

    /**
     * Returns the length of the continuum (i.e., the last possible right delimiter of an annotation
     * unit).
     */
    long getContinuumLength();

    /**
     * Returns the number of units rated by the given rater.
     */
    long getUnitCount(int aRaterIdx);

    // TODO: public void addSectionBoundary(long position);

}
