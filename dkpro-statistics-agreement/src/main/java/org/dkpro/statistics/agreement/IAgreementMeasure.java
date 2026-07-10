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

import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.slf4j.LoggerFactory;

/**
 * Generic interface that is to be implemented by all inter-rater agreement measures. The basic idea
 * is to calculate a numerical score for a given {@link IAnnotationStudy}.
 *
 * @author Christian M. Meyer
 */
public interface IAgreementMeasure
{

    /**
     * Returns the inter-rater agreement score. Unless otherwise indicated, the result is between -1
     * and +1, where +1 indicates perfect agreement, 0 indicates no agreement or that the agreement
     * can be fully explained by chance, and -1 indicates perfect disagreement.
     */
    public double calculateAgreement();

    /**
     * Tests whether this measure is able to process the given annotation study, allowing an
     * application to decide up front whether the data needs to be pre-processed (e.g., by filtering
     * incomplete items) rather than discovering an unsupported configuration only at computation
     * time. A study is supported if this measure can cope with its number of raters (see
     * {@link IMultiRaterAgreement}) and, for coding studies, with any missing values it may contain
     * (see {@link IMissingValueSupport}).
     *
     * @param study
     *            the annotation study to test; must not be {@code null}.
     * @return {@code true} if the measure can process the study, {@code false} otherwise.
     */
    default boolean canHandle(final IAnnotationStudy study)
    {
        int raterCount = study.getRaterCount();
        if (this instanceof IMultiRaterAgreement) {
            if (raterCount < 2) {
                return false;
            }
        }
        else if (raterCount != 2) {
            return false;
        }

        return !hasUnsupportedMissingValues(study);
    }

    /**
     * Ensures that the given study has a number of raters that this measure can process. Unless the
     * measure is marked as an {@link IMultiRaterAgreement}, it is only applicable to studies with
     * exactly two raters.
     *
     * @param study
     *            the annotation study to check.
     * @throws IllegalArgumentException
     *             if the measure requires exactly two raters but the study has a different number.
     */
    default void ensureSupportedRaterCount(final IAnnotationStudy study)
    {
        if (!(this instanceof IMultiRaterAgreement) && study.getRaterCount() != 2) {
            throw new IllegalArgumentException("This agreement measure is only "
                    + "applicable for annotation studies with two raters!");
        }
    }

    /**
     * Emits a warning if the given study contains missing values but this measure is not marked as
     * an {@link IMissingValueSupport} (i.e., it cannot deal with missing values).
     *
     * @param study
     *            the annotation study to check.
     */
    default void warnIfMissingValues(final IAnnotationStudy study)
    {
        if (hasUnsupportedMissingValues(study)) {
            LoggerFactory
                    .getLogger(getClass()).warn(
                            "{} does not support dealing with missing values. Consider using, for "
                                    + "example, Krippendorff's alpha instead.",
                            getClass().getName());
        }
    }

    /**
     * @return {@code true} if the study contains missing values that this measure cannot handle,
     *         that is, it is a coding study with missing values and this measure is not marked as
     *         an {@link IMissingValueSupport}.
     */
    private boolean hasUnsupportedMissingValues(final IAnnotationStudy study)
    {
        return !(this instanceof IMissingValueSupport) && study instanceof ICodingAnnotationStudy
                && ((ICodingAnnotationStudy) study).hasMissingValues();
    }

}
