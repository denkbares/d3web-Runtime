package de.d3web.core.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.terminology.Question;

public class DefaultProtocol implements Protocol {

	private List<ProtocolEntry> entries;
	
	public DefaultProtocol() {
		this.entries = new ArrayList<ProtocolEntry>();
	}
	
	@Override
	public List<ProtocolEntry> getProtocolHistory() {
		return Collections.unmodifiableList(this.entries);
	}

	@Override
	public void addEntry(Question question, Value value) {
		this.entries.add(new DefaultProtocolEntry(question, value));
	}

}
