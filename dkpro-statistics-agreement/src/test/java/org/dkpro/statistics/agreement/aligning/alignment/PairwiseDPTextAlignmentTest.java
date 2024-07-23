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

import java.util.List;

import org.junit.jupiter.api.Test;

public class PairwiseDPTextAlignmentTest
{
    @Test
    public void testGetAlignments()
    {
        char[] source = new char[5];
        source[0] = 't';
        source[1] = 'h';
        source[2] = 'e';
        source[3] = 'i';
        source[4] = 'r';
        char[] target = new char[5];
        target[0] = 't';
        target[1] = 'h';
        target[2] = 'e';
        target[3] = 'r';
        target[4] = 'e';
        ITextAlignment ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        List<String[]> alignments = ta.getAlignments();
        assertThat(alignments).hasSize(1);

        source = new char[10];
        source[0] = '{';
        source[1] = 'a';
        source[2] = '}';
        source[3] = '{';
        source[4] = 'b';
        source[5] = 'b';
        source[6] = 'c';
        source[7] = 'c';
        source[8] = 'c';
        source[9] = '}';
        target = new char[10];
        target[0] = '{';
        target[1] = 'a';
        target[2] = 'b';
        target[3] = 'b';
        target[4] = '}';
        target[5] = '{';
        target[6] = 'c';
        target[7] = 'c';
        target[8] = 'c';
        target[9] = '}';

        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');
        alignments = ta.getAlignments();
        // alignment method excludes alignments where units are not aligned
        // if an optimal alignment exists where units are aligned.
        // this is returned:
        // {a--}{bbccc}
        // {abb}{--ccc}
        // this is excluded:
        // {a}{bb--ccc}
        // {a--bb}{ccc}

        assertThat(alignments).hasSize(1);

        source = new char[9];
        source[0] = '{';
        source[1] = 'a';
        source[2] = '}';
        source[3] = '{';
        source[4] = 'b';
        source[5] = 'c';
        source[6] = 'c';
        source[7] = 'c';
        source[8] = '}';
        target = new char[9];
        target[0] = '{';
        target[1] = 'a';
        target[2] = 'b';
        target[3] = '}';
        target[4] = '{';
        target[5] = 'c';
        target[6] = 'c';
        target[7] = 'c';
        target[8] = '}';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        alignments = ta.getAlignments();
        assertThat(alignments).hasSize(1);

        // Test example with many optimal alignments
        // alignment method only returns alignments with aligned tokens
        // (i.e. 4 in the example below)
        source = new char[] { '{', 't', '}', '{', 't', '}', '{', 't', '}', '{', 't', '}' };
        target = new char[] { '{', 'a', 'a', 'a', 'a', '}' };

        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');
        alignments = ta.getAlignments();
        assertThat(alignments).hasSize(4);

        ta = new PairwiseDPTextAlignment(target, source, '-', '{', '}');
        alignments = ta.getAlignments();
        assertThat(alignments).hasSize(4);
    }

    @Test
    public void testGetInsertions()
    {
        char[] source = { 'T', 'e' };
        char[] target = { 'T' };
        ITextAlignment ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getInsertions()).isEqualTo(0);

        source = new char[3];
        source[0] = 'T';
        source[1] = 'e';
        source[2] = 's';
        target = new char[4];
        target[0] = 'T';
        target[1] = 'e';
        target[2] = 's';
        target[3] = 't';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getInsertions()).isEqualTo(1);

        source = new char[5];
        source[0] = 't';
        source[1] = 'h';
        source[2] = 'e';
        source[3] = 'i';
        source[4] = 'r';
        target = new char[5];
        target[0] = 't';
        target[1] = 'h';
        target[2] = 'e';
        target[3] = 'r';
        target[4] = 'e';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getInsertions()).isEqualTo(1);
    }

    @Test
    public void testGetDeletions()
    {
        char[] source = { 'T', 'e' };
        char[] target = { 'T' };
        ITextAlignment ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getDeletions()).isEqualTo(1);

        source = new char[3];
        source[0] = 'T';
        source[1] = 'e';
        source[2] = 's';
        target = new char[4];
        target[0] = 'T';
        target[1] = 'e';
        target[2] = 's';
        target[3] = 't';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getDeletions()).isEqualTo(0);

        source = new char[5];
        source[0] = 't';
        source[1] = 'h';
        source[2] = 'e';
        source[3] = 'i';
        source[4] = 'r';
        target = new char[5];
        target[0] = 't';
        target[1] = 'h';
        target[2] = 'e';
        target[3] = 'r';
        target[4] = 'e';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getDeletions()).isEqualTo(1);
    }

    @Test
    public void testGetSubstitutions()
    {

        // substitutions are not allowed - always 0

        char[] source = { 'T', 'e' };
        char[] target = { 'T' };
        ITextAlignment ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getSubstitutions()).isEqualTo(0);

        source = new char[3];
        source[0] = 'T';
        source[1] = 'e';
        source[2] = 's';
        target = new char[4];
        target[0] = 'T';
        target[1] = 'e';
        target[2] = 'r';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getSubstitutions()).isEqualTo(0);

        source = new char[5];
        source[0] = 't';
        source[1] = 'h';
        source[2] = 'e';
        source[3] = 'i';
        source[4] = 'r';
        target = new char[5];
        target[0] = 't';
        target[1] = 'h';
        target[2] = 'e';
        target[3] = 'r';
        target[4] = 'e';
        ta = new PairwiseDPTextAlignment(source, target, '-', '{', '}');

        assertThat(ta.getSubstitutions()).isEqualTo(0);
    }
}
