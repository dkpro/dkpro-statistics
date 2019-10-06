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

import org.dkpro.statistics.agreement.IAnnotationUnit;
import org.dkpro.statistics.agreement.distance.SetAnnotation;

/**
 * Implementation of a simple "max" percentage agreement measure for calculating the inter-rater
 * agreement for two or more raters applying set-valued annotations. The measure is neither
 * chance-corrected nor weighted.<br>
 * <br>
 * References:
 * <ul>
 * <li>Jean Véronis. A study of polysemy judgements and inter-annotator agreement. In
 * <em>Proceedings of SENSEVAL-1</em>, 1998.</li>
 * </ul>
 *
 * @author Tristan Miller
 */
public class MaxPercentageAgreement
    extends CodingAgreementMeasure
    implements ICodingItemSpecificAgreement /* , IRaterAgreement */
{

    /**
     * Initializes the instance for the given annotation study. The study should never be null.
     */
    public MaxPercentageAgreement(final ICodingAnnotationStudy study)
    {
        super(study);
        ensureTwoRaters();
        warnIfMissingValues();
    }

    /**
     * Calculates the inter-rater agreement for the given annotation item. This is the basic step
     * that is performed for each item of an annotation study, when calling
     * {@link #calculateAgreement()}.
     *
     * @throws NullPointerException
     *             if the given item is null.
     */
    @Override
    public double doCalculateItemAgreement(final ICodingAnnotationItem item)
    {
        SetAnnotation setAnnotation = null;
        for (IAnnotationUnit annotationUnit : item.getUnits()) {
            SetAnnotation raterSetAnnotation = (SetAnnotation) (annotationUnit.getCategory());
            if (setAnnotation == null) {
                setAnnotation = new SetAnnotation(raterSetAnnotation);
            }
            else {
                setAnnotation.retainAll(raterSetAnnotation);
            }
            if (setAnnotation.size() == 0) {
                return 0.0;
            }
        }
        return setAnnotation.size() == 0 ? 0.0 : item.getRaterCount();
    }

    /**
     * Calculates the inter-rater agreement for the given annotation item. This is the basic step
     * that is performed for each item of an annotation study, when calling
     * {@link #calculateAgreement()}.
     *
     * @throws NullPointerException
     *             if the given item is null.
     */
    @Override
    public double calculateItemAgreement(final ICodingAnnotationItem item)
    {
        return doCalculateItemAgreement(item) / item.getRaterCount();
    }

}
