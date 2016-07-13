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

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import com.denkbares.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.XCLRelationType;

/**
 * PersistenceHandler for XCLModels
 * 
 * @author kazamatzuri, Markus Friedrich (denkbares GmbH)
 * 
 */
public class XCLModelPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public final static String ID = "xclpattern";

	public static final String ATTRIBUTE_MIN_SUPPORT = "minSupport";
	public static final String ELEMENT_XCL_MODEL = "XCLModel";
	public static final String ELEMENT_RELATIONS = "Relations";
	public static final String ELEMENT_NECESSARY_RELATIONS = "necessaryRelations";
	public static final String ELEMENT_CONTRADICTING_RELATIONS = "contradictingRelations";
	public static final String ELEMENT_SUFFICIENT_RELATIONS = "sufficientRelations";
	public static final String ELEMENT_RELATION = "relation";
	public static final String ELEMENT_WEIGHT = "weight";

	public static final String ATTRIBUTE_ID = "ID";
	public static final String ATTRIBUTE_REFERENCED_ID = "refID";
	public static final String ATTRIBUTE_SOLUTION_ID = "SID";
	public static final String ATTRIBUTE_CONSIDER_ONLY_RELEVANT_RELATIONS = "considerOnlyRelevantRelations";
	public static final String ATTRIBUTE_ESTABLISHED_THRESHOLD = "establishedThreshold";
	public static final String ATTRIBUTE_SUGGESTED_THRESHOLD = "suggestedThreshold";

	@Override
	public void read(PersistenceManager manager, KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb, stream);
		parseModels(persistence, listener);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return kb.getAllKnowledgeSlicesFor(XCLModel.KNOWLEDGE_KIND).size();
	}

	private Element writeModel(Persistence<KnowledgeBase> persistence, XCLModel xclmodel, RelationPool pool) throws IOException {
		Element modelelement = persistence.getDocument().createElement(ELEMENT_XCL_MODEL);
		if (xclmodel.getMinSupport() != null) modelelement.setAttribute(ATTRIBUTE_MIN_SUPPORT,
				String.valueOf(xclmodel.getMinSupport()));
		if (xclmodel.getSuggestedThreshold() != null) modelelement.setAttribute(
				ATTRIBUTE_SUGGESTED_THRESHOLD, String.valueOf(xclmodel.getSuggestedThreshold()));
		if (xclmodel.getEstablishedThreshold() != null) modelelement.setAttribute(
				ATTRIBUTE_ESTABLISHED_THRESHOLD, String.valueOf(xclmodel.getEstablishedThreshold()));
		modelelement.setAttribute(ATTRIBUTE_SOLUTION_ID,
				xclmodel.getSolution().getName());
		modelelement.setAttribute(ATTRIBUTE_CONSIDER_ONLY_RELEVANT_RELATIONS,
				String.valueOf(xclmodel.isConsiderOnlyRelevantRelations()));
		modelelement.appendChild(writeRelations(persistence, xclmodel.getNecessaryRelations(),
				ELEMENT_NECESSARY_RELATIONS, pool));
		modelelement.appendChild(writeRelations(persistence, xclmodel.getSufficientRelations(),
				ELEMENT_SUFFICIENT_RELATIONS, pool));
		modelelement.appendChild(writeRelations(persistence, xclmodel.getContradictingRelations(),
				ELEMENT_CONTRADICTING_RELATIONS, pool));
		modelelement.appendChild(writeRelations(persistence, xclmodel.getRelations(),
				ELEMENT_RELATIONS, pool));
		return modelelement;
	}

	private Element writeRelations(Persistence<KnowledgeBase> persistence, Collection<XCLRelation> relations, String elementName, RelationPool pool) throws IOException {
		Element relationsElement = persistence.getDocument().createElement(elementName);
		List<XCLRelation> relList = new ArrayList<>(relations);
		Collections.sort(relList, XCLRelationComparator.getInstance());
		for (XCLRelation current : relList) {
			relationsElement.appendChild(writeRelation(persistence, current, pool));
		}
		return relationsElement;
	}

	/**
	 * Writes a relation or a relation reference, based if it is already
	 * available in the pool. The pool will also be updated by the specified
	 * relation.
	 * 
	 * @created 07.09.2012
	 * @param relation relation to be written
	 * @param doc document to create element for
	 * @param pool the pool to search relation and update if not exists
	 * @return the xml element
	 * @throws IOException if something went wrong
	 */
	private Element writeRelation(Persistence<KnowledgeBase> persistence, XCLRelation relation, RelationPool pool) throws IOException {
		boolean isAdded = pool.add(relation);
		String id = pool.getID(relation);
		if (isAdded) {
			// check if an equal relation is new and added to the pool,
			// then add relation contents to xml
			return writeRelation(persistence, id, relation);
		}
		else {
			// check if an equal relation is already used,
			// then add reference instead of relation contents
			Element element = persistence.getDocument().createElement(ELEMENT_RELATION);
			element.setAttribute(ATTRIBUTE_REFERENCED_ID, id);
			return element;
		}
	}

	private Element writeRelation(Persistence<KnowledgeBase> persistence, String id, XCLRelation relation) throws IOException {
		Element relationElement = persistence.getDocument().createElement(ELEMENT_RELATION);
		if (id != null) relationElement.setAttribute(ATTRIBUTE_ID, id);
		Condition cond = relation.getConditionedFinding();
		if (cond != null) {
			relationElement.appendChild(persistence.writeFragment(cond));
		}
		else {
			throw new IOException("Missing condition.");
		}
		if (relation.getWeight() != XCLRelation.DEFAULT_WEIGHT) {
			Element weight = persistence.getDocument().createElement(ELEMENT_WEIGHT);
			weight.setTextContent(String.valueOf(relation.getWeight()));
			relationElement.appendChild(weight);
		}
		return relationElement;
	}

	@Override
	public void write(PersistenceManager manager, KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb);
		Document doc = persistence.getDocument();

		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);

		// prepare loading
		ArrayList<XCLModel> slices = new ArrayList<>(
				kb.getAllKnowledgeSlicesFor(XCLModel.KNOWLEDGE_KIND));
		Collections.sort(slices, XCLModelComparator.getInstance());
		float cur = 0;
		int max = slices.size();

		// prepare relation pool
		RelationPool pool = new RelationPool();
		for (XCLModel model : slices) {
			ksNode.appendChild(writeModel(persistence, model, pool));
			listener.updateProgress(++cur / max, "Saving knowledge base: XCL Models");
		}
		XMLUtil.writeDocumentToOutputStream(doc, stream);
	}

	private String getAttribute(String name, Node node) {
		if ((node != null) && (node.getAttributes() != null)
				&& node.getAttributes().getNamedItem(name) != null) {
			return node.getAttributes().getNamedItem(name).getNodeValue();
		}
		return null;
	}

	private void parseModels(Persistence<KnowledgeBase> persistence, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Preparing xcl models");
		NodeList xclmodels = persistence.getDocument().getElementsByTagName(ELEMENT_XCL_MODEL);
		RelationPool pool = new RelationPool();
		float cur = 0;
		int max = xclmodels.getLength();
		for (int i = 0; i < max; i++) {
			listener.updateProgress(cur++ / max, "Loading xcl models");
			Node current = xclmodels.item(i);
			parseModel(persistence, current, pool);
		}
		listener.updateProgress(1, "Loading xcl models completed");
	}

	private void parseModel(Persistence<KnowledgeBase> persistence, Node current, RelationPool pool) throws IOException {
		String solutionName = getAttribute(ATTRIBUTE_SOLUTION_ID, current);
		String minSupport = getAttribute(ATTRIBUTE_MIN_SUPPORT, current);
		String suggestedThreshold = getAttribute(ATTRIBUTE_SUGGESTED_THRESHOLD, current);
		String establishedThreshold = getAttribute(ATTRIBUTE_ESTABLISHED_THRESHOLD, current);
		String considerOnlyRelevantRelations = getAttribute(
				ATTRIBUTE_CONSIDER_ONLY_RELEVANT_RELATIONS, current);
		Solution diag = persistence.getArtifact().getManager().searchSolution(solutionName);
		XCLModel model = new XCLModel(diag);
		NodeList relations = current.getChildNodes();
		for (int i = 0; i < relations.getLength(); i++) {
			parseRelations(persistence, model, relations.item(i).getChildNodes(), pool);
		}

		if (minSupport != null) {
			model.setMinSupport(Double.parseDouble(minSupport));
		}
		if (suggestedThreshold != null) {
			model.setSuggestedThreshold(Double.parseDouble(suggestedThreshold));
		}
		if (establishedThreshold != null) {
			model.setEstablishedThreshold(Double.parseDouble(establishedThreshold));
		}
		if (considerOnlyRelevantRelations != null) {
			model.setConsiderOnlyRelevantRelations(Boolean.parseBoolean(considerOnlyRelevantRelations));
		}
		diag.getKnowledgeStore().addKnowledge(XCLModel.KNOWLEDGE_KIND, model);
	}

	private void parseRelations(Persistence<KnowledgeBase> persistence, XCLModel model, NodeList relationsOfAType, RelationPool pool) throws IOException {
		for (int i = 0; i < relationsOfAType.getLength(); i++) {
			Node aRelation = relationsOfAType.item(i);
			if (!aRelation.getNodeName().equals(ELEMENT_RELATION)) continue;
			String refID = getAttribute(ATTRIBUTE_REFERENCED_ID, aRelation);
			if (refID != null && !refID.isEmpty()) {
				XCLRelation relation = pool.getRelation(refID);
				if (relation == null) {
					throw new IOException("referenced relation '" + refID + "' does not exists");
				}
				model.addRelation(relation);
			}
			else {
				String id = getAttribute(ATTRIBUTE_ID, aRelation);
				XCLRelation relation = parseRelation(persistence, aRelation);
				pool.add(id, relation);
				model.addRelation(relation);
			}
		}
	}

	static XCLRelation parseRelation(Persistence<KnowledgeBase> persistence, Node relationNode) throws IOException {
		String typeName = relationNode.getParentNode().getNodeName();
		NodeList children = relationNode.getChildNodes();
		Condition ac = null;
		double weight = XCLRelation.DEFAULT_WEIGHT;
		for (int t = 0; t < children.getLength(); t++) {
			Node child = children.item(t);

			if (child.getNodeName().equals("Condition")) {
				child.getTextContent();
				ac = (Condition) persistence.readFragment((Element) child);
			}
			else if (child.getNodeName().equals(ELEMENT_WEIGHT)) {
				weight = Double.parseDouble(child.getTextContent());
			}
		}
		if (ac == null) {
			throw new IOException("missing condition in relation");
		}
		XCLRelationType type;
		switch (typeName) {
			case ELEMENT_RELATIONS:
				type = XCLRelationType.explains;
				break;
			case ELEMENT_NECESSARY_RELATIONS:
				type = XCLRelationType.requires;
				break;
			case ELEMENT_CONTRADICTING_RELATIONS:
				type = XCLRelationType.contradicted;
				break;
			case ELEMENT_SUFFICIENT_RELATIONS:
				type = XCLRelationType.sufficiently;
				break;
			default:
				throw new IOException("unknown relation type '" + typeName + "'");
		}
		XCLRelation rel = new XCLRelation(ac, weight, type);
		return rel;
	}

}
