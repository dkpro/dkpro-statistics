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
import de.tudarmstadt.ukp.dkpro.statistics.agreement.KrippendorffAlphaAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.WeightedKappaAgreement;

/**
 * Interface for a distance function for calculating the inter-rater 
 * agreement in a weighted setting. The interface can e.g. be used in
 * {@link WeightedKappaAgreement} or {@link KrippendorffAlphaAgreement}.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public interface IDistanceFunction {

	/** Returns a distance value for both the annotation categories that
	 *  occur within the given annotation study. */
	public double measureDistance(final IAnnotationStudy study, 
			final Object category1, final Object category2);
					
}
