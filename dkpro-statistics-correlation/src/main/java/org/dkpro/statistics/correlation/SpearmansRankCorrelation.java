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
package org.dkpro.statistics.correlation;


import java.util.List;

import org.apache.commons.math.stat.correlation.SpearmansCorrelation;


public class SpearmansRankCorrelation {
    
    /**
     * Computes the correlation between two datasets.
     * @param list1 The first dataset as a list.
     * @param list2 The second dataset as a list.
     * @return The correlation between the two datasets.
     */
    public static double computeCorrelation(List<Double> list1, List<Double> list2) {
        double[] l1 = new double[list1.size()];
        double[] l2 = new double[list2.size()];

        for (int i=0; i<list1.size(); i++) {
            l1[i] = list1.get(i);
        }
        for (int i=0; i<list2.size(); i++) {
            l2[i] = list2.get(i);
        }

        SpearmansCorrelation sc = new SpearmansCorrelation();
        return sc.correlation(l1,  l2);
    }    
}
