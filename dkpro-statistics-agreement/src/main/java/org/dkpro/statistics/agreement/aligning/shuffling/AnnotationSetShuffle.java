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
package org.dkpro.statistics.agreement.aligning.shuffling;

import static org.dkpro.statistics.agreement.aligning.shuffling.TextChangeType.DELETION;
import static org.dkpro.statistics.agreement.aligning.shuffling.TextChangeType.INSERTION;
import static org.dkpro.statistics.agreement.aligning.shuffling.TextChangeType.SUBSTITUTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.data.AlignableAnnotationTextUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotatedText;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;

public class AnnotationSetShuffle
{

    // 1. Change the text for AnnotatedText
    // Important: the functions changing the text assume a segmentation, i.e. the units may not
    // overlap this is not tested; if this assumption is violated, some units may not be adapted
    // correctly

    public static AnnotatedText shuffleText(AnnotatedText orig, double m)
    {
        if (!(0 <= m && m <= 1)) {
            throw new IllegalArgumentException("Magnitude has to be between 0 and 1");
        }

        var numChanges = (int) (orig.getUnitCount() * m);

        var prop = 1 / (double) TextChangeType.values().length;
        var pt = new ArrayList<Pair<TextChangeType, Double>>(TextChangeType.values().length);

        for (var type : TextChangeType.values()) {
            pt.add(new Pair<TextChangeType, Double>(type, prop));
        }
        var changes = new EnumeratedDistribution<TextChangeType>(pt);

        var numChars = 0;
        var characters = new HashMap<Character, Integer>();
        for (var unit : orig.getUnits()) {
            if (unit instanceof AlignableAnnotationTextUnit textUnit) {
                for (char c : textUnit.getText().toCharArray()) {
                    numChars += 1;
                    if (characters.containsKey(c)) {
                        characters.put(c, characters.get(c) + 1);
                    }
                    else {
                        characters.put(c, 1);
                    }
                }
            }
        }

        var pc = new ArrayList<Pair<Character, Double>>();
        for (var c : characters.keySet()) {
            pc.add(new Pair<Character, Double>(c, characters.get(c) / (double) numChars));
        }

        var characterGen = new EnumeratedDistribution<Character>(pc);

        return changeText(orig, numChanges, changes, characterGen);
    }

    public static AnnotatedText changeText(AnnotatedText orig, int changes,
            EnumeratedDistribution<TextChangeType> changeChooser,
            EnumeratedDistribution<Character> characterGenerator)
    {
        return changeText(orig, changes, changeChooser, characterGenerator, new Random());
    }

    public static AnnotatedText changeText(AnnotatedText orig, int changes,
            EnumeratedDistribution<TextChangeType> changeChooser,
            EnumeratedDistribution<Character> characterGenerator, Random positionChooser)
    {

        var text = new StringBuilder(orig.getText());
        var annots = new ArrayList<>(orig.getTextUnits());

        var changedUnits = new HashSet<Integer>();

        // avoid infinite loop if changes > number of annotations!
        if (changes > annots.size()) {
            changes = annots.size();
        }

        for (int i = 0; i < changes; i++) {

            boolean changed = false;
            while (!changed) {
                TextChangeType type = changeChooser.sample();
                if (type.equals(INSERTION)) {
                    changeTextInsertion(text, annots, characterGenerator, positionChooser,
                            changedUnits);
                    changed = true;
                }
                else if (type.equals(DELETION)) {
                    // changeTextDeletion(text, annots, positionChooser, changed_units);
                    changed = changeTextDeletionWithoutUnit(text, annots, positionChooser,
                            changedUnits);
                }
                else if (type.equals(SUBSTITUTION)) {
                    changeTextSubstitution(text, annots, characterGenerator, positionChooser,
                            changedUnits);
                    changed = true;
                }
            }

        }

        return new AnnotatedText(text.toString(), annots);
    }

    private static int pickUnitToChange(Random positionGenerator, int num_annotations,
            Set<Integer> changed_units)
    {

        // avoid infinite loops if all units are marked as changed
        // should not happen as the number of changes is restricted to the number of units!
        if (changed_units.size() == num_annotations) {
            new RuntimeException("All units have been changed; but more changes are needed");
        }

        int unit_offset = positionGenerator.nextInt(num_annotations);
        while (changed_units.contains(unit_offset)) {
            unit_offset = positionGenerator.nextInt(num_annotations);
        }
        changed_units.add(unit_offset);

        return unit_offset;
    }

    private static int pickTextOffset(Random positionGenerator, AlignableAnnotationUnit u)
    {
        // pick a position for the change
        int pos_offset = positionGenerator.nextInt((int) (u.getEnd() - u.getBegin()));
        return (int) (u.getBegin() + pos_offset);

    }

    public static AnnotatedText changeTextInsertion(AnnotatedText orig,
            EnumeratedDistribution<Character> characterGenerator)
    {
        return AnnotationSetShuffle.changeTextInsertion(orig, characterGenerator, new Random());
    }

    public static AnnotatedText changeTextInsertion(AnnotatedText orig,
            EnumeratedDistribution<Character> characterGenerator, Random positionGenerator)
    {
        var text = new StringBuilder(orig.getText());
        var annots = new ArrayList<>(orig.getTextUnits());

        AnnotationSetShuffle.changeTextInsertion(text, annots, characterGenerator,
                positionGenerator, new HashSet<Integer>());

        return new AnnotatedText(text.toString(), annots);
    }

    @SuppressWarnings("unchecked")
    private static <T extends AlignableAnnotationUnit> void changeTextInsertion(StringBuilder orig,
            List<T> annotations, EnumeratedDistribution<Character> characterGenerator,
            Random positionGenerator, Set<Integer> aChangedUnits)
    {

        // pick a unit and position to change
        int unitIndex = pickUnitToChange(positionGenerator, annotations.size(), aChangedUnits);
        AlignableAnnotationUnit u = annotations.get(unitIndex);
        // pick a position for the change
        int pos_offset = positionGenerator.nextInt((int) (u.getEnd() - u.getBegin() + 1));
        int pos = (int) (u.getBegin() + pos_offset);

        // update the text
        orig.insert(pos, characterGenerator.sample().charValue());

        // move the end of the unit to the right, change text if textunit
        u = u.cloneWithDifferentOffsets(u.getBegin(), u.getEnd() + 1);
        if (u instanceof AlignableAnnotationTextUnit textUnit) {
            u = textUnit
                    .cloneWithDifferentText(orig.substring((int) u.getBegin(), (int) u.getEnd()));
        }
        annotations.set(unitIndex, (T) u);

        // move all following units to the right
        for (int i = unitIndex + 1; i < annotations.size(); i++) {
            u = annotations.get(i);
            annotations.set(i, (T) u.cloneWithDifferentOffsets(u.getBegin() + 1, u.getEnd() + 1));
        }
    }

    public static AnnotatedText changeTextDeletion(AnnotatedText orig)
    {
        return AnnotationSetShuffle.changeTextDeletion(orig, new Random());
    }

    public static AnnotatedText changeTextDeletion(AnnotatedText orig, Random positionGenerator)
    {
        var text = new StringBuilder(orig.getText());
        var annots = new ArrayList<>(orig.getTextUnits());

        changeTextDeletion(text, annots, positionGenerator, new HashSet<Integer>());

        return new AnnotatedText(text.toString(), annots);
    }

    @SuppressWarnings("unchecked")
    private static <T extends AlignableAnnotationUnit> void changeTextDeletion(StringBuilder orig,
            List<T> annotations, Random positionGenerator, Set<Integer> changed_units)
    {

        // pick a unit and position to change
        int unit_offset = pickUnitToChange(positionGenerator, annotations.size(), changed_units);
        AlignableAnnotationUnit u = annotations.get(unit_offset);
        int pos = pickTextOffset(positionGenerator, u);

        // update the text
        orig.deleteCharAt(pos);

        if (u.getEnd() - u.getBegin() > 1) {
            // move the end of the unit to the left, change text if textunit
            u = u.cloneWithDifferentOffsets(u.getBegin(), u.getEnd() - 1);
            if (u instanceof AlignableAnnotationTextUnit textUnit) {
                u = textUnit.cloneWithDifferentText(
                        orig.substring((int) u.getBegin(), (int) u.getEnd()));
            }
            annotations.set(unit_offset, (T) u);
        }
        else {
            // remove the unit if it only spans the deleted char
            annotations.remove(unit_offset);
            unit_offset -= 1;
        }

        // move all following units to the left
        for (int i = unit_offset + 1; i < annotations.size(); i++) {
            u = annotations.get(i);
            annotations.set(i, (T) u.cloneWithDifferentOffsets(u.getBegin() - 1, u.getEnd() - 1));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AlignableAnnotationUnit> boolean changeTextDeletionWithoutUnit(
            StringBuilder orig, List<T> annotations, Random positionGenerator,
            Set<Integer> changed_units)
    {

        // pick a unit to change
        int unit_offset = positionGenerator.nextInt(annotations.size());
        while (changed_units.contains(unit_offset)) {
            unit_offset = positionGenerator.nextInt(annotations.size());
        }

        AlignableAnnotationUnit u = annotations.get(unit_offset);
        // pick a position for the change
        int pos_offset = positionGenerator.nextInt((int) (u.getEnd() - u.getBegin()));

        int pos = (int) annotations.get(unit_offset).getBegin() + pos_offset;

        if (u.getEnd() - u.getBegin() == 1) {
            // don't change the text
            return false;
        }

        changed_units.add(unit_offset);

        // update the text
        orig.deleteCharAt(pos);

        // move the end of the unit to the left, change text if textunit
        u = u.cloneWithDifferentOffsets(u.getBegin(), u.getEnd() - 1);
        if (u instanceof AlignableAnnotationTextUnit textUnit) {
            u = textUnit
                    .cloneWithDifferentText(orig.substring((int) u.getBegin(), (int) u.getEnd()));
        }
        annotations.set(unit_offset, (T) u);

        // move all following units to the left
        for (int i = unit_offset + 1; i < annotations.size(); i++) {
            u = annotations.get(i);
            annotations.set(i, (T) u.cloneWithDifferentOffsets(u.getBegin() - 1, u.getEnd() - 1));
        }

        return true;
    }

    public static AnnotatedText changeTextSubstitution(AnnotatedText orig,
            EnumeratedDistribution<Character> characterGenerator)
    {
        return AnnotationSetShuffle.changeTextSubstitution(orig, characterGenerator, new Random());
    }

    public static AnnotatedText changeTextSubstitution(AnnotatedText orig,
            EnumeratedDistribution<Character> characterGenerator, Random positionGenerator)
    {
        var text = new StringBuilder(orig.getText());
        var annots = new ArrayList<>(orig.getTextUnits());

        AnnotationSetShuffle.changeTextSubstitution(text, annots, characterGenerator,
                positionGenerator, new HashSet<Integer>());

        return new AnnotatedText(text.toString(), annots);
    }

    @SuppressWarnings("unchecked")
    private static <T extends AlignableAnnotationUnit> void changeTextSubstitution(
            StringBuilder orig, List<T> annotations,
            EnumeratedDistribution<Character> characterGenerator, Random positionGenerator,
            Set<Integer> changed_units)
    {

        // pick a unit and position to change
        int unit_offset = pickUnitToChange(positionGenerator, annotations.size(), changed_units);
        AlignableAnnotationUnit u = annotations.get(unit_offset);
        int pos = pickTextOffset(positionGenerator, u);

        // update the text
        char orig_char = orig.charAt(pos);
        Character new_char = characterGenerator.sample();
        while (new_char.charValue() == orig_char) {
            new_char = characterGenerator.sample();
        }
        orig.replace(pos, pos + 1, new_char.toString());

        // change text of the unit if textunit
        if (u instanceof AlignableAnnotationTextUnit textUnit) {
            annotations.set(unit_offset, (T) textUnit
                    .cloneWithDifferentText(orig.substring((int) u.getBegin(), (int) u.getEnd())));
        }
    }

    // 2. Change the segmentation for AnnotationSet

    public static AnnotationSet shuffleSegmentation(AnnotationSet orig, double m)
    {
        if (!(0 <= m && m <= 1)) {
            throw new IllegalArgumentException("Magnitude has to be between 0 and 1");
        }

        int numChanges = (int) (orig.getUnitCount() * m);

        double prop = 1 / (double) SegmentationChangeType.values().length;
        var ps = new ArrayList<Pair<SegmentationChangeType, Double>>(
                SegmentationChangeType.values().length);
        for (SegmentationChangeType type : SegmentationChangeType.values()) {
            ps.add(new Pair<SegmentationChangeType, Double>(type, prop));
        }
        var changes = new EnumeratedDistribution<SegmentationChangeType>(ps);

        return changeSegmentation(orig, numChanges, changes);
    }

    public static AnnotationSet changeSegmentation(AnnotationSet orig, int changes,
            EnumeratedDistribution<SegmentationChangeType> changeChooser)
    {
        return AnnotationSetShuffle.changeSegmentation(orig, changes, 0, changeChooser);
    }

    public static AnnotationSet changeSegmentation(AnnotationSet orig, int changes, int merge_gap,
            EnumeratedDistribution<SegmentationChangeType> changeChooser)
    {
        return AnnotationSetShuffle.changeSegmentation(orig, changes, merge_gap, changeChooser,
                new Random());
    }

    public static AnnotationSet changeSegmentation(AnnotationSet orig, int changes,
            EnumeratedDistribution<SegmentationChangeType> changeChooser, Random positionChooser)
    {
        return changeSegmentation(orig, changes, 0, changeChooser, positionChooser);
    }

    public static AnnotationSet changeSegmentation(AnnotationSet orig, int changes, int merge_gap,
            EnumeratedDistribution<SegmentationChangeType> changeChooser, Random positionChooser)
    {
        var annots = new ArrayList<>(orig.getUnits());

        for (int i = 0; i < changes; i++) {
            SegmentationChangeType type = changeChooser.sample();
            if (type.equals(SegmentationChangeType.MERGE)) {
                changeSegmentationMerge(annots, merge_gap, positionChooser);
            }
            if (type.equals(SegmentationChangeType.SPLIT)) {
                changeSegmentationSplit(annots, positionChooser);
            }
        }

        return new AnnotationSet(annots);
    }

    public static AnnotationSet changeSegmentationMerge(AnnotationSet orig)
    {
        return changeSegmentationMerge(orig, new Random());
    }

    public static AnnotationSet changeSegmentationMerge(AnnotationSet orig,
            Random positionGenerator)
    {
        return changeSegmentationMerge(orig, 0, positionGenerator);
    }

    public static AnnotationSet changeSegmentationMerge(AnnotationSet orig, int gap)
    {
        return changeSegmentationMerge(orig, gap, new Random());
    }

    public static AnnotationSet changeSegmentationMerge(AnnotationSet orig, int gap,
            Random positionGenerator)
    {
        var annots = new LinkedList<AlignableAnnotationUnit>(orig.getUnits());
        AnnotationSetShuffle.changeSegmentationMerge(annots, gap, positionGenerator);
        return new AnnotationSet(annots);
    }

    private static void changeSegmentationMerge(List<AlignableAnnotationUnit> annotations, int gap,
            Random positionGenerator)
    {
        // pick a unit to merge
        int unit_offset = positionGenerator.nextInt(annotations.size());
        AlignableAnnotationUnit base = annotations.get(unit_offset);
        AlignableAnnotationUnit merge = null;

        int searchOffset = 1;
        while (unit_offset + searchOffset < annotations.size()
                && annotations.get(unit_offset + searchOffset).getBegin() <= base.getEnd() + gap) {

            merge = annotations.get(unit_offset + searchOffset);
            // test if merge is a valid merge_candidate
            if (merge.getBegin() >= base.getEnd()
                    && Objects.equals(base.getRater(), merge.getRater())
                    && Objects.equals(base.getCategory(), merge.getCategory())) {
                break;
            }

            merge = null;
            searchOffset += 1;
        }

        if (merge != null) {
            annotations.set(unit_offset,
                    base.cloneWithDifferentOffsets(base.getBegin(), merge.getEnd()));
            if (base instanceof AlignableAnnotationTextUnit) {
                annotations.set(unit_offset,
                        ((AlignableAnnotationTextUnit) annotations.get(unit_offset))
                                .cloneWithDifferentText(
                                        ((AlignableAnnotationTextUnit) base).getText()
                                                + (merge.getBegin() - base.getEnd() > 0 ? " " : "")
                                                + ((AlignableAnnotationTextUnit) merge).getText()));
            }
            annotations.remove(unit_offset + searchOffset);
        }

    }

    public static AnnotationSet changeSegmentationSplit(AnnotationSet orig)
    {
        return changeSegmentationSplit(orig, new Random());
    }

    public static AnnotationSet changeSegmentationSplit(AnnotationSet orig,
            Random positionGenerator)
    {
        var annots = new LinkedList<AlignableAnnotationUnit>(orig.getUnits());
        AnnotationSetShuffle.changeSegmentationSplit(annots, positionGenerator);
        return new AnnotationSet(annots);
    }

    private static void changeSegmentationSplit(List<AlignableAnnotationUnit> annotations,
            Random positionGenerator)
    {

        // pick a unit to change
        int unit_offset = positionGenerator.nextInt(annotations.size());
        AlignableAnnotationUnit u = annotations.get(unit_offset);

        if (u.getEnd() - u.getBegin() <= 1) {
            // split not possible
            return;
        }

        // pick a position for the split
        int pos = positionGenerator.nextInt((int) u.getEnd() - (int) u.getBegin() - 1) + 1;

        AlignableAnnotationUnit u1 = u.cloneWithDifferentOffsets(u.getBegin(), u.getBegin() + pos);
        AlignableAnnotationUnit u2 = u.cloneWithDifferentOffsets(u.getBegin() + pos, u.getEnd());

        if (u instanceof AlignableAnnotationTextUnit) {
            u1 = ((AlignableAnnotationTextUnit) u1).cloneWithDifferentText(
                    ((AlignableAnnotationTextUnit) u).getText().substring(0, pos));
            u2 = ((AlignableAnnotationTextUnit) u2).cloneWithDifferentText(
                    ((AlignableAnnotationTextUnit) u).getText().substring(pos));
        }

        annotations.set(unit_offset, u1);
        annotations.add(unit_offset + 1, u2);
    }

    // 3. Create random labels for AnnotationSet

    public static AnnotationSet randomizeFeatureValues(AnnotationSet orig, String aFeatureName,
            EnumeratedDistribution<String> labelGenerator)
    {
        var relabeled = new ArrayList<AlignableAnnotationUnit>();

        for (var annot : orig.getUnits()) {
            relabeled.add(annot.cloneWithDifferentLabel(aFeatureName, labelGenerator.sample()));
        }

        return new AnnotationSet(relabeled);
    }

    public static AnnotationSet shuffleAttributeValues(AnnotationSet orig, String attribute,
            double m)
    {
        if (!(0 <= m && m <= 1)) {
            throw new IllegalArgumentException("Magnitude has to be between 0 and 1");
        }

        int numChanges = (int) (orig.getUnitCount() * m);

        var labels = new HashMap<String, Integer>();

        for (var u : orig.getUnits()) {
            var label = u.getFeatureValue(attribute);
            if (labels.containsKey(u.getFeatureValue(attribute))) {
                labels.put(label, labels.get(label) + 1);
            }
            else {
                labels.put(label, 1);
            }
        }

        var ps = new ArrayList<Pair<String, Double>>(labels.size());
        for (var label : labels.keySet()) {
            ps.add(new Pair<String, Double>(label,
                    labels.get(label) / (double) orig.getUnitCount()));
        }

        var labelGenerator = new EnumeratedDistribution<>(ps);

        return changeAttributeValues(orig, attribute, numChanges, labelGenerator);
    }

    public static AnnotationSet changeAttributeValues(AnnotationSet orig, String attribute,
            int changes, EnumeratedDistribution<String> labelGenerator)
    {
        return changeAttributeValues(orig, attribute, changes, labelGenerator, new Random());
    }

    public static AnnotationSet changeAttributeValues(AnnotationSet orig, String attribute,
            int changes, EnumeratedDistribution<String> labelGenerator, Random positionChooser)
    {
        var units = new ArrayList<>(orig.getUnits());

        var changedUnits = new HashSet<Integer>();

        for (var i = 0; i < changes; i++) {
            changeAttributeValue(units, attribute, labelGenerator, positionChooser, changedUnits);
        }

        return new AnnotationSet(units);
    }

    public static AnnotationSet changeAttributeValue(AnnotationSet orig, String attribute,
            EnumeratedDistribution<String> labelGenerator)
    {
        return changeAttributeValue(orig, attribute, labelGenerator, new Random());
    }

    public static AnnotationSet changeAttributeValue(AnnotationSet orig, String attribute,
            EnumeratedDistribution<String> labelGenerator, Random positionGenerator)
    {
        var annots = new ArrayList<>(orig.getUnits());
        changeAttributeValue(annots, attribute, labelGenerator, positionGenerator, new HashSet<>());
        return new AnnotationSet(annots);
    }

    private static void changeAttributeValue(List<AlignableAnnotationUnit> aUnits, String attribute,
            EnumeratedDistribution<String> labelGenerator, Random positionGenerator,
            Set<Integer> changed_units)
    {
        // pick a unit to change
        int unitIndex = pickUnitToChange(positionGenerator, aUnits.size(), changed_units);

        var origLabel = aUnits.get(unitIndex).getFeatureValue(attribute);
        var newLabel = labelGenerator.sample();
        while (newLabel.equals(origLabel)) {
            newLabel = labelGenerator.sample();
        }

        aUnits.set(unitIndex, aUnits.get(unitIndex).cloneWithDifferentLabel(attribute, newLabel));
    }
}
