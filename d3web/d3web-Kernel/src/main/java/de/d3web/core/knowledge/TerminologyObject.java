package de.d3web.core.knowledge;

import de.d3web.core.knowledge.terminology.IDObject;

public interface TerminologyObject extends IDObject {

	// --- structure of terminology ---
	TerminologyObject[] getParents();

	TerminologyObject[] getChildren();

	// --- storing knowledge ---
	// KnowledgeStore getKnowledgeStore();

}
