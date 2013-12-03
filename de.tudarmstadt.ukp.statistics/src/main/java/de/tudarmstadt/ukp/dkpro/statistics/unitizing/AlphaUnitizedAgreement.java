package de.tudarmstadt.ukp.dkpro.statistics.unitizing;

import java.util.List;

import de.tudarmstadt.ukp.dkpro.statistics.unitizing.UnitizingStudy.Section;


/**
 * Calculates the unitized alpha coefficient described in Krippendorf
 * (2004) "Measuring the Reliability of Qualitative Text Analysis Data" 
 * The agreement can either be calculated for a certain category or 
 * for all categories. The coefficient can be used for two or more coders.
 * 
 * @author Christian Stab
 */
public class AlphaUnitizedAgreement {

	
	/** 
	 * The study object containing the sections of each coder 
	 */
	private UnitizingStudy study;
	
	
	/**
	 * Basic constructor
	 * @param study the study object containing the sections of each coder
	 */
	public AlphaUnitizedAgreement(UnitizingStudy study) {
		this.study = study;
	}
	
	
	/**
	 * Estimates the inter-annotator agreement of the given category  
	 * @param category the category of interest
	 * @return IAA for the given category
	 */
	public double estimateCategoryAgreement(String category) {
		return 1- (getObservedDisagreement(category)/getExpectedDisagreement(category));
	}
	
	
	/**
	 * Estimates the joint agreement for all categories given in the study
	 * @return joint agreement for all given categories
	 */
	public double estimateJointAgreement() {
		List<String> categories = study.getCategories(); 
		double enumerator = 0.0;
		double denominator = 0.0;
		for (String category : categories) {
			enumerator = enumerator + getObservedDisagreement(category);
			denominator = denominator + getExpectedDisagreement(category);
		}
		
		return 1-(enumerator/denominator);
	}
	
	
	/**
	 * Calculates the observed disagreement for the given category
	 * @param category the category of interest
	 * @return observed agreement of the given category
	 */
	public double getObservedDisagreement(String category) {
		double sum = 0.0;
		for (int i=0; i<study.getAnnotators(); i++) {
			for (int g=0; g<study.getSections(category, i).size(); g++) {
				for (int j=0; j<study.getAnnotators(); j++) {
					if (j!=i) {
						for (int h=0; h<study.getSections(category, j).size(); h++) {
							Section u = study.getSection(category, i, g);
							Section v = study.getSection(category, j, h);
							
							sum = sum + delta(u, v);
						}
					}
				}
			}
		}
		
		return sum/(study.getAnnotators()*(study.getAnnotators()-1)*Math.pow(study.getL(),2));
	}

	
	/**
	 * Calculates the expected disagreement for the given category
	 * @param category the category of interest
	 * @return expected agreement of the given category
	 */
	public double getExpectedDisagreement(String category) {
		// calculate number of units of category c
		double nc = 0;
		for (int i=0; i<study.getAnnotators(); i++) {
			for (int g=0; g<study.getSections(category, i).size(); g++) {
				nc = nc + study.getSection(category, i, g).v;
			}
		}
		
		// calculate enumerator
		double enumerator = 0.0;
		for (int i=0; i<study.getAnnotators(); i++) {
			for (int g=0; g<study.getSections(category, i).size(); g++) {
				
				Section cig = study.getSection(category, i, g);
				if (cig.v!=0) {
					double tmp = ((nc-1)/3) * (2*Math.pow(cig.l, 3) - 3*Math.pow(cig.l, 2) + cig.l);
					
					double tmp2 = 0.0;
					for (int j=0; j<study.getAnnotators(); j++) {
						for (int h=0; h<study.getSections(category, j).size(); h++) {
							Section cjh = study.getSection(category, j, h);
							if (cjh.l >= cig.l) {
								tmp2 = tmp2 + (1-cjh.v)*(cjh.l-cig.l+1);
							}
						}
					}
					tmp2 = tmp2*Math.pow(cig.l, 2);
					tmp = tmp + tmp2;
					enumerator = enumerator + cig.v * tmp;
				}
			}
		}
		enumerator = enumerator * (2.0/study.getL());
		
		// calculate denominator
		double denominator = 0.0;
		denominator = (study.getAnnotators()*study.getL())*(study.getAnnotators()*study.getL()-1);
		double tmp = 0.0;
		for (int i=0; i<study.getAnnotators(); i++) {
			for (int g=0; g<study.getSections(category, i).size(); g++) {
				Section cig = study.getSection(category, i, g);
				tmp = tmp + cig.v * cig.l * (cig.l-1);
			}
		}
		denominator = denominator - tmp;
		
		return enumerator / denominator;
	}
	
	
	/**
	 * The delta-function for calculating the distance between two
	 * units.  
	 * @param u section of first annotator
	 * @param v section of second annotator
	 * @return distance between the units
	 */
	private double delta(Section u, Section v) {
		if (!u.category.equals(v.category)) {
			System.out.println("Error: Categories do not match!");
			return -1;
		}
		
		if (u.v==1 && v.v==1 && (-u.l)<(u.b-v.b) && (u.b-v.b)<v.l) {
			return Math.pow((u.b-v.b),2) + Math.pow((u.b+u.l-v.b-v.l),2);
		}
		
		if (u.v==1 && v.v==0 && (v.l-u.l)>=(u.b-v.b) && (u.b-v.b)>=0) {
			return Math.pow(u.l,2);
		}
		
		if (u.v==0 && v.v==1 && (v.l-u.l)<=(u.b-v.b) && (u.b-v.b)<=0) {
			return Math.pow(v.l,2);
		}
		
		return 0;
	}
	
}
