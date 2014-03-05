package de.d3web.core.inference.condition;

import java.util.List;

import de.d3web.core.session.Session;

/**
 * Created by Albrecht Striffler (denkbares GmbH) on 03.03.14.
 */
public class CondNonTerminalUnknown extends NonTerminalCondition {

	/**
	 * Creates a new AND-condition based on the conjunction of the specified terms ({@link Condition} instances).
	 *
	 * @param terms a collection of {@link Condition} instances
	 */
	public CondNonTerminalUnknown(List<Condition> terms) {
		super(terms);
	}

	/**
	 * Returns true, when <b>any</b> of the nested {@link Condition}s throws the {@link UnknownAnswerException}.
	 *
	 * @param session the given {@link Session}
	 */
	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		boolean wasNoAnswer = false;
		for (Condition condition : getTerms()) {
			try {
				condition.eval(session);
			}
			catch (NoAnswerException nae) {
				wasNoAnswer = true;
			}
			catch (UnknownAnswerException uae) {
				return true;
			}
		}
		if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		}
		return false;
	}

	@Override
	public String toString() {
		String ret = "\u2190 CondUnknown {";
		for (Condition condition : getTerms()) {
			if (condition != null) {
				ret += condition.toString() + "; ";
			}
		}
		// remove last "; "
		if (ret.endsWith("; ")) {
			ret = ret.substring(0, ret.length() - 2);
		}
		ret += "}";
		return ret;
	}
}