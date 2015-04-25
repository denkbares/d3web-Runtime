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
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.utils.Files;
import de.d3web.utils.Streams;

import static org.junit.Assert.*;

/**
 * This test does only test methods which are not used very frequently and are therefore not tested
 * by other tests already (like Headless-App-Tests).
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 18.10.2013
 */
public class FilesTest {

	private static final String TXT_FILE = "src/test/resources/exampleFiles/faust.txt";
	private static final String JPG_FILE = "src/test/resources/exampleFiles/faust.jpg";

	@Test
	public void readFiles() throws IOException {
		checkBinarySize(JPG_FILE);
		checkBinarySize(TXT_FILE);

		assertEquals(
				"check text length of '" + TXT_FILE + "'",
				219369,
				Files.getText(new File(TXT_FILE)).length());
	}

	public void checkBinarySize(String filename) throws IOException, FileNotFoundException {
		File file = new File(filename);
		assertEquals(
				"check file lenght of '" + file + "'",
				file.length(),
				Files.getBytes(file).length);
	}

	@Test
	public void directories() throws IOException {
		File folder = Files.createTempDir();
		Assert.assertTrue(folder.exists());

		File sub = new File(folder, "foo/bla/goo");
		Assert.assertTrue(sub.mkdirs());

		File file = new File(sub, "test.jpg");
		Streams.streamAndClose(
				new FileInputStream(JPG_FILE),
				new FileOutputStream(file));
		Assert.assertTrue(file.exists());

		Files.recursiveDelete(folder);
		Assert.assertFalse(file.exists());
		Assert.assertFalse(sub.exists());
		Assert.assertFalse(folder.exists());
	}

	@Test
	public void extensions() {
		// test with null
		assertEquals(null, Files.getExtension((String) null));
		assertEquals(null, Files.getExtension((File) null));
		assertEquals(null, Files.stripExtension((String) null));
		assertEquals(null, Files.stripExtension((File) null));
		assertFalse(Files.hasExtension((String) null, "jpg", "txt"));
		assertFalse(Files.hasExtension((File) null, "jpg", "txt"));
		assertFalse(Files.hasExtension((String) null, (String) null));
		assertFalse(Files.hasExtension((File) null, (String[]) null));

		String txtFileName = "hello world..foo.txt";
		File txtFile = new File(txtFileName);
		assertEquals("txt", Files.getExtension(txtFileName));
		assertEquals("txt", Files.getExtension(txtFile));
		assertEquals("hello world..foo", Files.stripExtension(txtFileName));
		assertEquals("hello world..foo", Files.stripExtension(txtFile));
		assertTrue(Files.hasExtension(txtFileName, "jpg", "txt"));
		assertTrue(Files.hasExtension(txtFile, "jpg", "txt"));
		assertFalse(Files.hasExtension(txtFileName, "foo", "bla"));
		assertFalse(Files.hasExtension(txtFile, "foo", "bla"));
		assertFalse(Files.hasExtension(txtFileName, (String) null));
		assertFalse(Files.hasExtension(txtFile, (String[]) null));
	}
}
