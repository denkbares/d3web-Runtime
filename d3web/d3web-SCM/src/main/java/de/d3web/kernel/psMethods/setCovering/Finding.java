package de.d3web.kernel.psMethods.setCovering;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.utils.NumericalIntervalMapper;
import de.d3web.kernel.psMethods.shared.PSMethodShared;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.psMethods.shared.comparators.DefaultQuestionComparator;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.kernel.supportknowledge.Property;

/**
 * This class describes a Findng in general.
 * 
 * @author bruemmer
 */
public abstract class Finding extends SCNode {

	String id = null;
	private Object[] answers = null;

	public abstract String verbalize();

	public Object[] getAnswers() {
		return answers;
	}

	protected void setAnswers(Object[] answers) {
		this.answers = answers;
	}

	/**
	 * calculates the id from question ans answers
	 */
	public String getId() {
		if (this.id == null) {
			StringBuffer id = new StringBuffer();
			if ((getNamedObject() != null) && (getAnswers() != null)) {
				id.append(getNamedObject().getId());
				for (int i = 0; i < getAnswers().length; ++i) {
					id.append("_");
					Answer ans = (Answer) getAnswers()[i];
					if (ans instanceof AnswerUnknown) {
						id.append("unknown");
					} else if (ans instanceof AnswerChoice) {
						id.append(((AnswerChoice) ans).getId());
					} else {
						id.append(ans.getValue(null));
					}
				}
				this.id = id.toString();
			}
		}
		return this.id;
	}

	public int getWeight() {
		return getWeight((Question) getNamedObject());
	}

	public double getWeight(XPSCase theCase) {
		return getWeight((Question) getNamedObject(), theCase);
	}
	
	public static int getWeight(Question question) {
		int weight = 1;
		List knowledgeList = question.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_WEIGHT);
		if ((knowledgeList != null) && (!knowledgeList.isEmpty())) {
			Weight sharedWeight = (Weight) knowledgeList.get(0);
			weight = sharedWeight.getQuestionWeightValue().getValue();
		}
		return weight;
	}
	
	public static double getWeight(Question question, XPSCase theCase) {
		if (theCase != null) {
			Map<Question, Double> weights = (Map<Question, Double>) theCase.getProperties().getProperty(Property.CASE_USER_DEFINED_WEIGHTS);
			if (weights != null) {
				Double userWeight = weights.get(question);
				if (userWeight != null) {
					return userWeight;
				}
			}
		}
		return getWeight(question);
	}

	public abstract double calculateSimilarity(Finding otherFinding);

	private boolean isAnswerUnknown(List answers) {
		return (answers == null) || (answers.isEmpty())
				|| answers.contains(((Question) getNamedObject()).getUnknownAlternative());
	}

	/**
	 * Simply compares the two given answer lists using the defined comparator
	 * for the internal NamedObject
	 */
	public double calculateSimilarity(List answers0, List answers1) {
		List similarityKnowledge = getNamedObject().getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_SIMILARITY);
		QuestionComparator qcomp = null;
		if ((similarityKnowledge == null) || similarityKnowledge.isEmpty()) {
			qcomp = DefaultQuestionComparator.getInstance();
		} else {
			qcomp = (QuestionComparator) similarityKnowledge.get(0);
		}

		if (getNamedObject() instanceof QuestionNum) {
			List newAns0 = mapAnswersToIntervalsIfChoice(answers0);
			List newAns1 = mapAnswersToIntervalsIfChoice(answers1);
			return qcomp.compare(newAns0, newAns1);
		}

		if ((!isAnswerUnknown(answers0)) && (!isAnswerUnknown(answers1))) {
			return qcomp.compare(answers0, answers1);
		} else {
			return 0;
		}
	}

	private List mapAnswersToIntervalsIfChoice(List answers) {
		List ret = new LinkedList();
		Iterator iter = answers.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof AnswerChoice) {
				AnswerChoice ansChoice = (AnswerChoice) o;
				NumericalInterval interval = NumericalIntervalMapper.getInstance().map(ansChoice);
				if (interval != null) {
					ret.add(interval);
				} else {
					// maybe answer was AnswerNum before...
					AnswerNum ansNum = new AnswerNum();
					ansNum.setQuestion((Question) getNamedObject());
					ansNum.setValue(new Double(ansChoice.getText()));
				}
			} else {
				ret.add(o);
			}
		}
		return ret;
	}

	public boolean parametricallyEquals(Finding other) {
		return getNamedObject().equals(other.getNamedObject());
	}

	public boolean isLeaf() {
		return true;
	}

}
