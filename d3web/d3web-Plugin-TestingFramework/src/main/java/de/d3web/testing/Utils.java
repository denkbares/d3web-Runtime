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
package de.d3web.testing;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * General utility class of the Testing Framework.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 11.06.2012
 */
public class Utils {

	/**
	 * Checks whether the calling thread has been interrupted and throws
	 * InterruptedException in case.
	 * 
	 * @created 16.08.2012
	 * @throws InterruptedException
	 */
	public static void checkInterrupt() throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
	}

	/**
	 * Method mostly for testing purposes.
	 * 
	 * @created 20.09.2012
	 * @param testClass the name of the test you want to slow down (for log
	 *        message)
	 * @param milliseconds the estimated time in milliseconds by which the test
	 *        will be slowed down
	 * @param interruptible to make the test interruptible or not
	 * @throws InterruptedException
	 */
	public static void slowDowntest(Class<?> testClass, int milliseconds, boolean interruptible) throws InterruptedException {
		for (int i = 0; i < milliseconds; i++) {
			if (interruptible) {
				checkInterrupt();
			}
			List<Double> sortMe = new LinkedList<Double>();
			for (int j = 0; j < 3500; j++) {
				sortMe.add(Math.random());
			}
			Collections.sort(sortMe);
			if (i % 1000 == 0) Logger.getLogger(testClass.getName()).info(
					testClass.getSimpleName() + ": " + i + "/" + milliseconds + " iterations.");
		}
		Logger.getLogger(testClass.getName()).info(
				testClass.getSimpleName() + ": " + milliseconds + "/" + milliseconds
						+ " iterations.");
	}
}
