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

import org.dkpro.statistics.agreement.IAnnotationUnit;

/**
 * Extension of the {@link IAnnotationUnit} interface for representing the annotation units of
 * {@link IUnitizingAnnotationStudy}s. That is, an annotation unit that models the position of the
 * unit within the continuum of an annotation study and the category assigned to this unit by a
 * certain rater. Implement this interface when measuring inter-rater agreement using a
 * {@link IUnitizingAgreementMeasure}.
 * 
 * @see IAnnotationUnit
 * @see IUnitizingAgreementMeasure
 * @see IUnitizingAnnotationStudy
 * @author Christian M. Meyer
 */
public interface IUnitizingAnnotationUnit
    extends IAnnotationUnit, Comparable<IUnitizingAnnotationUnit>
{
    /**
     * @return the offset of the annotation unit (i.e., the start position of the identified
     *         segment).
     * @deprecated Use {@link #getBegin()}
     */
    @Deprecated
    default long getOffset()
    {
        return getBegin();
    }

    /**
     * @return the length of the annotation unit (i.e., the difference between the end and start
     *         position of the identified segment).
     */
    default long getLength()
    {
        return getEnd() - getBegin();
    }

    /**
     * @return the right delimiter of the annotation unit (i.e., the end position of the identified
     *         segment). The method is a shorthand for {@link #getOffset()} + {@link #getLength()}.
     * @deprecated Use {@link #getEnd()}
     */
    @Deprecated
    default long getEndOffset()
    {
        return getEnd();
    }

    /**
     * @return the offset of the annotation unit (i.e., the start position of the identified
     *         segment).
     */
    long getBegin();

    /**
     * @return the right delimiter of the annotation unit (i.e., the end position of the identified
     *         segment). The method is a shorthand for {@link #getOffset()} + {@link #getLength()}.
     */
    long getEnd();
}
