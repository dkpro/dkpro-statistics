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
package org.dkpro.statistics.agreement.distance;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a set of categories that can be used as annotation type in a multi-labeling setting.
 * That is, an annotation study allowing the raters to assign multiple elements from a given set of
 * categories to the very same unit. An example is tagging summaries with multiple content units, as
 * it done for the pyramid method (cf. Passonneau, 2006). Another example is relation anchoring
 * (i.e., the task of finding the correct word senses of the source and the target of a semantic
 * relation. In this setting, raters may assign multiple word senses to a relation endpoint (cf.
 * Meyer &amp; Gurevych, 2012).<br>
 * <br>
 * References:
 * <ul>
 * <li>Meyer, C.M. &amp; Gurevych, I.: OntoWiktionary – Constructing an Ontology from the
 * Collaborative Online Dictionary Wiktionary. In: Semi-Automatic Ontology Development: Processes
 * and Resources, p. 131–161, Hershey, PA: IGI Global, 2012.</li>
 * <li>Passonneau, R.: Measuring agreement on set-valued items (MASI) for semantic and pragmatic
 * annotation, in: Proceedings of the Fifth International Conference on Language Resources and
 * Evaluation, p. 831–836, 2006.</li>
 * </ul>
 * 
 * @see IDistanceFunction
 * @see SetAnnotationDistanceFunction
 * @see MASISetAnnotationDistanceFunction
 * @author Christian M. Meyer
 */
public class SetAnnotation
    extends HashSet<Object>
    implements Comparable<SetAnnotation>
{
    /** Instantiates an empty set annotation. */
    public SetAnnotation() {
        super();
    }

    /** Instantiates a set annotation with the given values as set elements. */
    public SetAnnotation(Object... values) {
        super();
        for (Object value : values) {
            add(value);
        }
    }

    /** Instantiates a set annotation with the given values as set elements. */
    public SetAnnotation(Collection<? extends Object> c) {
        super(c);
    }

    @Override
    public int compareTo(final SetAnnotation that) {
        return toString().compareTo(that.toString());
    }

    @Override
    public boolean equals(final Object that)    {
        if (!(that instanceof SetAnnotation)) {
            return false;
        }
        return toString().equals(((SetAnnotation) that).toString());
    }
    
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Object value : this) {
            result.append(result.length() == 0 ? "" : ", ").append(value);
        }
        return result.toString();
    }

}
