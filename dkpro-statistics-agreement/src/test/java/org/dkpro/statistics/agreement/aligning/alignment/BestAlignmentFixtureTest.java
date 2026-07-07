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
 */
package org.dkpro.statistics.agreement.aligning.alignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.io.IOException;
import java.util.stream.Stream;

import org.dkpro.statistics.agreement.aligning.PygammaFixtures;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import tools.jackson.databind.JsonNode;

public class BestAlignmentFixtureTest
{
    static Stream<String> fixtures() throws IOException
    {
        return PygammaFixtures.fixtures();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void tier1BestAlignmentDisorderMatches(String aFixture)
    {
        JsonNode root = PygammaFixtures.load(aFixture);
        var set = PygammaFixtures.buildContinuum(root);
        var d = PygammaFixtures.buildDissimilarity(root);

        double expected = root.get("tier1").get("bestAlignmentDisorder").asDouble();

        var result = BestAlignmentSolver.solve(set, d);

        // pygamma computes in float32; use a relative tolerance of 1e-5 with a small absolute floor
        // so the exact-zero fixture is handled gracefully.
        double tolerance = Math.max(1e-5 * Math.abs(expected), 1e-9);
        assertThat(result.disorder()).isCloseTo(expected, offset(tolerance));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("fixtures")
    void internalConsistencyAlignmentDisorderMatchesIlpSum(String aFixture)
    {
        JsonNode root = PygammaFixtures.load(aFixture);
        var set = PygammaFixtures.buildContinuum(root);
        var d = PygammaFixtures.buildDissimilarity(root);

        var result = BestAlignmentSolver.solve(set, d);

        assertThat(result.alignment().getDisorder(d)).isCloseTo(result.disorder(), offset(1e-9));
    }
}
