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

import java.util.Collection;
import java.util.List;

import org.dkpro.statistics.agreement.aligning.AlignableAnnotationUnit;

public class AnnotatedText
    extends AnnotationSet
{
    private final String text;

    public AnnotatedText(String aText, Collection<AlignableAnnotationTextUnit> aUnits)
    {
        super(aUnits);

        if (lowestOffset < 0) {
            throw new IndexOutOfBoundsException("Lowest offset < 0");
        }

        if (highestOffset > aText.length()) {
            throw new IndexOutOfBoundsException("Highest offset > length of text");
        }

        text = aText;
    }
    
    @Override
    public void addUnit(AlignableAnnotationUnit aUnit)
    {
        if (!(aUnit instanceof AlignableAnnotationTextUnit)) {
            throw new IllegalArgumentException("AnnotatedText only accepts AlignableAnnotationTextUnit");
        }
        
        super.addUnit(aUnit);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<AlignableAnnotationTextUnit> getTextUnits()
    {
        return (List<AlignableAnnotationTextUnit>) (List) super.getUnits();
    }

    public String getText()
    {
        return text;
    }
}
