/*******************************************************************************
 * Copyright 2013
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
 ******************************************************************************/
package de.tudarmstadt.ukp.dkpro.statistics.correlation;

import java.util.List;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.inference.TestUtils;


/**
 * Computes statistical significance.
 * @author zesch
 *
 */
public class Significance {

    /** 
     * Tests two correlation values for equality.
     * http://www.lesn.appstate.edu/olson/stat_directory/Statistical%20procedures/Correlations/a_test_of_the_equivalence_of_two.htm
     * 
     * Null-Hypothesis: Both samples of pairs show the same correlation strength, i.e., r1 = r2.
     * 
     * Assumptions:
     * The values of both members of both samples of pairs are Normal (bivariate) distributed.
     * Should only be used when both samples (n1 and n2) are larger than 10.
     * 
     * Scale:
     * Interval (for the raw data).
     * 
     * Procedure:
     * The two correlation coefficients are transformed with the Fisher Z-transformation:
     * Zf = 1/2 * ln( (1+r) / (1-r) )
     *  
     * The difference
     * z = (Zf1 - Zf2) / SQRT( 1/(N1-3) + 1/(N2-3) )
     * 
     * is approximately Standard Normal distributed.
     * 
     * So the p-value is
     * pv = 2 * (1 - pnorm(abs(z)))
     * 
     * Uses:
     *  - for testing whether to correlations differ significantly
     * 
     * 
     * @param correlation1 The first correlation value.
     * @param correlation2 The second correlation value.
     * @param n1 The size of the first dataset.
     * @param n2 The size of the second dataset.
     * @param alpha The significance level.
     * @return True, if the null hypothesis is rejected, i.e. the difference between the correlation values is significant. False otherwise.
     * @throws MathException 
     */
    public static boolean testCorrelations(double correlation1, double correlation2, int n1, int n2, double alpha) throws MathException {

        double p = getSignificance(correlation1, correlation2, n1, n2);
        
        if (p <= alpha) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Uses a paired t-test to test whether the correlation value computed from these datasets is significant.
     * @param sample1 The first dataset vector.
     * @param sample2 The second dataset vector.
     * @return The significance value p.
     * @throws MathException 
     * @throws IllegalArgumentException 
     */
    public static double getSignificance(double[] sample1, double[] sample2) throws IllegalArgumentException, MathException {
        double alpha = TestUtils.pairedTTest(sample1, sample2);
        boolean significance = TestUtils.pairedTTest(sample1, sample2, .30);
System.err.println("sig: " + significance);
        return alpha;
    }
    
    
    /**
     * @throws MathException 
     * @throws IllegalArgumentException 
     * @see org.tud.sir.util.statistics.Significance#getSignificance(double[], double[])
     */
    public static double getSignificance(List<Double> list1, List<Double> list2) throws IllegalArgumentException, MathException {
        double[] doubleArray1 = new double[list1.size()];
        double[] doubleArray2 = new double[list2.size()];

        int off1 = 0;
        for (double item : list1) {
            doubleArray1[off1] = item;
            off1++;
        }

        int off2 = 0;
        for (double item : list2) {
            doubleArray2[off2] = item;
            off2++;
        }

        return getSignificance(doubleArray1, doubleArray2);
    }
    
    
    
    /**
     * Computes the significance of the difference between two correlations.
     * @see org.tud.sir.util.statistics.Significance.testCorrelations
     */
    public static double getSignificance(double correlation1, double correlation2, int n1, int n2) throws MathException {
        
        // transform to Fisher Z-values
        double zv1 = getZValue(correlation1);
        double zv2 = getZValue(correlation2);

        // difference of the Z-values
        double zDifference = (zv1 - zv2) / Math.sqrt( (double)1/(n1-3) + (double)1/(n2-3));
        
        // get p value from the normal distribution
        NormalDistribution normal = new NormalDistributionImpl();
        double p = 2 * (1 - normal.cumulativeProbability( Math.abs(zDifference)));
        return p;
    }
    
    
    /**
     * zf = 1/2 * ln( (1+r) / (1-r) )
     * 
     * @param correlation
     * @return The Fisher Z-value for the given correlation
     */
    private static double getZValue(double correlation) {
        double zf = 0.5 * Math.log( (1 + correlation) / (1 - correlation));
        return zf;
    }    
}
