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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.HashMap;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.junit.jupiter.api.Test;

public class UnitTest
{
    private static final Rater ANNOTATOR_1 = new Rater("1", 0);
    private static final Rater ANNOTATOR_2 = new Rater("2", 1);

    @Test
    public void createUnitBeginBiggerEnd()
    {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            new AlignableAnnotationUnit(null, 1, 0, null);
        });
    }

    @Test
    public void testEqualsObject()
    {
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_1, 0, 3, null))
                .isEqualTo(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null));
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_1, "Test", 0, 3, null))
                .isEqualTo(new AlignableAnnotationUnit(ANNOTATOR_1, "Test", 0, 3, null));
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_1, "Test", 0, 3, null))
                .isNotEqualTo(new AlignableAnnotationUnit(null, "Test", 0, 3, null));
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null))
                .isNotEqualTo(new AlignableAnnotationUnit(null, "Test", 0, 3, null));
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null))
                .isNotEqualTo(new AlignableAnnotationUnit(null, null, 1, 3, null));
    }

    @Test
    public void testOverlaps()
    {
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null)
                .overlaps(new AlignableAnnotationUnit(null, null, 2, 4, null))).isTrue();
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null)
                .overlaps(new AlignableAnnotationUnit(null, null, 0, 3, null))).isTrue();
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null)
                .overlaps(new AlignableAnnotationUnit(null, null, 3, 5, null))).isFalse();
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null)
                .overlaps(new AlignableAnnotationUnit(null, null, 4, 5, null))).isFalse();
        assertThat(new AlignableAnnotationUnit(null, null, 4, 5, null)
                .overlaps(new AlignableAnnotationUnit(null, null, 0, 3, null))).isFalse();
        assertThat(new AlignableAnnotationUnit(null, null, 4, 5, null)
                .overlaps(new AlignableAnnotationUnit(null, null, 0, 4, null))).isFalse();
    }

    @Test
    public void testCompareToUnit()
    {
        assertThat(new AlignableAnnotationUnit(null, null, 0, 3, null))
                .isLessThan(new AlignableAnnotationUnit(null, null, 2, 4, null));
        assertThat(new AlignableAnnotationUnit(null, null, 3, 4, null))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 4, null));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 3, null))
                .isLessThan(new AlignableAnnotationUnit(null, null, 2, 4, null));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, null))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 3, null));

        assertThat(new AlignableAnnotationUnit(null, "a", 2, 4, null))
                .isLessThan(new AlignableAnnotationUnit(null, "b", 2, 4, null));
        assertThat(new AlignableAnnotationUnit(null, "b", 2, 4, null))
                .isGreaterThan(new AlignableAnnotationUnit(null, "a", 2, 4, null));

        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, null))
                .isLessThan(new AlignableAnnotationUnit(null, "b", 2, 4, null));
        assertThat(new AlignableAnnotationUnit(null, "a", 2, 4, null))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 4, null));

        assertThat(new AlignableAnnotationUnit(ANNOTATOR_1, null, 2, 4, null))
                .isLessThan(new AlignableAnnotationUnit(ANNOTATOR_2, null, 2, 4, null));
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_2, null, 2, 4, null))
                .isGreaterThan(new AlignableAnnotationUnit(ANNOTATOR_1, null, 2, 4, null));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, null))
                .isLessThan(new AlignableAnnotationUnit(ANNOTATOR_2, null, 2, 4, null));
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_2, null, 2, 4, null))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 4, null));

        HashMap<String, String> a1 = new HashMap<String, String>();
        HashMap<String, String> a2 = new HashMap<String, String>();
        HashMap<String, String> b = new HashMap<String, String>();

        a1.put("a", "a");
        a2.put("a", "b");
        b.put("b", "a");

        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, a1))
                .isLessThan(new AlignableAnnotationUnit(null, null, 2, 4, b));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, b))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 4, a1));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, a1))
                .isLessThan(new AlignableAnnotationUnit(null, null, 2, 4, a2));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, a2))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 4, a1));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, null))
                .isLessThan(new AlignableAnnotationUnit(null, null, 2, 4, a1));
        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, a1))
                .isGreaterThan(new AlignableAnnotationUnit(null, null, 2, 4, null));

        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, null)
                .compareTo(new AlignableAnnotationUnit(null, null, 2, 4, null))).isZero();

    }

    @Test
    public void testHash()
    {
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_1, 0, 3, null).hashCode())
                .isEqualTo(new AlignableAnnotationUnit(ANNOTATOR_1, null, 0, 3, null).hashCode());
        assertThat(new AlignableAnnotationUnit(ANNOTATOR_1, "Test", 0, 3, null).hashCode())
                .isEqualTo(new AlignableAnnotationUnit(ANNOTATOR_1, "Test", 0, 3, null).hashCode());

        var a1 = new HashMap<String, String>();
        var a2 = new HashMap<String, String>();

        a1.put("a", "b");
        a2.put("a", "b");

        assertThat(new AlignableAnnotationUnit(null, null, 2, 4, a1).hashCode())
                .isEqualTo(new AlignableAnnotationUnit(null, null, 2, 4, a2).hashCode());
    }
}
