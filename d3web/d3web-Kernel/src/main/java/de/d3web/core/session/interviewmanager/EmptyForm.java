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

import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;

/**
 * Null object to represent an empty form, where no
 * questions are presented in the dialog.
 * 
 * @author joba
 *
 */
public class EmptyForm implements Form {

	private static final Form instance = new EmptyForm();
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InterviewObject> getInterviewObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getTitle() {
		return "EMPTY";
	}

	public static Form getInstance() {
		return instance;
	}

}
