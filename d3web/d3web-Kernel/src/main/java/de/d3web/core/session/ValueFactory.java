package de.d3web.core.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.core.knowledge.terminology.AnswerMultipleChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;

public class ValueFactory {

	// it's planned to eliminate this method later, since
	// the dialog should yield Value instances not 
	// Answer instances any more
	public static Value toValue(ValuedObject valuedObject, 
			Object[] newValue, XPSCase theCase) {
		if (newValue == null || newValue.length == 0) {
			return null;
		}
//		if (valuedObject instanceof QuestionMC) {
//			List<ChoiceValue> values = new ArrayList<ChoiceValue>(newValue.length);
//			for (int i = 0; i < newValue.length; i++) {
//				if (newValue[i] instanceof AnswerUnknown) {
//					// when one answer is UNKNOWN, then the entire value is set to unknown
//					return new Unknown();
//				} else {
//					AnswerChoice answer = (AnswerChoice)newValue[i];
//					values.add(new ChoiceValue(answer));
//				}
//			}
//			return new MultipleChoiceValue(values);
//		} else {
			Object o = newValue[0];
			if (o instanceof AnswerUnknown) {
				return new Unknown();
			} else if (valuedObject instanceof QuestionNum) {
				Double d = (Double) ((AnswerNum)o).getValue(theCase);
				return new NumValue(d);
			} else if (valuedObject instanceof QuestionOC) {
				return new ChoiceValue((AnswerChoice)o);
			} else if (valuedObject instanceof QuestionMC) {
				return new MultipleChoiceValue((AnswerMultipleChoice)o);
			} else if (o instanceof QuestionDate) {
				Date date = (Date) ((AnswerDate)o).getValue(theCase);
				return new DateValue(date);
			} else if (o instanceof QuestionText) {
				String text = ((AnswerText)o).getText();
				return new TextValue(text);
			} 
//		}
		return null;
	}

}
