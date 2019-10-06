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
package org.dkpro.statistics.agreement;

/**
 * Default implementation of the {@link IAgreementMeasure} interface providing computational hooks
 * for calculating the observed and the expected agreement. The values for observed and expected
 * agreement are combined using Scott's (1955)<center>(A_O - A_E) / (1.0 - A_E)</center> formula
 * (where A_O denotes the observed agreement and A_E denotes the expected agreement. See also
 * {@link DisagreementMeasure} for the analogous definition of a measure based on the observed and
 * expected disagreement.<br>
 * <br>
 * References:
 * <ul>
 * <li>Artstein, R. &amp; Poesio, M.: Inter-Coder Agreement for Computational Linguistics.
 * Computational Linguistics 34(4):555-596, 2008.</li>
 * <li>Scott, W.A.: Reliability of content analysis: The case of nominal scale coding. Public
 * Opinion Quaterly 19(3):321-325, 1955.</li>
 * </ul>
 * 
 * @see IAgreementMeasure
 * @see DisagreementMeasure
 * @author Christian M. Meyer
 */
public abstract class AgreementMeasure
    implements IAgreementMeasure
{

    @Override
    public double calculateAgreement()
    {
        double A_O = calculateObservedAgreement();
        double A_E = calculateExpectedAgreement();
        if (A_E == 0.0) {
            return A_O;
        }
        else if (A_O == 1.0 && A_E == 1.0) {
            throw new InsufficientDataException(
                    "Insufficient variation. Most likely, the raters only used a single category which yields an expected agreement of 1.0. In this case, it is not possible to make any statement about the other categories and thus the agreement of the study itself.");
        }
        else {
            return (A_O - A_E) / (1.0 - A_E);
        }
    }

    protected abstract double calculateObservedAgreement();

    protected double calculateExpectedAgreement()
    {
        return 0.0;
    }
}
