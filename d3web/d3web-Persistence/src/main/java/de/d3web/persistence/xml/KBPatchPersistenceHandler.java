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

package de.d3web.persistence.xml;

import java.net.URL;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.xml.loader.KBPatchLoader;

/**
 * PersistenceHandler for reading kb-patches (e.g. redefined RuleComplex).
 * @author gbuscher
 */
public class KBPatchPersistenceHandler implements AuxiliaryPersistenceHandler {
	
	public static final String PATCH_PERSISTENCE_HANDLER = "kb-patch";


	public KnowledgeBase load(KnowledgeBase kb, URL url) {
		KBPatchLoader kbel = new KBPatchLoader();
		kbel.setFileURL(url);
		kbel.update(kb);
		return kb;
	}

	public String getId() {
		return PATCH_PERSISTENCE_HANDLER;
	}

	public String getDefaultStorageLocation() {
		return "kb/kb-patch.xml";
	}

	/** 
	 * not implemented
	 */
	public Document save(KnowledgeBase kb) {
		return null;
	}

	

}
