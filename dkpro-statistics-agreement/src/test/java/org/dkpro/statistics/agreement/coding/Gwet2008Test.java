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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;

import org.dkpro.statistics.agreement.InsufficientDataException;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;
import org.dkpro.statistics.agreement.distance.IntervalDistanceFunction;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests for Gwet's {@link GwetAC1Agreement AC1} and {@link GwetAC2Agreement AC2} coefficients.<br>
 * <br>
 * References:
 * <ul>
 * <li>Gwet, K.L.: Computing inter-rater reliability and its variance in the presence of high
 * agreement. British Journal of Mathematical and Statistical Psychology 61(1):29-48, 2008.</li>
 * </ul>
 */
class Gwet2008Test
{
    /**
     * A small, hand-verifiable two-category study that illustrates how AC1 avoids the kappa
     * paradox: despite a highly skewed marginal distribution, AC1 stays close to the observed
     * agreement, whereas Scott's pi is pulled down substantially.
     */
    @Test
    void testHandExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(8, "y", "y");
        study.addMultipleItems(1, "n", "n");
        study.addMultipleItems(1, "y", "n");

        GwetAC1Agreement ac1 = new GwetAC1Agreement(study);
        assertThat(ac1.calculateObservedAgreement()).isCloseTo(0.9, offset(0.0001));
        assertThat(ac1.calculateExpectedAgreement()).isCloseTo(0.255, offset(0.0001));
        assertThat(ac1.calculateAgreement()).isCloseTo(0.8658, offset(0.0001));

        // Scott's pi suffers from the skewed distribution.
        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(0.6078, offset(0.0001));
    }

    /**
     * AC1 for the study introduced by Cohen (1960: p. 37); reference values computed from Gwet's
     * (2008) definition.
     */
    @Test
    void testCohenExample1()
    {
        ICodingAnnotationStudy study = Cohen1960Test.createExample1();

        GwetAC1Agreement ac1 = new GwetAC1Agreement(study);
        assertThat(ac1.calculateObservedAgreement()).isCloseTo(0.29, offset(0.0001));
        assertThat(ac1.calculateExpectedAgreement()).isCloseTo(0.3225, offset(0.0001));
        assertThat(ac1.calculateAgreement()).isCloseTo(-0.0480, offset(0.0001));
    }

    /**
     * AC1 for the study introduced by Cohen (1960: p. 45); reference values computed from Gwet's
     * (2008) definition.
     */
    @Test
    void testCohenExample2()
    {
        ICodingAnnotationStudy study = Cohen1960Test.createExample2();

        GwetAC1Agreement ac1 = new GwetAC1Agreement(study);
        assertThat(ac1.calculateObservedAgreement()).isCloseTo(0.70, offset(0.0001));
        assertThat(ac1.calculateExpectedAgreement()).isCloseTo(0.2925, offset(0.0001));
        assertThat(ac1.calculateAgreement()).isCloseTo(0.5760, offset(0.0001));
    }

    /**
     * With a nominal distance function, AC2 must reduce exactly to AC1, since the weight matrix has
     * T_w = q. This is checked across several studies.
     */
    @Test
    void testAC2ReducesToAC1ForNominalDistance()
    {
        ICodingAnnotationStudy[] studies = { Cohen1960Test.createExample1(),
                Cohen1960Test.createExample2() };

        for (ICodingAnnotationStudy study : studies) {
            GwetAC1Agreement ac1 = new GwetAC1Agreement(study);
            GwetAC2Agreement ac2 = new GwetAC2Agreement(study, new NominalDistanceFunction());
            assertThat(ac2.calculateAgreement()).isCloseTo(ac1.calculateAgreement(),
                    offset(0.0001));
        }
    }

    /**
     * AC2 with a nominal distance function must reduce to AC1 even when some items are rated by
     * only a single rater. Both measures skip such items rather than letting them inflate the
     * observed agreement: AC2 must normalize per item by its own rater count instead of assuming
     * that every item carries the full complement of raters.
     */
    @Test
    void testAC2ReducesToAC1WithMissingValues()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(8, "y", "y");
        study.addMultipleItems(1, "n", "n");
        study.addMultipleItems(1, "y", "n");
        // Single-rater items must not change the observed agreement of either measure.
        study.addItem("y", null);
        study.addItem("y", null);
        study.addItem("n", null);

        GwetAC1Agreement ac1 = new GwetAC1Agreement(study);
        GwetAC2Agreement ac2 = new GwetAC2Agreement(study, new NominalDistanceFunction());

        // The single-rater items are skipped, so the observed agreement stays at the 0.9 of the
        // complete study (cf. testHandExample) rather than being inflated by the perfectly
        // "agreeing" single annotations.
        assertThat(1.0 - ac2.calculateObservedDisagreement()).isCloseTo(0.9, offset(0.0001));
        assertThat(1.0 - ac2.calculateObservedDisagreement())
                .isCloseTo(ac1.calculateObservedAgreement(), offset(1e-9));
        assertThat(ac2.calculateAgreement()).isCloseTo(ac1.calculateAgreement(), offset(1e-9));
    }

    /**
     * AC2 must reduce to AC1 for a nominal distance function even with more than two raters and
     * variable per-item rater counts (some annotations missing).
     */
    @Test
    void testAC2ReducesToAC1MultipleRatersWithMissingValues()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem(1, 1, 1);
        study.addItem(1, 1, 2);
        study.addItem(2, 2, 2);
        study.addItem(3, 3, 2);
        study.addItem(1, 1, null);
        study.addItem(2, null, null);
        study.addItem(3, 3, 3);
        study.addItem(2, 3, null);

        GwetAC2Agreement ac2 = new GwetAC2Agreement(study, new NominalDistanceFunction());
        assertThat(1.0 - ac2.calculateObservedDisagreement()).isCloseTo(
                new PercentageAgreement(study).calculateObservedAgreement(), offset(1e-9));
    }

    /**
     * A study in which every rater only ever used a single category offers no decision to agree on.
     * Gwet's chance-agreement estimate divides by {@code q * (q - 1) = 0} there, so AC2 must refuse
     * such a study with an {@link InsufficientDataException} rather than emitting a silent
     * {@code NaN}. This mirrors {@link GwetAC1Agreement}, which rejects the same study, and the
     * general convention of the library (cf.
     * {@link org.dkpro.statistics.agreement.AgreementMeasure#calculateAgreement()}).<br>
     * <br>
     * Note that Gwet's own reference implementation (irrCAC) instead floors the chance agreement to
     * a tiny epsilon and reports a coefficient of {@code 1.0}; dkpro deliberately diverges by
     * treating the single-category case as insufficient data, consistently for both AC1 and AC2.
     */
    @Test
    void testSingleCategoryThrows()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(10, "y", "y");

        // AC1 already rejects a single-category study; AC2 must behave identically.
        GwetAC1Agreement ac1 = new GwetAC1Agreement(study);
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(ac1::calculateAgreement);

        for (IDistanceFunction distance : new IDistanceFunction[] { new NominalDistanceFunction(),
                new IntervalDistanceFunction() }) {
            GwetAC2Agreement ac2 = new GwetAC2Agreement(study, distance);
            assertThatExceptionOfType(InsufficientDataException.class)
                    .isThrownBy(ac2::calculateAgreement);
            // Both public component methods must guard as well, not just the combined coefficient.
            assertThatExceptionOfType(InsufficientDataException.class)
                    .isThrownBy(ac2::calculateObservedDisagreement);
            assertThatExceptionOfType(InsufficientDataException.class)
                    .isThrownBy(ac2::calculateExpectedDisagreement);
        }
    }

    /**
     * A study that has two or more categories but in which every item was annotated by only a
     * single rater leaves nothing to compare: the per-item {@code r_i * (r_i - 1)} normalization
     * skips every item, so the observed-agreement denominator collapses to zero. AC2 must refuse
     * such a study with an {@link InsufficientDataException} rather than dividing by zero and
     * returning a silent {@code NaN}/{@code Infinity}. (Unlike {@link #testSingleCategoryThrows()},
     * the expected agreement here is perfectly well-defined, so only the observed side and the
     * combined coefficient guard.)
     */
    @Test
    void testNoItemWithTwoRatersThrows()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        // Two categories are present, so ensureSufficientCategories() passes, but each item carries
        // only a single rater and is therefore skipped by the per-item normalization.
        study.addItem("y", null);
        study.addItem("n", null);

        for (IDistanceFunction distance : new IDistanceFunction[] { new NominalDistanceFunction(),
                new IntervalDistanceFunction() }) {
            GwetAC2Agreement ac2 = new GwetAC2Agreement(study, distance);
            assertThatExceptionOfType(InsufficientDataException.class)
                    .isThrownBy(ac2::calculateObservedDisagreement);
            assertThatExceptionOfType(InsufficientDataException.class)
                    .isThrownBy(ac2::calculateAgreement);
        }
    }
}
