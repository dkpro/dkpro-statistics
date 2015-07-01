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
package org.dkpro.statistics.significance;

public class FisherZTransformation {

    /**
     * zf = 1/2 * ln( (1+r) / (1-r) )
     * 
     * @param value A correlation value
     * @return The Fisher Z-value for the given correlation
     */
	public static double transform(double value) {
		double transformed = 0.5 * Math.log( (1+value) / (1-value) );
		return transformed;
	}
	
	public static double retransform(double value) {
		double retransformed = (Math.exp(2*value) - 1) / (Math.exp(2*value) + 1);
		return retransformed;
	}
}