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

package de.d3web.core.knowledge.terminology;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jochen
 * 
 *         Question Zero Choice
 * 
 *         This QuestionChoice is restricted to have NO answers. Its not a real
 *         Question-type but only used to attach texts. In the dialog those can
 *         be rendered to guide the user through the dialog without answer to
 *         click.
 * 
 */
public class QuestionZC extends QuestionOC {

	public static final String XML_IDENTIFIER = "Info";

	public QuestionZC(String id) {
		super(id);
	}

	@Override
	public List<Choice> getAllAlternatives() {
		return new ArrayList<Choice>();
	}

	@Override
	public void setAlternatives(List<Choice> l) {
		if (l.size() > 0) {
			Logger.getLogger(this.getClass().getName()).severe(
					"Tried to set AnswerAlternatives for QuestionZC");
		}
	}

	@Override
	public void addAlternative(Choice a) {
		Logger.getLogger(this.getClass().getName()).severe(
				"Tried to add AnswerAlternative for QuestionZC");
	}
}
