/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus.internal;

import java.util.Set;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;

/**
 * 21.10.2003 15:48:17
 * @author hoernlein
 */
public class CLeaf extends AbstractCNode {
	
	public final static Type INCLUDED = new Type("included");
	public final static Type EXCLUDED = new Type("excluded");
	
	public static class Type {
		private String name;
		private Type() { /* hide empty constructor */ }
		private Type(String name) { this.name = name; }
		public String getName() { return name; }
	}
	
	private Diagnosis d;
	private Type type;
	
	private CLeaf() { /* hide empty constructor */ }
	public CLeaf(Diagnosis d, Type type) {
		this.d = d;
		this.type = type;
	}
		
	/* (non-Javadoc)
	 * @see de.d3web.Train.FUS.AbstractCNode#matches(java.util.Set)
	 */
	public boolean matches(Set<Diagnosis> diagnoses) {
		if (type == INCLUDED) {
			return diagnoses.contains(d);
		} else if (type == EXCLUDED) {
			return !diagnoses.contains(d);
		} else {
			Logger.getLogger(this.getClass().getName()).severe("not implemented for type '" + type.getName() + "'");
			return false;
		}
	}

	/**
	 * @return
	 */
	public Diagnosis getDiagnosis() {
		return d;
	}

	/**
	 * @return
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public Object clone() {
		CLeaf temp = new CLeaf (d, type);
		return temp;
	}

}
