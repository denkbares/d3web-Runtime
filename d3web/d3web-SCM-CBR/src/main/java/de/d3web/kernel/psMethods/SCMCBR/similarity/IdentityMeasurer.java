package de.d3web.kernel.psMethods.SCMCBR.similarity;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;
import de.d3web.core.session.XPSCase;

public class IdentityMeasurer implements ISimilarityMeasurer {
	private final Value expectedValue;
	private final Question question;
	
	public IdentityMeasurer(Question question, Value expectedValue) {
		this.expectedValue = expectedValue;
		this.question = question;
	}

	@Override
	public double computeSimilarity(XPSCase theCase) {
		Value value = question.getValue(theCase);
		if (value.equals(expectedValue)) // TODO Fix
			return 1;
		else
			return 0;
	}

}
