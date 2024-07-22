/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.statistics.agreement.aligning;

import org.dkpro.statistics.agreement.IAnnotationUnit;

/**
 * Extension of the {@link IAnnotationUnit} interface for representing the annotation units of
 * {@link IAligningAnnotationStudy}s. That is, an annotation unit that models the position of the
 * unit within the continuum of an annotation study and the type and attributes assigned to this
 * unit by a certain rater. Implement this interface when measuring inter-rater agreement using a
 * {@link IAligningAgreementMeasure}.
 * 
 * @see IAnnotationUnit
 * @see IAligningAgreementMeasure
 * @see IAligningAnnotationStudy
 * @author Christian M. Meyer
 * @author Richard Eckart de Castilho
 */
public interface IAlignableAnnotationUnit
    extends IAnnotationUnit, Comparable<IAlignableAnnotationUnit>
{
    static final String NO_TYPE = "";

    /**
     * @return the length of the annotation unit (i.e., the difference between the end and start
     *         position of the identified segment).
     */
    default long getLength()
    {
        return getEnd() - getBegin();
    }

    /**
     * @return the offset of the annotation unit (i.e., the start position of the identified
     *         segment).
     */
    long getBegin();

    /**
     * @return the right delimiter of the annotation unit (i.e., the end position of the identified
     *         segment).
     */
    long getEnd();
}
