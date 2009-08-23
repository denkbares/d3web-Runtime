package de.d3web.caserepository.utilities;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.xml.utilities.InputFilter;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JarIndexData {
	
	private Map caseRepositoryURLs = new HashMap();
	
	public JarIndexData(URL jarFileURL) {
		
		try {
			InputSource input = InputFilter.getFilteredInputSource(
				new URL(jarFileURL, PersistenceManager.CRS_INDEX_URL));
			parseIndexData(jarFileURL, input);
		} catch (Exception e) {
			
			// [MISC]:aha:legacy code for old KB-INF/index.xml
			Logger.getLogger(this.getClass().getName()).info("trying old jar-format");
			try {
				InputSource input = InputFilter.getFilteredInputSource(
					new URL(jarFileURL, PersistenceManager.KB_INDEX_URL));
				parseIndexData(jarFileURL, input);
			} catch (Exception e2) {
				Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(), "JarIndexData()", e2);
			}
			
		}
		
	}
	
	private void parseIndexData(URL jarFileURL, InputSource input) {
		try {
			DocumentBuilder dBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document indexDoc = dBuilder.parse(input);
			NodeList crs = indexDoc.getElementsByTagName("CaseRepository");

			// [MISC]:aha:legacy code
			if (crs.getLength() == 0)
				crs = indexDoc.getElementsByTagName("Repository");
			
			for (int i = 0; i < crs.getLength(); i++) {
				Node cr = crs.item(i);
				String id = cr.getAttributes().getNamedItem("loader").getNodeValue();
				String url = cr.getAttributes().getNamedItem("ref").getNodeValue();
				URL _url = new URL(jarFileURL, url);
				caseRepositoryURLs.put(id, _url);
			}
						
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "parseIndexData", e);
		}
	}

	public URL getCaseRepositoryURL(String id) {
		return (URL) caseRepositoryURLs.get(id);
	}

}
