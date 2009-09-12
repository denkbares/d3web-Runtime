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

package de.d3web.kernel.psMethods.setCovering.persistence;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.persistence.loader.SCMLoader;
import de.d3web.kernel.psMethods.setCovering.persistence.writers.SCMWriter;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.utilities.StringBufferInputStream;
import de.d3web.persistence.utilities.StringBufferStream;
import de.d3web.persistence.xml.AuxiliaryPersistenceHandler;
import de.d3web.persistence.xml.PersistenceHandler;
import de.d3web.xml.utilities.InputFilter;

/**
 * This is the PersistenceHandler for SCM-Knowledge
 * 
 * @author bates
 */
public class SCMPersistenceHandler
		implements
			AuxiliaryPersistenceHandler,
			PersistenceHandler,
			ProgressListener,
			ProgressNotifier {

	public final static String SCM_PERSISTENCE_HANDLER = "set-covering";

	protected Vector progressListeners = null;

	public SCMPersistenceHandler() {
		progressListeners = new Vector();
	}

	public KnowledgeBase load(KnowledgeBase kb, URL url) {

		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(InputFilter.getFilteredInputSource(url));

			SCMLoader.getInstance().addProgressListener(this);

			KnowledgeBase ret = SCMLoader.getInstance().loadKnowledgeSlices(kb, doc);
			SCMLoader.getInstance().removeProgressListener(this);
			return ret;

		} catch (Exception e) {
			Logger.getLogger(SCMPersistenceHandler.class.getName()).throwing(SCMPersistenceHandler.class.getName(), "load", e);
		}

		return null;
	}

	public String getId() {
		return SCM_PERSISTENCE_HANDLER;
	}

	public String getDefaultStorageLocation() {
		return "kb/scm.xml";
	}

	public Document save(KnowledgeBase kb) {
		try {

			StringBuffer sb = new StringBuffer(SCMWriter.getInstance().getXMLString(kb));

			// building the Document from StringBuffer
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));
			Document dom = builder.parse(stream);
			return dom;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void updateProgress(ProgressEvent evt) {
		fireProgressEvent(evt);
	}

	public void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);
	}

	public void fireProgressEvent(ProgressEvent evt) {
		Enumeration enu = progressListeners.elements();
		while (enu.hasMoreElements())
			((de.d3web.persistence.progress.ProgressListener) enu.nextElement())
					.updateProgress(evt);
	}

	public long getProgressTime(int operationType, Object additionalInformation) {
		try {
			KnowledgeBase kb = (KnowledgeBase) additionalInformation;
			Collection relations = kb.getAllKnowledgeSlicesFor(PSMethodSetCovering.class);
			return relations.size();
		} catch (Exception e) {
			return PROGRESSTIME_UNKNOWN;
		}
	}

}
