package de.d3web.kernel.psMethods.SCMCBR.similarity;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.CaseQuestion;

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
