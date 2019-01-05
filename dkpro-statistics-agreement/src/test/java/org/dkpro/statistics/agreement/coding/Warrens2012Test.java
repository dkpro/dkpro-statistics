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
package org.dkpro.statistics.agreement.coding;

import junit.framework.TestCase;

/**
 * Tests based on Warrens (2012) for measuring
 * {@link HubertKappaAgreement}.<br><br>
 * References:<ul>
 * <li>Warrens, M.J.: On the Equivalence of Multirater Kappas Based on 
 *   2-Agreement and 3-Agreement with Binary Scores. ISRN Probability and 
 *   Statistics 2012(656390):1-11, 2012.</li></ul>
 * @author Christian M. Meyer
 */
public class Warrens2012Test extends TestCase {
    
    /***/
    public void testExample1() {
        CodingAnnotationStudy study = createExample1();
        
        HubertKappaAgreement kappaH = new HubertKappaAgreement(study);
        assertEquals(0.802479, kappaH.calculateAgreement(), 0.000001);

        // There is an error in the denominator of equation (3.2):
        // It should read 1 − (8/15)(2/3) − (7/15)(1/3) = 22/45
        ICodingAnnotationStudy pairwiseStudy = study.extractRaters(0, 1);
        CohenKappaAgreement kappa2 = new CohenKappaAgreement(pairwiseStudy);
        assertEquals(8.0 / 15.0 + 1.0 / 3.0, kappa2.calculateObservedAgreement(), 0.0001);
        assertEquals((8.0 / 15.0) * (2.0 / 3.0) + (7.0 / 15.0) * (1.0 / 3.0),
                kappa2.calculateExpectedAgreement(), 0.0001);
        assertEquals(8.0 / 11.0, kappa2.calculateAgreement(), 0.0001);
    }

    /***/
    public void testExample2a() {
        ICodingAnnotationStudy study = createExample2a();
                        
        // There seems to be another error in the cacluation of k(4,2),
        // cross-checked pairwise equivalence!
        // When switching the frequencies 6 and 4 in the example, 
        // we end up at a k(4,2) of 0.645!
        HubertKappaAgreement kappaH = new HubertKappaAgreement(study);
        //assertEquals(0.645, kappaH.calculateAgreement(), 0.001);
        assertEquals(0.675, kappaH.calculateAgreement(), 0.001);
        
//        assertEquals(0.599, kappa(4,4).calculateAgreement(), 001);
    }

    /***/
    public void testExample2b() {
        ICodingAnnotationStudy study = createExample2b();
                
        HubertKappaAgreement kappaH = new HubertKappaAgreement(study);
        assertEquals(0.564, kappaH.calculateAgreement(), 0.001);
        
//        assertEquals(0.625, kappa(4,4).calculateAgreement(), 001);
    }

    /** Creates an example annotation study introduced by 
     *  Warrens (2012: p. 3). */
    public static CodingAnnotationStudy createExample1() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(4);
        study.addMultipleItems(10, 1, 1, 1, 1);        
        study.addMultipleItems( 2, 1, 0, 1, 0);
        study.addMultipleItems( 2, 1, 0, 0, 0);
        study.addMultipleItems( 1, 0, 0, 0, 1);
        study.addMultipleItems(15, 0, 0, 0, 0);
        return study;
    }
    
    /** Creates an example annotation study introduced by 
     *  Warrens (2012: p. 9). */
    public static CodingAnnotationStudy createExample2a() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(4);
        study.addMultipleItems(6, 1, 1, 1, 1);
        study.addMultipleItems(5, 1, 0, 0, 0);
        study.addMultipleItems(4, 0, 0, 0, 0);
        return study;
    }

    /** Creates an example annotation study introduced by 
     *  Warrens (2012: p. 9). */
    public static CodingAnnotationStudy createExample2b() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(4);
        study.addMultipleItems(6, 1, 1, 1, 1);
        study.addMultipleItems(5, 1, 0, 1, 0);
        study.addMultipleItems(4, 0, 0, 0, 0);
        return study;
    }

}
