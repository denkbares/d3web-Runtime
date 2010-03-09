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

/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.fus;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.addons.fus.internal.*;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Diagnosis;

/**
 * 21.10.2003 17:29:59
 * @author hoernlein
 */
public class FUSConfigurationReader extends AbstractTagReader {

	protected FUSConfigurationReader(String id) { super(id); }
	private static FUSConfigurationReader instance;
	private FUSConfigurationReader() { this("FUSConfigurationReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new FUSConfigurationReader();
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"FUSConfiguration",
			"Configuration",
			"Node",
			"Leaf",
			"FUSs",
			"FUS"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("FUSConfiguration")) {
			startFUSConfiguration(attributes);
		} else if (qName.equals("Configuration")) {
			startConfiguration(attributes);
		} else if (qName.equals("Node")) {
			startNode(attributes);
		} else if (qName.equals("Leaf")) {
			startLeaf(attributes);
		} else if (qName.equals("FUSs")) {
			startFUSs(attributes);
		} else if (qName.equals("FUS")) {
			startFUS(attributes);
		}
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("FUSConfiguration")) {
			endFUSConfiguration();
		} else if (qName.equals("Configuration")) {
			endConfiguration();
		} else if (qName.equals("Node")) {
			endNode();
		} else if (qName.equals("FUSs")) {
			endFUSs();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#initialize(de.d3web.kernel.domainModel.KnowledgeBase, de.d3web.caserepository.CaseObjectImpl)
	 */
	public void initialize(KnowledgeBase knowledgeBase, CaseObjectImpl caseObject) {
		openNodes = new LinkedList();
		super.initialize(knowledgeBase, caseObject);
	}

	private FUSConfiguration currentFUSC = null;
	private Configuration currentConf = null;
	private ProbabilityList currentPL = null;
	
	private List openNodes;

	private void startFUSConfiguration(Attributes attributes) {
		currentFUSC = new FUSConfiguration();
	}

	private void endFUSConfiguration() {
		getCaseObject().setFUSConfiguration(currentFUSC);
		currentFUSC = null;
	}

	private void startConfiguration(Attributes attributes) {
		currentConf = new Configuration();
	}
	
	private void endConfiguration() {
		currentFUSC.addConfiguration(currentConf);
		currentConf = null;
	}

	private void startNode(Attributes attributes) {
		
		CNode.Type type = null;
		String typeS = attributes.getValue("type");
		if (CNode.AND.getName().equals(typeS)) {
			type = CNode.AND;
		} else if (CNode.OR.getName().equals(typeS)) {
			type = CNode.OR;
		} else if (CNode.XOR.getName().equals(typeS)) {
			type = CNode.XOR;
		} else if (CNode.NONE.getName().equals(typeS)) {
			type = CNode.NONE;
		} else if (CNode.NOFM.getName().equals(typeS)) {
			String n = attributes.getValue("n");
			try {
				type = new CNode.NOFMType(Integer.parseInt(n));
			} catch (Exception ex) {
				Logger.getLogger(this.getClass().getName()).warning("CNode.NOFM can't be set with n='" + attributes.getValue("n") + "'");
				return;
			}
		} else {
			Logger.getLogger(this.getClass().getName()).warning("no way to handle CNodes of type '" + type + "'");
			return;
		}

		CNode c = new CNode(type);

		openNodes.add(0, c);

	}
			
	private void startLeaf(Attributes attributes) {
		
		String did = attributes.getValue("diagnosis");
		Diagnosis d = getKnowledgeBase().searchDiagnosis(did);
		if (d == null) {
			Logger.getLogger(this.getClass().getName()).warning("no diagnosis found for '" + did + "'");
			return;
		}
		
		String type = attributes.getValue("type");
		CLeaf.Type t = CLeaf.EXCLUDED.getName().equals(type)
			? CLeaf.EXCLUDED
			: CLeaf.INCLUDED.getName().equals(type)
				? CLeaf.INCLUDED
				: null;
		if (t == null) {
			Logger.getLogger(this.getClass().getName()).warning("no way to handle CLeafs of type '" + type + "'");
			return;
		}
		
		CLeaf l = new CLeaf(d, t);
		
		if (openNodes.isEmpty())
			currentConf.setNode(l);
		else
			((CNode) openNodes.get(0)).addNode(l);
			
	}

	private void endNode() {
		if (openNodes.size() == 1)
			currentConf.setNode((CNode) openNodes.get(0));
		CNode c = (CNode) openNodes.remove(0);
		((CNode) openNodes.get(0)).addNode(c);
	}

	private void startFUSs(Attributes attributes) {
		currentPL = new ProbabilityList();
	}

	private void endFUSs() {
		currentConf.setCaseObjectIDProbabilityList(currentPL);
		currentPL = null;
	}

	private void startFUS(Attributes attributes) {
		String id = attributes.getValue("id");
		String probability = attributes.getValue("probability");
		double d = 1.0;
		try {
			d = Double.parseDouble(probability);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).warning(
				"can't parse probability >" + probability + "< set to 1.0");
		}
		currentPL.add(new ProbabilityList.Item(id, d));
	}

}
