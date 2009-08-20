package de.d3web.kernel.domainModel;
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
