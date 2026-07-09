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
import org.dkpro.statistics.agreement.distance.SetAnnotation;
import org.junit.jupiter.api.Test;

/**
 * End-to-end regression test for issue #20. {@link SetAnnotation} instances are used as keys in the
 * coincidence matrix built by
 * {@link CodingAnnotationStudy#countCategoryCoincidence(org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy)},
 * so their {@code equals}/{@code hashCode} directly determine which categories are treated as
 * identical. When those relied on {@code toString()}, sets holding elements that merely render to
 * the same text (e.g. the integer {@code 1} vs. the string {@code "1"}) were conflated, inflating
 * the observed agreement.
 */
public class SetAnnotationCategoryConflationTest
{
    /**
     * Every item pairs an integer-valued category with a string-valued category that renders to the
     * same text. The two raters therefore never actually agree. Before the fix, the categories were
     * conflated via their string form, so this study was scored as perfect agreement.
     */
    @Test
    public void categoriesDifferingOnlyByElementTypeAreNotConflated()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        for (int i = 1; i <= 4; i++) {
            study.addItem(new SetAnnotation(Integer.valueOf(i)),
                    new SetAnnotation(Integer.toString(i)));
        }

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());

        // The raters disagree on every single item.
        assertThat(alpha.calculateObservedDisagreement()).isEqualTo(1.0);
        // ... so agreement must be at or below chance, definitely not perfect (1.0).
        assertThat(alpha.calculateAgreement()).isLessThanOrEqualTo(0.0);
    }

    /**
     * Positive control: when both raters use the very same integer-valued categories, the study is
     * perfect agreement. Guards against a "fix" that simply makes all sets compare unequal.
     */
    @Test
    public void identicalIntegerValuedCategoriesAreFullAgreement()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        for (int i = 1; i <= 4; i++) {
            study.addItem(new SetAnnotation(Integer.valueOf(i)),
                    new SetAnnotation(Integer.valueOf(i)));
        }

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());

        assertThat(alpha.calculateObservedDisagreement()).isEqualTo(0.0);
        assertThat(alpha.calculateAgreement()).isCloseTo(1.0, offset(0.0001));
    }
}
