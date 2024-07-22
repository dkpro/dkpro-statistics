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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UnitizingAnnotationStudy}.<br>
 * <br>
 * 
 * @author Christian M. Meyer
 */
public class UnitizingAnnotationStudyTest
{
    @Test
    public void testSortOrder()
    {
        UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(3, 7);
        study.addUnit(0, 1, 1, null);
        study.addUnit(3, 1, 1, null);
        study.addUnit(1, 1, 1, null);
        study.addUnit(2, 3, 3, null);
        study.addUnit(2, 5, 1, null);
        study.addUnit(2, 2, 1, null);
        study.addUnit(1, 1, 3, null);
        study.addUnit(1, 1, 2, null);
        study.addUnit(2, 4, 2, null);
        study.addUnit(3, 2, 1, null);

        Iterator<IUnitizingAnnotationUnit> iter = study.getUnits().iterator();
        assertAnnotationItem(0, 1, 1, iter.next());
        assertAnnotationItem(1, 1, 1, iter.next());
        assertAnnotationItem(1, 1, 2, iter.next());
        assertAnnotationItem(1, 1, 3, iter.next());
        assertAnnotationItem(2, 2, 1, iter.next());
        assertAnnotationItem(2, 3, 3, iter.next());
        assertAnnotationItem(2, 4, 2, iter.next());
        assertAnnotationItem(2, 5, 1, iter.next());
        assertAnnotationItem(3, 1, 1, iter.next());
        assertAnnotationItem(3, 2, 1, iter.next());
        assertThat(iter.hasNext()).isFalse();
    }

    protected static void assertAnnotationItem(int expectedOffset, int expectedLength,
            int expectedRaterIdx, final IUnitizingAnnotationUnit actual)
    {
        assertThat( actual.getOffset()).as(actual.toString()).isEqualTo(expectedOffset);
        assertThat( actual.getLength()).as(actual.toString()).isEqualTo(expectedLength);
        assertThat( actual.getRaterIdx()).as(actual.toString()).isEqualTo(expectedRaterIdx);
    }
}
