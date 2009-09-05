package de.d3web.dialog2.basics.persistence;

import java.net.URL;

import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * Classes implementing this interface can be registered at
 * DialogPersistenceManager. Alike AuxilliaryPersistenceHandler, they are
 * loading further knowledge to store in the knowledgebase-object. However (in
 * contrast to AuxilliaryPersistenceHandler), the further knowledge does not
 * have to (must not) be specified within the knowledgebase (jar) file, but on
 * other locations. AdditionalDialogConfigKnowledgeLoader are intended to load
 * configurational knowledge, which is to use within the dialog, but which shall
 * not be stored within the knowledgebase (jar) file.
 * 
 * @author gbuscher
 */
public interface AdditionalDialogConfigKnowledgeLoader {

    public String getId();

    public void loadAdditionalDialogConfigKnowledge(KnowledgeBase kb,
	    String kbId, URL filename);

}
