/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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

import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.utilities.Checker;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CaseRepositoryReader {

	private List additionalTagReaders = new LinkedList();

	private CaseRepository createCaseRepositoryInternal(Object source, KnowledgeBase kb) {
		
		boolean shouldBeReadWithOld = false;
		
		try {
			shouldBeReadWithOld = Checker.checkForOldFormat(source);
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(), "Checker.check...", e);
		}
		
		CaseRepository ret = null;
		
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
			ret = dh.getCaseRepository();

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

	public CaseRepository createCaseRepository(String xmlCode, KnowledgeBase kb) {
		return createCaseRepositoryInternal(xmlCode, kb);
	}

	public CaseRepository createCaseRepository(File xmlfile, KnowledgeBase kb) {
		return createCaseRepositoryInternal(xmlfile, kb);
	}

	public CaseRepository createCaseRepository(URL jarInternalURL, KnowledgeBase kb) {
		return createCaseRepositoryInternal(jarInternalURL, kb);
	}
	
	public CaseRepository createCaseRepository(InputStream inputStream, KnowledgeBase kb) {
		return createCaseRepositoryInternal(inputStream, kb);
	}
	
	public CaseRepository createCaseRepository(Reader reader, KnowledgeBase kb) {
		return createCaseRepositoryInternal(reader, kb);
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
