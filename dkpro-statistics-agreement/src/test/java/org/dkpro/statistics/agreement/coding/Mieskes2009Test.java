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

import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.FleissKappaAgreement;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;

import junit.framework.TestCase;

/**
 * Tests based on Mieskes (2009) for category-specific agreement using
 * {@link FleissKappaAgreement}.<br><br>
 * References:<ul>
 * <li>Mieskes, M.: Exploring Methods for the Automatic Summarization of 
 *   Meetings. Dissertation. Friedrich-Alexander-Universität 
 *   Erlangen-Nürnberg, 2009.</li></ul>
 * @author Christian M. Meyer
 * @author Margot Mieskes
 */
public class Mieskes2009Test extends TestCase {

    /***/
    public void testAgreement() {
        ICodingAnnotationStudy study = createExample();

        FleissKappaAgreement kappa = new FleissKappaAgreement(study);
        assertEquals(0.84, kappa.calculateObservedAgreement(), 0.01);
        assertEquals(0.20, kappa.calculateExpectedAgreement(), 0.01);
        assertEquals(0.80, kappa.calculateAgreement(), 0.01);
        
        assertEquals(1.00, kappa.calculateCategoryAgreement("INP"), 0.01);
        assertEquals(1.00, kappa.calculateCategoryAgreement("JJ"), 0.01);
        assertEquals(1.00, kappa.calculateCategoryAgreement("MD"), 0.01);
        assertEquals(0.48, kappa.calculateCategoryAgreement("NN"), 0.01);
        assertEquals(1.00, kappa.calculateCategoryAgreement("PRP"), 0.01);
        assertEquals(0.56, kappa.calculateCategoryAgreement("RB"), 0.01);
        assertEquals(0.67, kappa.calculateCategoryAgreement("UH"), 0.01);
        assertEquals(0.29, kappa.calculateCategoryAgreement("VB"), 0.01);
        assertEquals(-0.02, kappa.calculateCategoryAgreement("VBP"), 0.01);
    }

    /** Creates an example annotation study introduced by 
     *  Mieskes (2009: p. 58). */
    public static ICodingAnnotationStudy createExample() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(3);
        study.addItem("UH", "VB", "UH");
        study.addItem("INP", "INP", "INP");
        study.addItem("UH", "UH", "UH");
        study.addItem("INP", "INP", "INP");
        study.addItem("RB", "RB", "RB");
        study.addItem("JJ", "JJ", "JJ");
        study.addItem("INP", "INP", "INP");
        study.addItem("PRP", "PRP", "PRP");
        study.addItem("NN", "NN", "RB");
        study.addItem("VB", "VBP", "VB");
        study.addItem("PRP", "PRP", "PRP");
        study.addItem("INP", "INP", "INP");
        study.addItem("RB", "UH", "UH");
        study.addItem("INP", "INP", "INP");
        study.addItem("MD", "MD", "MD");
        study.addItem("PRP", "PRP", "PRP");
        study.addItem("INP", "INP", "INP");
        return study;
    }
    
}
