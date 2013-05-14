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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.util;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Simple implementation of the {@link IDistanceFunction} interface that
 * compares the annotation categories and returns 0 if their equal or 1 otherwise.
 *
 * @author Kostadin Cholakov
 * @date 23.10.2012
 */
public class NominalDistanceFunction implements IDistanceFunction {

    @Override
    public double measureDistance(IAnnotationStudy study, Object category1, Object category2) {

        return (category1.equals(category2) ? 0 : 1);
    }
}
