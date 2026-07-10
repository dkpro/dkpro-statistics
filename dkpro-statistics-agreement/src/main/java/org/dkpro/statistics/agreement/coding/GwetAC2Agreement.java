/*
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
import java.util.Map.Entry;

import org.dkpro.statistics.agreement.IChanceCorrectedDisagreement;
import org.dkpro.statistics.agreement.IMultiRaterAgreement;
import org.dkpro.statistics.agreement.InsufficientDataException;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Weighted generalization of Gwet's {@link GwetAC1Agreement AC1} coefficient, commonly referred to
 * as AC2 (Gwet, 2014). AC2 allows for scoring partial agreement between distinct but similar
 * categories by means of an {@link IDistanceFunction} and is thus suited for ordinal, interval, or
 * ratio-scaled categories, in the same way that weighted kappa generalizes Cohen's kappa. Before an
 * inter-rater agreement can be calculated, an {@link IDistanceFunction} instance needs to be
 * assigned.<br>
 * <br>
 * As with AC1, the chance agreement is estimated as
 * {@code A_E = T_w/(q*(q-1)) * sum_k pi_k * (1 - pi_k)}, where {@code q} denotes the number of
 * categories, {@code pi_k} the overall proportion of annotations assigned to category {@code k},
 * and {@code T_w} the sum over the weight matrix {@code w_kl = 1 - d(k,l)/d_max}. When a
 * {@link org.dkpro.statistics.agreement.distance.NominalDistanceFunction} is used, {@code T_w = q}
 * and AC2 reduces exactly to {@link GwetAC1Agreement AC1}. Like AC1, the measure is designed to
 * avoid the kappa paradox under skewed marginal distributions.<br>
 * <br>
 * References:
 * <ul>
 * <li>Gwet, K.L.: Computing inter-rater reliability and its variance in the presence of high
 * agreement. British Journal of Mathematical and Statistical Psychology 61(1):29-48, 2008.</li>
 * <li>Gwet, K.L.: Handbook of Inter-Rater Reliability. Gaithersburg, MD: Advanced Analytics,
 * 2014.</li>
 * </ul>
 *
 * @see GwetAC1Agreement
 */
public class GwetAC2Agreement
    extends WeightedAgreement
    implements IChanceCorrectedDisagreement, IMultiRaterAgreement
{

    /**
     * Initializes the instance for the given annotation study. The study should never be null.
     */
    public GwetAC2Agreement(final ICodingAnnotationStudy study,
            final IDistanceFunction distanceFunction)
    {
        super(study);
        this.distanceFunction = distanceFunction;
    }

    /**
     * Calculates the observed inter-rater agreement for the annotation study that was passed to the
     * class constructor and the currently assigned distance function. This is computed in the same
     * way as for the weighted kappa measure.
     *
     * @throws NullPointerException
     *             if the study is null.
     * @throws InsufficientDataException
     *             if the study has fewer than two categories, or if no item was annotated by at
     *             least two raters (so there is no pair of judgements to compare).
     */
    @Override
    public double calculateObservedDisagreement()
    {
        ensureDistanceFunction();
        ensureSufficientCategories();

        // The weight matrix w_kl = 1 - d(k,l)/d_max must use one fixed d_max (the maximum distance
        // over all category pairs) so that the observed and the expected agreement are normalized
        // consistently. This differs from WeightedKappaAgreement, which grows d_max only over the
        // category pairs actually co-occurring within an item.
        double maxDistance = calculateMaximumDistance();

        // Normalize per item by its own rater count (r_i * (r_i - 1)) and skip items rated by fewer
        // than two raters, mirroring CodingAgreementMeasure#calculateObservedAgreement. Using the
        // global item and rater counts instead would count single-rater items and assume every item
        // carries the full complement of raters, which inflates the observed agreement whenever
        // values are missing. This keeps the "AC2 with a nominal distance reduces to AC1" invariant
        // intact for arbitrary per-item rater counts, and leaves complete studies unchanged.
        double result = 0.0;
        double denominator = 0.0;
        for (ICodingAnnotationItem item : study.getItems()) {
            int raterCount = item.getRaterCount();
            if (raterCount <= 1) {
                continue;
            }

            Map<Object, Integer> annotationsPerCategory = CodingAnnotationStudy
                    .countTotalAnnotationsPerCategory(item);

            double itemDisagreement = 0.0;
            for (Entry<Object, Integer> category1 : annotationsPerCategory.entrySet()) {
                for (Entry<Object, Integer> category2 : annotationsPerCategory.entrySet()) {
                    double distance = distanceFunction.measureDistance(study, category1.getKey(),
                            category2.getKey());
                    itemDisagreement += category1.getValue() * category2.getValue() * distance;
                }
            }

            result += itemDisagreement / (raterCount - 1.0);
            denominator += raterCount;
        }

        // Every item was skipped (empty study, or no item annotated by two or more raters), so the
        // denominator collapsed to zero. Refuse rather than returning a silent NaN/Infinity,
        // mirroring
        // ensureSufficientCategories(): there is no pair of judgements from which to observe
        // agreement.
        if (denominator == 0.0) {
            throw new InsufficientDataException(
                    "An annotation study needs at least one item annotated by two or more raters; otherwise there is no pair of judgements from which to observe agreement.");
        }

        return result / (maxDistance * denominator);
    }

    /**
     * Gwet's chance-agreement estimate divides by {@code q * (q - 1)}, which collapses to zero for
     * a single-category study and would otherwise make {@link #calculateExpectedDisagreement()}
     * (and hence the coefficient) a silent {@code NaN}. Mirror {@link GwetAC1Agreement} and
     * {@link CodingAgreementMeasure#calculateObservedAgreement()} by refusing such studies: with
     * only one category there is no decision for the raters to agree on.
     *
     * @throws InsufficientDataException
     *             if the study has fewer than two categories.
     */
    protected void ensureSufficientCategories()
    {
        if (study.getCategoryCount() <= 1) {
            throw new InsufficientDataException(
                    "An annotation study needs at least two different categories; otherwise there is no decision for the raters to agree on.");
        }
    }

    /**
     * Determines the maximum distance over all category pairs so that the weight matrix
     * {@code w_kl = 1 - d(k,l)/d_max} is normalized to {@code [0,1]} with {@code w_kk = 1}. A floor
     * of {@code 1.0} mirrors the convention of the other weighted measures.
     */
    protected double calculateMaximumDistance()
    {
        double maxDistance = 1.0;
        for (Object category1 : study.getCategories()) {
            for (Object category2 : study.getCategories()) {
                double distance = distanceFunction.measureDistance(study, category1, category2);
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }
        }
        return maxDistance;
    }

    /**
     * Calculates the expected inter-rater agreement using Gwet's weighted estimator
     * {@code T_w/(q*(q-1)) * sum_k pi_k * (1 - pi_k)}. The result is returned as an expected
     * disagreement (i.e., {@code 1 - A_E}) so that it combines with the observed disagreement using
     * the {@code 1 - D_O/D_E} formula of the enclosing
     * {@link org.dkpro.statistics.agreement.DisagreementMeasure}.
     *
     * @throws NullPointerException
     *             if the annotation study is null.
     * @throws InsufficientDataException
     *             if the study has fewer than two categories.
     */
    @Override
    public double calculateExpectedDisagreement()
    {
        ensureDistanceFunction();
        ensureSufficientCategories();

        Map<Object, Integer> annotationsPerCategory = CodingAnnotationStudy
                .countTotalAnnotationsPerCategory(study);

        double total = 0.0;
        for (Integer catCount : annotationsPerCategory.values()) {
            if (catCount != null) {
                total += catCount;
            }
        }

        double maxDistance = calculateMaximumDistance();

        // T_w = sum over the weight matrix.
        double weightSum = 0.0;
        for (Object category1 : study.getCategories()) {
            for (Object category2 : study.getCategories()) {
                double distance = distanceFunction.measureDistance(study, category1, category2);
                weightSum += 1.0 - (distance / maxDistance);
            }
        }

        double piSum = 0.0;
        for (Object category : study.getCategories()) {
            Integer catCount = annotationsPerCategory.get(category);
            if (catCount != null) {
                double pi = catCount / total;
                piSum += pi * (1.0 - pi);
            }
        }

        int q = study.getCategoryCount();
        double expectedAgreement = (weightSum / (q * (q - 1.0))) * piSum;
        return 1.0 - expectedAgreement;
    }
}
