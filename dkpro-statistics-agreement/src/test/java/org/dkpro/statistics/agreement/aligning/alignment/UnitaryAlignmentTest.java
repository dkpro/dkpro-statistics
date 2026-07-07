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

import static java.util.Arrays.asList;
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

public class UnitaryAlignmentTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);
    private static final Rater ANNOTATOR_3 = new Rater("3", 1);

    @Test
    public void testCreationWithAnnotationsFromOneCreator()
    {
        var units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 4, null));

        var as = new AnnotationSet(units);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new UnitaryAlignment(as.getUnits(), new HashSet<Rater>(as.getRaters()));
        });
    }

    @Test
    public void testCreationWithAnnotationsFromUnknownCreator()
    {
        var units = asList( //
                new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null), //
                new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 4, null));

        var as = new AnnotationSet(units);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new UnitaryAlignment(as.getUnits(),
                    new HashSet<Rater>(asList(as.getRaters().iterator().next())));
        });
    }

    @Test
    public void testArity()
    {
        var units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));

        var as = new AnnotationSet(units);

        var a = new UnitaryAlignment(as.getUnits(), as.getRaters());
        assertThat(a.arity()).isEqualTo(2);
    }

    @Test
    public void testGetDisorder()
    {
        var units = new ArrayList<AlignableAnnotationUnit>(3);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));

        var as = new AnnotationSet(units);

        var a = new UnitaryAlignment(as.getUnits(), as.getRaters());
        assertThat(a.getDisorder(new NominalFeatureDissimilarity())).isEqualTo(0.0);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 3, 4, null));

        as = new AnnotationSet(units);

        a = new UnitaryAlignment(as.getUnits(), as.getRaters());
        assertThat(a.getDisorder(new NominalFeatureDissimilarity())).isCloseTo(2 / 3.0,
                offset(0.0001));

        units = new ArrayList<AlignableAnnotationUnit>(1);
        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 3, 4, null));

        a = new UnitaryAlignment(units, as.getRaters());
        assertThat(a.getDisorder(new NominalFeatureDissimilarity())).isCloseTo(2 / 3.0,
                offset(0.0001));
    }

    @Test
    public void testEqualsObject()
    {
        var units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));

        var annotators = new HashSet<Rater>();

        annotators.add(ANNOTATOR_1);
        annotators.add(ANNOTATOR_2);

        UnitaryAlignment a = new UnitaryAlignment(units, annotators);

        annotators.add(ANNOTATOR_3);

        UnitaryAlignment b = new UnitaryAlignment(units, annotators);

        // different arity
        assertThat(a).isNotEqualTo(b);

        units.clear();
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 0, 3, null));

        annotators = new HashSet<Rater>();

        annotators.add(ANNOTATOR_1);
        annotators.add(ANNOTATOR_3);

        UnitaryAlignment c = new UnitaryAlignment(units, annotators);

        // different annotators
        assertThat(a).isNotEqualTo(c);

        units.clear();
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 2, null));

        annotators = new HashSet<Rater>();

        annotators.add(ANNOTATOR_1);
        annotators.add(ANNOTATOR_2);

        UnitaryAlignment d = new UnitaryAlignment(units, annotators);

        // different units
        assertThat(a).isNotEqualTo(d);

        units.clear();
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));

        UnitaryAlignment e = new UnitaryAlignment(units, annotators);

        // equal alignments
        assertThat(a).isEqualTo(e);
    }

    @Test
    public void testCompareToUnitaryAlignment()
    {

        List<AlignableAnnotationUnit> units = new ArrayList<AlignableAnnotationUnit>(2);

        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));

        Set<Rater> annotators = new HashSet<Rater>();

        annotators.add(ANNOTATOR_1);
        annotators.add(ANNOTATOR_2);

        UnitaryAlignment a = new UnitaryAlignment(units, annotators);

        annotators.add(ANNOTATOR_3);

        UnitaryAlignment b = new UnitaryAlignment(units, annotators);

        // different arity
        assertThat(a.compareTo(b)).isEqualTo(-1);
        assertThat(b.compareTo(a)).isEqualTo(1);

        units.clear();
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_3, null, 0, 3, null));

        annotators = new HashSet<Rater>();

        annotators.add(ANNOTATOR_1);
        annotators.add(ANNOTATOR_3);

        UnitaryAlignment c = new UnitaryAlignment(units, annotators);

        // different annotators
        assertThat(a.compareTo(c)).isEqualTo(-1);
        assertThat(c.compareTo(a)).isEqualTo(1);

        units.clear();
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 2, null));

        annotators = new HashSet<Rater>();

        annotators.add(ANNOTATOR_1);
        annotators.add(ANNOTATOR_2);

        UnitaryAlignment d = new UnitaryAlignment(units, annotators);

        // different units
        assertThat(d.compareTo(a)).isEqualTo(-1);
        assertThat(a.compareTo(d)).isEqualTo(1);

        units.clear();
        units.add(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        units.add(new AlignableAnnotationUnit(ANNOTATOR_2, null, 0, 3, null));

        UnitaryAlignment e = new UnitaryAlignment(units, annotators);

        // equal alignments
        assertThat(a.compareTo(e)).isEqualTo(0);
    }
}
