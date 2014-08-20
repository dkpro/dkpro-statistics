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
package de.tudarmstadt.ukp.dkpro.statistics.agreement.coding;

import java.util.Map;
import java.util.Map.Entry;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.DisagreementMeasure;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IWeightedAgreement;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Abstract base class for weighted measures. In most cases, weighted
 * measures are defined as disagreement functions. They are characterized
 * by a distance function that is used to score the similarity between two
 * categories.
 * @see IDistanceFunction
 * @see DisagreementMeasure 
 * @author Christian M. Meyer
 */
public abstract class WeightedAgreement extends DisagreementMeasure
		implements IWeightedAgreement {

	protected IDistanceFunction distanceFunction;
	protected ICodingAnnotationStudy study;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public WeightedAgreement(final ICodingAnnotationStudy study) {
		this.study = study;
	}
	
	public IDistanceFunction getDistanceFunction() {
		return distanceFunction;
	}
	
	/** Uses the given distance function for upcoming calculations of the
	 *  inter-rater agreement. */
	public void setDistanceFunction(final IDistanceFunction distanceFunction) {
		this.distanceFunction = distanceFunction;
	}

	protected void ensureDistanceFunction() {
		if (distanceFunction == null)
			throw new NullPointerException("No distance function provided. "
					+ "Use " + getClass() + ".setDistanceFunction()!");
	}
	
}
