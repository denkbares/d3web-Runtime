package de.d3web.core.knowledge;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;

public interface KnowledgeStore {

	void addKnowledge(Class<? extends PSMethod> solver, MethodKind kind, KnowledgeSlice slice);

	void removeKnowledge(Class<? extends PSMethod> solver, MethodKind kind, KnowledgeSlice slice);

	KnowledgeSlice[] getKnowledge(Class<? extends PSMethod> solver, MethodKind kind);

	KnowledgeSlice[] getKnowledge();

}
