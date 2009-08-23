package de.d3web.caserepository.sax;

import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import de.d3web.caserepository.MetaDataImpl;
import de.d3web.kernel.supportknowledge.Property;

/**
 * @author bates
 */
public class MetaDataTagReader extends AbstractTagReader {

	private static MetaDataTagReader instance = null;
	public static AbstractTagReader getInstance() {
		if (instance == null) {
			instance = new MetaDataTagReader("MetaData");
		}
		return instance;
	}
	protected MetaDataTagReader(String id) { super(id); }

	private MetaDataImpl currentMetaData = null;

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		List ret = new LinkedList();
		ret.add("Metadata");
		ret.add("Account");
		ret.add("ProcessingTime");
		return ret;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("Metadata")) {
			startMetadata(attributes);
		} else if (qName.equals("ProcessingTime")) {
			startProcessingTime(attributes);
		} 
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("Metadata")) {
			endMetadata();
		} else if (qName.equals("Account")) {
			endAccount();
		}
	}

	private void startMetadata(Attributes attributes) {
		currentMetaData = new MetaDataImpl();
	}

	private void endMetadata() {
		getCaseObject().getProperties().setProperty(Property.CASE_METADATA, currentMetaData);
		currentMetaData = null;
	}

	private void startProcessingTime(Attributes attributes) {
		if (currentMetaData != null)
			currentMetaData.setProcessingTime(Long.parseLong(attributes.getValue("value")));
	}

	private void endAccount() {
		if (currentMetaData != null) {
			currentMetaData.setAccount(getTextBetweenCurrentTag());
		}
	}

}
