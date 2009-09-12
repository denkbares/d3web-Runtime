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
 * ExaminationBlocksCreator.java
 *
 * Created on 19. März 2002, 16:16
 */

package de.d3web.caserepository.addons.train.dom;

import org.w3c.dom.Node;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.dom.AdditionalCaseObjectCreator;
import de.d3web.caserepository.dom.CaseObjectCreator;

/**
 * Reads and initializes the ExaminationBlocks.
 * @author  betz
 */
public class ExaminationBlocksCreator implements AdditionalCaseObjectCreator {
	
	public void process(CaseObjectCreator creator, Node node, CaseObjectImpl caseObject) {
		ExaminationBlocksCreator._process(creator, node, caseObject);
	}
    
	public static void _process(CaseObjectCreator creator, Node node, CaseObjectImpl caseObject) {
	    // [MISC]:aha:since newer versions of d3web.Train treat ExaminationBlocks differently
	    // we do not read them in
//	    IExaminationBlocks e = new ExaminationBlocks();
//    	boolean gotOne = false;
//        ChildrenIterator iter = new ChildrenIterator(node);
//        while (iter.hasNext()) {
//            Node child = (Node) iter.next();
//            String nodeName = child.getNodeName();
//            if (nodeName.equals("ExaminationBlock")) {
//                try {
//                    String targetId=child.getAttributes().getNamedItem("target").getNodeValue();
//					QContainer targetQASet = creator.getKnowledgeBase().searchQContainers(targetId);
//                    if (targetQASet !=null) {
//						e.addBlock(targetQASet);
//						Node solutionsN = XPathAPI.selectSingleNode(child, "Solutions");
//						CaseObjectCreator.readSolutions(solutionsN, e.getBlock(targetQASet), creator.getKnowledgeBase());
//						// [FIXME]:chris:DifferentialDiagnosis are still missing
//                    }
//                    gotOne = true;
//                } catch (Exception ex) {
//					Logger.getLogger(ExaminationBlocksCreator.class.getName()).throwing(ExaminationBlocksCreator.class.getName(), "_process", ex);
//				}
//            }
//        }
//        if (gotOne)
//        	caseObject.setExaminationBlocks(e);
    }
}
