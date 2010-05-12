package de.d3web.core.session;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

public class ValueFactory {

	public static String getID_or_Value(Value value) {
		if (value instanceof ChoiceValue) {
			return ((Choice) (value.getValue())).getId();
		}
		else if (value instanceof Unknown) {
			return Unknown.UNKNOWN_ID;
		}
		else if (value instanceof UndefinedValue) {
			return UndefinedValue.UNDEFINED_ID;
		}
		else {
			return value.getValue().toString();
		}
	}

	public static Value toValue(Question question, Answer answer) {
		if (question instanceof QuestionOC) {
			return new ChoiceValue((Choice) answer);
		}
		else if (question instanceof QuestionMC) {
			return new MultipleChoiceValue((AnswerMultipleChoice) answer);
		}
		else if (question instanceof QuestionNum) {
			AnswerNum numa = (AnswerNum) answer;
			Double d = (Double) numa.getValue(null);
			return new NumValue(d);
		}
		// TODO 04.2010 insert handling for text and date
		else {
			return UndefinedValue.getInstance();
		}
	}
}
