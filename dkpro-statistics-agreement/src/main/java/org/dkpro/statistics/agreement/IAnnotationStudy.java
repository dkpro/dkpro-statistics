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
 * Basic interface representing the essential data model of any annotation
 * study. The data model provides access to the raters participating
 * in the annotation study, the set of categories a rater may use, and the
 * annotation units. Each unit represents a single annotation (i.e., the
 * decision of a single rater to assign one of the categories to a predefined
 * or rater-defined unit). The interface acts as the common ground for any
 * inter-rater agreement measure, whereas its two fundamental extensions -
 * the coding and the unitizing study interfaces - provide further interface
 * methods necessary for calculating individual measures.
 * @see IAgreementMeasure
 * @see IAnnotationUnit
 * @author Christian M. Meyer
 */
public interface IAnnotationStudy {
    
    // -- Raters --

    /** Returns the number of raters participating in this study. */
    public int getRaterCount();
    
    // -- Categories --

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
    
    /** Returns true if, and only if, the categories defined by the study yield
     *  a dichotomy (i.e., there are exactly two categories). */
    public boolean isDichotomous();
    
    // -- Units --
    
//    public Iterable<? extends IAnnotationUnit> getUnits();
    
    /** Returns the number of annotation units defined by the study. That is, the
     *  number of annotations coded by the raters. */
    public int getUnitCount();

}
