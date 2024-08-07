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

import java.util.Hashtable;

import org.dkpro.statistics.agreement.IAnnotationStudy;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;
import org.dkpro.statistics.agreement.distance.IntervalDistanceFunction;
import org.dkpro.statistics.agreement.distance.NominalDistanceFunction;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link WeightedKappaAgreement} and {@link KrippendorffAlphaAgreement}.
 * 
 * @author Christian M. Meyer
 */
public class WeightedAgreementTest
{
    @Test
    public void testDistanceFunction1()
    {
        ICodingAnnotationStudy study = createExample();

        IDistanceFunction weightedDistanceFunction = new IDistanceFunction()
        {
            @Override
            public double measureDistance(final IAnnotationStudy study, 
                    final Object category1, final Object category2) {
                final double[][] WEIGHTS = new double[][]{
                        {0, 1, 1, 1, 1, 1, 1},
                        {1, 0, 3, 1, 1, 1, 3},
                        {1, 3, 0, 1, 3, 1, 3},
                        {1, 1, 1, 0, 2, 1, 3},
                        {1, 1, 3, 2, 0, 1, 1},
                        {1, 1, 1, 1, 1, 0, 1},
                        {1, 3, 3, 3, 1, 1, 0}
                };
                final Hashtable<Object, Integer> idx = new Hashtable<Object, Integer>();
                idx.put("", 0);
                idx.put("A", 1);
                idx.put("B", 2);
                idx.put("AB", 3);
                idx.put("AC", 4);
                idx.put("ABC", 5);
                idx.put("C", 6);
                return WEIGHTS[idx.get(category1)][idx.get(category2)] / 3.0;
            }            
        };
        
        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, null);
        alpha.setDistanceFunction(weightedDistanceFunction);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.333, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.409, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.186, offset(0.001));    
    }
    
    
    @Test
    public void testDistanceFunction2()
    {
        ICodingAnnotationStudy study = createExample();
        
        IDistanceFunction weightedDistanceFunction = new IDistanceFunction()
        {
            @Override
            public double measureDistance(final IAnnotationStudy study, 
                    final Object category1, final Object category2) {
                final double[][] WEIGHTS = new double[][]{
                        {0 / 3.0, 1 / 3.0, 1 / 3.0, 1 / 3.0, 1 / 3.0, 1 / 3.0, 1 / 3.0},
                        {1 / 3.0, 0 / 3.0, 3 / 3.0, 1 / 6.0, 1 / 6.0, 2 / 9.0, 3 / 3.0},
                        {1 / 3.0, 3 / 3.0, 0 / 3.0, 1 / 6.0, 3 / 3.0, 2 / 9.0, 3 / 3.0},
                        {1 / 3.0, 1 / 6.0, 1 / 6.0, 0 / 3.0, 4 / 9.0, 1 / 9.0, 3 / 3.0},
                        {1 / 3.0, 1 / 6.0, 3 / 3.0, 4 / 9.0, 0 / 3.0, 1 / 9.0, 1 / 6.0},
                        {1 / 3.0, 2 / 9.0, 2 / 9.0, 1 / 9.0, 1 / 9.0, 0 / 3.0, 2 / 9.0},
                        {1 / 3.0, 3 / 3.0, 3 / 3.0, 3 / 3.0, 1 / 6.0, 2 / 9.0, 0 / 3.0}
                };
                final Hashtable<Object, Integer> idx = new Hashtable<Object, Integer>();
                idx.put("", 0);
                idx.put("A", 1);
                idx.put("B", 2);
                idx.put("AB", 3);
                idx.put("AC", 4);
                idx.put("ABC", 5);
                idx.put("C", 6);
                return WEIGHTS[idx.get(category1)][idx.get(category2)];
            }
        };
        
        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study, null);
        alpha.setDistanceFunction(weightedDistanceFunction);
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.253, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.338, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(0.252, offset(0.001));        
    }
    
    @Test
    public void testMissingVariance()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(11, 1, 1);
        study.addItem(1, 2);
        study.addItem(1, 3);

        KrippendorffAlphaAgreement alpha = new KrippendorffAlphaAgreement(study,
                new NominalDistanceFunction());
        assertThat(alpha.calculateObservedDisagreement()).isCloseTo(0.153, offset(0.001));
        assertThat(alpha.calculateExpectedDisagreement()).isCloseTo(0.150, offset(0.001));
        assertThat(alpha.calculateAgreement()).isCloseTo(-0.020, offset(0.001));

        WeightedKappaAgreement kappaW = new WeightedKappaAgreement(study,
                new NominalDistanceFunction());
        assertThat(kappaW.calculateObservedDisagreement()).isCloseTo(0.153, offset(0.001));
        assertThat(kappaW.calculateExpectedDisagreement()).isCloseTo(0.153, offset(0.001));
        assertThat(kappaW.calculateAgreement()).isCloseTo(0.000, offset(0.001));

        kappaW = new WeightedKappaAgreement(study, new IntervalDistanceFunction());
        assertThat(kappaW.calculateObservedDisagreement()).isCloseTo(0.096, offset(0.001));
        assertThat(kappaW.calculateExpectedDisagreement()).isCloseTo(0.096, offset(0.001));
        assertThat(kappaW.calculateAgreement()).isCloseTo(0.000, offset(0.001));
    }

    @Test
    public void testNormalization()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(11, 10, 10);
        study.addItem(10, 20);
        study.addItem(10, 30);

        WeightedKappaAgreement kappaW = new WeightedKappaAgreement(study,
                new NominalDistanceFunction());
        assertThat(kappaW.calculateObservedDisagreement()).isCloseTo(0.153, offset(0.001));
        assertThat(kappaW.calculateExpectedDisagreement()).isCloseTo(0.153, offset(0.001));
        assertThat(kappaW.calculateAgreement()).isCloseTo(0.000, offset(0.001));

        kappaW = new WeightedKappaAgreement(study, new IntervalDistanceFunction());
        assertThat(kappaW.calculateObservedDisagreement()).isCloseTo(0.096, offset(0.001));
        assertThat(kappaW.calculateExpectedDisagreement()).isCloseTo(0.096, offset(0.001));
        assertThat(kappaW.calculateAgreement()).isCloseTo(0.000, offset(0.001));
    }

    /** Creates an example annotation study. */
    public ICodingAnnotationStudy createExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem("", "");
        study.addItem("A", "");
        study.addItem("A", "A");
        study.addItem("A", "B");
        study.addItem("AB", "A");
        study.addItem("AB", "AC");
        study.addItem("ABC", "ABC");
        study.addItem("C", "AC");
        study.addItem("C", "AC");
        return study;
    }

}
