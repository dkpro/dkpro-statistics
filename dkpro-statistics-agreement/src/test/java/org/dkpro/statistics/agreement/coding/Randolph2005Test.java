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
 * Tests based on Randolph (2005) for measuring {@link RandolphKappaAgreement} and
 * {@link FleissKappaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Randolph, J.J.: Free-marginal multirater kappa (multirater kappa_free): An alternative to
 * Fleiss' fixed-marginal multirater kappa. In: Proceedings of the 5th Joensuu University Learning
 * and Instruction Symposium, 2005.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class Randolph2005Test
{
    @Test
    public void testExample1()
    {
        ICodingAnnotationStudy study = createExample1();

        FleissKappaAgreement pi = new FleissKappaAgreement(study);
        assertThat(pi.calculateObservedAgreement()).isCloseTo(0.67, offset(0.01));
        assertThat(pi.calculateExpectedAgreement()).isCloseTo(0.50, offset(0.01));
        assertThat(pi.calculateAgreement()).isCloseTo(0.33, offset(0.01));

        RandolphKappaAgreement rk = new RandolphKappaAgreement(study);
        assertThat(rk.calculateAgreement()).isCloseTo(0.33, offset(0.01));
    }

    
    @Test
    public void testExample2()
    {
        ICodingAnnotationStudy study = createExample2();

        FleissKappaAgreement pi = new FleissKappaAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(-0.2, offset(0.01));

        RandolphKappaAgreement rk = new RandolphKappaAgreement(study);
        assertThat(rk.calculateAgreement()).isCloseTo(0.33, offset(0.01));
    }

    /**
     * Creates an example annotation study introduced by Randolph (2005: p. 17).
     */
    public static ICodingAnnotationStudy createExample1()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("Yes", "Yes", "Yes");
        study.addItem("Yes", "Yes", "No");
        study.addItem("No", "No", "Yes");
        study.addItem("No", "No", "No");
        return study;
    }

    /**
     * Creates an example annotation study introduced by Randolph (2005: p. 17).
     */
    public static ICodingAnnotationStudy createExample2()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("Yes", "Yes", "Yes");
        study.addItem("Yes", "Yes", "No");
        study.addItem("Yes", "Yes", "No");
        study.addItem("Yes", "Yes", "Yes");
        return study;
    }

}
