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

import junit.framework.TestCase;

/**
 * Tests based on Agresti (1992) for category-specific agreement using
 * {@link CohenKappaAgreement}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Agresti, A.: Modelling patterns of agreement and disagreement. Statistical Methods in Medical
 * Research 1(2): 201-218, 1992.</li>
 * <li>Landis J.R. &amp; Koch G.G.: An application of hierarchical kappa-type statistics in the
 * assessment of majority agreement among multiple observers. Biometrics 33:363-374, 1977.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class Agresti1992Test
    extends TestCase
{

    
    public void testAgreement()
    {
        ICodingAnnotationStudy study = createExample();

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertEquals(0.493, kappa.calculateAgreement(), 0.001);

        // TODO: assertEquals(0.057, getStandardError(), 0.001);

        assertEquals(0.781, kappa.calculateCategoryAgreement(1), 0.001);
        assertEquals(0.247, kappa.calculateCategoryAgreement(2), 0.001);
        assertEquals(0.402, kappa.calculateCategoryAgreement(3), 0.001);
        assertEquals(0.435, kappa.calculateCategoryAgreement(4), 0.001);
    }

    /**
     * Creates an example annotation study introduced by Agresti (1992: p. 202), originally derived
     * from Landis &amp; Koch (1977).
     */
    public static ICodingAnnotationStudy createExample()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(22, 1, 1);
        study.addMultipleItems(2, 1, 2);
        study.addMultipleItems(2, 1, 3);
        study.addMultipleItems(5, 2, 1);
        study.addMultipleItems(7, 2, 2);
        study.addMultipleItems(14, 2, 3);
        study.addMultipleItems(2, 3, 2);
        study.addMultipleItems(36, 3, 3);
        study.addMultipleItems(1, 4, 2);
        study.addMultipleItems(17, 4, 3);
        study.addMultipleItems(10, 4, 4);
        return study;
    }

}
