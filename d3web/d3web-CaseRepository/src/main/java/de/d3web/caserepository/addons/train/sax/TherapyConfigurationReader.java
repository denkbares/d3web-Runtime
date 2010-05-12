/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.addons.train.TherapyConfiguration;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * 21.10.2003 17:29:59
 * 
 * @author hoernlein
 */
public class TherapyConfigurationReader extends AbstractTagReader {

	protected TherapyConfigurationReader(String id) {
		super(id);
	}

	private static TherapyConfigurationReader instance;

	private TherapyConfigurationReader() {
		this("TherapyConfigurationReader");
	}

	public static AbstractTagReader getInstance() {
		if (instance == null) instance = new TherapyConfigurationReader();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	@Override
	public List<String> getTagNames() {
		return Arrays.asList(new String[] {
				"TherapyConfiguration",
				"TCNode",
				"TCLeaf"
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.
	 * String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("TCNode")) {
			startNode(attributes);
		}
		else if (qName.equals("TCLeaf")) {
			startLeaf(attributes);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	@Override
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("TherapyConfiguration")) {
			endTherapyConfiguration();
		}
		else if (qName.equals("TCNode")) {
			endNode();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.sax.AbstractTagReader#initialize(de.d3web.kernel
	 * .domainModel.KnowledgeBase, de.d3web.caserepository.CaseObjectImpl)
	 */
	@Override
	public void initialize(KnowledgeBase knowledgeBase, CaseObjectImpl caseObject) {
		openNodes = new LinkedList<TherapyConfiguration.TCNode>();
		super.initialize(knowledgeBase, caseObject);
	}

	private TherapyConfiguration.ITCNode node = null;

	private void endTherapyConfiguration() {
		if (node != null) getCaseObject().setTherapyConfiguration(new TherapyConfiguration(node));
		node = null;
	}

	private List<TherapyConfiguration.TCNode> openNodes;

	private void startNode(Attributes attributes) {

		int n = Integer.parseInt(attributes.getValue("n"));
		int m = Integer.parseInt(attributes.getValue("m"));

		TherapyConfiguration.TCNode c = new TherapyConfiguration.TCNode(n, m);
		openNodes.add(0, c);

	}

	private void startLeaf(Attributes attributes) {

		String did = attributes.getValue("id");
		Solution d = getKnowledgeBase().searchSolution(did);
		if (d == null) {
			Logger.getLogger(this.getClass().getName()).warning(
					"no diagnosis found for '" + did + "'");
			return;
		}

		TherapyConfiguration.TCLeaf l = new TherapyConfiguration.TCLeaf(d);

		if (openNodes.isEmpty()) node = l;
		else (openNodes.get(0)).addChild(l);

	}

	private void endNode() {
		if (openNodes.size() == 1) node = openNodes.get(0);
		else {
			TherapyConfiguration.TCNode c = openNodes.remove(0);
			(openNodes.get(0)).addChild(c);
		}
	}

}
