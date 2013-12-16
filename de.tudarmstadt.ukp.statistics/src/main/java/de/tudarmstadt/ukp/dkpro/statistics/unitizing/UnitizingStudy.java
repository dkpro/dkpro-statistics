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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class holds units/section of several annotators and additional
 * information that is needed for calculating the agreement. Each 
 * annotation is stored as an Unit-Object. The class provides different
 * methods for accessing certain units of interest.   
 * 
 * <p>
 * <b>Note</b>: Before using the study, needs to be 'closed' using {@link UnitizingStudy#close()}
 * </p>
 * <p>
 * <b>Note 2</b>: The begin of the continuum of the study is set to 0 by default, but this can be changed using
 * {@link UnitizingStudy#setContinuumStart(int)}.
 * </p> 
 * 
 * @author Christian Stab
 * @author Roland Kluge
 */
public class UnitizingStudy
{

    /**
     * The start index of the continuum
     */
    private int continuumStart;

    /** 
     * Length of the continuum
     */
    private int contiuumLength;

    private boolean isClosed;

    private int annotatorCount;

    private Set<String> categories;
    private Map<AnnotatorAndCategory, List<Section>> annotatorAndCategoryToSections;

    /**
     * Basic constructor
     * @param annotators number of annotators
     */
    public UnitizingStudy(final int annotators)
    {
        this.contiuumLength = 0;
        this.continuumStart = 0;
        this.isClosed = false;
        this.annotatorCount = annotators;
        this.categories = new HashSet<String>();
        this.annotatorAndCategoryToSections = new HashMap<AnnotatorAndCategory, List<Section>>();
    }

    /**
     * This method finalizes the study.
     * 
     * After that, the study should not be changed anymore (do not add sections or modify l).
     */
    public void close()
    {
        final int continuumStart = this.getContinuumStart();
        final int continuumEnd = this.getContinuumEnd();
        final int continuumLength = this.getContinuumLength();

        for (final Entry<AnnotatorAndCategory, List<Section>> entry : this.annotatorAndCategoryToSections
                .entrySet()) {
            final int annotator = entry.getKey().getAnnotator();
            final String category = entry.getKey().getCategory();
            final List<Section> sections = entry.getValue();

            if (!sections.isEmpty()) {

                final Section lastSection = sections.get(sections.size() - 1);

                // Check whether the last section exceeds the contiuum length
                final int endOfLastSection = lastSection.getEnd();
                if (endOfLastSection > continuumEnd) {
                    throw new IllegalStateException(
                            String.format(
                                    "The last section for annotator %d and category %s ends at %d and exceeds the continuum's end at %d",
                                    annotator, category, endOfLastSection, continuumEnd));
                }

                // If there is a gap between the last section and the end of the continuum, fill it
                if (endOfLastSection < continuumEnd) {
                    final int finalGapLength = continuumEnd - endOfLastSection;
                    final Section finalGap = Section.createGap(category, annotator,
                            endOfLastSection, finalGapLength);
                    sections.add(finalGap);
                }
            }
            else {
                final Section finalGap = Section.createGap(category, annotator, continuumStart,
                        continuumLength);
                sections.add(finalGap);
            }

        }

        this.isClosed = true;
    }

    /**
     * Returns whether the study has been marked as complete.
     * 
     * @return whether the study 
     */
    public boolean isClosed()
    {
        return this.isClosed;
    }

    /**
     * Method for adding section to the study. Note that the sections
     * have to be ordered with respect to their occurrence
     * @param category category of the section
     * @param annotator the annotator of the unit
     * @param b the start unit of the section
     * @param l the length of the section
     * @param v indicates if the section is a gap (v=0) or a unit (v=1)
     * 
     * @deprecated Use {@link UnitizingStudy#addSection(String, int, int, int)}, which does not require the 'v' flag
     */
    @Deprecated
    public void addSection(final String category, final int annotator, final int b, final int l,
            final int v)
    {
        final Section section = new Section(category, annotator, b, l, v);
        this.categories.add(category);
        getSections(category, annotator).add(section);
    }

    /**
     * Method for adding section to the study. 
     * 
     * <b>Note</b> that the sections
     * have to be ordered by begin ('b' property).
     * 
     * @param category category of the section
     * @param annotator the annotator of the unit
     * @param begin the start unit of the section
     * @param l the length of the section
     * 
     */
    public void addSection(final String category, final int annotator, final int begin, final int l)
    {
        checkBeginIndex(begin);

        final List<Section> sections = getSections(category, annotator);
        if (sections.isEmpty()) {
            if (begin != 0) {
                final int gapLength = begin - this.continuumStart;
                sections.add(new Section(category, annotator, this.continuumStart, gapLength,
                        Section.GAP));
            }
        }
        else {
            final Section lastSection = sections.get(sections.size() - 1);
            final int endOfLastSection = lastSection.getBegin() + lastSection.getLength();
            if (begin > endOfLastSection) {
                final int gapLength = begin - endOfLastSection;
                sections.add(new Section(category, annotator, endOfLastSection, gapLength,
                        Section.GAP));
            }
        }

        final Section annotatedSection = new Section(category, annotator, begin, l,
                Section.ANNOTATED);
        this.categories.add(category);
        sections.add(annotatedSection);
    }

    private void checkBeginIndex(final int b)
    {
        if (b < this.continuumStart) {
            throw new IllegalStateException(String.format(
                    "Begin index %d is before the beginning of the continuum at %d", b,
                    this.continuumStart));
        }
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
     * Returns an list of sections for the given category and annotator.
     * The list is ordered with respect to the occurrence of the units.
     * @param category category of interest
     * @param annotator the annotator of the sections
     * @return list of sections (units)
     */
    public List<Section> getSections(final String category, final int annotator)
    {
        final AnnotatorAndCategory key = new AnnotatorAndCategory(annotator, category);
        if (!this.annotatorAndCategoryToSections.containsKey(key)) {
            this.annotatorAndCategoryToSections.put(key, new ArrayList<Section>());
        }

        return this.annotatorAndCategoryToSections.get(key);
    }

    /**
     * Returns a list of all categories included in the given study
     * @return list of all categories
     */
    public Collection<String> getCategories()
    {
        return this.categories;
    }

    /**
     * Returns all annotator indices
     * 
     * @return all annotator indices
     * 
     */
    public Collection<Integer> getAnnotators()
    {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < this.annotatorCount; ++i) {
            result.add(i);
        }
        return result;
    }

    public int getNumAnnotatedSections(final String category)
    {
        int nc = 0;
        for (int annotator = 0; annotator < this.getAnnotatorCount(); annotator++) {
            final List<Section> sections = this.getSections(category, annotator);
            for (int s = 0; s < sections.size(); s++) {
                if (sections.get(s).isAnnotated()) {
                    ++nc;
                }
            }
        }
        return nc;
    }

    /**
     * Returns the number of annotators
     * 
     * @return the number of annotators
     * 
     */
    public int getAnnotatorCount()
    {
        return this.annotatorCount;
    }

    /**
     * Returns the length of the continuum.
     * 
     * @return the length
     * 
     * @deprecated Use {@link UnitizingStudy#getContinuumLength}
     */
    @Deprecated
    public int getL()
    {
        return contiuumLength;
    }

    /**
     * Returns the length of the continuum.
     * 
     * @return the length of the continuum
     */
    public int getContinuumLength()
    {
        return this.contiuumLength;
    }

    /**
     * Set the length of the continuum
     * 
     * @param l the length
     * 
     * @deprecated Use {@link UnitizingStudy#setContinuumLength}
     */
    @Deprecated
    public void setL(final int l)
    {
        this.contiuumLength = l;
    }

    /**
     * Set the length of the continuum
     * 
     * @param length the length of the continuum
     */
    public void setContinuumLength(final int length)
    {
        this.contiuumLength = length;
    }

    /**
     * Returns the start of the continuum
     * 
     * @return the start of the continuum
     */
    public int getContinuumStart()
    {
        return this.continuumStart;
    }

    /**
     * Sets the start of the continuum
     * 
     * @param continuumStart the start index
     */
    public void setContinuumStart(final int continuumStart)
    {
        this.continuumStart = continuumStart;
    }

    /**
     * Returns the end index (exclusive) of the continuum
     * 
     * @return the end of the continuum
     */
    public int getContinuumEnd()
    {
        return this.getContinuumLength() + this.continuumStart;
    }

    /**
     * Holds a pair of annotator and category
     * 
     * @author Roland Kluge
     */
    private static class AnnotatorAndCategory
    {
        private String category;
        private int annotator;

        private AnnotatorAndCategory(final int annotator, final String category)
        {
            this.annotator = annotator;
            this.category = category;
        }

        private int getAnnotator()
        {
            return this.annotator;
        }

        private String getCategory()
        {
            return this.category;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + annotator;
            result = prime * result + ((category == null) ? 0 : category.hashCode());
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
            final AnnotatorAndCategory other = (AnnotatorAndCategory) obj;
            if (annotator != other.annotator) {
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
            return true;
        }

    }

    /**
     * This class holds all properties of a section.
     * @author Christian Stab
     */
    public static class Section
    {
        public static final int GAP = 0;
        public static final int ANNOTATED = 1;

        public String category;
        public int annotator;
        public int b;
        public int l;
        public int v;

        public Section()
        {
            // assigns default values
        }

        public Section(final String category, final int annotator, final int b, final int l,
                final int v)
        {
            this.category = category;
            this.annotator = annotator;
            this.b = b;
            this.l = l;
            this.v = v;
        }

        /**
         * Returns the index of the first covered item
         * @return 
         * 
         * @return the begin index
         */
        public int getBegin()
        {
            return this.b;
        }

        /**
         * Returns the end index of this section.
         * 
         * The end index is exclusive, i.e., it is the index of the last covered item plus one.
         * 
         * @return the end index
         */
        public int getEnd()
        {
            return this.b + this.l;
        }

        /**
         * Returns the length of this section
         * 
         * @return the length
         */
        public int getLength()
        {
            return this.l;
        }

        /**
         * Returns whether this sections is a gap.
         * 
         * @return whether this is a gap
         */
        public boolean isGap()
        {
            return GAP == this.v;
        }

        /**
         * Returns whether this sections is annotated.
         * 
         * @return whether this is annotated
         */
        public boolean isAnnotated()
        {
            return ANNOTATED == this.v;
        }

        /**
         * Creates a section with the given properties and v=1.
         * 
         * @param category
         * @param annotator
         * @param b
         * @param l
         * @return
         */
        public static Section createAnnotated(final String category, final int annotator,
                final int b, final int l)
        {
            return new Section(category, annotator, b, l, ANNOTATED);
        }

        /**
         * Creates a section with the given properties and v=0.
         * 
         * @param category
         * @param annotator
         * @param b
         * @param l
         * @return
         */
        public static Section createGap(final String category, final int annotator, final int b,
                final int l)
        {
            return new Section(category, annotator, b, l, GAP);
        }

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
            return "Section [" + (this.v == GAP ? "GAP" : "ANNOTATED") + " cat=" + category
                    + ", anno=" + annotator + ", b=" + b + ", l=" + l + "]";
        }

    }

}
