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
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.Edge;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * 
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
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {

		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("system", "d3web");
		doc.appendChild(root);

		Element ksNode = doc.createElement(FLOWS_ELEM);
		root.appendChild(ksNode);

		FlowSet flowSet = DiaFluxUtils.getFlowSet(knowledgeBase);

		if (flowSet != null) {

			float cur = 0;

			int max = getEstimatedSize(knowledgeBase);

			for (Flow flow : flowSet.getFlows()) {
				ksNode.appendChild(writeFlow(flow, doc));
				listener.updateProgress(++cur / max, "Saving knowledge base: DiaFlux");
			}

		}

		Util.writeDocumentToOutputStream(doc, stream);

	}

	/**
	 * 
	 * @param flow
	 * @param doc
	 * @param ksNode
	 * @return
	 * @throws IOException
	 */
	private Element writeFlow(Flow flow, Document doc) throws IOException {
		Element flowElem = doc.createElement(FLOW_ELEM);
		flowElem.setAttribute(ID, flow.getId());
		flowElem.setAttribute(AUTOSTART, Boolean.toString(flow.isAutostart()));
		flowElem.setAttribute(NAME, flow.getName());

		Element nodesElem = doc.createElement(NODES_ELEM);
		flowElem.appendChild(nodesElem);

		Element edgesElem = doc.createElement(EDGES_ELEM);
		flowElem.appendChild(edgesElem);

		for (Node node : flow.getNodes()) {
			nodesElem.appendChild(createNodeElement(node, doc));
		}

		for (Edge edge : flow.getEdges()) {
			nodesElem.appendChild(createEdgeElement(edge, doc));

		}

		// TODO autosave??
		XMLUtil.appendInfoStore(flowElem, flow, null);

		return flowElem;

	}

	/**
	 * 
	 * @param edge
	 * @param doc
	 * @return
	 * @throws IOException
	 */
	private Element createEdgeElement(Edge edge, Document doc) throws IOException {
		Element edgeElem = doc.createElement(EDGE_ELEM);

		edgeElem.setAttribute(FROM_ID, edge.getStartNode().getID());
		edgeElem.setAttribute(TO_ID, edge.getEndNode().getID());
		edgeElem.setAttribute(ID, edge.getID());

		Element condElem = PersistenceManager.getInstance().writeFragment(edge.getCondition(), doc);
		edgeElem.appendChild(condElem);

		return edgeElem;

	}

	/**
	 * 
	 * @param node
	 * @param parent
	 * @param doc
	 * @return
	 * @throws IOException
	 */
	private Element createNodeElement(Node node, Document doc) throws IOException {

		NodeFragmentHandler handler = getFragmentHandler(node);

		Element nodeElem = handler.write(node, doc);

		nodeElem.setAttribute(ID, node.getID());
		nodeElem.setAttribute(NAME, node.getName());

		return nodeElem;
	}

	/**
	 * 
	 * @param node
	 * @throws NoSuchFragmentHandlerException
	 */
	private NodeFragmentHandler getFragmentHandler(Node node) throws NoSuchFragmentHandlerException {

		for (NodeFragmentHandler handler : HANDLERS) {
			if (handler.canWrite(node)) return handler;
		}

		throw new NoSuchFragmentHandlerException("No FragmentHandler found for node '" + node + "'");
	}

	/**
	 * 
	 * @param node
	 * @throws NoSuchFragmentHandlerException
	 */
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
	public void read(KnowledgeBase knowledgeBase, InputStream stream, ProgressListener listerner) throws IOException {

		Document doc = Util.streamToDocument(stream);

		NodeList flows = doc.getElementsByTagName(FLOW_ELEM);

		for (int i = 0; i < flows.getLength(); i++) {
			Element flowElem = (Element) flows.item(i);

			Flow flow = readFlow(knowledgeBase, flowElem);

			fillInfoStore(flow, flowElem, knowledgeBase);

			DiaFluxUtils.addFlow(flow, knowledgeBase);
		}

	}

	/**
	 * 
	 * @created 24.02.2011
	 * @param flow
	 * @param flowElem
	 * @param knowledgeBase
	 * @throws IOException
	 */
	private void fillInfoStore(Flow flow, Element flowElem, KnowledgeBase knowledgeBase) throws IOException {

		NodeList list = flowElem.getElementsByTagName(XMLUtil.INFO_STORE);

		if (list.getLength() != 0) {
			XMLUtil.fillInfoStore(flow.getInfoStore(), (Element) list.item(0), knowledgeBase);
		}

	}

	/**
	 * 
	 * @created 11.11.2010
	 * @param knowledgeBase
	 * @param flowElem
	 * @throws IOException
	 */
	private Flow readFlow(KnowledgeBase knowledgeBase, Element flowElem) throws IOException {
		NodeList nodeList = flowElem.getElementsByTagName(NODE_ELEM);
		List<Node> nodes = new ArrayList<Node>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			nodes.add(readNode(knowledgeBase, (Element) nodeList.item(i)));
		}

		NodeList edgeList = flowElem.getElementsByTagName(EDGE_ELEM);
		List<Edge> edges = new ArrayList<Edge>();

		for (int i = 0; i < edgeList.getLength(); i++) {
			edges.add(readEdge(knowledgeBase, (Element) edgeList.item(i), nodes));
		}

		String name = flowElem.getAttribute(NAME);
		String id = flowElem.getAttribute(ID);
		boolean autostart = Boolean.parseBoolean(flowElem.getAttribute(AUTOSTART));
		Flow flow = FlowFactory.getInstance().createFlow(id, name, nodes, edges);
		flow.setAutostart(autostart);
		return flow;

	}

	/**
	 * 
	 * @param knowledgeBase
	 * @param nodes
	 * @param item
	 * @return
	 * @throws IOException
	 */
	private Edge readEdge(KnowledgeBase knowledgeBase, Element edgeElem, List<Node> nodes) throws IOException {

		String fromID = edgeElem.getAttribute(FROM_ID);
		String toID = edgeElem.getAttribute(TO_ID);
		String id = edgeElem.getAttribute(ID);

		Node startNode = getNodeByID(fromID, nodes);
		Node endNode = getNodeByID(toID, nodes);

		Condition condition = (Condition) PersistenceManager.getInstance().readFragment(
				(Element) edgeElem.getElementsByTagName("Condition").item(0), knowledgeBase);
		return FlowFactory.getInstance().createEdge(id, startNode, endNode, condition);
	}

	private Node readNode(KnowledgeBase knowledgeBase, Element node) throws IOException {

		return (Node) getFragmentHandler(node).read(knowledgeBase, node);
	}

	public static Node getNodeByID(String nodeID, List<Node> nodes) {
		for (Node node : nodes) {
			if (node.getID().equals(nodeID)) return node;
		}

		return null;
	}

}
