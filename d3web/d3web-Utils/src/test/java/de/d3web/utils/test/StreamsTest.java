/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.utils.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.utils.Streams;

/**
 * This test does only test methods which are not used very frequently and are
 * therefore not tested by other tests already (like Headless-App-Tests).
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 18.10.2013
 */
public class StreamsTest {

	@Test
	public void readFiles() throws IOException {
		checkBinarySize("src/test/resources/exampleFiles/faust.jpg");
		checkBinarySize("src/test/resources/exampleFiles/faust.txt");

		Assert.assertEquals(
				"check text length of 'faust.txt'",
				219369,
				Streams.getTextAndClose(
						new FileInputStream("src/test/resources/exampleFiles/faust.txt")).length());
	}

	@SuppressWarnings("resource")
	public void checkBinarySize(String filename) throws IOException, FileNotFoundException {
		File file = new File(filename);
		Assert.assertEquals(
				"check file lenght of '" + file + "'",
				file.length(),
				Streams.getBytesAndClose(new FileInputStream(file)).length);
	}
}
