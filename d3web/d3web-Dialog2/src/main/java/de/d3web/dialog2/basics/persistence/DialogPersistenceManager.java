package de.d3web.dialog2.basics.persistence;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.xml.AuxiliaryPersistenceHandler;
import de.d3web.persistence.xml.CaseRepositoryHandler;
import de.d3web.persistence.xml.PersistenceManager;

/**
 * @author gbuscher PersistenceManager that also loads dialog-specific files,
 *         that are not defined through index-data of the jar-knowledgebases.
 */
public class DialogPersistenceManager extends PersistenceManager {

    private static DialogPersistenceManager instance = null;

    public static DialogPersistenceManager getInstance() {
	if (instance == null) {
	    instance = new DialogPersistenceManager();
	}
	return instance;
    }

    /**
     * maps Ids of loaders to loaders.
     */
    private Map<String, AdditionalDialogConfigKnowledgeLoader> additionalConfigKnowledgeLoaders = new HashMap<String, AdditionalDialogConfigKnowledgeLoader>();

    private DialogPersistenceManager() {
	addAdditionalLoader(new MultimediaKnowledgeLoader());
    }

    /**
     * Registers an AdditionalDialogConfigKnowledgeLoader with its own id.
     */
    public void addAdditionalLoader(AdditionalDialogConfigKnowledgeLoader loader) {
	additionalConfigKnowledgeLoaders.put(loader.getId(), loader);
    }

    @Override
    public void addCaseRepositoryHandler(CaseRepositoryHandler handler) {
	super.addCaseRepositoryHandler(handler);
	// very ugly fix: as long as PersistenceManager.getInstance is not the
	// same as this instance...
	PersistenceManager.getInstance().addCaseRepositoryHandler(handler);
    }

    @Override
    public void addPersistenceHandler(AuxiliaryPersistenceHandler handler) {
	super.addPersistenceHandler(handler);
	// very ugly fix: as long as PersistenceManager.getInstance is not the
	// same as this instance...
	PersistenceManager.getInstance().addPersistenceHandler(handler);
    }

    private void executeAdditionalDialogConfigKnowledgeLoaders(
	    KnowledgeBase kb, String kbid, URL filename) {
	if (kbid == null) {
	    kbid = kb.getId();
	}
	Iterator<AdditionalDialogConfigKnowledgeLoader> iter = additionalConfigKnowledgeLoaders
		.values().iterator();
	while (iter.hasNext()) {
	    AdditionalDialogConfigKnowledgeLoader loader = iter.next();
	    try {
		loader.loadAdditionalDialogConfigKnowledge(kb, kbid, filename);
	    } catch (Exception ex) {
		Logger.getLogger(this.getClass().getName()).throwing(
			this.getClass().getName(),
			"executeAdditionalDialogConfigKnowledgeLoaders", ex);
	    }
	}
    }

    @Override
    public KnowledgeBase load(URL baseURL) {
	return load(baseURL, true);
    }

    /**
     * Loads the knowledgebase that is defined by xml-files in a jar-file
     * (baseURL).
     */
    @Override
    public KnowledgeBase load(URL baseURL, boolean loadPatch) {
	return load(baseURL, loadPatch, null);
    }

    /**
     * Loads the knowledgebase that is defined by xml-files in a jar-file
     * (baseURL). If existing, a knowledgebase-specific java-code will be
     * executed.
     */
    public KnowledgeBase load(URL baseURL, boolean loadPatch, String kbid) {
	KnowledgeBase kb = super.load(baseURL, loadPatch);
	executeAdditionalDialogConfigKnowledgeLoaders(kb, kbid, baseURL);
	return kb;
    }

}
