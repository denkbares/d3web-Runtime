package de.d3web.kernel.domainModel;

import java.util.Collection;
import java.util.Iterator;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;

/**
 * A Num2ChoiceSchema is a knowledge slice of QuestionOC, which facilitates the
 * question to receive numerical values (AnswerNum) and to convert it to an
 * appropriate choice answer.
 * 
 * @author baumeister
 */
public class Num2ChoiceSchema extends IDObject implements KnowledgeSlice {

	private Double[] schemaArray;
	private Question question;

	public void setSchemaArray(Double[] newArray) {
		schemaArray = newArray;
	}

	public Double[] getSchemaArray() {
		return schemaArray;
	}

	/**
	 * @return PSMethodQuestionSetter.class
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#getProblemsolverContext()
	 */
	public Class getProblemsolverContext() {
		return PSMethodQuestionSetter.class;
	}
	/**
	 * @return true by default, not used in this context.
	 * @see de.d3web.kernel.domainModel.KnowledgeSlice#isUsed(de.d3web.kernel.XPSCase)
	 */
	public boolean isUsed(XPSCase theCase) {
		return true;
	}

	/**
	 * @return the answer selected from the given answer collection according to
	 *         the given numeric value
	 */
	public Answer getAnswerForNum(Double num, Collection answers, XPSCase theCase) {
		boolean ascending = isAscending();
		for (int i = 0; i < schemaArray.length; i++) {
			if ((ascending && num.doubleValue() < schemaArray[i].doubleValue())
					|| (!ascending && num.doubleValue() > schemaArray[i].doubleValue())) {
				return nth(answers, i);
			}
		}
		return nth(answers, answers.size() - 1);
	}

	protected boolean isAscending() {
		if (schemaArray.length > 1) {
			// enough to check first values, because KnowME will inform the user
			// if schema array is neither ascending nor descending.
			return schemaArray[0] < schemaArray[1];
		}
		return true;
	}

	protected Answer nth(Collection answers, int pos) {
		Iterator iter = answers.iterator();
		int position = 0;
		while (iter.hasNext()) {
			Answer element = (Answer) iter.next();
			if (position == pos) {
				return element;
			}
			position++;
		}
		return null;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void remove() {
		question.removeKnowledge(getProblemsolverContext(), this, PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);
	}

}
