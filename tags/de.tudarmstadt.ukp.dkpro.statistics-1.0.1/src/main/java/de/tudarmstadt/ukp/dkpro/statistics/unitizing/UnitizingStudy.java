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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class holds units/section of several annotators and additional
 * information that is needed for calculating the agreement. Each 
 * annotation is stored as an Unit-Object. The class provides different
 * methods for accessing certain units of interest.   
 * 
 * @author Christian Stab
 */
public class UnitizingStudy
{

    /** 
     * The units of multiple annotators 
     */
    private List<List<Section>> sections;

    /** 
     * Length of the continuum
     */
    private int l;

    /**
     * Basic constructor
     * @param annotators number of annotators
     */
    public UnitizingStudy(final int annotators)
    {
        sections = new ArrayList<List<Section>>();
        for (int i = 0; i < annotators; i++) {
            sections.add(new ArrayList<Section>());
        }
    }

    /**
     * Method for adding section to the study. Note that the sections
     * have to be ordered with respect to their occurrence
     * @param category category of the section
     * @param annotator the annotator of the unit
     * @param b the start unit of the section
     * @param l the length of the section
     * @param v indicates if the section is a gap (v=0) or a unit (v=1)
     */
    public void addSection(final String category, final int annotator, final int b, final int l,
            final int v)
    {
        final Section u = new Section();
        u.category = category;
        u.annotator = annotator;
        u.b = b;
        u.l = l;
        u.v = v;
        sections.get(annotator).add(u);
    }

    /**
     * Returns the section of a given category, annotator and section index
     * @param category category of interest
     * @param annotator annotator of interest
     * @param section the section index
     * @return unit
     */
    public Section getSection(final String category, final int annotator, final int section)
    {
        return getSections(category, annotator).get(section);
    }

    /**
     * Returns an list of sections for the given category and annotor.
     * The list is ordered with respect to the occurrence of the units.
     * @param category category of interest
     * @param annotator the annotator of the sections
     * @return list of sections (units)
     */
    public List<Section> getSections(final String category, final int annotator)
    {
        final LinkedList<Section> result = new LinkedList<Section>();

        for (final Section u : sections.get(annotator)) {
            if (u.category.equals(category)) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Returns all sections of all annotators
     * @return
     */
    public List<List<Section>> getSections()
    {
        return sections;
    }

    /**
     * Returns a list of all categories included in the given study
     * @return list of all categories
     */
    public List<String> getCategories()
    {
        final List<String> result = new LinkedList<String>();
        for (final List<Section> list : sections) {
            for (final Section u : list) {
                if (!result.contains(u.category)) {
                    result.add(u.category);
                }
            }
        }
        return result;
    }

    public int getAnnotators()
    {
        return sections.size();
    }

    public int getL()
    {
        return l;
    }

    public void setL(final int l)
    {
        this.l = l;
    }

    /**
     * This class holds all properties of a section.
     * @author Christian Stab
     */
    public static class Section
    {
        public String category;
        public int annotator;
        public int b;
        public int l;
        public int v;

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + annotator;
            result = prime * result + b;
            result = prime * result + ((category == null) ? 0 : category.hashCode());
            result = prime * result + l;
            result = prime * result + v;
            return result;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Section other = (Section) obj;
            if (annotator != other.annotator) {
                return false;
            }
            if (b != other.b) {
                return false;
            }
            if (category == null) {
                if (other.category != null) {
                    return false;
                }
            }
            else if (!category.equals(other.category)) {
                return false;
            }
            if (l != other.l) {
                return false;
            }
            if (v != other.v) {
                return false;
            }
            return true;
        }

        @Override
        public String toString()
        {
            return "Section [category=" + category + ", annotator=" + annotator + ", b=" + b
                    + ", l=" + l + ", v=" + v + "]";
        }

    }

}
