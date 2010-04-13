package de.d3web.xcl;

import java.util.Collection;

import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.session.Session;

public interface InferenceTrace {

	public abstract DiagnosisState getState();

	public abstract Collection<XCLRelation> getPosRelations();

	public abstract Collection<XCLRelation> getNegRelations();

	public abstract Collection<XCLRelation> getContrRelations();

	public abstract Collection<XCLRelation> getReqPosRelations();

	public abstract Collection<XCLRelation> getReqNegRelations();

	public abstract Collection<XCLRelation> getSuffRelations();

	public abstract double getScore();

	public abstract double getSupport();
	
	public void refreshRelations(XCLModel xclModel, Session session);

}