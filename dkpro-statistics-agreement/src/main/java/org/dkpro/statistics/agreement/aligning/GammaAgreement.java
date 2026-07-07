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
 * Ported from the pygamma-agreement project:
 * https://github.com/bootphon/pygamma-agreement - version 0.5.9, commit 44587ef.
 * Original file: pygamma_agreement/continuum.py (Continuum.compute_gamma and GammaResults).
 * Original authors: Rachid Riad, Hadrien Titeux, Léopold Favre.
 *
 * The original code is distributed under the MIT license, reproduced verbatim below:
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 CoML
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Paper: Mathet, Widlöcher, and Métivier (2015), "The Unified and Holistic Method
 * Gamma for Inter-Annotator Agreement Measure and Alignment"
 * (https://aclanthology.org/J15-3003.pdf).
 */
package org.dkpro.statistics.agreement.aligning;

import static java.lang.Math.ceil;
import static java.lang.Math.pow;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.dkpro.statistics.agreement.DisagreementMeasure;
import org.dkpro.statistics.agreement.InsufficientDataException;
import org.dkpro.statistics.agreement.aligning.alignment.BestAlignmentSolver;
import org.dkpro.statistics.agreement.aligning.alignment.BestAlignmentSolver.BestAlignment;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.disorder.IDisorderSampler;
import org.dkpro.statistics.agreement.aligning.disorder.IGammaDisorderSamplerFactory;
import org.dkpro.statistics.agreement.aligning.dissimilarity.AbstractDissimilarity;
import org.dkpro.statistics.agreement.aligning.dissimilarity.CombinedCategoricalDissimilarity;
import org.dkpro.statistics.agreement.aligning.dissimilarity.IDissimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Mathet et al. (2015) gamma (&gamma;) inter-annotator agreement measure for
 * a continuum of timed intervals, ported from pygamma-agreement's
 * {@code Continuum.compute_gamma}/{@code GammaResults}.
 * <p>
 * The <b>observed disagreement</b> is the disorder of the best (minimal-disorder) alignment of the
 * continuum, computed exactly via {@link BestAlignmentSolver}. The <b>expected disagreement</b> is
 * the mean best-alignment disorder over a batch of random continua drawn from an injected
 * {@link IDisorderSampler}. Gamma is then {@code 1 - observed / expected}.
 * <p>
 * References:
 * <ul>
 * <li>Mathet, Widlöcher, Métivier (2015): <i>The Unified and Holistic Method Gamma (&gamma;) for
 * Inter-Annotator Agreement Measure and Alignment.</i> Computational Linguistics 41(3),
 * <a href="https://aclanthology.org/J15-3003.pdf">https://aclanthology.org/J15-3003.pdf</a>.</li>
 * <li>Titeux &amp; Riccardi (2021): <i>pygamma-agreement</i>, <a href=
 * "https://github.com/bootphon/pygamma-agreement">https://github.com/bootphon/pygamma-agreement</a>.</li>
 * </ul>
 * <p>
 * Corresponds to the Python {@code Continuum.compute_gamma} + {@code GammaResults.gamma}.
 * Deviations from the original:
 * <ul>
 * <li>Units carry {@code long} offsets and everything is computed in {@code double} instead of
 * pygamma's {@code float32} (expect ~1e-5 relative divergence against reference values).</li>
 * <li>The expected-disorder procedure is a faithful port of pygamma's <b>one-shot top-up</b>: draw
 * {@code numberOfSamples} samples, optionally re-estimate the required count once from their
 * coefficient of variation and draw a single top-up batch. This deliberately differs from
 * {@link TextGammaAgreement#calculateExpectedDisagreement}, which loops until the estimate
 * stabilizes.</li>
 * <li>Gamma is <b>not clamped</b> and may be negative, exactly as in pygamma.</li>
 * </ul>
 * <p>
 * <b>Validation scope.</b> This port is cross-validated against pygamma-agreement 0.5.9 only for
 * the default {@code deltaEmpty == 1.0} (the value used by both the pygamma library and its CLI).
 * For that value the observed disorder, candidate pruning, and best-alignment disorder match the
 * reference to within the {@code float32}-vs-{@code double} floor (~1e-5 relative). It is
 * <b>not</b> validated for other {@code deltaEmpty} values, and it will deliberately <i>not</i>
 * reproduce pygamma 0.5.9's output there, because pygamma 0.5.9 has a bug for non-default
 * {@code deltaEmpty}: {@code AbstractDissimilarity.__init__} compiles and caches the {@code njit}
 * {@code d_mat} closure immediately, and {@code CombinedCategoricalDissimilarity} then reassigns
 * {@code cat_dissim.delta_empty} <i>without</i> recompiling that closure. As a result pygamma's
 * best-alignment/pruning path (which uses the cached {@code d_mat}) scores categorical disagreement
 * with the stale {@code delta_empty = 1.0}, while its public {@code d()} method uses the requested
 * value. This makes pygamma internally inconsistent for {@code deltaEmpty != 1.0}. This
 * implementation applies {@code deltaEmpty} consistently to both the positional and categorical
 * terms (so it is arguably more correct), and consequently diverges from pygamma 0.5.9 for those
 * values.
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public class GammaAgreement
    extends DisagreementMeasure
    implements IAligningAgreementMeasure
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /** Confidence factor for a 95% confidence interval, hardcoded in pygamma. */
    private static final double CONFIDENCE_95 = 1.96;

    private final AnnotationSet annotationSet;
    private final IDissimilarity dissimilarity;
    private final double deltaEmpty;
    private final IDisorderSampler sampler;
    private final int numberOfSamples;
    private final Double precisionLevel;
    private final RandomGenerator randomGenerator;

    private BestAlignment bestAlignment;
    private int expectedDisagreementSampleCount = -1;

    private GammaAgreement(Builder builder)
    {
        if (builder.annotationSet == null) {
            throw new IllegalArgumentException("An annotation set (continuum) must be given");
        }

        if (builder.annotationSet.getRaterCount() < 2) {
            throw new IllegalArgumentException(
                    "The continuum must contain at least two raters, but contains "
                            + builder.annotationSet.getRaterCount() + ".");
        }

        if (builder.numberOfSamples < 1) {
            throw new IllegalArgumentException("The number of samples must be positive, but was "
                    + builder.numberOfSamples + ".");
        }

        if (builder.precisionLevel != null
                && !(builder.precisionLevel > 0.0 && builder.precisionLevel < 1.0)) {
            throw new IllegalArgumentException(
                    "The precision level must be null or in the open interval (0, 1), but was "
                            + builder.precisionLevel + ".");
        }

        annotationSet = builder.annotationSet;
        dissimilarity = builder.dissimilarity;
        deltaEmpty = builder.deltaEmpty;
        numberOfSamples = builder.numberOfSamples;
        precisionLevel = builder.precisionLevel;

        // Resolve the source of randomness before the sampler is created below, so a sampler
        // created
        // through the factory can pick it up via getRandomGenerator(). When no generator (or seed)
        // is
        // configured we fall back to a fresh, time-seeded generator (mirrors TextGammaAgreement).
        randomGenerator = builder.randomGenerator != null ? builder.randomGenerator
                : new Well19937c();

        // The sampler is optional: it is only required by calculateExpectedDisagreement() /
        // calculateAgreement(). A caller interested solely in the observed disorder need not supply
        // one. When neither a sampler nor a factory is given, the sampler stays null and the
        // expected-disorder path throws lazily (see requireSampler()).
        if (builder.sampler != null) {
            sampler = builder.sampler;
        }
        else if (builder.samplerFactory != null) {
            sampler = builder.samplerFactory.create(this);
        }
        else {
            sampler = null;
        }
    }

    public AnnotationSet getAnnotationSet()
    {
        return annotationSet;
    }

    public IDissimilarity getDissimilarity()
    {
        return dissimilarity;
    }

    public double getDeltaEmpty()
    {
        return deltaEmpty;
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

    /**
     * @return the cached best alignment computed for the observed disorder. Mirrors
     *         {@code GammaResults.best_alignment}. Computed lazily on first access.
     */
    public BestAlignment getBestAlignment()
    {
        if (bestAlignment == null) {
            bestAlignment = BestAlignmentSolver.solve(annotationSet, dissimilarity, deltaEmpty);
        }

        return bestAlignment;
    }

    /**
     * @return the number of samples that the most recent {@link #calculateExpectedDisagreement()}
     *         call actually drew (i.e. {@code numberOfSamples} plus any one-shot top-up), or
     *         {@code -1} if the expected disagreement has not been computed yet.
     */
    public int getExpectedDisagreementSampleCount()
    {
        return expectedDisagreementSampleCount;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InsufficientDataException
     *             if the expected disorder is zero while the observed disorder is positive: there
     *             is no chance baseline to correct against, so gamma would be an undefined division
     *             by zero. Mirrors the spirit of {@link TextGammaAgreement}'s zero-expected
     *             handling.
     */
    @Override
    public double calculateAgreement()
    {
        var observedDisorder = calculateObservedDisagreement();

        // pygamma GammaResults.gamma special case: an observed disorder of exactly zero yields
        // gamma
        // == 1 without dividing by the expected disorder (and without even drawing any samples).
        if (observedDisorder == 0.0) {
            return 1.0;
        }

        var expectedDisorder = calculateExpectedDisagreement();

        LOG.trace("Disorder -- observed: {} -- expected: {}", observedDisorder, expectedDisorder);

        if (expectedDisorder == 0.0) {
            throw new InsufficientDataException(
                    "Expected disorder is zero while observed disorder is positive: the chance model "
                            + "could not introduce any disorder, so there is no chance baseline to "
                            + "correct against.");
        }

        // Deliberately NOT clamped: gamma may be negative when the observed disorder exceeds the
        // expected disorder. Mirrors pygamma GammaResults.gamma.
        return 1.0 - (observedDisorder / expectedDisorder);
    }

    @Override
    public double calculateObservedDisagreement()
    {
        return getBestAlignment().disorder();
    }

    @Override
    public double calculateExpectedDisagreement()
    {
        var s = requireSampler();

        // Exact port of pygamma continuum.py:compute_gamma. Draw the initial batch.
        var disorders = new ArrayList<Double>(numberOfSamples);
        for (int i = 0; i < numberOfSamples; i++) {
            disorders.add(s.sampleDisorder());
        }

        // Optional one-shot re-estimation (single top-up batch, NOT a re-checking loop). When
        // precisionLevel is null (the pygamma library default) exactly numberOfSamples samples are
        // used, with no variation test.
        if (precisionLevel != null) {
            var mean = new Mean();
            // ddof=0 population standard deviation, matching numpy's np.std default. Commons-math's
            // StandardDeviation defaults to the bias-corrected (sample, ddof=1) variant, so we must
            // explicitly disable bias correction here.
            var populationStd = new StandardDeviation(false);
            for (var disorder : disorders) {
                mean.increment(disorder);
                populationStd.increment(disorder);
            }

            double variationCoeff = populationStd.getResult() / mean.getResult();
            long requiredSamples = (long) ceil(
                    pow(variationCoeff * CONFIDENCE_95 / precisionLevel, 2));

            if (requiredSamples > numberOfSamples) {
                LOG.info("Computing second batch of {} samples because variation was too high.",
                        requiredSamples - numberOfSamples);
                for (long i = numberOfSamples; i < requiredSamples; i++) {
                    disorders.add(s.sampleDisorder());
                }
            }
        }

        expectedDisagreementSampleCount = disorders.size();

        var mean = new Mean();
        for (var disorder : disorders) {
            mean.increment(disorder);
        }

        return mean.getResult();
    }

    private IDisorderSampler requireSampler()
    {
        if (sampler == null) {
            throw new IllegalStateException(
                    "No disorder sampler was configured. A sampler (or sampler factory) is required "
                            + "to compute the expected disagreement / agreement, but not for the "
                            + "observed disagreement.");
        }

        return sampler;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private AnnotationSet annotationSet;
        private IDissimilarity dissimilarity = new CombinedCategoricalDissimilarity();
        private double deltaEmpty = 1.0;
        private IDisorderSampler sampler;
        private IGammaDisorderSamplerFactory samplerFactory;
        private int numberOfSamples = 30;
        private Double precisionLevel = null;
        private RandomGenerator randomGenerator;

        private Builder()
        {
        }

        /**
         * Sets the continuum whose agreement is measured.
         */
        public Builder withAnnotationSet(AnnotationSet aAnnotationSet)
        {
            annotationSet = aAnnotationSet;
            return this;
        }

        /**
         * Sets the dissimilarity together with an explicit empty-unit cost.
         */
        public Builder withDissimilarity(IDissimilarity aDissimilarity, double aDeltaEmpty)
        {
            dissimilarity = aDissimilarity;
            deltaEmpty = aDeltaEmpty;
            return this;
        }

        /**
         * Sets the dissimilarity and derives the empty-unit cost from it (via
         * {@link AbstractDissimilarity#getDeltaEmpty()}).
         */
        public Builder withDissimilarity(AbstractDissimilarity aDissimilarity)
        {
            dissimilarity = aDissimilarity;
            deltaEmpty = aDissimilarity.getDeltaEmpty();
            return this;
        }

        /**
         * Sets the chance model used to estimate the expected disorder. A sampler (or a
         * {@link IGammaDisorderSamplerFactory factory}) is <b>required</b>: there is no default,
         * and computing gamma without one throws.
         *
         * @see org.dkpro.statistics.agreement.aligning.disorder.StatisticalContinuumDisorderSampler
         *      the statistical resampling sampler mirroring pygamma's default
         */
        public Builder withDisorderSampler(IDisorderSampler aSampler)
        {
            sampler = aSampler;
            samplerFactory = null;
            return this;
        }

        public Builder withDisorderSampler(IGammaDisorderSamplerFactory aSamplerFactory)
        {
            samplerFactory = aSamplerFactory;
            sampler = null;
            return this;
        }

        /**
         * Number of random continua sampled to estimate the expected disorder. Defaults to 30
         * (pygamma's {@code n_samples}).
         */
        public Builder withNumberOfSamples(int aNumberOfSamples)
        {
            numberOfSamples = aNumberOfSamples;
            return this;
        }

        /**
         * Target error percentage of the gamma estimation, in the open interval (0, 1), or
         * {@code null} for none. When {@code null} (the pygamma library default) exactly
         * {@link #withNumberOfSamples(int) numberOfSamples} samples are drawn with no top-up. When
         * set, a single top-up batch may be drawn once (see
         * {@link GammaAgreement#calculateExpectedDisagreement()}).
         */
        public Builder withPrecisionLevel(Double aPrecisionLevel)
        {
            precisionLevel = aPrecisionLevel;
            return this;
        }

        /**
         * Seeds the chance model so that the measurement is reproducible. Convenience shortcut for
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

        public GammaAgreement build()
        {
            return new GammaAgreement(this);
        }
    }
}
