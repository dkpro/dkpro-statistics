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
package org.dkpro.statistics.agreement.distance;

import org.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Distance function for set-valued annotation studies based on the definition of partial matching
 * (Passonneau, 2004). The distance function assumes categories of type {@link SetAnnotation}.<br>
 * <br>
 * References:
 * <ul>
 * <li>Passonneau, R.J.: Computing reliability for coreference annotation. In: Proceedings of the
 * Fourth International Conference on Language Resources and Evaluation, p. 1503–1506, 2004.</li>
 * </ul>
 * 
 * @see IDistanceFunction
 * @see SetAnnotation
 * @see MASISetAnnotationDistanceFunction
 * @author Christian M. Meyer
 */
public class SetAnnotationDistanceFunction
    implements IDistanceFunction
{

    @Override
    public double measureDistance(final IAnnotationStudy study, final Object category1,
            final Object category2)
    {
        SetAnnotation c1 = (SetAnnotation) category1;
        SetAnnotation c2 = (SetAnnotation) category2;

        SetAnnotation delta = new SetAnnotation(c1);
        delta.removeAll(c2);
        boolean c1_subset_c2 = (delta.size() == 0);
        int overlap = c1.size() - delta.size();
        delta = new SetAnnotation(c2);
        delta.removeAll(c1);
        boolean c2_subset_c1 = (delta.size() == 0);

        if (c1_subset_c2 && c2_subset_c1) {
            return 0.0; // identical.
        }
        if (c1_subset_c2 || c2_subset_c1) {
            return 1.0 / 3.0; // subsets.
        }
        if (overlap > 0) {
            return 2.0 / 3.0; // some intersection.
        }
        else {
            return 1.0; // disjoint.
        }
    }

}
