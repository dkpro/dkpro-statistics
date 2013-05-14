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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Computes the correlation of two datasets.
 * @author zesch
 *
 */
public class PearsonCorrelation {

    private static Log log = LogFactory.getLog(PearsonCorrelation.class);

    /**
     * Computes the correlation between two datasets.
     * @param list1 The first dataset as a list.
     * @param list2 The second dataset as a list.
     * @return The correlation between the two datasets.
     */
    public static double computeCorrelation(List<Double> list1, List<Double> list2) {
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

        double correlation = computeCorrelation(doubleArray1, doubleArray2);

        return correlation;
    }

    /**
     * Computes the correlation between two datasets.
     * @param list1 The first dataset as a double array.
     * @param list2 The second dataset as a double array.
     * @return The correlation between the two datasets.
     */
    public static double computeCorrelation(double[] list1, double[] list2) {
        // R = { N* (Sum(x*y) - Sum(x)*Sum(y)} / sqrt( { N* Sum( x**2 ) - Sum( x )**2} ) * sqrt ( {N * Sum( y**2 ) - Sum( y )**2} )
        double sumXY = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXsquares = 0;
        double sumYsquares = 0;
        long N = list1.length; // we assume that both list are of equal length

        if (list1.length != list2.length) {
            log.fatal("Lists are not the same size ("+ list1.length + " - " + list2.length + ").");
            System.exit(1);
        }

        for (int i=0; i<N; i++) {
            double x = list1[i];
            double y = list2[i];
            sumXY += x*y;
            sumX += x;
            sumY += y;
            sumXsquares += x*x;
            sumYsquares += y*y;
        }

//        System.out.println("n: " + N);
//        System.out.println("x: " + sumX);
//        System.out.println("y: " + sumY);
//        System.out.println("xy: " + sumXY);
//        System.out.println("x^2: " + sumXsquares);
//        System.out.println("y^2: " + sumYsquares);

        double nominator = ( N*sumXY - sumX*sumY );

        double root1 = N*sumXsquares - sumX*sumX;
        double root2 = N*sumYsquares - sumY*sumY;
        double denominator = Math.sqrt( root1 ) * Math.sqrt( root2 );

//        System.out.println("root1: " + root1);
//        System.out.println("root2: " + root2);
//        System.out.println("nominator: " + nominator);
//        System.out.println("denominator: " + denominator);

        double correlation =  nominator / denominator;

        return correlation;
    }


}
