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

import org.dkpro.statistics.agreement.IChanceCorrectedAgreement;

/**
 * Generalization of Bennett et al.'s (1954) S-measure for calculating a chance-corrected
 * inter-rater agreement for multiple raters, which is known as Randolph's (2005) kappa. The measure
 * assumes a uniform probability distribution for all raters and categories. It yields thus
 * identical results as Bennett's S.<br>
 * <br>
 * References:
 * <ul>
 * <li>Bennett, E.M.; Alpert, R. &amp; Goldstein, A.C.: Communications through limited response
 * questioning. Public Opinion Quarterly 18(3):303-308, 1954.</li>
 * <li>Randolph, J.J.: Free-marginal multirater kappa (multirater kappa_free): An alternative to
 * Fleiss' fixed-marginal multirater kappa. In: Proceedings of the 5th Joensuu University Learning
 * and Instruction Symposium, 2005.</li>
 * <li>Warrens, M.J.: Inequalities between multi-rater kappas. Advances in Data Analysis and
 * Classification 4(4):271-286, 2010.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
public class RandolphKappaAgreement
    extends CodingAgreementMeasure
    implements IChanceCorrectedAgreement
{

    /**
     * Initializes the instance for the given annotation study. The study may never be null.
     */
    public RandolphKappaAgreement(final ICodingAnnotationStudy study)
    {
        super(study);
    }

    /**
     * Calculates the expected inter-rater agreement that assumes a uniform distribution over all
     * raters and annotations.
     * 
     * @throws NullPointerException
     *             if the annotation study is null.
     * @throws ArithmeticException
     *             if there are no annotation categories.
     */
    @Override
    public double calculateExpectedAgreement()
    {
        return 1.0 / (double) study.getCategoryCount();
    }

}
