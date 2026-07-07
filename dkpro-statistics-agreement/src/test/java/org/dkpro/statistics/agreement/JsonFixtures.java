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
package org.dkpro.statistics.agreement;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Shared loader for JSON cross-validation fixtures, so the {@code fixture_*.json} directory glob
 * and the JSON decoding live in exactly one place across the test suites.
 */
public final class JsonFixtures
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonFixtures()
    {
        // utility class
    }

    /**
     * @param aFixtureDir
     *            the directory to scan, relative to the module root.
     * @return the sorted file names of all {@code fixture_*.json} files in {@code aFixtureDir}, for
     *         use as a JUnit {@code @MethodSource}.
     */
    public static Stream<String> fixtures(String aFixtureDir) throws IOException
    {
        var names = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(aFixtureDir),
                "fixture_*.json")) {
            for (Path p : stream) {
                names.add(p.getFileName().toString());
            }
        }
        names.sort(String::compareTo);
        return names.stream();
    }

    /**
     * Parses the fixture {@code aFixture} located in {@code aFixtureDir} into a {@link JsonNode}.
     */
    public static JsonNode load(String aFixtureDir, String aFixture)
    {
        try (InputStream is = Files.newInputStream(Paths.get(aFixtureDir, aFixture))) {
            return MAPPER.readTree(is);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
