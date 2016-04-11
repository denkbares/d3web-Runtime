/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.strings.test;

import org.junit.Test;

import de.d3web.strings.LowerCaseNormalizer;
import de.d3web.strings.MultiWordNormalizer;
import de.d3web.strings.TokenNormalizer;

import static org.junit.Assert.assertEquals;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 25.04.2015
 */
public class TokenNormalizerTest {

	@Test
	public void lowerCase() throws Exception {
		TokenNormalizer normalizer = new LowerCaseNormalizer();
		assertEquals("hello", normalizer.normalize("Hello"));
	}

	@Test(expected = NullPointerException.class)
	public void lowerCaseNull() throws Exception {
		TokenNormalizer normalizer = new LowerCaseNormalizer();
		normalizer.normalize(null);
	}

	@Test
	public void multiWord() throws Exception {
		TokenNormalizer normalizer = new MultiWordNormalizer();
		assertEquals("hello world you re rotating",
				normalizer.normalize("Hello world,\n\tyou're  rotating!"));
	}

	@Test(expected = NullPointerException.class)
	public void multiWordNull() throws Exception {
		TokenNormalizer normalizer = new MultiWordNormalizer();
		normalizer.normalize(null);
	}

}