package de.d3web.kernel.psMethods.compareCase.tests.utils;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.kernel.supportknowledge.DCElement;

/**
 * This is a dummy class for Junit-tests. It is used for retrieving a similarity without
 * having to compare cases.
 * @author bruemmer
 */
public class CaseObjectTestDummy extends CaseObjectImpl {

	private double similarity = 0;
	private String id = null;

	public CaseObjectTestDummy(String id) {
		super(null);
		this.id = id;
		getDCMarkup().setContent(DCElement.IDENTIFIER, id);
	}

	public String getId() {
		return id;
	}

	public void setSimilarityForUnitTests(double similarity) {
		this.similarity = similarity;
	}

	public double getSimilarityForUnitTests(CaseObjectTestDummy other) {
		return similarity * other.similarity;
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object o) {
		try {
			CaseObjectTestDummy other = (CaseObjectTestDummy) o;
			return id.equals(other.id);
		} catch (Exception e) {
			return false;
		}
	}

	public String toString() {
		return id;
	}

}
