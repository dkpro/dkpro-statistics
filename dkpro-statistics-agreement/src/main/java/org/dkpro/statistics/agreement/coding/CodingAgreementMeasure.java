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
package org.dkpro.statistics.agreement.coding;

import java.util.Map;

import org.dkpro.statistics.agreement.AgreementMeasure;
import org.dkpro.statistics.agreement.InsufficientDataException;

/**
 * Abstract base class of agreement measures for {@link ICodingAnnotationStudy}s.
 * 
 * @author Christian M. Meyer
 */
public abstract class CodingAgreementMeasure
    extends AgreementMeasure
    implements ICodingAgreementMeasure
{
    protected ICodingAnnotationStudy study;

    /**
     * Initializes the instance for the given annotation study. The study should never be null.
     */
    public CodingAgreementMeasure(final ICodingAnnotationStudy study)
    {
        this.study = study;
    }

    @Override
    public double calculateObservedAgreement()
    {
        if (study.getCategoryCount() <= 1) {
            throw new InsufficientDataException(
                    "An annotation study needs at least two different categories; otherwise there is no decision for the raters to agree on.");
        }

        double result = 0.0;
        double denominator = 0.0;
        for (ICodingAnnotationItem item : study.getItems()) {
            int raterCount = item.getRaterCount();
            if (raterCount > 1) {
                result += doCalculateItemAgreement(item);
                denominator += raterCount;
            }
        }
        return result / denominator;
    }

    protected double doCalculateItemAgreement(final ICodingAnnotationItem item)
    {
        Map<Object, Integer> annotationsPerCategory = CodingAnnotationStudy
                .countTotalAnnotationsPerCategory(item);
        double result = 0.0;
        for (Integer count : annotationsPerCategory.values()) {
            result += count * (count - 1);
        }
        int raterCount = item.getRaterCount();
        if (raterCount <= 1) {
            return 0.0;
        }
        else {
            return result / (double) (raterCount - 1.0);
        }
    }

    protected void ensureTwoRaters()
    {
        if (study.getRaterCount() != 2) {
            throw new IllegalArgumentException("This agreement measure is only "
                    + "applicable for annotation studies with two raters!");
        }
    }

    protected void warnIfMissingValues()
    {
        if (study.hasMissingValues()) {
            System.err.println(getClass().getName()
                    + " does not support dealing with missing values. Consider using, for example, Krippendorff's alpha instead.");
        }
    }

}
