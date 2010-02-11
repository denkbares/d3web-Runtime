package de.d3web.kernel.psMethods.SCMCBR.similarity;

import java.util.List;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestion;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Question;

public class IdentityMeasurer implements ISimilarityMeasurer {

	
	private final Answer rightAnswer;
	private final Question question;
	
	
	
	/**
	 * @param rightAnswer
	 */
	public IdentityMeasurer(Question question, Answer rightAnswer) {
		this.rightAnswer = rightAnswer;
		this.question = question;
	}


	@Override
	public double computeSimilarity(XPSCase theCase) {
		
		CaseQuestion caseQuestino = (CaseQuestion) theCase.getCaseObject(question);
		
		List value = question.getValue(theCase);
		
		if (value.equals(rightAnswer)) //TODO Fix
			return 1;
		else 
			return 0;
	}

}
