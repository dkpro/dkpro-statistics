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
package org.dkpro.statistics.agreement.aligning;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.util.Arrays.asList;
import static org.dkpro.statistics.agreement.aligning.data.AnnotatedTextMerge.mergeAnnotatedTextsWithSegmentation;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.dkpro.statistics.agreement.DisagreementMeasure;
import org.dkpro.statistics.agreement.aligning.data.AnnotatedText;
import org.dkpro.statistics.agreement.aligning.disorder.IDisorderSampler;
import org.dkpro.statistics.agreement.aligning.disorder.IDisorderSamplerFactory;
import org.dkpro.statistics.agreement.aligning.dissimilarity.IDissimilarity;
import org.dkpro.statistics.agreement.aligning.dissimilarity.NominalFeatureTextDissimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Text-Gamma-measure for calculating a chance-corrected inter-rater agreement
 * for aligning studies with two (maybe multiple) raters.
 * 
 * References:
 * <ul>
 * <li>Barteld, F., Schröder, I., Zinsmeister, H.: tγ – Inter-annotator agreement for categorization
 * with simultaneous segmentation and transcription-error correction. In Proceedings of the 13th
 * Conference on Natural Language Processing (KONVENS 2016) pp. 27-37, 2016.</li>
 * </ul>
 * 
 * @author Fabian Barteld (original)
 * @author Richard Eckart de Castilho (adaptation into DKPro Statistics)
 */
public class TextGammaAgreement
    extends DisagreementMeasure
    implements IAligningAgreementMeasure
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final char OPEN_UNIT = '\uFDD0';
    public static final char CLOSE_UNIT = '\uFDD1';
    public static final char GAP = '\uFDD2';

    static {
        checkConstants();
    }

    private final AnnotatedText text1;
    private final AnnotatedText text2;
    private final IDissimilarity dissimilarity;
    private final IDisorderSampler sampler;
    private final double precision;
    private final double alpha;

    private TextGammaAgreement(Builder builder)
    {
        dissimilarity = builder.dissimilarity;
        precision = builder.precision;
        alpha = builder.alpha;

        if (builder.texts != null && builder.study != null) {
            throw new IllegalArgumentException(
                    "Either texts or a study must be given but not both");
        }

        if (builder.texts == null && builder.study == null) {
            throw new IllegalArgumentException("Either texts or a study must be given");
        }

        if (builder.texts != null) {
            if (builder.texts.size() != 2) {
                throw new IllegalArgumentException("Exactly two texts must be compared");
            }

            if (builder.texts.get(0).getRaterCount() != 1
                    || builder.texts.get(1).getRaterCount() != 1) {
                throw new IllegalArgumentException("Each text must have exactly one rater");
            }

            text1 = builder.texts.get(0);
            text2 = builder.texts.get(1);
        }
        else {
            if (builder.study.getRaters().size() != 2) {
                throw new IllegalArgumentException("The study must contain exactly two raters");
            }

            var texts = new ArrayList<AnnotatedText>();
            for (var rater : builder.study.getRaters()) {
                var units = builder.study.getTextUnits().stream() //
                        .filter(u -> rater.equals(u.getRater())) //
                        .toList();
                texts.add(new AnnotatedText(builder.study.getText(), units));
            }

            text1 = texts.get(0);
            text2 = texts.get(1);
        }

        if (builder.sampler != null) {
            sampler = builder.sampler;
        }
        else if (builder.samplerFactory != null) {
            sampler = builder.samplerFactory.create(this);
        }
        else {
            throw new IllegalArgumentException(
                    "Either a disorder sampler or sampler factory must be given");
        }
    }

    public IDissimilarity getDissimilarity()
    {
        return dissimilarity;
    }

    public List<AnnotatedText> getTexts()
    {
        return asList(text1, text2);
    }

    @Override
    public double calculateObservedDisagreement()
    {
        return getObservedDisorder(text1, text2, dissimilarity);
    }

    @Override
    public double calculateExpectedDisagreement()
    {
        return calculateExpectedDisagreement(sampler, alpha, precision);
    }

    static double calculateExpectedDisagreement(IDisorderSampler aDisorderSampler, double aAlpha,
            double aPrecision)
    {
        var n = 30l;

        var m = new Mean();
        var v = new StandardDeviation(true);

        var sn = new NormalDistribution(0, 1);

        while (m.getN() < n) {
            while (m.getN() < n) {
                var disorder = aDisorderSampler.sampleDisorder();
                m.increment(disorder);
                v.increment(disorder);
            }

            // re-estimate n
            double meanDisorder = m.getResult();

            double sdDisorder = v.getResult();
            double cv = sdDisorder / meanDisorder;
            n = round(pow(cv * sn.inverseCumulativeProbability(1 - (aAlpha / 2)) / aPrecision, 2));
        }

        return m.getResult();
    }

    public static double getObservedDisorder(AnnotatedText aText1, AnnotatedText aText2,
            IDissimilarity aDissiilarity)
    {
        var minDisorder = Double.MAX_VALUE;

        for (var al : mergeAnnotatedTextsWithSegmentation(aText1, aText2)) {
            var disorder = al.getDisorder(aDissiilarity);
            if (disorder < minDisorder) {
                minDisorder = disorder;
            }
        }

        return minDisorder;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private TextAligningAnnotationStudy study;
        private List<AnnotatedText> texts;
        private IDissimilarity dissimilarity = new NominalFeatureTextDissimilarity();
        private IDisorderSampler sampler;
        private IDisorderSamplerFactory samplerFactory;
        private double precision = 0.01;
        private double alpha = 0.05;

        private Builder()
        {
        }

        public Builder withStudy(TextAligningAnnotationStudy aStudy)
        {
            study = aStudy;
            texts = null;
            return this;
        }

        public Builder withTexts(AnnotatedText... aTexts)
        {
            texts = asList(aTexts);
            study = null;
            return this;
        }

        public Builder withDissimilarity(IDissimilarity aDissimilarity)
        {
            dissimilarity = aDissimilarity;
            return this;
        }

        public Builder withDisorderSampler(IDisorderSampler aSampler)
        {
            sampler = aSampler;
            samplerFactory = null;
            return this;
        }

        public Builder withDisorderSampler(IDisorderSamplerFactory aSampler)
        {
            samplerFactory = aSampler;
            sampler = null;
            return this;
        }

        public Builder withPrecision(double aPrecision)
        {
            precision = aPrecision;
            return this;
        }

        public Builder withAlpha(double aAlpha)
        {
            alpha = aAlpha;
            return this;
        }

        public TextGammaAgreement build()
        {
            return new TextGammaAgreement(this);
        }
    }

    @SuppressWarnings("unused")
    private static void checkConstants()
    {
        // assure that the characters denoting beginning and end of units and gaps differ from each
        // other
        if (OPEN_UNIT == CLOSE_UNIT) {
            throw new IllegalArgumentException(
                    "Characters denoting the start and the end of a unit must differ.");
        }
        if (OPEN_UNIT == GAP) {
            throw new IllegalArgumentException(
                    "Characters denoting the start of a unit and a gap must differ.");
        }
        if (CLOSE_UNIT == GAP) {
            throw new IllegalArgumentException(
                    "Characters denoting the end of a unit and a gap must differ.");
        }
    }
}
