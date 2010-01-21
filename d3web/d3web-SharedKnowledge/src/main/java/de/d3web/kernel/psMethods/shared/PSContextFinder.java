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

package de.d3web.kernel.psMethods.shared;
import java.util.Hashtable;

import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorYN;
/**
 * Helper class that saves Classes and their PSMethodContext
 * Creation date: (14.08.2001 14:55:11)
 * @author: Norman Brümmer
 */
public class PSContextFinder {
	private Abnormality abnorm = null;
	private Weight weight = null;
	private QuestionComparator qcomp = null;

	private Hashtable<Class<?>, Class<? extends PSMethod>> contextHash = null;

	private static PSContextFinder instance = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (14.08.2001 15:01:28)
	 */
	private PSContextFinder()
{
		abnorm = new Abnormality();
		qcomp = new QuestionComparatorYN();
		weight = new Weight();

		contextHash = new Hashtable<Class<?>, Class<? extends PSMethod>>();
		contextHash.put(Abnormality.class, abnorm.getProblemsolverContext());
		contextHash.put(QuestionComparator.class, qcomp.getProblemsolverContext());
		contextHash.put(Weight.class, weight.getProblemsolverContext());
	}

	/**
	 * finds the psmethod context to the given knowledgeslice class
	 * Creation date: (14.08.2001 15:01:09)
	 * @return java.lang.Class
	 * @param knowledgeSliceClass java.lang.Class
	 */
	public Class<?> findPSContext(Class<?> knowledgeSliceClass)
{
		return contextHash.get(knowledgeSliceClass);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (14.08.2001 15:17:52)
	 * @return de.d3web.kernel.psMethods.shared.PSContextFinder
	 */
	public static PSContextFinder getInstance()
{
		if (instance == null)
		{
			instance = new PSContextFinder();
		}
		return instance;
	}
}