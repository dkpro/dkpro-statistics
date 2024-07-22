/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Original source: https://github.com/fab-bar/TextGammaTool.git
 */
package org.dkpro.statistics.agreement.aligning;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.dkpro.statistics.agreement.aligning.data.Rater;

public class AlignableAnnotationUnit
    implements IAlignableAnnotationUnit
{
    private static final long serialVersionUID = 8646000607502108382L;

    private final Rater rater;
    private final String type;
    private final Map<String, String> features = new HashMap<String, String>();

    private final long begin;
    private final long end;

    public AlignableAnnotationUnit(Rater creator, int beg, int end)
    {
        this(creator, null, beg, end, null);
    }

    public AlignableAnnotationUnit(Rater creator, int beg, int end, Map<String, String> featureset)
    {
        this(creator, null, beg, end, featureset);
    }

    public AlignableAnnotationUnit(Rater aCreator, long aBegin, long aEnd,
            Map<String, String> aFeatures)
    {
        this(aCreator, NO_TYPE, aBegin, aEnd, null);
    }

    public AlignableAnnotationUnit(Rater aRater, String aType, long aBegin, long aEnd,
            Map<String, String> aFeatures)
    {
        if (aRater == null) {
            rater = new Rater("", -1);
        }
        else {
            rater = aRater;
        }

        if (aType == null) {
            type = NO_TYPE;
        }
        else {
            type = aType;
        }

        if (aBegin >= aEnd) {
            throw new IllegalArgumentException("Begin has to be smaller than end.");
        }

        begin = aBegin;
        end = aEnd;

        if (aFeatures != null) {
            features.putAll(aFeatures);
        }
    }

    public Rater getRater()
    {
        return rater;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public Object getCategory()
    {
        return getFeatures();
    }

    @Override
    public long getBegin()
    {
        return begin;
    }

    @Override
    public long getEnd()
    {
        return end;
    }

    @Override
    public int getRaterIdx()
    {
        return rater.getIndex();
    }

    public Set<String> getFeatureNames()
    {
        return features.keySet();
    }

    public String getFeatureValue(String attribute)
    {
        return features.get(attribute);
    }

    public Map<String, String> getFeatures()
    {
        return unmodifiableMap(features);
    }

    public boolean isCoextensive(AlignableAnnotationUnit aOther)
    {
        return (getBegin() == aOther.getBegin()) && (getEnd() == aOther.getEnd());
    }

    public boolean overlaps(AlignableAnnotationUnit aOther)
    {
        return !(aOther.getEnd() <= getBegin() || aOther.getBegin() >= getEnd());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        AlignableAnnotationUnit seg = (AlignableAnnotationUnit) o;
        // same type?
        if (!Objects.equals(getType(), seg.getType())) {
            return false;
        }

        // same creator?
        if (!Objects.equals(getRater(), seg.getRater())) {
            return false;
        }

        // same span?
        if (!this.isCoextensive(seg)) {
            return false;
        }

        // same attributes?
        if (!this.getFeatureNames().equals(seg.getFeatureNames())) {
            return false;
        }
        // same attribute values?
        for (String attribute : this.getFeatureNames()) {
            if (!Objects.equals(this.getFeatureValue(attribute), seg.getFeatureValue(attribute))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int compareTo(IAlignableAnnotationUnit aOther)
    {
        if (aOther == null) {
            return -1;
        }

        if (this.equals(aOther)) {
            return 0;
        }

        // first: start offset
        if (this.getBegin() < aOther.getBegin()) {
            return -1;
        }
        if (this.getBegin() > aOther.getBegin()) {
            return 1;
        }

        // second: end offset
        if (this.getEnd() < aOther.getEnd()) {
            return -1;
        }
        if (this.getEnd() > aOther.getEnd()) {
            return 1;
        }

        if (!(aOther instanceof AlignableAnnotationUnit)) {
            return -1;
        }

        AlignableAnnotationUnit other = (AlignableAnnotationUnit) aOther;

        // sort by Type
        if (!Objects.equals(getType(), other.getType())) {
            if (getType() != null && other.getType() != null) {
                return getType().compareTo(other.getType());
            }
            else if (getType() == null) {
                return -1;
            }
            else {
                return 1;
            }
        }

        // sort by Creator
        if (!Objects.equals(getRater(), other.getRater())) {
            if (this.getRater() != null && other.getRater() != null) {
                return this.getRater().getName().compareTo(other.getRater().getName());
            }
            else if (this.getRater() == null) {
                return -1;
            }
            else {
                return 1;
            }
        }

        // sort by number of attributes
        if (this.getFeatureNames().size() != other.getFeatureNames().size()) {
            return Integer.compare(this.getFeatureNames().size(), other.getFeatureNames().size());
        }

        // sort by attributes names
        List<String> attributelistX = new ArrayList<String>(this.getFeatureNames());
        sort(attributelistX);

        List<String> attributelistY = new ArrayList<String>(other.getFeatureNames());
        sort(attributelistY);

        if (!attributelistX.equals(attributelistY)) {
            return String.join("", attributelistX).compareTo(String.join("", attributelistY));
        }

        // sort by attribute values (in order of names)
        for (int i = 0; i < attributelistX.size(); i++) {
            String attr = attributelistX.get(i);
            if (!this.getFeatureValue(attr).equals(other.getFeatureValue(attr))) {
                return this.getFeatureValue(attr).compareTo(other.getFeatureValue(attr));
            }
        }

        // annotations are equal
        // (but one is instantiation of subclass)
        return 0;

    }

    public AlignableAnnotationUnit cloneWithDifferentLabel(String aType, String aLabel)
    {
        var feat = new HashMap<>(getFeatures());
        feat.put(aType, aLabel);
        return new AlignableAnnotationUnit(rater, aType, begin, end, feat);
    }

    public AlignableAnnotationUnit cloneWithDifferentOffsets(long aBegin, long aEnd)
    {
        return new AlignableAnnotationUnit(rater, type, aBegin, aEnd, features);

    }

    public AlignableAnnotationUnit cloneWithDifferentRater(Rater aRater)
    {
        return new AlignableAnnotationUnit(aRater, type, begin, end, features);
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += getRater().hashCode();
        hash += getType().hashCode();
        hash += features.hashCode();
        hash += getBegin() + getEnd();

        return hash;
    }

    @Override
    public String toString()
    {
        return this.toString(new ArrayList<String>());
    }

    public String toString(List<String> attributes)
    {
        var ret = new StringBuilder();
        ret.append(String.valueOf(getBegin()));
        ret.append("-");
        ret.append(String.valueOf(getEnd()));
        for (String attribute : attributes) {
            ret.append("\t");
            if (this.getFeatureValue(attribute) != null) {
                ret.append(this.getFeatureValue(attribute));
            }
            else {
                ret.append("--");
            }
        }
        return ret.toString();
    }
}
