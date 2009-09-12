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

package de.d3web.kernel.psMethods.setCovering.persistence.loader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.psMethods.setCovering.Finding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCKnowledge;
import de.d3web.kernel.psMethods.setCovering.SCKnowledgeFactory;
import de.d3web.kernel.psMethods.setCovering.SCNode;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.loader.ConditionFactory;

/**
 * This is the loader for all SCM-Knowledge
 * 
 * @author bates
 */
public class SCMLoader implements ProgressNotifier {

	private Vector progressListeners = null;

	private List nodes = null;

	private static SCMLoader instance = null;
	private KBLoaderDummy kbLoaderDummy = null;

	private ProgressEvent everLastingProgressEvent = null;

	private Map<Diagnosis, SCDiagnosis> scDiagMap = new HashMap<Diagnosis, SCDiagnosis>();
	
	private SCMLoader() {
		nodes = new LinkedList();
		progressListeners = new Vector();
		everLastingProgressEvent = new ProgressEvent(this, 0, 0, null, 0, 0);
	}

	public static SCMLoader getInstance() {
		if (instance == null) {
			instance = new SCMLoader();
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

	/**
	 * Adds SCM-knowledge from the Document to the given KnowledgeBase
	 * 
	 * @param kb
	 *            knowledge base to add SCM-knowledge to
	 * @param doc
	 *            DOM-Document containing the SCM-knowledge
	 */
	public KnowledgeBase loadKnowledgeSlices(KnowledgeBase kb, Document doc) {
		scDiagMap.clear();
		everLastingProgressEvent.type = ProgressEvent.START;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.PersistenceManager.loadKB");
		everLastingProgressEvent.currentValue = 0;
		everLastingProgressEvent.finishedValue = 1;
		fireProgressEvent(everLastingProgressEvent);

		kbLoaderDummy = new KBLoaderDummy(kb);
		NodeList screlations = doc.getElementsByTagName("KnowledgeSlice");
		// System.out.println("Loading " + screlations.getLength() + "
		// relations.");
		long startMillis = System.currentTimeMillis();

		everLastingProgressEvent.finishedValue = screlations.getLength();

		for (int i = 0; i < screlations.getLength(); ++i) {
			Node screlation = screlations.item(i);
			String type = getAttribute("type", screlation);
			if (type.equalsIgnoreCase("SCRelation")) {
				addRelationToKnowledgeBase(kb, screlation);
				if (i % 100 == 0) {
					everLastingProgressEvent.type = ProgressEvent.UPDATE;
					everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
					everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
							.getString("d3web.Persistence.SCMLoader.loadSCMSlice")
							+ i
							+ PersistenceManager.resourceBundle
									.getString("d3web.Persistence.SCMLoader.loadSCMSliceOf")
							+ screlations.getLength();
					everLastingProgressEvent.currentValue += 100;
					fireProgressEvent(everLastingProgressEvent);
				}
			}
		}
		double time = System.currentTimeMillis() - startMillis;
		// System.out.println(time / 1000 + " seconds.");
		everLastingProgressEvent.type = ProgressEvent.DONE;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.PersistenceManager.loadSCM");
		everLastingProgressEvent.currentValue = 1;
		everLastingProgressEvent.finishedValue = 1;
		fireProgressEvent(everLastingProgressEvent);
		return kb;
	}

	private void addRelationToKnowledgeBase(KnowledgeBase kb, Node node) {
		SCNode sourceNode = null;
		SCNode targetNode = null;
		List scKnowledgeList = new LinkedList();
		String id = getAttribute("ID", node);
		if (id == null) {
			id = getAttribute("id", node);
		}
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeName().equalsIgnoreCase("Source")) {
				sourceNode = retrieveNode(kb, child);
			} else if (child.getNodeName().equalsIgnoreCase("Target")) {
				targetNode = retrieveNode(kb, child);
			} else if (child.getNodeName().equalsIgnoreCase("KnowledgeMap")) {
				NodeList knowledgeList = child.getChildNodes();
				for (int k = 0; k < knowledgeList.getLength(); ++k) {
					Node knowledge = knowledgeList.item(k);
					if (knowledge.getNodeType() == Node.ELEMENT_NODE) {
						String value = getAttribute("value", knowledge);
						String type = getAttribute("type", knowledge);
						if (type == null) {
							type = "confirmationCategory";
							value = value = getAttribute("confirmationCategory", knowledge);
						}
						SCKnowledge scKnowledge = SCKnowledgeFactory.createSCKnowledge(type, value);
						scKnowledgeList.add(scKnowledge);
					}
				}
			}
		}

		// create the Relation (factory will add it as KnowledgeSlice)
		SCRelationFactory.createSCRelation(id, sourceNode, targetNode, scKnowledgeList);
	}

	private SCNode retrieveNode(KnowledgeBase kb, Node node) {
		SCNode ret = null;

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String type = getAttribute("type", child);
				if (type != null) {
					if (type.equalsIgnoreCase("SCDiagnosis")) {
						ret = createSCDiagnosis(kb, child);
					} else if (type.equalsIgnoreCase("Finding")) {
						ret = createFinding(kb, child);
					}
				} else {
					System.err.println("node without type:" + child);
				}
			}
		}
		int index = nodes.indexOf(ret);
		if (index >= 0) {
			ret = (SCNode) nodes.get(index);
		}
		return ret;
	}

	private Finding createFinding(KnowledgeBase kb, Node node) {
		AbstractCondition condition = null;
		NodeList condList = node.getChildNodes();
		for (int i = 0; i < condList.getLength(); ++i) {
			Node condNode = condList.item(i);
			if ((condNode.getNodeType() == Node.ELEMENT_NODE)
					&& (condNode.getNodeName().equals("Condition"))) {
				condition = ConditionFactory.createCondition(condNode, kbLoaderDummy, null);
			}
		}
		return SCNodeFactory.createPredictedFinding(condition);
	}

	private SCDiagnosis createSCDiagnosis(KnowledgeBase kb, Node node) {
		SCDiagnosis scDiagnosis = new SCDiagnosis();

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeName().equalsIgnoreCase("Diagnosis")) {
				String id = getAttribute("id", child);
				if (id == null) {
					id = getAttribute("ID", child);
				}
				Diagnosis diagnosis = kb.searchDiagnosis(id);
				if(scDiagMap.get(diagnosis) != null) {
					return scDiagMap.get(diagnosis);
				} // [HOTFIX]: Peter: create SCDiagnosis lazy = only 1 instance!
				scDiagnosis.setNamedObject(diagnosis);
				scDiagMap.put(diagnosis, scDiagnosis);
			}
		}
		String apriori = getAttribute("apriori", node);
		double aprioriParsed = 0.5;
		try {
			aprioriParsed = Double.parseDouble(apriori);
		} catch (Exception x) {
			// System.err.println("Apriori " + apriori + " is not a double
			// value!");
			// getting apriori from heuristic knowledge
			Score aprScore = ((Diagnosis) scDiagnosis.getNamedObject()).getAprioriProbability();
			if (aprScore != null) {
				// aprioriParsed = aprScore.getAPriori();
				// [TODO]: normalize the heuristic apriori-probs...
			}
		}
		scDiagnosis.setAprioriProbability(aprioriParsed);
		
		return scDiagnosis;
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

}
