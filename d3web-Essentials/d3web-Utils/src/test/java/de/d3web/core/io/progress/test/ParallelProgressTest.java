/*
 * Copyright (C) 2012 denkbares GmbH, Germany
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
package de.d3web.core.io.progress.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.d3web.core.io.progress.ParallelProgress;

/**
 * 
 * @author volker_belli
 * @created 15.09.2012
 */
public class ParallelProgressTest {

	@Test
	public void simultaneousProgress() {
		ProgressCounter counter = new ProgressCounter();
		ParallelProgress parallel = new ParallelProgress(counter, 1f, 4f, 5f);

		// check individual updates
		parallel.getSubTaskProgressListener(0).updateProgress(0.1f, "");
		assertEquals(1, counter.percent);

		parallel.getSubTaskProgressListener(1).updateProgress(0.1f, "");
		assertEquals(5, counter.percent);

		parallel.getSubTaskProgressListener(2).updateProgress(0.1f, "");
		assertEquals(10, counter.percent);

		parallel.getSubTaskProgressListener(0).updateProgress(1f, "");
		assertEquals(19, counter.percent);

		parallel.getSubTaskProgressListener(1).updateProgress(1f, "");
		assertEquals(55, counter.percent);

		parallel.getSubTaskProgressListener(2).updateProgress(1f, "");
		assertEquals(100, counter.percent);

		// over-finished
		parallel.getSubTaskProgressListener(0).updateProgress(2f, "");
		parallel.getSubTaskProgressListener(1).updateProgress(2f, "");
		parallel.getSubTaskProgressListener(2).updateProgress(2f, "");
		assertEquals(100, counter.percent);

		// check decreasing from over-finished
		parallel.getSubTaskProgressListener(0).updateProgress(1f, "");
		parallel.getSubTaskProgressListener(1).updateProgress(1f, "");
		parallel.getSubTaskProgressListener(2).updateProgress(1f, "");
		assertEquals(100, counter.percent);

		// check decreasing in normal range
		parallel.getSubTaskProgressListener(0).updateProgress(0.1f, "");
		parallel.getSubTaskProgressListener(1).updateProgress(0.1f, "");
		parallel.getSubTaskProgressListener(2).updateProgress(0.1f, "");
		assertEquals(10, counter.percent);

		// check decreasing below 0
		parallel.getSubTaskProgressListener(0).updateProgress(-1f, "");
		assertEquals(9, counter.percent);
		parallel.getSubTaskProgressListener(1).updateProgress(-1f, "");
		parallel.getSubTaskProgressListener(2).updateProgress(-1f, "");
		assertEquals(0, counter.percent);
	}
}
