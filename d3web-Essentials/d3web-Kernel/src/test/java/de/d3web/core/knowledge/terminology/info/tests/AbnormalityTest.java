/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.core.knowledge.terminology.info.tests;

import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;

import static de.d3web.core.knowledge.terminology.info.abnormality.Abnormality.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DefaultAbnormality}
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 23.03.2012
 */
public class AbnormalityTest {

	@Test
	public void choices() {
		String s = "Choice1:0.5; Choice2 : 0.6";
		DefaultAbnormality abnormality = DefaultAbnormality.valueOf(s);
		double value = abnormality.getValue(new ChoiceValue(new ChoiceID("Choice1")));
		assertEquals(0.5, value, 0);
		value = abnormality.getValue(new ChoiceValue(new ChoiceID("Choice2")));
		assertEquals(0.6, value, 0);
		DefaultAbnormality reParsed = DefaultAbnormality.valueOf(abnormality.toString());
		value = reParsed.getValue(new ChoiceValue(new ChoiceID("Choice1")));
		assertEquals(0.5, value, 0);
		value = reParsed.getValue(new ChoiceValue(new ChoiceID("Choice2")));
		assertEquals(0.6, value, 0);
		// Choice containing ":", value in German format, last pair empty
		String s2 = "Choice:A:0,7; ";
		assertEquals(0.7, DefaultAbnormality.valueOf(s2)
				.getValue(new ChoiceValue(new ChoiceID("Choice:A"))), 0);
		// testing with "real" choice and only one item
		Choice choice = new Choice("Choice");
		assertEquals(0.8, DefaultAbnormality.valueOf(choice + ":0.8").getValue(
				new ChoiceValue(choice)), 0);

		assertEquals(A4, DefaultAbnormality.valueOf("Choice:A3:a4")
				.getValue(new ChoiceValue(new ChoiceID("Choice:A3"))), 0);

		assertEquals(A4, DefaultAbnormality.valueOf("\"Choice;with;colon\":a4")
				.getValue(new ChoiceValue(new ChoiceID("Choice;with;colon"))), 0);
	}

	@Test
	public void numeric() {
		AbnormalityNum num = AbnormalityNum.valueOf("(0 1):A1; ]2.0 3.1[:0.2 ; [4 5]:0,2 ; [ 6 7 ] : 90%");
		assertEquals(A5, num.getValue(0), 0);
		assertEquals(A5, num.getValue(1), 0);
		assertEquals(A1, num.getValue(0.5), 0);

		assertEquals(A5, num.getValue(2), 0);
		assertEquals(A5, num.getValue(3.1), 0);
		assertEquals(0.2, num.getValue(3), 0);

		assertEquals(0.2, num.getValue(4), 0);
		assertEquals(0.2, num.getValue(5), 0);
		assertEquals(0.2, num.getValue(4.5), 0);

		assertEquals(0.9, num.getValue(6), 0);
		assertEquals(0.9, num.getValue(7), 0);

		assertEquals("(0.0 1.0) : A1; (2.0 3.1) : 0.2; [4.0 5.0] : 0.2; [6.0 7.0] : 0.9", num.toString());
	}
}
