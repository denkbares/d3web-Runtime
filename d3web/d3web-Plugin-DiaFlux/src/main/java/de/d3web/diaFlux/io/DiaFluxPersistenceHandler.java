/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowFactory;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.inference.DiaFluxUtils;


/**
 *
 * @author Reinhard Hatko
 * @created 11.11.2010
 */
public class DiaFluxPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	private static final List<NodeFragmentHandler> HANDLERS;

	static {
		// TODO create ExtensionPoint
		HANDLERS = new ArrayList<NodeFragmentHandler>();
		HANDLERS.add(new StartNodeFragmentHandler());
		HANDLERS.add(new ExitNodeFragmentHandler());
		HANDLERS.add(new ActionNodeFragmentHandler());
		HANDLERS.add(new CommentNodeFragmentHandler());
		HANDLERS.add(new SnapshotNodeFragmentHandler());

	}

	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream, ProgressListener listener) throws IOException {

		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("system", "d3web");
		doc.appendChild(root);

		Element ksNode = doc.createElement("Flows");
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
		Element flowElem = doc.createElement("Flow");
		flowElem.setAttribute("id", flow.getId());
		flowElem.setAttribute("name", flow.getName());

		Element nodesElem = doc.createElement("Nodes");
		flowElem.appendChild(nodesElem);

		Element edgesElem = doc.createElement("Edges");
		flowElem.appendChild(edgesElem);

		for (INode node : flow.getNodes()) {
			nodesElem.appendChild(writeNode(node, doc));
		}

		for (IEdge edge : flow.getEdges()) {
			nodesElem.appendChild(writeEdge(edge, edgesElem, doc));

		}

		return flowElem;

	}

	/**
	 *
	 * @param edge
	 * @param parent
	 * @param doc
	 * @return
	 * @throws IOException
	 * @throws NoSuchFragmentHandlerException
	 */
	private Element writeEdge(IEdge edge, Element parent, Document doc) throws NoSuchFragmentHandlerException, IOException {
		Element edgeElem = doc.createElement("Edge");

		edgeElem.setAttribute("fromID", edge.getStartNode().getID());
		edgeElem.setAttribute("toID", edge.getEndNode().getID());
		edgeElem.setAttribute("id", edge.getID());

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
	private Element writeNode(INode node, Document doc) throws IOException {

		NodeFragmentHandler handler = getFragmentHandler(node);

		Element nodeElem = handler.write(node, doc);

		nodeElem.setAttribute("id", node.getID());
		nodeElem.setAttribute("name", node.getName());



		return nodeElem;
	}

	/**
	 *
	 * @param node
	 * @throws NoSuchFragmentHandlerException
	 */
	private NodeFragmentHandler getFragmentHandler(INode node) throws NoSuchFragmentHandlerException {

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

		NodeList flows = doc.getElementsByTagName("Flow");

		for (int i = 0; i < flows.getLength(); i++) {
			Element flowElem = (Element) flows.item(i);

			Flow flow = readFlow(knowledgeBase, flowElem);
			DiaFluxUtils.addFlow(flow, knowledgeBase);
		}


	}

	/**
	 *
	 * @created 11.11.2010
	 * @param knowledgeBase
	 * @param flowElem
	 * @throws IOException
	 * @throws NoSuchFragmentHandlerException
	 */
	private Flow readFlow(KnowledgeBase knowledgeBase, Element flowElem) throws NoSuchFragmentHandlerException, IOException {
		NodeList nodeList = flowElem.getElementsByTagName("Node");
		List<INode> nodes = new ArrayList<INode>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			nodes.add(readNode(knowledgeBase, (Element) nodeList.item(i)));
		}

		NodeList edgeList = flowElem.getElementsByTagName("Edge");
		List<IEdge> edges = new ArrayList<IEdge>();

		for (int i = 0; i < edgeList.getLength(); i++) {
			edges.add(readEdge(knowledgeBase, (Element) edgeList.item(i), nodes));
		}

		String name = flowElem.getAttribute("name");
		String id = flowElem.getAttribute("id");
		return FlowFactory.getInstance().createFlow(id, name, nodes, edges);

	}

	/**
	 *
	 * @param knowledgeBase
	 * @param nodes
	 * @param item
	 * @return
	 * @throws IOException
	 * @throws NoSuchFragmentHandlerException
	 */
	private IEdge readEdge(KnowledgeBase knowledgeBase, Element edgeElem, List<INode> nodes) throws NoSuchFragmentHandlerException, IOException {

		String fromID = edgeElem.getAttribute("fromID");
		String toID = edgeElem.getAttribute("toID");
		String id = edgeElem.getAttribute("id");

		INode startNode = getNodeByID(fromID, nodes);
		INode endNode = getNodeByID(toID, nodes);

		Condition condition = (Condition) PersistenceManager.getInstance().readFragment(
				(Element) edgeElem.getElementsByTagName("Condition").item(0), knowledgeBase);
		return FlowFactory.getInstance().createEdge(id, startNode, endNode, condition);
	}

	/**
	 *
	 * @param knowledgeBase
	 * @param node
	 * @return
	 * @throws IOException
	 * @throws NoSuchFragmentHandlerException
	 */
	private INode readNode(KnowledgeBase knowledgeBase, Element node) throws NoSuchFragmentHandlerException, IOException {

		return (INode) getFragmentHandler(node).read(knowledgeBase, node);
	}

	public static INode getNodeByID(String nodeID, List<INode> nodes) {
		for (INode node : nodes) {
			if (node.getID().equals(nodeID)) return node;
		}

		return null;
	}

}
