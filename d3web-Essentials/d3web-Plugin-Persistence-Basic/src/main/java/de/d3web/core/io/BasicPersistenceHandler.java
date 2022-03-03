/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.denkbares.progress.ProgressListener;
import de.d3web.abstraction.ActionSetQuestion;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.Rule;
import de.d3web.core.io.fragments.PropertiesHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;
import de.d3web.core.utilities.NamedObjectComparator;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.ActionSuppressAnswer;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * PersistenceHandler for reading and writing basic knowledge Creation date:
 * (06.06.2001 15:26:13)
 * 
 * @author Michael Scharvogel, Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class BasicPersistenceHandler implements
		KnowledgeReader,
		KnowledgeWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicPersistenceHandler.class);
	public static final String BASIC_PERSISTENCE_HANDLER = "basic";

	/**
	 * @return the ID of this PersistenceHandler
	 */
	public String getId() {
		return BASIC_PERSISTENCE_HANDLER;
	}

	private float saveInitQuestions(Element father, KnowledgeBase kb, ProgressListener listener, float time, int abstime) throws IOException {
		List<? extends QASet> theList = kb.getInitQuestions();
		Document doc = father.getOwnerDocument();
		if (theList != null) {
			Iterator<? extends QASet> iter = theList.iterator();
			Element initQuestionsNode = doc.createElement("InitQuestions");
			while (iter.hasNext()) {
				listener.updateProgress(time++ / abstime, "Saving knowledge base: init QA sets");
				QASet o = iter.next();
				XMLUtil.appendQuestionLinkElement(initQuestionsNode, o);
			}
			father.appendChild(initQuestionsNode);
		}
		return time;
	}

	@Override
	public void read(PersistenceManager manager, KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Loading knowledge base");
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb, stream);
		Document doc = persistence.getDocument();

		List<Element> childNodes = XMLUtil.getElementList(doc.getChildNodes());
		if (childNodes.size() != 1) {
			throw new IOException("Document has more than one child.");
		}
		Element father = childNodes.get(0);
		Node idNode = father.getAttributes().getNamedItem("id");
		if (idNode != null) {
			String idString = idNode.getNodeValue();
			if (idString != null && !idString.equals("null")
					&& !idString.trim().equals("")) {
				kb.setId(idString);
			}
		}
		List<Element> kbchildren = XMLUtil.getElementList(father.getChildNodes());
		// splitting the kb children and calculating the absolute time to load
		int abstime = 0;
		List<Element> knowledgeSliceNodes = Collections.emptyList();
		List<Element> qASetNodes = new ArrayList<>();
		List<Element> diagnosisNodes = null;
		List<Element> initQuestionnodes = Collections.emptyList();
		String rootQASetID = null;
		String rootSolutionID = null;
		PropertiesHandler ph = new PropertiesHandler();
		for (Element child : kbchildren) {
			String name = child.getNodeName();
			if (name.equalsIgnoreCase("knowledgeslices")) {
				knowledgeSliceNodes = XMLUtil.getElementList(child.getChildNodes());
				abstime += knowledgeSliceNodes.size();
			}
			// former way of saving Questions
			else if (name.equalsIgnoreCase("Questions")) {
				qASetNodes.addAll(XMLUtil.getElementList(child.getChildNodes()));
			}
			else if (name.equalsIgnoreCase("diagnoses")) {
				diagnosisNodes = XMLUtil.getElementList(child.getChildNodes());
				abstime += diagnosisNodes.size();
			}
			// former way of saving QContainers
			else if (name.equalsIgnoreCase("QContainers")) {
				qASetNodes.addAll(XMLUtil.getElementList(child.getChildNodes()));
			}
			else if (name.equalsIgnoreCase("QASets")) {
				qASetNodes.addAll(XMLUtil.getElementList(child.getChildNodes()));
			}
			else if (name.equalsIgnoreCase("InitQuestions")
					// former name in previous versions
					|| name.equalsIgnoreCase("InitQASets")) {
				initQuestionnodes = XMLUtil.getElementList(child.getChildNodes());
				abstime += initQuestionnodes.size();
			}
			else if (name.equals("rootQASet")) {
				rootQASetID = child.getTextContent();
			}
			else if (name.equals("rootSolution")) {
				rootSolutionID = child.getTextContent();
			}
			else if (name.equals(XMLUtil.INFO_STORE)) {
				XMLUtil.fillInfoStore(persistence, kb.getInfoStore(), child);
			}
			// read old persistence format
			else if (ph.canRead(child)) {
				InfoStoreUtil.copyEntries(ph.read(persistence, child), kb.getInfoStore());
			}
		}
		abstime += qASetNodes.size();
		float time = 0;

		Map<Element, TerminologyObject> hierarchiemap = new HashMap<>();

		for (Element child : qASetNodes) {
			listener.updateProgress(time++ / abstime, "Building qasets");
			QASet q = (QASet) persistence.readFragment(child);
			hierarchiemap.put(child, q);
		}

		if (diagnosisNodes != null) {
			for (Element child : diagnosisNodes) {
				listener.updateProgress(time++ / abstime, "Building diagnosis");
				Solution diag = (Solution) persistence.readFragment(child);
				hierarchiemap.put(child, diag);
			}
		}

		// defining roots:
		if (rootQASetID != null) {
			kb.setRootQASet(kb.getManager().searchQASet(rootQASetID));
		}
		else {
			QASet rootQASet = getRootQASet(kb);
			if (rootQASet != null) {
				kb.setRootQASet(rootQASet);
			}
		}
		if (rootSolutionID != null) {
			kb.setRootSolution(kb.getManager().searchSolution(rootSolutionID));
		}
		else {
			Solution rootSolution = getRootSolution(kb);
			if (rootSolution != null) {
				kb.setRootSolution(rootSolution);
			}
		}

		// appending children
		for (Element e : hierarchiemap.keySet()) {
			XMLUtil.appendChildren(kb, hierarchiemap.get(e), e);
		}

		List<QASet> qaSets = new LinkedList<>();
		for (Element child : initQuestionnodes) {
			listener.updateProgress(time++ / abstime, "Loading knowledge base: init QA sets");
			if (child.getNodeName().equalsIgnoreCase("QContainer")
					|| child.getNodeName().equalsIgnoreCase("Question")
					|| child.getNodeName().equalsIgnoreCase("QASet")) {
				String id = child.getAttributes().getNamedItem("name")
						.getNodeValue();
				QASet item = (QASet) kb.getManager().search(id);
				if (item != null) {
					qaSets.add(item);
				}
			}
		}
		kb.setInitQuestions(qaSets);
		List<Object> readFragments = new LinkedList<>();
		// creating rules and schemas (rules are written into basic.xml in
		// former persistence versions
		for (Element child : knowledgeSliceNodes) {
			listener.updateProgress(time++ / abstime, "Loading knowledge base: knowledge slices");
			readFragments.add(persistence.readFragment(child));
		}
		// set the context, if it doesn't exist
		for (Object o : readFragments) {
			if (o instanceof Rule) {
				Rule r = (Rule) o;
				if (r.getProblemsolverContext() == null) {
					r.setProblemsolverContext(getContext(r.getAction()));
				}
			}
		}
		listener.updateProgress(1, "Loading knowledge base");
	}

	@Override
	public void write(PersistenceManager manager, KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb);
		Document doc = persistence.getDocument();

		float time = 0;
		int abstime = getEstimatedSize(kb);

		listener.updateProgress(time++ / abstime, "Saving knowledge base");

		Element father = doc.createElement("KnowledgeBase");
		doc.appendChild(father);

		father.setAttribute("id", kb.getId());
		father.setAttribute("type", "basic");
		father.setAttribute("system", "d3web");

		listener.updateProgress(time++ / abstime, "Saving knowledge base: properties");
		XMLUtil.appendInfoStore(persistence, father, kb, Autosave.basic);

		time = saveInitQuestions(father, kb, listener, time, abstime);

		listener.updateProgress(time++ / abstime, "Saving knowledge base: costs");

		if (kb.getRootQASet() != null) {
			Element rootQASetElement = doc.createElement("rootQASet");
			rootQASetElement.setTextContent(kb.getRootQASet().getName());
			father.appendChild(rootQASetElement);
		}

		if (kb.getRootSolution() != null) {
			Element rootSolutionElement = doc.createElement("rootSolution");
			rootSolutionElement.setTextContent(kb.getRootSolution().getName());
			father.appendChild(rootSolutionElement);
		}

		Element qContainersElement = doc.createElement("QASets");
		Map<TerminologyObject, Element> possibleParents = new HashMap<>();
		List<QASet> qASets = new ArrayList<>(kb.getManager().getQASets());
		qASets.sort(new NamedObjectComparator());
		for (QASet qASet : qASets) {
			listener.updateProgress(time++ / abstime, "Saving knowledge base: QASets");
			Element qContainerElement = persistence.writeFragment(qASet);
			qContainersElement.appendChild(qContainerElement);
			possibleParents.put(qASet, qContainerElement);
		}
		father.appendChild(qContainersElement);

		// Element questionsElement = doc.createElement("Questions");
		// for (Question q: kb.getQuestions()) {
		// listener.updateProgress(time++/abstime,
		// "Saving knowledge base: questions");
		// Element questionElement = pm.writeFragment(q, doc);
		// questionsElement.appendChild(questionElement);
		// possibleParents.put(q, questionElement);
		// }
		// father.appendChild(questionsElement);

		Element diagnosisElement = doc.createElement("Diagnoses");
		List<Solution> solutions = new ArrayList<>(kb.getManager().getSolutions());
		Collections.sort(solutions, new NamedObjectComparator());
		for (Solution diag : solutions) {
			listener.updateProgress(time++ / abstime, "Saving knowledge base: diagnosis");
			Element singleDiagnosisElement = persistence.writeFragment(diag);
			diagnosisElement.appendChild(singleDiagnosisElement);
			possibleParents.put(diag, singleDiagnosisElement);
		}
		father.appendChild(diagnosisElement);

		// appendChildren
		for (TerminologyObject parent : possibleParents.keySet()) {
			XMLUtil.appendChildren(parent, possibleParents.get(parent));
		}

		Element knowledgeSlicesElement = doc.createElement("KnowledgeSlices");
		father.appendChild(knowledgeSlicesElement);

		XMLUtil.writeDocumentToOutputStream(doc, stream);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		// DCMarkups are count as 1
		int time = 1;
		time += kb.getManager().getQuestions().size();
		time += kb.getManager().getQContainers().size();
		time += kb.getManager().getSolutions().size();
		time += kb.getInitQuestions().size();
		return time;
	}

	private static QASet getRootQASet(KnowledgeBase kb) {
		List<QASet> noParents = new ArrayList<>();
		for (QASet fk : kb.getManager().getQASets()) {
			if (fk.getParents() == null || fk.getParents().length == 0) {
				noParents.add(fk);
			}
		}
		if (noParents.size() > 1) {
			LOGGER.warn("more than one root node in qaset tree!");
			// [HOTFIX]:aha:multiple root / orphan handling
			QASet root = null;
			for (QASet q : noParents) {
				if (q.getName().equals("Q000")) root = q;
			}
			return root;

		}
		else if (noParents.size() < 1) {
			LOGGER.error("no root node in qaset tree!");
			return null;
		}
		return noParents.get(0);
	}

	private static Solution getRootSolution(KnowledgeBase kb) {
		List<Solution> result = new ArrayList<>();
		for (Solution d : kb.getManager().getSolutions()) {
			if (d.getParents() == null || d.getParents().length == 0) {
				result.add(d);
			}
		}
		if (result.size() > 1) {
			LOGGER.warn("more than one diagnosis root node!");

			// [HOTFIX]:aha:multiple root / orphan handling
			Solution root = null;
			for (Solution d : result) {
				if (d.getName().equals("P000")) root = d;
			}
			return root;

		}
		else if (result.size() < 1) {
			LOGGER.error("no root node in diagnosis tree!");
			return null;
		}
		return result.get(0);
	}

	/**
	 * Returns the Context used for creating a rule based on the action. This
	 * only works for actions in the Kernel. This method should only be used
	 * when it's absolutely necessary.
	 * 
	 * @created 29.06.2010
	 * @param action PSAction
	 * @return ProblemsolverContext
	 */
	private static Class<? extends PSMethodRulebased> getContext(PSAction action) {
		if (action instanceof ActionContraIndication) {
			return PSMethodStrategic.class;
		}
		else if (action instanceof ActionHeuristicPS) {
			return PSMethodHeuristic.class;
		}
		else if (action instanceof ActionNextQASet) {
			return PSMethodStrategic.class;
		}
		else if (action instanceof ActionSetQuestion) {
			return PSMethodAbstraction.class;
		}
		else if (action instanceof ActionSuppressAnswer) {
			return PSMethodStrategic.class;
		}
		else {
			return null;
			// throw new IllegalArgumentException("Action " + action +
			// " is not known to rule factory");
		}
	}
}
