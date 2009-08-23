/*
 * Created on 16.09.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.caserepository.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import de.d3web.caserepository.CaseObject;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.utilities.StringBufferInputStream;
import de.d3web.persistence.utilities.StringBufferOutputStream;
import de.d3web.persistence.utilities.StringBufferStream;
import de.d3web.persistence.xml.PersistenceManager;

/**
 * @author Atzmueller
 */
public class CaseObjectListWriter implements ProgressNotifier, ProgressListener {
	
	private Vector progressListeners = new Vector();
	private final static int MULT = 10;
	private boolean withProgressEvents = false;

	public void setWithProgressEvents(boolean b) {
		this.withProgressEvents = b;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#addProgressListener(de.d3web.persistence.progress.ProgressListener)
	 */
	public void addProgressListener(ProgressListener newProgressListener) {
		if (!withProgressEvents)
			return;
		progressListeners.add(newProgressListener);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#fireProgressEvent(de.d3web.persistence.progress.ProgressEvent)
	 */
	public void fireProgressEvent(ProgressEvent evt) {
		if (!withProgressEvents)
			return;
		Enumeration enumeration = progressListeners.elements();
		while (enumeration.hasMoreElements())
			 ((ProgressListener) enumeration.nextElement()).updateProgress(evt);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#removeProgressListener(de.d3web.persistence.progress.ProgressListener)
	 */
	public void removeProgressListener(ProgressListener listener) {
		if (!withProgressEvents)
			return;
		progressListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressListener#updateProgress(de.d3web.persistence.progress.ProgressEvent)
	 */
	public void updateProgress(ProgressEvent evt) {
		if (!withProgressEvents)
			return;
		fireProgressEvent(evt);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int, java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {
		if (!withProgressEvents)
			return 0;
		if (operationType != ProgressEvent.OPERATIONTYPE_SAVE)
			return ProgressNotifier.PROGRESSTIME_UNKNOWN;
		else if (!(additionalInformation instanceof List))
			return ProgressNotifier.PROGRESSTIME_UNKNOWN;
		else
			return ((List) additionalInformation).size()*MULT;
	}
	
	/* -------------------------------------------------- */
	
	private List additionalWriters = new LinkedList();
	
	public void addAdditionalWriter(CaseObjectListAdditionalWriter w) {
		additionalWriters.add(w);
	}

	/* -------------------------------------------------- */

	private void saveToStream(PrintWriter pw, Collection cases) {
		if (cases == null) return;
		if (withProgressEvents)
			fireProgressEvent(
				new ProgressEvent(this,
					ProgressEvent.START,
					ProgressEvent.OPERATIONTYPE_SAVE,
					PersistenceManager.resourceBundle.getString("d3web.CaseRepository.CaseHandler.saveCase"),
					0, 1)
			);
		long aktvalue = 0;
		long maxvalue = getProgressTime(ProgressEvent.OPERATIONTYPE_SAVE, cases);
		
		pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>\n");
		pw.println("<CaseRepository>\n");

		pw.println("<Cases>\n");
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			CaseObject cobj = (CaseObject) iter.next();
			
			// just to make sure the caseObject has an id!
			cobj.getId();
			
			pw.println(cobj.getXMLCode());
			if (withProgressEvents)
				fireProgressEvent(
					new ProgressEvent(this,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_SAVE,
						PersistenceManager.resourceBundle.getString("d3web.CaseRepository.CaseHandler.save"),
						aktvalue += 10,
						maxvalue
					)
				);
		}
		pw.println("</Cases>\n");
		
		if (!additionalWriters.isEmpty()) {
			Iterator iter2 = additionalWriters.iterator();
			while (iter2.hasNext()) {
				CaseObjectListAdditionalWriter aw = (CaseObjectListAdditionalWriter) iter2.next();
				pw.println("<" + aw.getTag() + ">\n");
				Iterator iter3 = cases.iterator();
				while (iter3.hasNext()) {
					CaseObject co = (CaseObject) iter3.next();
					String code = aw.getXMLCode(co);
					if (code != null) {
						pw.println("<Case id=\"" + co.getId() + "\">\n");
						pw.println(code);
						pw.println("</Case>\n");
					}
					if (withProgressEvents)
						fireProgressEvent(
							new ProgressEvent(
								this,
								ProgressEvent.UPDATE,
								ProgressEvent.OPERATIONTYPE_SAVE,
								PersistenceManager.resourceBundle.getString("d3web.CaseRepository.CaseHandler.saveadd") + ": " + aw.getTag(),
								aktvalue += 10,
								maxvalue
							)
						);
				}
				pw.println("</" + aw.getTag() + ">\n");
			}
		}
		
		pw.println("</CaseRepository>\n");
		if (withProgressEvents)
			fireProgressEvent(
				new ProgressEvent(this,
					ProgressEvent.DONE,
					ProgressEvent.OPERATIONTYPE_SAVE,
					PersistenceManager.resourceBundle.getString("d3web.CaseRepository.CaseHandler.save"),
					1, 1
				)
			);

	}

	public StringBuffer saveToStringBuffer(Collection cases) {
		StringBuffer sb = new StringBuffer();
		PrintWriter pw = new PrintWriter(new StringBufferOutputStream(new StringBufferStream(sb)));
		saveToStream(pw, cases);
		pw.close();
		return sb;
	}

	public Document saveToDocument(Collection cases) {
		StringBuffer sb = saveToStringBuffer(cases);
		InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));
		Document dom = null;
		try {
			dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "saveToDocument", e);
		}
		return dom;
	}
	
	public void saveToFile(File file, Collection cases) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(file), true);
			saveToStream(pw, cases);
			pw.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "saveToFile", e);
		}
	}

}
