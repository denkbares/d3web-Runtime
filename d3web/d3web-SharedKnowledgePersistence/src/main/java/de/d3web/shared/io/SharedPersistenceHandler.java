/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *                    denkbares GmbH
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

package de.d3web.shared.io;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.KnowledgeSliceComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.shared.PSMethodShared;
import de.d3web.shared.QuestionWeightValue;
import de.d3web.shared.Weight;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.mc.QuestionComparatorMCIndividual;
import de.d3web.shared.comparators.num.QuestionComparatorNumDivision;
import de.d3web.shared.comparators.oc.QuestionComparatorOCIndividual;
import de.d3web.shared.comparators.oc.QuestionComparatorYN;
import de.d3web.shared.comparators.text.QuestionComparatorTextIndividual;
/**
 * Loads and saves shared knowledge from/to XML
 * Creation date: (14.08.2001 14:04:56)
 * @author: Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class SharedPersistenceHandler implements KnowledgeReader, KnowledgeWriter {
	

	public final static String SHARED_PERSISTENCE_HANDLER = "shared";

	/**
	 * SharedPersistenceHandler constructor comment.
	 */
	public SharedPersistenceHandler() {
		super();
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Loading shared knowlege");
		Document doc = Util.streamToDocument(stream);
		// [TODO]:aha:check for "does this file actually match the
		// knowledgebase"!
		addKnowledgeSlices(kb, doc, listener);
		listener.updateProgress(1, "Loading shared knowlege");
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream,
			ProgressListener listener) throws IOException {
		listener.updateProgress(0, "Saving shared knowlege");
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		doc.appendChild(root);
		root.setAttribute("type", SharedPersistenceHandler.SHARED_PERSISTENCE_HANDLER);
		root.setAttribute("system", "d3web");
		
		Collection<KnowledgeSlice> kslices = kb.getAllKnowledgeSlicesFor(PSMethodShared.class);
		
		Element ksElement = doc.createElement("KnowledgeSlices");
		root.appendChild(ksElement);
		float time = 0f;
		int abs = kslices.size();
		List<KnowledgeSlice> ksList = new ArrayList<KnowledgeSlice>(kslices);
		Collections.sort(ksList, new KnowledgeSliceComparator());
		for (KnowledgeSlice ks: ksList) {
			listener.updateProgress(time++/abs, "Saving shared knowlege");
			ksElement.appendChild(PersistenceManager.getInstance().writeFragment(ks, doc));
		}
		Util.writeDocumentToOutputStream(doc, stream);
		listener.updateProgress(1, "Saving shared knowlege");
	}



	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		return kb.getAllKnowledgeSlicesFor(PSMethodShared.class).size();
	}
	
	public static Answer getAnswer(Session theCase, Question q, String idOrValue) {

		if (idOrValue.equals("MaU")) {
			return new AnswerUnknown();
		}

		if (q instanceof QuestionChoice) {
			return ((QuestionChoice) q).getAnswer(theCase, idOrValue);
		}

		if (q instanceof QuestionText) {
			return ((QuestionText) q).getAnswer(theCase, idOrValue);
		}

		if (q instanceof QuestionNum) {
			return ((QuestionNum) q).getAnswer(theCase, new Double(idOrValue));
		}

		return null;
	}

	/**
	 * Insert the method's description here. Creation date: (25.02.2002
	 * 14:48:32)
	 * 
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public static QuestionComparator addDefaultKnowledge(Question q) {

		QuestionComparator qc = null;

		if (q instanceof QuestionYN) {
			qc = new QuestionComparatorYN();
		} else if (q instanceof QuestionOC) {
			qc = new QuestionComparatorOCIndividual();
		} else if (q instanceof QuestionMC) {
			qc = new QuestionComparatorMCIndividual();
		} else if (q instanceof QuestionNum) {
			qc = new QuestionComparatorNumDivision();
		} else if (q instanceof QuestionText) {
			qc = new QuestionComparatorTextIndividual();
		}

		if (qc != null) {
			qc.setQuestion(q);
		}
		return qc;
	}

	/**
	 * Insert the method's description here. Creation date: (25.02.2002
	 * 14:48:32)
	 * 
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public static Weight addDefaultWeight(Question q) {

		Weight w = new Weight();

		QuestionWeightValue qww = new QuestionWeightValue();
		qww.setQuestion(q);
		qww.setValue(Weight.G4);

		w.setQuestionWeightValue(qww);

		return w;
	}

	private static void addKnowledgeSlices(KnowledgeBase kb, Document doc, ProgressListener listener) throws IOException {

		// Anzahl Slices ermitteln
		int slicecount = 0;
		int aktslicecount = 0;

		List<Element> nl = XMLUtil.getElementList(doc.getElementsByTagName("KnowledgeSlice"));
		//globalUnknownSimilaritys have an own nodename
		//it is not sufficient to parse all children of KnowledgeSlices, because in former Versions of the Persistence
		//the globalUnknownSimilarity was stored as child of the KnowledgeBase
		nl.addAll(XMLUtil.getElementList(doc.getElementsByTagName("globalUnknownSimilarity")));
		slicecount = nl.size();
		for (Element n: nl) {
			PersistenceManager.getInstance().readFragment(n, kb);
			listener.updateProgress(((float)aktslicecount)/slicecount, "Loading shared knowlege: knowledge slice "
					+ aktslicecount++
					+ " of "
					+ slicecount);
		}
	}
}