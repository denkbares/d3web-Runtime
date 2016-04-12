/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class DiaFluxPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public static final String EDGES_ELEM = "Edges";
	public static final String NODES_ELEM = "Nodes";
	public static final String FLOWS_ELEM = "Flows";
	public static final String FLOW_ELEM = "Flow";
	public static final String EDGE_ELEM = "Edge";
	public static final String NODE_ELEM = "Node";
	public static final String NAME = "name";
	public static final String TO_ID = "toID";
	public static final String ID = "id";
	public static final String FROM_ID = "fromID";
	public static final String AUTOSTART = "autostart";

	private static final List<NodeFragmentHandler> HANDLERS;

	static {
		// TODO create ExtensionPoint
		HANDLERS = new ArrayList<NodeFragmentHandler>();
		HANDLERS.add(new StartNodeFragmentHandler());
		HANDLERS.add(new ExitNodeFragmentHandler());
		HANDLERS.add(new ActionNodeFragmentHandler());
		HANDLERS.add(new CommentNodeFragmentHandler());
		HANDLERS.add(new SnapshotNodeFragmentHandler());
		HANDLERS.add(new ComposedNodeFragmentHandler());
	}

	@Override
	public void write(PersistenceManager manager, KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb);
		Document doc = persistence.getDocument();

		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("system", "d3web");
		doc.appendChild(root);

		Element ksNode = doc.createElement(FLOWS_ELEM);
		root.appendChild(ksNode);

		FlowSet flowSet = DiaFluxUtils.getFlowSet(kb);
		if (flowSet != null) {
			float cur = 0;
			int max = getEstimatedSize(kb);
			for (Flow flow : flowSet.getFlows()) {
				ksNode.appendChild(writeFlow(flow, persistence));
				listener.updateProgress(++cur / max, "Saving knowledge base: DiaFlux");
			}
		}

		XMLUtil.writeDocumentToOutputStream(doc, stream);
	}

	private Element writeFlow(Flow flow, Persistence<KnowledgeBase> persistence) throws IOException {
		Document doc = persistence.getDocument();

		Element flowElem = doc.createElement(FLOW_ELEM);
		flowElem.setAttribute(AUTOSTART, Boolean.toString(flow.isAutostart()));
		flowElem.setAttribute(NAME, flow.getName());

		Element nodesElem = doc.createElement(NODES_ELEM);
		flowElem.appendChild(nodesElem);

		Element edgesElem = doc.createElement(EDGES_ELEM);
		flowElem.appendChild(edgesElem);

		for (Node node : flow.getNodes()) {
			nodesElem.appendChild(createNodeElement(node, persistence));
		}

		for (Edge edge : flow.getEdges()) {
			nodesElem.appendChild(createEdgeElement(edge, persistence));

		}

		XMLUtil.appendInfoStore(persistence, flowElem, flow, null);
		return flowElem;
	}

	private Element createEdgeElement(Edge edge, Persistence<KnowledgeBase> persistence) throws IOException {
		Element edgeElem = persistence.getDocument().createElement(EDGE_ELEM);

		edgeElem.setAttribute(FROM_ID, edge.getStartNode().getID());
		edgeElem.setAttribute(TO_ID, edge.getEndNode().getID());
		edgeElem.setAttribute(ID, edge.getID());

		Element condElem = persistence.writeFragment(edge.getCondition());
		edgeElem.appendChild(condElem);

		return edgeElem;
	}

	private Element createNodeElement(Node node, Persistence<KnowledgeBase> persistence) throws IOException {
		NodeFragmentHandler handler = getFragmentHandler(node);
		Element nodeElem = handler.write(node, persistence);

		nodeElem.setAttribute(ID, node.getID());
		nodeElem.setAttribute(NAME, node.getName());

		return nodeElem;
	}

	private NodeFragmentHandler getFragmentHandler(Node node) throws NoSuchFragmentHandlerException {
		for (NodeFragmentHandler handler : HANDLERS) {
			if (handler.canWrite(node)) return handler;
		}
		throw new NoSuchFragmentHandlerException("No FragmentHandler found for node '" + node + "'");
	}

	private NodeFragmentHandler getFragmentHandler(Element node) throws NoSuchFragmentHandlerException {
		for (NodeFragmentHandler handler : HANDLERS) {
			if (handler.canRead(node)) return handler;
		}
		throw new NoSuchFragmentHandlerException("No FragmentHandler found for node '" + node + "'");
	}

	@Override
	public int getEstimatedSize(KnowledgeBase knowledgeBase) {
		FlowSet flowSet = DiaFluxUtils.getFlowSet(knowledgeBase);
		return flowSet == null ? 0 : flowSet.size();
	}

	@Override
	public void read(PersistenceManager manager, KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb, stream);
		Document doc = persistence.getDocument();

		NodeList flows = doc.getElementsByTagName(FLOW_ELEM);
		for (int i = 0; i < flows.getLength(); i++) {
			Element flowElem = (Element) flows.item(i);

			Flow flow = readFlow(persistence, flowElem);
			fillInfoStore(persistence, flow, flowElem);
		}
	}

	private void fillInfoStore(Persistence<?> persistence, Flow flow, Element flowElem) throws IOException {
		NodeList list = flowElem.getElementsByTagName(XMLUtil.INFO_STORE);
		if (list.getLength() != 0) {
			XMLUtil.fillInfoStore(persistence, flow.getInfoStore(), (Element) list.item(0));
		}
	}

	private Flow readFlow(Persistence<KnowledgeBase> persistence, Element flowElem) throws IOException {
		NodeList nodeList = flowElem.getElementsByTagName(NODE_ELEM);
		List<Node> nodes = new ArrayList<Node>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			nodes.add(readNode(persistence, (Element) nodeList.item(i)));
		}

		NodeList edgeList = flowElem.getElementsByTagName(EDGE_ELEM);
		List<Edge> edges = new ArrayList<Edge>();

		for (int i = 0; i < edgeList.getLength(); i++) {
			edges.add(readEdge(persistence, (Element) edgeList.item(i), nodes));
		}

		String name = flowElem.getAttribute(NAME);
		boolean autostart = Boolean.parseBoolean(flowElem.getAttribute(AUTOSTART));
		Flow flow = FlowFactory.createFlow(persistence.getArtifact(), name, nodes, edges);
		flow.setAutostart(autostart);
		return flow;

	}

	private Edge readEdge(Persistence<KnowledgeBase> persistence, Element edgeElem, List<Node> nodes) throws IOException {
		String fromID = edgeElem.getAttribute(FROM_ID);
		String toID = edgeElem.getAttribute(TO_ID);
		String id = edgeElem.getAttribute(ID);

		Node startNode = getNodeByID(fromID, nodes);
		Node endNode = getNodeByID(toID, nodes);

		Condition condition = (Condition) persistence.readFragment(
				(Element) edgeElem.getElementsByTagName("Condition").item(0));
		return FlowFactory.createEdge(id, startNode, endNode, condition);
	}

	private Node readNode(Persistence<KnowledgeBase> persistence, Element node) throws IOException {
		return (Node) getFragmentHandler(node).read(node, persistence);
	}

	public static Node getNodeByID(String nodeID, List<Node> nodes) {
		for (Node node : nodes) {
			if (node.getID().equals(nodeID)) return node;
		}
		return null;
	}

}
