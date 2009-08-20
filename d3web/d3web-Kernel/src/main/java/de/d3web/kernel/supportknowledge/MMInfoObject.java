package de.d3web.kernel.supportknowledge;
import java.util.Iterator;
import java.util.logging.Logger;


/**
 * Storing descriptor and content.
 * @author Christian Betz, hoernlein
 */
public class MMInfoObject implements DCMarkedUp, java.io.Serializable {
	
	private DCMarkup dcData;
	private String content;

	public MMInfoObject(DCMarkup dcData, String content) {
		setDCDMarkup(dcData);
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
		Iterator iter = DCElement.getIterator();
		while (iter.hasNext()) {
			DCElement dc = (DCElement) iter.next();
			String content = dcData.getContent(dc);
			if (content != null && !content.equals("") && this.getDCMarkup() != null &&
				!content.equals(this.getDCMarkup().getContent(dc)))
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
	public void setDCDMarkup(DCMarkup dcData) {
		String subject = dcData.getContent(DCElement.SUBJECT);

		// [MISC]:aha:legacy code
		if ("therapy".equals(subject))
		    subject = MMInfoSubject.THERAPY.getName();
		else if ("info.suggestion".equals(subject))
		    subject = MMInfoSubject.THERAPY.getName();
		
		Iterator iter = MMInfoSubject.getIterator();
		while (iter.hasNext())
			if (subject.equals(((MMInfoSubject) iter.next()).getName())) {
				this.dcData = dcData;
				return;
			}

		Logger.getLogger(this.getClass().getName()).warning(subject + " is not valid as DCElement.SUBJECT!");
		return;
		// throw new NullPointerException();
	}
}