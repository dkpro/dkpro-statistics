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

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.junit.jupiter.api.Test;

class TextUnitTest
{
    @Test
    void testEqualsObject()
    {
        var tok1 = new AlignableAnnotationTextUnit(null, 0, 1, "a");
        var tok2 = new AlignableAnnotationTextUnit(null, 0, 1, "a");
        var tok3 = new AlignableAnnotationTextUnit(null, 0, 1, null);

        assertThat(tok1).isEqualTo(tok2);
        assertThat(tok2).isNotEqualTo(tok3);

        var tok4 = new AlignableAnnotationUnit(null, "textunit", 0, 1, null);

        assertThat(tok1).isNotEqualTo(tok4);
        assertThat(tok4).isNotEqualTo(tok1);
    }
}
