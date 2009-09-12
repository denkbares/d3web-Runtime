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
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.ObservableFinding;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCProbability;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.utils.NumericalIntervalMapper;
import de.d3web.kernel.utilities.Utils;

/**
 * TestCase for all SCM-Objects
 * 
 * @author bates
 * 
 */
public class SCObjectsTest extends TestCase {

	private ObservableFinding f1 = null;
	private PredictedFinding f2 = null;
	private PredictedFinding f3 = null;
	private PredictedFinding f4 = null;

	private SCDiagnosis scd1 = null;
	private SCDiagnosis scd2 = null;
	private SCDiagnosis scd3 = null;

	private SCRelation scr0 = null;
	private SCRelation scr1 = null;
	private SCRelation scr2 = null;
	private SCRelation scr3 = null;
	private SCRelation scr4 = null;

	public SCObjectsTest(String arg0) {
		super(arg0);
	}

	public static TestSuite suite() {
		return new TestSuite(SCObjectsTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SCObjectsTest.suite());
	}

	public void setUp() {
		AnswerNum ans1 = new AnswerNum();
		ans1.setValue(new Double(5));

		AnswerNum ans2 = new AnswerNum();
		ans2.setValue(new Double(6));

		Question q1 = new QuestionNum();
		q1.setId("Qestion");

		Question q2 = new QuestionNum();
		q2.setId("Qestion");

		Question q4 = new QuestionNum();
		q4.setId("QestionDifferent");

		Diagnosis d1 = new Diagnosis();
		d1.setId("Diagnosis");

		Diagnosis d2 = new Diagnosis();
		d2.setId("Diagnosis");

		Diagnosis d3 = new Diagnosis();
		d3.setId("DiagnosisDifferent");

		f1 = SCNodeFactory.createObservableFinding(q1, new Object[]{ans1});
		f2 = SCNodeFactory.createFindingEquals(q2, new Object[]{ans1});
		f3 = SCNodeFactory.createFindingEquals(q1, new Object[]{ans2});
		f4 = SCNodeFactory.createFindingEquals(q4, new Object[]{ans2});

		scd1 = SCNodeFactory.createSCDiagnosis(d1);
		scd2 = SCNodeFactory.createSCDiagnosis(d2);
		scd3 = SCNodeFactory.createSCDiagnosis(d3);

		List prob6AsList = Utils.createList(new Object[]{SCProbability.P6});

		scr0 = SCRelationFactory.createSCRelation(scd1, f1, prob6AsList);
		scr1 = SCRelationFactory.createSCRelation(scd1, f1, prob6AsList);
		scr2 = SCRelationFactory.createSCRelation(scd2, f1, prob6AsList);
		scr3 = SCRelationFactory.createSCRelation(scd3, f1, Utils
				.createList(new Object[]{SCProbability.P7}));
		scr4 = SCRelationFactory.createSCRelation(scd1, f2, prob6AsList);

	}

	public void testHashCode() {

		assertTrue("hashCode() of PredictedFinding incorrect (1)", f2.hashCode() == f1.hashCode());
		assertTrue("hashCode() of PredictedFinding incorrect (2)", f3.hashCode() != f1.hashCode());
		assertTrue("hashCode() of PredictedFinding incorrect (2)", f3.hashCode() != f4.hashCode());

		assertTrue("hashCode() of SCDiagnosis incorrect (1)", scd1.hashCode() == scd2.hashCode());
		assertTrue("hashCode() of SCDiagnosis incorrect (2)", scd1.hashCode() != scd3.hashCode());

		assertTrue("hashCode() of SCRelation incorrect (1)", scr0.hashCode() == scr1.hashCode());
		assertTrue("hashCode() of SCRelation incorrect (2)", scr1.hashCode() == scr2.hashCode());
		assertTrue("hashCode() of SCRelation incorrect (3)", scr2.hashCode() != scr3.hashCode());
		assertTrue("hashCode() of SCRelation incorrect (4)", scr1.hashCode() != scr3.hashCode());
		assertTrue("hashCode() of SCRelation incorrect (5)", scr1.hashCode() == scr4.hashCode());
	}

	public void testEquals() {

		assertTrue("equals() of PredictedFinding incorrect (1)", f1.equals(f2));
		assertTrue("equals() of PredictedFinding incorrect (2)", !f1.equals(f3));
		assertTrue("equals() of PredictedFinding incorrect (3)", !f4.equals(f3));

		assertTrue("equals() of SCDiagnosis incorrect (1)", scd1.equals(scd2));
		assertTrue("equals() of SCDiagnosis incorrect (2)", !scd1.equals(scd3));

		assertTrue("equals() of SCRelation incorrect (1)", scr0.equals(scr1));
		assertTrue("equals() of SCRelation incorrect (2)", scr1.equals(scr2));
		assertTrue("equals() of SCRelation incorrect (3)", !scr2.equals(scr3));
		assertTrue("equals() of SCRelation incorrect (4)", !scr1.equals(scr3));
		assertTrue("equals() of SCRelation incorrect (5)", scr1.equals(scr4));

	}

	public void testNumericalIntervalMapper() {
		NumericalIntervalMapper mapper = NumericalIntervalMapper.getInstance();

		NumericalInterval interval1 = new NumericalInterval(2.3, 4.5);
		interval1.setLeftOpen(true);
		interval1.setRightOpen(false);
		mapper.putInterval(interval1);

		NumericalInterval interval2 = new NumericalInterval(1.2, 3.4);
		interval2.setLeftOpen(false);
		interval2.setRightOpen(true);
		mapper.putInterval(interval2);

		AnswerChoice ans1 = AnswerFactory.createAnswerChoice(interval1.toString(), interval1
				.toString());
		AnswerChoice ans2 = AnswerFactory.createAnswerChoice(interval2.toString(), interval2
				.toString());

		assertEquals("mapping wrong(0)", ans1, mapper.map(interval1));
		assertEquals("mapping wrong(1)", ans2, mapper.map(interval2));

		assertEquals("mapping wrong(2)", interval1, mapper.map(ans1));
		assertEquals("mapping wrong(3)", interval2, mapper.map(ans2));
	}

}
