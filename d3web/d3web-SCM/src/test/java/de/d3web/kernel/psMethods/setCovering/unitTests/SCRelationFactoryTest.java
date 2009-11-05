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

package de.d3web.kernel.psMethods.setCovering.unitTests;

import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.utilities.Utils;

/**
 * Test case for testing all features of the SCRelationFactory
 * 
 * @author bates
 * 
 */
public class SCRelationFactoryTest extends TestCase {

	private SCRelation relation = null;
	private PredictedFinding finding = null;
	private SCDiagnosis scdiagnosis = null;

	public SCRelationFactoryTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(SCRelationFactoryTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SCRelationFactoryTest.suite());
	}

	public void setUp() {
		Question question = new QuestionNum();
		question.setId("Q1");
		question.setText("Question1");

		this.finding = SCNodeFactory.createFindingEquals(question, new Object[]{});

		Diagnosis diagnosis = new Diagnosis();
		diagnosis.setId("D1");
		diagnosis.setText("Diagnosis1");

		this.scdiagnosis = SCNodeFactory.createSCDiagnosis(diagnosis);

		this.relation = SCRelationFactory.createSCRelation(this.scdiagnosis, this.finding, Utils
				.createList(new Object[]{new SCProbability("0.8", 0.8)}));
	}

	/**
	 * tests if the Relation has been added correctly to the knowledgeMap of the
	 * corresponding NamedObjects
	 */
	public void testKnowledgeAddition() {
		assertEquals("Source is not scdiagnosis!", scdiagnosis, relation.getSourceNode());
		assertEquals("Target is not finding!", finding, relation.getTargetNode());

		List forwardKnowledge = scdiagnosis.getNamedObject().getKnowledge(
				PSMethodSetCovering.class, MethodKind.FORWARD);

		List backwardKnowledge = finding.getNamedObject().getKnowledge(
				PSMethodSetCovering.class, MethodKind.BACKWARD);

		assertTrue("scdiagnosis-knowledge is null or empty", (forwardKnowledge != null)
				&& (!forwardKnowledge.isEmpty()));
		assertTrue("finding-knowledge is null or empty", (backwardKnowledge != null)
				&& (!backwardKnowledge.isEmpty()));

		assertEquals("knowledge wrong in scdiagnosis", this.relation, forwardKnowledge.get(0));
		assertEquals("knowledge wrong in finding", this.relation, backwardKnowledge.get(0));
	}

	/**
	 * tests Exception-throwing in case of giving wrong parameters
	 */
	public void testTypeCheck() {

		finding = SCNodeFactory.createFindingEquals(new QuestionNum(), new Object[]{});
		SCDiagnosis scDiagnosis = new SCDiagnosis();
		scDiagnosis.setNamedObject(new Diagnosis());

		List knowledgeList = Utils.createList(new Object[]{SCProbability.ZERO});

		try {
			SCRelationFactory.createSCRelation(null, finding, knowledgeList);
			assertTrue("illegal argument null accepted (as source)!", false);
		} catch (IllegalArgumentException e) {
		}

		try {
			SCRelationFactory.createSCRelation(scdiagnosis, null, knowledgeList);
			assertTrue("illegal argument null accepted (as target)!", false);
		} catch (IllegalArgumentException e) {
		}

		try {
			SCRelationFactory.createSCRelation(finding, scDiagnosis, knowledgeList);
			assertTrue("illegal argument finding accepted (as source)!", false);
		} catch (IllegalArgumentException e) {
		}

//		TODO: commented out this test, because SCProbability accepts negative values
//		try {
//			new SCProbability("-0.1", -0.1);
//			assertTrue("illegal probability accepted (-0.1)!", false);
//		} catch (IllegalArgumentException e) {
//		}

		try {
			new SCProbability("1.1", 1.1);
			assertTrue("illegal probability accepted (1.1)!", false);
		} catch (IllegalArgumentException e) {
		}

	}

}
