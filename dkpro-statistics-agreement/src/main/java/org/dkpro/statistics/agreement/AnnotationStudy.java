/*
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
 */
package org.dkpro.statistics.agreement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of the {@link IAnnotationStudy} interface. Abstract base class for coding
 * and unitizing annotation studies.
 * 
 * @author Christian M. Meyer
 */
public abstract class AnnotationStudy
    implements IAnnotationStudy
{

    protected ArrayList<String> raters;
    protected Set<Object> categories;

    protected AnnotationStudy()
    {
        raters = new ArrayList<String>();
        categories = new LinkedHashSet<Object>();
    }

    /**
     * Add a rater with the given name. Returns the index of the newly added rater which is required
     * to identify the rater in {@link IAnnotationUnit}. The first rater receives index 0. Note that
     * adding names for the raters is optional.
     */
    public int addRater(final String name)
    {
        raters.add(name);
        return (raters.size() - 1);
    }

    /** Find the index of the rater with the given name. */
    public int findRater(final String name)
    {
        return raters.indexOf(name);
    }

    @Override
    public int getRaterCount()
    {
        return raters.size();
    }

    /**
     * Adds the given category to the set of possible annotation labels. This method is only
     * required of a category has not been used by any rater of within the annotation study.
     * 
     * @throws NullPointerException
     *             if the specified category is null.
     */
    public void addCategory(final Object category)
    {
        categories.add(category);
    }

    @Override
    public Iterable<Object> getCategories()
    {
        return categories;
    }

    /*
     * public Object[] getCategoryArray() { Object[] result = null; int idx = 0; for (Object
     * category : getCategories()) { if (result == null) result = new Object[categories.size()];
     * result[idx++] = category; } return result; }
     */

    @Override
    public int getCategoryCount()
    {
        return categories.size();
    }

    @Override
    public boolean isDichotomous()
    {
        return (categories.size() == 2);
    }
}
