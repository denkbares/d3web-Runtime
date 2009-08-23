package de.d3web.caserepository.sax;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.caserepository.AbstractCaseRepositoryHandler;
import de.d3web.caserepository.utilities.*;
import de.d3web.caserepository.utilities.Utilities;
import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CaseRepositoryHandler extends AbstractCaseRepositoryHandler {

	private CaseObjectListCreator colc = null;

	private CaseObjectListCreator getCaseObjectListCreator() {
		if (colc == null)
			colc = new CaseObjectListCreator();
		return colc;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#load(de.d3web.kernel.domainModel.KnowledgeBase, java.net.URL)
	 */
	public List load(KnowledgeBase kb, URL jarURL) {
		List ret = new LinkedList();

		try {
			if (Utilities.isCRXMLFile(jarURL)) {
				ret = getCaseObjectListCreator().createCaseObjectList(jarURL, kb);
				// [FIXME]:aha:quick and dirty
				String loc = jarURL.toExternalForm();
				if (jarURL.getProtocol().equals("jar"))
					setStorageLocation(loc.substring(loc.lastIndexOf("!/") + 2));
				else
					Logger.getLogger(this.getClass().getName()).warning("can't find out where to set location based on '" + loc + "'");
			} else if (Utilities.hasCasesInf(jarURL)) {
				URL jarInternalURL = new JarIndexData(jarURL).getCaseRepositoryURL(getId());
				ret = getCaseObjectListCreator().createCaseObjectList(jarInternalURL, kb);
				setStorageLocation(jarInternalURL.toExternalForm());
			} else {
				InputStream zipInput = Utilities.getInputStreamFromZipJarURL(jarURL);
				ret = getCaseObjectListCreator().createCaseObjectList(zipInput, kb);
				setStorageLocation(jarURL.toExternalForm());
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "load", e);
		}

		return ret;
	}

	/**
	 * 
	 * @param tagReader AbstractTagReader
	 */
	public void addTagReader(AbstractTagReader tagReader) {
		getCaseObjectListCreator().addTagReader(tagReader);
	}

}
