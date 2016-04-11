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

package de.d3web.testcase.model;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * Template representing a {@link Finding} to be used in a {@link TestCase}. Use method {@link
 * #toFinding(KnowledgeBase)} to get a {@link Finding} working specifically with the given knowledge base.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 28.10.15
 */
public interface FindingTemplate {

	Finding toFinding(KnowledgeBase knowledgeBase) throws TransformationException;

}
