package de.d3web.persistence.xml.loader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceNo;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceYes;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreater;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreaterEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLessEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.kernel.domainModel.ruleCondition.CondTextEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondUnknown;
import de.d3web.persistence.xml.loader.rules.RuleConditionPersistenceHandler;
import de.d3web.xml.domtools.DOMAccess;
import de.d3web.xml.utilities.XMLTools;

/**
 * FactoryClass for building all rule conditions from DOM-tree. <br>
 * Creation date: (12.07.2001 17:00:46)
 * 
 * @author Norman Brümmer
 */
public class ConditionFactory {

	/**
	 * Creates a rule condition from a DOM-node respecting the given context
	 * Creation date: (13.07.2001 16:27:01)
	 */
	public static AbstractCondition createCondition(Node condNode, KBLoader loader, Class context) {

		String type = condNode.getAttributes().getNamedItem("type").getNodeValue();

		// terminals

		if (type.equals("DState")) {
			return createCondDState(condNode, loader, context);
		} else if (type.equals("choiceNo")) {
			return createCondChoiceNo(condNode, loader);
		} else if (type.equals("choiceYes")) {
			return createCondChoiceYes(condNode, loader);
		} else if (type.equals("equal")) {
			return createCondEqual(condNode, loader);
		} else if (type.equals("unknown")) {
			return createCondUnknown(condNode, loader);
		} else if (type.equals("known")) {
			return createCondKnown(condNode, loader);
		} else if (type.equals("numGreater")) {
			return createCondNumGreater(condNode, loader);
		} else if (type.equals("numGreaterEqual")) {
			return createCondNumGreaterEqual(condNode, loader);
		} else if (type.equals("numIn")) {
			return createCondNumIn(condNode, loader);
		} else if (type.equals("numLess")) {
			return createCondNumLess(condNode, loader);
		} else if (type.equals("numLessEqual")) {
			return createCondNumLessEqual(condNode, loader);
		} else if (type.equals("numEqual")) {
			return createCondNumEqual(condNode, loader);
		} else if (type.equals("textContains")) {
			return createCondTextContains(condNode, loader);
		} else if (type.equals("textEqual")) {
			return createCondTextEqual(condNode, loader);
		}

		// non-terminals

		else if (type.equals("or")) {
			return createCondOr(condNode, loader, context);
		} else if (type.equals("and")) {
			return createCondAnd(condNode, loader, context);
		} else if (type.equals("not")) {
			return createCondNot(condNode, loader, context);
		} else if (type.equals("MofN")) {
			return createCondMofN(condNode, loader, context);
		}

		// error

		else if (type.equals("dstate")) {
			// [MISC]:aha:legacy code
			Logger.getLogger(ConditionFactory.class.getName()).info(
					"'dstate' is treated as if it was 'DState'");
			return createCondDState(condNode, loader, context);
		} else {

			RuleConditionPersistenceHandler additionalHandler = searchAdditionalConditionPersistenceHandler(
					loader, condNode);
			if (additionalHandler == null) {
				Logger.getLogger(ConditionFactory.class.getName()).warning(
						"can't handle Conditions of type '" + type + "'");
				return null;
			} else {
				return additionalHandler.loadCondition(condNode, loader, context);
			}
		}

	}

	private static RuleConditionPersistenceHandler searchAdditionalConditionPersistenceHandler(
			KBLoader loader, Node condNode) {
		Iterator<RuleConditionPersistenceHandler> iter = loader.getRuleLoader()
				.getRuleConditionHandlers().iterator();
		while (iter.hasNext()) {
			RuleConditionPersistenceHandler handler = iter.next();
			if (handler.checkCompatibility(condNode)) {
				return handler;
			}
		}
		return null;
	}

	private static AbstractCondition createCondChoiceNo(Node condNode, KBLoader loader) {
		CondChoiceNo ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		QuestionYN q = (QuestionYN) loader.searchQuestion(id);
		ret = new CondChoiceNo(q);
		return ret;
	}

	private static AbstractCondition createCondChoiceYes(Node condNode, KBLoader loader) {
		CondChoiceYes ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		QuestionYN q = (QuestionYN) loader.searchQuestion(id);
		ret = new CondChoiceYes(q);
		return ret;
	}

	private static AbstractCondition createCondEqual(Node condNode, KBLoader loader) {
		CondEqual ret = null;
		String qid = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String valuestring = condNode.getAttributes().getNamedItem("value").getNodeValue();
		Question q = loader.searchQuestion(qid);
		Answer answer = loader.searchAnswer(valuestring);
		if (answer == null) {
			Logger.getLogger(ConditionFactory.class.getName()).warning(
					"CondEqual for QuestionChoice " + qid + " and Answer " + valuestring
							+ " can not be handled - replaced with UNKNOWNANSWER");
			answer = q.getUnknownAlternative();
		}
		if (q instanceof QuestionChoice) {
			ret = new CondEqual((QuestionChoice) q, answer);
		} else {
			ret = new CondEqual(q, q.getUnknownAlternative());
		}
		return ret;
	}

	private static AbstractCondition createCondKnown(Node condNode, KBLoader loader) {
		CondKnown ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		Question question = loader.searchQuestion(id);
		ret = new CondKnown(question);
		return ret;
	}

	private static AbstractCondition createCondNumEqual(Node condNode, KBLoader loader) {
		CondNumEqual ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = condNode.getAttributes().getNamedItem("value").getNodeValue();
		QuestionNum q = (QuestionNum) loader.searchQuestion(id);
		ret = new CondNumEqual(q, new Double(value));
		return ret;
	}

	private static AbstractCondition createCondNumGreater(Node condNode, KBLoader loader) {
		CondNumGreater ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = condNode.getAttributes().getNamedItem("value").getNodeValue();
		QuestionNum q = (QuestionNum) loader.searchQuestion(id);
		ret = new CondNumGreater(q, new Double(value));
		return ret;
	}

	private static AbstractCondition createCondNumGreaterEqual(Node condNode, KBLoader loader) {
		CondNumGreaterEqual ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = condNode.getAttributes().getNamedItem("value").getNodeValue();
		QuestionNum q = (QuestionNum) loader.searchQuestion(id);
		ret = new CondNumGreaterEqual(q, new Double(value));
		return ret;
	}

	private static AbstractCondition createCondNumLess(Node condNode, KBLoader loader) {
		CondNumLess ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = condNode.getAttributes().getNamedItem("value").getNodeValue();
		QuestionNum q = (QuestionNum) loader.searchQuestion(id);
		ret = new CondNumLess(q, new Double(value));
		return ret;
	}

	private static AbstractCondition createCondNumLessEqual(Node condNode, KBLoader loader) {
		CondNumLessEqual ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = condNode.getAttributes().getNamedItem("value").getNodeValue();
		QuestionNum q = (QuestionNum) loader.searchQuestion(id);
		ret = new CondNumLessEqual(q, new Double(value));
		return ret;
	}

	private static AbstractCondition createCondTextContains(Node condNode, KBLoader loader) {
		CondTextContains ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = null;
		NodeList cc = condNode.getChildNodes();
		for (int i = 0; i < cc.getLength(); i++) {
			if ("Value".equals(cc.item(i).getNodeName()))
				value = DOMAccess.getText(cc.item(i));
			value = XMLTools.prepareFromCDATA(value);
		}

		// [MISC]:aha:legacy code
		if (value == null)
			value = condNode.getAttributes().getNamedItem("value").getNodeValue();

		QuestionText q = (QuestionText) loader.searchQuestion(id);
		ret = new CondTextContains(q, value);
		return ret;
	}

	private static AbstractCondition createCondTextEqual(Node condNode, KBLoader loader) {
		CondTextEqual ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String value = null;
		NodeList cc = condNode.getChildNodes();
		for (int i = 0; i < cc.getLength(); i++) {
			if ("Value".equals(cc.item(i).getNodeName()))
				value = DOMAccess.getText(cc.item(i));
			value = XMLTools.prepareFromCDATA(value);
		}

		// [MISC]:aha:legacy code
		if (value == null)
			value = condNode.getAttributes().getNamedItem("value").getNodeValue();

		QuestionText q = (QuestionText) loader.searchQuestion(id);
		ret = new CondTextEqual(q, value);
		return ret;
	}

	private static AbstractCondition createCondUnknown(Node condNode, KBLoader loader) {
		CondUnknown ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		Question question = loader.searchQuestion(id);
		ret = new CondUnknown(question);
		return ret;
	}

	private static DiagnosisState getDiagnosisState(String status) {

		if (status.equals("established"))
			return DiagnosisState.ESTABLISHED;

		if (status.equals("excluded"))
			return DiagnosisState.EXCLUDED;

		if (status.equals("suggested"))
			return DiagnosisState.SUGGESTED;

		if (status.equals("unclear"))
			return DiagnosisState.UNCLEAR;

		return null;
	}

	private static AbstractCondition createCondAnd(Node condNode, KBLoader loader, Class context) {
		CondAnd ret = null;
		List terms = new LinkedList();
		NodeList nl = condNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("Condition")) {
				terms.add(createCondition(n, loader, context));
			}
		}
		ret = new CondAnd(terms);
		return ret;
	}

	private static AbstractCondition createCondDState(Node condNode, KBLoader loader, Class context) {
		CondDState ret = null;
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();
		String status = condNode.getAttributes().getNamedItem("value").getNodeValue();
		Diagnosis d = loader.searchDiagnosis(id);
		ret = new CondDState(d, getDiagnosisState(status), context);
		return ret;
	}

	private static AbstractCondition createCondMofN(Node condNode, KBLoader handler, Class context) {
		CondMofN ret = null;

		List terms = new LinkedList();
		int min = Integer.parseInt(condNode.getAttributes().getNamedItem("min").getNodeValue());
		// [HOTFIX]:aha:missing max attribute...
		// it doesn't hurt, it is semantically correct, it gives a warning
		int max = -1;
		try {
			max = Integer.parseInt(condNode.getAttributes().getNamedItem("max").getNodeValue());
		} catch (NullPointerException ex) {
			Logger.getLogger(ConditionFactory.class.getName()).throwing(
					ConditionFactory.class.getName(), "createCondMofN", ex);
		}

		NodeList nl = condNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("Condition")) {
				terms.add(createCondition(n, handler, context));
			}
		}

		if (max == -1) {
			Logger.getLogger(ConditionFactory.class.getName()).warning(
					"ConfMofN: max attribute missing, setting to #terms");
			max = terms.size();
		}

		ret = new CondMofN(terms, min, max);
		return ret;
	}

	private static AbstractCondition createCondNot(Node condNode, KBLoader handler, Class context) {

		CondNot ret = null;

		AbstractCondition cond = null;

		NodeList nl = condNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("Condition")) {
				cond = createCondition(n, handler, context);
			}
		}
		ret = new CondNot(cond);
		return ret;
	}

	private static AbstractCondition createCondNumIn(Node condNode, KBLoader loader) {
		String id = condNode.getAttributes().getNamedItem("ID").getNodeValue();

		QuestionNum q = null;
		try {
			q = (QuestionNum) loader.searchQuestion(id);
		} catch (ClassCastException e) {
			Logger.getLogger(ConditionFactory.class.getName()).warning(
					"createCondNumIn(): question with id " + id + " is no QuestionNum!");
		}

		NodeList children = condNode.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if ((child.getNodeType() == Node.ELEMENT_NODE)
					&& (child.getNodeName().equals(NumericalIntervalsUtils.TAG))) {
				NumericalInterval interval = NumericalIntervalsCodec
						.getInstance().readNumericalInterval(child);
				return new CondNumIn(q, interval);
			}
		}

		return null;
	}

	private static AbstractCondition createCondOr(Node condNode, KBLoader handler, Class context) {
		CondOr ret = null;
		List terms = new LinkedList();
		NodeList nl = condNode.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("Condition")) {
				terms.add(createCondition(n, handler, context));
			}
		}
		ret = new CondOr(terms);
		return ret;
	}
}