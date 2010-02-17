package de.d3web.core.inference;

import java.util.Collection;

import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.Question;

public interface StrategicSupport {
	
//	public Map<QContainer, Double> computeBestQuestions(double percentOfMax, XPSCase theCase);
//	TODO sollte die Methode nicht zu PSMethod gehören? Sollte der Problemlöser diese Diagnosen nicht auf mindestens suggested bewerten (beim XCL unabhängig vom Schwelwert) -> Methode überflüssig
	Collection<Diagnosis> getPossibleDiagnoses(XPSCase theCase);
	Collection<Question> getDiscriminatingQuestions(Collection<Diagnosis> solutions, XPSCase theCase);
	double getEntropy(Collection<? extends QASet> qasets, Collection<Diagnosis> solutions, XPSCase theCase);
}
