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
import static org.dkpro.statistics.agreement.aligning.data.AnnotatedTextMerge.mergeAnnotatedTextsWithSegmentation;

import org.dkpro.statistics.agreement.aligning.alignment.Alignment;
import org.dkpro.statistics.agreement.aligning.dissimilarity.NominalFeatureTextDissimilarity;
import org.junit.jupiter.api.Test;

class AnnotatedTextMergeTest
{
    private static final Rater ANNOTATOR_A = new Rater("A", 0);
    private static final Rater ANNOTATOR_B = new Rater("B", 1);

    @Test
    void testMergeAnnotatedTextsWithSegmentation()
    {
        // Case 1: identical texts
        var annots1 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 0, 4, "kauf"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 4, 8, "mann"));
        var text1 = new AnnotatedText("kaufmann", annots1);

        var annots2 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 0, 4, "kauf"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 4, 8, "mann"));
        var text2 = new AnnotatedText("kaufmann", annots2);

        var harm = mergeAnnotatedTextsWithSegmentation(text1, text2);

        assertThat(harm).hasSize(1);
        Alignment al = harm.toArray(new Alignment[0])[0];
        assertThat(al.getDisorder(new NominalFeatureTextDissimilarity())).isEqualTo(0);

        // Case 2: Alignable but different TextUnits
        annots1 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 0, 5, "kauff"),
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 5, 9, "mann"));
        text1 = new AnnotatedText("kauffmann", annots1);

        annots2 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 0, 4, "kauf"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 4, 9, "mmann"));
        text2 = new AnnotatedText("kaufmmann", annots2);

        harm = AnnotatedTextMerge.mergeAnnotatedTextsWithSegmentation(text1, text2);

        assertThat(harm).hasSize(1);
        al = harm.toArray(new Alignment[0])[0];
        assertThat(al.getDisorder(new NominalFeatureTextDissimilarity())).isEqualTo(1);

        // Case 3: segmentation completely different
        annots1 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 0, 1, "k"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 1, 8, "aufmann"));
        text1 = new AnnotatedText("kaufmann", annots1);

        annots2 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 0, 4, "kauf"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 4, 8, "mann"));
        text2 = new AnnotatedText("kaufmann", annots2);

        harm = AnnotatedTextMerge.mergeAnnotatedTextsWithSegmentation(text1, text2);

        assertThat(harm).hasSize(1);
        al = harm.toArray(new Alignment[0])[0];
        assertThat(al.getDisorder(new NominalFeatureTextDissimilarity())).isEqualTo(2);

        // Case 4: two possible alignments
        annots1 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 0, 5, "a"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 6, 11, "b"));
        text1 = new AnnotatedText("sonst sonst", annots1);

        annots2 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 0, 5, "a"));
        text2 = new AnnotatedText("sonst", annots2);

        harm = AnnotatedTextMerge.mergeAnnotatedTextsWithSegmentation(text1, text2);

        assertThat(harm).hasSize(2);

        // for (int i=0; i < harm.size(); i++) {
        // System.out.println(harm.toArray(new Alignment[0])[i]);
        // System.out.println(harm.toArray(new Alignment[0])[i].getDisorder(new
        // SimpleTextUnitDissimilarity()));
        // }

        // Case 5: three possible alignments
        annots1 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 0, 2, "a"),
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 3, 5, "b"),
                new AlignableAnnotationTextUnit(ANNOTATOR_A, 6, 8, "c"));
        text1 = new AnnotatedText("so so so", annots1);

        annots2 = asList( //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 0, 2, "b"), //
                new AlignableAnnotationTextUnit(ANNOTATOR_B, 3, 5, "c"));
        text2 = new AnnotatedText("so so", annots2);

        harm = AnnotatedTextMerge.mergeAnnotatedTextsWithSegmentation(text1, text2);

        assertThat(harm).hasSize(3);

        // for (int i=0; i < harm.size(); i++) {
        // System.out.println(harm.toArray(new Alignment[0])[i]);
        // System.out.println(harm.toArray(new Alignment[0])[i].getDisorder(new
        // SimpleTextUnitDissimilarity()));
        // }
    }

}
