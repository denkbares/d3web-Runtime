/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.testcase.persistence;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.CheckTemplate;
import de.d3web.testcase.model.ConditionCheck;
import de.d3web.testcase.model.TransformationException;

/**
 * Template for {@link ConditionCheck}s. The {@link Condition} is stored in its XML form and is transformed into the
 * appropriate Java object using the normal persistence handlers.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 28.10.15
 */
public class ConditionPersistenceCheckTemplate implements CheckTemplate {

	private String conditionXml;
	private Document document = null;

	public ConditionPersistenceCheckTemplate(String conditionXml) {
		this.conditionXml = Objects.requireNonNull(conditionXml);
	}

	@Override
	public Check toCheck(KnowledgeBase knowledgeBase) throws TransformationException {

		Object conditionFragment;
		try {
			Document document = getDocument();
			KnowledgeBasePersistence persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), knowledgeBase, document);
			conditionFragment = persistence.readFragment(document.getDocumentElement());
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			throw new TransformationException(e.getMessage());
		}
		if (conditionFragment instanceof Condition) {
			return new ConditionCheck((Condition) conditionFragment);
		}
		else {
			throw new TransformationException("No valid condition element found xml string: " + conditionXml);
		}
	}

	public Document getDocument() throws ParserConfigurationException, SAXException, IOException {
		if (this.document == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.document = builder.parse(new InputSource(new StringReader(conditionXml)));
		}
		return document;
	}

	public String getConditionXml() {
		return conditionXml;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConditionPersistenceCheckTemplate that = (ConditionPersistenceCheckTemplate) o;
		return conditionXml.equals(that.conditionXml);

	}

	@Override
	public int hashCode() {
		return conditionXml.hashCode();
	}
}
