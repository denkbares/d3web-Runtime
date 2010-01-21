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
package de.d3web.core.kpers.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * Interface for handlers of fragments.
 * They are used to read/write similar elements in different knowledge readers/writers. 
 * @author Markus Friedrich (denkbares GmbH)
 */
public interface FragmentHandler {

	/**
	 * Reads an xml element and creates the object in represents.
	 * @param kb Knowledgebase in which the object should be created
	 * @param element xml element containing the representation of the object
	 * @return object represented by the xml element
	 * @throws IOException if an error occurs
	 */
	public Object read(KnowledgeBase kb, Element element) throws IOException;
	
	/**
	 * Creates an xml element as a representation of the given object
	 * @param object object which should be represented
	 * @param doc Document in which the element should be created
	 * @return an xml element representing the object
	 * @throws IOException if an error occurs
	 */
	public Element write(Object object, Document doc) throws IOException;
	
	/**
	 * Checks if this handler can read the element
	 * @param element to be read
	 * @return true, if it can be read, false otherwise
	 */
	public boolean canRead(Element element);
	
	/**
	 * Checks if this hanlder can write the object
	 * @param object to be written
	 * @return true, if it can be written, false otherwise
	 */
	public boolean canWrite(Object object);
}
