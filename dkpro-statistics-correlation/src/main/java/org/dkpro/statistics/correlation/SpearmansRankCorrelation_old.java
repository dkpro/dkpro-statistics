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
package org.dkpro.statistics.correlation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO this seems to produce wrong Spearman values => replace with more reliable method
public class SpearmansRankCorrelation_old
{
    private Map<String, ValuesAndRanks> data;
    private int maxTiedRanksBeforeCorrection;
    private boolean sortDescending;

    public static final int DEFAULT_MAX_TIED_RANKS_BEFORE_CORRELATION = 0;
    public static final boolean DEFAULT_SORT_DESCENDING = false;

    public SpearmansRankCorrelation_old(final int maxTiedRanksBeforeCorrection,
            final boolean sortDescending)
    {
        data = new HashMap<String, ValuesAndRanks>();
        this.maxTiedRanksBeforeCorrection = maxTiedRanksBeforeCorrection;
        this.sortDescending = sortDescending;
    }

    public SpearmansRankCorrelation_old(final boolean sortDescending)
    {
        data = new HashMap<String, ValuesAndRanks>();
        this.maxTiedRanksBeforeCorrection = DEFAULT_MAX_TIED_RANKS_BEFORE_CORRELATION;
        this.sortDescending = sortDescending;
    }

    public SpearmansRankCorrelation_old()
    {
        data = new HashMap<String, ValuesAndRanks>();
        this.maxTiedRanksBeforeCorrection = DEFAULT_MAX_TIED_RANKS_BEFORE_CORRELATION;
        this.sortDescending = DEFAULT_SORT_DESCENDING;
    }

    /**
     * Computes the correlation between two datasets.
     * @param list1 The first dataset as a list.
     * @param list2 The second dataset as a list.
     * @return The correlation between the two datasets.
     */
    public static double computeCorrelation(final List<Double> list1, final List<Double> list2)
    {
        final SpearmansRankCorrelation_old src = new SpearmansRankCorrelation_old();
        for (int i = 0; i < list1.size(); i++) {
            src.addXValue(new Integer(i).toString(), list1.get(i));
            src.addYValue(new Integer(i).toString(), list2.get(i));
            i++;
        }
        return src.calculate();
    }

    public void addXValue(final String id, final double value)
    {
        ValuesAndRanks vr = data.get(id);
        if (vr == null) {
            vr = new ValuesAndRanks();
            data.put(id, vr);
        }
        vr.xData = value;
    }

    public void addYValue(final String id, final double value)
    {
        ValuesAndRanks vr = data.get(id);
        if (vr == null) {
            vr = new ValuesAndRanks();
            data.put(id, vr);
        }
        vr.yData = value;
    }

    public void clear()
    {
        data.clear();
    }

    public double calculate()
    {
        final Collection<ValuesAndRanks> values = data.values();
        final List<ValuesAndRanks> valueList = new ArrayList<ValuesAndRanks>(values);
        int nTiedRanks = 0;
        double xCorrection = 0;
        double yCorrection = 0;

        final int nDataValues = values.size();

        final Correction xCorrValue = setRanks(ValuesAndRanks.X, valueList);
        xCorrection = xCorrValue.correctionValue;
        nTiedRanks += xCorrValue.nTiedRanks;
        final Correction yCorrValue = setRanks(ValuesAndRanks.Y, valueList);
        yCorrection = yCorrValue.correctionValue;
        nTiedRanks += yCorrValue.nTiedRanks;

        double d = 0;
        for (final ValuesAndRanks vr : valueList) {
            d += Math.pow(vr.xRank - vr.yRank, 2);
        }

        double coef = 0;

        if (nTiedRanks > maxTiedRanksBeforeCorrection) {
            // use correction

            final double tmp = (Math.pow(nDataValues, 3) - nDataValues) / 12;
            xCorrection = tmp - xCorrection;
            yCorrection = tmp - yCorrection;

            coef = (xCorrection + yCorrection - d) / (2 * Math.sqrt(xCorrection * yCorrection));
        }
        else {
            coef = 1 - ((6 * d) / (Math.pow(nDataValues, 3) - nDataValues));
        }

        return coef;
    }

    private Correction setRanks(final int dataType, final List<ValuesAndRanks> valueList)
    {
        final Correction correction = new Correction();
        Collections.sort(valueList, new Comp(dataType, sortDescending));

        int start = 0;
        int end = 0;
        int rank = 1;
        while (start < valueList.size()) {
            end = start;
            int sumRank = rank;
            int tmp = start + 1;
            int tmpRank = rank;
            final Object startValue = valueList.get(start).getData(dataType);
            while (tmp < valueList.size()) {
                if (!startValue.equals(valueList.get(tmp).getData(dataType))) {
                    break;
                }
                tmpRank++;
                sumRank += tmpRank;
                end++;
                tmp++;
            }
            final int ntr = end - start + 1;
            final double realRank = (double) sumRank / (double) ntr;

            // calculate correction factor and add it ( (t^3-t)/12 )
            if (ntr > 1) {
                correction.correctionValue += (Math.pow(ntr, 3) - ntr) / 12;
                correction.nTiedRanks += ntr - 1;
            }

            for (int i = start; i <= end; i++) {
                valueList.get(i).setRank(dataType, realRank);
            }

            rank = rank + (end - start) + 1;
            start = end + 1;
        }
        return correction;
    }

    private class ValuesAndRanks
    {
        private static final int X = 1;
        private static final int Y = 2;

        public Object xData;
        public Object yData;
        public double xRank;
        public double yRank;

        // public double getRank(final int dataType)
        // {
        // double result = 0;
        // switch (dataType) {
        // case X:
        // result = xRank;
        // break;
        // case Y:
        // result = yRank;
        // break;
        // default:
        // throw new IllegalArgumentException("illegal data type: " + dataType);
        // }
        // return result;
        // }

        public Object getData(final int dataType)
        {
            Object result = 0;
            switch (dataType) {
            case X:
                result = xData;
                break;
            case Y:
                result = yData;
                break;
            default:
                throw new IllegalArgumentException("illegal data type: " + dataType);
            }
            return result;
        }

        public void setRank(final int dataType, final double rank)
        {
            switch (dataType) {
            case X:
                xRank = rank;
                break;
            case Y:
                yRank = rank;
                break;
            default:
                throw new IllegalArgumentException("illegal data type: " + dataType);
            }

        }

        // public void setData(final int dataType, final Object data)
        // {
        // switch (dataType) {
        // case X:
        // xData = data;
        // break;
        // case Y:
        // yData = data;
        // break;
        // default:
        // throw new IllegalArgumentException("illegal data type: " + dataType);
        // }
        //
        // }

        @Override
        public String toString()
        {
            final StringBuilder buf = new StringBuilder();
            buf.append("x: ");
            buf.append(xData);
            buf.append(" | rx: ");
            buf.append(xRank);
            buf.append(" | y: ");
            buf.append(yData);
            buf.append(" | ry: ");
            buf.append(yRank);
            return buf.toString();
        }
    }

    private class Comp
        implements Comparator<ValuesAndRanks>
    {
        private boolean sortDescending;
        private int dataType;

        public Comp(final int dataType, final boolean sortDescending)
        {
            this.dataType = dataType;
            this.sortDescending = sortDescending;
        }

        @Override
        public int compare(final ValuesAndRanks o1, final ValuesAndRanks o2)
        {
            final Object data1 = o1.getData(dataType);
            final Object data2 = o2.getData(dataType);

            int result = 0;
            if (sortDescending) {
                result = ((Comparable) data2).compareTo(data1);
            }
            else {
                result = ((Comparable) data1).compareTo(data2);
            }
            return result;
        }

    }

    private class Correction
    {
        public double correctionValue;
        public int nTiedRanks;
    }
}
