package de.d3web.core.kr;

import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethod;

public interface KnowledgeStore {
	
	void addKnowledge(Class<? extends PSMethod> solver, MethodKind kind, KnowledgeSlice slice);
	void removeKnowledge(Class<? extends PSMethod> solver, MethodKind kind, KnowledgeSlice slice);
	
	KnowledgeSlice[] getKnowledge(Class<? extends PSMethod> solver, MethodKind kind);
	KnowledgeSlice[] getKnowledge();

}
