/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.shared.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Loads and saves shared knowledge from/to XML Creation date: (14.08.2001
 * 14:04:56)
 * 
 * @author: Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class SharedPersistenceHandler implements KnowledgeReader {

	public final static String SHARED_PERSISTENCE_HANDLER = "shared";

	/**
	 * SharedPersistenceHandler constructor comment.
	 */
	public SharedPersistenceHandler() {
		super();
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Loading shared knowlege");
		Document doc = Util.streamToDocument(stream);
		// [TODO]:aha:check for "does this file actually match the
		// knowledgebase"!
		addKnowledgeSlices(kb, doc, listener);
		listener.updateProgress(1, "Loading shared knowlege");
	}

	private static void addKnowledgeSlices(KnowledgeBase kb, Document doc, ProgressListener listener) throws IOException {

		// Anzahl Slices ermitteln
		int slicecount = 0;
		int aktslicecount = 0;

		List<Element> nl = XMLUtil.getElementList(doc.getElementsByTagName("KnowledgeSlice"));
		// globalUnknownSimilaritys have an own nodename
		// it is not sufficient to parse all children of KnowledgeSlices,
		// because in former Versions of the Persistence
		// the globalUnknownSimilarity was stored as child of the KnowledgeBase
		nl.addAll(XMLUtil.getElementList(doc.getElementsByTagName("globalUnknownSimilarity")));
		slicecount = nl.size();
		for (Element n : nl) {
			PersistenceManager.getInstance().readFragment(n, kb);
			listener.updateProgress(((float) aktslicecount) / slicecount,
					"Loading shared knowlege: knowledge slice "
							+ aktslicecount++
							+ " of "
							+ slicecount);
		}
	}
}