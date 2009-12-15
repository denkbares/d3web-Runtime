package de.d3web.kernel.psMethods;

import de.d3web.kernel.domainModel.NamedObject;

public class PropagationEntry {
	
	private final NamedObject object;
	private final Object[] oldValue;
	private final Object[] newValue;
	
	public PropagationEntry(NamedObject object, Object[] oldValue, Object[] newValue) {
		this.object = object;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public NamedObject getObject() {
		return object;
	}

	public Object[] getOldValue() {
		return oldValue;
	}

	public boolean hasOldValue() {
		return oldValue.length != 0;
	}

	public Object[] getNewValue() {
		return newValue;
	}

	public boolean hasNewValue() {
		return newValue.length != 0;
	}

}
