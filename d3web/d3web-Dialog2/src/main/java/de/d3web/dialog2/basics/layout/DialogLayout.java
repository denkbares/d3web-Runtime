package de.d3web.dialog2.basics.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.qasets.Question;

public class DialogLayout {

    public static String LAYOUTFILE_STRING = "dialoglayout.xml";

    private List<QContainerLayout> qContainerList;

    private QuestionPageLayout questionPageLayout;

    public static Logger logger = Logger.getLogger(DialogLayout.class);

    public QContainerLayout getQContainerLayoutDefinitionForID(String id) {
	for (QContainerLayout qCont : qContainerList) {
	    if (qCont.getQContID().equals(id)) {
		return qCont;
	    }
	}
	return null;
    }

    public List<QContainerLayout> getQContainerList() {
	return qContainerList;
    }

    public QuestionPageLayout getQuestionPageLayout() {
	return questionPageLayout;
    }


    public boolean hasDefinitonsForQContainerID(String id) {
	if (qContainerList.size() == 0) {
	    return false;
	}
	for (QContainerLayout qCont : qContainerList) {
	    if (qCont.getQContID().equals(id)) {
		return true;
	    }
	}
	return false;
    }

    public void init(String kbid) {
	qContainerList = new ArrayList<QContainerLayout>();
	questionPageLayout = new QuestionPageLayout();
	new DialogLayoutLoader(this).init(kbid);
    }

    public void setQContainerList(List<QContainerLayout> containerList) {
	qContainerList = containerList;
    }

    public void setQuestionPageLayout(QuestionPageLayout questionPageLayout) {
	this.questionPageLayout = questionPageLayout;
    }

}
