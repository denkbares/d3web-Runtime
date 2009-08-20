package de.d3web.persistence.xml.writers;

import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Num2ChoiceSchema;
/**
 * This writer generates an XML output for a given 
 * Num2ChoiceSchema.
 * @author baumeister
 *
 */
public class SchemaWriter implements IXMLWriter {
	
	public static final String ID = SchemaWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(java.lang.Object)
	 */
	public String getXMLString(Object o) {
		StringBuffer sb = new StringBuffer();

		if ((o == null) || (!(o instanceof Num2ChoiceSchema))) {
			Logger.getLogger(this.getClass().getName()).warning("No schema given for " + o);
		} else {

			Num2ChoiceSchema schema = (Num2ChoiceSchema) o;

			sb.append(
				"<KnowledgeSlice ID='"
					+ schema.getId()
					+ "' type='Schema'>");
			sb.append(
				"\n<Question ID='"
					+ schema.getQuestion().getId()
					+ "'/>");
			sb.append(
				"\n<LeftClosedInterval value='"
					+ arrayToString(schema.getSchemaArray())
					+ "'/>");
			sb.append("\n</KnowledgeSlice>\n");
		}

		return sb.toString();
	}

	private static String arrayToString(Double[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i == 0)
				sb.append(array[i]);
			else
				sb.append(" " + array[i]);
		}
		return sb.toString();
	}
}
