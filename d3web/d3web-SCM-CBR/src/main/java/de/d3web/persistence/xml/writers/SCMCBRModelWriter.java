package de.d3web.persistence.xml.writers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.SCMCBR.PSMethodSCMCBR;
import de.d3web.kernel.psMethods.SCMCBR.SCMCBRModel;
import de.d3web.kernel.psMethods.SCMCBR.SCMCBRRelation;
import de.d3web.persistence.SCMCBRModelPersistenceHandler;
import de.d3web.persistence.xml.BasicPersistenceHandler;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;

/**
 * This writer produces a XMLRepresentation of an XCLModel-Object
 * @author kazamatzuri
 *
 */
public class SCMCBRModelWriter implements IXMLWriter{
	private static SCMCBRModelWriter instance;
	public static final String ID = SCMCBRModelWriter.class.getName();
	private Map writers = null;
	private SCMCBRModelWriter(){
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
			Logger.getLogger(SCMCBRModelPersistenceHandler.class.getName()).severe(o.getClass().getName()+" is no KnowledgeBase");
			return null;
		}
	
	}
	
	private StringBuffer generateKnowledgeBaseXML(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='UTF-8' ?>");
		sb.append("<KnowledgeBase type='" + SCMCBRModelPersistenceHandler.ID
				+ "' system='d3web'>");		
		
		List<Diagnosis> dias = kb.getDiagnoses();
		sb.append("<KnowledgeSlices>");		
		
		Collection<KnowledgeSlice> slices = kb.getAllKnowledgeSlicesFor(PSMethodSCMCBR.class);
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
			Logger.getLogger(this.getClass().getName()).warning("null is no SCMCBRModel");
		} else if (!(o instanceof SCMCBRModel)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no SCMCBRodel");
		} else {
			SCMCBRModel xclmodel = (SCMCBRModel) o;
			retVal="<SCMCBRModel";
			retVal+=" minSupport=\""+xclmodel.getMinSupport()+"\"";
			retVal+=" suggestedThreshold=\""+xclmodel.getSuggestedThreshold()+"\"";
			retVal+=" establishedThreshold=\""+xclmodel.getEstablishedThreshold()+"\"";
			retVal+=" coveringSuggestedThreshold=\""+xclmodel.getCoveringSuggestedThreshold()+"\"";
			retVal+=" coveringEstablishedThreshold=\""+xclmodel.getCoveringEstablishedThreshold()+"\"";
			retVal+=" completenessSuggestedThreshold=\""+xclmodel.getCompletenessSuggestedThreshold()+"\"";
			retVal+=" completenessEstablishedThreshold=\""+xclmodel.getCompletenessEstablishedThreshold()+"\"";
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
			retVal+="</SCMCBRModel>";
		}
		return retVal;
	}
	
	private String relationstoString(Collection<SCMCBRRelation> relations){
		String erg="";
		for (SCMCBRRelation current:relations)
			erg+=relationtoString(current);		
		return erg;

	}
	
	private String relationtoString(SCMCBRRelation r){
		String rel="<relation ID=\""+r.getId()+"\">";
		ConditionsPersistenceHandler cph=ConditionsPersistenceHandler.getInstance();
		AbstractCondition cond = r.getConditionedFinding();
		if(cond == null) {
			Logger.getLogger(SCMCBRModelWriter.class.getName()).severe("AbstractCondition is null: "+r.getId());
			return null;
		}
		rel+=cph.toXML(cond);		
		if (r.getWeight()!=r.DEFAULT_WEIGHT)
			rel+="<weight>"+r.getWeight()+"</weight>";
		
		
		
		
		rel+="</relation>";
		return rel;
	}

	public static SCMCBRModelWriter getInstance() {
			if (instance==null)
				instance=new SCMCBRModelWriter();
		return instance;
	}



}
