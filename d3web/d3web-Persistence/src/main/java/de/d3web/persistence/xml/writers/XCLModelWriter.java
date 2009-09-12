/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.xml.writers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.psMethods.xclPattern.XCLModel;
import de.d3web.kernel.psMethods.xclPattern.XCLRelation;
import de.d3web.persistence.utilities.XCLModelComparator;
import de.d3web.persistence.xml.BasicPersistenceHandler;
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
		
		ArrayList<KnowledgeSlice> slices = new ArrayList<KnowledgeSlice>(kb.getAllKnowledgeSlicesFor(PSMethodXCL.class));
		Collections.sort(slices, new XCLModelComparator());
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
