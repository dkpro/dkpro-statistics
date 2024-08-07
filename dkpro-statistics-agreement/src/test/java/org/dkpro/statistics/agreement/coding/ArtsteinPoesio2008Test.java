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

import org.dkpro.statistics.agreement.IAnnotationStudy;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests based on Artstein &amp; Poesio (2008) for several inter-rater agreement measures.<br>
 * <br>
 * References:
 * <ul>
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational Linguistics.
 * Computational Linguistics 34(4):555-596, 2008.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
class ArtsteinPoesio2008Test
{
    @Test
    void testExample1()
    {
        ICodingAnnotationStudy study = createExample1();

        // Two raters, observed agreement.
        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.7, offset(0.001));

        // Two raters, chance-corrected agreement.
        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateObservedAgreement()).isCloseTo(0.7, offset(0.001));
        assertThat(s.calculateExpectedAgreement()).isCloseTo(0.5, offset(0.001));
        assertThat(s.calculateAgreement()).isCloseTo(0.4, offset(0.001));

        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateObservedAgreement()).isCloseTo(0.7, offset(0.001));
        assertThat(pi.calculateExpectedAgreement()).isCloseTo(0.545, offset(0.001));
        assertThat(pi.calculateAgreement()).isCloseTo(0.341, offset(0.001));

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.7, offset(0.001));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.54, offset(0.001));
        assertThat(kappa.calculateAgreement()).isCloseTo(0.348, offset(0.001));
    }

    @Test
    public void testExample2()
    {
        ICodingAnnotationStudy study = createExample2();

        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.88, offset(0.001));

        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateObservedAgreement()).isCloseTo(0.88, offset(0.001));
        assertThat(s.calculateExpectedAgreement()).isCloseTo(0.333, offset(0.001));
        assertThat(s.calculateAgreement()).isCloseTo(0.82, offset(0.001));

        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateObservedAgreement()).isCloseTo(0.88, offset(0.001));
        assertThat(pi.calculateExpectedAgreement()).isCloseTo(0.401, offset(0.001));
        assertThat(pi.calculateAgreement()).isCloseTo(0.799, offset(0.001));

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.88, offset(0.001));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.396, offset(0.001));
        assertThat(kappa.calculateAgreement()).isCloseTo(0.801, offset(0.001));
    }

    /*
     * public void testCategoryAgreement() { //TODO positive and negative agreement!
     * ICodingAnnotationStudy study = createExample1();
     * 
     * PercentageAgreement cat = new PercentageAgreement(study); assertEquals(0.571,
     * cat.calculateCategoryAgreement("STAT"), 0.001); assertEquals(0.769,
     * cat.calculateCategoryAgreement("IReq"), 0.001); }
     */

    @Test
    public void testWeightedAgreement()
    {
        ICodingAnnotationStudy study = createExample2();
        IDistanceFunction weightedDistanceFunction = new IDistanceFunction()
        {
            @Override
            public double measureDistance(final IAnnotationStudy study, final Object category1,
                    final Object category2)
            {
                if (category1.equals(category2)) {
                    return 0.0;
                }
                if ("Chck".equals(category1) || "Chck".equals(category2)) {
                    return 0.5;
                }
                return 1.0;
            }
        };

        // Unweighted coefficients.
        PercentageAgreement poa = new PercentageAgreement(study);
        assertThat(poa.calculateAgreement()).isCloseTo(0.880, offset(0.001));

        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateObservedAgreement()).isCloseTo(0.880, offset(0.001));
        assertThat(s.calculateExpectedAgreement()).isCloseTo(0.333, offset(0.001));
        assertThat(s.calculateAgreement()).isCloseTo(0.820, offset(0.001));
        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertThat(pi.calculateObservedAgreement()).isCloseTo(0.880, offset(0.001));
        assertThat(pi.calculateExpectedAgreement()).isCloseTo(0.4014, offset(0.001));
        assertThat(pi.calculateAgreement()).isCloseTo(0.7995, offset(0.001));
        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertThat(kappa.calculateObservedAgreement()).isCloseTo(0.880, offset(0.001));
        assertThat(kappa.calculateExpectedAgreement()).isCloseTo(0.396, offset(0.001));
        assertThat(kappa.calculateAgreement()).isCloseTo(0.8013, offset(0.001));

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.120, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.601, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.800, offset(0.001));

        alpha.setDistanceFunction(weightedDistanceFunction);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.090, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.4879, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.8156, offset(0.001));

        WeightedKappaAgreement kappaW = new WeightedKappaAgreement(study,
                new NominalDistanceFunction());
        assertThat(kappaW.calculateObservedDisagreement()).isCloseTo(0.120, offset(0.001));
        assertThat(kappaW.calculateExpectedDisagreement()).isCloseTo(0.604, offset(0.001));
        assertThat(kappaW.calculateAgreement()).isCloseTo(0.8013, offset(0.001));

        kappaW.setDistanceFunction(weightedDistanceFunction);
        assertThat(kappaW.calculateObservedDisagreement()).isCloseTo(0.090, offset(0.001));
        assertThat(kappaW.calculateExpectedDisagreement()).isCloseTo(0.490, offset(0.001));
        assertThat(kappaW.calculateAgreement()).isCloseTo(0.8163, offset(0.001));
    }

    /**
     * Creates an example annotation study introduced by Artstein &amp; Poesio (2008: p. 558).
     */
    public static ICodingAnnotationStudy createExample1()
    {
        // @formatter:off
        //         STAT     IReq          Σ
        // STAT     20         10         30
        // IReq     20         50         70
        // Σ        40         60        100
        // @formatter:on
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(20, "STAT", "STAT");
        study.addMultipleItems(20, "IReq", "STAT");
        study.addMultipleItems(10, "STAT", "IReq");
        study.addMultipleItems(50, "IReq", "IReq");
        return study;
    }

    /**
     * Creates an example annotation study introduced by Artstein &amp; Poesio (2008: p. 568).
     */
    public static ICodingAnnotationStudy createExample2()
    {
        // @formatter:off
        //          STAT     IReq       Chck          Σ
        // STAT     46          0          0         46
        // IReq      6         32          6         44
        // Chck      0          0         10         10
        // Σ        52         32         16        100
        // @formatter:on
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(46, "STAT", "STAT");
        study.addMultipleItems(6, "IReq", "STAT");
        study.addMultipleItems(32, "IReq", "IReq");
        study.addMultipleItems(6, "IReq", "Chck");
        study.addMultipleItems(10, "Chck", "Chck");
        return study;
    }
}
