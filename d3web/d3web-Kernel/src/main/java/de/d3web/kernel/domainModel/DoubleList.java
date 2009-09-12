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

package de.d3web.kernel.domainModel;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoubleList extends AbstractNumberList {

	
	public DoubleList() {
		super();
	}
	
	public DoubleList(Collection c) {
		super(c);
		
	}
	
	public Double get(int i) {
		return (Double)values.get(i);
	}

	public void add(Number o) {
		if(o instanceof Double) {
			values.add(o);
		}
		else {
			String s = "Object passed to IDoubleList.add()"+
			" not instanceof Double!";
			Logger.getLogger(IntegerList.class.getName()).log(Level.WARNING, DoubleList.class.getName(), s);
		}
		
	}

}
