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
import static org.assertj.core.api.Assertions.offset;

import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;
import org.junit.jupiter.api.Test;

public class OrdinalDistanceFunctionTest
{
    private final OrdinalDistanceFunction sut = new OrdinalDistanceFunction();

    /** Study where each of the categories 1, 2, 3 is annotated exactly twice. */
    private static ICodingAnnotationStudy study()
    {
        CodingAnnotationStudy study = new CodingAnnotationStudy(2);
        study.addItem(1, 1);
        study.addItem(2, 2);
        study.addItem(3, 3);
        return study;
    }

    @Test
    public void equalIntegersAreZero()
    {
        assertThat(sut.measureDistance(study(), 2, 2)).isEqualTo(0.0);
    }

    @Test
    public void distanceGrowsWithTheEnclosedCategoryMass()
    {
        // (nk[1]/2 + nk[2]/2)^2 = (1 + 1)^2 = 4
        assertThat(sut.measureDistance(study(), 1, 2)).isCloseTo(4.0, offset(0.0001));
        // (nk[1]/2 + nk[3]/2 + nk[2])^2 = (1 + 1 + 2)^2 = 16
        assertThat(sut.measureDistance(study(), 1, 3)).isCloseTo(16.0, offset(0.0001));
    }

    @Test
    public void nonIntegerCategoriesFallBackToNominal()
    {
        assertThat(sut.measureDistance(null, "A", "A")).isEqualTo(0.0);
        assertThat(sut.measureDistance(null, "A", "B")).isEqualTo(1.0);
    }

    @Test
    public void nullEqualsNullIsZero()
    {
        assertThat(sut.measureDistance(null, null, null)).isEqualTo(0.0);
    }

    @Test
    public void nullVersusValueIsMaximal()
    {
        assertThat(sut.measureDistance(null, null, "A")).isEqualTo(1.0);
        assertThat(sut.measureDistance(null, "A", null)).isEqualTo(1.0);
    }
}
