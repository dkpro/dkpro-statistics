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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;

import java.util.Iterator;

import org.dkpro.statistics.agreement.InsufficientDataException;
import org.junit.jupiter.api.Test;

/**
 * Tests for several inter-rater agreement measures with two raters.
 * @author Christian M. Meyer
 */
public class TwoRaterAgreementTest {

    @Test
    public void testAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        PercentageAgreement pa = new PercentageAgreement(study);
        double agreement = pa.calculateAgreement();
        assertThat(agreement).isEqualTo(0.7);
        //TODO
        // double se = poa.standardError(agreement);
        // double[] ci = poa.confidenceInterval(agreement, se,
        //      TwoRaterObservedAgreement.CONFIDENCE_95);
        // assertThat(se).isCloseTo(0.045, offset(0.001));
        // assertThat(ci[0]).isCloseTo(0.610, offset(0.001));
        // assertThat(ci[1]).isCloseTo(0.789, offset(0.001));

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
    public void testItemSpecificAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        PercentageAgreement pa = new PercentageAgreement(study);
        Iterator<ICodingAnnotationItem> itemIter = study.getItems().iterator();
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(0.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(0.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(0.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(pa.calculateItemAgreement(itemIter.next())).isEqualTo(1.0);
        assertThat(itemIter.hasNext()).isFalse();
    }

    /*
    @Test
    public void testCategorySpecificAgreement() {
        ICodingAnnotationStudy study = createExample();

        new ContingencyMatrixPrinter().print(System.out, study);
        new CoincidenceMatrixPrinter().print(System.out, study);

        PercentageAgreement pa = new PercentageAgreement(study);
        assertThat(pa.calculateCategoryAgreement("low")).isEqualTo(4 / 7);
        assertThat(pa.calculateCategoryAgreement("high")).isEqualTo(10 / 13);
    }*/

    @Test
    public void testMissingCategories()
    {
        // Annotation categories not used by any rater must be added to
        // the study explicitly in order to avoid an InsufficientDataException.
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addCategory("A");
        study.addCategory("B");
        study.addItem("A", "A");
        study.addItem("A", "A");
        study.addItem("A", "A");

        PercentageAgreement pa = new PercentageAgreement(study);
        assertThat(pa.calculateAgreement()).isEqualTo(1.0);

        BennettSAgreement s = new BennettSAgreement(study);
        assertThat(s.calculateObservedAgreement()).isCloseTo(1.0, offset(0.001));
        assertThat(s.calculateExpectedAgreement()).isCloseTo(0.5, offset(0.001));
        assertThat(s.calculateAgreement()).isCloseTo(1.0, offset(0.001));

        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new ScottPiAgreement(study).calculateAgreement());
        
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(() -> new CohenKappaAgreement(study).calculateAgreement());
    }

    @Test
    public void testInsufficientData()
    {
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

    @Test
    public void testInvalidRaterCount()
    {
        CodingAnnotationStudy tooManyRatersStudy = new CodingAnnotationStudy(3);
        
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BennettSAgreement(tooManyRatersStudy));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ScottPiAgreement(tooManyRatersStudy));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CohenKappaAgreement(tooManyRatersStudy));
    }

    /** Creates an example annotation study. */
    public static ICodingAnnotationStudy createExample()
    {
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
