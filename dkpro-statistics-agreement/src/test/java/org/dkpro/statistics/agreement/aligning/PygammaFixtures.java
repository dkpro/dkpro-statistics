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
package org.dkpro.statistics.agreement.aligning;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.CombinedCategoricalDissimilarity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Shared loader for the pygamma cross-validation fixtures under
 * {@code src/test/resources/pygamma}. Reused by the tier-1/tier-2/tier-3 tests so the JSON schema is
 * decoded in exactly one place.
 */
public final class PygammaFixtures
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String FIXTURE_DIR = "src/test/resources/pygamma";

    private PygammaFixtures()
    {
        // utility class
    }

    /**
     * @return the sorted file names of all {@code fixture_*.json} files, for use as a JUnit
     *         {@code @MethodSource}.
     */
    public static Stream<String> fixtures() throws IOException
    {
        var names = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FIXTURE_DIR),
                "fixture_*.json")) {
            for (Path p : stream) {
                names.add(p.getFileName().toString());
            }
        }
        names.sort(String::compareTo);
        return names.stream();
    }

    public static JsonNode load(String aFixture)
    {
        try (InputStream is = Files.newInputStream(Paths.get(FIXTURE_DIR, aFixture))) {
            return MAPPER.readTree(is);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Builds an {@link AnnotationSet} from the top-level {@code continuum} array of a fixture.
     */
    public static AnnotationSet buildContinuum(JsonNode aRoot)
    {
        return buildAnnotationSet(aRoot.get("continuum"));
    }

    /**
     * Builds an {@link AnnotationSet} from an array of unit entries (each with
     * {@code annotator}/{@code start}/{@code end}/{@code category} keys and integral offsets). Works
     * both for the top-level {@code continuum} and for a sampled continuum's {@code scaled.units}.
     */
    public static AnnotationSet buildAnnotationSet(JsonNode aUnitsArray)
    {
        var raters = new HashMap<String, Rater>();
        var units = new ArrayList<AlignableAnnotationUnit>();
        for (JsonNode entry : aUnitsArray) {
            String annotator = entry.get("annotator").asText();
            var rater = raters.computeIfAbsent(annotator, name -> new Rater(name, raters.size()));
            long start = entry.get("start").asLong();
            long end = entry.get("end").asLong();
            String category = entry.get("category").asText();
            units.add(new AlignableAnnotationUnit(rater, null, start, end,
                    Map.of("category", category)));
        }
        return new AnnotationSet(units);
    }

    /**
     * Builds the combined categorical dissimilarity from a fixture's {@code params} block.
     */
    public static CombinedCategoricalDissimilarity buildDissimilarity(JsonNode aRoot)
    {
        JsonNode params = aRoot.get("params");
        return CombinedCategoricalDissimilarity.builder() //
                .withAlpha(params.get("alpha").asDouble()) //
                .withBeta(params.get("beta").asDouble()) //
                .withDeltaEmpty(params.get("deltaEmpty").asDouble()) //
                .build();
    }
}
