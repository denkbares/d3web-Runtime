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

import java.util.List;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.XPSCase;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.scoring.inference.PSMethodHeuristic;

public class DiagnosesTreeBean {

	public static final String STANDARD_TYPE = "diagnosisTree";

	public static final String EXCLUDED_TYPE = "diagExcl";

	public static final String ESTABLISHED_TYPE = "diagEstab";

	public static final String SUGGESTED_TYPE = "diagSugg";

	private TreeModel diagTreeModel;

	private HtmlTree diagTree;

	public void checkNodeStyles(XPSCase theCase) {
		TreeNode root = diagTreeModel.getNodeById("0");
		checkNodeStylesRecursive(root, theCase);

	}

	private void checkNodeStylesRecursive(TreeNode node, XPSCase theCase) {
		Diagnosis actual = theCase.getKnowledgeBase().searchDiagnosis(
				node.getIdentifier());
		if (actual.getState(theCase, PSMethodHeuristic.class).equals(
				DiagnosisState.ESTABLISHED)) {
			node.setType(DiagnosesTreeBean.ESTABLISHED_TYPE);
		} else if (actual.getState(theCase, PSMethodHeuristic.class).equals(
				DiagnosisState.EXCLUDED)) {
			node.setType(DiagnosesTreeBean.EXCLUDED_TYPE);
		} else if (actual.getState(theCase, PSMethodHeuristic.class).equals(
				DiagnosisState.SUGGESTED)) {
			node.setType(DiagnosesTreeBean.SUGGESTED_TYPE);
		} else {
			node.setType(DiagnosesTreeBean.STANDARD_TYPE);
		}
		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				checkNodeStylesRecursive((TreeNode) node.getChildren().get(i),
						theCase);
			}
		}
	}

	private void createTreeRecursive(Diagnosis diag, TreeNode parentNode) {
		parentNode.setIdentifier(diag.getId());
		if (diag.getChildren().size() == 0) {
			parentNode.setLeaf(true);
			return;
		} else {
			List<? extends NamedObject> childrenList = diag.getChildren();
			for (int i = 0; i < childrenList.size(); i++) {
				Diagnosis diagChild = (Diagnosis) childrenList.get(i);
				TreeNode newNode = new TreeNodeBase(
						DiagnosesTreeBean.STANDARD_TYPE, diagChild.getText(),
						false);
				parentNode.getChildren().add(newNode);
				createTreeRecursive(diagChild, newNode);
			}
		}
	}

	public boolean getDiagnosesAvailable() {
		List<Diagnosis> established = DialogUtils.getDialog().getTheCase()
				.getDiagnoses(DiagnosisState.ESTABLISHED);
		List<Diagnosis> suggested = DialogUtils.getDialog().getTheCase()
				.getDiagnoses(DiagnosisState.SUGGESTED);
		List<Diagnosis> excluded = DialogUtils.getDialog().getTheCase()
				.getDiagnoses(DiagnosisState.EXCLUDED);
		if (established.size() != 0 || suggested.size() != 0
				|| excluded.size() != 0) {
			return true;
		}
		return false;
	}

	public HtmlTree getDiagTree() {
		return diagTree;
	}

	public TreeModel getDiagTreeModel() {
		return diagTreeModel;
	}

	public void init() {
		XPSCase theCase = DialogUtils.getDialog().getTheCase();
		initTreeModel(theCase);
		diagTree = new HtmlTree();
		diagTree.setModel(diagTreeModel);
		diagTree.expandAll();
		// check nodes
		checkNodeStyles(theCase);
	}

	private void initTreeModel(XPSCase theCase) {
		Diagnosis rootDiag = theCase.getKnowledgeBase().getRootDiagnosis();
		TreeNode treeData = new TreeNodeBase(DiagnosesTreeBean.STANDARD_TYPE,
				rootDiag.getText(), false);
		createTreeRecursive(rootDiag, treeData);
		diagTreeModel = new TreeModelBase(treeData);
	}

	public void setDiagTree(HtmlTree diagTree) {
		this.diagTree = diagTree;
	}

	public void setDiagTreeModel(TreeModel diagTreeModel) {
		this.diagTreeModel = diagTreeModel;
	}
}
