/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *                    denkbares GmbH
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

package de.d3web.xcl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.KnowledgeSliceComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.inference.PSMethodXCL;

/**
 * PersistenceHandler for XCLModels
 * 
 * @author kazamatzuri, Markus Friedrich (denkbares GmbH)
 *
 */
public class XCLModelPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {
	public static String ID = "xclpattern";
	
	@Deprecated
	public String getId() {
		return ID;
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		loadKnowledgeSlices(kb, doc, listener);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return kb.getAllKnowledgeSlicesFor(PSMethodXCL.class, XCLModel.XCLMODEL).size();
	}
	
	public Element getModelElement(XCLModel xclmodel, Document doc) throws IOException {
		Element modelelement = doc.createElement("XCLModel");
		if (xclmodel.getMinSupport()!=XCLModel.defaultMinSupport)
			modelelement.setAttribute("minSupport", ""+xclmodel.getMinSupport());
		if (xclmodel.getSuggestedThreshold()!=XCLModel.defaultSuggestedThreshold)
			modelelement.setAttribute("suggestedThreshold", ""+xclmodel.getSuggestedThreshold());
		if (xclmodel.getEstablishedThreshold()!=XCLModel.defaultEstablishedThreshold)
			modelelement.setAttribute("establishedThreshold", ""+xclmodel.getEstablishedThreshold());
		modelelement.setAttribute("ID", xclmodel.getId());
		modelelement.setAttribute("SID", xclmodel.getSolution().getId());
		modelelement.setAttribute("considerOnlyRelevantRelations", ""+xclmodel.isConsiderOnlyRelevantRelations());
		modelelement.appendChild(getRelationsElement(xclmodel.getNecessaryRelations(),"necessaryRelations", doc));
		modelelement.appendChild(getRelationsElement(xclmodel.getSufficientRelations(), "sufficientRelations", doc));
		modelelement.appendChild(getRelationsElement(xclmodel.getContradictingRelations(), "contradictingRelations", doc));
		modelelement.appendChild(getRelationsElement(xclmodel.getRelations(), "Relations", doc));
		return modelelement;
	}
	
	private Element getRelationsElement(Collection<XCLRelation> relations, String relationstext, Document doc) throws IOException{
		Element relationsElement = doc.createElement(relationstext);
		List<XCLRelation> relList = new ArrayList<XCLRelation>(relations);
		Collections.sort(relList, new XCLRelationComparator());
		for (XCLRelation current:relList) {
			relationsElement.appendChild(getRelationElement(current, doc));
		}
		return relationsElement;
	}
	
	private Element getRelationElement(XCLRelation r, Document doc) throws IOException {
		Element relationElement = doc.createElement("relation");
		relationElement.setAttribute("ID", r.getId());
		Condition cond = r.getConditionedFinding();
		if (cond != null) {
			relationElement.appendChild(PersistenceManager.getInstance().writeFragment(cond, doc));
		} else {
			throw new IOException("Missing condition.");
		}
		if (r.getWeight()!=XCLRelation.DEFAULT_WEIGHT) {
			Element weight = doc.createElement("weight");
			weight.setTextContent(""+r.getWeight());
			relationElement.appendChild(weight);
		}
		return relationElement;
	}

	public void write(KnowledgeBase kb, OutputStream stream,
			ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", XCLModelPersistenceHandler.ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);
		
		ArrayList<KnowledgeSlice> slices = new ArrayList<KnowledgeSlice>(kb.getAllKnowledgeSlicesFor(PSMethodXCL.class, XCLModel.XCLMODEL));
		Collections.sort(slices, new KnowledgeSliceComparator());
		float cur = 0;
		int max = getEstimatedSize(kb);
		for (KnowledgeSlice model : slices) {
			if (model instanceof XCLModel) {
				ksNode.appendChild(getModelElement((XCLModel) model, doc));
				listener.updateProgress(++cur/max, "Saving knowledge base: XCL Models");
			}
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}
	
	private String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null) && node.getAttributes().getNamedItem(name)!=null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return null;
	}


	public KnowledgeBase loadKnowledgeSlices(KnowledgeBase kb, Document doc, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Loading knowledge base");
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement
				.createInstance(kb);
		NodeList xclmodels = doc.getElementsByTagName("XCLModel");
		int cur = 0;
		int max = xclmodels.getLength();
		for (int i = 0; i < xclmodels.getLength(); i++) {
			
			Node current = xclmodels.item(i);
			
			addKnowledge(kbm, current);
			listener.updateProgress(++cur/max, "Loading knowledge base: XCL Models");
		}
		
		return kb;
	}

	private void addKnowledge(KnowledgeBaseManagement kbm, Node current) throws IOException {
		String solutionID = getAttribute("SID", current);
		String ID = getAttribute("ID", current);
		String minSupportS = getAttribute("minSupport", current);
		String suggestedThresholdS = getAttribute("suggestedThreshold", current);
		String establishedThresholdS = getAttribute("establishedThreshold",
				current);
		String considerOnlyRelevantRelations = getAttribute("considerOnlyRelevantRelations",
				current);
		Solution diag = kbm.findDiagnosis(solutionID);
		XCLModel model = new XCLModel(diag);
		NodeList relations =  current.getChildNodes();
		for(int i = 0; i < relations.getLength(); i++) {
			addRelations(kbm, model, relations.item(i).getChildNodes());	
		}
		
		model.setId(ID);
		if (minSupportS != null)
			model.setMinSupport(Double.parseDouble(minSupportS));
		if (suggestedThresholdS != null)
			model
					.setSuggestedThreshold(Double
							.parseDouble(suggestedThresholdS));
		if (establishedThresholdS != null)
			model.setEstablishedThreshold(Double
					.parseDouble(establishedThresholdS));
		if (considerOnlyRelevantRelations!=null) {
			model.setConsiderOnlyRelevantRelations(Boolean.parseBoolean(considerOnlyRelevantRelations));
		}
		diag.addKnowledge(PSMethodXCL.class, model, XCLModel.XCLMODEL);

	}

	private void addRelations(KnowledgeBaseManagement kbm, XCLModel model,
			NodeList relationsOfAType) throws IOException {
		Node aRelation;
		for (int i = 0; i < relationsOfAType.getLength(); i++) {
			aRelation = relationsOfAType.item(i);			
			String type = aRelation.getParentNode().getNodeName();
			String id = getAttribute("ID", aRelation);
			NodeList children = aRelation.getChildNodes();
			Condition ac = null;
			double weight = XCLRelation.DEFAULT_WEIGHT;
			for (int t = 0; t < children.getLength(); t++) {
				Node child = children.item(t);
				
				if (child .getNodeName().equals("Condition")) {					
						//TODO: check jochen
					child .getTextContent();
					ac = (Condition) PersistenceManager.getInstance().readFragment((Element) child, kbm.getKnowledgeBase());
				} else if (child .getNodeName().equals("weight")) {
					weight = Double.parseDouble(child.getTextContent());
				}
			}
			if (ac != null) {
				XCLRelation rel = XCLRelation.createXCLRelation(ac, weight,id);
				if (type.equals("Relations")) {
					model.addRelation(rel);
				} else if (type.equals("necessaryRelations")) {
					model.addNecessaryRelation(rel);
				} else if (type.equals("contradictingRelations")) {
					model.addContradictingRelation(rel);
				} else if (type.equals("sufficientRelations")) {
					model.addSufficientRelation(rel);
				}
			}

		}

	}

}
