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
import java.net.MalformedURLException;
import java.util.List;

import javax.activation.UnsupportedDataTypeException;

import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.caseConverter.CaseObjectToKnOffice;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.xml.PersistenceManager;

public class InterviewBotRunner {
	static String workspace = "/jobatrash/";
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
	    throws UnsupportedDataTypeException, MalformedURLException {
		filename = "digitalys_final.jar";
		KnowledgeBase k = loadKnowledgebase(filename);
		
		watch.start();
		InterviewBot bot = new InterviewBot.Builder(k).
			ratingStrategy(new HeuristicScoreRatingStrategy()).
			build();
		List<SequentialTestCase> cases = bot.generate();
		watch.stop();
		
		System.out.println(watch);
		writeCases(filename, cases);
	}
	

	@SuppressWarnings("unused")
	private static void demoForGenerated1K_KB() 
	    throws MalformedURLException, UnsupportedDataTypeException {
		filename  = "testKnowledgebase1KRules.jar";
		KnowledgeBase k = loadKnowledgebase(filename);
		
		watch.start();
		InterviewBot bot = new InterviewBot.Builder(k).build();
		List<SequentialTestCase> cases = bot.generate();
		watch.stop();

		System.out.println(watch);
		
		writeCases(filename, cases);
	}


	@SuppressWarnings("unused")
	private static void demoForCarDiagnosis() throws Exception {
		filename  = "carDiagnosis.jar";

		KnowledgeBase k = loadKnowledgebase(filename);

		watch.start();
		InterviewBot bot = new InterviewBot.Builder(k).
//			knownAnswers(Finding.createFinding(k, "Driving", "delayed take-off")).
//			knownAnswers(Finding.createFinding(k, "Exhaust fumes", "black")).
//			forbiddenAnswer(Finding.createFinding(k, "Engine noises", "knocking")).
			build();
		List<SequentialTestCase> cases = bot.generate();
		watch.stop();

		System.out.println(watch);
		
		writeCases(filename, cases);
	}
	
	

	private static KnowledgeBase loadKnowledgebase(String filename) throws MalformedURLException {
		KnowledgeBase k = PersistenceManager.getInstance().load(new File(workspace+filename).toURI().toURL());
		return k;
	}

	private static void writeCases(String filename, List<SequentialTestCase> cases) {
		CaseObjectToKnOffice conv = new CaseObjectToKnOffice();
		long casesK = cases.size() / 1000;
		conv.write(cases, workspace+filename+"_cases_"+casesK+".txt");
	}	
}
