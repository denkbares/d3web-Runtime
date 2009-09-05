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