package de.d3web.dialog2.frequentness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.d3web.caserepository.CaseObject;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.CaseObjectDescriptor;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.utilities.caseLoaders.CaseRepository;

public class D3webAnswerFrequentnessImpl implements FrequentnessInterface {

    Collection<SelectItem> items;

    List<String> selectedData;

    private List<DataGroup> dataGroupWithFrequentnessData;

    private List<XPSCase> savedCases;

    private int getAbsoluteFreq(QuestionChoice q, String ansID, XPSCase theCase) {
	if (savedCases == null) {
	    setUpSavedCases(theCase);
	}
	// absolute frequency of this answer: how often is this answer set in
	// all cases...
	int absoluteFreq = 0;
	for (XPSCase oneCase : savedCases) {
	    List<Object> values = q.getValue(oneCase);
	    for (Object o : values) {
		Answer a = (Answer) o;
		if (a.getId().equals(ansID)) {
		    absoluteFreq++;
		    break;
		}
	    }
	}
	return absoluteFreq;
    }

    @Override
    public List<DataGroup> getDataGroupWithFrequentnessData() {
	dataGroupWithFrequentnessData = new ArrayList<DataGroup>();
	// add stuff!!!
	if (selectedData != null) {
	    for (String qID : selectedData) {
		XPSCase theCase = DialogUtils.getDialog().getTheCase();
		Question q = theCase.getKnowledgeBase().searchQuestions(qID);
		if (q != null && q instanceof QuestionChoice) {
		    QuestionChoice qCh = (QuestionChoice) q;
		    DataGroup group = new DataGroup(qCh.getText());
		    List<AnswerChoice> answers = qCh.getAllAlternatives();
		    for (AnswerChoice a : answers) {
			int absoluteFreq = getAbsoluteFreq(qCh, a.getId(),
				theCase);
			double relFreq = getRelFreq(absoluteFreq);
			DataWithFrequentness data = new DataWithFrequentness(a
				.getText(), absoluteFreq, relFreq);
			group.addDataWithFrequentness(data);
		    }
		    dataGroupWithFrequentnessData.add(group);
		}
	    }
	}
	return dataGroupWithFrequentnessData;
    }

    private double getRelFreq(int absoluteFreq) {
	if (absoluteFreq == 0 || savedCases == null || savedCases.size() == 0) {
	    return 0.0;
	} else {
	    return absoluteFreq / (double) savedCases.size();
	}

    }

    public Collection<SelectItem> getSelectData() {
	if (items == null) {
	    List<Question> qList = DialogUtils.getDialog().getTheCase()
		    .getKnowledgeBase().getQuestions();
	    items = new ArrayList<SelectItem>();
	    for (Question q : qList) {
		if (q instanceof QuestionChoice) {
		    SelectItem item = new SelectItem();
		    item.setLabel(q.getText());
		    item.setValue(q.getId());
		    items.add(item);
		}
	    }
	}
	return items;
    }

    public List<String> getSelectedData() {
	return selectedData;
    }

    @Override
    public boolean isDataAvailable() {
	XPSCase theCase = DialogUtils.getDialog().getTheCase();
	if (theCase == null) {
	    return false;
	}
	Collection<CaseObjectDescriptor> casesForKB = CaseManager.getInstance()
		.getCaseObjectDescriptorsForKb(
			theCase.getKnowledgeBase().getId());
	if (casesForKB.size() > 0) {
	    return true;
	}
	return false;
    }

    @Override
    public boolean isDataSelected() {
	if (selectedData != null && selectedData.size() > 0) {
	    return true;
	}
	return false;
    }

    public void setSelectedData(List<String> selectedData) {
	this.selectedData = selectedData;
    }

    private void setUpSavedCases(XPSCase theCase) {
	savedCases = new ArrayList<XPSCase>();
	Collection<CaseObjectDescriptor> codForKB = CaseManager.getInstance()
		.getCaseObjectDescriptorsForKb(
			theCase.getKnowledgeBase().getId());
	for (CaseObjectDescriptor cod : codForKB) {
	    CaseObject o = CaseRepository.getInstance().getCaseById(
		    theCase.getKnowledgeBase().getId(), cod.getCaseId());
	    XPSCase newCase = DialogUtils.createNewAnsweredCase(o, theCase
		    .getKnowledgeBase());
	    savedCases.add(newCase);
	}

    }

}
