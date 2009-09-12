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

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.psMethods.setCovering.PredictedFinding;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNodeFactory;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.SCRelationFactory;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.psMethods.setCovering.utils.TransitiveClosure;
import de.d3web.kernel.utilities.Utils;

/**
 * TestCase for the TransitiveClosure-calculation
 * 
 * @author bruemmer
 * 
 */
public class TransitiveClosureTest extends TestCase {

	private SCDiagnosis d1, d2, d3 = null;
	private PredictedFinding f1 = null;
	private SCRelation r1, r2, r3, r4, r5 = null;

	private TransitiveClosure transitiveClosure = null;

	public TransitiveClosureTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		return new TestSuite(TransitiveClosureTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TransitiveClosureTest.suite());
	}

	public void setUp() {
		Diagnosis diag1 = new Diagnosis();
		diag1.setId("d1");
		diag1.setText("d1-text");

		Diagnosis diag2 = new Diagnosis();
		diag2.setId("d2");
		diag2.setText("d2-text");

		Diagnosis diag3 = new Diagnosis();
		diag3.setId("d3");
		diag3.setText("d3-text");

		d1 = SCNodeFactory.createSCDiagnosis(diag1);
		d2 = SCNodeFactory.createSCDiagnosis(diag2);
		d3 = SCNodeFactory.createSCDiagnosis(diag3);

		QuestionNum q1 = new QuestionNum();
		q1.setId("f1");
		q1.setText("f1-text");
		f1 = SCNodeFactory.createFindingEquals(q1, new Object[]{q1.getUnknownAlternative()});

		r1 = SCRelationFactory.createSCRelation(d1, d2, null);
		r2 = SCRelationFactory.createSCRelation(d2, d3, null);
		r3 = SCRelationFactory.createSCRelation(d2, f1, null);
		r4 = SCRelationFactory.createSCRelation(d1, d3, null);
		r5 = SCRelationFactory.createSCRelation(d3, f1, null);

		transitiveClosure = new TransitiveClosure(SetPool.getInstance().getFilledSet(
				new Object[]{d1, d2, d3, f1}));

	}

	public void testTransitiveClosureCalculation() {

		Map col_d1 = transitiveClosure.getRelationsBySCNodesStartingAt(d1);
		Map col_d2 = transitiveClosure.getRelationsBySCNodesStartingAt(d2);
		Map col_d3 = transitiveClosure.getRelationsBySCNodesStartingAt(d3);
		Map col_f1 = transitiveClosure.getRelationsBySCNodesStartingAt(f1);

		Map row_d1 = transitiveClosure.getRelationsBySCNodesLeadingTo(d1);
		Map row_d2 = transitiveClosure.getRelationsBySCNodesLeadingTo(d2);
		Map row_d3 = transitiveClosure.getRelationsBySCNodesLeadingTo(d3);
		Map row_f1 = transitiveClosure.getRelationsBySCNodesLeadingTo(f1);

		Set setE = SetPool.getInstance().getEmptySet();

		Set set21 = SetPool.getInstance().getEmptySet();
		Set set31 = SetPool.getInstance().getEmptySet();
		Set set41 = SetPool.getInstance().getEmptySet();

		set21.add(Utils.createList(new Object[]{r1}));

		set31.add(Utils.createList(new Object[]{r4}));
		set31.add(Utils.createList(new Object[]{r1, r2}));

		set41.add(Utils.createList(new Object[]{r1, r3}));
		set41.add(Utils.createList(new Object[]{r1, r2, r5}));
		set41.add(Utils.createList(new Object[]{r4, r5}));

		Set set32 = SetPool.getInstance().getEmptySet();
		Set set42 = SetPool.getInstance().getEmptySet();

		set32.add(Utils.createList(new Object[]{r2}));

		set42.add(Utils.createList(new Object[]{r3}));
		set42.add(Utils.createList(new Object[]{r2, r5}));

		Set set43 = SetPool.getInstance().getEmptySet();

		set43.add(Utils.createList(new Object[]{r5}));

		assertEquals("wrong entry in matrix (col_d1, row_d1)", setE, col_d1.get(d1));
		assertEquals("wrong entry in matrix (row_d1, col_d1)", setE, row_d1.get(d1));

		assertEquals("wrong entry in matrix (col_d1, row_d2)", set21, col_d1.get(d2));
		assertEquals("wrong entry in matrix (row_d2, col_d1)", set21, row_d2.get(d1));

		assertEquals("wrong entry in matrix (col_d1, row_d3)", set31, col_d1.get(d3));
		assertEquals("wrong entry in matrix (row_d3, col_d1)", set31, row_d3.get(d1));

		assertEquals("wrong entry in matrix (col_d1, row_f1)", set41, col_d1.get(f1));
		assertEquals("wrong entry in matrix (row_f1, col_d1)", set41, row_f1.get(d1));

		assertEquals("wrong entry in matrix (col_d2, row_d1)", null, col_d2.get(d1));
		assertEquals("wrong entry in matrix (row_d1, col_d2)", null, row_d1.get(d2));

		assertEquals("wrong entry in matrix (col_d2, row_d2)", setE, col_d2.get(d2));
		assertEquals("wrong entry in matrix (row_d2, col_d2)", setE, row_d2.get(d2));

		assertEquals("wrong entry in matrix (col_d2, row_d3)", set32, col_d2.get(d3));
		assertEquals("wrong entry in matrix (row_d3, col_d2)", set32, row_d3.get(d2));

		assertEquals("wrong entry in matrix (col_d2, row_f1)", set42, col_d2.get(f1));
		assertEquals("wrong entry in matrix (row_f1, col_d2)", set42, row_f1.get(d2));

		assertEquals("wrong entry in matrix (col_d3, row_d1)", null, col_d3.get(d1));
		assertEquals("wrong entry in matrix (row_d1, col_d3)", null, row_d1.get(d3));

		assertEquals("wrong entry in matrix (col_d3, row_d2)", null, col_d3.get(d2));
		assertEquals("wrong entry in matrix (row_d2, col_d3)", null, row_d2.get(d3));

		assertEquals("wrong entry in matrix (col_d3, row_d3)", setE, col_d3.get(d3));
		assertEquals("wrong entry in matrix (row_d3, col_d3)", setE, row_d3.get(d3));

		assertEquals("wrong entry in matrix (col_d3, row_f1)", set43, col_d3.get(f1));
		assertEquals("wrong entry in matrix (row_f1, col_d3)", set43, row_f1.get(d3));

		assertEquals("wrong entry in matrix (col_f1, row_d1)", null, col_f1.get(d1));
		assertEquals("wrong entry in matrix (row_d1, col_f1)", null, row_d1.get(f1));

		assertEquals("wrong entry in matrix (col_f1, row_d2)", null, col_f1.get(d2));
		assertEquals("wrong entry in matrix (row_d2, col_f1)", null, row_d2.get(f1));

		assertEquals("wrong entry in matrix (col_f1, row_d3)", null, col_f1.get(d3));
		assertEquals("wrong entry in matrix (row_d3, col_f1)", null, row_d3.get(f1));

		assertEquals("wrong entry in matrix (col_f1, row_f1)", setE, col_f1.get(f1));
		assertEquals("wrong entry in matrix (row_f1, col_f1)", setE, row_f1.get(f1));

	}

}
