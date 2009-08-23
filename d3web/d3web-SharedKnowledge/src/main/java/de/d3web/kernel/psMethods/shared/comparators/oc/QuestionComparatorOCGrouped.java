package de.d3web.kernel.psMethods.shared.comparators.oc;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.psMethods.shared.comparators.GroupedComparator;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;
/**
 * Insert the type's description here.
 * Creation date: (03.08.2001 15:13:05)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparatorOCGrouped extends QuestionComparatorOC implements GroupedComparator {

	protected List pairRelations = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (03.08.2001 15:42:38)
	 */
	public QuestionComparatorOCGrouped() {
		pairRelations = new LinkedList();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (03.08.2001 15:23:14)
	 * @param ans1 de.d3web.kernel.domainModel.answers.AnswerChoice
	 */
	public void addPairRelation(AnswerChoice ans1, AnswerChoice ans2) {
		addPairRelation(ans1, ans2, 1.0);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (03.08.2001 15:23:14)
	 * @param ans1 de.d3web.kernel.domainModel.answers.AnswerChoice
	 */
	public void addPairRelation(AnswerChoice ans1, AnswerChoice ans2, double value) {
		PairRelation rel = new PairRelation(ans1, ans2, value);
		if (pairRelations.contains(rel)) {
			pairRelations.remove(rel);
		}

		pairRelations.add(rel);

	}

	public double getPairRelationValue(AnswerChoice ans1, AnswerChoice ans2) {
		PairRelation rel = new PairRelation(ans1, ans2, 0.0);
		boolean found = pairRelations.contains(rel);
		if (found) {
			int index = pairRelations.indexOf(rel);
			PairRelation foundrel = (PairRelation) pairRelations.get(index);
			return foundrel.getValue();
		} else
			return 0.0;
	}

	public double compare(List answers1, List answers2) {
		try {
			AnswerChoice ans1 = (AnswerChoice) answers1.get(0);
			AnswerChoice ans2 = (AnswerChoice) answers2.get(0);

			if (ans1.equals(ans2)) {
				return 1;
			}

			Iterator iter = pairRelations.iterator();
			while (iter.hasNext()) {
				PairRelation r = (PairRelation) iter.next();
				if (r.containsAnswer(ans1) && r.containsAnswer(ans2)) {
					return r.getValue();
				}
			}
		} catch (Exception x) {
			System.err.println("OCGrouped: Exception while comparing: " + x);
			return 0;
		}
		return 0;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.08.2001 13:56:27)
	 * @return java.lang.String
	 */
	public abstract java.lang.String getXMLString() ;
	
}