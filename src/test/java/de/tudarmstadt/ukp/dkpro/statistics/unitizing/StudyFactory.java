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
package de.tudarmstadt.ukp.dkpro.statistics.unitizing;


/**
 * Factory containing some testing methods that create UnitizingStudies.
 *  
 * @author Christian Stab
 */
public class StudyFactory
{

    /**
     * This Method returns an UnitizingStudy according to the examples 
     * provided in Krippendorf (2004): Measuring the Reliability of 
     * Qualitative Text Analysis Data
     * @return Unitizing study containing the sections of two annotators. 
     */
    public static UnitizingStudy getKrippendorfSampleStudy()
    {
        final UnitizingStudy study = new UnitizingStudy(2);
        study.setContinuumStart(150);
        // observer i is annotator 0
        // observer j is annotator 1

        // study.addSection("c", 0, 150, 75, 0);
        study.addSection("c", 0, 225, 70);
        // study.addSection("c", 0, 295, 75, 0);
        study.addSection("c", 0, 370, 30);
        // study.addSection("c", 0, 400, 50, 0);

        // study.addSection("c", 1, 150, 70, 0);
        study.addSection("c", 1, 220, 80);
        // study.addSection("c", 1, 300, 55, 0);
        study.addSection("c", 1, 355, 20);
        // study.addSection("c", 1, 375, 25, 0);
        study.addSection("c", 1, 400, 20);
        // study.addSection("c", 1, 420, 30, 0);

        // study.addSection("k", 0, 150, 30, 0);
        study.addSection("k", 0, 180, 60);
        // study.addSection("k", 0, 240, 60, 0);
        study.addSection("k", 0, 300, 50);
        // study.addSection("k", 0, 350, 100, 0);

        // study.addSection("k", 1, 150, 30, 0);
        study.addSection("k", 1, 180, 60);
        // study.addSection("k", 1, 240, 60, 0);
        study.addSection("k", 1, 300, 50);
        // study.addSection("k", 1, 350, 100, 0);

        study.setContinuumLength(300);
        study.close();

        return study;
    }
}