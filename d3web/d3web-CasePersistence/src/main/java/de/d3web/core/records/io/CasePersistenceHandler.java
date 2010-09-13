package de.d3web.core.records.io;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.io.progress.ProgressListener;

/**
 * A CasePersistenceHandler reads and writes s special part of a CaseObject.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface CasePersistenceHandler {

	/**
	 * Reads the information from the caseElement and adds them to the
	 * caseObject
	 * 
	 * @param caseElement XML Element containing the case, the
	 *        CasePersistanceHandler automatically extracts his nodes
	 * @param caseObject CaseObject, in which the information will be inserted
	 * @param listener ProgressListener, which will be informed about the
	 *        progress
	 * @throws IOException if an error occurs
	 */
	void read(Element caseElement, CaseObject caseObject, ProgressListener listener) throws IOException;

	/**
	 * Appends Elements containing knowledge this PersistanceHandler can handle
	 * 
	 * @param caseElement XML Element, where the created elements should be
	 *        appended
	 * @param caseObject CaseObject containing the information
	 * @param listener ProgressListener, which will be informed about the
	 *        progress
	 * @throws IOException if an error occurs
	 */
	void write(Element caseElement, CaseObject caseObject, ProgressListener listener) throws IOException;
}
