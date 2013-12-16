/*******************************************************************************
 * Copyright 2013
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
package de.tudarmstadt.ukp.dkpro.statistics.unitizing;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.statistics.unitizing.UnitizingStudy.Section;

/**
 * Unit tests for {@link UnitizingStudy}
 * @author Roland Kluge
 *
 */
public class UnitizingStudyTest
{
    private static final int ANNOTATOR0 = 0;
    private static final String CATEGORY0 = "cat0";

    private UnitizingStudy study;

    @Before
    public void setUp()
    {
        this.study = new UnitizingStudy(1);
    }

    @Test
    public void testAddSection1()
        throws Exception
    {
        study.addSection(CATEGORY0, ANNOTATOR0, 0, 1);

        final Section annotated = Section.createAnnotated(CATEGORY0, ANNOTATOR0, 0, 1);
        Assert.assertEquals(Arrays.asList(annotated), study.getSections(CATEGORY0, ANNOTATOR0));
    }

    /**
     * When inserting a section that does not start at b=0, we expect the study to
     * insert a gap section.
     */
    @Test
    public void testAddSection2()
        throws Exception
    {
        study.addSection(CATEGORY0, ANNOTATOR0, 1, 1);

        final Section gap = Section.createGap(CATEGORY0, ANNOTATOR0, 0, 1);
        final Section annotated = Section.createAnnotated(CATEGORY0, ANNOTATOR0, 1, 1);
        Assert.assertEquals(Arrays.asList(gap, annotated), study.getSections(CATEGORY0, ANNOTATOR0));
    }

    /**
     * When inserting two sections that leave a gap in between, we
     * expect the study to insert a gap section between the annotated sections. 
     */
    @Test
    public void testAddSection3()
        throws Exception
    {
        study.addSection(CATEGORY0, ANNOTATOR0, 0, 1);
        // Gap: b=1, l=1
        study.addSection(CATEGORY0, ANNOTATOR0, 2, 1);

        final Section annotated1 = Section.createAnnotated(CATEGORY0, ANNOTATOR0, 0, 1);
        final Section gap = Section.createGap(CATEGORY0, ANNOTATOR0, 1, 1);
        final Section annotated2 = Section.createAnnotated(CATEGORY0, ANNOTATOR0, 2, 1);
        Assert.assertEquals(Arrays.asList(annotated1, gap, annotated2),
                study.getSections(CATEGORY0, ANNOTATOR0));
    }

    @Test
    public void testClose1()
        throws Exception
    {

        study.close();
        Assert.assertTrue(study.isClosed());
    }

    /**
     * If the length of the continuum has not been set appropriately, we expect an exception.
     * 
     * The default continuum length is 0, so the section with an end at 1 will cause an exception.
     */
    @Test(expected = Exception.class)
    public void testClose2()
        throws Exception
    {
        study.addSection(CATEGORY0, ANNOTATOR0, 1, 1);

        Assert.assertEquals(0, study.getContinuumLength());
        study.close();
    }

    /**
     * There is a gap between the last section (ending at 1) and the continuum's end at 2.
     */
    @Test
    public void testClose3()
        throws Exception
    {
        study.addSection(CATEGORY0, ANNOTATOR0, 0, 1);
        // Final gap: b=1, l=1
        study.setContinuumLength(2);

        study.close();
        final Section annotated = Section.createAnnotated(CATEGORY0, ANNOTATOR0, 0, 1);
        final Section gap = Section.createGap(CATEGORY0, ANNOTATOR0, 1, 1);
        Assert.assertEquals(Arrays.asList(annotated, gap), study.getSections(CATEGORY0, ANNOTATOR0));
    }

}
