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

package de.d3web.shared.tests;

import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.shared.Abnormality;
import de.d3web.shared.LocalWeight;
import de.d3web.shared.PSMethodShared;
import de.d3web.shared.QuestionWeightValue;
import de.d3web.shared.Weight;
import de.d3web.shared.comparators.KnowledgeBaseUnknownSimilarity;
import de.d3web.shared.comparators.oc.QuestionComparatorYN;

public class RemoveKnowledgeSliceTest extends TestCase {

	private KnowledgeBase base;

	private QContainer qc;

	private QuestionYN qyn1;

	private QuestionYN qyn2;

	private Solution d1;

	private Solution d2;

	public static Test suite() {
		return new TestSuite(RemoveKnowledgeSliceTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(RemoveKnowledgeSliceTest.suite());
	}

	@Override
	protected void setUp() {
		base = new KnowledgeBase();

		qc = new QContainer("qc");
		qc.setName("qc");
		qc.setKnowledgeBase(base);

		qyn1 = new QuestionYN("qyn1");
		qyn1.setName("qyn1");
		qyn1.setKnowledgeBase(base);
		qc.addChild(qyn1);
		// qyn1.setParents(Arrays.asList(new NamedObject[] { qc }));

		qyn2 = new QuestionYN("qyn2");
		qyn2.setName("qyn2");
		qyn2.setKnowledgeBase(base);
		qc.addChild(qyn2);
		// qyn2.setParents(Arrays.asList(new NamedObject[] { qc }));

		d1 = new Solution("d1");
		d1.setName("d1");
		d1.setKnowledgeBase(base);

		d2 = new Solution("d2");
		d2.setName("d2");
		d2.setKnowledgeBase(base);

				Abnormality sl1 = new Abnormality();
		sl1.setQuestion(qyn1);
		qyn1.addKnowledge(PSMethodShared.class, sl1,
				PSMethodShared.SHARED_ABNORMALITY);
		Abnormality sl2 = new Abnormality();
		sl2.setQuestion(qyn2);
		qyn2.addKnowledge(PSMethodShared.class, sl2,
				PSMethodShared.SHARED_ABNORMALITY);

		KnowledgeBaseUnknownSimilarity sl3 = new KnowledgeBaseUnknownSimilarity();
		sl3.setKnowledgeBase(base);
		base.addKnowledge(PSMethodShared.class, sl3,
				PSMethodShared.SHARED_SIMILARITY);
		KnowledgeBaseUnknownSimilarity sl4 = new KnowledgeBaseUnknownSimilarity();
		sl4.setKnowledgeBase(base);
		base.addKnowledge(PSMethodShared.class, sl4,
				PSMethodShared.SHARED_SIMILARITY);

		LocalWeight sl5 = new LocalWeight();
		sl5.setQuestion(qyn1);
		qyn1.addKnowledge(PSMethodShared.class, sl5,
				PSMethodShared.SHARED_LOCAL_WEIGHT);
		LocalWeight sl6 = new LocalWeight();
		sl6.setQuestion(qyn2);
		qyn2.addKnowledge(PSMethodShared.class, sl6,
				PSMethodShared.SHARED_LOCAL_WEIGHT);
		
		QuestionComparatorYN sl7 = new QuestionComparatorYN();
		sl7.setQuestion(qyn1);
		qyn1.addKnowledge(PSMethodShared.class, sl7,
				PSMethodShared.SHARED_SIMILARITY);
		QuestionComparatorYN sl8 = new QuestionComparatorYN();
		sl8.setQuestion(qyn2);
		qyn2.addKnowledge(PSMethodShared.class, sl8,
				PSMethodShared.SHARED_SIMILARITY);
		
		Weight sl9 = new Weight();
		QuestionWeightValue v1 = new QuestionWeightValue();
		v1.setQuestion(qyn1);
		sl9.setQuestionWeightValue(v1);
		qyn1.addKnowledge(PSMethodShared.class, sl9,
				PSMethodShared.SHARED_WEIGHT);
		Weight sl10 = new Weight();
		QuestionWeightValue v2 = new QuestionWeightValue();
		v2.setQuestion(qyn2);
		sl10.setQuestionWeightValue(v2);
		qyn2.addKnowledge(PSMethodShared.class, sl10,
				PSMethodShared.SHARED_WEIGHT);
	}

	public void testRemoveAbnormality() {
		List<?> list;
		Abnormality slice;
		list = (List<?>) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_ABNORMALITY);
		slice = (Abnormality) list.get(0);
		NamedObject no = slice.getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
	
		assertFalse("Deleted slice still mapped in '" + no.getName()
				+ "', expected false : ", (no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_ABNORMALITY))==slice);

		List<?> all = (List<?>) qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		}
		catch (IllegalAccessException e) {
			fail(qyn2.getName()+" should have had no children!");
		}
		Collection<KnowledgeSlice> slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}

	}

	public void testRemoveKnowledgeBaseUnknownSimilarity() {
		List<?> list;
		KnowledgeBaseUnknownSimilarity slice;
		list = (List<?>) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_SIMILARITY);
		slice = (KnowledgeBaseUnknownSimilarity) list.get(0);
		KnowledgeBase no = slice.getKnowledgeBase();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no
				+ "', expected false : ", ((List<?>) no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_SIMILARITY))
				.contains(slice));

		Collection<KnowledgeSlice> all = qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		}
		catch (IllegalAccessException e) {
			fail(qyn2.getName()+" should have had no children!");
		}
		Collection<KnowledgeSlice> slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}

	public void testRemoveLocalWeight() {
		List<?> list;
		LocalWeight slice;
		list = (List<?>) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_LOCAL_WEIGHT);
		slice = (LocalWeight) list.get(0);
		NamedObject no = slice.getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no.getName()
				+ "', expected false : ", (no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_LOCAL_WEIGHT))==slice);

		Collection<KnowledgeSlice> all = qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		}
		catch (IllegalAccessException e) {
			fail(qyn2.getName()+" should have had no children!");
		}
		Collection<KnowledgeSlice> slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}

	
	public void testRemoveQuestionComparator() {
		List<?> list;
		QuestionComparatorYN slice;
		list = (List<?>) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_SIMILARITY);
		slice = (QuestionComparatorYN) list.get(4);
		NamedObject no = slice.getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));

		assertFalse("Deleted slice still mapped in '" + no.getName()
				+ "', expected false : ", (no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_SIMILARITY))==slice);

		Collection<KnowledgeSlice> all = qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		}
		catch (IllegalAccessException e) {
			fail(qyn2.getName()+" should have had no children!");
		}
		Collection<KnowledgeSlice> slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}
	

	public void testRemoveWeight() {
		List<?> list;
		Weight slice;
		list = (List<?>) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_WEIGHT);
		slice = (Weight) list.get(0);
		NamedObject no = slice.getQuestionWeightValue().getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no.getName()
				+ "', expected false : ", (no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_WEIGHT))==slice);

		Collection<KnowledgeSlice> all = qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		}
		catch (IllegalAccessException e) {
			fail(qyn2.getName()+" should have had no children!");
		}
		Collection<KnowledgeSlice> slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}
	 
}
