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

import java.util.Collection;
import java.util.HashSet;



/**
 * Represents a set of annotation categories that can be used as annotation
 * type in an annotation study to represent set-valued annotations, i.e. 
 * annotation studies that allow multiple annotation for each annotation
 * item/unit.
 * @author Christian M. Meyer
 * @date 04.11.2009 
 */
public class SetAnnotation extends HashSet<Object> implements Comparable<SetAnnotation> {

	/** Instanciates an empty set annotation. */
	public SetAnnotation() {
		super();
	}

	/** Instanciates a set annotation with the given values as set elements. */
	public SetAnnotation(Object... values) {
		super();
		for (Object value : values)
			add(value);
	}
		
	/** Instanciates a set annotation with the given values as set elements. */
	public SetAnnotation(Collection<? extends Object> c) {
		super(c);		
	}

	public int compareTo(final SetAnnotation that) {
		return toString().compareTo(that.toString());
	}
	
	public boolean equals(final Object that)	{
		if (!(that instanceof SetAnnotation))
			return false;
		return toString().equals(((SetAnnotation) that).toString());
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Object value : this)
			result.append(result.length() == 0 ? "" : ", ").append(value);
		return result.toString();
	}
	
}
