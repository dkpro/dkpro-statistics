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

import java.util.Collection;
import java.util.List;

import de.tudarmstadt.ukp.dkpro.statistics.unitizing.UnitizingStudy.Section;

/**
 * Calculates the unitized alpha coefficient described in Krippendorf
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
        if (!study.isClosed()) {
            throw new IllegalArgumentException(
                    "The study needs to be closed before further processing!");
        }

        this.study = study;
    }

    /**
     * Estimates the inter-annotator agreement of the given category  
     * @param category the category of interest
     * @return IAA for the given category
     */
    public double estimateCategoryAgreement(final String category)
    {
        return 1 - (getObservedDisagreement(category) / getExpectedDisagreement(category));
    }

    /**
     * Estimates the joint agreement for all categories given in the study
     * @return joint agreement for all given categories
     */
    public double estimateJointAgreement()
    {
        final Collection<String> categories = study.getCategories();
        double enumerator = 0.0;
        double denominator = 0.0;
        for (final String category : categories) {
            enumerator = enumerator + getObservedDisagreement(category);
            denominator = denominator + getExpectedDisagreement(category);
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
        for (int i = 0; i < study.getAnnotatorCount(); i++) {
            for (int g = 0; g < study.getSections(category, i).size(); g++) {
                for (int j = 0; j < study.getAnnotatorCount(); j++) {
                    if (j != i) {
                        for (int h = 0; h < study.getSections(category, j).size(); h++) {
                            final Section u = study.getSection(category, i, g);
                            final Section v = study.getSection(category, j, h);

                            sum = sum + delta(u, v);
                        }
                    }
                }
            }
        }

        return sum
                / (study.getAnnotatorCount() * (study.getAnnotatorCount() - 1) * Math.pow(
                        study.getContinuumLength(), 2));
    }

    /**
     * Calculates the expected disagreement for the given category
     * @param category the category of interest
     * @return expected agreement of the given category
     */
    public double getExpectedDisagreement(final String category)
    {
        // calculate number of units of category c
        final int nc = study.getNumAnnotatedSections(category);

        // calculate enumerator
        double enumerator = 0.0;
        for (int i = 0; i < study.getAnnotatorCount(); i++) {

            final List<Section> outerSections = study.getSections(category, i);
            for (int g = 0; g < outerSections.size(); g++) {

                final Section cig = outerSections.get(g);
                if (cig.isAnnotated()) {
                    double tmp = ((nc - 1.0) / 3)
                            * (2 * Math.pow(cig.l, 3) - 3 * Math.pow(cig.l, 2) + cig.l);

                    double tmp2 = 0.0;
                    for (int j = 0; j < study.getAnnotatorCount(); j++) {

                        final List<Section> innerSections = study.getSections(category, j);
                        for (int h = 0; h < innerSections.size(); h++) {
                            final Section cjh = innerSections.get(h);
                            if (cjh.isGap() && cjh.l >= cig.l) {
                                tmp2 += cjh.l - cig.l + 1;
                            }
                        }

                    }

                    tmp2 *= Math.pow(cig.l, 2);
                    tmp += tmp2;
                    enumerator = enumerator + tmp;
                }
            }
        }

        enumerator = enumerator * (2.0 / study.getContinuumLength());

        // calculate denominator
        double denominator = 0.0;
        denominator = (study.getAnnotatorCount() * study.getContinuumLength())
                * (study.getAnnotatorCount() * study.getContinuumLength() - 1);
        double tmp = 0.0;
        for (int i = 0; i < study.getAnnotatorCount(); i++) {
            final List<Section> sections = study.getSections(category, i);
            for (int g = 0; g < sections.size(); g++) {

                final Section cig = sections.get(g);
                if (cig.isAnnotated()) {
                    tmp += cig.l * (cig.l - 1);
                }

            }
        }

        denominator = denominator - tmp;

        return enumerator / denominator;
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

        final int beginOffset = u.b - v.b;

        if (u.v == 1 && v.v == 1 && (-u.l) < beginOffset && beginOffset < v.l) {
            return Math.pow(beginOffset, 2) + Math.pow((u.b + u.l - v.b - v.l), 2);
        }

        if (u.v == 1 && v.v == 0 && (v.l - u.l) >= beginOffset && beginOffset >= 0) {
            return Math.pow(u.l, 2);
        }

        if (u.v == 0 && v.v == 1 && (v.l - u.l) <= beginOffset && beginOffset <= 0) {
            return Math.pow(v.l, 2);
        }

        return 0;
    }

}
