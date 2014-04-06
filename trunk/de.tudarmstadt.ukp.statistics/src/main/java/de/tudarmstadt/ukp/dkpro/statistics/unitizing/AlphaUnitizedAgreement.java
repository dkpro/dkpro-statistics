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

import de.tudarmstadt.ukp.dkpro.statistics.unitizing.UnitizingStudy.Section;

/**
 * Calculates the unitized alpha coefficient described in Krippendorff
 * (2004) "Measuring the Reliability of Qualitative Text Analysis Data" 
 * The agreement can either be calculated for a certain category or 
 * for all categories. The coefficient can be used for two or more coders.
 * 
 * @author Christian Stab
 */
public class AlphaUnitizedAgreement
{

    /** 
     * The study object containing the sections of each coder 
     */
    private UnitizingStudy study;

    /**
     * Basic constructor
     * @param study the study object containing the sections of each coder
     */
    public AlphaUnitizedAgreement(final UnitizingStudy study)
    {
        this.validateThatStudyIsClosed(study);

        this.study = study;
    }

    /**
     * Estimates the inter-annotator agreement of the given category  
     * @param category the category of interest
     * @return IAA for the given category
     */
    public double estimateCategoryAgreement(final String category)
    {
        return 1 - (this.getObservedDisagreement(category) / this.getExpectedDisagreement(category));
    }

    /**
     * Estimates the joint agreement for all categories given in the study
     * @return joint agreement for all given categories
     */
    public double estimateJointAgreement()
    {
        double enumerator = 0.0;
        double denominator = 0.0;
        for (final String category : this.study.getCategories()) {
            enumerator = enumerator + this.getObservedDisagreement(category);
            denominator = denominator + this.getExpectedDisagreement(category);
        }

        return 1 - (enumerator / denominator);
    }

    /**
     * Calculates the observed disagreement for the given category
     * @param category the category of interest
     * @return observed agreement of the given category
     */
    public double getObservedDisagreement(final String category)
    {
        double sum = 0.0;
        for (final int annotator1 : this.study.getAnnotators()) {
            for (final Section sectionByAnnotator1 : this.study.getSections(category, annotator1)) {
                for (final int annotator2 : this.study.getAnnotators()) {
                    if (annotator2 != annotator1) {
                        for (final Section sectionByAnnotator2 : this.study.getSections(category,
                                annotator2)) {

                            sum = sum + this.delta(sectionByAnnotator1, sectionByAnnotator2);
                        }
                    }
                }
            }
        }

        return sum / this.determineNormalizationTermForObservedDisagreement();
    }

    /**
     * Calculates the expected disagreement for the given category
     * @param category the category of interest
     * @return expected agreement of the given category
     */
    public double getExpectedDisagreement(final String category)
    {
        final double enumerator = this.calculateEnumeratorForExpectedDisagreement(category);
        final double denominator = this.calculateDenominatorForExpectedDisagreement(category);

        return enumerator / denominator;
    }

    private void validateThatStudyIsClosed(final UnitizingStudy study)
    {
        if (!study.isClosed()) {
            throw new IllegalArgumentException(
                    "The study needs to be closed before further processing!");
        }
    }

    /**
     * The delta-function for calculating the distance between two
     * units.  
     * @param u section of first annotator
     * @param v section of second annotator
     * @return distance between the units
     */
    private double delta(final Section u, final Section v)
    {
        if (!u.category.equals(v.category)) {
            return -1;
        }

        final int diffOfBeginIndices = u.begin - v.begin;

        if (u.isAnnotated == 1 && v.isAnnotated == 1 && (-u.length) < diffOfBeginIndices
                && diffOfBeginIndices < v.length) {
            return Math.pow(diffOfBeginIndices, 2) + Math.pow((u.getEnd() - v.getEnd()), 2);
        }

        if (u.isAnnotated == 1 && v.isAnnotated == 0 && (v.length - u.length) >= diffOfBeginIndices
                && diffOfBeginIndices >= 0) {
            return Math.pow(u.length, 2);
        }

        if (u.isAnnotated == 0 && v.isAnnotated == 1 && (v.length - u.length) <= diffOfBeginIndices
                && diffOfBeginIndices <= 0) {
            return Math.pow(v.length, 2);
        }

        return 0;
    }

    private double determineNormalizationTermForObservedDisagreement()
    {
        return this.study.getAnnotatorCount() * (this.study.getAnnotatorCount() - 1)
                * Math.pow(this.study.getContinuumLength(), 2);
    }

    private double calculateEnumeratorForExpectedDisagreement(final String category)
    {
        final int numberOfSectionsForCategory = this.study.getNumberOfAnnotatedSections(category);

        double enumerator = 0.0;
        for (final int annotator1 : this.study.getAnnotators()) {

            for (final Section sectionByAnnotator1 : this.study.getSections(category, annotator1)) {

                if (sectionByAnnotator1.isAnnotated()) {

                    final double tmp1 = ((numberOfSectionsForCategory - 1.0) / 3)
                            * (2 * Math.pow(sectionByAnnotator1.length, 3) - 3
                                    * Math.pow(sectionByAnnotator1.length, 2) + sectionByAnnotator1.length);

                    double tmp2 = 0.0;
                    for (final int annotator2 : this.study.getAnnotators()) {

                        for (final Section sectionByAnnotator2 : this.study.getSections(category,
                                annotator2)) {
                            if (sectionByAnnotator2.isGap()
                                    && sectionByAnnotator2.length >= sectionByAnnotator1.length) {
                                tmp2 += sectionByAnnotator2.length - sectionByAnnotator1.length + 1;
                            }
                        }

                    }

                    tmp2 *= Math.pow(sectionByAnnotator1.length, 2);
                    enumerator += tmp1 + tmp2;
                }
            }
        }

        enumerator = enumerator * (2.0 / this.study.getContinuumLength());

        return enumerator;
    }

    private double calculateDenominatorForExpectedDisagreement(final String category)
    {
        double denominator = 0.0;
        final double annoCount = this.study.getAnnotatorCount();
        denominator = 1.0 * (annoCount * this.study.getContinuumLength())
                * (annoCount * this.study.getContinuumLength() - 1);

        for (final int annotator1 : this.study.getAnnotators()) {
            for (final Section section : this.study.getSections(category, annotator1)) {

                if (section.isAnnotated()) {
                    denominator -= section.length * (section.length - 1);
                }

            }
        }

        return denominator;
    }

}
