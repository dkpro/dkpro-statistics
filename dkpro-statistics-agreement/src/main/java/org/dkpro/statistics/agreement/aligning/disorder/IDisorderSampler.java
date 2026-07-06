/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Original source: https://github.com/fab-bar/TextGammaTool.git
 */
package org.dkpro.statistics.agreement.aligning.disorder;

/**
 * Samples a single disorder value for one random draw of the null model.
 * <p>
 * Deviations from the upstream TextGammaTool implementation:
 * <ul>
 * <li>Upstream {@code DisorderSampler} was an abstract class; this is a functional interface.</li>
 * </ul>
 */
@FunctionalInterface
public interface IDisorderSampler
{
    Double sampleDisorder();
}
