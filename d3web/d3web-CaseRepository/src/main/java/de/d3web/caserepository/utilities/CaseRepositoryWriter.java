/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

/*
 * Created on 16.09.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code Template
 */
package de.d3web.caserepository.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;

/**
 * @author Atzmueller
 */
public class CaseRepositoryWriter implements ProgressNotifier, ProgressListener {

	private Vector progressListeners = new Vector();
	private final static int MULT = 10;
	private boolean withProgressEvents = false;

	public void setWithProgressEvents(boolean b) {
		this.withProgressEvents = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.persistence.progress.ProgressNotifier#addProgressListener(de
	 * .d3web.persistence.progress.ProgressListener)
	 */
	public void addProgressListener(ProgressListener newProgressListener) {
		if (!withProgressEvents) return;
		progressListeners.add(newProgressListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.persistence.progress.ProgressNotifier#fireProgressEvent(de.d3web
	 * .persistence.progress.ProgressEvent)
	 */
	public void fireProgressEvent(ProgressEvent evt) {
		if (!withProgressEvents) return;
		Enumeration enumeration = progressListeners.elements();
		while (enumeration.hasMoreElements())
			((ProgressListener) enumeration.nextElement()).updateProgress(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.persistence.progress.ProgressNotifier#removeProgressListener
	 * (de.d3web.persistence.progress.ProgressListener)
	 */
	public void removeProgressListener(ProgressListener listener) {
		if (!withProgressEvents) return;
		progressListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.persistence.progress.ProgressListener#updateProgress(de.d3web
	 * .persistence.progress.ProgressEvent)
	 */
	public void updateProgress(ProgressEvent evt) {
		if (!withProgressEvents) return;
		fireProgressEvent(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int,
	 * java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {
		if (!withProgressEvents) return 0;
		if (operationType != ProgressEvent.OPERATIONTYPE_SAVE) return ProgressNotifier.PROGRESSTIME_UNKNOWN;
		else if (!(additionalInformation instanceof List)) return ProgressNotifier.PROGRESSTIME_UNKNOWN;
		else return ((List) additionalInformation).size() * MULT;
	}

	/* -------------------------------------------------- */

	private List additionalWriters = new LinkedList();

	public void addAdditionalWriter(CaseObjectListAdditionalWriter w) {
		additionalWriters.add(w);
	}

	/* -------------------------------------------------- */

	private void saveToStream(PrintWriter pw, CaseRepository caseRepository) {
		if (caseRepository == null) return;
		if (withProgressEvents) fireProgressEvent(new ProgressEvent(this,
					ProgressEvent.START,
					ProgressEvent.OPERATIONTYPE_SAVE,
					"Saving cases",
					0, 1));
		long aktvalue = 0;
		long maxvalue = getProgressTime(ProgressEvent.OPERATIONTYPE_SAVE, caseRepository);

		pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
		pw.println("<CaseRepository>\n");

		pw.println("<Cases>\n");
		Iterator<CaseObject> iter = caseRepository.iterator();
		while (iter.hasNext()) {
			CaseObject cobj = iter.next();

			// just to make sure the caseObject has an id!
			cobj.getId();

			pw.println(cobj.getXMLCode());
			if (withProgressEvents) fireProgressEvent(new ProgressEvent(this,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_SAVE,
						"Saving basic case data",
						aktvalue += 10,
						maxvalue));
		}
		pw.println("</Cases>\n");

		if (!additionalWriters.isEmpty()) {
			Iterator iter2 = additionalWriters.iterator();
			while (iter2.hasNext()) {
				CaseObjectListAdditionalWriter aw = (CaseObjectListAdditionalWriter) iter2.next();
				pw.println("<" + aw.getTag() + ">\n");
				Iterator<CaseObject> iter3 = caseRepository.iterator();
				while (iter3.hasNext()) {
					CaseObject co = iter3.next();
					String code = aw.getXMLCode(co);
					if (code != null) {
						pw.println("<Case id=\"" + co.getId() + "\">\n");
						pw.println(code);
						pw.println("</Case>\n");
					}
					if (withProgressEvents) fireProgressEvent(new ProgressEvent(
								this,
								ProgressEvent.UPDATE,
								ProgressEvent.OPERATIONTYPE_SAVE,
								"Saving additional case data" + ": " + aw.getTag(),
								aktvalue += 10,
								maxvalue));
				}
				pw.println("</" + aw.getTag() + ">\n");
			}
		}

		pw.println("</CaseRepository>\n");
		if (withProgressEvents) fireProgressEvent(new ProgressEvent(this,
					ProgressEvent.DONE,
					ProgressEvent.OPERATIONTYPE_SAVE,
					"Saving basic case data",
					1, 1));

	}

	public void saveToFile(File file, CaseRepository caseRepository) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(file), true);
			saveToStream(pw, caseRepository);
			pw.close();
		}
		catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(), "saveToFile", e);
		}
	}

}
