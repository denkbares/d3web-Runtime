/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.multimedia.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * PersistenceHandler for PictureQuestion used in the {@link ImageQuestionHandler}.
 * 
 * TODO: Get the writer Component working.
 * 
 * @author Johannes Dienst
 *
 */
public class ImageQuestionPersistenceHandler implements KnowledgeReader, KnowledgeWriter{

	@SuppressWarnings("unchecked")
	@Override
	public void read(KnowledgeBase knowledgeBase, InputStream stream,
			ProgressListener listener) throws IOException {

		listener.updateProgress(0, "Starting to load picture questions");

		Document doc = Util.streamToDocument(stream);
		List<Element> childNodes = XMLUtil.getElementList(doc.getChildNodes());
		
		// Check for right DocumentStructure
		if (!(childNodes.size() == 0) && !(childNodes.size() > 1)) {		
		    if (childNodes.get(0).getNodeName().equals("Questions")) {
		    	
				List<Element> questions = XMLUtil.getElementList(childNodes
						.get(0).getChildNodes());

				// Load properties for every ImageQuestion
				for (int i = 0; i < questions.size(); i++) {

					Element questionElement = questions.get(i);
					String id = questionElement.getAttribute("ID");
					Question q = knowledgeBase.searchQuestion(id);

					List<Element> atts = XMLUtil.getElementList(questionElement
							.getChildNodes());
					String file = atts.get(0).getAttribute("file");

					List answerRegions = readAnswerRegions(atts.get(0));
					List questionInfo = (List) q.getProperties().getProperty(
							Property.IMAGE_QUESTION_INFO);
					questionInfo = new ArrayList();
					questionInfo.add(file);
					questionInfo.add(answerRegions);
					q.getProperties().setProperty(Property.IMAGE_QUESTION_INFO,
							questionInfo);
				}
		    }
		}
		listener.updateProgress(1, "Loading Picture Questions finished");
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getEstimatedSize(KnowledgeBase knowledgeBase) {
		int count = 0;
		for (Question q : knowledgeBase.getQuestions()) {
			List props = (List) q.getProperties().getProperty(Property.IMAGE_QUESTION_INFO);
			if (props != null)
				count++;
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(KnowledgeBase knowledgeBase, OutputStream stream,
			ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to save Image Questions");
		int maxvalue = getEstimatedSize(knowledgeBase);
		float aktvalue = 0;
		
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("Questions");
		List<Question> questions = knowledgeBase.getQuestions();
		
		for (Question q : questions) {
			List props = (List) q.getProperties().getProperty(Property.IMAGE_QUESTION_INFO);
			if (props != null) {
				Element question = doc.createElement("Question");
				question.setAttribute("ID", q.getId());
				Element questionImage = doc.createElement("QuestionImage");
				questionImage.setAttribute("file", (String)props.get(0));
				
				// Answer Region
				List answerRegions = (List)props.get(1);
				for (Object ar : answerRegions) {
					List<String> attributes = (List<String>) ar;
					String answerID = attributes.get(0);
					int xStart = Integer.parseInt(attributes.get(1));
					int xEnd = Integer.parseInt(attributes.get(2));
					int yStart = Integer.parseInt(attributes.get(3));
					int yEnd = Integer.parseInt(attributes.get(4));
					Element answerEl = doc.createElement("AnswerRegion");
					answerEl.setAttribute("answerID", answerID);
					answerEl.setAttribute("xStart", attributes.get(1));
					answerEl.setAttribute("xEnd", attributes.get(2));
					answerEl.setAttribute("yStart", attributes.get(3));
					answerEl.setAttribute("yEnd", attributes.get(4));
					questionImage.appendChild(answerEl);
				}
				listener.updateProgress(aktvalue++ / maxvalue, "Saving Image Question "
						+ Math.round(aktvalue) + " of " + maxvalue);
				question.appendChild(questionImage);
				root.appendChild(question);
			}
		}
		doc.appendChild(root);
		
		listener.updateProgress(1, "Image Question saved");
		Util.writeDocumentToOutputStream(doc, stream);
		
	}

	@SuppressWarnings("unchecked")
	private static List readAnswerRegions(Element element) {
		ArrayList ret = new ArrayList();
		List<Element> list = XMLUtil.getElementList(element.getChildNodes());	
		for(int i = 0; i < list.size();i++) {
			Element aR = list.get(i);
			ArrayList regionsInfo = new ArrayList();
			regionsInfo.add(aR.getAttribute("answerID"));
			regionsInfo.add(aR.getAttribute("xStart"));
			regionsInfo.add(aR.getAttribute("xEnd"));
			regionsInfo.add(aR.getAttribute("yStart"));
			regionsInfo.add(aR.getAttribute("yEnd"));			
			ret.add(regionsInfo);
		}
		return ret;
	}
}
