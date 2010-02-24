package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.Question;

public interface StrategicSupport {
	
	Collection<Diagnosis> getPossibleDiagnoses(XPSCase theCase);
	Collection<Question> getDiscriminatingQuestions(Collection<Diagnosis> solutions, XPSCase theCase);
	double getEntropy(Collection<? extends QASet> qasets, Collection<Diagnosis> solutions, XPSCase theCase);
}
