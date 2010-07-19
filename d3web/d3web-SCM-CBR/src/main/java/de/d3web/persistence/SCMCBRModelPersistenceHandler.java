package de.d3web.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

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
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.kernel.psmethods2.scmcbr.PSMethodSCMCBR;
import de.d3web.kernel.psmethods2.scmcbr.SCMCBRModel;
import de.d3web.kernel.psmethods2.scmcbr.SCMCBRRelation;

/**
 * 
 * @author Reinhard Hatko
 * Created: 25.09.2009
 *
 */
public class SCMCBRModelPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {
	public static String ID = "scmcbrpattern";

	//TODO: Move to XML
	@Deprecated
	public String getDefaultStorageLocation() {

		return "kb/scmcbr.xml";
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream,
			ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		listener.updateProgress(0, "Loading knowledge base");
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement
				.createInstance(kb);
		NodeList xclmodels = doc.getElementsByTagName("SCMCBRModel");
		int cur = 0;
		int max = xclmodels.getLength();
		for (int i = 0; i < xclmodels.getLength(); i++) {

			Node current = xclmodels.item(i);

			addKnowledge(kbm, current);
			listener.updateProgress(++cur / max, "Loading SCMCBR Models");
		}
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream,
			ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", SCMCBRModelPersistenceHandler.ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);
		
		Collection<KnowledgeSlice> slices = kb.getAllKnowledgeSlicesFor(PSMethodSCMCBR.class);
		
		int cur = 0;
		for (KnowledgeSlice model : slices) {
			ksNode.appendChild(getModelElement((SCMCBRModel) model, doc));
			listener.updateProgress(++cur/slices.size(), "Saving knowledge base: SCMCBR Models");
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}

	private String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)) {
			try {
				return node.getAttributes().getNamedItem(name).getNodeValue();
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	private void addKnowledge(KnowledgeBaseManagement kbm, Node current) throws IOException {
		String solutionID = getAttribute("SID", current);
		String ID = getAttribute("ID", current);
		String minSupportS = getAttribute("minSupport", current);
		String suggestedThresholdS = getAttribute("suggestedThreshold", current);
		String establishedThresholdS = getAttribute("establishedThreshold",
				current);
		String coveringSuggestedThreshold = getAttribute("coveringSuggestedThreshold", current);
		String coveringEstablishedThreshold = getAttribute("coveringEstablishedThreshold",
				current);
		String completenessSuggestedThreshold = getAttribute("completenessSuggestedThreshold", current);
		String completenessEstablishedThreshold = getAttribute("completenessEstablishedThreshold",
				current);
		Solution diag = kbm.findSolution(solutionID);
		SCMCBRModel model = new SCMCBRModel(diag);
		NodeList relations =  current.getChildNodes();
		for(int i = 0; i < relations.getLength(); i++) {
			addRelations(kbm, model, relations.item(i).getChildNodes());	
		}
		
		model.setId(ID);
		model.setMinSupport(Double.parseDouble(minSupportS));
		model.setSuggestedThreshold(Double.parseDouble(suggestedThresholdS));
		model.setEstablishedThreshold(Double.parseDouble(establishedThresholdS));		

		model.setCompletenessEstablishedThreshold(Double.parseDouble(completenessEstablishedThreshold));
		model.setCompletenessSuggestedThreshold(Double.parseDouble(completenessSuggestedThreshold));
		model.setCoveringEstablishedThreshold(Double.parseDouble(coveringEstablishedThreshold));
		model.setCoveringSuggestedThreshold(Double.parseDouble(coveringSuggestedThreshold));
		
		diag.addKnowledge(PSMethodSCMCBR.class, model, SCMCBRModel.SCMCBR);

	}

	private void addRelations(KnowledgeBaseManagement kbm, SCMCBRModel model,
			NodeList relationsOfAType) throws IOException {
		Node aRelation;
		for (int i = 0; i < relationsOfAType.getLength(); i++) {
			aRelation = relationsOfAType.item(i);			
			String type = aRelation.getParentNode().getNodeName();
			String id = getAttribute("ID", aRelation);
			NodeList children = aRelation.getChildNodes();
			Condition ac = null;
			double weight = SCMCBRRelation.DEFAULT_WEIGHT;
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
				SCMCBRRelation rel = SCMCBRRelation.createSCMCBRRelation(ac, weight,id);
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
	
	public Element getModelElement(SCMCBRModel o, Document doc) throws IOException {
		Element modelelement = doc.createElement("XCLModel");
		modelelement.setAttribute("minSupport", ""+o.getMinSupport());
		modelelement.setAttribute("suggestedThreshold", ""+o.getSuggestedThreshold());
		modelelement.setAttribute("establishedThreshold", ""+o.getEstablishedThreshold());
		modelelement.setAttribute("coveringSuggestedThreshold", ""+o.getCoveringSuggestedThreshold());
		modelelement.setAttribute("coveringEstablishedThreshold", ""+o.getCoveringEstablishedThreshold());
		modelelement.setAttribute("completenessSuggestedThreshold", ""+o.getCompletenessSuggestedThreshold());
		modelelement.setAttribute("completenessEstablishedThreshold", ""+o.getCompletenessEstablishedThreshold());
		modelelement.setAttribute("ID", o.getId());
		modelelement.setAttribute("SID", o.getSolution().getId());
		modelelement.appendChild(getRelationsElement(o.getNecessaryRelations(),"necessaryRelations", doc));
		modelelement.appendChild(getRelationsElement(o.getSufficientRelations(), "sufficientRelations", doc));
		modelelement.appendChild(getRelationsElement(o.getContradictingRelations(), "contradictingRelations", doc));
		modelelement.appendChild(getRelationsElement(o.getRelations(), "Relations", doc));
		return modelelement;
	}
	
	private Element getRelationsElement(Collection<SCMCBRRelation> relations, String relationstext, Document doc) throws IOException{
		Element relationsElement = doc.createElement(relationstext);
		for (SCMCBRRelation current:relations) {
			relationsElement.appendChild(getRelationElement(current, doc));
		}
		return relationsElement;
	}
	
	private Element getRelationElement(SCMCBRRelation r, Document doc) throws IOException {
		Element relationElement = doc.createElement("relation");
		relationElement.setAttribute("ID", r.getId());
		Condition cond = r.getConditionedFinding();
		if (cond != null) {
			relationElement.appendChild(PersistenceManager.getInstance().writeFragment(cond, doc));
		} else {
			throw new IOException("Missing condition.");
		}
		if (r.getWeight()!=SCMCBRRelation.DEFAULT_WEIGHT) {
			Element weight = doc.createElement("weight");
			weight.setTextContent(""+r.getWeight());
			relationElement.appendChild(weight);
		}
		return relationElement;
	}
}