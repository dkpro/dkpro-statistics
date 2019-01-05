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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Iterator;

import org.dkpro.statistics.agreement.InsufficientDataException;

import junit.framework.TestCase;

/**
 * Tests for several inter-rater agreement measures with two raters.
 * @author Christian M. Meyer
 */
public class TwoRaterAgreementTest extends TestCase {

    /***/
    public void testAgreement() {
        ICodingAnnotationStudy study = createExample();

        PercentageAgreement pa = new PercentageAgreement(study);
        double agreement = pa.calculateAgreement();
        assertEquals(0.7, agreement);
        //TODO
        // double se = poa.standardError(agreement);
        // double[] ci = poa.confidenceInterval(agreement, se,
        //      TwoRaterObservedAgreement.CONFIDENCE_95);
        // assertEquals(0.045, se, 0.001);
        // assertEquals(0.610, ci[0], 0.001);
        // assertEquals(0.789, ci[1], 0.001);

        BennettSAgreement s = new BennettSAgreement(study);
        assertEquals(0.7, s.calculateObservedAgreement(), 0.001);
        assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
        assertEquals(0.4, s.calculateAgreement(), 0.001);

        ScottPiAgreement pi = new ScottPiAgreement(study);
        assertEquals(0.7, pi.calculateObservedAgreement(), 0.001);
        assertEquals(0.545, pi.calculateExpectedAgreement(), 0.001);
        assertEquals(0.341, pi.calculateAgreement(), 0.001);

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertEquals(0.7, kappa.calculateObservedAgreement(), 0.001);
        assertEquals(0.54, kappa.calculateExpectedAgreement(), 0.001);
        assertEquals(0.348, kappa.calculateAgreement(), 0.001);
    }

    /***/
    public void testItemSpecificAgreement() {
        ICodingAnnotationStudy study = createExample();

        PercentageAgreement pa = new PercentageAgreement(study);
        Iterator<ICodingAnnotationItem> itemIter = study.getItems().iterator();
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(0.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(0.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(0.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertEquals(1.0, pa.calculateItemAgreement(itemIter.next()));
        assertFalse(itemIter.hasNext());
    }

    /* ** /
    public void testCategorySpecificAgreement() {
        ICodingAnnotationStudy study = createExample();

        new ContingencyMatrixPrinter().print(System.out, study);
        new CoincidenceMatrixPrinter().print(System.out, study);

        PercentageAgreement pa = new PercentageAgreement(study);
        assertEquals(4 / 7, pa.calculateCategoryAgreement("low"));
        assertEquals(10 / 13, pa.calculateCategoryAgreement("high"));
    }*/


    public void testMissingCategories() {
        // Annotation categories not used by any rater must be added to
        // the study explicitly in order to avoid an InsufficientDataException.
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addCategory("A");
        study.addCategory("B");
        study.addItem("A", "A");
        study.addItem("A", "A");
        study.addItem("A", "A");

        PercentageAgreement pa = new PercentageAgreement(study);
        assertEquals(1.0, pa.calculateAgreement());

        BennettSAgreement s = new BennettSAgreement(study);
        assertEquals(1.0, s.calculateObservedAgreement(), 0.001);
        assertEquals(0.5, s.calculateExpectedAgreement(), 0.001);
        assertEquals(1.0, s.calculateAgreement(), 0.001);

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new ScottPiAgreement(study).calculateAgreement());
        
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new CohenKappaAgreement(study).calculateAgreement());
    }


    /***/
    public void testInsufficientData() {
        // Empty annotation study.
        CodingAnnotationStudy emptyStudy = new CodingAnnotationStudy(2);

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new BennettSAgreement(emptyStudy).calculateAgreement());

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new ScottPiAgreement(emptyStudy).calculateAgreement());
        
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new CohenKappaAgreement(emptyStudy).calculateAgreement());
        
        // Annotation study with single category.
        CodingAnnotationStudy singleCategoryStudy = new CodingAnnotationStudy(2);
        singleCategoryStudy.addItem("A", "A");
        singleCategoryStudy.addItem("A", "A");
        singleCategoryStudy.addItem("A", "A");

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new BennettSAgreement(singleCategoryStudy).calculateAgreement());
        
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new ScottPiAgreement(singleCategoryStudy).calculateAgreement());
        
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new CohenKappaAgreement(singleCategoryStudy)
                        .calculateAgreement());
    }

    /***/
    public void testInvalidRaterCount() {
        CodingAnnotationStudy tooManyRatersStudy = new CodingAnnotationStudy(3);
        
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BennettSAgreement(tooManyRatersStudy));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ScottPiAgreement(tooManyRatersStudy));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CohenKappaAgreement(tooManyRatersStudy));
    }

    /** Creates an example annotation study. */
    public static ICodingAnnotationStudy createExample() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem("high", "high");
        study.addItem("high", "high");
        study.addItem("high", "low");
        study.addItem("low", "high");
        study.addItem("low", "low");
        study.addItem("low", "low");
        study.addItem("low", "low");
        study.addItem("low", "high");
        study.addItem("low", "low");
        study.addItem("low", "low");
        return study;
    }

}
