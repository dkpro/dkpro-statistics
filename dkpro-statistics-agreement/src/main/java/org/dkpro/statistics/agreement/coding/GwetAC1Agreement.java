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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

import org.dkpro.statistics.agreement.IChanceCorrectedAgreement;
import org.dkpro.statistics.agreement.InsufficientDataException;

/**
 * Implementation of Gwet's AC1 (2008) for calculating a chance-corrected inter-rater agreement for
 * two raters. Like Scott's pi and Cohen's kappa, AC1 combines the observed agreement with an
 * estimate of the agreement expected by chance using the {@code (A_O - A_E) / (1 - A_E)} formula.
 * Unlike those measures, however, Gwet estimates the chance agreement as
 * {@code A_E = 1/(q-1) * sum_k pi_k * (1 - pi_k)} (where {@code q} denotes the number of categories
 * and {@code pi_k} the overall proportion of annotations assigned to category {@code k}). This
 * estimator is designed to avoid the so-called kappa paradox: Scott's pi and Cohen's kappa can
 * yield very low or even negative values despite a high observed agreement whenever the categories
 * are distributed very unevenly (i.e., in the presence of a high prevalence of one category). AC1
 * is far less sensitive to such skewed marginal distributions.<br>
 * <br>
 * The measure assumes the same probability distribution for all raters. For scaled or ordered
 * categories, refer to the weighted generalization {@link GwetAC2Agreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Gwet, K.L.: Computing inter-rater reliability and its variance in the presence of high
 * agreement. British Journal of Mathematical and Statistical Psychology 61(1):29-48, 2008.</li>
 * <li>Gwet, K.L.: Handbook of Inter-Rater Reliability. Gaithersburg, MD: Advanced Analytics,
 * 2014.</li>
 * </ul>
 *
 * @see GwetAC2Agreement
 */
public class GwetAC1Agreement
    extends CodingAgreementMeasure
    implements IChanceCorrectedAgreement
{

    /**
     * Initializes the instance for the given annotation study. The study may never be null.
     */
    public GwetAC1Agreement(final ICodingAnnotationStudy study)
    {
        super(study);
        ensureTwoRaters();
        warnIfMissingValues();
    }

    /**
     * Calculates the expected inter-rater agreement using Gwet's estimator
     * {@code 1/(q-1) * sum_k pi_k * (1 - pi_k)}, which assumes the same distribution for all raters
     * and annotations.
     *
     * @throws NullPointerException
     *             if the annotation study is null.
     * @throws InsufficientDataException
     *             if the study has fewer than two categories; Gwet's estimator divides by
     *             {@code q - 1}, which collapses to zero for a single-category study, and with only
     *             one category there is no decision for the raters to agree on.
     */
    @Override
    public double calculateExpectedAgreement()
    {
        if (study.getCategoryCount() <= 1) {
            throw new InsufficientDataException(
                    "An annotation study needs at least two different categories; otherwise there is no decision for the raters to agree on.");
        }

        Map<Object, Integer> annotationsPerCategory = CodingAnnotationStudy
                .countTotalAnnotationsPerCategory(study);

        BigDecimal total = BigDecimal.ZERO;
        for (Integer catCount : annotationsPerCategory.values()) {
            total = total.add(new BigDecimal(catCount));
        }

        BigDecimal result = BigDecimal.ZERO;
        for (Object category : study.getCategories()) {
            Integer catCount = annotationsPerCategory.get(category);
            if (catCount != null) {
                BigDecimal pi = new BigDecimal(catCount).divide(total, MathContext.DECIMAL128);
                result = result.add(pi.multiply(BigDecimal.ONE.subtract(pi)));
            }
        }
        result = result.divide(new BigDecimal(study.getCategoryCount() - 1),
                MathContext.DECIMAL128);
        return result.doubleValue();
    }

}
