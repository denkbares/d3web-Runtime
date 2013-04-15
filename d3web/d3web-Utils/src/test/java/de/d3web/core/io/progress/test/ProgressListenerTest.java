/*
 * Copyright (C) 2013 denkbares GmbH
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
import static org.junit.Assert.assertTrue;

import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.Test;

import de.d3web.core.io.progress.CombinedProgressListener;
import de.d3web.core.io.progress.ProgressDialog;

;

public class ProgressListenerTest {

	@Test(expected = IllegalArgumentException.class)
	public void illegalArgument() {
		CombinedProgressListener listener = new CombinedProgressListener(-1, new ProgressCounter());
		listener.updateProgress(100, "Done");
	}

	private static class TestCancelAction extends Thread {

		boolean cancled = false;

		@Override
		public void run() {
			cancled = true;
		}

		public boolean isCancled() {
			return cancled;
		}
	}

	@Test
	public void progressDialog() throws InterruptedException, InvocationTargetException {
		TestCancelAction cancelAction = new TestCancelAction();
		final ProgressDialog progressDialog = new ProgressDialog("Test");
		progressDialog.setCancelAction(cancelAction);
		progressDialog.setVisible(true);
		progressDialog.updateProgress(0.1f, "Start");
		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				assertEquals(0.1f, progressDialog.getProgress(), 0.00001);
			}
		});
		progressDialog.updateProgress(1f, "Done");
		progressDialog.dispatchEvent(new WindowEvent(progressDialog,
				WindowEvent.WINDOW_CLOSING));
		assertTrue(cancelAction.isCancled());
	}
}
