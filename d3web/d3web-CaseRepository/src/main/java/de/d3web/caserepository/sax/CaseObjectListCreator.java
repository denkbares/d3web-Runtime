package de.d3web.caserepository.sax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

import de.d3web.caserepository.utilities.Checker;
import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CaseObjectListCreator {

	private List additionalTagReaders = new LinkedList();

	private List createCaseObjectListInternal(Object source, KnowledgeBase kb) {
		
		boolean shouldBeReadWithOld = false;
		
		try {
			shouldBeReadWithOld = Checker.checkForOldFormat(source);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(), "Checker.check...", e);
		}
		
		List ret = null;
		
		Logger.getLogger(this.getClass().getName()).info("loading the case base ...");
		if (shouldBeReadWithOld)
			Logger.getLogger(this.getClass().getName()).warning(
				"##########################################\n" +
				"this file should be read with de.d3web.caserepository.DOM.CaseObjectListCreator!" +
				"\n##########################################");
		double startMs = System.currentTimeMillis();
		
		try {
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			
			CaseRepositoryDefaultHandler dh =
				new CaseRepositoryDefaultHandler();
			dh.setKnowledgeBase(kb);

			registerTagReaders(dh);

			if (source instanceof String)
				parser.parse(new InputSource(new StringReader((String) source)), dh);
			else if (source instanceof File)
				parser.parse((File) source, dh);
			else if (source instanceof URL)
				parser.parse(((URL) source).toExternalForm(), dh);
			else if (source instanceof InputStream)
				parser.parse((InputStream) source, dh);
			else if(source instanceof Reader) {
				parser.parse(new InputSource((Reader) source), dh);
			}
			ret = dh.getCaseObjectList();

		} catch (Exception x) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(), "createCaseObjectListInternal", x);
		}
		
		double finishedMs = System.currentTimeMillis();
		if (shouldBeReadWithOld)
			Logger.getLogger(this.getClass().getName()).warning(
				"##########################################\n" +
				"this file should be read with de.d3web.caserepository.DOM.CaseObjectListCreator!" +
				"\n##########################################");
		Logger.getLogger(this.getClass().getName()).info(
			"... finished (took "
			+ Double.toString((finishedMs - startMs) / 1000)
			+ "s)"
		);

		return ret;
	}

	public List createCaseObjectList(String xmlCode, KnowledgeBase kb) {
		return createCaseObjectListInternal(xmlCode, kb);
	}

	public List createCaseObjectList(File xmlfile, KnowledgeBase kb) {
		return createCaseObjectListInternal(xmlfile, kb);
	}

	public List createCaseObjectList(URL jarInternalURL, KnowledgeBase kb) {
		return createCaseObjectListInternal(jarInternalURL, kb);
	}
	
	public List createCaseObjectList(InputStream inputStream, KnowledgeBase kb) {
		return createCaseObjectListInternal(inputStream, kb);
	}
	
	public List createCaseObjectList(Reader reader, KnowledgeBase kb) {
		return createCaseObjectListInternal(reader, kb);
	}

	private void registerTagReaders(CaseRepositoryDefaultHandler dh) {
		// registering TagReaders
		dh.registerReader(QuestionTagReader.getInstance());
		dh.registerReader(SolutionTagReader.getInstance());
		dh.registerReader(DCMarkupReader.getInstance());
		dh.registerReader(PropertiesReader.getInstance());
		dh.registerReader(MetaDataTagReader.getInstance());
		
		// register the optional tag readers
		Iterator optTRIter = additionalTagReaders.iterator();
		while (optTRIter.hasNext()) {
			AbstractTagReader reader = (AbstractTagReader) optTRIter.next();
			dh.registerReader(reader);
		}
	}
	
	public void addTagReader(AbstractTagReader tagReader) {
		additionalTagReaders.add(tagReader);
	}

}
