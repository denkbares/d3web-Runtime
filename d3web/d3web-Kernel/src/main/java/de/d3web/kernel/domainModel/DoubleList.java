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
