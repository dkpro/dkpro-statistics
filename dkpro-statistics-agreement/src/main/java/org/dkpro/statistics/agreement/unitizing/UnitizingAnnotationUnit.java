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

import org.dkpro.statistics.agreement.AnnotationUnit;

/**
 * Default implementation of {@link IUnitizingAnnotationUnit} holding the rater's index and the
 * category that the rater assigned to this unit as well as the position of the annotation unit
 * within the continuum of an annotation study. When using the default implementation, it is
 * recommended to use {@link UnitizingAnnotationStudy#addUnit(long, long, int, Object)} instead of
 * instantiating this type.
 * 
 * @see UnitizingAnnotationStudy
 * @see IUnitizingAnnotationUnit
 * @author Christian M. Meyer
 */
public class UnitizingAnnotationUnit
    extends AnnotationUnit
    implements IUnitizingAnnotationUnit
{
    private static final long serialVersionUID = -6716379300031576772L;

    final protected long offset;
    final protected long length;

    /**
     * Initializes the unit with the given offset and length as well as the category assigned to the
     * unit by the rater with the specified index. Normally, the method
     * {@link UnitizingAnnotationStudy#addUnit(long, long, int, Object)} should be used to define
     * the annotation units.
     */
    protected UnitizingAnnotationUnit(long aOffset, long aLength, int aRaterIdx,
            final Object aCategory)
    {
        super(aRaterIdx, aCategory);
        offset = aOffset;
        length = aLength;
    }

    @Override
    public long getOffset()
    {
        return offset;
    }

    @Override
    public long getLength()
    {
        return length;
    }

    @Override
    public long getEndOffset()
    {
        return offset + length;
    }

    public long getBegin()
    {
        return getOffset();
    }

    public long getEnd()
    {
        return getEndOffset();
    }

    @Override
    public int compareTo(final IUnitizingAnnotationUnit that)
    {
        if (this.equals(that)) {
            return 0;
        }

        if (this.offset < that.getOffset()) {
            return -1;
        }
        if (this.offset > that.getOffset()) {
            return +1;
        }

        if (this.length < that.getLength()) {
            return -1;
        }
        if (this.length > that.getLength()) {
            return +1;
        }

        if (this.raterIdx < that.getRaterIdx()) {
            return -1;
        }
        if (this.raterIdx > that.getRaterIdx()) {
            return +1;
        }

        return (this.hashCode() < that.hashCode() ? -1 : +1);
    }

    @Override
    public String toString()
    {
        return raterIdx + "<" + category + ">" + ((offset >= 0) ? "@" + offset : "")
                + ((length > 0) ? "-" + length : "");
    }

}
