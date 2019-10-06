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
 * Implementation of Bennett et al.'s S (1954) for calculating a chance-corrected inter-rater
 * agreement for two raters. The measure assumes a uniform probability distribution for all raters
 * and categories. The measure is equivalent to Janson and Vegelius's (1979) C and Brennan and
 * Prediger's (1981) kappa_n.<br>
 * <br>
 * References:
 * <ul>
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational Linguistics.
 * Computational Linguistics 34(4):555-596, 2008.</li>
 * <li>Bennett, E.M.; Alpert, R. &amp; Goldstein, A.C.: Communications through limited response
 * questioning. Public Opinion Quarterly 18(3):303-308, 1954.</li>
 * <li>Brennan, R.L. &amp; Prediger, D.: Coefficient kappa: Some uses, misuses, and alternatives.
 * Educational and Psychological Measurement 41(3):687-699, 1981.</li>
 * <li>Janson, S. &amp; Vegelius, J.: On generalizations of the G index and the phi coefficient to
 * nominal scales. Multivariate Behavioral Research 14(2):255-269, 1979.</li>
 * </ul>
 * 
 * @author Christian M. Meyer
 */
// TODO: Check correspondence to Guilford's (1961; Holley & Guilford, 1964)
// G index; and Maxwell's (1977) random error (RE) coefficient - Zwick88
public class BennettSAgreement
    extends CodingAgreementMeasure
    implements IChanceCorrectedAgreement
{

    /**
     * Initializes the instance for the given annotation study. The study may never be null.
     */
    public BennettSAgreement(final ICodingAnnotationStudy study)
    {
        super(study);
        ensureTwoRaters();
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
