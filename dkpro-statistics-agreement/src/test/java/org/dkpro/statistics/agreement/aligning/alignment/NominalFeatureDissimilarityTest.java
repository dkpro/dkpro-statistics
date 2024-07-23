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
package org.dkpro.statistics.agreement.aligning.alignment;

import static org.assertj.core.api.Assertions.assertThat;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.NominalFeatureDissimilarity;
import org.junit.jupiter.api.Test;

public class NominalFeatureDissimilarityTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);

    @Test
    void testDissimilarity()
    {
        var diss = new NominalFeatureDissimilarity();

        var u = new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null);
        var v = new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null);
        assertThat(diss.dissimilarity(u, v)).isEqualTo(0.0);

        u = new AlignableAnnotationUnit(ANNOTATOR_1, "textunit", 0, 3, null);
        v = new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null);
        assertThat(diss.dissimilarity(u, v)).isEqualTo(1.0);

        u = new AlignableAnnotationUnit(ANNOTATOR_1, null, 3, 4, null);
        v = new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null);
        assertThat(diss.dissimilarity(u, v)).isEqualTo(1.0);

        v = u.cloneWithDifferentLabel("pos", "A");
        assertThat(diss.dissimilarity(u, v)).isEqualTo(1.0);

        u = v;
        assertThat(diss.dissimilarity(u, v)).isEqualTo(0.0);

        assertThat(diss.dissimilarity(u, null)).isEqualTo(1.0);
        assertThat(diss.dissimilarity(null, v)).isEqualTo(1.0);

        assertThat(diss.dissimilarity(null, null)).isEqualTo(0.0);
    }
}
