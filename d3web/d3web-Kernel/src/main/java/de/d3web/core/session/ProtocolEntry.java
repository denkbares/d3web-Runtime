package de.d3web.core.session;

import de.d3web.core.knowledge.terminology.Question;

public interface ProtocolEntry {
	public Question getQuestion();
	public Value getValue();
}
