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

import de.d3web.core.io.utilities.InputFilter;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.util.DialogUtils;

public class KBDescriptorLoader {

	private static KBDescriptorLoader instance = null;

	private List<KnowledgeBaseDescriptor> kbdescriptors = null;

	private URL descLocation = null;

	public static Logger logger = Logger.getLogger(KBDescriptorLoader.class);

	/**
	 * @return de.d3web.gui.html.knowledge.KBDescriptorLoader
	 */
	public static KBDescriptorLoader getInstance() {
		if (instance == null) {
			instance = new KBDescriptorLoader();
		}
		return instance;
	}

	private KBDescriptorLoader() {
		initialize();
	}

	/**
	 * 
	 * @return boolean
	 * @param desc de.d3web.gui.html.knowledge.KnowledgeBaseDescriptor
	 */
	public boolean addDescriptor(KnowledgeBaseDescriptor desc) {
		if (!kbdescriptors.contains(desc)) {
			kbdescriptors.add(desc);
			save();
			KnowledgeBaseRepository.getInstance().refreshKbDescriptorList(
					kbdescriptors);
			return true;
		}
		return false;
	}

	/**
	 * If the given location is a "jar:"-location, a cleaned file-location will
	 * be returned. Example: "jar:xyz.jar!/" is going to be "xyz.jar".
	 */
	private String getFileLocation(String jarLocation) {
		String PRAEFIX = "jar:";
		String location = jarLocation;
		if (location.startsWith(PRAEFIX)) {
			location = location.substring(PRAEFIX.length());
			while ((location.startsWith("/")) || (location.startsWith("\\"))) {
				location = location.substring(1);
			}
			location = location.substring(0, location.indexOf('!'));
		}
		return location;
	}

	/**
	 * @return KnowledgeBaseDescriptor for the given kbid
	 */
	public KnowledgeBaseDescriptor getKnowledgeBaseDescriptor(String kbid) {
		Iterator<KnowledgeBaseDescriptor> iter = kbdescriptors.iterator();
		while (iter.hasNext()) {
			KnowledgeBaseDescriptor desc = iter.next();
			if (desc.getId().equals(kbid)) {
				return (desc);
			}
		}
		return null;
	}

	/**
	 * 
	 * @return java.util.List
	 */
	public List<KnowledgeBaseDescriptor> getKnowledgeBaseDescriptors() {
		return kbdescriptors;
	}

	public void initialize() {
		try {
			descLocation = new URL(ResourceRepository.getInstance()
					.getPropertyPathValue(ResourceRepository.KBDESCRIPTORS_URL));
			load();
		}
		catch (Exception x) {
			logger.error(x);
		}
	}

	/**
	 * @return java.util.List
	 * @param location java.net.URL
	 */
	private void load() {
		kbdescriptors = new LinkedList<KnowledgeBaseDescriptor>();
		if (!(new File(descLocation.getFile()).exists())) {
			return;
		}
		try {

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = dBuilder.parse(InputFilter
					.getFilteredInputSource(descLocation));

			NodeList nl = doc.getElementsByTagName("KnowledgeBase");
			for (int i = 0; i < nl.getLength(); ++i) {
				Node n = nl.item(i);
				String id = n.getAttributes().getNamedItem("ID").getNodeValue();
				String name = n.getAttributes().getNamedItem("name")
						.getNodeValue();
				String type = n.getAttributes().getNamedItem("type")
						.getNodeValue();
				String location = n.getAttributes().getNamedItem("location")
						.getNodeValue();
				String locationType = n.getAttributes().getNamedItem(
						"locationType").getNodeValue();

				KnowledgeBaseDescriptor desc = new KnowledgeBaseDescriptor();
				desc.setId(id);
				desc.setName(name);
				desc.setType(type);

				location = DialogUtils.getRealPath(location);

				// this is needed for JarExtractor
				if (locationType.equalsIgnoreCase("jar")) {
					location = getFileLocation(location);
				}

				desc.setLocation(location);

				desc.setLocationType(locationType);

				kbdescriptors.add(desc);
			}

		}
		catch (Exception x) {
			logger.error("Exception while loading kbDescriptors");
		}

	}

	/**
	 * Removes all KnowledgeBaseDescriptors for the KnowledgeBase with the given
	 * id and renames the kb-jar-File to ".jar.bak".
	 * 
	 * @param kbid
	 */
	public void removeDescriptorByKnowledgeBaseId(String kbid) {
		Iterator<KnowledgeBaseDescriptor> iter = kbdescriptors.iterator();
		while (iter.hasNext()) {
			KnowledgeBaseDescriptor desc = iter.next();
			if (desc.getId().equals(kbid)) {
				DialogUtils.backupFile(desc.getLocation(), desc
						.getLocationType());
				iter.remove();
			}
		}
	}

	/**
	 * Removes all KnowledgeBaseDescriptors for the KnowledgeBase with the given
	 * id.
	 * 
	 * @param kbid
	 */
	public void removeDescriptorOnlyByKnowledgeBaseId(String kbid) {
		Iterator<KnowledgeBaseDescriptor> iter = kbdescriptors.iterator();
		while (iter.hasNext()) {
			KnowledgeBaseDescriptor desc = iter.next();
			if (desc.getId().equals(kbid)) {
				iter.remove();
			}
		}
	}

	public void save() {

		try {

			PrintWriter pw = new PrintWriter(new FileWriter(descLocation
					.getFile()), true);

			pw.println("<?xml version='1.0' encoding='ISO-8859-1'?>\n");

			pw.println("<KnowledgeBases>\n");

			Iterator<KnowledgeBaseDescriptor> iter = kbdescriptors.iterator();
			while (iter.hasNext()) {
				KnowledgeBaseDescriptor desc = iter.next();
				pw.println("<KnowledgeBase ID='" + desc.getId() + "' name='"
						+ desc.getName() + "' type='" + desc.getType()
						+ "' location='"
						+ DialogUtils.getVariablePath(desc.getLocation())
						+ "' locationType='" + desc.getLocationType()
						+ "' />\n");
			}
			pw.println("</KnowledgeBases>\n");

			pw.close();

		}
		catch (Exception x) {
			logger.error("kb-descriptors could not be saved");
		}

	}

	/**
	 * 
	 * @param loc java.net.URL
	 */
	public void setLocationURL(URL loc) {
		descLocation = loc;
	}

}