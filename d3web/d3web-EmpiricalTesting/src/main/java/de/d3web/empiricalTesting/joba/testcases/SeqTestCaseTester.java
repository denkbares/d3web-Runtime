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

package de.d3web.empiricalTesting.joba.testcases;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.ScoreRating;

public class SeqTestCaseTester {
	KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
	Question q1, q2, q3;
	Solution d1, d2, d3;

	public static void main(String[] args) throws Exception {
		new SeqTestCaseTester().run();
		System.out.println("Idle.");
	}

	private void run() throws Exception {
		createKnowledgeBase();

		RatedTestCase rtc1 = new RatedTestCase();
		rtc1.add(new Finding((QuestionChoice) q1, "qoc1A1"));
		rtc1.add(new Finding((QuestionChoice) q2, "qoc2A1"));
		rtc1.update(d2, new ScoreRating(10));
		rtc1.update(d1, new ScoreRating(20));

		RatedTestCase rtc2 = new RatedTestCase();
		rtc2.add(new Finding((QuestionChoice) q1, "qoc1A1"));
		rtc2.add(new Finding((QuestionChoice) q2, "qoc2A1"));
		rtc2.update(d2, new ScoreRating(10));
		rtc2.update(d1, new ScoreRating(20));

		// RatedTestCase rtc2 = new RatedTestCase();
		// rtc2.add(new Finding((QuestionNum)q3, 2.0));
		// rtc2.update(d2, new ScoreRating(100));
		//		
		// SequentialTestCase stc = new SequentialTestCase();
		// stc.setName("STC01");
		// stc.add(rtc);
		// stc.add(rtc2);
		// stc.inverseSortSolutions();
		//		
		//		
		// List<SequentialTestCase> stcrepository = new
		// ArrayList<SequentialTestCase>();
		// stcrepository.add(stc);
		//		
		// SequentialTestCasePersistence p =
		// SequentialTestCasePersistence.getInstance();
		// //Writer swriter = new StringWriter();
		// Writer swriter = new FileWriter(new File("/jobatrash/deleteme.xml"));
		// p.saveTo(stcrepository, swriter);
		// // System.out.println(swriter);

	}

	private void createKnowledgeBase() {
		Solution root = kbm.getKnowledgeBase().getRootDiagnosis();
		QASet rootContainer = kbm.getKnowledgeBase().getRootQASet();
		d1 = kbm.createSolution("d1", root);
		d2 = kbm.createSolution("d2", root);
		d3 = kbm.createSolution("d3", root);
		QContainer qc1 = kbm.createQContainer("qc1", rootContainer);
		q1 = kbm.createQuestionOC("qoc1", qc1, new String[] { "qoc1A1",
				"qocq1A2" });
		q2 = kbm.createQuestionOC("qoc2", qc1, new String[] { "qoc2A1",
				"qoc2qA2" });
		q3 = kbm.createQuestionNum("qnum1", qc1);
	}

}
