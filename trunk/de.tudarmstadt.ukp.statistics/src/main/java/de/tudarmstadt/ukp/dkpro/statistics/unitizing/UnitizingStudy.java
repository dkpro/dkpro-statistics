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
import java.util.Set;

/**
 * A unitized annotation study holds free-span annotations by several annotators for several categories. 
 * Each annotation is stored as so-called section.   
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

    private int continuumStart;
    private int contiuumLength;

    private boolean isClosed;

    private Set<String> categories;
    private Map<AnnotatorAndCategory, List<Section>> annotatorAndCategoryToSections;

    private List<Integer> annotators;

    /**
     * Basic constructor
     * @param annotatorCount number of annotators
     */
    public UnitizingStudy(final int annotatorCount)
    {
        this.contiuumLength = 0;
        this.continuumStart = 0;
        this.isClosed = false;
        this.createAnnotatorsList(annotatorCount);

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
        for (final int annotator : this.getAnnotators()) {
            for (final String category : this.getCategories()) {
                this.closeForAnnotatorAndCategory(annotator, category);
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
     * Method for adding section to the study. 
     * 
     * <b>Note</b> that the sections
     * have to be ordered by begin ('b' property).
     * 
     * @param category category of the section
     * @param annotator the annotator of the unit
     * @param begin the start unit of the section
     * @param length the length of the section
     * 
     */
    public void addSection(final String category, final int annotator, final int begin,
            final int length)
    {
        this.validateBeginIndex(begin);

        final Section newSection = new Section(category, annotator, begin, length,
                Section.ANNOTATED);

        final List<Section> sections = this.getSections(category, annotator);
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
            if (newSection.begin > endOfLastSection) {
                final int gapLength = begin - endOfLastSection;
                sections.add(new Section(category, annotator, endOfLastSection, gapLength,
                        Section.GAP));
            }
        }

        this.categories.add(category);
        sections.add(newSection);
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
        return this.getSections(category, annotator).get(section);
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
     * Returns the number of sections for the given annotator and category
     * @param annotator
     * @param category
     * 
     * @return the number of annotated sections
     */
    public int getNumberOfSections(final int annotator, final String category)
    {
        return this.getSections(category, annotator).size();
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
     * Returns the number of annotators
     * 
     * @return the number of annotators
     * 
     */
    public int getAnnotatorCount()
    {
        return this.annotators.size();
    }

    /**
     * Returns all annotator indices
     * 
     * @return all annotator indices
     * 
     */
    public Collection<Integer> getAnnotators()
    {
        return this.annotators;
    }

    /**
     * Returns the number of annotated sections for this category
     * @param category
     * @return
     */
    public int getNumberOfAnnotatedSections(final String category)
    {
        int count = 0;
        for (final int annotator : this.getAnnotators()) {

            for (final Section section : this.getSections(category, annotator)) {
                if (section.isAnnotated()) {
                    ++count;
                }
            }

        }
        return count;
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
        return this.getContinuumLength() + this.getContinuumStart();
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
        this.getSections(category, annotator).add(section);
    }

    /**
     * Returns the length of the continuum.
     * 
     * @return the length
     * 
     * @deprecated Use {@link UnitizingStudy#getContinuumLength}. This methods will be removed in future versions.
     */
    @Deprecated
    public int getL()
    {
        return this.contiuumLength;
    }

    /**
     * Set the length of the continuum
     * 
     * @param l the length
     * 
     * @deprecated Use {@link UnitizingStudy#setContinuumLength}. This methods will be removed in future versions.
     */
    @Deprecated
    public void setL(final int l)
    {
        this.contiuumLength = l;
    }

    private void createAnnotatorsList(final int annotatorCount)
    {
        this.annotators = new ArrayList<Integer>();
        for (int a = 0; a < annotatorCount; ++a) {
            this.annotators.add(a);
        }
    }

    private void validateBeginIndex(final int beginIndex)
    {
        if (beginIndex < this.continuumStart) {
            throw new IllegalStateException(String.format(
                    "Begin index %d is before the beginning of the continuum at %d", beginIndex,
                    this.continuumStart));
        }
    }

    private void closeForAnnotatorAndCategory(final int annotator, final String category)
    {

        final int continuumStart = this.getContinuumStart();
        final int continuumEnd = this.getContinuumEnd();
        final int continuumLength = this.getContinuumLength();

        final List<Section> sections = this.getSections(category, annotator);

        if (!sections.isEmpty()) {

            final Section lastSection = sections.get(sections.size() - 1);

            this.validateThatSectionDoesNotExceedContinuum(lastSection, continuumEnd);

            // If there is a gap between the last section and the end of the continuum, fill
            // it
            final int endOfLastSection = lastSection.getEnd();
            if (endOfLastSection < continuumEnd) {
                final int finalGapLength = continuumEnd - endOfLastSection;
                final Section finalGap = Section.createGap(category, annotator, endOfLastSection,
                        finalGapLength);
                sections.add(finalGap);
            }
        }
        else {
            final Section finalGap = Section.createGap(category, annotator, continuumStart,
                    continuumLength);
            sections.add(finalGap);
        }

    }

    private void validateThatSectionDoesNotExceedContinuum(final Section lastSection,
            final int continuumEnd)
    {
        if (lastSection.getEnd() > continuumEnd) {
            throw new IllegalStateException(
                    String.format(
                            "The last section for annotator %d and category %s ends at %d and exceeds the continuum's end at %d",
                            lastSection.annotator, lastSection.category, lastSection.getEnd(),
                            continuumEnd));
        }
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
            result = prime * result + this.annotator;
            result = prime * result + ((this.category == null) ? 0 : this.category.hashCode());
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
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final AnnotatorAndCategory other = (AnnotatorAndCategory) obj;
            if (this.annotator != other.annotator) {
                return false;
            }
            if (this.category == null) {
                if (other.category != null) {
                    return false;
                }
            }
            else if (!this.category.equals(other.category)) {
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
        public int begin;
        public int length;
        public int isAnnotated;

        public Section()
        {
            // assigns default values
        }

        public Section(final String category, final int annotator, final int b, final int l,
                final int v)
        {
            this.category = category;
            this.annotator = annotator;
            this.begin = b;
            this.length = l;
            this.isAnnotated = v;
        }

        /**
         * Returns the index of the first covered item
         * @return 
         * 
         * @return the begin index
         */
        public int getBegin()
        {
            return this.begin;
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
            return this.begin + this.length;
        }

        /**
         * Returns the length of this section
         * 
         * @return the length
         */
        public int getLength()
        {
            return this.length;
        }

        /**
         * Returns whether this sections is a gap.
         * 
         * @return whether this is a gap
         */
        public boolean isGap()
        {
            return GAP == this.isAnnotated;
        }

        /**
         * Returns whether this sections is annotated.
         * 
         * @return whether this is annotated
         */
        public boolean isAnnotated()
        {
            return ANNOTATED == this.isAnnotated;
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
            result = prime * result + this.annotator;
            result = prime * result + this.begin;
            result = prime * result + ((this.category == null) ? 0 : this.category.hashCode());
            result = prime * result + this.length;
            result = prime * result + this.isAnnotated;
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
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Section other = (Section) obj;
            if (this.annotator != other.annotator) {
                return false;
            }
            if (this.begin != other.begin) {
                return false;
            }
            if (this.category == null) {
                if (other.category != null) {
                    return false;
                }
            }
            else if (!this.category.equals(other.category)) {
                return false;
            }
            if (this.length != other.length) {
                return false;
            }
            if (this.isAnnotated != other.isAnnotated) {
                return false;
            }
            return true;
        }

        @Override
        public String toString()
        {
            return "Section [" + (this.isAnnotated == GAP ? "GAP" : "ANNOTATED") + " cat=" + this.category
                    + ", anno=" + this.annotator + ", b=" + this.begin + ", l=" + this.length + "]";
        }

    }

}
