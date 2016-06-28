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

import junit.framework.Assert;

import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;

/**
 * Tests {@link DefaultAbnormality}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 23.03.2012
 */
public class AbnormalityTest {

	@Test
	public void test() {
		String s = "Choice1:0.5; Choice2 : 0.6";
		DefaultAbnormality abnormality = DefaultAbnormality.valueOf(s);
		double value = abnormality.getValue(new ChoiceValue(new ChoiceID("Choice1")));
		Assert.assertEquals(0.5, value);
		value = abnormality.getValue(new ChoiceValue(new ChoiceID("Choice2")));
		Assert.assertEquals(0.6, value);
		DefaultAbnormality reParsed = DefaultAbnormality.valueOf(abnormality.toString());
		value = reParsed.getValue(new ChoiceValue(new ChoiceID("Choice1")));
		Assert.assertEquals(0.5, value);
		value = reParsed.getValue(new ChoiceValue(new ChoiceID("Choice2")));
		Assert.assertEquals(0.6, value);
		// Choice containing ":", value in German format, last pair empty
		String s2 = "Choice:A:0,7; ";
		Assert.assertEquals(0.7,
				DefaultAbnormality.valueOf(s2).getValue(new ChoiceValue(new ChoiceID("Choice:A"))));
		// testing with "real" choice and only one item
		Choice choice = new Choice("Choice");
		Assert.assertEquals(
				0.8,
				DefaultAbnormality.valueOf(choice + ":0.8").getValue(
						new ChoiceValue(choice)));

	}

}
