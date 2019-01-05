/*
 * Copyright 2014
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dkpro.statistics.agreement.unitizing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.dkpro.statistics.agreement.ICategorySpecificAgreement;
import org.dkpro.statistics.agreement.coding.KrippendorffAlphaAgreement;

/**
 * Implementation of Krippendorff's (1995) alpha_U-measure for calculating a
 * chance-corrected inter-rater agreement for unitizing studies with multiple
 * raters. As a model for expected disagreement, alpha_U considers all
 * possible unitizations for the given continuum and raters. Thereby, alpha_U
 * becomes fully compatible with {@link KrippendorffAlphaAgreement}. Note
 * that units coded with the same categories by a single rater may not
 * overlap with each other.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: On the reliability of unitizing contiguous data.
 *   Sociological Methodology 25:47–76, 1995.</li>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   2nd edition, Thousand Oaks, CA: Sage Publications, 2004.</li></ul>
 * @author Christian M. Meyer
 * @author Christian Stab
 */
public class KrippendorffAlphaUnitizingAgreement extends UnitizingAgreementMeasure
        implements ICategorySpecificAgreement {

    /** Initializes the instance for the given annotation study. The study
     *  may never be null. */
    public KrippendorffAlphaUnitizingAgreement(
            final IUnitizingAnnotationStudy study) {
        super(study);
    }

    @Override
    protected double calculateObservedDisagreement() {
        double result = 0.0;
        for (Object category : study.getCategories()) {
            result += calculateObservedCategoryDisagreement(category);
        }
        result /= study.getCategoryCount();
        return result;
    }

    @Override
    protected double calculateExpectedDisagreement() {
        double result = 0.0;
        for (Object category : study.getCategories()) {
            result += calculateExpectedCategoryDisagreement(category);
        }
        result /= study.getCategoryCount();
        return result;
    }

    @Override
    public double calculateCategoryAgreement(Object category) {
        double D_O = calculateObservedCategoryDisagreement(category);
        double D_E = calculateExpectedCategoryDisagreement(category);
        if (D_O == D_E) {
            return 0.0;
        }
        else {
            return 1.0 - (D_O / D_E);
        }
    }

    protected double calculateObservedCategoryDisagreement(final Object category) {
        long B = study.getContinuumBegin();
        long L = study.getContinuumLength();
        int R = study.getRaterCount();
        double result = 0.0;
        for (int r1 = 0; r1 < R; r1++) {
            for (int r2 = r1 + 1; r2 < R; r2++) {
                Iterator<IUnitizingAnnotationUnit> units1 = study.getUnits().iterator();
                Iterator<IUnitizingAnnotationUnit> units2 = study.getUnits().iterator();
                IUnitizingAnnotationUnit nextUnit1 = UnitizingAnnotationStudy.findNextUnit(units1,
                        r1, category);
                IUnitizingAnnotationUnit nextUnit2 = UnitizingAnnotationStudy.findNextUnit(units2,
                        r2, category);
                long offset1 = B, length1 = 0;
                long offset2 = B, length2 = 0;
                Object category1 = null;
                Object category2 = null;
                long pos = B;
                while (pos < B + L && (nextUnit1 != null || nextUnit2 != null)) {
                    if (pos == offset1 + length1) {
                        if (nextUnit1 != null && pos == nextUnit1.getOffset()) {
                            length1 = nextUnit1.getLength();
                            category1 = nextUnit1.getCategory();
                            nextUnit1 = UnitizingAnnotationStudy.findNextUnit(units1, r1, category);
                        } else {
                            length1 = (nextUnit1 != null ? nextUnit1.getOffset() : B + L) - pos;
                            category1 = null;
                        }
                        offset1 = pos;
                    }
                    if (pos == offset2 + length2) {
                        if (nextUnit2 != null && pos == nextUnit2.getOffset()) {
                            length2 = nextUnit2.getLength();
                            category2 = nextUnit2.getCategory();
                            nextUnit2 = UnitizingAnnotationStudy.findNextUnit(units2, r2, category);
                        } else {
                            length2 = (nextUnit2 != null ? nextUnit2.getOffset() : B + L) - pos;
                            category2 = null;
                        }
                        offset2 = pos;
                    }
                    result += measureDistance(offset1, length1, category1,
                            offset2, length2, category2);
                    pos = Math.min(offset1 + length1, offset2 + length2);
                }
            }
        }
        result *= 2.0;
        result /= (double) (R * (R - 1) * (L * L));
        return result;
    }

    protected double calculateExpectedCategoryDisagreement(final Object category) {
        long B = study.getContinuumBegin();
        long L = study.getContinuumLength();
        int R = study.getRaterCount();

        int N_c = 0;
        BigDecimal squaredLengths = BigDecimal.ZERO;
        for (IUnitizingAnnotationUnit unit : study.getUnits()) {
            if (category.equals(unit.getCategory())) {
                N_c++;
                squaredLengths = squaredLengths.add(
                        new BigDecimal(unit.getLength()).multiply(
                                new BigDecimal(unit.getLength() - 1.0)));
            }
        }

        // Create a sorted list of all gap lengths.
        List<Long> gaps = new ArrayList<Long>();
        for (int r = 0; r < R; r++) {
            Iterator<IUnitizingAnnotationUnit> units = study.getUnits().iterator();
            IUnitizingAnnotationUnit nextUnit = UnitizingAnnotationStudy.findNextUnit(units, r,
                    category);
            long offset = B;
            long length = 0;
            long pos = B;
            while (pos < B + L) {
                if (pos == offset + length) {
                    if (nextUnit != null && pos == nextUnit.getOffset()) {
                        length = nextUnit.getLength();
                        nextUnit = UnitizingAnnotationStudy.findNextUnit(units, r, category);
                    } else {
                        length = (nextUnit != null ? nextUnit.getOffset() : B + L) - pos;
                        gaps.add(length);
                    }
                    offset = pos;
                }
                pos = offset + length;
            }
        }
        Collections.sort(gaps, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (o1 < o2) {
                    return +1;
                }
                if (o1 > o2) {
                    return -1;
                }
                return 0;
            }
        });

        BigDecimal result = BigDecimal.ZERO;
        for (IUnitizingAnnotationUnit unit : study.getUnits()) {
            if (category.equals(unit.getCategory())) {
                long length1 = unit.getLength();
                BigDecimal sum1 = new BigDecimal((N_c - 1.0)
                        * (2.0 * length1 * length1 * length1 - 3.0 * length1 * length1 + length1))
                        .divide(new BigDecimal(3), MathContext.DECIMAL128);
                BigDecimal sum2 = BigDecimal.ZERO;
                for (Long gap : gaps) {
                    if (gap >= length1) {
                        sum2 = sum2.add(new BigDecimal(gap - length1 + 1.0));
                    }
                    else {
                        break;
                    }
                }
                sum2 = sum2.multiply(new BigDecimal(length1 * length1));
                result = result.add(sum1).add(sum2);
            }
        }
        result = result
                .multiply(new BigDecimal(2).divide(new BigDecimal(L), MathContext.DECIMAL128));
        result = result.divide(
                new BigDecimal(R * L * (R * L - 1)).subtract(squaredLengths),
                MathContext.DECIMAL128);
        return result.doubleValue();
    }

    protected static double measureDistance(long offset1, long length1, final Object category1,
            long offset2, long length2, final Object category2)
    {
        long beginDiff = offset1 - offset2;
        long lengthDiff = length1 - length2;
        if (category1 != null && category2 != null && -length1 < beginDiff && beginDiff < length2) {
            return beginDiff * beginDiff + (beginDiff + lengthDiff) * (beginDiff + lengthDiff);
        }
        else if (category1 != null && category2 == null && -lengthDiff >= beginDiff
                && beginDiff >= 0) {
            return length1 * length1;
        }
        else if (category1 == null && category2 != null && -lengthDiff <= beginDiff
                && beginDiff <= 0) {
            return length2 * length2;
        }
        else {
            return 0.0;
        }
    }
}
