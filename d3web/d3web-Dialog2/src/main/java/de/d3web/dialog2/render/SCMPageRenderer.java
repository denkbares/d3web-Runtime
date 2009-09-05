package de.d3web.dialog2.render;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import de.d3web.dialog2.component.html.UISCMPage;
import de.d3web.dialog2.util.CoveredFinding;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.SimilarFinding;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisScore;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.setCovering.Finding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.simple.SimpleSCResult;
import de.d3web.kernel.supportknowledge.Property;

public class SCMPageRenderer extends Renderer {

    private static String format(double val) {
	NumberFormat nf = NumberFormat.getInstance();
	nf.setMaximumFractionDigits(DialogUtils.getDialogSettings()
		.getScm_digitcount());
	return nf.format(val);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
	    throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	XPSCase theCase = DialogUtils.getDialog().getTheCase();

	String diagToExplain = ((UISCMPage) component).getDiag();

	Diagnosis kbDiag = theCase.getKnowledgeBase().searchDiagnosis(
		diagToExplain);
	if (kbDiag == null) {
	    return;
	}
	SCDiagnosis scDiag = getSCDiagnosis(kbDiag);

	int totalScore = (int) (Math
		.round(scDiag.getCurrentScore(theCase) * 100));
	double coveredScore = scDiag.getCoveredSymptomWeightSum(theCase)
		- scDiag.getPositiveRemainderCoveringStrengthSum(theCase);
	double consideredScore = PSMethodSetCovering.getInstance()
		.getTotalObservedFindingsWeightSum(theCase)
		- scDiag.getPositiveRemainderCoveringStrengthSum(theCase)
		- scDiag.getNegativeRemainderCoveringStrengthSum(theCase);

	// simple SC Problemsolving

	if (isSimple(theCase)) {
	    SimpleSCResult result = scDiag.getSimpleSymptomSum(theCase);
	    totalScore = (int) (result.getPrecision() * 100);
	    coveredScore = result.getAnsweredWithPosDefinition();
	    consideredScore = result.getPositiveDefinedQuestionsInModel();
	}

	DialogRenderUtils.renderTableWithClass(writer, component, "scmTable");
	writer.startElement("tr", component);
	writer.startElement("th", component);
	String isSimple = "";
	if (isSimple(theCase)) {
	    isSimple = " (SCMSimple)";
	}
	writer.writeText(DialogUtils
		.getMessageFor("scm.explainD3.diagnosisTitle")
		+ isSimple, "value");
	writer.endElement("th");
	writer.startElement("th", component);
	writer.writeAttribute("class", "score", "class");
	writer.writeText(DialogUtils
		.getMessageFor("scm.explainD3.totalScoreText"), "value");
	writer.writeText(" : (", "value");
	if (isSimple(theCase)) {
	    writer.writeText(DialogUtils.getMessageFor("scm.explainD3.recall")
		    + " = "
		    + DialogUtils.getMessageFor("scm.explainD3.answered"),
		    "value");
	} else {
	    writer.writeText(DialogUtils
		    .getMessageFor("scm.explainD3.coveredScoreText"), "value");
	}

	writer.writeText(" / ", "value");
	if (isSimple(theCase)) {
	    writer.writeText(DialogUtils
		    .getMessageFor("scm.explainD3.relevant"), "value");
	} else {
	    writer.writeText(DialogUtils
		    .getMessageFor("scm.explainD3.consideredScoreText"),
		    "value");
	}
	writer.writeText(")", "value");
	writer.endElement("th");
	writer.endElement("tr");
	writer.startElement("tr", component);
	writer.startElement("td", component);
	writer.writeAttribute("id", "scmdiag_" + kbDiag.getId(), "id");
	writer.writeText(kbDiag.getText(), "value");
	DiagnosisScore score = kbDiag
		.getScore(theCase, PSMethodHeuristic.class);
	if (score.equals(Score.N7)) {
	    writer.writeText(
		    " (" + DialogUtils.getMessageFor("scm.explainD3.excluded")
			    + ")", "value");
	}
	writer.endElement("td");
	writer.startElement("td", component);
	writer.writeAttribute("class", "score", "class");
	writer.writeAttribute("id", "scmdiagscore_" + kbDiag.getId(), "id");
	writer.writeText(totalScore + "% : " + format(coveredScore) + " / "
		+ format(consideredScore), "value");
	writer.endElement("td");
	writer.endElement("tr");
	writer.endElement("table");

	writer.startElement("h3", component);
	writer.writeText(DialogUtils
		.getMessageFor("scm.explainD3.findings.description"), "value");
	writer.endElement("h3");

	renderExplainedFindings(writer, component, theCase, scDiag);

	renderUnexplainedFindings(writer, component, theCase, scDiag);

	renderIgnoredFindings(writer, component, theCase, scDiag);

    }

    private CoveredFinding getCoveredFinding(Finding f, double cStrength,
	    double score, XPSCase theCase) {
	return getCoveredFinding(f, null, cStrength, score, 0, theCase);
    }

    private CoveredFinding getCoveredFinding(Finding f, Finding similar,
	    double cStrength, double score, double similarity, XPSCase theCase) {
	CoveredFinding cFinding = new CoveredFinding(cStrength, f
		.getWeight(theCase), score, f.getWeight(theCase) * cStrength, f
		.getNamedObject().getText(), f.verbalize());
	if (similar != null) {
	    cFinding.setSimFinding(new SimilarFinding(similarity, similar
		    .getNamedObject().getText(), similar.verbalize()));
	}
	return cFinding;
    }

    private Object[] getFindingsWithCoveringRelation(XPSCase theCase,
	    SCDiagnosis scDiag, int sign) {
	List<CoveredFinding> cFindingList = new ArrayList<CoveredFinding>();

	double totalScore = 0;
	double totalPossibleScore = 0;

	Set<PredictedFinding> explained = scDiag
		.getTransitiveExplainedFindings(theCase);
	if (explained != null) {
	    for (PredictedFinding f : explained) {
		double cStrength = scDiag.getCoveringStrength(f) * sign;
		// take only these covering relations with right sign
		if (cStrength > 0) {
		    double score = f.getWeight(theCase) * cStrength;
		    totalScore += score;
		    totalPossibleScore += score;
		    cFindingList.add(getCoveredFinding(f, cStrength, score,
			    theCase));
		}
	    }
	}
	Hashtable<Finding, PredictedFinding> similarFindings = scDiag
		.getObservedFindingsWithSimilarity(theCase);
	if (similarFindings != null) {
	    for (Finding observed : similarFindings.keySet()) {
		PredictedFinding similar = similarFindings.get(observed);
		double cStrength = scDiag.getCoveringStrength(similar) * sign;
		// take only these covering relations with right sign
		if (cStrength > 0) {
		    double similarity = observed.calculateSimilarity(Arrays
			    .asList(observed.getAnswers()), Arrays
			    .asList(similar.getAnswers()));
		    double score = observed.getWeight(theCase) * cStrength
			    * similarity;
		    totalScore += score;
		    totalPossibleScore += observed.getWeight(theCase)
			    * cStrength;
		    cFindingList.add(getCoveredFinding(observed, similar,
			    cStrength, score, similarity, theCase));
		}
	    }
	}
	return new Object[] { cFindingList, totalScore, totalPossibleScore };
    }

    private SCDiagnosis getSCDiagnosis(Diagnosis d) {
	List<? extends KnowledgeSlice> knowledge = d.getKnowledge(
		PSMethodSetCovering.class, MethodKind.FORWARD);
	SCRelation r = (SCRelation) knowledge.get(0);
	return (SCDiagnosis) r.getSourceNode();
    }

    private int getSimpleWeight(SCDiagnosis diag, Finding f) {
	Diagnosis d = (Diagnosis) diag.getNamedObject();
	List<? extends KnowledgeSlice> coll = d
		.getKnowledge(PSMethodSetCovering.class);
	int simpleWeight = 0;
	for (KnowledgeSlice knowledgeSlice : coll) {
	    if (knowledgeSlice instanceof SCRelation) {
		if (f.getNamedObject().equals(
			((SCRelation) knowledgeSlice).getTargetNode()
				.getNamedObject())
			&& ((SCRelation) knowledgeSlice).getProbability() > 0) {
		    simpleWeight = 1;
		}
	    }
	}
	return simpleWeight;
    }

    private boolean hasIgnoredFindings(SCDiagnosis scDiag,
	    Set<PredictedFinding> explained) {
	for (PredictedFinding f : explained) {
	    double cStrength = scDiag.getCoveringStrength(f);
	    if (cStrength == 0) {
		return true;
	    }
	}
	return false;
    }

    private boolean isSimple(XPSCase theCase) {
	boolean simple = false;
	Object b = theCase.getKnowledgeBase().getProperties().getProperty(
		Property.SC_PROBLEMSOLVER_SIMPLE);
	if (b != null && b instanceof Boolean) {
	    simple = ((Boolean) b).booleanValue();
	}
	return simple;
    }

    private void renderExplainedFindings(ResponseWriter writer,
	    UIComponent component, XPSCase theCase, SCDiagnosis scDiag)
	    throws IOException {

	Object[] explainedFindings = getFindingsWithCoveringRelation(theCase,
		scDiag, 1);

	DialogRenderUtils.renderTableWithClass(writer, component, "scmTable");
	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.writeAttribute("colspan", "2", "colspan");
	// string will not be encoded (<i> is possible)
	writer
		.write(DialogUtils
			.getMessageFor("scm.explainD3.explained.title"));
	writer.endElement("th");
	writer.endElement("tr");

	List<CoveredFinding> cFindingList = (List<CoveredFinding>) explainedFindings[0];
	for (CoveredFinding cFinding : cFindingList) {
	    writer.startElement("tr", component);
	    writer.startElement("td", component);
	    writer.writeAttribute("id", "explFinding_" + cFinding.getText(),
		    "id");
	    writer.writeText(cFinding.getText() + " = "
		    + cFinding.getTextVerbalization() + " ", "value");
	    writer.startElement("i", component);
	    if (isSimple(theCase)) {
		writer.writeText("(" + format(1) + "; " + 1 + ")", "value");
	    } else {
		writer.writeText("(" + format(cFinding.getCStrength()) + "; "
			+ cFinding.getWeight() + ")", "value");
	    }

	    SimilarFinding simFinding = cFinding.getSimFinding();
	    if (simFinding != null) {
		writer.write("; ");
		writer.writeText(DialogUtils
			.getMessageFor("scm.explainD3.similarTo"), "value");
		writer.write(" ");
		writer.writeText(simFinding.getText() + " = "
			+ simFinding.getTextVerbalization(), "value");
		writer.write(" ");
		writer.writeText(DialogUtils
			.getMessageFor("scm.explainD3.similarWithValue"),
			"value");
		writer.write(" ");
		writer.writeText(format(simFinding.getSimilarity()), "value");
	    }
	    writer.endElement("i");

	    writer.endElement("td");
	    writer.startElement("td", component);
	    writer.writeAttribute("class", "score", "class");
	    writer.writeAttribute("id", "explFindingScore_"
		    + cFinding.getText(), "id");
	    if (isSimple(theCase)) {
		writer.writeText(format(1) + " / " + format(1), "value");
	    } else {
		writer.writeText(format(cFinding.getScore()) + " / "
			+ format(cFinding.getPossibleScore()), "value");
	    }
	    writer.endElement("td");
	    writer.endElement("tr");
	}
	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.write("&nbsp;");
	writer.endElement("th");
	writer.startElement("td", component);
	writer.writeAttribute("class", "score sum", "class");
	if (isSimple(theCase)) {
	    writer.writeText(format(cFindingList.size()) + " / "
		    + format(cFindingList.size()), "value");
	} else {
	    writer.writeText(format((Double) explainedFindings[1]) + " / "
		    + format((Double) explainedFindings[2]), "value");
	}
	writer.endElement("td");
	writer.endElement("tr");
	writer.endElement("table");
    }

    private void renderIgnoredFindings(ResponseWriter writer,
	    UIComponent component, XPSCase theCase, SCDiagnosis scDiag)
	    throws IOException {
	Set<PredictedFinding> explained = scDiag
		.getTransitiveExplainedFindings(theCase);
	if (explained != null && hasIgnoredFindings(scDiag, explained)) {

	    DialogRenderUtils.renderTableWithClass(writer, component,
		    "scmTable");
	    writer.startElement("tr", component);
	    writer.startElement("th", component);
	    writer.writeText(DialogUtils
		    .getMessageFor("scm.explainD3.ignored.title"), "value");
	    writer.endElement("th");
	    writer.endElement("tr");

	    Iterator<PredictedFinding> iter = explained.iterator();
	    while (iter.hasNext()) {
		PredictedFinding f = iter.next();
		double cStrength = scDiag.getCoveringStrength(f);
		// P0 means: "ignore"
		if (cStrength == 0) {
		    writer.startElement("tr", component);
		    writer.startElement("td", component);
		    writer.write(f.getNamedObject().getText() + " = "
			    + f.verbalize());
		    writer.endElement("td");
		    writer.endElement("tr");
		}
	    }
	}
    }

    private void renderUnexplainedFindings(ResponseWriter writer,
	    UIComponent component, XPSCase theCase, SCDiagnosis scDiag)
	    throws IOException {
	Object[] unexplainedFindings = getFindingsWithCoveringRelation(theCase,
		scDiag, -1);

	Set<Finding> unexplained = scDiag.getUnexplainedFindings(theCase);
	if (!unexplainedFindingsAvailable(unexplained,
		(List<CoveredFinding>) unexplainedFindings[0])) {
	    return;
	}

	DialogRenderUtils.renderTableWithClass(writer, component, "scmTable");
	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.writeAttribute("colspan", "2", "colspan");
	// string will not be encoded (<i> is possible)
	writer.write(DialogUtils
		.getMessageFor("scm.explainD3.unexplained.title"));
	writer.endElement("th");
	writer.endElement("tr");

	double totalPossibleScore = 0;
	int totalSimpleWeight = 0;
	if (unexplained != null) {
	    Iterator<Finding> iter = unexplained.iterator();
	    while (iter.hasNext()) {
		Finding f = iter.next();

		if (isSimple(theCase)) {
		    writer.startElement("tr", component);
		    writer.startElement("td", component);
		    writer.writeAttribute("id", "unexplFinding_" + f.getId(),
			    "id");
		    if (getSimpleWeight(scDiag, f) == 0) {
			writer.startElement("font", component);
			writer.writeAttribute("color", "grey", "color");
		    }

		    writer.writeText(f.getNamedObject().getText() + " = "
			    + f.verbalize() + " ", "value");
		    writer.startElement("i", component);
		    writer.writeText("(" + getSimpleWeight(scDiag, f) + ")",
			    "value");
		    writer.endElement("i");
		    if (getSimpleWeight(scDiag, f) == 0) {

			writer
				.writeText(
					" ("
						+ DialogUtils
							.getMessageFor("scm.explainD3.no_knowledge")
						+ ")", "value");

		    }
		} else {
		    writer.startElement("tr", component);
		    writer.startElement("td", component);
		    writer.writeAttribute("id", "unexplFinding_" + f.getId(),
			    "id");
		    writer.writeText(f.getNamedObject().getText() + " = "
			    + f.verbalize() + " ", "value");
		    writer.startElement("i", component);
		    writer.writeText("(" + f.getWeight(theCase) + ")", "value");
		    writer.endElement("i");
		}

		writer.endElement("td");
		writer.startElement("td", component);
		writer.writeAttribute("class", "score", "class");
		writer.writeAttribute("id", "unexplFindingScore_" + f.getId(),
			"id");
		if (isSimple(theCase)) {
		    int simpleWeight = getSimpleWeight(scDiag, f);
		    totalSimpleWeight += simpleWeight;
		    writer.writeText(format(0) + " / " + format(simpleWeight),
			    "value");
		    if (getSimpleWeight(scDiag, f) == 0) {
			writer.endElement("font");
		    }

		} else {
		    writer.writeText(format(0) + " / "
			    + format(f.getWeight(theCase)), "value");
		}
		writer.endElement("td");
		writer.endElement("tr");
		totalPossibleScore += f.getWeight(theCase);
	    }
	}
	List<CoveredFinding> cFindingList = (List<CoveredFinding>) unexplainedFindings[0];
	if (cFindingList.size() > 0) {
	    for (CoveredFinding cF : cFindingList) {
		writer.startElement("tr", component);
		writer.startElement("td", component);
		writer.writeAttribute("id", "unexplCovFinding_" + cF.getText(),
			"id");
		writer.writeText(cF.getText() + " = "
			+ cF.getTextVerbalization() + " ", "value");

		writer.startElement("i", component);
		writer.writeText(format(cF.getWeight()) + ")", "value");
		writer.endElement("i");

		writer.endElement("td");
		writer.startElement("td", component);
		writer.writeAttribute("class", "score", "class");
		writer.writeAttribute("id", "unexplCovFindingScore_"
			+ cF.getText(), "id");
		writer.writeText(format(0) + " / " + format(cF.getWeight()),
			"value");
		writer.endElement("td");
		writer.endElement("tr");
	    }
	}

	writer.startElement("tr", component);
	writer.startElement("th", component);
	writer.write("&nbsp;");
	writer.endElement("th");
	writer.startElement("td", component);
	writer.writeAttribute("class", "score sum", "class");
	writer.writeAttribute("id", "unexpl_totalsum", "id");
	if (isSimple(theCase)) {
	    writer.writeText(format(0) + " / " + format(totalSimpleWeight),
		    "value");
	} else {
	    writer.writeText(format(0)
		    + " / "
		    + format((Double) unexplainedFindings[2]
			    + totalPossibleScore), "value");
	}
	writer.endElement("td");
	writer.endElement("tr");
	writer.endElement("table");

    }

    private boolean unexplainedFindingsAvailable(
	    Set<Finding> unexplainedFindings, List<CoveredFinding> cFindingList) {
	if (unexplainedFindings != null && unexplainedFindings.size() > 0) {
	    return true;
	} else if (cFindingList != null && cFindingList.size() > 0) {
	    return true;
	}
	return false;
    }

}
