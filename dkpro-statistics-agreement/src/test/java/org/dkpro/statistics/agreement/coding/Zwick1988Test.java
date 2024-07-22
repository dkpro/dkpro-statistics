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
 * Tests based on Zwick (1988) for several inter-rater agreement measures.<br>
 * <br>
 * References:
 * <ul>
 * <li>Zwick, R.: Another look at interrater agreement. Psychological Bulletin 103(3):374–378,
 * 1988.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class Zwick1988Test
{
    @Test
    public void testExample1()
    {
        ICodingAnnotationStudy study = createExample1();

        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.600, offset(0.001));

        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateAgreement()).isCloseTo(0.467, offset(0.001));
        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(0.467, offset(0.001));
        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateAgreement()).isCloseTo(0.467, offset(0.001));
    }

    
    @Test
    public void testExample2()
    {
        ICodingAnnotationStudy study = createExample2();

        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.600, offset(0.001));

        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateAgreement()).isCloseTo(0.467, offset(0.001));
        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(0.444, offset(0.001));
        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateAgreement()).isCloseTo(0.444, offset(0.001));
    }

    
    @Test
    public void testExample3()
    {
        ICodingAnnotationStudy study = createExample3();

        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.600, offset(0.001));

        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateAgreement()).isCloseTo(0.467, offset(0.001));
        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateAgreement()).isCloseTo(0.460, offset(0.001));
        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateAgreement()).isCloseTo(0.474, offset(0.001));
    }

    /**
     * Creates an example annotation study introduced by Zwick (1988: p. 376).
     */
    public static ICodingAnnotationStudy createExample1()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(20, "A", "A");
        study.addMultipleItems(5, "A", "D");
        study.addMultipleItems(10, "B", "B");
        study.addMultipleItems(15, "B", "C");
        study.addMultipleItems(15, "C", "B");
        study.addMultipleItems(10, "C", "C");
        study.addMultipleItems(5, "D", "A");
        study.addMultipleItems(20, "D", "D");
        return study;
    }

    /**
     * Creates an example annotation study introduced by Zwick (1988: p. 376).
     */
    public static ICodingAnnotationStudy createExample2()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(20, "A", "A");
        study.addMultipleItems(10, "A", "B");
        study.addMultipleItems(10, "A", "C");
        study.addMultipleItems(10, "B", "A");
        study.addMultipleItems(10, "B", "B");
        study.addMultipleItems(10, "C", "A");
        study.addMultipleItems(10, "C", "C");
        study.addMultipleItems(20, "D", "D");
        return study;
    }

    /**
     * Creates an example annotation study introduced by Zwick (1988: p. 376).
     */
    public static ICodingAnnotationStudy createExample3()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(20, "A", "A");
        study.addMultipleItems(5, "A", "B");
        study.addMultipleItems(5, "A", "C");
        study.addMultipleItems(10, "A", "D");
        study.addMultipleItems(10, "B", "B");
        study.addMultipleItems(5, "B", "C");
        study.addMultipleItems(5, "B", "D");
        study.addMultipleItems(5, "C", "B");
        study.addMultipleItems(10, "C", "C");
        study.addMultipleItems(5, "C", "D");
        study.addMultipleItems(20, "D", "D");
        return study;
    }

}
