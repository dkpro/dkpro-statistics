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

import org.junit.jupiter.api.Test;

/**
 * Tests based on Cohen (1960) for measuring {@link CohenKappaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. Educational and Psychological
 * Measurement 20(1):37-46, 1960.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
class Cohen1960Test
{
    @Test
    public void testExample1()
    {
        ICodingAnnotationStudy study = createExample1();

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.29, offset(0.01));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.35, offset(0.01));
        assertThat(kappa.calculateMaximumObservedAgreement()).isCloseTo(0.90, offset(0.01));
        assertThat(kappa.calculateMaximumAgreement()).isCloseTo(0.85, offset(0.01));
    }

    @Test
    void testExample2()
    {
        ICodingAnnotationStudy study = createExample2();

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.70, offset(0.01));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.41, offset(0.01));
        assertThat(kappa.calculateAgreement()).isCloseTo(0.492, offset(0.001));
        assertThat(kappa.calculateMaximumAgreement()).isCloseTo(0.831, offset(0.001));
    }

    /**
     * Creates an example annotation study introduced by Cohen (1960: p. 37).
     */
    static ICodingAnnotationStudy createExample1()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(25, 1, 1);
        study.addMultipleItems(12, 1, 2);
        study.addMultipleItems(3, 1, 3);
        study.addMultipleItems(13, 2, 1);
        study.addMultipleItems(2, 2, 2);
        study.addMultipleItems(15, 2, 3);
        study.addMultipleItems(12, 3, 1);
        study.addMultipleItems(16, 3, 2);
        study.addMultipleItems(2, 3, 3);
        return study;
    }

    /**
     * Creates an example annotation study introduced by Cohen (1960: p. 45).
     */
    static ICodingAnnotationStudy createExample2()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(88, 1, 1);
        study.addMultipleItems(14, 1, 2);
        study.addMultipleItems(18, 1, 3);
        study.addMultipleItems(10, 2, 1);
        study.addMultipleItems(40, 2, 2);
        study.addMultipleItems(10, 2, 3);
        study.addMultipleItems(2, 3, 1);
        study.addMultipleItems(6, 3, 2);
        study.addMultipleItems(12, 3, 3);
        return study;
    }

}
