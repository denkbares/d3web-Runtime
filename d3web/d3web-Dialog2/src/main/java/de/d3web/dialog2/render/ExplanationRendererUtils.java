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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

import de.d3web.dialog2.util.DialogUtils;
import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.ECondition;
import de.d3web.explain.eNodes.ENode;
import de.d3web.explain.eNodes.EReason;
import de.d3web.explain.eNodes.reasons.EPSMethodReason;
import de.d3web.explain.eNodes.reasons.ERuleReason;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondNum;
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
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.NonTerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.TerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.contraIndication.ActionContraIndication;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;
import de.d3web.kernel.psMethods.questionSetter.ActionQuestionSetter;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;
import de.d3web.kernel.supportknowledge.Property;

public class ExplanationRendererUtils {

    public static Logger logger = Logger
	    .getLogger(ExplanationRendererUtils.class);

    private static boolean hasActiveRules = false;

    public static void explainConcreteDerivation(ResponseWriter writer,
	    UIComponent component, Diagnosis diag, XPSCase theCase)
	    throws IOException {
	Collection<Class<?>> explainContext = new LinkedList<Class<?>>();
	explainContext.add(PSMethodHeuristic.class);
	explainContext.add(PSMethodUserSelected.class);

	ExplanationFactory eFac = new ExplanationFactory(theCase);
	ENode expl = eFac.explain(diag, explainContext);
	renderENode(writer, component, expl, theCase);
    }

    public static String getStateTranslation(DiagnosisState state) {
	if (state.getName().equals("established")) {
	    return DialogUtils.getMessageFor("explain.diag_established");
	} else if (state.getName().equals("suggested")) {
	    return DialogUtils.getMessageFor("explain.diag_suggested");
	} else if (state.getName().equals("unclear")) {
	    return DialogUtils.getMessageFor("explain.diag_unclear");
	} else if (state.getName().equals("excluded")) {
	    return DialogUtils.getMessageFor("explain.diag_excluded");
	} else
	    return "";
    }

    /**
     * 
     * @return List : List of (Boolean, String) Boolean: condition is active
     *         String: kind of condition-status
     */
    private static List<Object> getStatusFor(AbstractCondition cond,
	    XPSCase theCase, boolean asException, boolean parentFired) {
	LinkedList<Object> returnList = new LinkedList<Object>();
	try {
	    if (cond.eval(theCase)) {
		returnList.add(new Boolean(true));
		if (asException) {
		    if (parentFired)
			returnList.add("exFired");
		    else
			returnList.add("exFiredEffectless");
		} else
		    returnList.add("fired");
	    }
	} catch (NoAnswerException e) {
	    returnList.add(new Boolean(false));
	    returnList.add("unrealized");
	} catch (UnknownAnswerException ex) {
	}
	if (returnList.size() == 0) {
	    returnList.add(new Boolean(false));
	    if (asException)
		returnList.add("exNotFired");
	    else
		returnList.add("notFired");
	}
	return (returnList);
    }

    public static void renderCondition(ResponseWriter writer,
	    UIComponent component, AbstractCondition cond, XPSCase theCase,
	    boolean showStatus, boolean parentFired, String when,
	    boolean parentIsNot, String rcID) throws IOException {
	if (cond instanceof TerminalCondition) {
	    renderTCondition(writer, component, (TerminalCondition) cond,
		    theCase, showStatus, false, parentFired, when, parentIsNot,
		    rcID);
	} else {
	    renderNonTCondition(writer, component, (NonTerminalCondition) cond,
		    theCase, showStatus, false, parentFired, when, rcID);
	}
    }

    public static void renderDiagnosisObject(ResponseWriter writer,
	    Diagnosis diag) throws IOException {
	writer.writeText(diag.getText() + " (" + diag.getId() + ")", "value");
    }

    private static void renderECondition(ResponseWriter writer,
	    UIComponent component, ECondition eCond, XPSCase theCase,
	    boolean asException, String rcID) throws IOException {
	if (eCond.getCondition() instanceof TerminalCondition) {
	    renderTCondition(writer, component, (TerminalCondition) eCond
		    .getCondition(), theCase, false, asException, true,
		    DialogUtils.getMessageFor("explain.if_verb"), false, rcID);
	} else if (eCond.getCondition() instanceof CondNot) {
	    renderCondition(writer, component, eCond.getCondition(), theCase,
		    false, false, DialogUtils.getMessageFor("explain.if_verb"),
		    false, rcID);
	} else {
	    renderCondition(writer, component, eCond.getCondition(), theCase,
		    false, false, DialogUtils.getMessageFor("explain.if_verb"),
		    false, rcID);
	}
    }

    private static void renderENode(ResponseWriter writer,
	    UIComponent component, ENode eNode, XPSCase theCase)
	    throws IOException {

	DialogRenderUtils.renderTableWithClass(writer, component,
		"explanationtable");

	// ProReasons
	Iterator<EReason> iter = eNode.getProReasons().iterator();
	while (iter.hasNext()) {
	    EReason eReason = iter.next();
	    renderEReason(writer, component, eReason, theCase, eNode);
	}
	// ContraReasons
	iter = eNode.getContraReasons().iterator();
	while (iter.hasNext()) {
	    EReason eReason = iter.next();
	    renderEReason(writer, component, eReason, theCase, eNode);
	}
	if (!hasActiveRules) {
	    writer.startElement("tr", component);
	    writer.startElement("td", component);
	    writer.writeText(DialogUtils
		    .getMessageFor("explain.no_active_rule"), "value");
	    writer.endElement("td");
	    writer.endElement("tr");
	}

	writer.endElement("table");
    }

    private static void renderEReason(ResponseWriter writer,
	    UIComponent component, EReason eReason, XPSCase theCase, ENode eNode)
	    throws IOException {
	writer.startElement("tr", component);

	if (eReason instanceof ERuleReason) {
	    ERuleReason ruleReason = (ERuleReason) eReason;

	    writer.writeAttribute("id", "eR_" + ruleReason.getRule().getId(),
		    "id");

	    if (ruleReason.getActiveCondition() != null) {
		hasActiveRules = true;
		// 1. column, Rule ID

		writer.startElement("td", component);
		renderRuleComplexId(writer, ruleReason.getRule());
		writer.endElement("td");

		// 2. column: Action
		writer.startElement("td", component);
		renderRuleComplexAction(writer, ruleReason.getRule());
		writer.endElement("td");

		// column Condition
		renderECondition(writer, component, ruleReason
			.getActiveCondition(), theCase, false, ruleReason
			.getRule().getId());

		if (ruleReason.getActiveException() != null) {
		    renderECondition(writer, component, ruleReason
			    .getActiveException(), theCase, true, ruleReason
			    .getRule().getId());
		}
		if (ruleReason.getActiveContext() != null) {
		    renderECondition(writer, component, ruleReason
			    .getActiveContext(), theCase, true, ruleReason
			    .getRule().getId());
		}
	    }
	} else if (eReason instanceof EPSMethodReason) {
	    EPSMethodReason epsReason = (EPSMethodReason) eReason;

	    writer.startElement("td", component);
	    writer.writeAttribute("colspan", "5", "colspan");

	    if (epsReason.getContext() == PSMethodUserSelected.class) {
		logger.warn("TODOOOO " + epsReason.getContext());
	    } else if (epsReason.getContext() == PSMethodInit.class) {
		logger.warn("TODOOOO " + epsReason.getContext());
	    }
	    writer.endElement("td");
	}
	writer.endElement("tr");
    }

    private static void renderNonTCondition(ResponseWriter writer,
	    UIComponent component, NonTerminalCondition cond, XPSCase theCase,
	    boolean showStatus, boolean asException, boolean parentFired,
	    String when, String rcID) throws IOException {

	List<Object> statusValues = null;
	if (showStatus) {
	    statusValues = getStatusFor(cond, theCase, asException, parentFired);
	}

	writer.startElement("td", component);
	if (statusValues != null) {
	    writer.writeAttribute("class", statusValues.get(1), "class");
	}

	// IF AND OR ...
	if (when != null) {
	    writer.writeText(when, "value");
	} else {
	    writer.write("&nbsp;");
	}
	if (cond instanceof CondNot) {
	    writer.write("&nbsp;");
	    writer.writeText(DialogUtils.getMessageFor("explain.not_verb"),
		    "value");
	}

	writer.endElement("td");
	writer.startElement("td", component);
	if (statusValues != null) {
	    writer.writeAttribute("class", statusValues.get(1), "class");
	}

	if (cond instanceof CondNot) {
	    writer.writeAttribute("id", rcID + "_condnot", "id");
	    List<AbstractCondition> terms = cond.getTerms();
	    DialogRenderUtils.renderTableWithClass(writer, component, "inner");
	    for (Iterator<AbstractCondition> iter = terms.iterator(); iter
		    .hasNext();) {
		writer.startElement("tr", component);
		renderCondition(writer, component, iter.next(), theCase,
			showStatus, parentFired
				&& (statusValues != null)
				&& ((Boolean) statusValues.get(0))
					.booleanValue(), null, true, rcID);
		writer.endElement("tr");
	    }
	    writer.endElement("table");
	} else if (cond instanceof CondOr) {
	    writer.writeAttribute("id", rcID + "_condor", "id");
	    List<AbstractCondition> terms = cond.getTerms();
	    DialogRenderUtils.renderTable(writer, component);
	    for (int i = 0; i < terms.size(); i++) {
		writer.startElement("tr", component);
		if (i == 0) {
		    renderCondition(writer, component, terms.get(i), theCase,
			    showStatus, parentFired
				    && (statusValues != null)
				    && ((Boolean) statusValues.get(0))
					    .booleanValue(), null, false, rcID);
		} else {
		    renderCondition(writer, component, terms.get(i), theCase,
			    showStatus, parentFired
				    && (statusValues != null)
				    && ((Boolean) statusValues.get(0))
					    .booleanValue(), DialogUtils
				    .getMessageFor("explain.or_verb"), false,
			    rcID);
		}
		writer.endElement("tr");
	    }
	    writer.endElement("table");
	} else if (cond instanceof CondAnd) {
	    writer.writeAttribute("id", rcID + "_condand", "id");
	    List<AbstractCondition> terms = cond.getTerms();
	    DialogRenderUtils.renderTable(writer, component);
	    for (int i = 0; i < terms.size(); i++) {
		writer.startElement("tr", component);
		if (i == 0) {
		    renderCondition(writer, component, terms.get(i), theCase,
			    showStatus, parentFired
				    && (statusValues != null)
				    && ((Boolean) statusValues.get(0))
					    .booleanValue(), null, false, rcID);
		} else {
		    renderCondition(writer, component, terms.get(i), theCase,
			    showStatus, parentFired
				    && (statusValues != null)
				    && ((Boolean) statusValues.get(0))
					    .booleanValue(), DialogUtils
				    .getMessageFor("explain.and_verb"), false,
			    rcID);
		}
		writer.endElement("tr");
	    }
	    writer.endElement("table");
	} else if (cond instanceof CondMofN) {
	    writer.writeAttribute("id", rcID + "_condmofn", "id");
	    List<AbstractCondition> terms = cond.getTerms();

	    CondMofN cmofn = (CondMofN) cond;

	    DialogRenderUtils.renderTable(writer, component);
	    writer.startElement("tr", component);
	    if (cmofn.getMin() >= 0 && cmofn.getMax() >= 0) {
		if (cmofn.getMin() == cmofn.getMax()) {
		    if (cmofn.getMin() == 1) {
			writer.writeText(DialogUtils.getMessageWithParamsFor(
				"explain.mofn_exact_1", new Object[] { cmofn
					.getMin() }), "value");
		    } else {
			writer.writeText(DialogUtils.getMessageWithParamsFor(
				"explain.mofn_exact_more", new Object[] { cmofn
					.getMin() }), "value");
		    }
		} else {
		    writer.writeText(DialogUtils.getMessageWithParamsFor(
			    "explain.mofn_in", new Object[] { cmofn.getMin(),
				    cmofn.getMax() }), "value");
		}
	    } else if (cmofn.getMin() >= 0) {
		if (cmofn.getMin() == 1) {
		    writer.writeText(DialogUtils.getMessageWithParamsFor(
			    "explain.mofn_min_1",
			    new Object[] { cmofn.getMin() }), "value");
		} else {
		    writer.writeText(DialogUtils.getMessageWithParamsFor(
			    "explain.mofn_min_more", new Object[] { cmofn
				    .getMin() }), "value");
		}
	    } else if (cmofn.getMax() >= 0) {
		if (cmofn.getMax() == 1) {
		    writer.writeText(DialogUtils.getMessageWithParamsFor(
			    "explain.mofn_max_1",
			    new Object[] { cmofn.getMax() }), "value");
		} else {
		    writer.writeText(DialogUtils.getMessageWithParamsFor(
			    "explain.mofn_max_more", new Object[] { cmofn
				    .getMax() }), "value");
		}
	    }
	    writer.endElement("tr");
	    for (int i = 0; i < terms.size(); i++) {

		writer.startElement("tr", component);
		if (i == 0) {
		    renderCondition(writer, component, terms.get(i), theCase,
			    showStatus, parentFired
				    && (statusValues != null)
				    && ((Boolean) statusValues.get(0))
					    .booleanValue(), null, false, rcID);
		} else {
		    renderCondition(
			    writer,
			    component,
			    terms.get(i),
			    theCase,
			    showStatus,
			    parentFired
				    && (statusValues != null)
				    && ((Boolean) statusValues.get(0))
					    .booleanValue(),
			    DialogUtils
				    .getMessageFor("explain.mofn_connector_verb"),
			    false, rcID);
		}
		writer.endElement("tr");
	    }
	    writer.endElement("table");
	}
	writer.endElement("td");
    }

    public static void renderRuleComplexAction(ResponseWriter writer,
	    RuleComplex rc) throws IOException {
	if (rc.getAction() instanceof ActionHeuristicPS) {
	    ActionHeuristicPS ac = (ActionHeuristicPS) rc.getAction();
	    Score score = ac.getScore();
	    writer.writeText(score.getSymbol(), "value");
	    if (score.equals(Score.P7)) {
		writer.write(" (+++) ");
	    } else if (score.equals(Score.N7)) {
		writer.write(" (---) ");
	    } else {
		Double scoreval = score.getScore();
		writer.writeText(" (" + scoreval.intValue() + ") ", "value");
	    }

	}
	// TODO what has to be rendered here?
	else if (rc.getAction() instanceof ActionContraIndication) {

	} else if (rc.getAction() instanceof ActionNextQASet) {

	} else if (rc.getAction() instanceof ActionQuestionSetter) {

	}
    }

    public static void renderRuleComplexId(ResponseWriter writer, RuleComplex rc)
	    throws IOException {
	writer.writeText("(" + rc.getId() + "):", "value");
    }

    public static void renderTCondition(ResponseWriter writer,
	    UIComponent component, TerminalCondition cond, XPSCase theCase,
	    boolean showStatus, boolean asException, boolean parentFired,
	    String when, boolean parentIsNot, String rcID) throws IOException {
	List<Object> statusValues = null;
	if (showStatus) {
	    statusValues = getStatusFor(cond, theCase, asException, parentFired);
	}

	if (!parentIsNot) {
	    // table cell with "IF", "AND", "OR", ...
	    writer.startElement("td", component);
	    if (statusValues != null) {
		writer.writeAttribute("class", statusValues.get(1), "class");
	    }
	    if (when != null) {
		writer.writeText(when, "value");
	    } else {
		writer.write("&nbsp;");
	    }
	    writer.endElement("td");
	}

	writer.startElement("td", component);

	if (statusValues != null) {
	    writer.writeAttribute("class", statusValues.get(1), "class");
	}

	if (cond instanceof CondEqual) {
	    CondEqual ce = (CondEqual) cond;
	    writer.writeAttribute("id", rcID + "_condequal_"
		    + ce.getQuestion().getId(), "id");
	    writer.writeText(ce.getQuestion().getText() + " ("
		    + ce.getQuestion().getId() + ")", "value");
	    writer.write(" = ");
	    for (Iterator<Answer> iter = ce.getValues().iterator(); iter
		    .hasNext();) {
		Answer ans = iter.next();
		if (ans instanceof AnswerChoice) {
		    AnswerChoice ansC = (AnswerChoice) ans;
		    writer
			    .writeText(ansC.getText() + " (" + ansC.getId()
				    + ")", "value");
		} else if (ans instanceof AnswerUnknown) {
		    writer.writeText(AnswerUnknown.UNKNOWN_VALUE, "value");
		}
	    }
	} else if (cond instanceof CondKnown) {
	    CondKnown ck = (CondKnown) cond;
	    writer.writeAttribute("id", rcID + "_condknown_"
		    + ck.getQuestion().getId(), "id");
	    writer.writeText(ck.getQuestion().getText() + " ("
		    + ck.getQuestion().getId() + ")", "value");
	    writer
		    .writeText(" = "
			    + DialogUtils.getMessageFor("explain.known"),
			    "value");
	} else if (cond instanceof CondUnknown) {
	    CondUnknown cuk = (CondUnknown) cond;
	    writer.writeAttribute("id", rcID + "_condunknown_"
		    + cuk.getQuestion().getId(), "id");
	    writer.writeText(cuk.getQuestion().getText() + " ("
		    + cuk.getQuestion().getId() + ")", "value");
	    writer.writeText(" = "
		    + DialogUtils.getMessageFor("explain.unknown"), "value");
	} else if (cond instanceof CondDState) {
	    CondDState cds = (CondDState) cond;
	    writer.writeAttribute("id", rcID + "_condstate_"
		    + cds.getDiagnosis().getId(), "id");
	    renderDiagnosisObject(writer, cds.getDiagnosis());
	    writer.write(" = ");
	    writer.writeText(getStateTranslation(cds.getStatus()), "value");
	} else if ((cond instanceof CondNumEqual)
		|| (cond instanceof CondNumGreater)
		|| (cond instanceof CondNumGreaterEqual)
		|| (cond instanceof CondNumLess)
		|| (cond instanceof CondNumLessEqual)) {

	    CondNum cn = (CondNum) cond;
	    writer.writeAttribute("id", rcID + "_condnumequal_"
		    + cn.getQuestion().getId(), "id");
	    writer.writeText(cn.getQuestion().getText() + " ("
		    + cn.getQuestion().getId() + ")", "value");

	    if (cn instanceof CondNumEqual)
		writer.write(" = ");
	    else if (cn instanceof CondNumGreater)
		writer.writeText(
			" " + DialogUtils.getMessageFor("explain.numgreater")
				+ " ", "value");
	    else if (cn instanceof CondNumGreaterEqual)
		writer.writeText(" "
			+ DialogUtils.getMessageFor("explain.numgreater_equal")
			+ " ", "value");
	    else if (cn instanceof CondNumLess)
		writer.writeText(" "
			+ DialogUtils.getMessageFor("explain.numless") + " ",
			"value");
	    else if (cn instanceof CondNumLessEqual)
		writer.writeText(" "
			+ DialogUtils.getMessageFor("explain.numless_equal")
			+ " ", "value");

	    writer.writeText(cn.getAnswerValue().toString(), "value");

	    String unit = (String) cn.getQuestion().getProperties()
		    .getProperty(Property.UNIT);
	    if (unit != null) {
		writer.writeText(" " + unit, "value");
	    }

	} else if (cond instanceof CondNumIn) {
	    CondNumIn cni = (CondNumIn) cond;
	    writer.writeAttribute("id", rcID + "_condnumin_"
		    + cni.getQuestion().getId(), "id");
	    writer.writeText(cni.getQuestion().getText() + " ("
		    + cni.getQuestion().getId() + ")", "value");
	    writer.writeText(" "
		    + DialogUtils
			    .getMessageWithParamsFor("explain.numin",
				    new Object[] { cni.getMinValue(),
					    cni.getMaxValue() }), "value");
	} else if (cond instanceof CondTextContains) {
	    // FIXME depends on sourceforge bug #1801281, has to be tested
	    CondTextContains ctc = (CondTextContains) cond;
	    writer.writeAttribute("id", rcID + "_condtextcont_"
		    + ctc.getQuestion().getId(), "id");
	    writer.writeText(ctc.getQuestion().getText() + " ("
		    + ctc.getQuestion().getId() + ")", "value");
	    writer.writeText(" "
		    + DialogUtils.getMessageFor("explain.textcontains") + " ",
		    "value");
	    writer.writeText(ctc.getValue(), "value");
	} else if (cond instanceof CondTextEqual) {
	    // FIXME depends on sourceforge bug #1801281, has to be tested
	    CondTextEqual cte = (CondTextEqual) cond;
	    writer.writeAttribute("id", rcID + "_condtextequal_"
		    + cte.getQuestion().getId(), "id");
	    writer.writeText(cte.getQuestion().getText() + " ("
		    + cte.getQuestion().getId() + ")", "value");
	    writer.writeText(" "
		    + DialogUtils.getMessageFor("explain.textequals") + " ",
		    "value");
	    writer.writeText(cte.getValue(), "value");
	} else {
	    logger.warn(cond.getClass().toString() + " -> unhandled type");
	}
	writer.endElement("td");
    }

}
