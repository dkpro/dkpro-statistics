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

/**
 * Marker interface for {@link IAgreementMeasure}s that support annotation studies containing
 * missing values, that is, items which have not been annotated by all raters (represented by a
 * {@code null} category). Measures that do <i>not</i> implement this interface are assumed to
 * require complete data; when applied to a study with missing values they emit a warning and may
 * return a skewed result. Krippendorff's alpha is the canonical example of a measure that genuinely
 * supports missing values.
 * <p>
 * Applications can query this capability directly via
 * {@code measure instanceof IMissingValueSupport} or, together with the rater-count requirement,
 * via {@link IAgreementMeasure#canHandle}.
 *
 * @see IAgreementMeasure#canHandle(IAnnotationStudy)
 * @see IMultiRaterAgreement
 */
public interface IMissingValueSupport
    extends IAgreementMeasure
{
    // Marker interface -- no additional methods.
}
