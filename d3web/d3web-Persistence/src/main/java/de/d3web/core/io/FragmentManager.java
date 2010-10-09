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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.Extension;

/**
 * This is a utility class to write and read fragments to/from xml documents
 * using Extensions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 20.09.2010
 */
public class FragmentManager {

	protected Extension[] fragmentPlugins;

	/**
	 * This method is used to create an XML element ({@link Document})for the
	 * specified object using the {@link FragmentHandler} with the highest
	 * priority who can create the element.
	 * 
	 * @param object the specified object
	 * @param doc the specified XML element, in which the element should be
	 *        created
	 * @return the {@link Element} representing the input object
	 * @throws NoSuchFragmentHandlerException if no appropriate
	 *         {@link FragmentHandler} is available for the specified object
	 * @throws IOException if an error occurs during saving the specified object
	 */
	public Element writeFragment(Object object, Document doc) throws NoSuchFragmentHandlerException, IOException {
		for (Extension plugin : fragmentPlugins) {
			FragmentHandler handler = (FragmentHandler) plugin.getSingleton();
			if (handler.canWrite(object)) {
				return handler.write(object, doc);
			}
		}
		throw new NoSuchFragmentHandlerException("No fragment handler found for: " + object);
	}

	/**
	 * Reads the specified XML {@link Element} and creates its corresponding
	 * object. For this operation, the {@link FragmentHandler} with the highest
	 * priority and ability to handle the element is used. The specified
	 * {@link KnowledgeBase} instance is used to retrieve the appropriate object
	 * instances.
	 * 
	 * @param child the specified XML Element
	 * @param knowledgeBase the specified knowledge base
	 * @return the created object
	 * @throws NoSuchFragmentHandlerException if no appropriate
	 *         {@link FragmentHandler} is available
	 * @throws IOException if an IO error occurs during the read operation
	 */
	public Object readFragment(Element child, KnowledgeBase knowledgeBase) throws NoSuchFragmentHandlerException, IOException {
		for (Extension plugin : fragmentPlugins) {
			FragmentHandler handler = (FragmentHandler) plugin.getSingleton();
			if (handler.canRead(child)) {
				return handler.read(knowledgeBase, child);
			}
		}
		throw new NoSuchFragmentHandlerException("No fragment handler found for: " + child);
	}
}