package de.tudarmstadt.ukp.dkpro.statistics.unitizing;

import de.tudarmstadt.ukp.dkpro.statistics.unitizing.UnitizingStudy;

/**
 * Factory containing some testing methods that create UnitizingStudies.
 *  
 * @author Christian Stab
 */
public class StudyFactory {

	/**
	 * This Method returns an UnitizingStudy according to the examples 
	 * provided in Krippendorf (2004): Measuring the Reliability of 
	 * Qualitative Text Analysis Data
	 * @return Unitizing study containing the sections of two annotators. 
	 */
	public static UnitizingStudy getStudy() {
		UnitizingStudy study = new UnitizingStudy(2);
		// observer i is annotator 1
		// observer j is annotator 2
		
		study.addSection("c", 0, 150, 75, 0);
		study.addSection("c", 0, 225, 70, 1);
		study.addSection("c", 0, 295, 75, 0);
		study.addSection("c", 0, 370, 30, 1);
		study.addSection("c", 0, 400, 50, 0);
		study.addSection("c", 1, 150, 70, 0);
		study.addSection("c", 1, 220, 80, 1);
		study.addSection("c", 1, 300, 55, 0);
		study.addSection("c", 1, 355, 20, 1);
		study.addSection("c", 1, 375, 25, 0);
		study.addSection("c", 1, 400, 20, 1);
		study.addSection("c", 1, 420, 30, 0);
		study.addSection("k", 0, 150, 30, 0);
		study.addSection("k", 0, 180, 60, 1);
		study.addSection("k", 0, 240, 60, 0);
		study.addSection("k", 0, 300, 50, 1);
		study.addSection("k", 0, 350, 100, 0);
		study.addSection("k", 1, 150, 30, 0);
		study.addSection("k", 1, 180, 60, 1);
		study.addSection("k", 1, 240, 60, 0);
		study.addSection("k", 1, 300, 50, 1);
		study.addSection("k", 1, 350, 100, 0);
		
		study.setL(300);
		return study;
	}
	
}
