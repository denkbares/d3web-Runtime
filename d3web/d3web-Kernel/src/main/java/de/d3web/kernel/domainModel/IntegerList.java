package de.d3web.kernel.domainModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntegerList extends AbstractNumberList{
	
	public IntegerList() {
		super();
	}
	
	public IntegerList(Collection c) {
		super(c);
		
	}
	
	public Integer get(int i) {
		return (Integer)values.get(i);
	}
	
	
	
	public void add(Number o) {
		if(o instanceof Integer) {
			values.add(o);
		}
		else {
			String s = "Object passed to IntegerList.add()"+
			" not instanceof Integer!";
			Logger.getLogger(IntegerList.class.getName()).log(Level.WARNING, IntegerList.class.getName(), s);
		}
		
	}
	
	public String toString() {
		String s = "{ ";
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			 Integer element = (Integer) iter.next();
			s += element.toString();
			s += " ";
		}
		
		s += " }";
		return s;
	}
	
	
	

}
