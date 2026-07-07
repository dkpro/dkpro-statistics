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
 */
package org.dkpro.statistics.agreement.aligning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.offset;

import java.util.List;
import java.util.Map;

import org.dkpro.statistics.agreement.InsufficientDataException;
import org.dkpro.statistics.agreement.aligning.data.AnnotationSet;
import org.dkpro.statistics.agreement.aligning.data.Rater;
import org.dkpro.statistics.agreement.aligning.disorder.IDisorderSampler;
import org.junit.jupiter.api.Test;

class GammaAgreementTest
{
    private static final Rater ANNOTATOR_A = new Rater("A", 0);
    private static final Rater ANNOTATOR_B = new Rater("B", 1);

    /**
     * Sampler that replays a scripted sequence of disorders and falls back to a fixed value once the
     * sequence is exhausted, counting the total number of draws.
     */
    private static final class CountingSampler
        implements IDisorderSampler
    {
        private final double[] scripted;
        private final double fallback;
        private int count = 0;

        CountingSampler(double fallback, double... scripted)
        {
            this.scripted = scripted;
            this.fallback = fallback;
        }

        @Override
        public Double sampleDisorder()
        {
            double value = count < scripted.length ? scripted[count] : fallback;
            count++;
            return value;
        }

        int count()
        {
            return count;
        }
    }

    private static AnnotationSet perfectContinuum()
    {
        // Two raters with identical units -> best-alignment disorder is exactly 0.
        return new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 0, 4, "a"), //
                unit(ANNOTATOR_A, 5, 9, "b"), //
                unit(ANNOTATOR_B, 0, 4, "a"), //
                unit(ANNOTATOR_B, 5, 9, "b")));
    }

    private static AnnotationSet disagreeingContinuum()
    {
        // Two raters, one shifted unit each -> positive observed disorder.
        return new AnnotationSet(List.of( //
                unit(ANNOTATOR_A, 0, 4, "a"), //
                unit(ANNOTATOR_B, 2, 6, "a")));
    }

    private static AlignableAnnotationUnit unit(Rater aRater, long aBegin, long aEnd, String aCat)
    {
        return new AlignableAnnotationUnit(aRater, null, aBegin, aEnd, Map.of("category", aCat));
    }

    @Test
    void missingAnnotationSetIsRejected()
    {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GammaAgreement.builder().build());
    }

    @Test
    void singleRaterIsRejected()
    {
        var set = new AnnotationSet(List.of(unit(ANNOTATOR_A, 0, 4, "a")));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> GammaAgreement.builder().withAnnotationSet(set).build());
    }

    @Test
    void invalidPrecisionLevelIsRejected()
    {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> GammaAgreement
                .builder().withAnnotationSet(perfectContinuum()).withPrecisionLevel(1.5).build());
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> GammaAgreement
                .builder().withAnnotationSet(perfectContinuum()).withPrecisionLevel(0.0).build());
    }

    @Test
    void invalidNumberOfSamplesIsRejected()
    {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> GammaAgreement
                .builder().withAnnotationSet(perfectContinuum()).withNumberOfSamples(0).build());
    }

    @Test
    void expectedDisagreementWithoutPrecisionIsMeanOfFirstN()
    {
        var sampler = new CountingSampler(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(disagreeingContinuum()) //
                .withDisorderSampler(sampler) //
                .withNumberOfSamples(5) //
                .withPrecisionLevel(null) //
                .build();

        // Mean of the first 5 scripted values (1+2+3+4+5)/5 = 3.0, exactly 5 draws, no top-up.
        assertThat(gamma.calculateExpectedDisagreement()).isCloseTo(3.0, offset(1e-12));
        assertThat(sampler.count()).isEqualTo(5);
        assertThat(gamma.getExpectedDisagreementSampleCount()).isEqualTo(5);
    }

    @Test
    void expectedDisagreementWithZeroVarianceDoesNotTopUp()
    {
        var sampler = new CountingSampler(2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0);
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(disagreeingContinuum()) //
                .withDisorderSampler(sampler) //
                .withNumberOfSamples(10) //
                .withPrecisionLevel(0.05) //
                .build();

        // cv == 0 -> required == 0 -> no top-up; exactly 10 draws, mean == 2.0.
        assertThat(gamma.calculateExpectedDisagreement()).isCloseTo(2.0, offset(1e-12));
        assertThat(sampler.count()).isEqualTo(10);
        assertThat(gamma.getExpectedDisagreementSampleCount()).isEqualTo(10);
    }

    @Test
    void expectedDisagreementWithHighVarianceTopsUpExactlyOnce()
    {
        // First batch [1,3,1,3]: mean 2.0, population std 1.0, cv 0.5.
        // required = ceil((0.5 * 1.96 / 0.05)^2) = ceil(384.16) = 385.
        // top-up = 385 - 4 = 381, all equal to the fallback 2.0 so the overall mean stays 2.0.
        var sampler = new CountingSampler(2.0, 1.0, 3.0, 1.0, 3.0);
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(disagreeingContinuum()) //
                .withDisorderSampler(sampler) //
                .withNumberOfSamples(4) //
                .withPrecisionLevel(0.05) //
                .build();

        assertThat(gamma.calculateExpectedDisagreement()).isCloseTo(2.0, offset(1e-12));
        assertThat(sampler.count()).isEqualTo(385);
        assertThat(gamma.getExpectedDisagreementSampleCount()).isEqualTo(385);
    }

    @Test
    void observedZeroYieldsGammaOneWithoutSampling()
    {
        // Sampler that fails if invoked - proves the obs == 0 short-circuit never draws samples.
        IDisorderSampler failing = () -> {
            throw new AssertionError("sampler must not be called when observed disorder is zero");
        };
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(perfectContinuum()) //
                .withDisorderSampler(failing) //
                .build();

        assertThat(gamma.calculateObservedDisagreement()).isCloseTo(0.0, offset(1e-12));
        assertThat(gamma.calculateAgreement()).isEqualTo(1.0);
    }

    @Test
    void zeroExpectedWithPositiveObservedThrows()
    {
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(disagreeingContinuum()) //
                .withDisorderSampler(() -> 0.0) //
                .withNumberOfSamples(5) //
                .build();

        assertThat(gamma.calculateObservedDisagreement()).isGreaterThan(0.0);
        assertThatExceptionOfType(InsufficientDataException.class)
                .isThrownBy(gamma::calculateAgreement);
    }

    @Test
    void missingSamplerThrowsLazilyOnExpectedDisagreement()
    {
        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(disagreeingContinuum()) //
                .build();

        // Observed disorder works without a sampler ...
        assertThat(gamma.calculateObservedDisagreement()).isGreaterThan(0.0);
        // ... but the expected disorder requires one.
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(gamma::calculateExpectedDisagreement);
    }

    @Test
    void agreementIsOneMinusObservedOverExpected()
    {
        var set = disagreeingContinuum();
        var observed = GammaAgreement.builder().withAnnotationSet(set).build()
                .calculateObservedDisagreement();

        var gamma = GammaAgreement.builder() //
                .withAnnotationSet(set) //
                .withDisorderSampler(() -> 1.0) //
                .withNumberOfSamples(10) //
                .build();

        // expected == 1.0 -> gamma == 1 - observed / 1.0.
        assertThat(gamma.calculateAgreement()).isCloseTo(1.0 - observed, offset(1e-9));
    }
}
