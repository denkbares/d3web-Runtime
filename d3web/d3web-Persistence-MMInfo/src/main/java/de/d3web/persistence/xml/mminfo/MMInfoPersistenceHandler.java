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

package de.d3web.persistence.xml.mminfo;
import java.net.URL;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.AuxiliaryPersistenceHandler;
/**
 * Insert the type's description here.
 * Creation date: (25.01.2002 14:18:47)
 * @author: Christian Betz
 */
public class MMInfoPersistenceHandler implements AuxiliaryPersistenceHandler, ProgressNotifier {

	public final static String MMINFO_PERSISTENCE_HANDLER = "mminfo";

	/**
	 * getId method comment.
	 */
	public String getId() {
		return MMINFO_PERSISTENCE_HANDLER;
	}

	/**
	 * load method comment.
	 */
	public KnowledgeBase load(KnowledgeBase kb, URL url) {

		// [TODO]:aha:check for "does this file actually match the  knowledgebase"!

		return MMInfoLoader.loadMMinfoStorages(kb, url);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (25.01.2002 14:19:16)
	 * @return boolean
	 * @param kb de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public Document save(KnowledgeBase kb) {
		return MMInfoStorageWriter.writeMMInfoStorages(kb);
	}
	/**
	 * @see de.d3web.persistence.xml.PersistenceHandler#getDefaultStorageLocation()
	 */
	public String getDefaultStorageLocation() {
		return "kb/mminfo.xml";
	}

	public void addProgressListener(ProgressListener listener) {
		MMInfoLoader.addProgressListener(listener);
		MMInfoStorageWriter.addProgressListener(listener);
	}

	public void fireProgressEvent(ProgressEvent evt) {
		MMInfoLoader.fireProgressEvent(evt);
		MMInfoStorageWriter.fireProgressEvent(evt);

	}

	public void removeProgressListener(ProgressListener listener) {
		MMInfoLoader.removeProgressListener(listener);
		MMInfoStorageWriter.removeProgressListener(listener);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int, java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {

		if (operationType == ProgressEvent.OPERATIONTYPE_SAVE) {

			if (!(additionalInformation instanceof KnowledgeBase))
				return PROGRESSTIME_UNKNOWN;

			KnowledgeBase kb = (KnowledgeBase) additionalInformation;

			return MMInfoStorageWriter.getProgressTime(kb);

		}

		//		Bei den Writern ermittlen!
		return PROGRESSTIME_UNKNOWN;

	}

}