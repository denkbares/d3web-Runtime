package de.d3web.core.session;

import de.d3web.core.session.blackboard.Fact;

public class DefaultProtocolEntry implements ProtocolEntry {

	private Fact fact;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fact == null) ? 0 : fact.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultProtocolEntry other = (DefaultProtocolEntry) obj;
		if (fact == null) {
			if (other.fact != null)
				return false;
		} else if (!fact.equals(other.fact))
			return false;
		return true;
	}

	public DefaultProtocolEntry(Fact fact) {
		this.fact = fact;
	}
	
	@Override
	public Fact getFact() {
		return this.fact;
	}
	
	@Override
	public String toString() {
		return this.fact.toString();
	}

}
