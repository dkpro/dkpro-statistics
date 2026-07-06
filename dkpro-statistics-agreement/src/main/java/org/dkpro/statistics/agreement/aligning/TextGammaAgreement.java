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
import java.util.Optional;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.dkpro.statistics.agreement.DisagreementMeasure;
import org.dkpro.statistics.agreement.InsufficientDataException;
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
    private final AnnotatedText baseText;
    private final IDissimilarity dissimilarity;
    private final IDisorderSampler sampler;
    private final double precision;
    private final double alpha;
    private final RandomGenerator randomGenerator;

    private TextGammaAgreement(Builder builder)
    {
        dissimilarity = builder.dissimilarity;
        precision = builder.precision;
        alpha = builder.alpha;

        // Resolve the source of randomness before the sampler is created below, so a sampler
        // created through the factory can pick it up via getRandomGenerator(). When no generator (or
        // seed) is configured we fall back to a fresh, time-seeded generator - preserving the
        // previous, non-reproducible behaviour.
        randomGenerator = builder.randomGenerator != null ? builder.randomGenerator
                : new Well19937c();

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

        // Base text used by the chance model. Upstream TextGamma always had an explicit gold "orig"
        // text - a text AND a reference segmentation - from which random annotators were derived. The
        // DKPro API has no such parameter and, in general, there is no reference: in particular we
        // must not assume the raters share a segmentation. So we only use a base text when the caller
        // explicitly supplies one (asserting a genuine external reference). Otherwise there is
        // deliberately no base and the sampler stays symmetric in the raters (see
        // SimpleDisorderSampler#sampleDisorder).
        baseText = builder.baseText;

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

    /**
     * @return the source of randomness used by the chance model. Samplers should draw all their
     *         randomness from this generator so that a seed configured via {@link Builder#withSeed}
     *         (or {@link Builder#withRandomGenerator}) makes the measurement reproducible.
     */
    public RandomGenerator getRandomGenerator()
    {
        return randomGenerator;
    }

    public List<AnnotatedText> getTexts()
    {
        return asList(text1, text2);
    }

    /**
     * @return the base text used by the chance model if one was explicitly supplied via
     *         {@link Builder#withBaseText}. Empty otherwise, in which case the sampler stays
     *         symmetric over both raters rather than assuming a reference (see SimpleDisorderSampler).
     */
    public Optional<AnnotatedText> getBaseText()
    {
        return Optional.ofNullable(baseText);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InsufficientDataException
     *             if the expected disorder is zero. This happens when the chance model cannot
     *             introduce any disorder (e.g. the annotations carry no categories and the sampler
     *             uses zero text/segmentation change rates), leaving no chance baseline to correct
     *             against - the agreement would otherwise be an undefined division by zero.
     */
    @Override
    public double calculateAgreement()
    {
        var observedDisorder = calculateObservedDisagreement();
        var expectedDisorder = calculateExpectedDisagreement();

        LOG.trace("Disorder -- observed: {} -- expected: {}", observedDisorder, expectedDisorder);

        if (expectedDisorder == 0.0) {
            throw new InsufficientDataException(
                    "Expected disorder is zero: the chance model could not introduce any disorder, "
                            + "so there is no chance baseline to correct against. This typically "
                            + "means the annotations carry no categories and the disorder sampler "
                            + "uses zero text and segmentation change rates.");
        }

        return 1.0 - (observedDisorder / expectedDisorder);
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
        private AnnotatedText baseText;
        private IDissimilarity dissimilarity = new NominalFeatureTextDissimilarity();
        private IDisorderSampler sampler;
        private IDisorderSamplerFactory samplerFactory;
        private double precision = 0.01;
        private double alpha = 0.05;
        private RandomGenerator randomGenerator;

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

        /**
         * Supplies an explicit base ("orig") text for the chance model, mirroring the original
         * TextGamma tool. When set, random annotators are always derived from this text. When not
         * set, no base text is used: the sampler instead stays symmetric over both raters (see
         * {@link TextGammaAgreement#getBaseText()}).
         */
        public Builder withBaseText(AnnotatedText aBaseText)
        {
            baseText = aBaseText;
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

        /**
         * Seeds the chance model so that the measurement is reproducible: with a fixed seed the same
         * study yields the same agreement value on every run. Without a seed (or an explicit
         * generator) the chance model uses a fresh, time-seeded generator and results vary slightly
         * from run to run within the configured precision. Convenience shortcut for
         * {@link #withRandomGenerator} with a {@link Well19937c} seeded with {@code aSeed}.
         */
        public Builder withSeed(long aSeed)
        {
            randomGenerator = new Well19937c(aSeed);
            return this;
        }

        /**
         * Supplies the source of randomness for the chance model. See {@link #withSeed} for the
         * effect on reproducibility.
         */
        public Builder withRandomGenerator(RandomGenerator aRandomGenerator)
        {
            randomGenerator = aRandomGenerator;
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
