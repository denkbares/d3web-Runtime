package de.d3web.kernel.psMethods.setCovering.unitTests;

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
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCRelation;

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

		SCRelation sl1 = new SCRelation();
		SCDiagnosis sc1 = new SCDiagnosis();
		sc1.setNamedObject(d1);
		PredictedFinding sc2 = new PredictedFinding();
		sc2.setNamedObject(qyn1);
		sc2.setCondition(cond1);
		sl1.setSourceNode(sc1);
		sl1.setTargetNode(sc2);
		d1.addKnowledge(PSMethodSetCovering.class, sl1,
				MethodKind.FORWARD);
		
		
		
		
		SCRelation sl2 = new SCRelation();
		SCDiagnosis sc3 = new SCDiagnosis();
		sc3.setNamedObject(d1);
		PredictedFinding sc4 = new PredictedFinding();
		sc4.setNamedObject(qyn1);
		sc4.setCondition(cond1);
		sl2.setSourceNode(sc3);
		sl2.setTargetNode(sc4);
		d1.addKnowledge(PSMethodSetCovering.class, sl2,
				MethodKind.FORWARD);

	}

	public void testRemoveSCRelation() {
		List list;
		SCRelation slice;
		list = (List) base.getKnowledge(PSMethodSetCovering.class,
				MethodKind.FORWARD);
		slice = (SCRelation) list.get(0);
		NamedObject no = slice.getSourceNode().getNamedObject();
		assertTrue("Tried to remove existing slice, true expected : ", base
				.remove(slice));
		
		assertFalse("Deleted slice still mapped in '" + no.getText()
				+ "', expected false : ", ((List) no.getKnowledge(
						PSMethodSetCovering.class, MethodKind.FORWARD))
				.contains(slice));

		List all = (List) d1.getAllKnowledge();
		try {
			base.remove(d1);
		} catch (Exception e) {
			fail(d1.getText()+" should have had no children!");
		}
		Collection slices = base
				.getAllKnowledgeSlicesFor(PSMethodSetCovering.class);

		for (Object object : all) {
			assertFalse("Deleted slice still mapped in Knowledgebase : ",
					slices.contains(object));
		}
	}
	 
}
