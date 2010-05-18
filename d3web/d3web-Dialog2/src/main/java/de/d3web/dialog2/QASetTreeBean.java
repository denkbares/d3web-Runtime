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
import de.d3web.core.session.interviewmanager.MQDialogController;
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

	private void checkNodeStylesRecursive(TreeNode node, Session theCase) {
		QASet actual = theCase.getKnowledgeBase().searchQASet(
				node.getIdentifier());
		MQDialogController mqdc = DialogUtils.getMQDialogController(theCase);
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
		} else if (actual.isDone(theCase)) {
			node.setType(QASetTreeBean.DONE_TYPE);
		} else if (mqdc.isIndicatedOrHasIndicatedChild((QContainer) actual,
				true)) {
			if (mqdc.isValidForDC(actual)
					|| mqdc.isAnyChildValid((QContainer) actual)) {
				node.setType(QASetTreeBean.TO_BE_ASKED_TYPE);
			}
		} else {
			node.setType(QASetTreeBean.STANDARD_TYPE);
		}

		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				checkNodeStylesRecursive((TreeNode) node.getChildren().get(i),
						theCase);
			}
		}
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
}