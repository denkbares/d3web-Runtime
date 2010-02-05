package de.d3web.core.session;

import java.util.Collection;

import de.d3web.core.kr.InfoStore;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PropagationContoller;

/**
 * The Session interface represents a running xps session.
 * This interface replaces the formerly used XPSCase.
 * 
 * @author volker_belli
 *
 */
public interface Session {
	
	// --- manage problem solvers ---
	Collection<? extends PSMethod> getPSMethods();
	PSMethod getPSMethodInstance(Class<? extends PSMethod> solverClass);
	void addPSMethod(PSMethod solver);
	void removePSMethod(PSMethod solver);
	
	// --- access information ---
	Interview getInterviewManager();
	Blackboard getBlackboard();
	PropagationContoller getPropagationContoller();
	
	// --- access header information ---
	KnowledgeBase getKnowledgeBase();
	InfoStore getInfoStore(); // some information will be created/updated automatically (id, change-date, create-date)
	
	// --- reserved for later implementation --- (inkrement 2)
	//Protocol getProtocol();
}
