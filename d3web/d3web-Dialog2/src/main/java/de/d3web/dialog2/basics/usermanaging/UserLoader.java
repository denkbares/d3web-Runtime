/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.dialog2.basics.usermanaging;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.utilities.InputFilter;
import de.d3web.dialog2.basics.settings.ResourceRepository;

public class UserLoader {

	private java.net.URL fileURL;

	private List<User> users;

	public static Logger logger = Logger.getLogger(UserLoader.class);

	public UserLoader() {
		users = new LinkedList<User>();
		setFileURL(ResourceRepository.getInstance().getPropertyPathValue(
				ResourceRepository.USERS_URL));
	}

	/**
	 * @param doc org.w3c.dom.Document
	 */
	private void buildUserLists(Document doc) {
		try {
			NodeList nl = doc.getElementsByTagName("user");
			for (int i = 0; i < nl.getLength(); ++i) {
				Node n = nl.item(i);
				String name = n.getAttributes().getNamedItem("name")
						.getNodeValue();
				String forename = n.getAttributes().getNamedItem("forename")
						.getNodeValue();
				String pass = n.getAttributes().getNamedItem("pass")
						.getNodeValue();
				String email = n.getAttributes().getNamedItem("email")
						.getNodeValue();
				String admin = n.getAttributes().getNamedItem("admin")
						.getNodeValue();

				User u = new User();
				u.setName(name);
				u.setForename(forename);
				u.setPassword(pass);
				u.setEmail(email);

				if (admin.equalsIgnoreCase("TRUE")) {
					u.setAdmin(true);
				}
				else {
					users.add(u);
				}
			}
		}
		catch (Exception x) {
			logger.error(x);
		}
	}

	/**
	 * @return java.net.URL
	 */
	public java.net.URL getFileURL() {
		return fileURL;
	}

	/**
	 * @return java.util.List
	 */
	public List<User> getUsers() {
		return users;
	}

	public void load() {
		try {
			users = new LinkedList<User>();

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = dBuilder.parse(InputFilter
					.getFilteredInputSource(fileURL));

			buildUserLists(doc);

		}
		catch (Exception x) {
			logger.error("no user-xml-file found.");
		}

	}

	/**
	 * @param newFileURL java.net.URL
	 */
	public void setFileURL(java.net.URL newFileURL) {
		fileURL = newFileURL;
	}

	/**
	 * @param newFileURL java.net.URL
	 */
	public void setFileURL(String urlstg) {
		try {
			fileURL = new URL(urlstg);
		}
		catch (Exception x) {
			logger.error("URL: " + urlstg + " not valid!");
		}
	}
}