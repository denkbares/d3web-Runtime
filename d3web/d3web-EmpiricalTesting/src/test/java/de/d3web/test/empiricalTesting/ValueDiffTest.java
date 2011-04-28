/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.test.empiricalTesting;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.empiricaltesting.caseAnalysis.ValueDiff;

/**
 * This class tests the differences between different values assigned to
 * questions.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 20.04.2011
 */
public class ValueDiffTest {

	@Test
	public void diffChoiceValueTest() {
		Value v1 = new ChoiceValue(new Choice("v1"));
		Value v2 = new ChoiceValue(new Choice("v2"));
		Value v1n = new ChoiceValue(new Choice("v1"));

		assertFalse(new ValueDiff(v1, null).differ());
		assertFalse(new ValueDiff(null, v1).differ());
		assertFalse(new ValueDiff(null, null).differ());

		assertFalse(new ValueDiff(v1, v1).differ());
		assertFalse(new ValueDiff(v1, v1n).differ());
		assertTrue(new ValueDiff(v1, v2).differ());
	}

	@Test
	public void diffNumValueTest() {
		Value v1 = new NumValue(1.0);
		Value v2 = new NumValue(2.0);
		Value v1n = new NumValue(1.0);

		assertFalse(new ValueDiff(v1, v1).differ());
		assertFalse(new ValueDiff(v1, v1n).differ());
		assertTrue(new ValueDiff(v1, v2).differ());
	}

	@Test
	public void diffValueToStringTest() {
		Value v1 = new NumValue(1.0);
		Value v2 = new NumValue(2.0);
		Value v1n = new NumValue(1.0);

		assertEquals("eq val: 1.0", new ValueDiff(v1, v1n).toString());
		assertEquals("exp: 1.0 but was 2.0", new ValueDiff(v1, v2).toString());
	}

}
