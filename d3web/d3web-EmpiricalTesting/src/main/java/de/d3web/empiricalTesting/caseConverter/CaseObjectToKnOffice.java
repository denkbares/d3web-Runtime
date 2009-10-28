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

package de.d3web.empiricalTesting.caseConverter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedSolution;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.SequentialTestCase;
import de.d3web.empiricalTesting.TestSuite;

/**
 * This Class converts CaseObject XML-Files to
 * a TestSuite and writes the created TestSuite
 * in KnOffice-Format to a txt-File.
 * @author Sebastian Furth
 *
 */
public class CaseObjectToKnOffice extends CaseObjectConverter {
	
	// quote the verbalized text, if at least on character of 
	// these is contained in the verbalization text
	private final String BAD_CHARS= "()=,:;?";
	// the default indent for each line within a case
	private final String INDENT = "   ";
	
	public static void main(String[] args) throws IOException {
		String workspace  = "d:/eigene projekte/temp/";
		String knowledgePath = "pflanzen.jar";
		String caseBase   = "pflanzen_cases_small.xml";
		String converted = "pflanzen_cases_small_converted.txt";
		
		CaseObjectConverter converter = new CaseObjectToKnOffice();
		TestSuite t = converter.convert(workspace+knowledgePath, workspace+caseBase);
				
		converter.write(t, workspace+converted);
		
		System.out.println("Case successfully converted to KnOffice-format!");
	}

	/**
	 * Writes a TestSuite to the specified file 
	 * using the TestSuite-KnOffice-Format
	 * @param t The TestSuite which shall be written to a file
	 * @param the path of the output file
	 */
	@Override
	public void write(TestSuite t, String filepath){
		write(t.getRepository(), filepath);
	}
	
	@Override
	public void write(List<SequentialTestCase> cases, String filepath) {
		try {
			Writer out =new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filepath), "UTF8"));
			convertToKnOfficeFormat(cases, out); 
			out.close();
		} catch (IOException e) {
			System.err.println("Error while converting to KnOffice-format!");
		}
	}

	
	public static void writeDataTo(String data, String filename) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filename), "UTF8"));
		out.write(data);
		out.close();
	}

	private void convertToKnOfficeFormat(List<SequentialTestCase> cases, Writer out) throws IOException {
		for (SequentialTestCase stc : cases) {
			out.write(stc.getName() + " {");
			for (RatedTestCase rtc : stc.getCases()) {
				convertRTCToKnOfficeFormat(rtc, out);
			}
			out.write("\n}\n\n");
		}		
	}

	private void convertRTCToKnOfficeFormat(RatedTestCase rtc, Writer out) throws IOException {
		convertFindingsToKnOfficeFormat(rtc, out);
		out.write(": ");
		convertExpectedSolutionsToKnOfficeFormat(rtc, out);
		out.write(";");
	}
	
	private void convertFindingsToKnOfficeFormat (RatedTestCase rtc, Writer out) throws IOException {
		for (Finding f : rtc.getFindings()) {
			out.write("\n" + INDENT);
			out.write(quoteTextIfRequired(f.getQuestion().getText()));
			out.write(" = ");
			out.write(quoteTextIfRequired(f.getAnswer().toString()));
			if (rtc.getFindings().indexOf(f) < rtc.getFindings().size() - 1) {
				out.write(",");
			}
		}
	}
	
	private void convertExpectedSolutionsToKnOfficeFormat (RatedTestCase rtc, Writer out) throws IOException {
		for (RatedSolution s : rtc.getExpectedSolutions()) {
			out.write("\n" + INDENT + INDENT + quoteTextIfRequired(s.getSolution().getText()));
			out.write(" (" + quoteTextIfRequired(s.getRating().toString()) + ")");
			if (rtc.getExpectedSolutions().indexOf(s) < rtc.getExpectedSolutions().size() - 1) {
				out.write(",");
			}
		}
	}
		
	private String quoteTextIfRequired(String text) {
		if (needsQuotation(text))
			return "\"" + text + "\"";
		else
			return text;
	}

	private boolean needsQuotation(String text) {
		for (int i = 0; i < BAD_CHARS.length(); i++) {
			if (text.indexOf(BAD_CHARS.charAt(i)) > -1 )
				return true;
		}
		return false;
	}



	
}
