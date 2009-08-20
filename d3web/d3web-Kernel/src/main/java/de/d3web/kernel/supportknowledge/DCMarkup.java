package de.d3web.kernel.supportknowledge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DCMarkup is for characterising an object according to the Dublin Core
 * @link http://dublincore.org/
 * @see de.d3web.kernel.supportknowledge.DCMarkedUp
 * @author hoernlein
 */
public class DCMarkup implements java.io.Serializable {
	
	private Map data = new HashMap(15);
	
	/**
	 * if content == null the saved content is ""
	 * @param dc DCElement
	 * @param content String
	 * @throws NullPointerException iff dc == null
	 */
	public void setContent(DCElement dc, String content) {
		if (dc == null) throw new NullPointerException();
		if (content == null) content = "";
		data.put(dc, content);
	}

	/**
	 * if content == null the returned content is ""
	 * @param dc DCElement
	 * @throws NullPointerException iff dc == null
	 */
	public String getContent(DCElement dc) {
		if (dc == null) throw new NullPointerException();
		String result = (String) data.get(dc);
		if (result == null) result = "";
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DCMarkup)) return false;
		Iterator iter = DCElement.getIterator();
		while (iter.hasNext()) {
			DCElement dc = (DCElement) iter.next();
			if (!((DCMarkup) obj).getContent(dc).equals(this.getContent(dc))) return false;
		}
		return true;
	}
	
	/**
	 * Returns a new instance of DCMarkup which with the same values
	 * as this instance.
	 */
	public Object clone() {
		DCMarkup clonedDC = new DCMarkup();
		Iterator iter = data.keySet().iterator();
		while (iter.hasNext()) {
			DCElement dc = (DCElement) iter.next();
			clonedDC.setContent(dc, new String(getContent(dc)));
		}
		return clonedDC;
	}

}
