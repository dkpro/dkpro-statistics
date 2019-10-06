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

/**
 * Default implementation of {@link IAnnotationUnit} holding the rater's index and the category that
 * the rater assigned to this unit.
 * 
 * @see IAnnotationStudy
 * @see IAnnotationUnit
 * @author Christian M. Meyer
 */
public class AnnotationUnit
    implements IAnnotationUnit
{
    private static final long serialVersionUID = 4277733312128063453L;
    
    protected int raterIdx;
    protected Object category;

    /**
     * Initializes the annotation unit with the given category as the annotation by the rater with
     * the specified index.
     */
    public AnnotationUnit(int raterIdx, final Object category)
    {
        this.raterIdx = raterIdx;
        this.category = category;
    }

    @Override
    public int getRaterIdx()
    {
        return raterIdx;
    }

    @Override
    public Object getCategory()
    {
        return category;
    }

    @Override
    public String toString()
    {
        return raterIdx + "<" + category + ">";
    }
}
