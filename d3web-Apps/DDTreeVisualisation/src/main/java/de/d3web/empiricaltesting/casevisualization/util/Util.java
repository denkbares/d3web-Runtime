/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.empiricaltesting.casevisualization.util;

import java.util.List;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 22.07.2013
 */
public class Util {

	// TODO: Nicht nur eine Antwort (auf eine Frage) sondern mehrere
	// Antworten auf mehrere (erste) Fragen möglich
	public static TestCase getPartiallyAnsweredSuite(Choice answer, List<SequentialTestCase> repository) {
		TestCase ret = new TestCase();
		for (SequentialTestCase stc : repository) {
			if (stc.getCases().get(0).getFindings().get(0).getValue().equals(answer)) ret.getRepository().add(
					stc);
		}
		return ret;
	}
}
