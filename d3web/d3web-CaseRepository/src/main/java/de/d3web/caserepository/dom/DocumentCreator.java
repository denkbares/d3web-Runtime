package de.d3web.caserepository.dom;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.d3web.persistence.utilities.URLUtils;
/**
 * Creates a w3c.Document-Object based on a xml-file.
 */
public class DocumentCreator {

	/**
	 * Creates a Document based on the XML-File specified by filename
	 * 
	 * @return Document
	 * @param filename of the xmlfile
	 */
	public static Document createDocument(String filename) throws Exception {
		try {
			return getDocumentBuilder().parse(new InputSource(new FileReader(filename)));
		} catch (Exception e) {
			Logger.getLogger(DocumentCreator.class.getName()).throwing(DocumentCreator.class.getName(), "createDocument", e);
			throw e;
		}
	}

	/**
	 * Creates a Document based on the XML-File specified by filename
	 * 
	 * @return Document
	 * @param inputStream (of an xmlfile)
	 */
	public static Document createDocument(InputStream inputStream) throws Exception {
		try {
			return getDocumentBuilder().parse(
				new InputSource(new InputStreamReader(inputStream)));
		} catch (Exception e) {
			Logger.getLogger(DocumentCreator.class.getName()).throwing(DocumentCreator.class.getName(), "createDocument", e);
			throw e;
		}
	}
	
	/**
	 * 
	 * @return DocumentBuilder
	 * @throws ParserConfigurationException
	 */
	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		
		return dbf.newDocumentBuilder();
	}

	/**
	 * Creates a Document based on the XML-File specified by URL
	 * 
	 * @return Document
	 * @param repositoryURL of the xmlfile
	 */
	public static Document createDocument(URL repositoryURL) throws Exception {
		try {
			return getDocumentBuilder().parse(URLUtils.openStream(repositoryURL));
		} catch (Exception e) {
			Logger.getLogger(DocumentCreator.class.getName()).throwing(DocumentCreator.class.getName(), "createDocument", e);
			throw e;
		}
	}

	/**
	 * Creates a Document based on the XML-content of xmlCode
	 * 
	 * @return Document
	 * @param xmlCode String with XML
	 */
	public static Document createDocumentFromString(String xmlCode) throws Exception {
		try {
			return getDocumentBuilder().parse(new InputSource(new StringReader(xmlCode)));
		} catch (Exception e) {
			Logger.getLogger(DocumentCreator.class.getName()).throwing(DocumentCreator.class.getName(), "createDocument", e);
			throw e;
		}
	}
}