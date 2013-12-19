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

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Default implementation of the {@link IAnnotationStudy} inteface. The
 * implementation uses an array list for storing the annotation items and
 * caches the annotation categories in a tree set as soon as this 
 * information is requested.
 * @author Christian M. Meyer
 * @date 04.11.2009
 */
public class AnnotationStudy implements IAnnotationStudy {

	/**
	 * Default implementation of the {@link IAnnotationItem} interface. 
	 * @author Christian M. Meyer
	 */
	public class AnnotationItem implements IAnnotationItem {

		protected Object[] annotations;
		
		/** Creates a new annotation item with the given annotations. It is
		 *  recommended to use {@link IAnnotationStudy#addItem(Object...)}
		 *  to create the annotation items. */
		public AnnotationItem(final Object[] annotations) {
			this.annotations = annotations;
		}
		
		public Object getAnnotation(int annotator) {
			return annotations[annotator];
		}

		public Object[] getAnnotations() {
			return annotations;
		}
		
		/** Returns an iterable tree set of all the annotation categories
		 *  of the current annotation item instance. */
		public Iterable<Object> getCategories() {
			categories = new TreeSet<Object>();
			for (Object category : getAnnotations())
				categories.add(category);
			return categories;
		}
		
	}
	
	
	protected ArrayList<IAnnotationItem> items;
	protected TreeSet<Object> categories;
	protected int annotatorCount;
	
	/** Instanciates the annotation study with the given number of 
	 *  raters/annotators. */
	public AnnotationStudy(final int annotatorCount) {
		items = new ArrayList<IAnnotationItem>();
		this.annotatorCount = annotatorCount;
	}

	public Iterable<IAnnotationItem> getItems() {
		return items;
	}

	public int getItemCount() {
		return items.size();
	}
	
	public int getAnnotatorCount() {
		return annotatorCount;
	}

	public Iterable<Object> getCategories() {
		if (categories == null) {
			categories = new TreeSet<Object>();
			for (IAnnotationItem item : items) 
				for (Object category : item.getAnnotations())
					categories.add(category);
		}
		return categories;
	}

	public int getCategoryCount() {
		getCategories();
		return categories.size();
	}

	public void addItem(final Object... annotations) {
		addItemAsArray(annotations);
	}

	public void addItemAsArray(final Object[] annotations) {
		items.add(new AnnotationItem(annotations));
	}

}
