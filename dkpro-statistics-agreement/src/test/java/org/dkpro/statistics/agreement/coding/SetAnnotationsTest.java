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

import org.dkpro.statistics.agreement.distance.MASISetAnnotationDistanceFunction;
import org.dkpro.statistics.agreement.distance.SetAnnotation;
import org.dkpro.statistics.agreement.distance.SetAnnotationDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link KrippendorffAlphaAgreement} with {@link SetAnnotationDistanceFunction} and
 * {@link MASISetAnnotationDistanceFunction}.
 * 
 * @author Christian M. Meyer
 * @author Tristan Miller
 */
public class SetAnnotationsTest
{
    @Test
    public void testSetDistanceFunction()
    {
        ICodingAnnotationStudy study = createExample();

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, null);
        alpha.setDistanceFunction(new SetAnnotationDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.333, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.409, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.186, offset(0.001));
    }

    @Test
    public void testMASIDistanceFunction()
    {
        ICodingAnnotationStudy study = createExample();

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, null);
        alpha.setDistanceFunction(new MASISetAnnotationDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.253, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.338, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.252, offset(0.001));
    }

    @Test
    public void testPercentageAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        PercentageAgreement percentageAgreement = new PercentageAgreement(study);
        assertThat(percentageAgreement.calculateAgreement()).isCloseTo(0.333, offset(0.001));
    }

    @Test
    public void testMaxPercentageAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        MaxPercentageAgreement maxPercentageAgreement = new MaxPercentageAgreement(study);
        assertThat(maxPercentageAgreement.calculateAgreement()).isCloseTo(0.667, offset(0.001));
    }

    @Test
    public void testDiceAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        DiceAgreement DiceAgreement = new DiceAgreement(study);
        assertThat(DiceAgreement.calculateAgreement()).isCloseTo(0.5, offset(0.001));
    }

    /** Creates an example annotation study. */
    public ICodingAnnotationStudy createExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(new SetAnnotation(), new SetAnnotation());
        study.addItem(new SetAnnotation("A"), new SetAnnotation());
        study.addItem(new SetAnnotation("A"), new SetAnnotation("A"));
        study.addItem(new SetAnnotation("A"), new SetAnnotation("B"));
        study.addItem(new SetAnnotation("A", "B"), new SetAnnotation("A"));
        study.addItem(new SetAnnotation("A", "B"), new SetAnnotation("A", "C"));
        study.addItem(new SetAnnotation("A", "B", "C"), new SetAnnotation("A", "B", "C"));
        study.addItem(new SetAnnotation("C"), new SetAnnotation("A", "C"));
        study.addItem(new SetAnnotation("C"), new SetAnnotation("A", "C"));
        return study;
    }
}
