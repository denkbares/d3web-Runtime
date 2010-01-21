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

package de.d3web.kernel.supportknowledge;
import java.util.Iterator;
import java.util.logging.Logger;


/**
 * Storing descriptor and content.
 * @author Christian Betz, hoernlein
 */
public class MMInfoObject implements DCMarkedUp, java.io.Serializable {
	
	private static final long serialVersionUID = -2724066948400335342L;
	private DCMarkup dcData;
	private String content;

	public MMInfoObject(DCMarkup dcData, String content) {
		setDCMarkup(dcData);
		setContent(content);
	}

	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	/**
	 * @return true, iff all content in dcData equals to content in this.getDCData
	 * example:
	 * 
	 * 	this->DCData	DCElement.SUBJECT = "foo"
	 * 						DCElement.LANGUAGE = "de"
	 * 
	 * 	dcData			DCElement.LANGUAGE = "de"
	 * 
	 * -> match!
	 * 
 	 * 	this->DCData	DCElement.LANGUAGE = "de"
	 * 
	 * 	dcData			DCElement.SUBJECT = "foo"
	 * 						DCElement.LANGUAGE = "de"
	 * 
	 * -> no match!
	 * 
	 */
	public boolean matches(DCMarkup dcData) {
		Iterator<DCElement> iter = DCElement.getIterator();
		while (iter.hasNext()) {
			DCElement dc = iter.next();
			String content = dcData.getContent(dc);
			if (content != null && !content.equals("") && this.getDCMarkup() != null &&
				!content.equalsIgnoreCase(this.getDCMarkup().getContent(dc)))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.misc.DCDataAdapter#getDCData()
	 */
	public DCMarkup getDCMarkup() {
		return dcData;
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.misc.DCDataAdapter#setDCData(de.d3web.kernel.misc.DCData)
	 */
	public void setDCMarkup(DCMarkup dcData) {
		String subject = dcData.getContent(DCElement.SUBJECT);

		// [MISC]:aha:legacy code
		if ("therapy".equals(subject))
		    subject = MMInfoSubject.THERAPY.getName();
		else if ("info.suggestion".equals(subject))
		    subject = MMInfoSubject.THERAPY.getName();
		
		Iterator<MMInfoSubject> iter = MMInfoSubject.getIterator();
		while (iter.hasNext())
			if (subject.equals((iter.next()).getName())) {
				this.dcData = dcData;
				return;
			}

		Logger.getLogger(this.getClass().getName()).warning(subject + " is not valid as DCElement.SUBJECT!");
		return;
		// throw new NullPointerException();
	}
}