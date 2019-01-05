/*******************************************************************************
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
 ******************************************************************************/
package org.dkpro.statistics.agreement.distance;

import java.util.Map;

import org.dkpro.statistics.agreement.IAnnotationStudy;
import org.dkpro.statistics.agreement.coding.CodingAnnotationStudy;
import org.dkpro.statistics.agreement.coding.ICodingAnnotationStudy;

/**
 * Implementation of the {@link IDistanceFunction} interface for scoring the distance between
 * ordinal-scaled categories. That is to say, categories represent ranks, such as the values on a
 * Likert scale. A typical example is FULLY AGREE, RATHER AGREE, UNDECIDED, RATHER DISAGREE, FULLY
 * DISAGREE. Note that one cannot easily say that the difference between FULLY AGREE and RATHER
 * AGREE is equal. The intuition of the ordinal distance is function is rather to count the number
 * ranks in between a pair of given ranks. Consider the example by Krippendorff (1980): 10 units are
 * assigned the ranks 1, 2, 3, 3, 4, 4, 4, 4, 5, 10. Then intuitively, the difference should be much
 * smaller between 1 and 3 then between 3 and 5. In the case of uniform frequencies of the ranks
 * (e.g., 1, 1, 2, 2, 3, 3, 4, 4), the ordinal distance function equals the
 * {@link IntervalDistanceFunction}. Mathematically, the ordinal scale only allows for the equality
 * and comparison operations, but prohibits addition and multiplication. The distance function
 * assumes the ranks to be of type integer. For other category types, the distance function falls
 * back to a {@link NominalDistanceFunction}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology. Beverly Hills, CA:
 * Sage Publications, 1980.</li>
 * </ul>
 * 
 * @see IDistanceFunction
 * @author Christian M. Meyer
 */
public class OrdinalDistanceFunction implements IDistanceFunction {
    
    @Override
    public double measureDistance(final IAnnotationStudy study, 
            final Object category1, final Object category2) {
        if (category1 instanceof Integer && category2 instanceof Integer) {
            if (category1.equals(category2)) {
                return 0.0;
            }
            
            // TODO: Provide generic method for the annotation study w/ potential use for unitizing
            // tasks!
            Map<Object, Integer> nk = CodingAnnotationStudy
                    .countTotalAnnotationsPerCategory((ICodingAnnotationStudy) study);
            Integer v;
            
            double result = 0.0;
            v = nk.get(category1);
            if (v != null) {
                result += ((double) v) / 2.0;
            }
            v = nk.get(category2);
            if (v != null) {
                result += ((double) v) / 2.0;
            }
            
            int cat1 = (Integer) category1;
            int cat2 = (Integer) category2;
            int minCat = (cat1 < cat2 ? cat1 : cat2);
            int maxCat = (cat1 < cat2 ? cat2 : cat1);
            for (int i = minCat + 1; i < maxCat; i++) {
                v = nk.get(i);
                if (v != null) {
                    result += v;
                }    
            }
            return result * result;
        }
            
        return (category1.equals(category2) ? 0.0 : 1.0);
    }    
}
