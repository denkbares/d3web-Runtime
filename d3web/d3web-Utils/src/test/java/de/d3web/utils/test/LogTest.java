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

import java.util.logging.Filter;
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

	/**
	 * Some performance measures for logging.
	 * 
	 * @created 20.01.2014
	 * @param args will be ignored
	 */
	public static void main(String[] args) {
		Log.logger().setFilter(new Filter() {

			@Override
			public boolean isLoggable(LogRecord record) {
				return false;
			}
		});

		// to enable jut for both
		measureLogPerformance(false);

		// measure different stack sizes
		System.out.println("\nStack size: 1");
		measureLogPerformance(1);
		System.out.println("\nStack size: 10");
		measureLogPerformance(10);
		System.out.println("\nStack size: 100");
		measureLogPerformance(100);
		System.out.println("\nStack size: 1000");
		measureLogPerformance(1000);
		System.out.println("\nStack size: 10000");
		measureLogPerformance(10000);
	}

	private static void measureLogPerformance(int i) {
		if (i <= 0) {
			measureLogPerformance(true);
		}
		else {
			measureLogPerformance(i - 1);
		}
	}

	private static void measureLogPerformance(boolean showResults) {
		final int TEST_SIZE = 10000000;

		// classic logging, turned off
		long start = System.currentTimeMillis();
		for (int i = 0; i < TEST_SIZE; i++) {
			Logger.getLogger(LogTest.class.getName()).finest("test");
		}
		double durConOff = (System.currentTimeMillis() - start + 50) / 100 / 10.0;

		// classic logging, turned on
		start = System.currentTimeMillis();
		for (int i = 0; i < TEST_SIZE; i++) {
			Logger.getLogger(LogTest.class.getName()).info("test");
		}
		double durConOn = (System.currentTimeMillis() - start + 50) / 100 / 10.0;

		// new logging, turned off
		start = System.currentTimeMillis();
		for (int i = 0; i < TEST_SIZE; i++) {
			Logger.getLogger(LogTest.class.getName()).finest("test");
		}
		double durNewOff = (System.currentTimeMillis() - start + 50) / 100 / 10.0;

		// new logging, turned on
		start = System.currentTimeMillis();
		for (int i = 0; i < TEST_SIZE; i++) {
			Logger.getLogger(LogTest.class.getName()).info("test");
		}
		double durNewOn = (System.currentTimeMillis() - start + 50) / 100 / 10.0;

		if (!showResults) return;
		System.out.println("\tOn\tOff");
		System.out.println(String.format("java\t%ss\t%ss", durConOn, durConOff));
		System.out.println(String.format("Log\t%ss\t%ss", durNewOn, durNewOff));
	}

}
