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

package de.d3web.core.terminology.info;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class AbstractNumberList {
	
	protected List<Number> values ;

	public AbstractNumberList() {
		values = new LinkedList<Number>();
	}
	
	
	public AbstractNumberList(Collection c) {
		values = new LinkedList();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if(element instanceof Number) {
				add((Number) element);
			}
			else {
				//log problem
				String s = "Object in Collection passed to AbstractNumberList-Constructor"+
				" not instanceof Number!";
					
				Logger.getLogger(IntegerList.class.getName()).log(Level.WARNING, AbstractNumberList.class.getName(), s);
			}
		}
	}


	public void clear() {
		values.clear();
	}
	
	public void remove(int i) {
		values.remove(i);
	}
	
	public int size(){
		return values.size();
	}
	
	public String toParseableString(String separator) {		
		if(size() == 0) {
			return "";
		}
		StringBuffer buffy = new StringBuffer();
		for(int i = 0; i < size()-1; i++ ) {
			Number aValue = get(i);
			buffy.append(aValue.toString());
			buffy.append(separator);
		}
		Number lastValue = get(size()-1);
		buffy.append(lastValue.toString());
	
		return buffy.toString();
	}
	
	abstract public Number get(int i);
	abstract public void add(Number number);
}
