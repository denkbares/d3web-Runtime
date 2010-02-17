package de.d3web.costBenefit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.costBenefit.model.Target;

/**
 * The DefaultTargetFunction createsone target of each QContainer, which
 * contains a relevant question. Multitargets are not created.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultTargetFunction implements TargetFunction {

	@Override
	public Collection<Target> getTargets(XPSCase theCase,
			Collection<Question> relevantQuestions,
			Collection<Diagnosis> diagnosisToDiscriminate) {
		Set<Target> set = new HashSet<Target>();
		for (Question q : relevantQuestions) {
			if (!q.isDone(theCase))
				addParentContainers(set, q);
		}
		return set;
	}

	private static void addParentContainers(Set<Target> targets, NamedObject q) {
		for (NamedObject qaset : q.getParents()) {
			if (qaset instanceof QContainer) {
				targets.add(new Target((QContainer) qaset));
			} else {
				addParentContainers(targets, qaset);
			}
		}

	}
}
