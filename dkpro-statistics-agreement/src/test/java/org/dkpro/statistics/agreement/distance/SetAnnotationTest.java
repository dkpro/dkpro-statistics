/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.statistics.agreement.distance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SetAnnotationTest
{
    @Test
    public void setsWithEqualElementsAreEqualRegardlessOfInsertionOrder()
    {
        SetAnnotation a = new SetAnnotation("A", "B");
        SetAnnotation b = new SetAnnotation("B", "A");

        assertThat((Object) a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.compareTo(b)).isZero();
    }

    @Test
    public void emptySetsAreEqual()
    {
        assertThat((Object) new SetAnnotation()).isEqualTo(new SetAnnotation());
        assertThat(new SetAnnotation().compareTo(new SetAnnotation())).isZero();
    }

    @Test
    public void setsWithDifferentElementsAreNotEqual()
    {
        assertThat((Object) new SetAnnotation("A")).isNotEqualTo(new SetAnnotation("B"));
        assertThat((Object) new SetAnnotation("A", "B")).isNotEqualTo(new SetAnnotation("A"));
    }

    /**
     * Regression test for issue #20: elements whose {@code toString()} coincide must not be treated
     * as equal when the elements themselves are different (e.g. the integer 1 vs. the string "1").
     */
    @Test
    public void elementsWithEqualStringRepresentationAreNotConflated()
    {
        SetAnnotation numeric = new SetAnnotation(Integer.valueOf(1));
        SetAnnotation textual = new SetAnnotation("1");

        assertThat((Object) numeric).isNotEqualTo(textual);
        assertThat(numeric.hashCode()).isNotEqualTo(textual.hashCode());
    }

    /**
     * Regression test for issue #20: two sets that are equal by element identity must not be
     * considered different just because their elements render to the same text as another set's.
     */
    @Test
    public void equalNumericSetsAreEqual()
    {
        assertThat((Object) new SetAnnotation(Integer.valueOf(1)))
                .isEqualTo(new SetAnnotation(Integer.valueOf(1)));
        assertThat(new SetAnnotation(Integer.valueOf(1)).hashCode())
                .isEqualTo(new SetAnnotation(Integer.valueOf(1)).hashCode());
    }

    /**
     * {@link SetAnnotation} instances are used as keys in a {@code TreeMap} (e.g. the marginals in
     * {@code KrippendorffAlphaAgreement}), so {@link SetAnnotation#compareTo} must be consistent
     * with {@link SetAnnotation#equals}: unequal sets must not compare as {@code 0}.
     */
    @Test
    public void compareToIsConsistentWithEquals()
    {
        SetAnnotation numeric = new SetAnnotation(Integer.valueOf(1));
        SetAnnotation textual = new SetAnnotation("1");

        assertThat(numeric.compareTo(textual)).isNotZero();
        // Antisymmetry: opposite comparisons yield opposite signs.
        assertThat(Integer.signum(numeric.compareTo(textual)))
                .isEqualTo(-Integer.signum(textual.compareTo(numeric)));
    }
}
