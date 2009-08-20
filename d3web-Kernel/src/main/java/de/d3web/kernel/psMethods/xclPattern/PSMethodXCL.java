package de.d3web.kernel.psMethods.xclPattern;

import java.util.Collection;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.IEventSource;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.psMethods.PSMethodAdapter;

public class PSMethodXCL extends PSMethodAdapter {
	
	private static PSMethodXCL instance = null;

	private PSMethodXCL() {
		super();
	}
	
	public static PSMethodXCL getInstance() {
		if (instance  == null) {
			instance = new PSMethodXCL();
		}
		return instance;
	}
	
	public DiagnosisState getState(XPSCase theCase, Diagnosis diagnosis) {
		
		//TODO implement me!
		return null;
	}
	
	public void propagate(
			XPSCase theCase,
			NamedObject nob,
			Object[] newValue) {

		Collection<KnowledgeSlice> models = theCase.getKnowledgeBase().getAllKnowledgeSlicesFor(PSMethodXCL.class);
		for (KnowledgeSlice knowledgeSlice : models) {
			if(knowledgeSlice instanceof XCLModel) {
				//((XCLModel)knowledgeSlice).getState(theCase);
				((XCLModel)knowledgeSlice).notifyListeners(theCase, ((XCLModel)knowledgeSlice));
			}
		} 
		
		
	}
	
}
