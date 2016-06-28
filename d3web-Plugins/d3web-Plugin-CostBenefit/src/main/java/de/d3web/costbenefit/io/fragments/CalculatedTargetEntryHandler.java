/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit.io.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.costbenefit.session.protocol.CalculatedTargetEntry;
import de.d3web.costbenefit.session.protocol.CalculatedTargetEntry.Target;
import de.d3web.strings.Strings;

/**
 * Saves and loads {@link CalculatedTargetEntry}s
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 11.09.2014
 */
public class CalculatedTargetEntryHandler implements FragmentHandler<KnowledgeBase> {

	private static final String COSTBENEFIT = "costbenefit";
	private static final String ELEMENT_NAME = "entry";
	private static final String ELEMENT_TYPE = "calculatedTarget";
	private static final String ATTR_DATE = "date";
	private static final String TARGETS = "targets";
	private static final String CALCULATEDTARGET = "calculatedTarget";
	private static final String QCONTAINER = "qcontainer";
	private static final String SOLUTION = "solution";
	private static final String SPRINT_GROUP = "sprintGroup";
	private static final String BENEFIT = "benefit";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		try {
			String dateString = element.getAttribute(ATTR_DATE);
			Date date = Strings.readDate(dateString);
			List<Element> elementList = XMLUtil.getElementList(element.getChildNodes());
			if (elementList.size() < 2
					|| !elementList.get(0).getNodeName().equals(CALCULATEDTARGET)
					|| !elementList.get(1).getNodeName().equals(TARGETS)) {
				throw new IOException(
						"Element must have two ore more children, the first ones must be named "
								+ CALCULATEDTARGET + " and " + TARGETS);
			}
			Target calculatedTarget = readTarget(XMLUtil.getElementList(
					elementList.get(0).getChildNodes()).get(0));
			List<Element> grandChildren = XMLUtil.getElementList(elementList.get(1).getChildNodes());
			Set<Target> targets = new HashSet<>();
			for (Element grandChild : grandChildren) {
				targets.add(readTarget(grandChild));
			}
			Set<String> solutions = new HashSet<>();
			if (elementList.size() >= 3) {
				if (elementList.get(2).getNodeName().equals(SPRINT_GROUP)) {
					for (Element e : XMLUtil.getElementList(elementList.get(2).getChildNodes())) {
						solutions.add(e.getTextContent());
					}
				}
				else {
					throw new IOException("The third element must be named " + SPRINT_GROUP);
				}
			}
			return new CalculatedTargetEntry(calculatedTarget, targets, date, solutions);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CalculatedTargetEntry entry = (CalculatedTargetEntry) object;
		String dateString = Strings.writeDate(entry.getDate());
		Element e = persistence.getDocument().createElement(ELEMENT_NAME);
		e.setAttribute("type", ELEMENT_TYPE);
		e.setAttribute(ATTR_DATE, dateString);
		Element calculatedTarget = persistence.getDocument().createElement(CALCULATEDTARGET);
		calculatedTarget.appendChild(writeTarget(entry.getCalculatedTarget(),
				persistence.getDocument()));
		e.appendChild(calculatedTarget);
		Element targets = persistence.getDocument().createElement(TARGETS);
		e.appendChild(targets);
		for (Target target : entry.getTargets()) {
			targets.appendChild(writeTarget(target, persistence.getDocument()));
		}
		if (!entry.getSprintGroup().isEmpty()) {
			Element sprintGroupElement = persistence.getDocument().createElement(SPRINT_GROUP);
			for (String solution : entry.getSprintGroup()) {
				Element solutionElement = persistence.getDocument().createElement(SOLUTION);
				solutionElement.setTextContent(solution);
				sprintGroupElement.appendChild(solutionElement);
			}
			e.appendChild(sprintGroupElement);
		}
		return e;
	}

	private static Element writeTarget(Target target, Document document) {
		Element element = document.createElement("target");
		element.setAttribute(BENEFIT, Double.toString(target.getBenefit()));
		if (target.getCostbenefit() != Float.MAX_VALUE) {
			element.setAttribute(COSTBENEFIT, Double.toString(target.getCostbenefit()));
		}
		for (String qcontainer : target.getqContainerNames()) {
			Element qcontainerElement = document.createElement(QCONTAINER);
			qcontainerElement.setTextContent(qcontainer);
			element.appendChild(qcontainerElement);
		}
		return element;
	}

	private static Target readTarget(Element targetElement) throws IOException {
		String benefitString = targetElement.getAttribute(BENEFIT);
		String costBenefitString = targetElement.getAttribute(COSTBENEFIT);
		Set<String> qcontainer = new HashSet<>();
		for (Element qContainerElement : XMLUtil.getElementList(targetElement.getChildNodes())) {
			qcontainer.add(qContainerElement.getTextContent());
		}
		try {
			if (costBenefitString.isEmpty()) {
				return new Target(qcontainer, Double.parseDouble(benefitString));
			}
			else {
				return new Target(qcontainer, Double.parseDouble(benefitString),
						Double.parseDouble(costBenefitString));
			}
		}
		catch (NumberFormatException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, ELEMENT_NAME, ELEMENT_TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof CalculatedTargetEntry;
	}

}
