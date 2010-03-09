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

package de.d3web.dialog2.render;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.XPSCase;
import de.d3web.dialog2.basics.layout.QContainerLayout;
import de.d3web.dialog2.basics.layout.QuestionPageLayout;

public class QContainerRendererForUndefinedLayout extends QContainerRenderer {

	public QContainerRendererForUndefinedLayout(ResponseWriter writer, UIComponent component, XPSCase theCase, List<Question> qList, QuestionPageLayout layoutDef) {
		super(writer, component, theCase, qList, layoutDef);
	}
	
	@Override
	public void renderQuestions() throws IOException {
		// list with all valid questions
		List<Question> validQuestions = getValidQuestionsFromQList();
		
		int cols;
		if (layoutDef instanceof QContainerLayout) {
			cols = ((QContainerLayout) layoutDef).getCols();
		} else {
			cols = layoutDef.getQuestionColumns();
		}
		
		for (int i = 0; i < validQuestions.size(); i++) {
			Question q = validQuestions.get(i);
			int colsMod = i % cols;
			int diff = 0;
			// start new row if i % cols = 0
			if (colsMod == 0) {
				writer.startElement("tr", component);
			}

			if (i == validQuestions.size() - 1 && colsMod != (cols - 1)) {
				diff = cols - 1 - colsMod;
			}

			QuestionPageLayout layoutForQuestion = layoutDef;
			if (layoutDef instanceof QContainerLayout
					&& ((QContainerLayout) layoutDef)
							.getQuestionLayoutForQuestionID(q.getId()) != null) {
				layoutForQuestion = ((QContainerLayout) layoutDef)
						.getQuestionLayoutForQuestionID(q.getId());
			}

			QuestionsRendererUtils.renderQuestion(writer, component, theCase, q, layoutForQuestion,
					cols, diff);

			if (i % cols == (cols - 1) || i == validQuestions.size() - 1) {
				writer.endElement("tr");
			}
		}
	}
}
