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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Map.Entry;

import org.dkpro.statistics.agreement.IChanceCorrectedDisagreement;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Generalization of Cohen's (1960) kappa-measure for calculating a chance-corrected inter-rater
 * agreement for multiple raters with arbitrary distance/weighting functions. Before an inter-rater
 * agreement can be calculated, an {@link IDistanceFunction} instance needs to be assigned.<br>
 * <br>
 * References:
 * <ul>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. Educational and Psychological
 * Measurement 20(1):37-46, Beverly Hills, CA: Sage Publications, 1960.
 * <li>Cohen, J.: Weighted kappa: Nominal scale agreement with provision for scaled disagreement or
 * partial credit. Psychological Bulletin 70(4):213-220, Washington, DC: American Psychological
 * Association, 1968.
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational Linguistics.
 * Computational Linguistics 34(4):555-596, Cambridge, MA: The MIT Press, 2008.
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class WeightedKappaAgreement extends WeightedAgreement
        implements IChanceCorrectedDisagreement {

    /**
     * Initializes the instance for the given annotation study. The study should never be null.
     */
    public WeightedKappaAgreement(final ICodingAnnotationStudy study,
            final IDistanceFunction distanceFunction) {
        super(study);
        this.distanceFunction = distanceFunction;
    }

    /**
     * Calculates the observed inter-rater agreement for the annotation study that was passed to the
     * class constructor and the currently assigned distance function.
     * 
     * @throws NullPointerException
     *             if the study is null.
     * @throws ArithmeticException
     *             if the study does not contain any item or the number of raters is smaller than 2.
     */
    @Override
    public double calculateObservedDisagreement()
    {
        ensureDistanceFunction();

        double result = 0.0;
        double maxDistance = 1.0;
        for (ICodingAnnotationItem item : study.getItems()) {
            Map<Object, Integer> annotationsPerCategory
                    = CodingAnnotationStudy.countTotalAnnotationsPerCategory(item);

            for (Entry<Object, Integer> category1 : annotationsPerCategory.entrySet()) {
                for (Entry<Object, Integer> category2 : annotationsPerCategory.entrySet()) {
                    if (category1.getValue() == null) {
                        continue;
                    }
                    if (category2.getValue() == null) {
                        continue;
                    }

                    double distance = distanceFunction.measureDistance(study,
                            category1.getKey(), category2.getKey());
                    result += category1.getValue() * category2.getValue()
                            * distance;
                    if (distance > maxDistance) {
                        maxDistance = distance;
                    }
                }
            }
        }

        result /= (double) (maxDistance * study.getItemCount() * study.getRaterCount()
                * (study.getRaterCount() - 1));
        return result;
    }

    /**
     * Calculates the expected inter-rater agreement using the defined distance function to infer
     * the assumed probability distribution.
     * 
     * @throws NullPointerException
     *             if the annotation study is null.
     * @throws ArithmeticException
     *             if there are no items or raters in the annotation study.
     */
    @Override
    public double calculateExpectedDisagreement()
    {
        ensureDistanceFunction();

        BigDecimal result = BigDecimal.ZERO;
        Map<Object, int[]> annotationsPerCategory
                = CodingAnnotationStudy.countAnnotationsPerCategory(study);

        double maxDistance = 1.0;
        for (Object category1 : study.getCategories()) {
            for (Object category2 : study.getCategories()) {
                int[] annotationCounts1 = annotationsPerCategory.get(category1);
                int[] annotationCounts2 = annotationsPerCategory.get(category2);
                for (int m = 0; m < study.getRaterCount(); m++) {
                    for (int n = m + 1; n < study.getRaterCount(); n++) {
                        double distance = distanceFunction.measureDistance(study, category1,
                                category2);
                        result = result.add(
                                new BigDecimal(annotationCounts1[m]).multiply(
                                new BigDecimal(annotationCounts2[n]).multiply(
                                new BigDecimal(distance)
                                )));
                        if (distance > maxDistance) {
                            maxDistance = distance;
                        }
                    }
                }
            }
        }

        result = result.divide(new BigDecimal(study.getItemCount()).pow(2).multiply(
                new BigDecimal(maxDistance)), MathContext.DECIMAL128);
        return result.doubleValue();
    }
}
