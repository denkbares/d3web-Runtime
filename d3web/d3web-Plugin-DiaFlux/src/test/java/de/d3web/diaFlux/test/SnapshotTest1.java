/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.test;

import org.junit.Test;

/**
 * 
 * @author Reinhard Hatko
 * @created 03.12.2010
 */
public class SnapshotTest1 extends AbstractDiaFluxTest {

	private static final String FILE = "snapshottest1.d3web";
	private static final int TORTURE_LIMIT = 10;

	public SnapshotTest1() {
		super(FILE);
	}

	@Test
	public void testSnapshotSimpleCircle() throws Exception {

		String[] activeIDs;

		activeIDs = new String[] {
				"Start_ID", "Frage21_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage21 to Antw11
		answerOCQuestion("Frage21", "Antw11");

		activeIDs = new String[] {
				"Start_ID", "Frage21_ID", "Frage22_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage23 to Antw21
		answerOCQuestion("Frage22", "Antw21");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage23_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage3 to Antw31
		answerOCQuestion("Frage23", "Antw31");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage23_ID", "Frage21_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage22 to back to Ant22
		answerOCQuestion("Frage22", "Antw21");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage23_ID", "Frage21_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage21 to Antw11
		answerOCQuestion("Frage21", "Antw11");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage23_ID", "Frage21_ID", "Frage22_ID" };

		assertActiveNodes("Main", activeIDs, true);
	}

	@Test
	public void testSnapCircleTorture() throws Exception {

		testSnapshotSimpleCircle();

		for (int i = 0; i < TORTURE_LIMIT; i++) {

			String[] activeIDs;

			// Set Frage22 to Antw21
			answerOCQuestion("Frage22", "Antw21");

			activeIDs = new String[] {
					"Snapshot_ID", "Frage23_ID", "Frage21_ID" };

			assertActiveNodes("Main", activeIDs, true);

			// Set Frage21 to Antw11
			answerOCQuestion("Frage21", "Antw11");

			activeIDs = new String[] {
					"Snapshot_ID", "Frage23_ID", "Frage21_ID", "Frage22_ID" };

			assertActiveNodes("Main", activeIDs, true);

			// Set Frage22 to back to Ant22
			answerOCQuestion("Frage22", "Antw21");

			activeIDs = new String[] {
					"Snapshot_ID", "Frage23_ID", "Frage21_ID" };

			assertActiveNodes("Main", activeIDs, true);

		}
	}

}
