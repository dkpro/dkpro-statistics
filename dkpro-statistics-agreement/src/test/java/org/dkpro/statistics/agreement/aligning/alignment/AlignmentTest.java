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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.NominalFeatureDissimilarity;
import org.junit.jupiter.api.Test;

public class AlignmentTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);
    private static final Rater ANNOTATOR_3 = new Rater("3", 1);

    @Test
    public void testAlignmentWithDifferingCreators()
    {
        Set<UnitaryAlignment> uas = new HashSet<UnitaryAlignment>();

        List<AlignableAnnotationUnit> annotations = new ArrayList<AlignableAnnotationUnit>(4);

        Set<Rater> creators = new HashSet<Rater>();
        creators.add(ANNOTATOR_1);
        creators.add(ANNOTATOR_2);

        List<AlignableAnnotationUnit> units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        creators.add(ANNOTATOR_3);

        units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 1, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 0, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Alignment(uas, new AnnotationSet(annotations));
        });
    }

    @Test
    void testAlignmentWithUnitContainedTwice()
    {

        Set<UnitaryAlignment> uas = new HashSet<UnitaryAlignment>();

        List<AlignableAnnotationUnit> annotations = new ArrayList<AlignableAnnotationUnit>(4);

        Set<Rater> creators = new HashSet<Rater>();
        creators.add(ANNOTATOR_1);
        creators.add(ANNOTATOR_2);

        List<AlignableAnnotationUnit> units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 1, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Alignment(uas, new AnnotationSet(annotations));
        });
    }

    @Test
    public void testAlignmentWithUnitNotContainedInAS()
    {

        Set<UnitaryAlignment> uas = new HashSet<UnitaryAlignment>();

        List<AlignableAnnotationUnit> annotations = new ArrayList<AlignableAnnotationUnit>(2);

        Set<Rater> creators = new HashSet<Rater>();
        creators.add(ANNOTATOR_1);
        creators.add(ANNOTATOR_2);

        List<AlignableAnnotationUnit> units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 1, 3, null));

        uas.add(new UnitaryAlignment(units, creators));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Alignment(uas, new AnnotationSet(annotations));
        });
    }

    @Test
    public void testAlignmentWithUnitNotContainedInUA()
    {

        Set<UnitaryAlignment> uas = new HashSet<UnitaryAlignment>();

        List<AlignableAnnotationUnit> annotations = new ArrayList<AlignableAnnotationUnit>(3);

        Set<Rater> creators = new HashSet<Rater>();
        creators.add(ANNOTATOR_1);
        creators.add(ANNOTATOR_2);

        List<AlignableAnnotationUnit> units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);
        annotations.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 1, 3, null));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new Alignment(uas, new AnnotationSet(annotations));
        });
    }

    @Test
    public void testGetDisorder()
    {

        Set<UnitaryAlignment> uas = new HashSet<UnitaryAlignment>();

        Set<Rater> creators = new HashSet<Rater>();
        creators.add(ANNOTATOR_1);
        creators.add(ANNOTATOR_2);
        creators.add(ANNOTATOR_3);

        List<AlignableAnnotationUnit> annotations = new ArrayList<AlignableAnnotationUnit>(4);

        List<AlignableAnnotationUnit> units = new ArrayList<AlignableAnnotationUnit>(3);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 0, 3, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        units = new ArrayList<AlignableAnnotationUnit>(1);
        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 3, 4, null));

        uas.add(new UnitaryAlignment(units, creators));

        annotations.addAll(units);

        Alignment a = new Alignment(uas, new AnnotationSet(annotations));

        double averageAnnotations = 4 / 3.0;
        double disorder_1 = 0;
        double disorder_2 = 2 / 3.0;

        assertThat(a.getDisorder(new NominalFeatureDissimilarity()))
                .isCloseTo((disorder_1 + disorder_2) / averageAnnotations, offset(0.0001));
    }
}
