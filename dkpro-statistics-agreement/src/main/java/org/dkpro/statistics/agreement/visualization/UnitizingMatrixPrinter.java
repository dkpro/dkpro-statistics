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
package org.dkpro.statistics.agreement.visualization;

import java.io.PrintStream;
import java.util.Objects;

import org.dkpro.statistics.agreement.unitizing.IUnitizingAnnotationStudy;
import org.dkpro.statistics.agreement.unitizing.IUnitizingAnnotationUnit;

/**
 * Plain-text visualization for unitizing studies. The visualization prints the annotation units of
 * two raters in a matrix form - similar to the matrices used by Krippendorff (1995: p. 62). This
 * type of visualization can be used for comparing the boundaries of the units identified by a pair
 * of raters. By defining a {@link PrintStream}, it is possible to display a study on the console or
 * to write the results to a text file.<br>
 * <br>
 * References:
 * <ul>
 * <li>Krippendorff, K.: On the reliability of unitizing contiguous data. Sociological Methodology
 * 25:47–76, 1995.</li>
 * </ul>
 * 
 * @see IUnitizingAnnotationStudy
 * @author Christian M. Meyer
 */
public class UnitizingMatrixPrinter
{

    /**
     * Print a plain-text representation of the given unitizing study. A matrix can only be printed
     * for two of the raters and for (non-overlapping) units coded with the same category.
     */
    public void print(final PrintStream out, final IUnitizingAnnotationStudy study,
            final Object category, int rater1, int rater2)
    {
        long B = study.getContinuumBegin();
        long L = study.getContinuumLength();

        // Continuum.
        int digits = (int) Math.floor(Math.log(B + L) / Math.log(10) + 1);
        char[] digitSpace = new char[digits];
        for (int i = 0; i < digits; i++) {
            digitSpace[i] = ' ';
        }
        char[][] scale = new char[digits][(int) L + 1];
        for (int i = 0; i < digits; i++) {
            for (int j = 0; j <= L; j++) {
                scale[i][j] = ' ';
            }
        }
        for (int i = 0; i <= L; i++) {
            int idx = 0;
            long pos = B + i;
            do {
                long lastDigit = pos % 10;
                scale[idx++][i] = (char) (lastDigit + '0');
                if (lastDigit == 0 || i == 0 || i == L) {
                    pos = pos / 10;
                }
                else {
                    break;
                }
            }
            while (pos > 0);
        }
        for (int j = digits - 1; j >= 0; j--) {
            out.print(digitSpace);
            out.print("  ");
            out.println(scale[j]);
        }

        // Rater 1.
        char[] annotations1 = new char[(int) L];
        for (int i = 0; i < L; i++) {
            annotations1[i] = ' ';
        }
        for (IUnitizingAnnotationUnit unit : study.getUnits()) {
            if (unit.getRaterIdx() == rater1 && Objects.equals(unit.getCategory(), category)) {
                for (int i = 0; i < unit.getLength(); i++) {
                    annotations1[i + (int) unit.getOffset() - (int) B] = '*';
                }
            }
        }
        out.print(digitSpace);
        out.print("  ");
        out.println(annotations1);

        // Rater 2.
        char[] annotations2 = new char[(int) L];
        for (int i = 0; i < L; i++) {
            annotations2[i] = ' ';
        }
        for (IUnitizingAnnotationUnit unit : study.getUnits()) {
            if (unit.getRaterIdx() == rater2 && Objects.equals(unit.getCategory(), category)) {
                for (int i = 0; i < unit.getLength(); i++) {
                    annotations2[i + (int) unit.getOffset() - (int) B] = '*';
                }
            }
        }

        for (int i = 0; i <= L; i++) {
            for (int j = digits - 1; j >= 0; j--) {
                out.print(scale[j][i]);
            }
            out.print(" ");
            if (i < L) {
                out.print(annotations2[i]);
                for (int k = 0; k < L; k++) {
                    /*
                     * if (i != k) { out.print(' '); continue; }
                     */
                    if (annotations1[k] == '*' && annotations2[i] == '*') {
                        out.print('*');
                    }
                    else if (annotations1[k] == '*' && annotations2[i] == ' ') {
                        out.print('\\');
                    }
                    else if (annotations1[k] == ' ' && annotations2[i] == '*') {
                        out.print('/');
                    }
                    else {
                        out.print('.');
                    }
                }
            }
            out.println();
        }
    }

}
