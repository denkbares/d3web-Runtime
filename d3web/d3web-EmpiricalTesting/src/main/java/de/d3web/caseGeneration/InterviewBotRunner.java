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

package de.d3web.caseGeneration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.kpers.PersistenceManager;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.caseConverter.CaseObjectToKnOffice;
import de.d3web.empiricalTesting.caseConverter.CaseObjectToTestSuiteXML;

public class InterviewBotRunner {
	static String workspace = "D:/Projekte/Temp/EmpiricalTesting/";
	static String filename = "";
	static Stopwatch watch = new Stopwatch();

	public static void main(String[] args) {
		try {
			demoForCarDiagnosis();
//			demoForGenerated1K_KB();
//			demoForDigitalysKB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unused")
	private static void demoForDigitalysKB() 
	    throws Exception {
		filename = "dano.jar";
		KnowledgeBase k = loadKnowledgebase(filename);
		
		watch.start();
		InterviewBot bot = new InterviewBot.Builder(k).
			maxCases(5).
			ratingStrategy(new HeuristicScoreRatingStrategy()).
			build();
		List<SequentialTestCase> cases = bot.generate();
		watch.stop();
		
		System.out.println(watch);
		writeCasesXML(filename, cases);
	}
	

	@SuppressWarnings("unused")
	private static void demoForGenerated1K_KB() 
	    throws Exception {
		filename  = "testKnowledgebase1KRules.jar";
		KnowledgeBase k = loadKnowledgebase(filename);
		
		watch.start();
		InterviewBot bot = new InterviewBot.Builder(k).build();
		List<SequentialTestCase> cases = bot.generate();
		watch.stop();

		System.out.println(watch);
		
		writeCasesXML(filename, cases);
	}


	@SuppressWarnings("unused")
	private static void demoForCarDiagnosis() throws Exception {
		filename  = "minicar.jar";

		KnowledgeBase k = loadKnowledgebase(filename);
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance(k);
		
		watch.start();
		InterviewBot bot = new InterviewBot.Builder(k).
			forbiddenAnswerCombination(FindingMC.createFindingMC(k, "Driving", new String[] { "insufficient power on full load" , "unsteady idle speed" })).
			forbiddenAnswerCombination(FindingMC.createFindingMC(k, "Driving", new String[] { "insufficient power on partial load" })).
			forbiddenAnswerCombination(FindingMC.createFindingMC(k, "Driving", new String[] { "unsteady idle speed" })).
//			maxAnswerCombinations(2).
//			maxAnswerCombinations(kbm.findQuestion("Driving"), 2).
			build();
		List<SequentialTestCase> cases = bot.generate();
		watch.stop();

		System.out.println(watch);

		writeCasesXML(filename, cases);
	}
	
	
	private static KnowledgeBase loadKnowledgebase(String filename) throws IOException {
		KnowledgeBase k = PersistenceManager.getInstance().load(new File(workspace+filename));
		return k;
	}


	private static void writeCasesXML(String filename, List<SequentialTestCase> cases) {
		CaseObjectToTestSuiteXML conv = new CaseObjectToTestSuiteXML();
		long casesK = cases.size() / 1000;
		conv.write(cases, workspace+filename+"_cases_"+casesK+".xml");
	}
	
	
	@SuppressWarnings("unused")
	private static void writeCasesTXT(String filename, List<SequentialTestCase> cases) {
		CaseObjectToKnOffice conv = new CaseObjectToKnOffice();
		long casesK = cases.size() / 1000;
		conv.write(cases, workspace+filename+"_cases_"+casesK+".txt");
	}
}
