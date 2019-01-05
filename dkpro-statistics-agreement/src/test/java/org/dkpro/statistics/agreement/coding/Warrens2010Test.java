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
 * Tests based on Warrens (2010) for measuring several inter-rater agreement
 * measures with more than two raters.<br><br>
 * References:<ul>
 * <li>O'Malley, F.P.; Mohsin, S.K.; Badve, S.; Bose, S.; Collins, L.C.;
 *   Ennis, M.; Kleer, C.G.; Pinder, S.E. &amp; Schnitt, S.J.: Interobserver
 *   reproducibility in the diagnosis of flat epithelial atypia of the
 *   breast. Modern Pathology 19(2):172-179, 2006.</li>
 * <li>Warrens, M.J.: Inequalities between multi-rater kappas. Advances in
 *   Data Analysis and Classification 4(4):271-286, 2010.</li></ul>
 * @author Christian M. Meyer
 */
public class Warrens2010Test extends TestCase {

    /***/
    public void testAgreement() {
        ICodingAnnotationStudy study = createExample();

        RandolphKappaAgreement s = new RandolphKappaAgreement(study);
        assertEquals(0.8358, s.calculateAgreement(), 0.0001);

        FleissKappaAgreement pi = new FleissKappaAgreement(study);
        assertEquals(0.8324, pi.calculateAgreement(), 0.0001);

        HubertKappaAgreement kappaH = new HubertKappaAgreement(study);
        assertEquals(0.8326, kappaH.calculateAgreement(), 0.0001);

        //TODO LightKappaAgreement kappaL = new LightKappaAgreement(study);
        //assertEquals(0.8325, kappaL.calculateAgreement(), 0.0001);
    }

    /***/
    public void testPairwiseEquivalenceHubertKappa() {
        CodingAnnotationStudy study = createExample();

        HubertKappaAgreement kappaH = new HubertKappaAgreement(study);
        assertEquals(0.8326, kappaH.calculateAgreement(), 0.0001);

        double AOmean = 0.0;
        for (int r1 = 0; r1 < study.getRaterCount(); r1++) {
            for (int r2 = r1 + 1; r2 < study.getRaterCount(); r2++) {
                ICodingAnnotationStudy pairwiseStudy = study.extractRaters(r1, r2);
                AOmean += new CohenKappaAgreement(pairwiseStudy).calculateObservedAgreement();
            }
        }
        AOmean *= 2.0;
        AOmean /= (double) (study.getRaterCount() * (double) (study.getRaterCount() - 1.0));
        double AE = kappaH.calculateExpectedAgreement();
        double kappaEquivalence = (AOmean - AE) / (1.0 - AE);
        assertEquals(0.8326, kappaEquivalence, 0.0001);
    }

    /***/
    public void testPairwiseEquivalenceLightKappa() {
        CodingAnnotationStudy study = createExample();

        //TODO LightKappaAgreement kappaL = new LightKappaAgreement(study);
        //assertEquals(0.8325, kappaL.calculateAgreement(), 0.0001);

        double kappaEquivalence = 0.0;
        for (int r1 = 0; r1 < study.getRaterCount(); r1++) {
            for (int r2 = r1 + 1; r2 < study.getRaterCount(); r2++) {
                ICodingAnnotationStudy pairwiseStudy = study.extractRaters(r1, r2);
                kappaEquivalence += new CohenKappaAgreement(pairwiseStudy).calculateAgreement();
            }
        }
        kappaEquivalence *= 2.0;
        kappaEquivalence /= (double) (study.getRaterCount()
                * (double) (study.getRaterCount() - 1.0));
        assertEquals(0.8325, kappaEquivalence, 0.0001);
    }

    /** Creates an example annotation study introduced by
     *  Warrens (2010: p. 284), based on O'Malley et al. (2006: p. 176). */
    public static CodingAnnotationStudy createExample() {
        CodingAnnotationStudy study = new CodingAnnotationStudy(8);
        study.addMultipleItems(14, "N", "N", "N", "N", "N", "N", "N", "N");
        study.addMultipleItems( 1, "N", "N", "N", "A", "N", "N", "N", "N");
        study.addMultipleItems( 1, "N", "N", "N", "N", "N", "N", "N", "A");
        study.addMultipleItems(10, "A", "A", "A", "A", "A", "A", "A", "A");
        study.addMultipleItems( 2, "A", "A", "N", "A", "A", "A", "A", "N");
        study.addMultipleItems( 1, "A", "A", "N", "A", "N", "A", "A", "N");
        study.addMultipleItems( 1, "A", "A", "N", "A", "N", "A", "N", "N");
        return study;
    }

}
