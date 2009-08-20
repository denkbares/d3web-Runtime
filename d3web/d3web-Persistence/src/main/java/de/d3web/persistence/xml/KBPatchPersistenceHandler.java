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
