package de.d3web.persistence.xml.writers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;

import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.psMethods.xclPattern.XCLModel;
import de.d3web.kernel.psMethods.xclPattern.XCLRelation;
import de.d3web.persistence.xml.BasicPersistenceHandler;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.XCLModelPersistenceHandler;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * This writer produces a XMLRepresentation of an XCLModel-Object
 * @author kazamatzuri
 *
 */
public class XCLModelWriter implements IXMLWriter{
	private static XCLModelWriter instance;
	public static final String ID = XCLModelWriter.class.getName();
	private Map writers = null;
	private XCLModelWriter(){
		writers = new HashMap();
		//TODO own xcldiagnosiswriter...
		writers.put(DiagnosisWriter.class, new DiagnosisWriter());
		writers.put(QuestionWriter.class, new QuestionWriter());
		BasicPersistenceHandler bph=new BasicPersistenceHandler();
		
		//writers.put(SCRelation.class, SCRelationWriter.getInstance());
	}
	private IXMLWriter getWriter(Class key) {
		return (IXMLWriter) writers.get(key);
	}
	public String getXMLString(Object o) {
	
		if (o instanceof KnowledgeBase) {
			return generateKnowledgeBaseXML((KnowledgeBase) o).toString();
		} else {
			Logger.getLogger(XCLModelPersistenceHandler.class.getName()).severe(o.getClass().getName()+" is no KnowledgeBase");
			return null;
		}
	
	}
	
	private StringBuffer generateKnowledgeBaseXML(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='UTF-8' ?>");
		sb.append("<KnowledgeBase type='" + XCLModelPersistenceHandler.ID
				+ "' system='d3web'>");		
		
		List<Diagnosis> dias = kb.getDiagnoses();
		sb.append("<KnowledgeSlices>");		
		
		Collection<KnowledgeSlice> slices = kb.getAllKnowledgeSlicesFor(PSMethodXCL.class);
		for (KnowledgeSlice model : slices) {
			sb.append(getXMLStringXCLM(model));
		}
		sb.append("</KnowledgeSlices>");

		sb.append("</KnowledgeBase>");
		return sb;
	}
	
	public String getXMLStringXCLM(Object o) {
		String retVal = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no XCLModel");
		} else if (!(o instanceof XCLModel)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no XCLodel");
		} else {
			XCLModel xclmodel = (XCLModel) o;
			retVal="<XCLModel";
			if (xclmodel.getMinSupport()!=xclmodel.defaultMinSupport)
				retVal+=" minSupport=\""+xclmodel.getMinSupport()+"\"";
			if (xclmodel.getSuggestedThreshold()!=xclmodel.defaultSuggestedThreshold)
				retVal+=" suggestedThreshold=\""+xclmodel.getSuggestedThreshold()+"\"";
			if (xclmodel.getEstablishedThreshold()!=xclmodel.defaultEstablishedThreshold)
				retVal+=" establishedThreshold=\""+xclmodel.getEstablishedThreshold()+"\"";			
			retVal+=" ID=\""+xclmodel.getId()+"\"";
			retVal+=" SID=\""+xclmodel.getSolution().getId()+"\"";
			retVal+=">";			
			retVal+="<necessaryRelations>";			
			retVal+=relationstoString(xclmodel.getNecessaryRelations());			
			retVal+="</necessaryRelations>";
			retVal+="<sufficientRelations>";
			retVal+=relationstoString(xclmodel.getSufficientRelations());
			retVal+="</sufficientRelations>";
			retVal+="<contradictingRelations>";
			retVal+=relationstoString(xclmodel.getContradictingRelations());
			retVal+="</contradictingRelations>";
			retVal+="<Relations>";
			retVal+=relationstoString(xclmodel.getRelations());
			retVal+="</Relations>";			
			retVal+="</XCLModel>";
		}
		return retVal;
	}
	
	private String relationstoString(Collection<XCLRelation> relations){
		String erg="";
		for (XCLRelation current:relations)
			erg+=relationtoString(current);		
		return erg;

	}
	
	private String relationtoString(XCLRelation r){
		String rel="<relation ID=\""+r.getId()+"\">";
		ConditionsPersistenceHandler cph=ConditionsPersistenceHandler.getInstance();
		AbstractCondition cond = r.getConditionedFinding();
		if(cond == null) {
			Logger.getLogger(XCLModelWriter.class.getName()).severe("AbstractCondition is null: "+r.getId());
			return null;
		}
		rel+=cph.toXML(cond);		
		if (r.getWeight()!=r.DEFAULT_WEIGHT)
			rel+="<weight>"+r.getWeight()+"</weight>";
		rel+="</relation>";
		return rel;
	}

	public static XCLModelWriter getInstance() {
			if (instance==null)
				instance=new XCLModelWriter();
		return instance;
	}



}
