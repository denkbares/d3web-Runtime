/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.dialog2.basics.usermanaging;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Insert the type's description here. Creation date: (05.10.2001 15:48:25)
 * 
 * @author: Norman Brümmer
 */
public class UserWriter {

    public static Logger logger = Logger.getLogger(UserWriter.class);

    /**
     * 
     * @param users
     *            java.util.List
     * @param fileURL
     *            java.lang.String
     */
    public static void writeUsers(List<User> users, String fileURL) {
	try {
	    URL url = new URL(fileURL);
	    String file = url.getFile();

	    PrintWriter pw = new PrintWriter(new FileWriter(file), true);

	    pw.println("<users>");

	    Iterator<User> iter = users.iterator();
	    while (iter.hasNext()) {
		User u = iter.next();
		pw.println(u.getXMLString());
	    }

	    pw.println("</users>");
	} catch (Exception e) {
	    logger.error(e);
	}
    }

    /**
     * 
     * @param users
     *            java.util.List
     * @param fileURL
     *            java.lang.String
     */
    public static void writeUsers(List<User> users, URL fileURL) {
	try {
	    String file = fileURL.getFile();

	    PrintWriter pw = new PrintWriter(new FileWriter(file), true);

	    pw.println("<users>");

	    Iterator<User> iter = users.iterator();
	    while (iter.hasNext()) {
		User u = iter.next();
		pw.println(u.getXMLString());
	    }

	    pw.println("</users>");
	} catch (Exception x) {
	    logger.error(x);
	}
    }

}