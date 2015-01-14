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

package de.d3web.utils.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.d3web.utils.Stopwatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 14.01.2015
 */
public class StopwatchTest {

	@Test
	public void basic() throws InterruptedException {
		// basic timing
		Stopwatch time = new Stopwatch();
		Thread.sleep(10);
		assertAtLeast(time, 10);

		// reset and automatically stop
		time.reset();
		assertTrue(time.getTime() == 0);
		Thread.sleep(1);
		assertTrue(time.getTime() == 0);

		// resume multiple times
		time.resume();
		Thread.sleep(1);
		assertAtLeast(time, 1);
		time.resume();
		Thread.sleep(1);
		assertAtLeast(time, 2);
		time.resume();
		Thread.sleep(1);
		assertAtLeast(time, 3);

		// check pause
		time.pause();
		assertAtLeast(time, 3);
		long temp = time.getTime();
		Thread.sleep(1);
		assertTrue(time.getTime() == temp);

		// check resume after pause
		time.resume();
		Thread.sleep(1);
		assertAtLeast(time, temp + 1);
	}

	@Test
	public void display() {
		Stopwatch time = new Stopwatch();
		time.reset();
		assertEquals("0ms", time.getDisplay());
		assertEquals("0ms", time.getDisplay(TimeUnit.MILLISECONDS));
		assertEquals("0.000s", time.getDisplay(TimeUnit.SECONDS));
		assertEquals("0:00 min", time.getDisplay(TimeUnit.MINUTES));
		assertEquals("0:00:00 hours", time.getDisplay(TimeUnit.HOURS));
		assertEquals("Elapsed time: 0ms", time.toString());
	}

	public void assertAtLeast(Stopwatch timer, long minTime) {
		long time = timer.getTime();
		assertTrue("time of " + time + " is not >=" + minTime, time >= minTime);
	}
}
