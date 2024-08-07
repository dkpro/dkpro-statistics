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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Iterator;

import org.dkpro.statistics.agreement.ICategorySpecificAgreement;
import org.junit.jupiter.api.Test;

/**
 * Tests based on Fleiss (1971) for measuring {@link FleissKappaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Fleiss, J.L.: Measuring nominal scale agreement among many raters. Psychological Bulletin
 * 76(5):378-381, 1971.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class Fleiss1971Test
{
    @Test
    public void testAgreement()
    {
        ICodingAnnotationStudy study = createExample();
        assertEquals(30, study.getItemCount());

        FleissKappaAgreement kappa = new FleissKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.5556, offset(0.001));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.2201, offset(0.001));
        double agreement = kappa.calculateAgreement();
        assertThat(agreement).isCloseTo(0.430, offset(0.001));

        // Var = 0.000759 = 2/n*m(m-1) * (AE - (2m-3)AE^2 + 2(m-2)AE / (1-AE)^2)
        // SE = 0.028
        // TODO
        /*
         * double se = raw.standardError(agreement); double[] ci = raw.confidenceInterval(agreement,
         * se, RawAgreement.CONFIDENCE_95); 
         * assertThat(se).isCloseTo(0.028, offset(0.001)); 
         * assertEquals(0.610, ci[0], 0.001); assertThat(ci[1]).isCloseTo(0.789, offset(0.001));
         */
    }

    
    @Test
    public void testCategoryAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        ICategorySpecificAgreement catAgreement = new FleissKappaAgreement(study);
        assertThat(catAgreement.calculateCategoryAgreement(1)).isCloseTo(0.248, offset(0.005));
        assertThat(catAgreement.calculateCategoryAgreement(2)).isCloseTo(0.248, offset(0.005));
        assertThat(catAgreement.calculateCategoryAgreement(3)).isCloseTo(0.517, offset(0.005));
        assertThat(catAgreement.calculateCategoryAgreement(4)).isCloseTo(0.470, offset(0.005));
        assertThat(catAgreement.calculateCategoryAgreement(5)).isCloseTo(0.565, offset(0.005));
    }

    
    @Test
    public void testItemAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        ICodingItemSpecificAgreement agreement = new PercentageAgreement(study);
        Iterator<ICodingAnnotationItem> iter = study.getItems().iterator();
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.400, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.400, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.400, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.267, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.400, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.400, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.267, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.267, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.267, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.267, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.400, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.467, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(0.667, offset(0.001));
        assertThat(agreement.calculateItemAgreement(iter.next())).isCloseTo(1.000, offset(0.001));
        assertFalse(iter.hasNext());
    }

    /**
     * Creates an example annotation study introduced by Fleiss (1971: 379).
     */
    public static ICodingAnnotationStudy createExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(6);
        study.addItem(4, 4, 4, 4, 4, 4);
        study.addItem(2, 2, 2, 5, 5, 5);
        study.addItem(2, 3, 3, 3, 3, 5);
        study.addItem(5, 5, 5, 5, 5, 5);
        study.addItem(2, 2, 2, 4, 4, 4);
        study.addItem(1, 1, 3, 3, 3, 3);
        study.addItem(3, 3, 3, 3, 5, 5);
        study.addItem(1, 1, 3, 3, 3, 4);
        study.addItem(1, 1, 4, 4, 4, 4);
        study.addItem(5, 5, 5, 5, 5, 5);

        study.addItem(1, 4, 4, 4, 4, 4);
        study.addItem(1, 2, 4, 4, 4, 4);
        study.addItem(2, 2, 2, 3, 3, 3);
        study.addItem(1, 4, 4, 4, 4, 4);
        study.addItem(2, 2, 4, 4, 4, 5);
        study.addItem(3, 3, 3, 3, 3, 5);
        study.addItem(1, 1, 1, 4, 5, 5);
        study.addItem(1, 1, 1, 1, 1, 2);
        study.addItem(2, 2, 4, 4, 4, 4);
        study.addItem(1, 3, 3, 5, 5, 5);

        study.addItem(5, 5, 5, 5, 5, 5);
        study.addItem(2, 4, 4, 4, 4, 4);
        study.addItem(2, 2, 4, 5, 5, 5);
        study.addItem(1, 1, 4, 4, 4, 4);
        study.addItem(1, 4, 4, 4, 4, 5);
        study.addItem(2, 2, 2, 2, 2, 4);
        study.addItem(1, 1, 1, 1, 5, 5);
        study.addItem(2, 2, 4, 4, 4, 4);
        study.addItem(1, 3, 3, 3, 3, 3);
        study.addItem(5, 5, 5, 5, 5, 5);
        return study;
    }
}
