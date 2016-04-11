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
package de.d3web.core.io.progress;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests progresslisteners not covered by other tests
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 03.08.2011
 */
public class ProgressListenerTest {

	@Test
	public void testConsole() throws UnsupportedEncodingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		ConsoleProgressListener listener = new ConsoleProgressListener();
		listener.updateProgress(0, "Updating");
		// repetition should not be printed
		listener.updateProgress(0, "Updating");
		listener.updateProgress(0.2f, "Updating...");
		listener.updateProgress(1, "Finished");
		System.out.flush();
		String actual = out.toString("UTF-8").replace("\r", "").trim();
		String expected = "0%: Updating\n20%: Updating...\n100%: Finished";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testMultiProgressListener() {
		TestListener a = new TestListener();
		TestListener b = new TestListener();
		MultiProgressListener multi = new MultiProgressListener(a, b);
		String testString = "test";
		multi.updateProgress(0, testString);
		Assert.assertEquals(1, a.messages.size());
		Assert.assertEquals(1, b.messages.size());
		Assert.assertEquals(testString, a.messages.get(0));
		Assert.assertEquals(testString, b.messages.get(0));
		multi.updateProgress(0.5f, testString);
		Assert.assertEquals(2, a.messages.size());
		Assert.assertEquals(2, b.messages.size());

	}

	private static class TestListener implements ProgressListener {

		List<String> messages = new LinkedList<String>();

		@Override
		public void updateProgress(float percent, String message) {
			messages.add(message);
		}

	}

}
