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

/*
 * Created on 14.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.core.session.values;

import java.util.Date;

import de.d3web.core.session.Session;

/**
 * This interface describes an evaluatable value of AnswerDate objects.
 * The evaluation-type is ´Date´. 
 * Creation date: (14.12.2000 14:10:35)
 * @author Tobias Vogele
 */
public interface EvaluatableAnswerDateValue extends EvaluatableDateValue{

	/**
	 * Evaluates its value considering the given XPSCase.
	 * @return evaluated AnswerDateValue (Date)
	 */
	public Date eval(Session theCase);
}
