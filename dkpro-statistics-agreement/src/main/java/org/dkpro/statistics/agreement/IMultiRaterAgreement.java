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
 * Marker interface for {@link IAgreementMeasure}s that support annotation studies with more than
 * two raters. Measures that do <i>not</i> implement this interface are only applicable to studies
 * with exactly two raters and throw an {@link IllegalArgumentException} when applied to a study
 * with a different number of raters.
 * <p>
 * Applications can query this capability directly via
 * {@code measure instanceof IMultiRaterAgreement} or, together with the missing-value requirement,
 * via {@link IAgreementMeasure#canHandle}.
 *
 * @see IAgreementMeasure#canHandle(IAnnotationStudy)
 * @see IMissingValueSupport
 */
public interface IMultiRaterAgreement
    extends IAgreementMeasure
{
    // Marker interface -- no additional methods.
}
