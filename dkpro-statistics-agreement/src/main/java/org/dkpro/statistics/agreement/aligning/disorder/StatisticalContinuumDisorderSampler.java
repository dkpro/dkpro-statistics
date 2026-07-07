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
 * Original file: pygamma_agreement/sampler.py (class StatisticalContinuumSampler),
 * fused with the sample-disorder job wiring of pygamma_agreement/continuum.py
 * (Continuum.compute_gamma, which draws each sample via a sampler and takes its
 * best-alignment disorder).
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
package org.dkpro.statistics.agreement.aligning.disorder;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.random.RandomGenerator;
import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;
import org.dkpro.statistics.agreement.aligning.GammaAgreement;
import org.dkpro.statistics.agreement.aligning.alignment.BestAlignmentSolver;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.dissimilarity.IDissimilarity;

/**
 * Statistical continuum sampler for the gamma chance model, a port of pygamma-agreement's
 * {@code StatisticalContinuumSampler} fused with the compute-sample-disorder job of
 * {@code Continuum.compute_gamma}. Each call to {@link #sampleDisorder()} draws a fresh random
 * continuum from the statistical characteristics of a reference continuum and returns its
 * best-alignment disorder.
 * <p>
 * The reference statistics (all derived from the {@link GammaAgreement}'s continuum, using
 * <b>population</b> mean/standard deviation to match numpy's {@code np.mean}/{@code np.std} with
 * {@code ddof=0}) are:
 * <ul>
 * <li><b>gap</b> - the list is seeded with a single {@code 0} entry (a pygamma quirk that always
 * lives in the list); for each rater the gap {@code start - previous_end} between consecutive units
 * (may be negative, reproducing overlaps) is added; additionally each rater whose first unit starts
 * beyond {@code 0} contributes that first start. Mirrors {@code _set_gap_information} exactly.</li>
 * <li><b>duration</b> - over all units ({@code end - begin}).</li>
 * <li><b>units per rater</b> - over the per-rater unit counts.</li>
 * <li><b>category weights</b> - the relative frequency of each category value over all units, with
 * categories enumerated in sorted order.</li>
 * </ul>
 * <p>
 * <b>Category handling (Java adaptation of pygamma's single-annotation model).</b> pygamma stores a
 * single scalar "annotation" (category) per unit. Units in this project instead carry a feature map,
 * so the "category" of a unit is taken to be the value of exactly one feature. If the reference units
 * collectively carry exactly one distinct feature name it is detected automatically; otherwise the
 * explicit feature-name constructor must be used (an {@link IllegalArgumentException} is thrown). Every
 * sampled unit carries exactly that one feature.
 * <p>
 * <b>Sampling one continuum (long adaptation, per PLAN &sect;3.6).</b> For each rater of the reference
 * (the same {@link Rater} objects, in sorted order):
 * <ol>
 * <li>{@code nbUnits = abs((int) normal(avgUnits, stdUnits))} - the {@code (int)} cast truncates toward
 * zero exactly like Python's {@code int()}. It is forced to {@code max(1, nbUnits)} only while the
 * sampled continuum is still empty (pygamma's {@code if not new_continnum}: the first rater is always
 * forced, later raters only if everything drawn so far was empty).</li>
 * <li>Per unit: {@code gap = normal(avgGap, stdGap)}; {@code startD = lastPointD + gap};
 * {@code durD = abs(normal(avgDur, stdDur))} redrawn while {@code round(startD + durD) - round(startD)
 * < 1} (the integer analogue of pyannote's {@code SEGMENT_PRECISION} redraw); the unit gets
 * {@code begin = round(startD)}, {@code end = round(startD + durD)}; the category is drawn by weighted
 * choice; {@code lastPointD = startD + durD} accumulates in {@code double} so only per-unit rounding is
 * applied and positions do not drift.</li>
 * </ol>
 * <p>
 * All randomness is drawn from {@link GammaAgreement#getRandomGenerator()} so that a seed configured on
 * the measure makes the sampling reproducible.
 * <p>
 * Deviations from the original:
 * <ul>
 * <li>Units carry {@code long} offsets; positions accumulate in {@code double} and are rounded per unit
 * (pygamma keeps {@code float} coordinates). At character-offset scale this rounding perturbation is
 * far below the 5%/1% sampling precision of the chance model.</li>
 * <li>The duration redraw threshold is an integer {@code 1} instead of pyannote's
 * {@code SEGMENT_PRECISION} (~{@code 1e-6}), because rounded units of zero length are the degenerate
 * case here.</li>
 * <li>Normal draws use {@code mean + sd * rng.nextGaussian()} rather than commons-math's
 * {@code NormalDistribution}, which throws for {@code sd == 0}; numpy returns the mean in that case
 * (and {@code sd == 0} happens routinely, e.g. equal unit counts across raters).</li>
 * <li>The category is a feature value rather than pygamma's single scalar annotation (see above).</li>
 * </ul>
 *
 * @see <a href="https://github.com/bootphon/pygamma-agreement">pygamma-agreement</a>
 * @see <a href="https://aclanthology.org/J15-3003.pdf">Mathet et al. 2015</a>
 */
public class StatisticalContinuumDisorderSampler
    implements IDisorderSampler
{
    private final AnnotationSet referenceContinuum;
    private final IDissimilarity dissimilarity;
    private final double deltaEmpty;
    private final RandomGenerator rng;
    private final String featureName;

    // Raters of the reference, in sorted (canonical) order. Sampled continua reuse these objects.
    private final List<Rater> raters;

    private final double avgNbUnitsPerRater;
    private final double stdNbUnitsPerRater;
    private final double avgGap;
    private final double stdGap;
    private final double avgUnitDuration;
    private final double stdUnitDuration;

    // Category values in sorted order, with a parallel array of relative frequencies (weights).
    private final String[] categories;
    private final double[] categoryWeights;

    /**
     * Creates a sampler for the given measure, auto-detecting the category feature name.
     *
     * @param aMeasure
     *            the gamma measure whose reference continuum, dissimilarity, delta-empty and source of
     *            randomness are used.
     * @throws IllegalArgumentException
     *             if the reference units do not collectively carry exactly one distinct feature name
     *             (use {@link #StatisticalContinuumDisorderSampler(GammaAgreement, String)} instead).
     */
    public StatisticalContinuumDisorderSampler(GammaAgreement aMeasure)
    {
        this(aMeasure, detectFeatureName(aMeasure.getAnnotationSet()));
    }

    /**
     * Creates a sampler for the given measure using the named feature as the category.
     *
     * @param aMeasure
     *            the gamma measure whose reference continuum, dissimilarity, delta-empty and source of
     *            randomness are used.
     * @param aFeatureName
     *            the feature whose value is treated as each unit's category.
     */
    public StatisticalContinuumDisorderSampler(GammaAgreement aMeasure, String aFeatureName)
    {
        if (aFeatureName == null) {
            throw new IllegalArgumentException("The category feature name must not be null.");
        }

        referenceContinuum = aMeasure.getAnnotationSet();
        dissimilarity = aMeasure.getDissimilarity();
        deltaEmpty = aMeasure.getDeltaEmpty();
        rng = aMeasure.getRandomGenerator();
        featureName = aFeatureName;

        raters = new ArrayList<>(referenceContinuum.getRaters());

        // --- number of units per rater ---
        var nbUnits = new ArrayList<Double>();
        for (var rater : raters) {
            nbUnits.add((double) referenceContinuum.getUnitsWithRater(rater).size());
        }
        avgNbUnitsPerRater = populationMean(nbUnits);
        stdNbUnitsPerRater = populationStd(nbUnits, avgNbUnitsPerRater);

        // --- gaps (mirrors _set_gap_information exactly, including the always-present 0 seed) ---
        var gaps = new ArrayList<Double>();
        gaps.add(0.0);
        for (var rater : raters) {
            var units = referenceContinuum.getUnitsWithRater(rater);
            for (int i = 1; i < units.size(); i++) {
                gaps.add((double) (units.get(i).getBegin() - units.get(i - 1).getEnd()));
            }
        }
        for (var rater : raters) {
            var units = referenceContinuum.getUnitsWithRater(rater);
            if (!units.isEmpty() && units.get(0).getBegin() > 0) {
                gaps.add((double) units.get(0).getBegin());
            }
        }
        avgGap = populationMean(gaps);
        stdGap = populationStd(gaps, avgGap);

        // --- durations (over all units) ---
        var durations = new ArrayList<Double>();
        for (var unit : referenceContinuum.getUnits()) {
            durations.add((double) (unit.getEnd() - unit.getBegin()));
        }
        avgUnitDuration = populationMean(durations);
        stdUnitDuration = populationStd(durations, avgUnitDuration);

        // --- category weights (sorted order) ---
        // A unit that does not carry the category feature yields a null value, representing an
        // unlabelled span. The measure supports these (categorical dissimilarity treats two
        // unlabelled units as identical), so null is a legitimate category here. A natural-ordered
        // TreeMap cannot hold a null key, hence the nulls-first comparator: it keeps the enumeration
        // order deterministic (which matters because weightedCategory() walks categories[] consuming
        // the RNG) while letting "unlabelled" be its own weighted category.
        var counts = new TreeMap<String, Integer>(nullsFirst(naturalOrder()));
        int total = 0;
        for (var unit : referenceContinuum.getUnits()) {
            String category = unit.getFeatureValue(featureName);
            counts.merge(category, 1, Integer::sum);
            total++;
        }
        categories = counts.keySet().toArray(new String[0]);
        categoryWeights = new double[categories.length];
        for (int i = 0; i < categories.length; i++) {
            categoryWeights[i] = counts.get(categories[i]) / (double) total;
        }
    }

    private static String detectFeatureName(AnnotationSet aContinuum)
    {
        var names = aContinuum.getFeatureNames();
        if (names.length != 1) {
            throw new IllegalArgumentException(
                    "Could not auto-detect the category feature: the reference units carry "
                            + names.length + " distinct feature names " + List.of(names)
                            + ", expected exactly one. Use the constructor that takes an explicit "
                            + "feature name.");
        }
        return names[0];
    }

    @Override
    public Double sampleDisorder()
    {
        // A rater whose unit count is drawn as zero vanishes from the sampled AnnotationSet (raters
        // exist only through their units), and BestAlignmentSolver cannot align fewer than two
        // raters. pygamma does not hit this because its Continuum retains annotators without
        // segments. Deviation: we redraw instead. With small reference continua this skews the
        // sample distribution towards continua where every rater has units, but such references
        // are far below the sample sizes the chance model needs to be meaningful anyway.
        var sample = sampleContinuum();
        while (sample.getRaterCount() < 2) {
            sample = sampleContinuum();
        }

        return BestAlignmentSolver.solve(sample, dissimilarity, deltaEmpty).disorder();
    }

    /**
     * Draws one random continuum from the reference statistics. Package-private test seam.
     */
    AnnotationSet sampleContinuum()
    {
        var sampled = new ArrayList<AlignableAnnotationUnit>();
        for (var rater : raters) {
            double lastPointD = 0.0;

            int nbUnits = Math.abs((int) normal(avgNbUnitsPerRater, stdNbUnitsPerRater));
            // pygamma's "if not new_continnum": force at least one unit only while nothing has been
            // sampled yet (the first rater is always forced; later raters only if all prior raters
            // produced zero units).
            if (sampled.isEmpty()) {
                nbUnits = Math.max(1, nbUnits);
            }

            for (int k = 0; k < nbUnits; k++) {
                double gap = normal(avgGap, stdGap);
                double startD = lastPointD + gap;

                double durD = Math.abs(normal(avgUnitDuration, stdUnitDuration));
                // Redraw until the rounded segment spans at least one integer unit (integer analogue
                // of pyannote's SEGMENT_PRECISION check). Guarantees begin < end.
                while (Math.round(startD + durD) - Math.round(startD) < 1) {
                    durD = Math.abs(normal(avgUnitDuration, stdUnitDuration));
                }

                long begin = Math.round(startD);
                long end = Math.round(startD + durD);
                String category = weightedCategory();

                // A null category models an unlabelled span: build it with an empty feature map so the
                // sampled unit looks exactly like a real unlabelled unit (Map.of would NPE on a null
                // value anyway). 5-arg constructor with a null type: the 4-arg (Rater, long, long, Map)
                // overload silently drops the features map, so it must not be used here.
                var features = category != null ? Map.of(featureName, category)
                        : Map.<String, String> of();
                sampled.add(new AlignableAnnotationUnit(rater, (String) null, begin, end, features));

                lastPointD = startD + durD;
            }
        }

        return new AnnotationSet(sampled);
    }

    /**
     * Draws a normal value as {@code mean + sd * gaussian()}. Unlike commons-math's
     * {@code NormalDistribution} this returns the mean for {@code sd == 0} rather than throwing.
     */
    private double normal(double aMean, double aStd)
    {
        return aMean + aStd * rng.nextGaussian();
    }

    private String weightedCategory()
    {
        double r = rng.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < categories.length; i++) {
            cumulative += categoryWeights[i];
            if (r < cumulative) {
                return categories[i];
            }
        }
        return categories[categories.length - 1];
    }

    private static double populationMean(List<Double> aValues)
    {
        double sum = 0.0;
        for (var v : aValues) {
            sum += v;
        }
        return sum / aValues.size();
    }

    private static double populationStd(List<Double> aValues, double aMean)
    {
        double sum = 0.0;
        for (var v : aValues) {
            double d = v - aMean;
            sum += d * d;
        }
        return Math.sqrt(sum / aValues.size());
    }

    // --- accessors (mainly for testing) ---

    public String getFeatureName()
    {
        return featureName;
    }

    public double getAverageNumberOfUnitsPerRater()
    {
        return avgNbUnitsPerRater;
    }

    public double getStandardDeviationOfUnitsPerRater()
    {
        return stdNbUnitsPerRater;
    }

    public double getAverageGap()
    {
        return avgGap;
    }

    public double getStandardDeviationOfGap()
    {
        return stdGap;
    }

    public double getAverageUnitDuration()
    {
        return avgUnitDuration;
    }

    public double getStandardDeviationOfUnitDuration()
    {
        return stdUnitDuration;
    }

    public String[] getCategories()
    {
        return categories.clone();
    }

    public double[] getCategoryWeights()
    {
        return categoryWeights.clone();
    }
}
