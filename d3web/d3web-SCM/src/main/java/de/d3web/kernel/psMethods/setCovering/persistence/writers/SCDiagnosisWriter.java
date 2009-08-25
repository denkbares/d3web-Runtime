package de.d3web.kernel.psMethods.setCovering.persistence.writers;

import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.persistence.xml.writers.IXMLWriter;

/**
 * This is the writer class for SCDiagnosis objects
 * 
 * @author bates
 */
public class SCDiagnosisWriter implements IXMLWriter {

	public static final Class ID = SCDiagnosisWriter.class;

	private static SCDiagnosisWriter instance = null;

	private SCDiagnosisWriter() {
	}

	public static SCDiagnosisWriter getInstance() {
		if (instance == null) {
			instance = new SCDiagnosisWriter();
		}
		return instance;
	}

	public String getXMLString(Object o) {
		if (o instanceof SCDiagnosis) {
			SCDiagnosis scDiagnosis = (SCDiagnosis) o;
			StringBuffer sb = new StringBuffer();
			sb.append("<SCNode type='SCDiagnosis' " + "apriori='"
					+ scDiagnosis.getAprioriProbability() + "' >\n");
			sb.append("<Diagnosis id='" + scDiagnosis.getNamedObject().getId() + "' " + " />\n");
			sb.append("</SCNode>\n");
			return sb.toString();
		}
		return null;
	}

}
