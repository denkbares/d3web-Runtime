/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.IntegerList;
/**
 * Fragment Handler for IntegerLists
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class IntegerListHandler implements FragmentHandler{

	public final static String TAG = "IntegerList";
	
	@Override
	public boolean canRead(Element element) {
		return (element.getNodeName().equals(TAG));
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof IntegerList);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		List<Integer> list = new LinkedList<Integer>();
		String text = element.getTextContent();
		Iterator<String> iter = Arrays.asList(text.split(" ")).iterator();
		while (iter.hasNext()) {
			list.add(Integer.parseInt(iter.next()));
		}
		return new IntegerList(list);
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Element element = doc.createElement(TAG);
		IntegerList list = (IntegerList) object;
		element.setTextContent(list.toParseableString(" "));
		return element;
	}

}
