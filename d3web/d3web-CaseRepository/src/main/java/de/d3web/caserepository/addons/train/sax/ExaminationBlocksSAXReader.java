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
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.addons.IExaminationBlock;
import de.d3web.caserepository.addons.IExaminationBlocks;
import de.d3web.caserepository.addons.train.ExaminationBlock;
import de.d3web.caserepository.addons.train.ExaminationBlocks;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.io.utilities.XMLTools;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QContainer;

/**
 * 22.09.2003 18:08:00
 * @author hoernlein
 */
public class ExaminationBlocksSAXReader extends AbstractTagReader {
	
	private IExaminationBlocks ebs = null;
	
	private List blocks = new Vector();

	protected ExaminationBlocksSAXReader(String id) { super(id); }
	private static ExaminationBlocksSAXReader instance;
	private ExaminationBlocksSAXReader() { this("ExaminationBlocksSAXReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new ExaminationBlocksSAXReader();
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"ExaminationBlocks",
			"ExaminationBlock",
			"Containers",
			"Container",
			"EBTitle",
			"Solutioncomments",
			"Solutioncomment"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("ExaminationBlocks"))
			startExaminationBlocks();
		else if (qName.equals("ExaminationBlock"))
		    startExaminationBlock(attributes);
		else if (qName.equals("EBTitle"))
		    ;
		else if (qName.equals("Containers"))
		    startContainers(attributes);
		else if (qName.equals("Container"))
		    startContainer(attributes);
		else if (qName.equals("Solutioncomments"))
		    ;
		else if (qName.equals("Solutioncomment"))
		    startSolutioncomment(attributes);
	}

    /* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("ExaminationBlocks"))
			endExaminationBlocks();
		else if (qName.equals("ExaminationBlock"))
			endExaminationBlock();
		else if (qName.equals("EBTitle"))
		    endTitle();
		else if (qName.equals("Containers"))
		    endContainers();
		else if (qName.equals("Container"))
		    ;
		else if (qName.equals("Solutioncomments"))
		    ;
		else if (qName.equals("Solutioncomment"))
		    endSolutioncomment();
	}

    private void startExaminationBlocks() {
		ebs = new ExaminationBlocks();
	}

	private void endExaminationBlocks() {
		ebs.setAllBlocks(blocks);
		getCaseObject().setExaminationBlocks(ebs);
		ebs = null;
		blocks = new Vector();
	}

	private IExaminationBlock currentExBlock = null;
	
	private void startExaminationBlock(Attributes attributes) {
		currentExBlock = new ExaminationBlock();
		setSolutionContainer(currentExBlock);
	}

	private void endExaminationBlock() {
		blocks.add(currentExBlock);
		currentExBlock = null;
	}

	private void endTitle() {
	    currentExBlock.setTitle(XMLTools.prepareFromCDATA(getTextBetweenCurrentTag()));
	}
	
	private List currentContainers = new LinkedList();
	
	private void startContainers(Attributes attributes) {
	    currentContainers = new LinkedList();
	}

	private void endContainers() {
	    Iterator iter = currentContainers.iterator();
	    while (iter.hasNext())
	        currentExBlock.addContent((QContainer) iter.next());
	    currentContainers = new LinkedList();
	}
	
	private void startContainer(Attributes attributes) {
	    String id = attributes.getValue("id");
	    if (id == null) {
	        Logger.getLogger(this.getClass().getName()).warning("no id tag in Container - omitted");
	        return;
	    }
	    QContainer q = getKnowledgeBase().searchQContainers(id);
	    if (q == null) {
	        Logger.getLogger(this.getClass().getName()).warning("no qcontainer found for id >" + id + "< - omitted");
	        return;
	    }
	    currentContainers.add(q);
	}
	
	private Solution currentDiagnosis = null; 
	
	private void startSolutioncomment(Attributes attributes) {
	    String id = attributes.getValue("id");
	    currentDiagnosis = getKnowledgeBase().searchDiagnosis(id);
	}
	
	private void endSolutioncomment() {
	    if (currentDiagnosis != null) {
	        String t = getTextBetweenCurrentTag();
	        t = XMLTools.prepareFromCDATA(t);
	        currentExBlock.setCommentFor(currentDiagnosis, t);
	    }
	    currentDiagnosis = null;
	}
	
}
