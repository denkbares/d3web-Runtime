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

package de.d3web.persistence.xml.loader;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.formula.Add;
import de.d3web.kernel.domainModel.formula.Count;
import de.d3web.kernel.domainModel.formula.Div;
import de.d3web.kernel.domainModel.formula.FormulaDate;
import de.d3web.kernel.domainModel.formula.FormulaDateElement;
import de.d3web.kernel.domainModel.formula.FormulaDateExpression;
import de.d3web.kernel.domainModel.formula.FormulaDatePrimitive;
import de.d3web.kernel.domainModel.formula.FormulaElement;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.formula.FormulaNumber;
import de.d3web.kernel.domainModel.formula.FormulaNumberElement;
import de.d3web.kernel.domainModel.formula.FormulaNumberPrimitive;
import de.d3web.kernel.domainModel.formula.Max;
import de.d3web.kernel.domainModel.formula.Min;
import de.d3web.kernel.domainModel.formula.Mult;
import de.d3web.kernel.domainModel.formula.QDateWrapper;
import de.d3web.kernel.domainModel.formula.QNumWrapper;
import de.d3web.kernel.domainModel.formula.Sub;
import de.d3web.kernel.domainModel.formula.Today;
import de.d3web.kernel.domainModel.formula.YearDiff;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

/**
 * A factory class for creation of all objects that are neccessary for
 * RuleActions
 * 
 * @author Norman Brümmer
 */
public class ActionContentFactory {

	/**
	 * creates all possible content for ActionAddValue or ActionSetValue (e.g.
	 * FormulaElements) from a DOM-node for the given knowledge base using the
	 * specified KBLoader. <br>Creation date: (09.07.2001 18:30:13)
	 */
	public static List createActionValueContent(Node slice, KBLoader loader) {
		List ret = new LinkedList();
		// index 0: question
		// index 1: value
		List value = new LinkedList();
		Node actionNode = getActionNode(slice);
		NodeList nl = actionNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("question")) {
				String id = child.getAttributes().getNamedItem("ID").getNodeValue();
				ret.add(0, loader.search(id));
			} else if (child.getNodeName().equalsIgnoreCase("values")) {
				NodeList values = child.getChildNodes();
				for (int k = 0; k < values.getLength(); ++k) {
					Node valNode = values.item(k);
					if (valNode.getNodeName().equalsIgnoreCase("value")) {
						String type = valNode.getAttributes().getNamedItem("type").getNodeValue();
						// new and old node:
						if (type.equalsIgnoreCase("answer") || type.equalsIgnoreCase("answerChoice")) {
							String id = valNode.getAttributes().getNamedItem("ID").getNodeValue();
							value.add(loader.searchAnswer(id));
						} else if (type.equalsIgnoreCase("evaluatable")) {
							value.addAll(createEvaluatables(loader, valNode));
						}
					}
				}
				ret.add(1, value);
			}
		}
		return ret;
	}

	private static List createEvaluatables(KBLoader loader, Node valNode) {
		List evaluatables = new LinkedList();
		NodeList evalNodes = valNode.getChildNodes();
		for (int i = 0; i < evalNodes.getLength(); ++i) {
			Node node = evalNodes.item(i);
			if (node.getNodeName().equalsIgnoreCase("FormulaExpression")) {
				evaluatables.add(createFormulaExpression(node, loader));
			} else if (node.getNodeName().equalsIgnoreCase("FormulaDateExpression")) {
				evaluatables.add(createFormulaDateExpression(node, loader));
			} else {
				FormulaElement elem = createFormulaElement(node, loader);
				if (elem != null) {
					evaluatables.add(elem);
				}
			}
		}
		return evaluatables;
	}

	/**
	 * Creates all neccessary Objects for ActionContraIndication from a
	 * DOM-node using the given KBLoader
	 */
	public static List createActionContraIndicationContent(Node slice, KBLoader loader) {
		return createActionNextQASetContent(slice, loader);
	}

	/**
	 * Creates all neccessary Objects for ActionHeuristicPS from a DOM-node
	 * using the given KBLoader
	 */
	public static List createActionHeuristicPSContent(Node slice, KBLoader loader) {
		List ret = new LinkedList();
		Node actionNode = getActionNode(slice);
		NodeList children = actionNode.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeName().equalsIgnoreCase("Score")) {
				String value = child.getAttributes().getNamedItem("value").getNodeValue();
				Score score = loader.getScore(value);
				ret.add(0, score);
			} else if (child.getNodeName().equalsIgnoreCase("Diagnosis")) {
				String id = child.getAttributes().getNamedItem("ID").getNodeValue();
				Diagnosis d = loader.searchDiagnosis(id);
				ret.add(1, d);
			}
		}
		return ret;
	}

	/**
	 * Creates all neccessary Objects for ActionNextQASet from a DOM-node using
	 * the given KBLoader
	 */
	public static List createActionNextQASetContent(Node slice, KBLoader loader) {
		List ret = new LinkedList();
		Node actionNode = getActionNode(slice);
		NodeList actionclildren = actionNode.getChildNodes();
		for (int i = 0; i < actionclildren.getLength(); ++i) {
			Node target = actionclildren.item(i);
			if (target.getNodeName().equalsIgnoreCase("targetQASets")) {
				NodeList qasets = target.getChildNodes();
				for (int k = 0; k < qasets.getLength(); ++k) {
					Node q = qasets.item(k);
					if (q.getNodeName().equalsIgnoreCase("QASet")) {
						String id = q.getAttributes().getNamedItem("ID").getNodeValue();
						QASet qset = (QASet) loader.search(id);
						ret.add(qset);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Creates all neccessary Objects for ActionSuppressAnswer from a DOM-node
	 * using the given KBLoader
	 */
	public static List createActionSuppressAnswerContent(Node slice, KBLoader handler) {
		List ret = new LinkedList();
		Question q = null;
		List suppress = new LinkedList();
		Node action = getActionNode(slice);
		NodeList nl = action.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("Question")) {
				String id = n.getAttributes().getNamedItem("ID").getNodeValue();
				q = handler.searchQuestion(id);
			} else if (n.getNodeName().equalsIgnoreCase("Suppress")) {
				NodeList sanslist = n.getChildNodes();
				for (int k = 0; k < sanslist.getLength(); ++k) {
					Node answer = sanslist.item(k);
					if (answer.getNodeName().equalsIgnoreCase("Answer")) {
						String id = answer.getAttributes().getNamedItem("ID").getNodeValue();
						Answer ans = handler.searchAnswer(id);
						suppress.add(ans);
					}
				}
			}
		}
		ret.add(0, q);
		ret.add(1, suppress);
		return ret;
	}

	private static Count createCount(Node countNode, KBLoader handler) {
		Count ret = null;
		NodeList nl = countNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("QuestionMC")) {
				NodeList qnl = n.getChildNodes();
				String id = qnl.item(0).getNodeValue();
				if (id == null)
					id = qnl.item(1).getNodeValue();
				QuestionMC countQuestion = (QuestionMC) handler.searchQuestion(id);
				if (countQuestion != null)
					ret = new Count(countQuestion);
			}
		}
		return ret;
	}

	private static Today createToday(Node todayNode, KBLoader handler) {
		Today ret = null;
		NodeList nl = todayNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node argElem = nl.item(i);
			FormulaElement elem = createFormulaElement(argElem, handler);
			if (elem instanceof FormulaNumberElement) {
				ret = new Today((FormulaNumberElement) elem);
			}
		}
		return ret;
	}

	private static FormulaExpression createFormulaExpression(Node exprNode, KBLoader handler) {
		de.d3web.kernel.domainModel.formula.FormulaExpression ret = null;
		Question q = null;
		FormulaNumberElement expr = null;
		NodeList nl = exprNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("Question") || n.getNodeName().equalsIgnoreCase("QuestionNum")) {
				Node idAtt = n.getAttributes().getNamedItem("ID");
				String id;
				if (idAtt != null) {
					id = idAtt.getNodeValue();
				}else {
					id = n.getChildNodes().item(0).getNodeValue();
				}
				if (id == null) {
					id = n.getChildNodes().item(1).getNodeValue();
				}
				q = handler.searchQuestion(id);
			} else {
				FormulaElement elem = createFormulaElement(n, handler);
				if (elem instanceof FormulaNumberElement) {
					expr = (FormulaNumberElement) elem;
				}
			}
		}
		ret = new FormulaExpression(q, expr);
		return ret;
	}

	private static FormulaDateExpression createFormulaDateExpression(Node exprNode, KBLoader handler) {
		FormulaDateExpression ret = null;
		Question q = null;
		FormulaDateElement expr = null;
		NodeList nl = exprNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("QuestionDate")) {
				String id = n.getAttributes().getNamedItem("ID").getNodeValue();
				if (id == null) {
					id = n.getChildNodes().item(1).getNodeValue();
				}
				q = handler.searchQuestion(id);
			} else {
				FormulaElement elem = createFormulaElement(n, handler);
				if (elem instanceof FormulaDateElement) {
					expr = (FormulaDateElement) elem;
				}
			}
		}
		ret = new FormulaDateExpression(q, expr);
		return ret;
	}

	private static FormulaNumberPrimitive createFormulaPrimitive(Node termNode, KBLoader handler) {
		FormulaNumberPrimitive ret = null;
		String type = termNode.getAttributes().getNamedItem("type").getNodeValue();
		NodeList nl = termNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node valNode = nl.item(i);
			if (valNode.getNodeName().equalsIgnoreCase("value")) {
				String val = valNode.getChildNodes().item(0).getNodeValue();
				if (val == null) {
					val = valNode.getChildNodes().item(1).getNodeValue();
				}
				if (val != null) {
					if (type.equalsIgnoreCase("FormulaNumber")) {
						ret = new FormulaNumber(new Double(val));
					} else if (type.equalsIgnoreCase("QNumWrapper")) {
						QuestionNum qnum = (QuestionNum) handler.searchQuestion(val);
						ret = new QNumWrapper(qnum);
					}
				}
			}
		}
		return ret;
	}

	private static FormulaDatePrimitive createFormulaDatePrimitive(Node termNode, KBLoader handler) {
		FormulaDatePrimitive ret = null;
		String type = termNode.getAttributes().getNamedItem("type").getNodeValue();
		NodeList nl = termNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node valNode = nl.item(i);
			if (valNode.getNodeName().equalsIgnoreCase("value")) {
				String val = valNode.getChildNodes().item(0).getNodeValue();
				if (val == null) {
					val = valNode.getChildNodes().item(1).getNodeValue();
				}
				if (val != null) {
					if (type.equalsIgnoreCase("FormulaDate")) {
						Date date = null;
						try {
							date = AnswerDate.format.parse(val);
						} catch (ParseException e) {
							Logger.getLogger(ActionContentFactory.class.getName()).throwing(ActionContentFactory.class.getName(), "createFormulaDatePrimitive",
									e);
						}
						ret = new FormulaDate(date);
					} else if (type.equalsIgnoreCase("QDateWrapper")) {
						QuestionDate qdate = (QuestionDate) handler.searchQuestion(val);
						ret = new QDateWrapper(qdate);
					}
				}
			}
		}
		return ret;
	}

	private static FormulaElement createFormulaTerm(Node termNode, KBLoader handler) {
		FormulaElement ret = null;
		FormulaElement arg1 = null;
		FormulaElement arg2 = null;
		String type = termNode.getAttributes().getNamedItem("type").getNodeValue();
		NodeList arguments = termNode.getChildNodes();
		try {
			for (int i = 0; i < arguments.getLength(); ++i) {
				Node arg = arguments.item(i);
				if (arg.getNodeName().equalsIgnoreCase("arg1") || arg.getNodeName().equalsIgnoreCase("arg2")) {
					boolean wasArg1 = arg.getNodeName().equalsIgnoreCase("arg1");
					NodeList nl = arg.getChildNodes();
					for (int k = 0; k < nl.getLength(); ++k) {
						Node argElem = nl.item(k);
						FormulaElement elem = createFormulaElement(argElem, handler);
						if (elem != null) {
							if (wasArg1) {
								arg1 = elem;
							} else {
								arg2 = elem;
							}
						}
					}
				}
			}
			if (type.equalsIgnoreCase("+")) {
				ret = new Add((FormulaNumberElement) arg1, (FormulaNumberElement) arg2);
			} else if (type.equalsIgnoreCase("-")) {
				ret = new Sub((FormulaNumberElement) arg1, (FormulaNumberElement) arg2);
			} else if (type.equalsIgnoreCase("*")) {
				ret = new Mult((FormulaNumberElement) arg1, (FormulaNumberElement) arg2);
			} else if (type.equalsIgnoreCase("/")) {
				ret = new Div((FormulaNumberElement) arg1, (FormulaNumberElement) arg2);
			} else if (type.equalsIgnoreCase("max")) {
				ret = new Max((FormulaNumberElement) arg1, (FormulaNumberElement) arg2);
			} else if (type.equalsIgnoreCase("min")) {
				ret = new Min((FormulaNumberElement) arg1, (FormulaNumberElement) arg2);
			} else if (type.equalsIgnoreCase("YEARDIFF")) {
				ret = new YearDiff((FormulaDateElement) arg1, (FormulaDateElement) arg2);
			}
		} catch (NullPointerException x) {
			Logger.getLogger(ActionContentFactory.class.getName()).throwing(
				ActionContentFactory.class.getName(),
				"createFormulaTerm",
				x);
			//		com.ibm.uvm.tools.DebugSupport.halt();
		}
		return ret;
	}

	private static Node getActionNode(Node slice) {
		NodeList sliceChildren = slice.getChildNodes();
		Node actionNode = null;
		for (int i = 0; i < sliceChildren.getLength(); ++i) {
			Node n = sliceChildren.item(i);
			if (n.getNodeName().equalsIgnoreCase("action")) {
				actionNode = n;
				break;
			}
		}
		return actionNode;
	}

	/**
	 * @return the target for ActionClarify and ActionRefine. <br>Creation
	 *         date: (09.07.2001 18:30:13)
	 */
	public static Diagnosis getTarget(Node slice, KBLoader handler) {
		//List ret = new LinkedList();
		Node actionNode = getActionNode(slice);
		NodeList actionchildren = actionNode.getChildNodes();
		for (int i = 0; i < actionchildren.getLength(); ++i) {
			Node target = actionchildren.item(i);
			if (target.getNodeName().equalsIgnoreCase("targetDiagnosis")) {
				String id = target.getAttributes().getNamedItem("ID").getNodeValue();
				Diagnosis diag = (Diagnosis) handler.search(id);
				return diag;
			}
		}
		return null;
	}

	private static FormulaElement createFormulaElement(Node node, KBLoader loader) {
		FormulaElement expr = null;
		if (node.getNodeName().equalsIgnoreCase("FormulaPrimitive")) {
			expr = createFormulaPrimitive(node, loader);
		} else if (node.getNodeName().equalsIgnoreCase("FormulaTerm")) {
			expr = createFormulaTerm(node, loader);
		} else if (node.getNodeName().equalsIgnoreCase("Count")) {
			expr = createCount(node, loader);
		} else if (node.getNodeName().equalsIgnoreCase("FormulaDatePrimitive")) {
			expr = createFormulaDatePrimitive(node, loader);
		} else if (node.getNodeName().equalsIgnoreCase("Today")) {
			expr = createToday(node, loader);
		} else if (node.getNodeName().equalsIgnoreCase("QuestionNum")) {
			// [MISC) tobi: QuestionNums are never saved directly. 
			// Is this legacy-Code or can it be removed?
			String id = "";
			id = node.getChildNodes().item(0).getNodeValue();
			if (id == null)
				id = node.getChildNodes().item(1).getNodeValue();
			expr = (FormulaNumberElement) loader.searchQuestion(id);
		}
		return expr;
	}
}