package de.d3web.kernel.shared.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondChoiceYes;
import de.d3web.kernel.psMethods.shared.Abnormality;
import de.d3web.kernel.psMethods.shared.LocalWeight;
import de.d3web.kernel.psMethods.shared.PSMethodShared;
import de.d3web.kernel.psMethods.shared.QuestionWeightValue;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.psMethods.shared.comparators.KnowledgeBaseUnknownSimilarity;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorYN;

public class RemoveKnowledgeSliceTest extends TestCase {

	private KnowledgeBase base;

	private QContainer qc;

	private QuestionYN qyn1;

	private QuestionYN qyn2;

	private Diagnosis d1;

	private Diagnosis d2;

	public static Test suite() {
		return new TestSuite(RemoveKnowledgeSliceTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(RemoveKnowledgeSliceTest.suite());
	}

	protected void setUp() {
		base = new KnowledgeBase();

		qc = new QContainer();
		qc.setId("qc");
		qc.setText("qc");
		qc.setKnowledgeBase(base);

		qyn1 = new QuestionYN();
		qyn1.setId("qyn1");
		qyn1.setText("qyn1");
		qyn1.setKnowledgeBase(base);
		qyn1.setParents(Arrays.asList(new NamedObject[] { qc }));

		qyn2 = new QuestionYN();
		qyn2.setId("qyn2");
		qyn2.setText("qyn2");
		qyn2.setKnowledgeBase(base);
		qyn2.setParents(Arrays.asList(new NamedObject[] { qc }));

		d1 = new Diagnosis();
		d1.setId("d1");
		d1.setText("d1");
		d1.setKnowledgeBase(base);

		d2 = new Diagnosis();
		d2.setId("d2");
		d2.setText("d2");
		d2.setKnowledgeBase(base);

		AbstractCondition cond1 = new CondChoiceYes(qyn1);
		AbstractCondition cond2 = new CondChoiceYes(qyn2);

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
		List list;
		Abnormality slice;
		list = (List) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_ABNORMALITY);
		slice = (Abnormality) list.get(0);
		NamedObject no = slice.getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
	
		assertFalse("Deleted slice still mapped in '" + no.getText()
				+ "', expected false : ", ((List) no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_ABNORMALITY))
				.contains(slice));

		List all = (List) qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		} catch (Exception e) {
			fail(qyn2.getText()+" should have had no children!");
		}
		Collection slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}

	}

	public void testRemoveKnowledgeBaseUnknownSimilarity() {
		List list;
		KnowledgeBaseUnknownSimilarity slice;
		list = (List) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_SIMILARITY);
		slice = (KnowledgeBaseUnknownSimilarity) list.get(0);
		KnowledgeBase no = slice.getKnowledgeBase();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no
				+ "', expected false : ", ((List) no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_SIMILARITY))
				.contains(slice));

		List all = (List) qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		} catch (Exception e) {
			fail(qyn2.getText()+" should have had no children!");
		}
		Collection slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}

	public void testRemoveLocalWeight() {
		List list;
		LocalWeight slice;
		list = (List) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_LOCAL_WEIGHT);
		slice = (LocalWeight) list.get(0);
		NamedObject no = slice.getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no.getText()
				+ "', expected false : ", ((List) no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_LOCAL_WEIGHT))
				.contains(slice));

		List all = (List) qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		} catch (Exception e) {
			fail(qyn2.getText()+" should have had no children!");
		}
		Collection slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}

	
	public void testRemoveQuestionComparator() {
		List list;
		QuestionComparatorYN slice;
		list = (List) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_SIMILARITY);
		slice = (QuestionComparatorYN) list.get(4);
		NamedObject no = slice.getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));

		assertFalse("Deleted slice still mapped in '" + no.getText()
				+ "', expected false : ", ((List) no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_SIMILARITY))
				.contains(slice));

		List all = (List) qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		} catch (Exception e) {
			fail(qyn2.getText()+" should have had no children!");
		}
		Collection slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}
	

	public void testRemoveWeight() {
		List list;
		Weight slice;
		list = (List) base.getKnowledge(PSMethodShared.class,
				PSMethodShared.SHARED_WEIGHT);
		slice = (Weight) list.get(0);
		NamedObject no = slice.getQuestionWeightValue().getQuestion();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no.getText()
				+ "', expected false : ", ((List) no.getKnowledge(
				PSMethodShared.class, PSMethodShared.SHARED_WEIGHT))
				.contains(slice));

		List all = (List) qyn2.getAllKnowledge();
		try {
			base.remove(qyn2);
		} catch (Exception e) {
			fail(qyn2.getText()+" should have had no children!");
		}
		Collection slices = base
				.getAllKnowledgeSlicesFor(PSMethodShared.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}
	 
}
