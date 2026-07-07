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
import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.CLOSE_UNIT;
import static org.dkpro.statistics.agreement.aligning.TextGammaAgreement.OPEN_UNIT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.alignment.Alignment;
import org.dkpro.statistics.agreement.aligning.alignment.PairwiseDPTextAlignment;
import org.dkpro.statistics.agreement.aligning.alignment.UnitaryAlignment;

/**
 * Merges two {@link AnnotatedText}s into the alignments of their combined annotations.
 * <p>
 * Deviations from the upstream TextGammaTool implementation:
 * <ul>
 * <li>The open/close/gap marker characters are fixed constants from {@code TextGammaAgreement}
 * rather than per-call {@code char} parameters, so the upstream runtime marker-distinctness check
 * is gone.</li>
 * <li>Marker insertion into the raw text and the reserved-character / overlapping-unit validation
 * were moved out of the merge into {@code PairwiseDPTextAlignment} (behaviour preserved, ownership
 * moved).</li>
 * <li>Each {@code UnitaryAlignment} receives the annotation set's shared sorted rater-set view
 * instead of a fresh unordered copy per iteration.</li>
 * <li>Adds an equal-text fast path: when both texts are identical, the units already share a
 * coordinate system and are merged directly at their native offsets, bypassing the
 * {@code PairwiseDPTextAlignment} character alignment entirely (avoiding its quadratic matrix and
 * stack-heavy recursive backtracking on document-scale input).</li>
 * </ul>
 */
public class AnnotatedTextMerge
{
    /**
     * Merges two AnnotatedText's using Needleman-Wunsch'- the annotations are not allowed to
     * overlap
     * 
     * @return a set of all possible segmentations of the resulting "Text"
     */
    public static Set<Alignment> mergeAnnotatedTextsWithSegmentation(AnnotatedText aText1,
            AnnotatedText aText2)
    {
        return mergeAnnotatedTextsWithSegmentation(aText1, aText2, -1);
    }

    public static Set<Alignment> mergeAnnotatedTextsWithSegmentation(AnnotatedText aText1,
            AnnotatedText aText2, int aLevenshteinThreshold)
    {
        // Fast path: identical base text. When both raters annotated the very same text -- the
        // common case for pairwise agreement over a single source document -- the character-level
        // Needleman-Wunsch alignment is unnecessary. The two texts already share a coordinate
        // system, so their units can be merged directly at their native offsets. This avoids
        // building the O(n^2) DP matrix and, in particular, the deep recursive backtracking in
        // PairwiseDPTextAlignment, which overflows the stack on document-scale input. The
        // text-level
        // edit distance is zero here, so any Levenshtein threshold is trivially satisfied -- marker
        // (i.e. annotation) differences are what gamma measures, not a text divergence to reject.
        if (aText1.getText().equals(aText2.getText())) {
            var units = new ArrayList<AlignableAnnotationUnit>(
                    aText1.getUnits().size() + aText2.getUnits().size());
            units.addAll(aText1.getUnits());
            units.addAll(aText2.getUnits());
            return Set.of(buildSegmentedAlignment(units));
        }

        var aligner = new PairwiseDPTextAlignment(aText1, aText2);

        // get the alignment cost and throw an Exception if cost is above a threshold
        if (aLevenshteinThreshold > -1) {
            int lev = aligner.getInsertions() + aligner.getDeletions() + aligner.getSubstitutions();
            if (lev > aLevenshteinThreshold) {
                throw new IllegalArgumentException(
                        "The texts are more different than given threshold.");
            }
        }

        // create annotation sets with the new annotations
        var bestAlignedUnits = new HashSet<List<AlignableAnnotationUnit>>();
        int bestOverlap = 0;

        var allUnits = new ArrayList<AlignableAnnotationUnit[]>(2);
        allUnits.add(aText1.getUnits().toArray(AlignableAnnotationUnit[]::new));
        allUnits.add(aText2.getUnits().toArray(AlignableAnnotationUnit[]::new));

        for (var alignment : aligner.getAlignments()) {
            var alignedAnnotations = getUnitsFromText(asList(alignment), allUnits);
            AlignableAnnotationUnit[] units1 = alignedAnnotations.get(0);
            AlignableAnnotationUnit[] units2 = alignedAnnotations.get(1);

            int overlap = countPairwiseColocatedUnits(units1, units2);

            // add only those with the best overlap
            if (overlap >= bestOverlap) {
                // found an alignment with better overlap than all alignments before
                if (overlap > bestOverlap) {
                    bestAlignedUnits.clear();
                    bestOverlap = overlap;
                }

                var unitGroup = new ArrayList<AlignableAnnotationUnit>();
                unitGroup.addAll(asList(units1));
                unitGroup.addAll(asList(units2));

                bestAlignedUnits.add(unitGroup);
            }
        }

        // create alignments
        var alignmentSet = new HashSet<Alignment>();
        for (var units : bestAlignedUnits) {
            alignmentSet.add(buildSegmentedAlignment(units));
        }

        return alignmentSet;

    }

    /**
     * Groups the given units (assumed to live in a common coordinate system) into a single
     * {@link Alignment}: co-extensive units are collected into the same {@link UnitaryAlignment},
     * relying on {@link AnnotationSet#getUnits()} returning them in offset order.
     */
    private static Alignment buildSegmentedAlignment(List<AlignableAnnotationUnit> aUnits)
    {
        var alignedUnits = new HashSet<UnitaryAlignment>();
        var annoset = new AnnotationSet(aUnits);
        var alignments = new ArrayList<AlignableAnnotationUnit>(annoset.getRaterCount());

        AlignableAnnotationUnit lastUnit = null;
        for (var u : annoset.getUnits()) {
            if (lastUnit != null && !u.isCoextensive(lastUnit)) {
                alignedUnits.add(new UnitaryAlignment(alignments, annoset.getRaters()));
                alignments.clear();
            }

            alignments.add(u);
            lastUnit = u;
        }

        if (!alignments.isEmpty()) {
            alignedUnits.add(new UnitaryAlignment(alignments, annoset.getRaters()));
        }

        return new Alignment(alignedUnits, annoset);
    }

    private static int countPairwiseColocatedUnits(AlignableAnnotationUnit[] units1,
            AlignableAnnotationUnit[] units2)
    {
        int overlap = 0;
        for (var u1 : units1) {
            for (var u2 : units2) {
                if (u1.getBegin() == u2.getBegin() && u1.getEnd() == u2.getEnd()) {
                    overlap++;
                }
            }
        }
        return overlap;
    }

    private static List<AlignableAnnotationUnit[]> getUnitsFromText(List<String> texts,
            List<AlignableAnnotationUnit[]> aAllUnits)
    {
        if (texts.isEmpty()) {
            return null;
        }

        if (texts.size() != aAllUnits.size()) {
            // number of texts has to be equal to the number of annotation lists
            throw new IllegalArgumentException("Numbers of annotation lists and texts differ.");
        }

        int textLength = texts.get(0).length();
        for (var text : texts) {
            if (text.length() != textLength) {
                throw new IllegalArgumentException("Texts have to be of the same length.");
            }
        }

        var newUnits = new ArrayList<AlignableAnnotationUnit[]>(aAllUnits.size());
        var currentBegins = new ArrayList<Integer>(aAllUnits.size());
        var annotNumbers = new ArrayList<Integer>(aAllUnits.size());

        for (int i = 0; i < aAllUnits.size(); i++) {
            newUnits.add(new AlignableAnnotationUnit[aAllUnits.get(i).length]);
            currentBegins.add(0);
            annotNumbers.add(0);
        }

        int offset = 0;
        for (int i = 0; i < texts.get(0).length(); i++) {
            // only change offset once per position
            var offsetChange = false;

            for (int j = 0; j < texts.size(); j++) {
                var text = texts.get(j);
                if (text.charAt(i) == OPEN_UNIT) {
                    currentBegins.set(j, i - offset);
                    offsetChange = true;
                }
                else if (text.charAt(i) == CLOSE_UNIT) {
                    newUnits.get(j)[annotNumbers.get(j)] = aAllUnits.get(j)[annotNumbers.get(j)]
                            .cloneWithDifferentOffsets(currentBegins.get(j), i - offset);

                    annotNumbers.set(j, annotNumbers.get(j) + 1);
                    offsetChange = true;
                }
            }

            if (offsetChange) {
                offset += 1;
            }
        }

        return newUnits;
    }
}
