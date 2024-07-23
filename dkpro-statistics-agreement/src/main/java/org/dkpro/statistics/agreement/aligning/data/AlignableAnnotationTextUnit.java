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
package org.dkpro.statistics.agreement.aligning.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

public class AlignableAnnotationTextUnit
    extends AlignableAnnotationUnit
{
    public static final String TYPE = "textunit";

    private static final long serialVersionUID = 1093784487189619115L;

    protected final String text;

    public static AlignableAnnotationTextUnit textUnit(Rater aRater, long aBegin, long aEnd,
            String aText)
    {
        return new AlignableAnnotationTextUnit(aRater, aBegin, aEnd, aText);
    }

    public static AlignableAnnotationTextUnit textUnit(Rater aRater, long aBegin, long aEnd,
            String aText, Map<String, String> aFeatureSet)
    {
        return new AlignableAnnotationTextUnit(aRater, aBegin, aEnd, aText, aFeatureSet);
    }

    public AlignableAnnotationTextUnit(Rater aRater, long aBegin, long aEnd, String aText)
    {
        this(aRater, aBegin, aEnd, aText, null);
    }

    public AlignableAnnotationTextUnit(Rater aRater, long aBegin, long aEnd, String aText,
            Map<String, String> aFeatureSet)
    {
        super(aRater, TYPE, aBegin, aEnd, aFeatureSet);

        if (aText == null) {
            aText = "";
        }

        text = aText;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public AlignableAnnotationTextUnit cloneWithDifferentLabel(String aType, String aLabel)
    {
        var feat = new HashMap<>(getFeatures());
        feat.put(aType, aLabel);
        return new AlignableAnnotationTextUnit(getRater(), getBegin(), getEnd(), text, feat);
    }

    @Override
    public AlignableAnnotationTextUnit cloneWithDifferentOffsets(long aBegin, long aEnd)
    {
        return new AlignableAnnotationTextUnit(getRater(), aBegin, aEnd, text, getFeatures());
    }

    @Override
    public AlignableAnnotationTextUnit cloneWithDifferentRater(Rater aCreator)
    {
        return new AlignableAnnotationTextUnit(aCreator, getBegin(), getEnd(), text, getFeatures());
    }

    public AlignableAnnotationTextUnit cloneWithDifferentText(String aText)
    {
        return new AlignableAnnotationTextUnit(getRater(), getBegin(), getEnd(), aText,
                getFeatures());
    }

    @Override
    public String toString(List<String> attributes)
    {
        var ret = new StringBuilder();
        ret.append(this.getText());

        for (var attribute : attributes) {
            ret.append("\t");
            if (getFeatureValue(attribute) != null) {
                ret.append(getFeatureValue(attribute));
            }
            else {
                ret.append("--");
            }
        }

        return ret.toString();
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof AlignableAnnotationTextUnit)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }

        AlignableAnnotationTextUnit castOther = (AlignableAnnotationTextUnit) other;
        return Objects.equals(text, castOther.text);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), text);
    }
}
