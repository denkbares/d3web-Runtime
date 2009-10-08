package de.d3web.persistence.xml.loaders;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.SCMCBR.PSMethodSCMCBR;
import de.d3web.kernel.psMethods.SCMCBR.SCMCBRModel;
import de.d3web.kernel.psMethods.SCMCBR.SCMCBRRelation;

import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.loader.ConditionFactory;
import de.d3web.persistence.xml.loader.KBLoader;
import de.d3web.persistence.xml.loader.KBLoaderDummy;

public class SCMCBRModelLoader

implements ProgressNotifier {

	private Vector progressListeners = null;

	private List nodes = null;

	private static SCMCBRModelLoader instance = null;

	private ProgressEvent everLastingProgressEvent = null;

	private SCMCBRModelLoader() {
		nodes = new LinkedList();
		progressListeners = new Vector();
		everLastingProgressEvent = new ProgressEvent(this, 0, 0, null, 0, 0);
	}

	public static SCMCBRModelLoader getInstance() {
		if (instance == null) {
			instance = new SCMCBRModelLoader();
		}
		return instance;
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

	public void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);

	}

	public void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);

	}

	public void fireProgressEvent(ProgressEvent evt) {
		Enumeration enu = progressListeners.elements();
		while (enu.hasMoreElements())
			((de.d3web.persistence.progress.ProgressListener) enu.nextElement())
					.updateProgress(evt);
	}

	public long getProgressTime(int operationType, Object additionalInformation) {
		return PROGRESSTIME_UNKNOWN;
	}

	public KnowledgeBase loadKnowledgeSlices(KnowledgeBase kb, Document doc) {
		everLastingProgressEvent.type = ProgressEvent.START;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.PersistenceManager.loadKB");
		everLastingProgressEvent.currentValue = 0;
		everLastingProgressEvent.finishedValue = 1;
		fireProgressEvent(everLastingProgressEvent);

		KnowledgeBaseManagement kbm = KnowledgeBaseManagement
				.createInstance(kb);
		NodeList xclmodels = doc.getElementsByTagName("SCMCBRModel");
		everLastingProgressEvent.finishedValue = xclmodels.getLength();
		for (int i = 0; i < xclmodels.getLength(); i++) {
			
			Node current = xclmodels.item(i);
			
			addKnowledge(kbm, current);
			everLastingProgressEvent.currentValue += 1;
			fireProgressEvent(everLastingProgressEvent);
		}
		
		return kb;
	}

	private void addKnowledge(KnowledgeBaseManagement kbm, Node current) {
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
		Diagnosis diag = kbm.findDiagnosis(solutionID);
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
			NodeList relationsOfAType) {
		Node aRelation;
		KBLoader loader = new KBLoaderDummy(kbm.getKnowledgeBase());
		for (int i = 0; i < relationsOfAType.getLength(); i++) {
			aRelation = relationsOfAType.item(i);			
			String type = aRelation.getParentNode().getNodeName();
			String id = getAttribute("ID", aRelation);
			NodeList children = aRelation.getChildNodes();
			AbstractCondition ac = null;
			double weight = SCMCBRRelation.DEFAULT_WEIGHT;
			for (int t = 0; t < children.getLength(); t++) {
				Node child = children.item(t);
				
				if (child .getNodeName().equals("Condition")) {					
						//TODO: check jochen
					child .getTextContent();
					ac = ConditionFactory.createCondition(child , loader, PSMethodSCMCBR.class);			
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

}
