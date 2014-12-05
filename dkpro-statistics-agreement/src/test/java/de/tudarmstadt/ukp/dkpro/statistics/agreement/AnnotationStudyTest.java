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
package de.tudarmstadt.ukp.dkpro.statistics.agreement;

import junit.framework.TestCase;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.coding.CodingAnnotationStudy;

public class AnnotationStudyTest extends TestCase {

	public void testAddItem() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(3);
		study.addItem("A", "B", "C");
		study.addItem(5, 3, 0);
		study.addItem(new Object(), "c", 12);
		study.addItem((Object[]) new String[]{"A", "B", "D"});
		assertEquals(4, study.getItemCount());
	}
	
	public void testAddItemMissingUnits() {
		CodingAnnotationStudy study = new CodingAnnotationStudy(3);
		try {
			study.addItem("A");
			fail("IllegalArgumentException expected!");
		} catch (IllegalArgumentException e) {}
		
		try {
			study.addItem(5, 3);
			fail("IllegalArgumentException expected!");
		} catch (IllegalArgumentException e) {}
		
		try {
			study.addItem((Object) new String[]{"A", "B", "D"});
			fail("IllegalArgumentException expected!");
		} catch (IllegalArgumentException e) {}
		
		try {
			study.addItem((Object[]) new String[]{"A", "B"});
			fail("IllegalArgumentException expected!");
		} catch (IllegalArgumentException e) {}
	}
	
}
