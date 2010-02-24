package de.d3web.costBenefit;

import java.util.Collection;

import de.d3web.core.inference.StrategicSupport;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.Question;
import de.d3web.costBenefit.model.Target;

/**
 * A TargetFunction calculates the targets for a SearchAlgorithm based on
 * relevant questions and diagnosis.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface TargetFunction {

	/**
	 * Returns a collection of targets. These targets contain relevantQuestions
	 * to discriminate the diagnosis.
	 * 
	 * @param theCase
	 * @param relevantQuestions
	 * @param diagnosisToDiscriminate
	 * @return
	 */
	Collection<Target> getTargets(XPSCase theCase,
			Collection<Question> relevantQuestions,
			Collection<Diagnosis> diagnosisToDiscriminate, StrategicSupport strategicSupport);
}
