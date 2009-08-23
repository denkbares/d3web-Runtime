package de.d3web.config.persistence;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.d3web.config.Config;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.utilities.StringBufferInputStream;
import de.d3web.persistence.utilities.StringBufferStream;
import de.d3web.persistence.xml.AuxiliaryPersistenceHandler;
import de.d3web.persistence.xml.writers.DCMarkupWriter;

/**
 * @author mweniger
 */
public class ConfigPersistenceHandler implements AuxiliaryPersistenceHandler{
	
	private final static String id = "config";
	private final static String defaultStorageLocation = "kb/config.xml";
	
	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.PersistenceHandler#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.PersistenceHandler#getDefaultStorageLocation()
	 */
	public String getDefaultStorageLocation() {
		return defaultStorageLocation;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.AuxiliaryPersistenceHandler#load(de.d3web.kernel.domainModel.KnowledgeBase, java.net.URL)
	 */	
	public KnowledgeBase load(KnowledgeBase kb, URL url) {
								
		Config conf = ConfigReader.createConfig(url, Config.TYPE_KNOWLEDGEBASE);
		kb.getProperties().setProperty(Property.CONFIG, conf);
		
		return kb;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.PersistenceHandler#save(de.d3web.kernel.domainModel.KnowledgeBase)
	 */
	public Document save(KnowledgeBase kb) {
		
		Config conf = (Config) kb.getProperties().getProperty(Property.CONFIG);
		if (conf == null)
			return null;
			
		StringBuffer sb = new StringBuffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		sb.append("<KnowledgeBase system=\"d3web\" type=\"" + getId() + "\">");
		sb.append(DCMarkupWriter.getInstance().getXMLString(kb.getDCMarkup()));

		ConfigWriter.write(conf, sb);
		
		sb.append("</KnowledgeBase>");
		
		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));
			Document dom = builder.parse(stream);
			return dom;

		} catch (Exception e) {
			System.err.println("Exception " + e);
			e.printStackTrace();
			return null;
		}

	}

}
