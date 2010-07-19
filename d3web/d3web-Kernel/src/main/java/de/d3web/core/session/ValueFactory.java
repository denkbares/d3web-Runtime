package de.d3web.core.session;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.values.ChoiceValue;
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
}
