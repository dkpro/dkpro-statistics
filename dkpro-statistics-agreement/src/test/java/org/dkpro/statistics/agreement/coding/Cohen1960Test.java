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
 * Tests based on Cohen (1960) for measuring 
 * {@link CohenKappaAgreement}.<br><br>
 * References:<ul>
 * <li>Cohen, J.: A Coefficient of Agreement for Nominal Scales. 
 *   Educational and Psychological Measurement 20(1):37-46, 1960.</li></ul>
 * @author Christian M. Meyer
 */
public class Cohen1960Test extends TestCase {

    /***/
    public void testExample1() {
        ICodingAnnotationStudy study = createExample1();

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertEquals(0.29, kappa.calculateObservedAgreement(), 0.01);
        assertEquals(0.35, kappa.calculateExpectedAgreement(), 0.01);
        assertEquals(0.90, kappa.calculateMaximumObservedAgreement(), 0.01);
        assertEquals(0.85, kappa.calculateMaximumAgreement(), 0.01);
    }

    /***/
    public void testExample2() {
        ICodingAnnotationStudy study = createExample2();

        CohenKappaAgreement kappa = new CohenKappaAgreement(study);
        assertEquals(0.70, kappa.calculateObservedAgreement(), 0.01);
        assertEquals(0.41, kappa.calculateExpectedAgreement(), 0.01);
        assertEquals(0.492, kappa.calculateAgreement(), 0.001);
        assertEquals(0.831, kappa.calculateMaximumAgreement(), 0.001);
    }

    /** Creates an example annotation study introduced by 
     *  Cohen (1960: p. 37). */
    public static ICodingAnnotationStudy createExample1() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(25, 1, 1);
        study.addMultipleItems(12, 1, 2);
        study.addMultipleItems( 3, 1, 3);        
        study.addMultipleItems(13, 2, 1);
        study.addMultipleItems( 2, 2, 2);
        study.addMultipleItems(15, 2, 3);
        study.addMultipleItems(12, 3, 1);
        study.addMultipleItems(16, 3, 2);
        study.addMultipleItems( 2, 3, 3);
        return study;
    }
    
    /** Creates an example annotation study introduced by 
     *  Cohen (1960: p. 45). */
    public static ICodingAnnotationStudy createExample2() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addMultipleItems(88, 1, 1);
        study.addMultipleItems(14, 1, 2);
        study.addMultipleItems(18, 1, 3);        
        study.addMultipleItems(10, 2, 1);
        study.addMultipleItems(40, 2, 2);
        study.addMultipleItems(10, 2, 3);
        study.addMultipleItems( 2, 3, 1);
        study.addMultipleItems( 6, 3, 2);
        study.addMultipleItems(12, 3, 3);
        return study;
    }

}
