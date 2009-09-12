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

package de.d3web.dialog2.basics.knowledge;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.dialog2.util.DialogUtils;
import de.d3web.xml.utilities.InputFilter;

public class CRDescriptorLoader {

    private static CRDescriptorLoader instance = null;

    public static CRDescriptorLoader getInstance() {
	if (instance == null) {
	    instance = new CRDescriptorLoader();
	}
	return instance;
    }

    private URL descriptorUrl = null;

    public static Logger logger = Logger.getLogger(CRDescriptorLoader.class);

    public java.net.URL getDescriptorUrl() {
	return descriptorUrl;
    }

    public List<CaseRepositoryDescriptor> load() {

	List<CaseRepositoryDescriptor> descrList = new LinkedList<CaseRepositoryDescriptor>();
	if (!(new File(descriptorUrl.getFile()).exists())) {
	    return descrList;
	}
	try {

	    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder();
	    Document doc = dBuilder.parse(InputFilter
		    .getFilteredInputSource(descriptorUrl));

	    NodeList nl = doc.getElementsByTagName("CaseRepository");
	    for (int i = 0; i < nl.getLength(); ++i) {
		Node n = nl.item(i);
		String kbID = n.getAttributes().getNamedItem("kbID")
			.getNodeValue();
		String locationType = n.getAttributes().getNamedItem(
			"locationType").getNodeValue();
		String location = n.getAttributes().getNamedItem("location")
			.getNodeValue();

		List<String> userEmails = new LinkedList<String>();
		NodeList userNodes = n.getChildNodes();
		for (int k = 0; k < userNodes.getLength(); ++k) {
		    Node userNode = userNodes.item(k);
		    if (userNode.getNodeName().equalsIgnoreCase("user")) {
			String email = userNode.getAttributes().getNamedItem(
				"email").getNodeValue();
			userEmails.add(email);
		    }
		}

		CaseRepositoryDescriptor crd = new CaseRepositoryDescriptor();
		crd.setKbId(kbID);
		crd.setLocationType(locationType);
		crd.setLocation(DialogUtils.getRealPath(location));
		crd.setUserEmails(userEmails);

		descrList.add(crd);
	    }

	} catch (Exception x) {
	    logger.error("Error while loading CaseRepositoryDescriptors");
	}
	return descrList;
    }

    public void save(List<CaseRepositoryDescriptor> descrList) {

	try {

	    logger.info("Writing to: " + descriptorUrl);

	    PrintWriter pw = new PrintWriter(new FileWriter(descriptorUrl
		    .getFile()), true);

	    pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>");

	    pw.println("<CaseRepositories>");

	    Iterator<CaseRepositoryDescriptor> iter = descrList.iterator();
	    while (iter.hasNext()) {
		CaseRepositoryDescriptor desc = iter.next();
		pw.println("\t<CaseRepository kbID='" + desc.getKbId()
			+ "' location='"
			+ DialogUtils.getVariablePath(desc.getLocation())
			+ "' locationType='" + desc.getLocationType() + "' >");

		if (desc.getUserEmails() != null) {
		    Iterator<String> users = desc.getUserEmails().iterator();
		    while (users.hasNext()) {
			pw.println("\t\t<user email='" + users.next() + "' />");
		    }
		}
		pw.println("\t</CaseRepository>");
	    }

	    pw.println("</CaseRepositories>");

	    pw.close();

	} catch (Exception e) {
	    logger.error(e + " -> cb-descriptors could not be saved");
	}

    }

    public void setDescriptorUrl(URL newDescriptorUrl) {
	descriptorUrl = newDescriptorUrl;
    }

}