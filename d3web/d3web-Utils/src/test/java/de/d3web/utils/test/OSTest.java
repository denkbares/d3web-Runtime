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

import org.junit.Assert;
import org.junit.Test;

import de.d3web.utils.OS;

/**
 * This test does only test methods which are not used very frequently and are
 * therefore not tested by other tests already (like Headless-App-Tests).
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 18.10.2013
 */
public class OSTest {

	@Test
	public void uniqueMatch() {
		int count = 0;
		for (OS os : OS.values()) {
			if (os.isCurrentOS()) count++;
		}
		Assert.assertEquals("must have exaclty one (1) current os", 1, count);
	}

	@Test
	public void knownMatches() {
		assertOS("AIX", OS.UNIX);
		assertOS("Digital Unix", OS.UNIX);
		assertOS("FreeBSD", OS.UNIX);
		assertOS("HP UX", OS.UNIX);
		assertOS("Irix", OS.UNIX);
		assertOS("Linux", OS.UNIX);
		assertOS("Mac OS", OS.MAC_OS);
		assertOS("Mac OS X", OS.MAC_OS);
		assertOS("MPE/iX", OS.OTHER);
		assertOS("Netware 4.11", OS.OTHER);
		assertOS("OS/2", OS.OTHER);
		assertOS("Solaris", OS.UNIX);
		assertOS("Windows 2000", OS.WINDOWS);
		assertOS("Windows 95", OS.WINDOWS);
		assertOS("Windows 98", OS.WINDOWS);
		assertOS("Windows 7", OS.WINDOWS);
		assertOS("Windows 8", OS.WINDOWS);
		assertOS("Windows NT", OS.WINDOWS);
		assertOS("Windows Vista", OS.WINDOWS);
		assertOS("Windows XP", OS.WINDOWS);
	}

	private void assertOS(String displayName, OS os) {
		Assert.assertEquals(
				"Wrong OS identification for '" + displayName + "'",
				os, OS.findOS(displayName));
	}

}
