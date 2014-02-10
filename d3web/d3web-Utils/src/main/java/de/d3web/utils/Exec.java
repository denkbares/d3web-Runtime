/*
 * Copyright (C) 2014 denkbares GmbH
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
package de.d3web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class for utility methods about executing commands and do other close-system
 * actions.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 08.02.2014
 */
public class Exec {

	/**
	 * Runs a simple command in given directory. The environment is inherited
	 * from the parent process (e.g. the one in which this Java VM runs).
	 * 
	 * @return Standard output from the command.
	 * @param command The command to run
	 * @param directory The working directory to run the command in
	 * @throws IOException If the command failed
	 * @throws InterruptedException If the command was halted
	 */
	public static String runSimpleCommand(String command, File directory)
			throws IOException,
			InterruptedException {
		StringBuffer result = new StringBuffer();

		Process process = Runtime.getRuntime().exec(command, null, directory);

		BufferedReader stdout = null;
		BufferedReader stderr = null;

		try
		{
			stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String line;

			while ((line = stdout.readLine()) != null) {
				result.append(line + "\n");
			}

			StringBuffer error = new StringBuffer();
			while ((line = stderr.readLine()) != null) {
				error.append(line + "\n");
			}

			if (error.length() > 0) {
				throw new IOException("Command failed, error stream is: " + error);
			}

			process.waitFor();

		}
		finally {
			// we must close all by exec(..) opened streams:
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4784692
			process.getInputStream().close();
			if (stdout != null) stdout.close();
			if (stderr != null) stderr.close();
		}

		return result.toString();
	}
}
