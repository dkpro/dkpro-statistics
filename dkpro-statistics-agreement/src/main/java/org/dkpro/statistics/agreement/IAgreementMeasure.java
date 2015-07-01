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
package org.dkpro.statistics.agreement;

/**
 * Generic interface that is to be implemented by all inter-rater agreement 
 * measures. The basic idea is to calculate a numerical score for a given 
 * {@link IAnnotationStudy}. 
 * @author Christian M. Meyer
 */
public interface IAgreementMeasure {

	/** Returns the inter-rater agreement score. Unless otherwise indicated, 
	 *  the result is between -1 and +1, where +1 indicates perfect agreement,
	 *  0 indicates no agreement or that the agreement can be fully explained by
	 *  chance, and -1 indicates perfect disagreement. */
	public double calculateAgreement();
	
}
