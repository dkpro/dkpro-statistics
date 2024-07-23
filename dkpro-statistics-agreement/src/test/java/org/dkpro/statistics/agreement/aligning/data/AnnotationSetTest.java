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
package org.dkpro.statistics.agreement.aligning.data;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.junit.jupiter.api.Test;

class AnnotationSetTest
{
    private static final Rater ANNOTATOR_A = new Rater("A", 0);
    private static final Rater ANNOTATOR_B = new Rater("B", 1);

    @Test
    public void testGetNumberOfAnnotators()
    {
        var set = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null), //
                new AlignableAnnotationUnit(ANNOTATOR_A, 1, 2, null)));

        assertThat(set.getRaterCount()).isEqualTo(2);
    }

    @Test
    public void testGetNumberOfAnnotations()
    {
        var set = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null)));

        assertThat(set.getUnitCount()).isEqualTo(2);
    }

    @Test
    public void testGetAverageNumberOfAnnotations()
    {
        var set = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null), //
                new AlignableAnnotationUnit(ANNOTATOR_A, 1, 2, null)));

        assertThat(set.getAverageNumberOfAnnotations()).isEqualTo(1.5);
    }

    @Test
    public void testGetAnnotators()
    {
        var set = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null), //
                new AlignableAnnotationUnit(ANNOTATOR_A, 1, 2, null)));

        assertThat(set.getRaters()) //
                .containsExactlyInAnyOrder(ANNOTATOR_A, ANNOTATOR_B);
    }

    @Test
    public void testGetUnits()
    {
        var annots = new AlignableAnnotationUnit[] { //
                new AlignableAnnotationUnit(null, 2, 3, null), //
                new AlignableAnnotationUnit(null, 1, 2, null) };

        var set = new AnnotationSet(asList(annots));

        assertThat(Arrays.equals( //
                set.getUnits().toArray(AlignableAnnotationUnit[]::new), annots)).isFalse();
        Arrays.sort(annots);
        assertThat(set.getUnits()).containsExactly(annots);
    }

    @Test
    public void testGetAnnotationsType()
    {
        var set = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(null, "A", 2, 3, null), //
                new AlignableAnnotationUnit(null, "", 1, 2, null), //
                new AlignableAnnotationUnit(null, null, 2, 3, null)));

        assertThat(set.getUnitsWithType("A")).hasSize(1);
        assertThat(set.getUnitsWithType("")).hasSize(2);
        assertThat(set.getUnitsWithType("A")).isNotEqualTo(set.getUnitsWithType(""));
    }

    @Test
    public void testGetAnnotationsCreator()
    {
        var a = ANNOTATOR_A;
        var b = new Rater("", -1);

        var set = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(a, null, 2, 3, null), //
                new AlignableAnnotationUnit(b, null, 1, 2, null), //
                new AlignableAnnotationUnit(null, null, 2, 3, null)));

        assertThat(set.getUnitsWithRater(a)).hasSize(1);
        assertThat(set.getUnitsWithRater(b)).hasSize(2);
        assertThat(set.getUnitsWithRater(a)).isNotEqualTo(set.getUnitsWithRater(b));
    }

    @Test
    public void testEquals()
    {
        var annots = asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null), //
                new AlignableAnnotationUnit(ANNOTATOR_A, 1, 2, null));

        var set1 = new AnnotationSet(annots);
        var set2 = new AnnotationSet(annots);

        var set3 = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 2, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null)));

        var set4 = new AnnotationSet(asList( //
                new AlignableAnnotationUnit(ANNOTATOR_A, 1, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_B, 1, 2, null), //
                new AlignableAnnotationUnit(ANNOTATOR_A, 1, 2, null)));

        assertThat(set1).isEqualTo(set1);
        assertThat(set1).isEqualTo(set2);
        assertThat(set2).isEqualTo(set1);
        assertThat(set1).isNotEqualTo(null);
        assertThat(set1).isNotEqualTo(set3);
        assertThat(set3).isNotEqualTo(set1);
        assertThat(set1).isNotEqualTo(set4);
        assertThat(set4).isNotEqualTo(set1);
    }
}
