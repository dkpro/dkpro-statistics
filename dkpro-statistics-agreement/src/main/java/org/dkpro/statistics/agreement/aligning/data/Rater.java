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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class Rater
    implements Comparable<Rater>
{
    private final String name;
    private final int index;

    public Rater(String aName, int aIndex)
    {
        name = aName;
        index = aIndex;
    }

    public String getName()
    {
        return name;
    }

    public int getIndex()
    {
        return index;
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

        if (name != null) {
            return name.equals(((Rater) o).name);
        }
        else {
            return ((Rater) o).name == null;
        }
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public int compareTo(Rater aOther)
    {
        return name.compareTo(aOther.name);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).appendSuper(super.toString())
                .append("name", name).append("index", index).toString();
    }
}
