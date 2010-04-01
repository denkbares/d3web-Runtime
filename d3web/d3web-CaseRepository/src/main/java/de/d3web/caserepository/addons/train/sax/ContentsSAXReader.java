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

/*
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;

import de.d3web.caserepository.addons.train.findings.CaseParagraph;
import de.d3web.caserepository.addons.train.findings.DiagnosisRelation;
import de.d3web.caserepository.addons.train.findings.Finding;
import de.d3web.caserepository.addons.train.findings.FindingsContents;
import de.d3web.caserepository.addons.train.findings.Rating;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QASet;

/**
 * 22.09.2003 18:08:00
 * 
 * @author chris
 */

public class ContentsSAXReader extends AbstractTagReader {

	private Boolean version = null; // FALSE=old style, TRUE=new style

	private FindingsContents c = null;

	private QASet q = null;

	private CaseParagraph caseParagraph = null;

	private Finding f = null;

	private StringBuffer oldStyleContent = null;

	private StringBuffer findingContent = null;

	private StringBuffer dummyFindingContent = null;

	private StringBuffer caseParagraphContent = null;

	protected ContentsSAXReader(String id) {
		super(id);
	}

	private static ContentsSAXReader instance;

	private ContentsSAXReader() {
		this("FindingsContentsSAXReader");
	}

	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new ContentsSAXReader();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] { "Contents", "Content", "Paragraph",
				"Finding", "DummyFinding", "FindingDiagnosisRelation" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		//		System.out.println("start reading " + qName);

		if ("Contents".equals(qName))
			startContents();
		else if ("Content".equals(qName))
			startContent(attributes);
		else if ("Paragraph".equals(qName))
			startParagraph();
		else if ("Finding".equals(qName))
			startFinding();
		else if ("DummyFinding".equals(qName))
			startDummyFinding();
		else if ("FindingDiagnosisRelation".equals(qName))
			startFindingDiagnosisRelation(attributes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		//		System.out.println(" end reading " + qName);

		if ("Contents".equals(qName))
			endContents();
		else if ("Content".equals(qName))
			endContent();
		else if ("Paragraph".equals(qName))
			endParagraph();
		else if ("Finding".equals(qName))
			endFinding();
		else if ("DummyFinding".equals(qName))
			endDummyFinding();
		else if ("FindingDiagnosisRelation".equals(qName))
			endFindingDiagnosisRelation();
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.sax.AbstractTagReader#characters(char[],
	 *      int, int)
	 */
	public void characters(char[] chars, int start, int length) {
		if (findingContent != null) {
			// i'm inside a finding content tag
			// System.out.println("reading finding content " +
			// String.copyValueOf(chars, start, length));
			findingContent.append(chars, start, length);
		} else if (dummyFindingContent != null) {
			// i'm inside a dummy finding tag
			dummyFindingContent.append(chars, start, length);
		} else if (caseParagraphContent != null) {
			// i'm inside a caseParagraph
			// System.out.println("reading case paragraph content " +
			// String.copyValueOf(chars, start, length));
			caseParagraphContent.append(chars, start, length);
		} else if (q != null && caseParagraph == null) {
			// i'm inside a contents>content tag and therefore this is an old
			// styled case file
			if (oldStyleContent == null) {
				oldStyleContent = new StringBuffer();
			}
			// System.out.println("reading content-content " +
			// String.copyValueOf(chars, start, length));
			oldStyleContent.append(chars, start, length);
		} else {
			// ignore
		}
	}

	private void startContent(Attributes attributes) {
		if (f != null) {
			// this is part of a finding
			startFindingContent();
		} else {
			// this is subtag to Contents
			startContentsContent(attributes);
		}
	}

	private void endContent() {
		if (f != null) {
			endFindingContent();
		} else {
			endContentsContent();
		}
	}

	/* From here to end you find methods for each node type */

	private void startContents() {
		c = new FindingsContents();
		version = null; // I cannot tell wether this is new version or not
	}

	/**
	 * @param attributes
	 */
	private void startContentsContent(Attributes attributes) {
		String id = attributes.getValue("id");
		q = getKnowledgeBase().searchQASet(id);
	}

	private void startParagraph() {
		version = Boolean.TRUE;
		caseParagraph = new CaseParagraph();
		caseParagraphContent = new StringBuffer();
	}

	/**
	 *  
	 */
	private void flushParagraphContent() {
		String text = caseParagraphContent.toString();
		// System.out.println("storing content to paragraph: " + text);
		if (text.length() > 0) {
			caseParagraph.addContent(text);
		}
		caseParagraphContent = new StringBuffer();
	}

	private void startFinding() {
		flushParagraphContent();
		f = new Finding();
	}

	private void startFindingContent() {
		findingContent = new StringBuffer();
	}

	private void startDummyFinding() {
		flushParagraphContent();
		dummyFindingContent = new StringBuffer();
	}

	private void endDummyFinding() {
		caseParagraph.addContent(new CaseParagraph.DummyFinding(
				dummyFindingContent.toString()));
		dummyFindingContent = null;
	}

	/**
	 *  
	 */
	private void endFindingContent() {
		f.setTextualContent(findingContent.toString());
		findingContent = null;
	}

	private void startFindingDiagnosisRelation(Attributes attributes) {
		Solution diag = getKnowledgeBase().searchDiagnosis(
				attributes.getValue("id"));
		if (diag != null) {
			Rating score = Rating.getRating(attributes.getValue("rating"));
			DiagnosisRelation fdr = new DiagnosisRelation();
			fdr.setDiagnosis(diag);
			fdr.setScore(score);
			f.addKnowledgeSlice(fdr);
		}

	}

	private void endFindingDiagnosisRelation() {
		// nothing to be done
	}

	private void endFinding() {
		caseParagraph.addContent(f);
		f = null;
	}

	private void endParagraph() {
		flushParagraphContent();
		caseParagraphContent = null;
		c.addCaseParagraph(q, caseParagraph);
		caseParagraph = null;
	}

	/**
	 *  
	 */
	private void endContentsContent() {
		// im toplevel to Contents-Tag
		if (version != null && version.booleanValue()) {
			// wir haben hier definitiv einen Fall in neuer "Schreibweise"
			// und machen also nichts eigenes, da ja nur q zurückgesetzt
			// werden
			// muss
		} else {
			// und hier haben wir einen Fall in alter Schreibweise
			CaseParagraph theCaseParagraph = new CaseParagraph();
			theCaseParagraph.addContent(oldStyleContent.toString());
			c.addCaseParagraph(q, theCaseParagraph);
			oldStyleContent = null;
		}
		q = null;
	}

	private void endContents() {
		getCaseObject().setContents(c);
		c = null;
	}

}