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
package org.dkpro.statistics.agreement.aligning.disorder;

import static java.util.Arrays.asList;
import static org.dkpro.statistics.agreement.aligning.shuffling.AnnotationSetShuffle.changeSegmentation;
import static org.dkpro.statistics.agreement.aligning.shuffling.AnnotationSetShuffle.changeText;
import static org.dkpro.statistics.agreement.aligning.shuffling.AnnotationSetShuffle.randomizeFeatureValues;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.TextGammaAgreement;
import org.dkpro.statistics.agreement.aligning.data.AlignableAnnotationTextUnit;
import org.dkpro.statistics.agreement.aligning.data.AnnotatedText;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.shuffling.SegmentationChangeType;
import org.dkpro.statistics.agreement.aligning.shuffling.TextChangeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDisorderSampler
    implements IDisorderSampler
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final TextGammaAgreement measure;

    private final EnumeratedDistribution<TextChangeType> changeChooserText;
    private final EnumeratedDistribution<Character> characterGenerator;

    private final EnumeratedDistribution<SegmentationChangeType> changeChooserSeg;

    private final Map<String, EnumeratedDistribution<String>> labelGenerators;

    private final double textChangeRate;
    private final double segmentChangeRate;

    // creates a SimpleDisorderSampler with uniform distribution over change types
    public SimpleDisorderSampler(TextGammaAgreement aMeasure, double aTextChangeRate,
            double aSegmentChangeRate)
    {
        measure = aMeasure;
        textChangeRate = aTextChangeRate;
        segmentChangeRate = aSegmentChangeRate;

        var units = new ArrayList<AlignableAnnotationUnit>();
        for (var text : measure.getTexts()) {
            units.addAll(text.getUnits());
        }

        characterGenerator = createCharacterGenerator(units);
        labelGenerators = createLabelGenerators(aMeasure.getTexts(), units);

        // set probabilities of change types to equal probabilities
        var prop = 1 / (double) TextChangeType.values().length;
        var pt = new ArrayList<Pair<TextChangeType, Double>>();
        for (var type : TextChangeType.values()) {
            pt.add(new Pair<TextChangeType, Double>(type, prop));
        }

        changeChooserText = new EnumeratedDistribution<TextChangeType>(pt);

        prop = 1 / (double) SegmentationChangeType.values().length;
        var ps = new ArrayList<Pair<SegmentationChangeType, Double>>();
        for (var type : SegmentationChangeType.values()) {
            ps.add(new Pair<SegmentationChangeType, Double>(type, prop));
        }

        changeChooserSeg = new EnumeratedDistribution<SegmentationChangeType>(ps);
    }

    @Override
    public Double sampleDisorder()
    {
        // REC: The original TextGamma had a "base text" which was used for disorder sampling here.
        // However, since we have no "base text" in general, the easiest way is to sample the
        // disorder on both texts and then take the mean average. Not sure if this is a viable idea,
        // but it seems like a reasonable starting point.
        var disorder = 0.0;
        for (var text : measure.getTexts()) {
            disorder += sampleDisorder(text);
        }
        
        return disorder / (double) measure.getTexts().size();
    }

    private double sampleDisorder(AnnotatedText aText)
    {
        var textChangeSampler = new BinomialDistribution(aText.getUnitCount(), textChangeRate);
        var segChangeSampler = new BinomialDistribution(aText.getUnitCount(), segmentChangeRate);

        // 1. apply textual changes
        var version1 = changeText(aText, textChangeSampler.sample(), changeChooserText,
                characterGenerator);
        var version2 = changeText(aText, textChangeSampler.sample(), changeChooserText,
                characterGenerator);

        // 2. apply segmentation changes
        var set1 = changeSegmentation(new AnnotationSet(version1.getTextUnits()),
                segChangeSampler.sample(), changeChooserSeg);
        var set2 = changeSegmentation(new AnnotationSet(version2.getTextUnits()),
                segChangeSampler.sample(), changeChooserSeg);

        // 3. apply categorization changes
        for (var featureName : labelGenerators.keySet()) {
            var labelGenerator = labelGenerators.get(featureName);
            if (labelGenerator == null) {
                throw new IllegalArgumentException(
                        "No generator for attribute " + featureName + " given.");
            }
            set1 = randomizeFeatureValues(set1, featureName, labelGenerator);
            set2 = randomizeFeatureValues(set2, featureName, labelGenerator);
        }

        // change the annotators
        var a = new Rater("A", 0);
        var b = new Rater("B", 1);

        var set1_arr = set1.getUnits().stream() //
                .map(u -> u.cloneWithDifferentRater(a)) //
                .map(u -> (AlignableAnnotationTextUnit) u) //
                .toList();
        var set2_arr = set2.getUnits().stream() //
                .map(u -> u.cloneWithDifferentRater(b)) //
                .map(u -> (AlignableAnnotationTextUnit) u) //
                .toList();

        var disorder = TextGammaAgreement.getObservedDisorder(
                new AnnotatedText(version1.getText(), set1_arr),
                new AnnotatedText(version2.getText(), set2_arr), measure.getDissimilarity());
        
        LOG.trace("Sampled disorder: {}", disorder);
        
        return disorder;
    }

    private static EnumeratedDistribution<Character> createCharacterGenerator(
            List<AlignableAnnotationUnit> units)
    {
        int numChars = 0;
        var characters = new HashMap<Character, Integer>();
        for (var u : units) {
            if (u instanceof AlignableAnnotationTextUnit textUnit) {
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

        var pc = new ArrayList<Pair<Character, Double>>(characters.size());
        for (var c : characters.keySet()) {
            pc.add(new Pair<Character, Double>(c, characters.get(c) / (double) numChars));
        }

        return new EnumeratedDistribution<Character>(pc);
    }

    private static HashMap<String, EnumeratedDistribution<String>> createLabelGenerators(
            List<AnnotatedText> aAnnotatedTexts, List<AlignableAnnotationUnit> units)
    {
        var featureNames = new HashSet<String>();
        for (var text : aAnnotatedTexts) {
            featureNames.addAll(asList(text.getFeatureNames()));
        }

        var labelGenerators = new HashMap<String, EnumeratedDistribution<String>>();
        var labels = new HashMap<String, Integer>();
        for (var featureName : featureNames) {
            for (var u : units) {
                var label = u.getFeatureValue(featureName);
                if (labels.containsKey(u.getFeatureValue(featureName))) {
                    labels.put(label, labels.get(label) + 1);
                }
                else {
                    labels.put(label, 1);
                }
            }

            var pl = new ArrayList<Pair<String, Double>>();
            for (var label : labels.keySet()) {
                pl.add(new Pair<String, Double>(label, labels.get(label) / (double) units.size()));
            }

            labelGenerators.put(featureName, new EnumeratedDistribution<String>(pl));
        }

        return labelGenerators;
    }
}
