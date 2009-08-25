package de.d3web.kernel.psMethods.setCovering.unitTests;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.SCScore;
import de.d3web.kernel.psMethods.setCovering.persistence.loader.SCMLoader;
import de.d3web.kernel.psMethods.setCovering.persistence.writers.SCMWriter;
import de.d3web.kernel.utilities.Utils;
import de.d3web.persistence.utilities.StringBufferInputStream;
import de.d3web.persistence.utilities.StringBufferStream;

/**
 * This TestCase provides test methods for the persistence of this project.
 * 
 * @author bates
 */
public class SCMPersistenceTest extends TestCase {

	private final double EPSILON = 0.001;

	private KnowledgeBase knowledgeBase = null;

	private AnswerNum ansNum5 = null;
	private PredictedFinding f1 = null;
	private PredictedFinding f2 = null;
	private SCDiagnosis scd1 = null;
	private SCRelation scr1 = null;
	private SCRelation scr2 = null;

	private class SCMKnowledgeBase extends KnowledgeBase {

		private Question q1 = null;
		private Question q2 = null;
		private Diagnosis d1 = null;

		public SCMKnowledgeBase() {
			q1 = new QuestionOC();
			q1.setId("q1");
			q1.setKnowledgeBase(this);
			AnswerChoice ansq1 = AnswerFactory.createAnswerChoice("q1a1", "ans1q1");
			((QuestionChoice) q1).setAlternatives(Arrays.asList(new Object[]{ansq1}));

			q2 = new QuestionNum();
			q2.setId("q2");
			q2.setKnowledgeBase(this);

			d1 = new Diagnosis();
			d1.setId("d1");
			d1.setKnowledgeBase(this);
		}
	}

	public SCMPersistenceTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(SCMPersistenceTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SCMPersistenceTest.suite());
	}

	public void setUp() {
		knowledgeBase = new SCMKnowledgeBase();
	}

	public void testWriteLoad() throws Exception {

		QuestionChoice q1 = (QuestionChoice) knowledgeBase.searchQuestions("q1");
		Question q2 = knowledgeBase.searchQuestions("q2");
		Diagnosis d1 = knowledgeBase.searchDiagnosis("d1");

		// adding SCM-knowledge

		f1 = SCNodeFactory.createFindingEquals(q1, new Object[]{q1.getAnswer(null, "q1a1")});

		ansNum5 = new AnswerNum();
		ansNum5.setValue(new Double(5));

		NumericalInterval interval = new NumericalInterval(Double.NEGATIVE_INFINITY, 1, true, false);

		f2 = SCNodeFactory.createFindingNum((QuestionNum) q2, interval);

		scd1 = new SCDiagnosis();
		scd1.setNamedObject(d1);
		scd1.setAprioriProbability(0.5);

		scr1 = SCRelationFactory.createSCRelation(scd1, f1, Utils
				.createList(new Object[]{SCProbability.P6}));
		scr2 = SCRelationFactory.createSCRelation(scd1, f2, Utils
				.createList(new Object[]{SCScore.P3}));

		// generating XML-code from KnowledgeBase
		String kbXML = SCMWriter.getInstance().getXMLString(knowledgeBase);
		StringBuffer sb = new StringBuffer(kbXML);

		// generating Document from XML-code
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));
		Document dom = builder.parse(stream);

		// ---------------------------------------------------------------------------------------

		// creating new kb and loading knowledge from Document
		KnowledgeBase newKb = new SCMKnowledgeBase();
		newKb = SCMLoader.getInstance().loadKnowledgeSlices(newKb, dom);

		// checking, if loaded knowledge is equal to saved
		Collection knowledgeSlices = newKb.getAllKnowledgeSlicesFor(PSMethodSetCovering.class);

		Map scRelationMap = new HashMap();
		Map scNodeMap = new HashMap();

		Iterator iter = knowledgeSlices.iterator();
		while (iter.hasNext()) {
			SCRelation relation = (SCRelation) iter.next();
			scRelationMap.put(relation.getId(), relation);
			scNodeMap.put(relation.getSourceNode().getId(), relation.getSourceNode());
			scNodeMap.put(relation.getTargetNode().getId(), relation.getTargetNode());
		}

		SCDiagnosis loadedSCDiag = (SCDiagnosis) scNodeMap.get(scd1.getId());

		assertEquals("SCNode f1 missing or saved wrong (1)", scNodeMap.get(f1.getId()), f1);
		assertEquals("SCNode f2 missing or saved wrong (2)", scNodeMap.get(f2.getId()), f2);
		assertEquals("SCNode d1 missing or saved wrong (3)", loadedSCDiag, scd1);
		assertEquals("Apriori-prob not wrote out or read in", loadedSCDiag.getAprioriProbability(),
				scd1.getAprioriProbability(), EPSILON);

		PredictedFinding predF2 = (PredictedFinding) scNodeMap.get(f2.getId());

		NumericalInterval interval2 = ((CondNumIn) predF2.getCondition()).getInterval();

		assertEquals("interval of d2 incorrect", interval, interval2);
		assertEquals("SCRelation scr1 missing or saved wrong (1)", scRelationMap.get(scr1.getId()),
				scr1);
		assertEquals("SCRelation scr2 missing or saved wrong (2)", scRelationMap.get(scr2.getId()),
				scr2);
	}

}
