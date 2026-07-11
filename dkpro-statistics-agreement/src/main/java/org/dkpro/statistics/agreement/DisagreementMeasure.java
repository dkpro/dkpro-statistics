/*
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

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link IAgreementMeasure} interface providing computational hooks
 * for calculating the observed and the expected disagreement. The values for observed and expected
 * disagreement are combined using Krippendorff's (1980) {@code 1 - (D_O / D_E)} formula (where D_O
 * denotes the observed disagreement and D_E denotes the expected disagreement. See also
 * {@link AgreementMeasure} for the analogous definition of a measure based on the observed and
 * expected agreement.<br>
 * <br>
 * References:
 * <ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology. Beverly Hills, CA:
 * Sage Publications, 1980.</li>
 * </ul>
 * 
 * @see IAgreementMeasure
 * @see AgreementMeasure
 * @author Christian M. Meyer
 */
public abstract class DisagreementMeasure
    implements IAgreementMeasure
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Calculates the inter-rater agreement for the annotation study that was passed to the class
     * constructor and the currently assigned distance function.
     * 
     * @throws NullPointerException
     *             if the study is null.
     * 
     * @throws ArithmeticException
     *             if the study does not contain any item or the number of raters is smaller than 2.
     */
    @Override
    public double calculateAgreement()
    {
        double D_O = calculateObservedDisagreement();
        double D_E = calculateExpectedDisagreement();

        LOG.trace("Disagreement -- observed: {} -- expected: {}", D_O, D_E);

        if (D_O == D_E) {
            // When observed and expected disagreement are equal, the chance-corrected agreement is
            // zero -- except when both are zero, which is the case if all raters agreed on every
            // unit. Such full agreement should yield 1.0 rather than 0.0, unless the study carries
            // no information from which agreement could be inferred and there is nothing to agree
            // on. See issue #35.
            if (D_O == 0.0) {
                return studyCarriesInformation() ? 1.0 : 0.0;
            }

            return 0.0;
        }

        return 1.0 - (D_O / D_E);
    }

    /**
     * Returns the annotation study underlying this measure, or {@code null} if the measure does not
     * operate on an {@link IAnnotationStudy} (e.g., purely text-based measures). This is used to
     * tell an empty study apart from a study in which the raters fully agree.
     */
    protected IAnnotationStudy getStudy()
    {
        return null;
    }

    /**
     * Tells whether the underlying study carries any information from which the measure can infer
     * agreement. {@link #calculateAgreement()} uses this to tell full agreement (yielding 1.0)
     * apart from a study the measure cannot draw any conclusion from (yielding 0.0), such as an
     * empty study. Subclasses should refine this check if there are further study configurations
     * that are invisible to the measure, e.g. length-weighted unitizing measures cannot observe
     * zero-length units.
     */
    protected boolean studyCarriesInformation()
    {
        IAnnotationStudy study = getStudy();
        return study != null && !study.isEmpty();
    }

    protected abstract double calculateObservedDisagreement();

    protected double calculateExpectedDisagreement()
    {
        return 0.0;
    }
}
