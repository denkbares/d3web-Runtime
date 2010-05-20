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

package de.d3web.dialog2;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeWalker;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.dialog2.util.DialogUtils;

public class QASetTreeBean {

	public static final String STANDARD_TYPE = "QASet";
	public static final String SELECTED_TYPE = "selectedQASet";
	public static final String DONE_TYPE = "doneQASet";
	public static final String TO_BE_ASKED_TYPE = "tobeaskedQASet";

	private TreeModel qaSetTreeModel;

	private HtmlTree qaSetHtmlTree;

	// workaround
	public void changeContainer(ActionEvent e) {
		changeContainer();
	}

	public void changeContainer() {
		TreeNode node = qaSetHtmlTree.getNode();
		DialogUtils.getQuestionPageBean().moveToQASet(node.getIdentifier());
	}

	public void checkNodeStyles(Session theCase) {
		TreeNode root = getQaSetTreeModel().getNodeById("0");
		checkNodeStylesRecursive(root, theCase);
	}

	private void checkNodeStylesRecursive(TreeNode node, Session session) {
		QASet actual = session.getKnowledgeBase().searchQASet(
				node.getIdentifier());
		// if QContainer is active ...
		if (node.getIdentifier()
				.equals(
						DialogUtils.getQuestionPageBean().getActualQContainer()
								.getId())) {
			node.setType(QASetTreeBean.SELECTED_TYPE);
			// find path and expand
			String path = getPathOfQASet(node.getIdentifier());
			if (path != null) {
				qaSetHtmlTree.expandPath(qaSetTreeModel
						.getPathInformation(path));
			}
		} else if (!DialogUtils.isValidQASet(actual, session)) { //  actual.isDone(session)) {
			node.setType(QASetTreeBean.DONE_TYPE);
		} else if (isIndicatedOrHasIndicatedChild((QContainer) actual,true, session)) {
			if (DialogUtils.isValidQASet(actual, session)
					|| isAnyChildContainerActive((QContainer) actual, session)) {
				node.setType(QASetTreeBean.TO_BE_ASKED_TYPE);
			}
		} else {
			node.setType(QASetTreeBean.STANDARD_TYPE);
		}

		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				checkNodeStylesRecursive((TreeNode) node.getChildren().get(i),
						session);
			}
		}
	}

	private boolean isAnyChildContainerActive(QContainer container, Session session) {
		TerminologyObject[] children = container.getChildren();
		for (TerminologyObject child : children) {
			if (child instanceof QContainer) {
				QContainer childC = (QContainer) child;
				if (DialogUtils.isValidQASet(childC, session)
					|| isAnyChildContainerActive(childC, session)) {
					return true;
				}
			}
		}
		return false;
	}

	private void createTreeRecursive(QASet qaset, TreeNode parentNode) {
		if (qaset.getChildren().length == 0) {
			parentNode.setLeaf(true);
			return;
		} else {
			TerminologyObject[] childrenList = qaset.getChildren();
			for (int i = 0; i < childrenList.length; i++) {
				QASet qaSetchild = (QASet) childrenList[i];
				if (!(qaSetchild instanceof Question)) {
					TreeNode newNode = new TreeNodeBase(
							QASetTreeBean.STANDARD_TYPE, qaSetchild.getName(),
							qaSetchild.getId(), false);
					parentNode.getChildren().add(newNode);
					createTreeRecursive(qaSetchild, newNode);
				}
			}
		}
	}

	private String getPathOfQASet(String qaSetId) {
		TreeWalker walker = qaSetTreeModel.getTreeWalker();
		walker.reset();
		walker.setCheckState(false);
		walker.setTree(qaSetHtmlTree);
		while (walker.next()) {
			String id = qaSetHtmlTree.getNodeId();
			String qAidentifier = qaSetHtmlTree.getNode().getIdentifier();
			if (qAidentifier.equals(qaSetId)) {
				return id;
			}
		}
		return null;
	}

	public HtmlTree getQaSetHtmlTree() {
		return qaSetHtmlTree;
	}

	public TreeModel getQaSetTreeModel() {
		return qaSetTreeModel;
	}

	public void init() {
		Session theCase = DialogUtils.getDialog().getSession();
		QASet root = theCase.getKnowledgeBase().getRootQASet();
		TreeNode treeData = new TreeNodeBase(QASetTreeBean.STANDARD_TYPE, root
				.getName(), root.getId(), false);
		qaSetTreeModel = new TreeModelBase(treeData);
		qaSetHtmlTree = new HtmlTree();
		qaSetHtmlTree.setModel(qaSetTreeModel);
		qaSetHtmlTree.collapseAll();
		createTreeRecursive(root, treeData);
		checkNodeStyles(theCase);
	}

	public void setQaSetHtmlTree(HtmlTree qaSetHtmlTree) {
		this.qaSetHtmlTree = qaSetHtmlTree;
	}

	public void setQaSetTreeModel(TreeModel qaSetTreeModel) {
		this.qaSetTreeModel = qaSetTreeModel;
	}
	
	
	/**
	 * Returns true, if the given container or any child of it is indicated.
	 * 
	 * @param considerQuestionsAsChildren determines, if the quesiton-children
	 *        of a container shall also be considered (if false, only
	 *        children-containers will be considered).
	 * @param session 
	 */
	private boolean isIndicatedOrHasIndicatedChild(QContainer container, boolean considerQuestionsAsChildren, Session session) {
		return isIndicatedOrHasIndicatedChild(container, new LinkedList<QASet>(), considerQuestionsAsChildren, session);
	}

	/**
	 * Returns true, if the given container or any child of it is indicated.
	 * 
	 * @param c (QContainer to test)
	 * @param processedQASets (to avoid cycles)
	 * @param considerQuestionsAsChildren determines, if the quesiton-children
	 *        of a container shall also be considered (if false, only
	 *        children-containers will be considered).
	 * @param session 
	 * @return boolean
	 */
	private boolean isIndicatedOrHasIndicatedChild(QContainer c, List<QASet> processedQASets, boolean considerQuestionsAsChildren, Session session) {
		if ((c == null) || (processedQASets.contains(c))) {
			return false;
		}
		if (DialogUtils.isValidQASet(c, session)) {
			return true;
		}
		else if (c.getChildren().length != 0) {
			if (c.getChildren()[0] instanceof Question) {
				if (considerQuestionsAsChildren && somethingIsDoneInContainer(c, session)) {
					return true;
				}
			}
			else {
				processedQASets.add(c);
				// go through all container-children
				for (TerminologyObject to : c.getChildren()) {
					if (to instanceof QContainer) {
						if (isIndicatedOrHasIndicatedChild((QContainer) to,
								processedQASets, considerQuestionsAsChildren,session)) {
							return true;
						}
					}
					else {
						if (to instanceof Question) {
							if (considerQuestionsAsChildren
									&& somethingIsDoneInContainer(c,session)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true, if at least one of the children-questions is done.
	 */
	public boolean somethingIsDoneInContainer(QContainer c, Session session) {
		for (TerminologyObject qaSet : c.getChildren()) {
			if (qaSet instanceof Question) {
				Question q = (Question) qaSet;
				if (UndefinedValue.isNotUndefinedValue(session.getBlackboard().getValue(q))) {
					return true;
				}
			}
		}
		return false;
	}
}