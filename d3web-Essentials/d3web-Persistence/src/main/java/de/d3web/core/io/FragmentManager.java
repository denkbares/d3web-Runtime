/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.io;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import com.denkbares.plugin.Extension;
import com.denkbares.plugin.PluginManager;
import com.denkbares.strings.Strings;

/**
 * This is a utility class to write and read fragments to/from xml documents
 * using Extensions
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 20.09.2010
 */
public class FragmentManager<Artifact> {

	private Extension[] fragmentPlugins;

	/**
	 * This method is used to create an XML element ({@link Document})for the
	 * specified object using the {@link FragmentHandler} with the highest
	 * priority who can create the element.
	 *
	 * @param object the specified object
	 * @return the {@link Element} representing the input object
	 * @throws NoSuchFragmentHandlerException if no appropriate
	 *                                        {@link FragmentHandler} is available for the specified object
	 * @throws IOException                    if an error occurs during saving the specified object
	 */
	public Element writeFragment(Object object, Persistence<Artifact> persistence) throws IOException {
		for (Extension plugin : fragmentPlugins) {
			@SuppressWarnings("unchecked")
			FragmentHandler<Artifact> handler = (FragmentHandler<Artifact>) plugin.getSingleton();
			if (handler.canWrite(object)) {
				return handler.write(object, persistence);
			}
		}
		throw new NoSuchFragmentHandlerException("No fragment handler found for: '" + object + "' ."
				+ " Very likely a plugin is missing which was used while creating this knowledge base.");
	}

	/**
	 * Reads the specified XML {@link Element} and creates its corresponding
	 * object. For this operation, the {@link FragmentHandler} with the highest
	 * priority and ability to handle the element is used. The specified
	 * {@link KnowledgeBase} instance is used to retrieve the appropriate object
	 * instances.
	 *
	 * @param child the specified XML Element
	 * @return the created object
	 * @throws NoSuchFragmentHandlerException if no appropriate
	 *                                        {@link FragmentHandler} is available
	 * @throws IOException                    if an IO error occurs during the read operation
	 */
	public Object readFragment(Element child, Persistence<Artifact> persistence) throws IOException {
		for (Extension plugin : fragmentPlugins) {
			@SuppressWarnings("unchecked")
			FragmentHandler<Artifact> handler = (FragmentHandler<Artifact>) plugin.getSingleton();
			if (handler.canRead(child)) {
				return handler.read(child, persistence);
			}
		}
		throw new NoSuchFragmentHandlerException("No fragment handler found for element: '" + getElementVerbalization(child)
				.trim() + "'. Very likely a plugin is missing which was used to while creating this knowledge base.");
	}

	@NotNull
	private String getElementVerbalization(Element child) {
		String textContent = child.getTextContent();
		if (Strings.isBlank(textContent)) {
			StringBuilder builder = new StringBuilder(child.getTagName());
			NamedNodeMap attributes = child.getAttributes();
			if (attributes.getLength() > 0) builder.append(": ");
			for (int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				builder.append(item.getNodeName()).append("=").append(item.getNodeValue());
				if (i < attributes.getLength() - 1) builder.append(", ");
			}
			textContent = builder.toString();
		}
		return textContent;
	}

	/**
	 * Initializes this manager with all extensions of a specific plugin / point
	 * id.
	 *
	 * @param extendedPluginID the plugin id of the extensions point to get the
	 *                         {@link FragmentHandler}s for
	 * @param extendedPointID  the point id of the extensions point to get the
	 *                         {@link FragmentHandler}s for
	 * @created 26.11.2013
	 */
	public void init(String extendedPluginID, String extendedPointID) {
		PluginManager manager = PluginManager.getInstance();
		this.fragmentPlugins = manager.getExtensions(extendedPluginID, extendedPointID);
	}
}