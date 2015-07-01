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
package org.dkpro.statistics.agreement.coding;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dkpro.statistics.agreement.IAnnotationUnit;
import org.dkpro.statistics.agreement.ICategorySpecificAgreement;
import org.dkpro.statistics.agreement.IChanceCorrectedDisagreement;
import org.dkpro.statistics.agreement.distance.IDistanceFunction;

/**
 * Implementation of Krippendorff's (1980) alpha-measure for calculating a
 * chance-corrected inter-rater agreement for multiple raters with arbitrary
 * distance/weighting functions. The basic idea is to divide the estimated
 * variance of within the items by the estimated total variance. Before an
 * inter-rater agreement can be calculated, an {@link IDistanceFunction}
 * instance needs to be assigned.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: Estimating the reliability, systematic error and 
 *   random error of interval data. Educational and Psychological Measurement
 *   30(1):61-70, 1970.</li>
 * <li>Krippendorff, K.: Content Analysis: An Introduction to Its Methodology.
 *   2nd edition, Thousand Oaks, CA: Sage Publications, 2004.</li>
 * <li>Artstein, R. & Poesio, M.: Inter-Coder Agreement for Computational 
 *   Linguistics. Computational Linguistics 34(4):555-596, 2008.</li></ul>
 * @author Christian M. Meyer
 */
public class KrippendorffAlphaAgreement extends WeightedAgreement
		implements IChanceCorrectedDisagreement, ICategorySpecificAgreement, 
		ICodingItemSpecificAgreement {

	protected Map<Object, Map<Object, Double>> coincidenceMatrix;
	
	/** Initializes the instance for the given annotation study. The study 
	 *  should never be null. */
	public KrippendorffAlphaAgreement(final ICodingAnnotationStudy study,
			final IDistanceFunction distanceFunction) {
		super(study);
		this.distanceFunction = distanceFunction;
	}
	
	@Override
	public double calculateObservedDisagreement() {
		ensureDistanceFunction();
		if (coincidenceMatrix == null)
			coincidenceMatrix = CodingAnnotationStudy.countCategoryCoincidence(study);
		
		double n = 0.0;
		double result = 0.0;
		for (Entry<Object, Map<Object, Double>> cat1 : coincidenceMatrix.entrySet())
			for (Entry<Object, Double> cat2 : cat1.getValue().entrySet()) {
					result += cat2.getValue() * distanceFunction.measureDistance(study, cat1.getKey(), cat2.getKey());
				n += cat2.getValue();
			}		
		result /= n;
		return result;
	}

	/** Calculates the expected inter-rater agreement using the defined 
	 *  distance function to infer the assumed probability distribution. 
	 *  @throws NullPointerException if the annotation study is null. 
	 *  @throws ArithmeticException if there are no items or raters in the 
	 *  	annotation study. */
	public double calculateExpectedDisagreement() {
		ensureDistanceFunction();
		if (coincidenceMatrix == null)
			coincidenceMatrix = CodingAnnotationStudy.countCategoryCoincidence(study);
		
		double n = 0.0;
		Map<Object, Double> marginals = new HashMap<Object, Double>();
		for (Entry<Object, Map<Object, Double>> cat1 : coincidenceMatrix.entrySet()) {
			double n_c = 0.0;
			for (Entry<Object, Double> cat2 : cat1.getValue().entrySet())
				n_c += cat2.getValue();
			marginals.put(cat1.getKey(), n_c);
			n += n_c;
		}
		
		double result = 0.0;
		for (Entry<Object, Double> cat1 : marginals.entrySet())
			for (Entry<Object, Double> cat2 : marginals.entrySet())
				result += cat1.getValue() * cat2.getValue()
						* distanceFunction.measureDistance(study, cat1.getKey(), cat2.getKey());
		result /= n * (n - 1.0);
		return result;
	}
	
	public double calculateItemAgreement(final ICodingAnnotationItem item) {
		ensureDistanceFunction();
		Map<Object, Map<Object, Double>> itemMatrix = 
				CodingAnnotationStudy.countCategoryCoincidence(item);
		
		double n = 0.0;
		double D_O = 0.0;
		for (Entry<Object, Map<Object, Double>> cat1 : itemMatrix.entrySet())
			for (Entry<Object, Double> cat2 : cat1.getValue().entrySet()) {
					D_O += cat2.getValue() * distanceFunction.measureDistance(study, cat1.getKey(), cat2.getKey());
				n += cat2.getValue();
			}		
		D_O /= n;
		
		if (coincidenceMatrix == null)
			coincidenceMatrix = CodingAnnotationStudy.countCategoryCoincidence(study);
		n = 0.0;
		Map<Object, Double> marginals = new TreeMap<Object, Double>();
		for (Entry<Object, Map<Object, Double>> cat1 : coincidenceMatrix.entrySet()) {
			double n_c = 0.0;
			for (Entry<Object, Double> cat2 : cat1.getValue().entrySet())
				n_c += cat2.getValue();
			marginals.put(cat1.getKey(), n_c);
			n += n_c;
		}
		
		/*double D_E = 0.0;
		for (Entry<Object, Double> cat1 : marginals.entrySet())
			for (Entry<Object, Double> cat2 : marginals.entrySet())
				D_E += cat1.getValue() * cat2.getValue()
						* distanceFunction.measureDistance(study, cat1.getKey(), cat2.getKey());
		D_E /= n * (n - 1.0);*/
		double D_E = calculateExpectedDisagreement();
		if (D_E == 0.0)
			return 1.0;
		else		
			return 1.0 - (D_O / D_E);
	}
	
	public double calculateCategoryAgreement(final Object category) {
		ensureDistanceFunction();
		
		final Object NULL_CATEGORY = new Object();
		double observedDisagreement = 0.0;
		int nKeepCategorySum = 0;
		int nNullCategorySum = 0;
		for (ICodingAnnotationItem item : study.getItems()) {
			int nKeepCategory = 0;
			int nNullCategory = 0;
			for (IAnnotationUnit annotation : item.getUnits())
				if (category.equals(annotation.getCategory()))
					nKeepCategory++;
				else
					nNullCategory++;
			observedDisagreement += 
					  nKeepCategory * nKeepCategory * distanceFunction.measureDistance(study, category, category)
					+ nKeepCategory * nNullCategory * distanceFunction.measureDistance(study, category, NULL_CATEGORY)
					+ nNullCategory * nKeepCategory * distanceFunction.measureDistance(study, NULL_CATEGORY, category)
					+ nNullCategory * nNullCategory * distanceFunction.measureDistance(study, NULL_CATEGORY, NULL_CATEGORY);
			nKeepCategorySum += nKeepCategory;
			nNullCategorySum += nNullCategory;
		}
		observedDisagreement /= (double) study.getItemCount() 
				* study.getRaterCount() * (study.getRaterCount() - 1);

		double expectedDisagreement = 
				  nKeepCategorySum * nKeepCategorySum * distanceFunction.measureDistance(study, category, category)
				+ nKeepCategorySum * nNullCategorySum * distanceFunction.measureDistance(study, category, NULL_CATEGORY)
				+ nNullCategorySum * nKeepCategorySum * distanceFunction.measureDistance(study, NULL_CATEGORY, category)
				+ nNullCategorySum * nNullCategorySum * distanceFunction.measureDistance(study, NULL_CATEGORY, NULL_CATEGORY);
		expectedDisagreement /= (double) study.getItemCount() * study.getRaterCount() 
				* (study.getItemCount() * study.getRaterCount() - 1);
		
		return 1.0 - (observedDisagreement / expectedDisagreement);
	}
	
}
