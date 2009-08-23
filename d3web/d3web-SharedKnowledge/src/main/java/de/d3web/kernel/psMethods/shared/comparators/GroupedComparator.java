/*
 * Created on 28.07.2003
 */
package de.d3web.kernel.psMethods.shared.comparators;

import de.d3web.kernel.domainModel.answers.AnswerChoice;

/**
 * Marks a question-comparator as grouped.
 * 
 * @author Tobias Vogele
 */
public interface GroupedComparator {

	void addPairRelation(AnswerChoice ans1, AnswerChoice ans2, double value);
	
	void addPairRelation(AnswerChoice ans1, AnswerChoice ans2);

	double getPairRelationValue(AnswerChoice ans1, AnswerChoice ans2);
	
}
