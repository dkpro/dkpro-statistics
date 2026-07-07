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
import static org.assertj.core.api.Assertions.offset;

import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests that full inter-rater agreement yields an agreement of 1.0 rather than 0.0. This is a
 * regression test for issue #35, where {@link KrippendorffAlphaAgreement} reported an agreement of
 * 0.0 when the observed and the expected disagreement were both zero (i.e., when all raters agreed
 * on every unit).
 */
public class FullAgreementTest
{
    /**
     * When every rater assigns the same single category to every unit while the study still defines
     * more than one category (e.g., an unused category from the tagset), the observed and the
     * expected disagreement are both zero. This is full agreement and must yield 1.0.
     */
    @Test
    public void testFullAgreementSingleUsedCategory()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        // Declare a second category that is never actually used by the raters.
        study.addCategory("A");
        study.addCategory("B");
        study.addItem("A", "A");
        study.addItem("A", "A");
        study.addItem("A", "A");

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(1.0, offset(0.001));
    }

    /**
     * When the raters fully agree but use more than one category, the expected disagreement is
     * greater than zero. This case already yielded 1.0 before the fix and must keep doing so.
     */
    @Test
    public void testFullAgreementMultipleUsedCategories()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem("A", "A");
        study.addItem("B", "B");
        study.addItem("A", "A");
        study.addItem("B", "B");

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.0, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isGreaterThan(0.0);
        assertThat(alpha.calculateAgreement()).isCloseTo(1.0, offset(0.001));
    }
}
