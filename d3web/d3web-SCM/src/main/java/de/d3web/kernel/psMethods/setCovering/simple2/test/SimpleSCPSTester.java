package de.d3web.kernel.psMethods.setCovering.simple2.test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.CaseFactory;
import de.d3web.kernel.domainModel.D3WebCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.xml.PersistenceManager;

public class SimpleSCPSTester {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String kbString = "c:/testkb.jar";
		PersistenceManager mgr = PersistenceManager.getInstance();
		mgr.addPersistenceHandler(new de.d3web.kernel.psMethods.setCovering.persistence.SCMPersistenceHandler());
		KnowledgeBase kb = null;
		try {
			kb = mgr.load(new File(kbString).toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 D3WebCase theCase = (D3WebCase)CaseFactory.createXPSCase(kb);
		PSMethod sc = de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering.getInstance();
		sc.init(theCase);
		theCase.addUsedPSMethod(sc);
		kb.getProperties().setProperty(Property.SC_PROBLEMSOLVER_SIMPLE, Boolean.TRUE);
		
		for(Question q: kb.getQuestions())  {
			if(q instanceof QuestionNum) {
				de.d3web.kernel.domainModel.answers.AnswerNum aNum = new de.d3web.kernel.domainModel.answers.AnswerNum();
				aNum.setValue(new Double(2));
				((QuestionNum)q).setValue(theCase, new Object[]{aNum});
				sc.propagate(theCase, q, new Object[]{aNum});
			}
		}
		
	
		ArrayList<PSMethod> list = new ArrayList<PSMethod>();
		list.add(sc);
		 List<Diagnosis> solutionsEst = theCase.getDiagnoses(DiagnosisState.ESTABLISHED,list);
		 List<Diagnosis> solutionsUnclear = theCase.getDiagnoses(DiagnosisState.UNCLEAR,list);
		 List<Diagnosis> solutionsEx = theCase.getDiagnoses(DiagnosisState.EXCLUDED,list);

	}

}
