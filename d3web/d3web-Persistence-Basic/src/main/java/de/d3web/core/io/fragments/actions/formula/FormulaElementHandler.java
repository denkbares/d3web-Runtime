/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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
package de.d3web.core.io.fragments.actions.formula;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.abstraction.formula.Count;
import de.d3web.abstraction.formula.FormulaDate;
import de.d3web.abstraction.formula.YearDiff;
import de.d3web.abstraction.formula.FormulaDateElement;
import de.d3web.abstraction.formula.FormulaDatePrimitive;
import de.d3web.abstraction.formula.FormulaElement;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.FormulaNumberElement;
import de.d3web.abstraction.formula.FormulaNumberPrimitive;
import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.QDateWrapper;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.abstraction.formula.Today;
import de.d3web.abstraction.formula.Operator.Operation;
import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;

/**
 * Handels all standard FormulaElements. If additional FormulaElements are used
 * in Plugins, they must contain their own FragmentHandler with a higher
 * priority.
 * 
 * @author Markus Friedrich (denkbares GmbH), Norman Brümmer
 */
public class FormulaElementHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		String nodeName = element.getNodeName();
		return (nodeName.equals("FormulaTerm")
				|| nodeName.equals("FormulaDatePrimitive")
				|| nodeName.equals("Today")
				|| nodeName.equals("Count") || nodeName.equals("FormulaPrimitive"));
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof FormulaElement);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		FormulaElement expr = null;
		if (element.getNodeName().equalsIgnoreCase("FormulaPrimitive")) {
			expr = createFormulaPrimitive(element, kb);
		}
		else if (element.getNodeName().equalsIgnoreCase("FormulaTerm")) {
			expr = createFormulaTerm(element, kb);
		}
		else if (element.getNodeName().equalsIgnoreCase("Count")) {
			expr = createCount(element, kb);
		}
		else if (element.getNodeName().equalsIgnoreCase("FormulaDatePrimitive")) {
			expr = createFormulaDatePrimitive(element, kb);
		}
		else if (element.getNodeName().equalsIgnoreCase("Today")) {
			expr = createToday(element, kb);
		}
		else if (element.getNodeName().equalsIgnoreCase("QuestionNum")) {
			// [MISC) tobi: QuestionNums are never saved directly.
			// Is this legacy-Code or can it be removed?
			String id = "";
			id = element.getChildNodes().item(0).getNodeValue();
			if (id == null) id = element.getChildNodes().item(1).getNodeValue();
			expr = (FormulaNumberElement) kb.searchQuestion(id);
		}
		return expr;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = null;
		PersistenceManager pm = PersistenceManager.getInstance();
		if (object instanceof YearDiff) {
			YearDiff fa = (YearDiff) object;
			element = createFormulaTerm(doc, fa.getSymbol(), fa.getArg1(), fa.getArg2());
		}
		else if (object instanceof Operator) {
			Operator fa = (Operator) object;
			element = createFormulaTerm(doc, fa.getSymbol(), fa.getArg1(), fa.getArg2());
		}
		else if (object instanceof FormulaDate) {
			FormulaDate fa = (FormulaDate) object;
			element = createFormulaPrimitive(doc, "FormulaDatePrimitive", "FormulaDate",
					FormulaDate.format.format(fa.getValue()));
		}
		else if (object instanceof QDateWrapper) {
			QDateWrapper fa = (QDateWrapper) object;
			element = createFormulaPrimitive(doc, "FormulaDatePrimitive", "QDateWrapper",
					fa.getQuestion().getId());
		}
		else if (object instanceof Today) {
			Today fa = (Today) object;
			element = doc.createElement("Today");
			element.appendChild(pm.writeFragment(fa.getArg(), doc));
		}
		else if (object instanceof Count) {
			Count fa = (Count) object;
			element = doc.createElement("Count");
			Element questionmcNode = doc.createElement("QuestionMC");
			questionmcNode.setTextContent(fa.getQuestionMC().getId());
			element.appendChild(questionmcNode);
		}
		else if (object instanceof FormulaNumber) {
			FormulaNumber fa = (FormulaNumber) object;
			element = createFormulaPrimitive(doc, "FormulaPrimitive", "FormulaNumber",
					fa.getValue().toString());
		}
		else if (object instanceof QNumWrapper) {
			QNumWrapper fa = (QNumWrapper) object;
			element = createFormulaPrimitive(doc, "FormulaPrimitive", "QNumWrapper",
					fa.getQuestion().getId());
		}
		else {
			throw new IOException("Object " + object + " not supported in FormulaElementHandler.");
		}
		return element;
	}

	private Element createFormulaTerm(Document doc, String symbol, Object arg_1, Object arg_2) throws DOMException, NoSuchFragmentHandlerException, IOException {
		PersistenceManager pm = PersistenceManager.getInstance();
		Element element = doc.createElement("FormulaTerm");
		element.setAttribute("type", symbol);
		Element arg1 = doc.createElement("arg1");
		arg1.appendChild(pm.writeFragment(arg_1, doc));
		Element arg2 = doc.createElement("arg2");
		arg2.appendChild(pm.writeFragment(arg_2, doc));
		element.appendChild(arg1);
		element.appendChild(arg2);
		return element;
	}

	private Element createFormulaPrimitive(Document doc, String tagname, String type, String valuetext) {
		Element element = doc.createElement(tagname);
		element.setAttribute("type", type);
		Element valueNode = doc.createElement("Value");
		valueNode.setTextContent(valuetext);
		element.appendChild(valueNode);
		return element;
	}

	private static FormulaNumberPrimitive createFormulaPrimitive(Node termNode, KnowledgeBase kb) {
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
					}
					else if (type.equalsIgnoreCase("QNumWrapper")) {
						QuestionNum qnum = (QuestionNum) kb.searchQuestion(val);
						ret = new QNumWrapper(qnum);
					}
				}
			}
		}
		return ret;
	}

	private static FormulaElement createFormulaTerm(Node termNode,
			KnowledgeBase kb) throws IOException {
		FormulaElement ret = null;
		FormulaElement arg1 = null;
		FormulaElement arg2 = null;
		String type = termNode.getAttributes().getNamedItem("type")
				.getNodeValue();
		NodeList arguments = termNode.getChildNodes();
		for (int i = 0; i < arguments.getLength(); ++i) {
			Node arg = arguments.item(i);
			if (arg.getNodeName().equalsIgnoreCase("arg1")
					|| arg.getNodeName().equalsIgnoreCase("arg2")) {
				boolean wasArg1 = arg.getNodeName().equalsIgnoreCase("arg1");
				List<Element> nl = XMLUtil.getElementList(arg.getChildNodes());
				for (Element argElem : nl) {
					FormulaElement elem = (FormulaElement) PersistenceManager
							.getInstance().readFragment(argElem, kb);
					if (elem != null) {
						if (wasArg1) {
							arg1 = elem;
						}
						else {
							arg2 = elem;
						}
					}
				}
			}
		}
		if (type.equalsIgnoreCase("+")) {
			ret = new Operator((FormulaNumberElement) arg1,
					(FormulaNumberElement) arg2, Operation.Add);
		}
		else if (type.equalsIgnoreCase("-")) {
			ret = new Operator((FormulaNumberElement) arg1,
					(FormulaNumberElement) arg2, Operation.Sub);
		}
		else if (type.equalsIgnoreCase("*")) {
			ret = new Operator((FormulaNumberElement) arg1,
					(FormulaNumberElement) arg2, Operation.Mult);
		}
		else if (type.equalsIgnoreCase("/")) {
			ret = new Operator((FormulaNumberElement) arg1,
					(FormulaNumberElement) arg2, Operation.Div);
		}
		else if (type.equalsIgnoreCase("max")) {
			ret = new Operator((FormulaNumberElement) arg1,
					(FormulaNumberElement) arg2, Operation.Max);
		}
		else if (type.equalsIgnoreCase("min")) {
			ret = new Operator((FormulaNumberElement) arg1,
					(FormulaNumberElement) arg2, Operation.Min);
		}
		else if (type.equalsIgnoreCase("YEARDIFF")) {
			ret = new YearDiff((FormulaDateElement) arg1,
					(FormulaDateElement) arg2);
		}

		return ret;
	}

	private static Count createCount(Node countNode, KnowledgeBase kb) {
		Count ret = null;
		NodeList nl = countNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase("QuestionMC")) {
				NodeList qnl = n.getChildNodes();
				String id = qnl.item(0).getNodeValue();
				if (id == null) id = qnl.item(1).getNodeValue();
				QuestionMC countQuestion = (QuestionMC) kb.searchQuestion(id);
				if (countQuestion != null) ret = new Count(countQuestion);
			}
		}
		return ret;
	}

	private static FormulaDatePrimitive createFormulaDatePrimitive(Node termNode, KnowledgeBase kb) throws IOException {
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
							date = FormulaDate.format.parse(val);
						}
						catch (ParseException e) {
							throw new IOException(
									"ParseExpetion while creating FormulaDatePrimitive", e);
						}
						ret = new FormulaDate(date);
					}
					else if (type.equalsIgnoreCase("QDateWrapper")) {
						QuestionDate qdate = (QuestionDate) kb.searchQuestion(val);
						ret = new QDateWrapper(qdate);
					}
				}
			}
		}
		return ret;
	}

	private static Today createToday(Node todayNode, KnowledgeBase kb) throws IOException {
		Today ret = null;
		Collection<Element> nl = XMLUtil.getElementList(todayNode.getChildNodes());
		for (Element argElem : nl) {
			FormulaElement elem = (FormulaElement) PersistenceManager.getInstance().readFragment(
					argElem, kb);
			if (elem instanceof FormulaNumberElement) {
				ret = new Today((FormulaNumberElement) elem);
			}
		}
		return ret;
	}
}
