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

/**
 * Main data interface for an annotation study that can be used to calculate 
 * the inter-rater agreement of two or more raters/annotators.
 * The default implementation is {@link AnnotationStudy}, which might be
 * sufficient for most applications.  
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public interface IAnnotationStudy {

	/**
	 * Represents a single item/unit that each rater/annotator should
	 * tag with a certain annotation category. The item is usually part
	 * of a {@link IAnnotationStudy} and is also created there. 
	 * @author Christian M. Meyer
	 */
	public interface IAnnotationItem {
		
		/** Returns the annotation for the current item that the given
		 *  rater/annotator has created. */
		public Object getAnnotation(final int annotator);
		
		/** Returns all annotations of the current item. The resulting 
		 *  array is ordered by the raters resp. the same order that 
		 *  was used to create the annotation item. */
		public Object[] getAnnotations();
		
		//public Iterable<Object> getCategories();
		
	}
	
	/** Returns the number of raters/annotations that participate in the 
	 *  study. */
	public int getAnnotatorCount();
	
	/** Returns an iterator over all annotation categories within the study. 
	 *  Note that the categories are not per se clear; they might need to be 
	 *  gathered by iterating through all associated items, which yields 
	 *  performance problems in large-scale annotation studies. */
	public Iterable<Object> getCategories();

	/** Returns the number of annotation categories in the study. Note that
	 *  the categories are not per se clear; they might need to be gathered by 
	 *  iterating through all associated items, which yields performance 
	 *  problems in large-scale annotation studies. */
	public int getCategoryCount();

	/** Returns an iterator over all annotation items, which allows to 
	 *  access all the single annotations. */
	public Iterable<IAnnotationItem> getItems();
	
	/** Returns the number of annotation items, i.e. the number of units that
	 *  each rater should annotate. */
	public int getItemCount();
	
	/** Creates a new annotation item with the given annotations. The 
	 *  annotations need to be in order of the raters to ensure that each
	 *  rater has its unique annotation distribution. */
	public void addItem(final Object... annotations);
	
	/** Creates a new annotation item with the given annotations. The 
	 *  annotations need to be in order of the raters to ensure that each
	 *  rater has its unique annotation distribution. */
	public void addItemAsArray(final Object[] annotations);
	
}
