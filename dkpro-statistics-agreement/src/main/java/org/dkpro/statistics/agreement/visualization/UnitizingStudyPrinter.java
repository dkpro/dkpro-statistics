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
package org.dkpro.statistics.agreement.visualization;

import java.io.PrintStream;

import org.dkpro.statistics.agreement.unitizing.IUnitizingAnnotationStudy;
import org.dkpro.statistics.agreement.unitizing.IUnitizingAnnotationUnit;

/**
 * Plain-text visualization for unitizing studies. The visualization prints 
 * the continuum and marks the units of each individual rater and for each 
 * category using asterisks. For category A of Krippendorff's (1995) example, 
 * the implementation prints, for instance: <pre>               1         2   2
 * r    0123456789012345678901234
 * 0      ********    ******    
 * 1        ****       **</pre>
 * That is, a continuum starting at 0, ending at 24, in which two raters 
 * identified two units. By defining a {@link PrintStream}, it is possible to
 * display a study on the console or to write the results to a text file.<br><br>
 * References:<ul>
 * <li>Krippendorff, K.: On the reliability of unitizing contiguous data.
 *   Sociological Methodology 25:47–76, 1995.</li></ul>
 * @see IUnitizingAnnotationStudy
 * @author Christian M. Meyer
 */
public class UnitizingStudyPrinter {
    
    /** Prints an integer scale of the continuum used for the given unitzing
     *  study. Optionally, the given prefix is printed left of the continuum
     *  scale and the output is correctly indented. */
    public void printContinuum(final PrintStream out, 
            final IUnitizingAnnotationStudy study, String prefix) {
        long B = study.getContinuumBegin();
        long L = study.getContinuumLength();
        String[] p = new String[]{"", ""};
        if (prefix != null) {
            p[0] = prefix;
            StringBuilder s = new StringBuilder();
            for (char ch : prefix.toCharArray())
                if (Character.isWhitespace(ch))
                    s.append(ch);
                else
                    s.append(" ");
            p[1] = s.toString();
        }
        int digits = (int) Math.ceil(Math.log(B + L) / Math.log(10));
        char[][] scale = new char[digits][(int) L + 1];
        for (int i = 0; i < digits; i++)
            for (int j = 0; j <= L; j++)
                scale[i][j] = ' ';
        for (int i = 0; i <= L; i++) {
            int idx = 0;
            long pos = B + i;
            do {
                long lastDigit = pos % 10;
                scale[idx++][i] = (char) (lastDigit + '0');
                if (lastDigit == 0 || i == 0 || i == L)
                    pos = pos / 10;
                else
                    break;
            } while (pos > 0);
        }        
        for (int j = digits - 1; j >= 0; j--) {
            out.print(j == 0 ? p[0] : p[1]);
            out.println(scale[j]);
        }
    }
    
    /** Identifies all annotation units of the given rater which have been
     *  coded with the given category and marks their boundaries using
     *  asterisks. The scale is compatible to {@link #printContinuum(
     *  PrintStream, IUnitizingAnnotationStudy, String)}. Optionally, the 
     *  given prefix is printed left of the continuum scale and the output 
     *  is correctly indented. */
    public void printUnitsForRater(final PrintStream out, 
            final IUnitizingAnnotationStudy study, int raterIdx, 
            final Object category, final String prefix) {
        long B = study.getContinuumBegin();
        long L = study.getContinuumLength();
        char[] annotations = new char[(int) L];
        for (int i = 0; i < L; i++)
            annotations[i] = ' ';
        for (IUnitizingAnnotationUnit unit : study.getUnits())
            if (unit.getRaterIdx() == raterIdx && unit.getCategory() == category)
                for (int i = 0; i < unit.getLength(); i++)
                    annotations[i + (int) unit.getOffset() - (int) B] = '*';
        out.print(prefix);
        out.println(annotations);
    }

    /** Iterate all raters, identify the corresponding annotation units, and
     *  visualize them using {@link #printUnitsForRater(PrintStream, 
     *  IUnitizingAnnotationStudy, int, Object, String)}. Optionally, the given 
     *  prefix is printed left of the continuum scale and the output is 
     *  correctly indented. */
    public void printUnitsForCategory(final PrintStream out, 
            final IUnitizingAnnotationStudy study, final Object category,
            final String prefix) {
        for (int raterIdx = 0; raterIdx < study.getRaterCount(); raterIdx++) 
            printUnitsForRater(out, study, raterIdx, category, prefix + raterIdx + "\t");
    }
    
    /** Print a plain-text representation of the given unitizing study. */
    public void print(final PrintStream out, 
            final IUnitizingAnnotationStudy study) {
        printContinuum(out, study, "c\tr\t");
        for (Object category : study.getCategories())
            printUnitsForCategory(out, study, category, category + "\t");
    }
    
}
