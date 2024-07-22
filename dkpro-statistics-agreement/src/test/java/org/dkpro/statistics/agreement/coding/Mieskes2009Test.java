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
 * Tests based on Mieskes (2009) for category-specific agreement using
 * {@link FleissKappaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Mieskes, M.: Exploring Methods for the Automatic Summarization of Meetings. Dissertation.
 * Friedrich-Alexander-Universität Erlangen-Nürnberg, 2009.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 * @author Margot Mieskes
 */
public class Mieskes2009Test
{
    @Test
    void testAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        FleissKappaAgreement kappa = new FleissKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.84, offset(0.01));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.20, offset(0.01));
        assertThat(kappa.calculateAgreement()).isCloseTo(0.80, offset(0.01));

        assertThat(kappa.calculateCategoryAgreement("INP")).isCloseTo(1.00, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("JJ")).isCloseTo(1.00, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("MD")).isCloseTo(1.00, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("NN")).isCloseTo(0.48, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("PRP")).isCloseTo(1.00, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("RB")).isCloseTo(0.56, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("UH")).isCloseTo(0.67, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("VB")).isCloseTo(0.29, offset(0.01));
        assertThat(kappa.calculateCategoryAgreement("VBP")).isCloseTo(-0.02, offset(0.01));
    }

    /**
     * Creates an example annotation study introduced by Mieskes (2009: p. 58).
     */
    static ICodingAnnotationStudy createExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("UH", "VB", "UH");
        study.addItem("INP", "INP", "INP");
        study.addItem("UH", "UH", "UH");
        study.addItem("INP", "INP", "INP");
        study.addItem("RB", "RB", "RB");
        study.addItem("JJ", "JJ", "JJ");
        study.addItem("INP", "INP", "INP");
        study.addItem("PRP", "PRP", "PRP");
        study.addItem("NN", "NN", "RB");
        study.addItem("VB", "VBP", "VB");
        study.addItem("PRP", "PRP", "PRP");
        study.addItem("INP", "INP", "INP");
        study.addItem("RB", "UH", "UH");
        study.addItem("INP", "INP", "INP");
        study.addItem("MD", "MD", "MD");
        study.addItem("PRP", "PRP", "PRP");
        study.addItem("INP", "INP", "INP");
        return study;
    }

}
