/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.interviewmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;

public class OneQuestionFormStrategy implements FormStrategy {

	@Override
	public Form nextForm(List<InterviewObject> agendaEnties) {
		if (agendaEnties.isEmpty()) {
			return EmptyForm.getInstance();
		}
		else {
			InterviewObject object = agendaEnties.get(0);
			if (object instanceof Question) {
				List<InterviewObject> interviewObjects = Arrays.asList(object);
				return new DefaultForm(((Question)object).getName(), interviewObjects);
			}
			else if (object instanceof QASet) {
				String title = ((QContainer)object).getName();
				return new DefaultForm(title, getFirstActiveQContainerWithChildren((QASet)object));
			}
			return null;			
		}
	}

	/**
	 * Traverses the specified qaset and returns the list of questions contained in the 
	 * qaset, that are active and contained in a sub-qcontainer.
	 * @param qaset the specified {@link QASet} instance
	 * @return the list of questions, contained in the first and active qcontainer, where the specified qaset is a parent 
	 */
	@SuppressWarnings("unchecked")
	private List<InterviewObject> getFirstActiveQContainerWithChildren(
			TerminologyObject qaset) {
		// Return an empty, when qaset has no children
		if (qaset == null || qaset.getChildren().length == 0) return Collections.EMPTY_LIST;
		
		InterviewObject firstItem = (InterviewObject)qaset.getChildren()[0];
		if (firstItem instanceof Question) {
			return flatten(qaset.getChildren());
		}
		else if (firstItem instanceof QContainer) {
			return getFirstActiveQContainerWithChildren(qaset.getChildren()[0]);
		}
		else {
			// TODO: throw a nice exception here
			System.err.println("UNKNOWN QASET passed: " + qaset);
			return Collections.EMPTY_LIST;
		}
	}

	private List<InterviewObject> flatten(TerminologyObject[] objects) {
		List<InterviewObject> flattenedList = new ArrayList<InterviewObject>(objects.length);
		for (TerminologyObject object : objects) {
			flattenedList.addAll(flatten(object)); 
		}
		return flattenedList;
	}

	private List<InterviewObject> flatten(TerminologyObject object) {
		List<InterviewObject> flattenedList = new ArrayList<InterviewObject>();
		flattenedList.add((InterviewObject)object);
		for (TerminologyObject child: object.getChildren()) {
			flattenedList.addAll(flatten(child)); 
		}
		return flattenedList;
	}

}
