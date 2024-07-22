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

import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.CLOSE_UNIT;
import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.GAP;
import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.OPEN_UNIT;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.data.AnnotatedText;

/**
 * Align the annotations of two texts using dynamic programming.
 */
public class PairwiseDPTextAlignment
    implements ITextAlignment
{
    private final char[] textA;
    private final char[] textB;
    private final int wGap;

    private final char alignChar;
    private final char openUnit;
    private final Set<Character> metaChars;

    private int[][] alignmentMatrix;

    private List<String[]> alignments = null;

    public PairwiseDPTextAlignment(AnnotatedText aText1,
            AnnotatedText aText2)
    {
        this(insertAnnotationsInText(aText1).toCharArray(),
                insertAnnotationsInText(aText2).toCharArray(), GAP, OPEN_UNIT, CLOSE_UNIT);
    }

    public PairwiseDPTextAlignment(char[] textA, char[] textB, char alignChar, char openUnit,
            char closeUnit)
    {
        this(textA, textB, alignChar, openUnit, closeUnit, 1);
    }

    public PairwiseDPTextAlignment(char[] aTextA, char[] aTextB, char aAlignChar, char aOpenUnit,
            char aCloseUnit, int aWGap)
    {
        textA = aTextA;
        textB = aTextB;

        alignChar = aAlignChar;

        openUnit = aOpenUnit;
        metaChars = Set.of(aOpenUnit, aCloseUnit);

        wGap = aWGap;
    }

    private void fillMatrix()
    {
        alignmentMatrix = new int[textA.length + 1][textB.length + 1];

        // initialize first row and column
        for (int i = 0; i <= textA.length; i++) {
            alignmentMatrix[i][0] = i * wGap;
        }
        for (int j = 0; j <= textB.length; j++) {
            alignmentMatrix[0][j] = j * wGap;
        }

        for (int i = 1; i <= textA.length; i++) {
            for (int j = 1; j <= textB.length; j++) {
                int costAlign = alignmentMatrix[i - 1][j - 1] + weight(i, j);
                int costGapB = alignmentMatrix[i][j - 1] + wGap;
                int costGapA = alignmentMatrix[i - 1][j] + wGap;
                alignmentMatrix[i][j] = Math.min(Math.min(costAlign, costGapB), costGapA);
            }
        }
    }

    private void backtrack_step(int i, int j, String aAlignmentSeqA, String aAlignmentSeqB,
            List<String[]> aAlignments)
    {
        if (i > 0 || j > 0) {
            // if one meta-char
            if ((i > 0 && metaChars.contains(textA[i - 1]))
                    || (j > 0 && metaChars.contains(textB[j - 1]))) {
                // if both opening: align
                if (i > 0 && j > 0
                        && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j - 1] + weight(i, j)
                        && openUnit == textA[i - 1]) {
                    backtrack_step(i - 1, j - 1, aAlignmentSeqA + textA[i - 1],
                            aAlignmentSeqB + textB[j - 1], aAlignments);
                }
                // if only one is opening: align the other
                else if (i > 0 && j > 0 && openUnit == textA[i - 1]
                        && alignmentMatrix[i][j] == alignmentMatrix[i][j - 1] + wGap) {
                    backtrack_step(i, j - 1, aAlignmentSeqA + alignChar,
                            aAlignmentSeqB + textB[j - 1], aAlignments);
                }
                else if (i > 0 && j > 0 && openUnit == textB[j - 1]
                        && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j] + wGap) {
                    backtrack_step(i - 1, j, aAlignmentSeqA + textA[i - 1],
                            aAlignmentSeqB + alignChar, aAlignments);
                }
                // else - try all possible alignments
                else {
                    if (i > 0 && j > 0 && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j - 1]
                            + weight(i, j)) {
                        backtrack_step(i - 1, j - 1, aAlignmentSeqA + textA[i - 1],
                                aAlignmentSeqB + textB[j - 1], aAlignments);
                    }
                    if (j > 0 && alignmentMatrix[i][j] == alignmentMatrix[i][j - 1] + wGap) {
                        backtrack_step(i, j - 1, aAlignmentSeqA + alignChar,
                                aAlignmentSeqB + textB[j - 1], aAlignments);
                    }
                    if (i > 0 && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j] + wGap) {
                        backtrack_step(i - 1, j, aAlignmentSeqA + textA[i - 1],
                                aAlignmentSeqB + alignChar, aAlignments);
                    }
                }
            }
            // otherwise: align if possible, or add gap in first sequence or in second sequence
            else if (i > 0 && j > 0
                    && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j - 1] + weight(i, j)) {
                backtrack_step(i - 1, j - 1, aAlignmentSeqA + textA[i - 1],
                        aAlignmentSeqB + textB[j - 1], aAlignments);
            }
            else if (j > 0 && alignmentMatrix[i][j] == alignmentMatrix[i][j - 1] + wGap) {
                backtrack_step(i, j - 1, aAlignmentSeqA + alignChar, aAlignmentSeqB + textB[j - 1],
                        aAlignments);
            }
            else if (i > 0 && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j] + wGap) {
                backtrack_step(i - 1, j, aAlignmentSeqA + textA[i - 1], aAlignmentSeqB + alignChar,
                        aAlignments);
            }

        }
        else {
            String[] ret = { new StringBuilder(aAlignmentSeqA).reverse().toString(),
                    new StringBuilder(aAlignmentSeqB).reverse().toString() };
            aAlignments.add(ret);
        }
    }

    private void backtrack()
    {
        alignments = new LinkedList<String[]>();
        backtrack_step(textA.length, textB.length, "", "", alignments);
    }

    private int weight(int i, int j)
    {
        if (textA[i - 1] == textB[j - 1]) {
            return 0;
        }
        else {
            // only allow gaps!
            return wGap * 2 + 1;
        }
    }

    private void computeAlignments()
    {
        fillMatrix();
        backtrack();
    }

    @Override
    public List<String[]> getAlignments()
    {
        if (alignments == null) {
            this.computeAlignments();
        }
        return alignments;
    }

    @Override
    public int getInsertions()
    {
        if (alignments == null) {
            this.computeAlignments();
        }

        int i = textA.length;
        int j = textB.length;

        int ins = 0;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0
                    && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j - 1] + weight(i, j)) {
                i = i - 1;
                j = j - 1;
            }
            else if (j > 0 && alignmentMatrix[i][j] == alignmentMatrix[i][j - 1] + wGap) {
                j = j - 1;
                ins = ins + 1;
            }
            else if (i > 0 && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j] + wGap) {
                i = i - 1;
            }
        }
        return ins;

    }

    @Override
    public int getDeletions()
    {
        if (alignments == null) {
            this.computeAlignments();
        }
        int i = textA.length;
        int j = textB.length;

        int del = 0;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0
                    && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j - 1] + weight(i, j)) {
                i = i - 1;
                j = j - 1;
            }
            else if (j > 0 && alignmentMatrix[i][j] == alignmentMatrix[i][j - 1] + wGap) {
                j = j - 1;
            }
            else if (i > 0 && alignmentMatrix[i][j] == alignmentMatrix[i - 1][j] + wGap) {
                i = i - 1;
                del = del + 1;
            }
        }
        return del;
    }

    @Override
    public int getSubstitutions()
    {
        // the cost are set such that subs are not allowed!
        return 0;
    }

    @Override
    public int getLength()
    {
        if (alignments == null) {
            this.computeAlignments();
        }
        return alignments.get(0)[0].length();
    }

    private static String insertAnnotationsInText(AnnotatedText aText)
    {
        // assure that the characters denoting beginning and end of units and gaps do not appear in
        // the text
        if (aText.getText().indexOf(OPEN_UNIT) != -1) {
            throw new IllegalArgumentException(
                    "The character denoting the start of a unit may not appear in the text.");
        }
        else if (aText.getText().indexOf(CLOSE_UNIT) != -1) {
            throw new IllegalArgumentException(
                    "The character denoting the end of a unit may not appear in the text.");
        }
        else if (aText.getText().indexOf(GAP) != -1) {
            throw new IllegalArgumentException(
                    "The character denoting a gap may not appear in the text.");
        }

        var text = new StringBuilder(aText.getText());
        long pos = 0;
        long offset = 0;

        for (var annot : aText.getUnits()) {
            if (annot.getBegin() + offset < pos) {
                // units overlap
                throw new IllegalArgumentException("Text contains overlapping units.");
            }

            text.insert((int) (annot.getBegin() + offset), OPEN_UNIT);
            offset = offset + 2;
            pos = annot.getEnd() + offset;
            text.insert((int) (pos - 1), CLOSE_UNIT);
        }

        return text.toString();
    }
}
