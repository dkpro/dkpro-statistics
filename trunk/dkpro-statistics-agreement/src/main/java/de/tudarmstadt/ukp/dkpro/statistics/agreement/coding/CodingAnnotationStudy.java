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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationUnit;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.IAnnotationUnit;

/**
 * Default implementation of the {@link ICodingAnnotationStudy} interface.
 * Instantiate this class for representing the annotation item of a
 * coding study (i.e., an annotation setup in which the (human) raters
 * are asked to code a set of given annotation items). The standard way of
 * representing the annotation units (i.e., the category assigned by a
 * certain rater) of an annotation item is using the
 * {@link #addItem(Object...)} method which allows specifying the list of
 * categories assigned by the raters.
 * @see ICodingAnnotationStudy
 * @see ICodingAnnotationItem
 * @author Christian M. Meyer
 */
public class CodingAnnotationStudy extends AnnotationStudy
		implements ICodingAnnotationStudy, Cloneable {

	protected List<ICodingAnnotationItem> items;

	/** Initializes and empty annotation study for a coding task. The basic
	 *  setup of a coding study is assigning categories to units with fixed
	 *  boundaries. */
	protected CodingAnnotationStudy() {
		super();
		items = new ArrayList<ICodingAnnotationItem>();
	}

	/** Initializes and empty annotation study for a coding task with the given
	 *  number of raters. The basic setup of a coding study is assigning
	 *  categories to units with fixed boundaries. */
	public CodingAnnotationStudy(int raterCount) {
		this();
		for (int raterIdx = 0; raterIdx < raterCount; raterIdx++) {
            addRater(Integer.toString(raterIdx));
        }
	}

	/** Add the given annotation item to this study. The specified item
	 *  should never be null. When relying on the default implementation
	 *  {@link CodingAnnotationItem}, it is recommended to use
	 *  {@link #addItem(Object...)} instead. */
	protected void addItem(final ICodingAnnotationItem item) {
		items.add(item);
	}

	/** Creates a new {@link CodingAnnotationItem} which has been coded with
	 *  the given annotation categories. Note that the order of the categories
	 *  must correspond to the raters' indexes. Use null to represent missing
	 *  annotations, Invoking <code>addItem("A", "B", null, "A")</code>
	 *  indicates an annotation item which has been coded as category "A"
	 *  by rater 0 and 3 and as category "B" by rater 1. Rater 2 did not
	 *  assign any category to the item. The method is a shorthand for
	 *  {@link #addItemAsArray(Object[])}.
	 *  @throws IllegalArgumentException if the number of annotations does
	 *      not match the number of raters. */
	public ICodingAnnotationItem addItem(final Object... annotations) {
		return addItemAsArray(annotations);
	}

	/** Creates a new {@link CodingAnnotationItem} which has been coded with
	 *  the given annotation categories. Note that the order of the categories
	 *  must correspond to the raters' indexes. Use null to represent missing
	 *  annotations, Invoking <code>addItem(new Object[]{"A", "B", null,
	 *  "A"})</code> indicates an annotation item which has been coded as
	 *  category "A" by rater 0 and 3 and as category "B" by rater 1. Rater 2
	 *  did not assign any category to the item.
	 *  @throws IllegalArgumentException if the number of annotations does
	 *      not match the number of raters. */
	public ICodingAnnotationItem addItemAsArray(final Object[] annotations) {
		if (annotations.length != raters.size()) {
            throw new IllegalArgumentException("Incorrect number of annotation units "
					+ "(expected " + raters.size() + ", given "
					+ annotations.length + "). "
					+ "For array params, use #addItemsAsArray instead of #addItem.");
        }

		int itemIdx = items.size();
		CodingAnnotationItem item = new CodingAnnotationItem(raters.size());
		for (int raterIdx = 0; raterIdx < annotations.length; raterIdx++) {
            item.addUnit(createUnit(itemIdx, raterIdx, annotations[raterIdx]));
        }
		items.add(item);
		return item;
	}

	/** Shorthand for invoking {@link #addItem(Object...)} with the same
	 *  parameters multiple times. This method is useful for modeling
	 *  annotation data based on a contingency table. */
	public void addMultipleItems(int times, final Object... values) {
		for (int i = 0; i < times; i++) {
            addItemAsArray(values);
        }
	}

	protected IAnnotationUnit createUnit(int index, int raterIdx,
			final Object category) {
		IAnnotationUnit result = new AnnotationUnit(/*index, */raterIdx, category);
		if (result.getCategory() != null) {
            categories.add(result.getCategory());
        }
		return result;
	}

	@Override
	public ICodingAnnotationItem getItem(int index) {
		return items.get(index);
	}

	@Override
	public Iterable<ICodingAnnotationItem> getItems() {
		return items;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public int getUnitCount() {
		int result = 0;
		for (ICodingAnnotationItem item : items) {
            result += item.getRaterCount();
        }
		return result;
		//return items.size() * raters.size();
	}

	@Override
	public boolean hasMissingValues() {
		for (ICodingAnnotationItem item : items) {
            if (item.getRaterCount() != raters.size()) {
                return true;
            }
        }

		return false;
	}

	@Override
	public CodingAnnotationStudy clone() {
		CodingAnnotationStudy result = new CodingAnnotationStudy(getRaterCount());
		for (ICodingAnnotationItem item : getItems()) {
			CodingAnnotationItem newItem = new CodingAnnotationItem(raters.size());
			for (IAnnotationUnit unit : item.getUnits()) {
                newItem.addUnit(result.createUnit(result.items.size(),
						unit.getRaterIdx(), unit.getCategory()));
            }
			result.items.add(newItem);
		}
		for (Object category : getCategories()) {
            result.addCategory(category);
        }
		return result;
	}

	/** Returns a clone of the current annotation study in which all categories
	 *  are replaced by the given nullCategory except the categories matching
	 *  the specified keepCategory. */
	public CodingAnnotationStudy stripCategories(final Object keepCategory,
			final Object nullCategory) {
		CodingAnnotationStudy result = new CodingAnnotationStudy(getRaterCount());
		for (ICodingAnnotationItem item : getItems()) {
			CodingAnnotationItem newItem = new CodingAnnotationItem(raters.size());
			for (IAnnotationUnit unit : item.getUnits()) {
				Object newCategory;
				if (!keepCategory.equals(unit.getCategory())) {
                    newCategory = nullCategory;
                }
                else {
                    newCategory = keepCategory;
                }
				newItem.addUnit(result.createUnit(result.items.size(),
						unit.getRaterIdx(), newCategory));
			}
			result.items.add(newItem);
		}
		return result;
	}

	/** Returns a clone of the current annotation study which contains
	 *  only the annotation units of the raters with the given indexes.
	 *  All other units will be removed. This method is useful for
	 *  converting an annotation study with multiple raters into a
	 *  (pairwise) annotation study with two raters. */
	public CodingAnnotationStudy extractRaters(final int... raters) {
		CodingAnnotationStudy result = new CodingAnnotationStudy(raters.length);
		for (ICodingAnnotationItem item : getItems()) {
			CodingAnnotationItem newItem = new CodingAnnotationItem(raters.length);
			for (int r = 0; r < raters.length; r++) {
				IAnnotationUnit unit = item.getUnit(raters[r]);
				newItem.addUnit(result.createUnit(result.items.size(),
						r, unit.getCategory()));
			}
			result.items.add(newItem);
		}
		return result;
	}

	/*TODO public IAnnotationStudy createPairwiseStudy(int rater1, int rater2) {
		AnnotationStudy result = new AnnotationStudy(2);
		for (IAnnotationItem item : getItems())
			result.addItem(item.getAnnotation(rater1), item.getAnnotation(rater2));
		return result;
	}*/


	/** Returns a two-dimensional map of categories and raters and the
	 *  corresponding usage frequencies in the given annotation study
	 *  (i.e., how often a certain rater used a certain category for
	 *  coding an annotation unit). */
	// Category x Rater -> #
	public static Map<Object, int[]> countAnnotationsPerCategory(
			final ICodingAnnotationStudy study) {
		Map<Object, int[]> result = new HashMap<Object, int[]>();
		for (ICodingAnnotationItem item : study.getItems()) {
            for (IAnnotationUnit unit : item.getUnits()) {
				Object category = unit.getCategory();
				if (category == null) {
                    continue;
                }

				int[] counts = result.get(category);
				if (counts == null) {
                    counts = new int[study.getRaterCount()];
                }
				counts[unit.getRaterIdx()]++;
				result.put(category, counts);
			}
        }
		return result;
	}

	/** Returns a map of categories and their usage frequencies (i.e.,
	 *  how often they are used in annotation units) within the given
	 *  annotation study. */
	// Category -> #
	public static Map<Object, Integer> countTotalAnnotationsPerCategory(
			final ICodingAnnotationStudy study) {
		Map<Object, Integer> result = new HashMap<Object, Integer>();
		for (ICodingAnnotationItem item : study.getItems()) {
			if (item.getRaterCount() <= 1) {
                continue;
            }

			for (IAnnotationUnit unit : item.getUnits()) {
				Object category = unit.getCategory();
				if (category == null) {
                    continue;
                }

				Integer count = result.get(category);
				if (count == null) {
                    result.put(category, 1);
                }
                else {
                    result.put(category, count + 1);
                }
			}
		}
		return result;
	}

	/** Returns a map of categories and their usage frequencies (i.e.,
	 *  how often they are used in annotation units) within the given
	 *  annotation item. */
	// Category -> #
	public static Map<Object, Integer> countTotalAnnotationsPerCategory(
			final ICodingAnnotationItem item) {
		Map<Object, Integer> result = new HashMap<Object, Integer>();
		for (IAnnotationUnit unit : item.getUnits()) {
			Object category = unit.getCategory();
			if (category == null) {
                continue;
            }

			Integer count = result.get(category);
			if (count == null) {
                result.put(category, 1);
            }
            else {
                result.put(category, count + 1);
            }
		}
		return result;
	}

	/** Returns a two dimensional map of category pairs and their co-occurrence
	 *  frequencies for the given annotation study. */
	// Category x Category -> #
	public static Map<Object, Map<Object, Double>>
			countCategoryCoincidence(final ICodingAnnotationStudy study) {
		Map<Object, Map<Object, Double>> result =
				new HashMap<Object, Map<Object, Double>>();
		for (ICodingAnnotationItem item : study.getItems()) {
			Map<Object, Map<Object, Double>> itemMatrix =
					countCategoryCoincidence(item);

			for (Entry<Object, Map<Object, Double>> itemCat : itemMatrix.entrySet()) {
				Map<Object, Double> resultCat = result.get(itemCat.getKey());
				if (resultCat == null) {
					resultCat = new HashMap<Object, Double>();
					result.put(itemCat.getKey(), resultCat);
				}
				for (Entry<Object, Double> itemEntry : itemCat.getValue().entrySet()) {
					Double resultEntry = resultCat.get(itemEntry.getKey());
					if (resultEntry == null) {
                        resultEntry = 0.0;
                    }
					resultCat.put(itemEntry.getKey(), resultEntry + itemEntry.getValue());
				}
			}
		}
		return result;
	}

	/** Returns a two dimensional map of category pairs and their co-occurrence
	 *  frequencies for the given annotation item. */
	//Category x Category -> #
	public static Map<Object, Map<Object, Double>>
			countCategoryCoincidence(final ICodingAnnotationItem item) {
		Map<Object, Map<Object, Double>> result =
				new HashMap<Object, Map<Object, Double>>();
		for (IAnnotationUnit unit1 : item.getUnits()) {
            for (IAnnotationUnit unit2 : item.getUnits()) {
				if (unit1 == unit2) {
                    continue;
                }

				Object category1 = unit1.getCategory();
				Object category2 = unit2.getCategory();
				if (category1 == null || category2 == null) {
                    continue;
                }

				Map<Object, Double> cat1 = result.get(category1);
				if (cat1 == null) {
					cat1 = new HashMap<Object, Double>();
					result.put(category1, cat1);
				}

				Double value = cat1.get(category2);
				if (value == null) {
                    cat1.put(category2, 1.0);
                }
                else {
                    cat1.put(category2, value + 1.0);
                }
			}
        }

		int raterCount = item.getRaterCount();
		for (Map<Object, Double> cat2 : result.values()) {
            for (Entry<Object, Double> entry : cat2.entrySet()) {
                cat2.put(entry.getKey(), entry.getValue() / (raterCount - 1));
            }
        }

		return result;
	}

}
