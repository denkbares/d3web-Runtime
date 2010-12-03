/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
 * @created 11.11.2010
 */
public class TMSTest1 extends AbstractDiaFluxTest {

	private static final String FILE = "tmstest1.d3web";

	public TMSTest1() {
		super(FILE);
	}

	@Test
	public void testSimpleTMS() {
		String[] activeIDs;

		activeIDs = new String[] {
				"Start_ID", "Frage1_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage1
		answerOCQuestion("Frage1", "Antw11");

		activeIDs = new String[] {
				"Start_ID", "Frage1_ID", "Frage3_1_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage3 to Antw31
		answerOCQuestion("Frage3", "Antw31");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage3_2_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage3 to Antw33
		answerOCQuestion("Frage3", "Antw33");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage3_2_ID", "Frage1_ID", "Frage3_1_ID" };

		assertActiveNodes("Main", activeIDs, true);

		// Set Frage3 to back to Antw31
		answerOCQuestion("Frage3", "Antw31");

		activeIDs = new String[] {
				"Snapshot_ID", "Frage3_2_ID" };

		assertActiveNodes("Main", activeIDs, true);

	}

}
