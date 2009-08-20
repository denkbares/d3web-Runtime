package de.d3web.persistence.xml.loader;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.psMethods.xclPattern.XCLModel;
import de.d3web.kernel.psMethods.xclPattern.XCLRelation;

import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.loader.ConditionFactory;

public class XCLModelLoader

implements ProgressNotifier {

	private Vector progressListeners = null;

	private List nodes = null;

	private static XCLModelLoader instance = null;

	private ProgressEvent everLastingProgressEvent = null;

	private XCLModelLoader() {
		nodes = new LinkedList();
		progressListeners = new Vector();
		everLastingProgressEvent = new ProgressEvent(this, 0, 0, null, 0, 0);
	}

	public static XCLModelLoader getInstance() {
		if (instance == null) {
			instance = new XCLModelLoader();
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
		NodeList xclmodels = doc.getElementsByTagName("XCLModel");
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
		Diagnosis diag = kbm.findDiagnosis(solutionID);
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
		diag.addKnowledge(PSMethodXCL.class, model, XCLModel.XCLMODEL);

	}

	private void addRelations(KnowledgeBaseManagement kbm, XCLModel model,
			NodeList relationsOfAType) {
		Node aRelation;
		KBLoader loader = new KBLoaderDummy(kbm.getKnowledgeBase());
		for (int i = 0; i < relationsOfAType.getLength(); i++) {
			aRelation = relationsOfAType.item(i);			
			String type = aRelation.getParentNode().getNodeName();
			String id = getAttribute("ID", aRelation);
			NodeList children = aRelation.getChildNodes();
			AbstractCondition ac = null;
			double weight = XCLRelation.DEFAULT_WEIGHT;
			for (int t = 0; t < children.getLength(); t++) {
				Node child = children.item(t);
				
				if (child .getNodeName().equals("Condition")) {					
						//TODO: check jochen
					child .getTextContent();
					ac = ConditionFactory.createCondition(child , loader,PSMethodXCL.class);			
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
