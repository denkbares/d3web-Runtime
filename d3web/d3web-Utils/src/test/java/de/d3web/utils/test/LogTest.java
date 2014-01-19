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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.d3web.utils.Log;
import de.d3web.utils.Log.ClassDetection;

/**
 * Test to check the methods for simplified java logging.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 18.10.2013
 */
public class LogTest {

	/**
	 * Handler to record and check the logged information.
	 * 
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	private static final class RecallHandler extends Handler {

		private LogRecord record;

		@Override
		public void publish(LogRecord record) {
			this.record = record;
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	}

	private RecallHandler handler;

	@Before
	public void init() {
		this.handler = new RecallHandler();
		Logger.getLogger("").addHandler(handler);
	}

	@Test
	public void log() {
		Log.init(ClassDetection.sun);
		Log.logger().setLevel(Level.WARNING);

		// check methods for levels logged

		Log.severe("severe");
		assertMessage("severe");

		Log.warning("warning");
		assertMessage("warning");

		// levels not logged at all
		// (and therefore message remains unchanged)

		Log.info("info");
		assertMessage("warning");

		Log.fine("fine");
		assertMessage("warning");

		Log.finer("finer");
		assertMessage("warning");

		Log.finest("finest");
		assertMessage("warning");

		// redo check with all levels
		// --------------------------

		Log.logger().setLevel(Level.FINEST);

		Log.severe("severe");
		assertMessage("severe");

		Log.warning("warning");
		assertMessage("warning");

		Log.info("info");
		assertMessage("info");

		Log.fine("fine");
		assertMessage("fine");

		Log.finer("finer");
		assertMessage("finer");

		Log.finest("finest");
		assertMessage("finest");

		// redo check with no levels
		// --------------------------

		Log.severe("off");
		Log.logger().setLevel(Level.OFF);

		Log.severe("severe");
		assertMessage("off");
		Log.severe("severe", new Exception());
		assertMessage("off");

		Log.warning("warning");
		assertMessage("off");
		Log.warning("warning", new Exception());
		assertMessage("off");

		Log.info("info");
		assertMessage("off");
		Log.info("info", new Exception());
		assertMessage("off");

		Log.fine("fine");
		assertMessage("off");
		Log.fine("fine", new Exception());
		assertMessage("off");

		Log.finer("finer");
		assertMessage("off");
		Log.finer("finer", new Exception());
		assertMessage("off");

		Log.finest("finest");
		assertMessage("off");
		Log.finest("finest", new Exception());
		assertMessage("off");
	}

	@Test
	public void sunFactory() {
		Log.init(ClassDetection.sun);
		Log.logger().setLevel(Level.WARNING);
		Log.severe("testing sunFactory");
		assertClass(getClass().getName());
		assertMethod("sunFactory");
	}

	@Test
	public void stackFactory() {
		Log.init(ClassDetection.stack);
		Log.logger().setLevel(Level.WARNING);
		Log.severe("testing stackFactory");
		assertClass(getClass().getName());
		assertMethod("stackFactory");
	}

	@Test
	public void noneFactory() {
		Log.init(ClassDetection.none);
		Log.logger().setLevel(Level.WARNING);
		Log.severe("testing noneFactory");
		assertClass("de.d3web");
		assertMethod("<method>");
	}

	public void assertClass(String className) {
		Assert.assertEquals(className, handler.record.getSourceClassName());
	}

	public void assertMethod(String methodName) {
		Assert.assertEquals(methodName, handler.record.getSourceMethodName());
	}

	public void assertMessage(String message) {
		Assert.assertEquals(message, handler.record.getMessage());
	}

}
