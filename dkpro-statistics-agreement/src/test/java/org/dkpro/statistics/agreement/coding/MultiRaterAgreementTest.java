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
 * Tests for {@link PercentageAgreement} and {@link FleissKappaAgreement}.
 * 
 * @author Christian M. Meyer
 */
public class MultiRaterAgreementTest
{
    @Test
    public void testExample()
    {
        ICodingAnnotationStudy study = createExample();

        // Generalized percentage of agreement.
        PercentageAgreement pa = new PercentageAgreement(study);
        double agreement = pa.calculateAgreement();
        assertThat(agreement).isCloseTo(0.533, offset(0.001));
        //TODO
        /*double se = poa.standardError(agreement);
        double[] ci = poa.confidenceInterval(agreement, se, RawAgreement.CONFIDENCE_95);
        assertThat(se).isCloseTo(0.045, offset(0.001));
        assertThat(ci[0]).isCloseTo(0.610, offset(0.001));
        assertThat(ci[1]).isCloseTo(0.789, offset(0.001));*/        
        
        // Fleiss' multi-pi.
        FleissKappaAgreement pi = new FleissKappaAgreement(study);
        assertThat(pi.calculateObservedAgreement()).isCloseTo(0.533, offset(0.001));
        assertThat(pi.calculateExpectedAgreement()).isCloseTo(0.508, offset(0.001));
        agreement = pi.calculateAgreement();
        assertThat(agreement).isCloseTo(0.049, offset(0.001));
    }

    /** Creates an example annotation study. */
    public static ICodingAnnotationStudy createExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("high", "high", "high");
        study.addItem("high", "high", "low");
        study.addItem("high", "low", "high");
        study.addItem("low", "high", "high");
        study.addItem("low", "low", "high");
        study.addItem("low", "low", "low");
        study.addItem("low", "low", "low");
        study.addItem("low", "high", "low");
        study.addItem("low", "low", "high");
        study.addItem("low", "low", "high");
        return study;
    }

}
