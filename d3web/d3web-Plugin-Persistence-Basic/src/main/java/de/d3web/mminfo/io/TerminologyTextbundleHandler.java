/*
 * Copyright (C) 2011 denkbares GmbH
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

package de.d3web.mminfo.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;

/**
 * This class generates a text-based property file containing all terminology
 * objects of a knowledge base that are relevant for
 * translation/internationalization. Answer choices are exported as a index
 * starting with 0.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 10.01.2012
 */
public class TerminologyTextbundleHandler implements KnowledgeWriter {

	public final static String TEXTBUNDLE_PERSISTENCE_HANDLER = "textbundle";

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Starting to save textbundle");
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		generatePropertyFile(writer, kb);
		writer.flush();
		listener.updateProgress(1, "Textbundle saved");
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return kb.getAllIDObjects().size() + 1;
	}

	/**
	 * Inspects all terminology objects of the given {@link KnowledgeBase}
	 * instance and prints the relevant data for translation to the specified
	 * {@link Writer} instance.
	 * 
	 * @created 10.01.2011
	 * @param output the specified {@link Writer} instance
	 * @param knowledge the specified {@link KnowledgeBase} instance
	 * @throws IOException
	 */
	private void generatePropertyFile(Writer writer, KnowledgeBase knowledge) throws IOException {
		for (Question question : knowledge.getQuestions()) {
			print(writer, question);
		}
		for (Solution solution : knowledge.getSolutions()) {
			append(writer, solution.getId(), ".title=" + solution.getName());
		}
	}

	private void print(Writer writer, Question question) throws IOException {
		final String id = question.getId(); // oid.genID();
		append(writer, id, ".title=" + question.getName());
		if (question instanceof QuestionChoice) {
			List<Choice> choices = ((QuestionChoice) question).getAllAlternatives();
			for (int i = 0; i < choices.size(); i++) {
				append(writer, id, "." + i + ".text=" + choices.get(i));
			}
		}
		else if (question instanceof QuestionNum) {
			String unit = question.getInfoStore().getValue(MMInfo.UNIT);
			if (unit != null) {
				append(writer, id, ".unit=" + unit);
			}
		}

		// some extensions to terminology objects
		String link = question.getInfoStore().getValue(MMInfo.LINK);
		if (link != null) {
			append(writer, id, ".link=" + link);
		}
		String description = question.getInfoStore().getValue(MMInfo.DESCRIPTION);
		if (description != null) {
			// TODO: take care of multi-line descriptions here! It will break
			// the property file format!
			append(writer, id, ".description=" + description);
		}
	}

	private void append(Writer writer, final String id, String string) throws IOException {
		writer.append(id + string + "\n");
	}
}