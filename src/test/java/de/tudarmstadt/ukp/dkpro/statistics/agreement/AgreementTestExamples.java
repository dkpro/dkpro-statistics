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
package de.tudarmstadt.ukp.dkpro.statistics.agreement;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationStudy;

/**
 * Factory class for creating example annotation studies that have been 
 * defined for the test cases or adopted from the literature.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public abstract class AgreementTestExamples {
	
	/** Creates an example annotation study used in 
	 *  (Artstein and Poesio, 2008). */
	public static IAnnotationStudy createArtsteinPoesio2008_1() {
		/*         STAT  IReq
		 *   STAT   20    20
		 *   IReq   10    50
		 */
		AnnotationStudy study = new AnnotationStudy(2);
		for (int i = 0; i < 20; i++)
			study.addItem("STAT", "STAT");
		for (int i = 0; i < 20; i++)
			study.addItem("IReq", "STAT");
		for (int i = 0; i < 10; i++)
			study.addItem("STAT", "IReq");
		for (int i = 0; i < 50; i++)
			study.addItem("IReq", "IReq");
		return study;
	}

	/** Creates an example annotation study used in 
	 *  (Artstein and Poesio, 2008). */
	public static IAnnotationStudy createArtsteinPoesio2008_2() {
		AnnotationStudy study = new AnnotationStudy(2);
		for (int i = 0; i < 46; i++)
			study.addItem("STAT", "STAT");
		for (int i = 0; i < 6; i++)
			study.addItem("IReq", "STAT");
		for (int i = 0; i < 32; i++)
			study.addItem("IReq", "IReq");
		for (int i = 0; i < 6; i++)
			study.addItem("IReq", "Chck");
		for (int i = 0; i < 10; i++)
			study.addItem("Chck", "Chck");
		return study;
	}

	/** Creates an example annotation study. */
	public static AnnotationStudy createMeyer2009_1() {
		AnnotationStudy study = new AnnotationStudy(2);
		study.addItem("high", "high");
		study.addItem("high", "high");
		study.addItem("high", "low");
		study.addItem("low", "high");
		study.addItem("low", "low");
		study.addItem("low", "low");
		study.addItem("low", "low");
		study.addItem("low", "high");
		study.addItem("low", "low");
		study.addItem("low", "low");
		return study;
	}

	/** Creates an example annotation study used in 
	 *  (Fleiss, 1972). */
	public static IAnnotationStudy createFleiss1972() {
		AnnotationStudy study = new AnnotationStudy(6);
		study.addItem(4, 4, 4, 4, 4, 4);
		study.addItem(2, 2, 2, 5, 5, 5);
		study.addItem(2, 3, 3, 3, 3, 5);
		study.addItem(5, 5, 5, 5, 5, 5);
		study.addItem(2, 2, 2, 4, 4, 4);
		study.addItem(1, 1, 3, 3, 3, 3);
		study.addItem(3, 3, 3, 3, 5, 5);
		study.addItem(1, 1, 3, 3, 3, 4);
		study.addItem(1, 1, 4, 4, 4, 4);
		study.addItem(5, 5, 5, 5, 5, 5);

		study.addItem(1, 4, 4, 4, 4, 4);
		study.addItem(1, 2, 4, 4, 4, 4);
		study.addItem(2, 2, 2, 3, 3, 3);
		study.addItem(1, 4, 4, 4, 4, 4);
		study.addItem(2, 2, 4, 4, 4, 5);
		study.addItem(3, 3, 3, 3, 3, 5);
		study.addItem(1, 1, 1, 4, 5, 5);
		study.addItem(1, 1, 1, 1, 1, 2);
		study.addItem(2, 2, 4, 4, 4, 4);
		study.addItem(1, 3, 3, 5, 5, 5);
		
		study.addItem(5, 5, 5, 5, 5, 5);
		study.addItem(2, 4, 4, 4, 4, 4);
		study.addItem(2, 2, 4, 5, 5, 5);
		study.addItem(1, 1, 4, 4, 4, 4);
		study.addItem(1, 4, 4, 4, 4, 5);
		study.addItem(2, 2, 2, 2, 2, 4);
		study.addItem(1, 1, 1, 1, 5, 5);
		study.addItem(2, 2, 4, 4, 4, 4);
		study.addItem(1, 3, 3, 3, 3, 3);
		study.addItem(5, 5, 5, 5, 5, 5);
		return study;
	}
	
	/** Creates an example annotation study with many items. Such a study
	 *  can easily yield problems related to numerical stability. */
	public static IAnnotationStudy createNumericallyInstable() {
		AnnotationStudy study = new AnnotationStudy(2);
		for (int i = 0; i < 45000; i++)
			study.addItem(1, 1);
		for (int i = 0; i < 5000; i++)
			study.addItem(1, 0);
		for (int i = 0; i < 4999; i++)
			study.addItem(0, 1);
		for (int i = 0; i < 45000; i++)
			study.addItem(0, 0);
		return study;
	}
	
}
