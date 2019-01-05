/*******************************************************************************
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
 ******************************************************************************/
package org.dkpro.statistics.agreement.unitizing;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.dkpro.statistics.agreement.InsufficientDataException;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.CohenKappaAgreement;
import org.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;
import org.dkpro.statistics.agreement.coding.ScottPiAgreement;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;

import junit.framework.TestCase;

/**
 * Tests based on Krippendorff (1995) for measuring
 * {@link KrippendorffAlphaUnitizingAgreement}.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: On the reliability of unitizing contiguous data.
 *   Sociological Methodology 25:47–76, 1995.</li></ul>
 * @author Christian M. Meyer
 */
public class Krippendorff1995Test
    extends TestCase
{
    public void testDigitizedAgreement() {
//        IUnitizingAnnotationStudy study = createExample(1, "A");
//        ICodingAnnotationStudy digitizedStudy = study.digitize();

        //TODO: Digitize method!
        // @formatter:off
        //    A Bertha:  0 0|1 1 1 1 1 1 1 1|0 0 0 0|1 1 1 1 1 1|0 0 0 0
        //      Bill:    0 0 0 0|1 1 1 1|0 0 0 0 0 0 0|1 1|0 0 0 0 0 0 0
        // @formatter:on
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(1, 0);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);

        assertEquals(0.385, new CohenKappaAgreement(study).calculateAgreement(), 0.001);
        assertEquals(0.314, new ScottPiAgreement(study).calculateAgreement(), 0.001);
        assertEquals(0.329, new KrippendorffAlphaAgreement(study, new NominalDistanceFunction())
                .calculateAgreement(), 0.001);

        // @formatter:off
        //  B John:    1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1|0 0 0 0 0 0
        //    Jill:    1 1|1|1|1|1|1 1 1|1|0 0 0 0 0 0 0 0 0 0 0 0 0 0
        // @formatter:on
        study = new CodingAnnotationStudy(2);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(0, 0);

        assertEquals(0.385, new CohenKappaAgreement(study).calculateAgreement(), 0.001);
        assertEquals(0.314, new ScottPiAgreement(study).calculateAgreement(), 0.001);

        // @formatter:off
        //    C Gerret:  0 0|1 1 1 1 1 1|0 0|1 1|0 0|1 1 1 1|0 0|1 1|0 0
        //      Gill:    1 1|0 0|1 1 1 1|0 0|1 1 1 1|0 0|1 1|0 0|1 1|0 0
        // @formatter:on
        study = new CodingAnnotationStudy(2);
        study.addItem(0, 1);
        study.addItem(0, 1);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(0, 1);
        study.addItem(0, 1);
        study.addItem(1, 0);
        study.addItem(1, 0);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(0, 0);
        study.addItem(0, 0);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(0, 0);
        study.addItem(0, 0);

        assertEquals(0.314, new CohenKappaAgreement(study).calculateAgreement(), 0.001);
        assertEquals(0.314, new ScottPiAgreement(study).calculateAgreement(), 0.001);
    }
    
    public void testThatAgreementThrowsInsufficientDataException()
    {
        // @formatter:off
        //  D Heather:    1 1|1 1 1 1 1 1 1 1|1 1 1 1|1 1 1 1 1 1|1 1 1 1
        //       Hill:    1 1 1 1|1 1 1 1|1 1 1 1 1 1 1|1 1|1 1 1 1 1 1 1
        // @formatter:on
        
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);
        study.addItem(1, 1);

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new ScottPiAgreement(study).calculateAgreement());

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new CohenKappaAgreement(study).calculateAgreement());
    }

    public void testVaryingLengths() {
        IUnitizingAnnotationStudy study;
        KrippendorffAlphaUnitizingAgreement alpha;

        // There seems to be a small error in the computation of D_E in the
        // original reference. Possibly related to (l_ik + l_hj + 1) where the
        // first plus should be a minus - see Krippendorff (2004).

        study = createExample(12 / 24.0);
//        new UnitizingStudyPrinter().print(System.out, study, "C");
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.05494, alpha.calculateExpectedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.494, alpha.calculateCategoryAgreement("C"), 0.02);

        study = createExample(120 / 24.0);
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.08234, alpha.calculateExpectedCategoryDisagreement("C"), 0.005);
        assertEquals(0.663, alpha.calculateCategoryAgreement("C"), 0.02);

        study = createExample(1200 / 24.0);
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.08600, alpha.calculateExpectedCategoryDisagreement("C"), 0.005);
        assertEquals(0.677, alpha.calculateCategoryAgreement("C"), 0.02);

        study = createExample(12000 / 24.0);
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.08638, alpha.calculateExpectedCategoryDisagreement("C"), 0.005);
        assertEquals(0.678, alpha.calculateCategoryAgreement("C"), 0.02);

        study = createExample(120000 / 24.0);
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.08642, alpha.calculateExpectedCategoryDisagreement("C"), 0.005);
        assertEquals(0.679, alpha.calculateCategoryAgreement("C"), 0.02);

        study = createExample(1200000 / 24.0);
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.08642, alpha.calculateExpectedCategoryDisagreement("C"), 0.005);
        assertEquals(0.679, alpha.calculateCategoryAgreement("C"), 0.02);
    }

    /***/
    public void testAgreement() {
        IUnitizingAnnotationStudy study = createExample(1);
//        new UnitizingStudyPrinter().print(System.out, study);
//        new UnitizingMatrixPrinter().print(System.out, study, "A", 0, 1);

        KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.03125, alpha.calculateObservedCategoryDisagreement("A"), 0.00001);
        assertEquals(2.26736, alpha.calculateObservedCategoryDisagreement("B"), 0.00001);
        assertEquals(0.02777, alpha.calculateObservedCategoryDisagreement("C"), 0.00001);
        assertEquals(0.38715, alpha.calculateObservedCategoryDisagreement("D"), 0.00001);

        study = createExample(1200000 / 24);
        alpha = new KrippendorffAlphaUnitizingAgreement(study);
        assertEquals(0.06990, alpha.calculateExpectedCategoryDisagreement("A"), 0.005);
        assertEquals(1.17731, alpha.calculateExpectedCategoryDisagreement("B"), 0.005);
        assertEquals(0.08642, alpha.calculateExpectedCategoryDisagreement("C"), 0.005);
        assertEquals(0.41445, alpha.calculateExpectedCategoryDisagreement("D"), 0.005);

        assertEquals( 0.553, alpha.calculateCategoryAgreement("A"), 0.02);
        assertEquals(-0.926, alpha.calculateCategoryAgreement("B"), 0.02);
        assertEquals( 0.679, alpha.calculateCategoryAgreement("C"), 0.02);
        assertEquals( 0.066, alpha.calculateCategoryAgreement("D"), 0.02);
    }


    /** Creates an example annotation study introduced by
     *  Krippendorff (1995: p. 57). */
    public static UnitizingAnnotationStudy createExample(double stretch) {
        UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(2,
                (int) (0 * stretch), (int) (24 * stretch));
        // @formatter:off
        //      A Bertha:  0 0|1 1 1 1 1 1 1 1|0 0 0 0|1 1 1 1 1 1|0 0 0 0
        //        Bill:    0 0 0 0|1 1 1 1|0 0 0 0 0 0 0|1 1|0 0 0 0 0 0 0
        //      B John:    1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1|0 0 0 0 0 0
        //        Jill:    1 1|1|1|1|1|1 1 1|1|0 0 0 0 0 0 0 0 0 0 0 0 0 0
        //      C Gerret:  0 0|1 1 1 1 1 1|0 0|1 1|0 0|1 1 1 1|0 0|1 1|0 0
        //        Gill:    1 1|0 0|1 1 1 1|0 0|1 1 1 1|0 0|1 1|0 0|1 1|0 0
        //      D Heather: 1 1|1 1 1 1 1 1 1 1|1 1 1 1|1 1 1 1 1 1|1 1 1 1
        //        Hill:    1 1 1 1|1 1 1 1|1 1 1 1 1 1 1|1 1|1 1 1 1 1 1 1
        // @formatter:on

        study.addUnit((int) ( 2 * stretch), (int) ( 8 * stretch), 0, "A");
        study.addUnit((int) (14 * stretch), (int) ( 6 * stretch), 0, "A");
        study.addUnit((int) ( 4 * stretch), (int) ( 4 * stretch), 1, "A");
        study.addUnit((int) (15 * stretch), (int) ( 2 * stretch), 1, "A");

        study.addUnit((int) ( 0 * stretch), (int) (18 * stretch), 0, "B");
        study.addUnit((int) ( 0 * stretch), (int) ( 2 * stretch), 1, "B");
        study.addUnit((int) ( 2 * stretch), (int) ( 1 * stretch), 1, "B");
        study.addUnit((int) ( 3 * stretch), (int) ( 1 * stretch), 1, "B");
        study.addUnit((int) ( 4 * stretch), (int) ( 1 * stretch), 1, "B");
        study.addUnit((int) ( 5 * stretch), (int) ( 1 * stretch), 1, "B");
        study.addUnit((int) ( 6 * stretch), (int) ( 3 * stretch), 1, "B");
        study.addUnit((int) ( 9 * stretch), (int) ( 1 * stretch), 1, "B");

        study.addUnit((int) ( 2 * stretch), (int) ( 6 * stretch), 0, "C");
        study.addUnit((int) (10 * stretch), (int) ( 2 * stretch), 0, "C");
        study.addUnit((int) (14 * stretch), (int) ( 4 * stretch), 0, "C");
        study.addUnit((int) (20 * stretch), (int) ( 2 * stretch), 0, "C");
        study.addUnit((int) ( 0 * stretch), (int) ( 2 * stretch), 1, "C");
        study.addUnit((int) ( 4 * stretch), (int) ( 4 * stretch), 1, "C");
        study.addUnit((int) (10 * stretch), (int) ( 4 * stretch), 1, "C");
        study.addUnit((int) (16 * stretch), (int) ( 2 * stretch), 1, "C");
        study.addUnit((int) (20 * stretch), (int) ( 2 * stretch), 1, "C");

        study.addUnit((int) ( 0 * stretch), (int) ( 2 * stretch), 0, "D");
        study.addUnit((int) ( 2 * stretch), (int) ( 8 * stretch), 0, "D");
        study.addUnit((int) (10 * stretch), (int) ( 4 * stretch), 0, "D");
        study.addUnit((int) (14 * stretch), (int) ( 6 * stretch), 0, "D");
        study.addUnit((int) (20 * stretch), (int) ( 4 * stretch), 0, "D");
        study.addUnit((int) ( 0 * stretch), (int) ( 4 * stretch), 1, "D");
        study.addUnit((int) ( 4 * stretch), (int) ( 4 * stretch), 1, "D");
        study.addUnit((int) ( 8 * stretch), (int) ( 7 * stretch), 1, "D");
        study.addUnit((int) (15 * stretch), (int) ( 2 * stretch), 1, "D");
        study.addUnit((int) (17 * stretch), (int) ( 7 * stretch), 1, "D");

        return study;
    }
}
