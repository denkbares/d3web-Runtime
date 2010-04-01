package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.XPSCase;

public interface StrategicSupport {
	
	Collection<Solution> getPossibleDiagnoses(XPSCase theCase);
	Collection<Question> getDiscriminatingQuestions(Collection<Solution> solutions, XPSCase theCase);
	double getEntropy(Collection<? extends QASet> qasets, Collection<Solution> solutions, XPSCase theCase);
}
