/*
 * Created on 11.08.2004 by Chris
 * 
 */
package de.d3web.caserepository.addons.train.findings;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * CaseParagraph
 * 
 * @author Chris 11.08.2004
 */
public class CaseParagraph {

	private List content = null;

	private Boolean hasRealFindings = null;

	public boolean hasRealFindings() {
		if (hasRealFindings == null) {
			hasRealFindings = Boolean.FALSE;
			if (content != null) {
				Iterator iter = content.iterator();
				while (iter.hasNext()) {
					Object element = iter.next();
					if (element instanceof Finding) {
						hasRealFindings = Boolean.TRUE;
						break;
					}
				}
			}
		}
		return hasRealFindings.booleanValue();
	}

	public List getContent() {
		return content;
	}

	public void setContent(List content) {
		this.content = content;
		this.hasRealFindings = null;
	}

	/**
	 *  
	 */
	public CaseParagraph() {
		super();
		content = new Vector();
		this.hasRealFindings = null;
	}

	public void addContent(String contentItem) {
		content.add(contentItem);
	}

	/**
	 * 
	 * DummyFinding
	 * 
	 * @author Chris 13.12.2004
	 */

	public static class DummyFinding extends Finding {

		private String textualContent;

		public DummyFinding(String text) {
			super();
			this.textualContent = text;
		}

		public String getTextualContent() {
			return textualContent;
		}

		public String toString() {
			return textualContent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.d3web.caserepository.addons.IContents#getXMLCode()
		 */
		public String getXMLCode() {
			StringBuffer sb = new StringBuffer();
			sb.append("<DummyFinding>");
			sb.append("<![CDATA[" + getTextualContent() + "]]>");
			sb.append("</DummyFinding>");
			return sb.toString();
		}

		/**
		 * @param textualContent The textualContent to set.
		 */
		public void setTextualContent(String textualContent) {
			this.textualContent = textualContent;
		}
	}

	/**
	 * @param contentItem
	 */
	public void addContent(DummyFinding contentItem) {
		content.add(contentItem);
	}

	public void addContent(Finding contentItem) {
		content.add(contentItem);
		hasRealFindings = Boolean.TRUE;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator iter = content.iterator();
		while (iter.hasNext()) {
			String contentItem = iter.next().toString();
			sb.append(contentItem);
		}
		return sb.toString();
	}

	public boolean isEmpty() {
		return content.isEmpty();
	}

	public Finding getLastFinding() {
		Finding finding = null;
		Iterator iter = content.iterator();
		while (iter.hasNext()) {
			Object contentItem = iter.next();
			if (contentItem instanceof Finding) {
				finding = (Finding) contentItem;
			}
		}
		return finding;
	}

	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		if (content == null || content.isEmpty()) {
			sb.append("<Paragraph/>\n");
		} else {
			sb.append("<Paragraph>\n");
			Iterator iter = content.iterator();
			while (iter.hasNext()) {
				Object item = iter.next();
				if (item instanceof String) {
					sb.append("<![CDATA[");
					sb.append(item);
					sb.append("]]>");
				} else if (item instanceof Finding) {
					Finding finding = (Finding) item;
					sb.append(finding.getXMLCode());
				} else if (item instanceof DummyFinding) {
					sb.append(((DummyFinding) item).getXMLCode());
				} else {
					throw new UnsupportedOperationException(
							"Unhandled entry class: " + item.getClass());
				}
			}
			sb.append("</Paragraph>\n");
		}
		return sb.toString();
	}

	public List<Finding> getFindings() {
		List<Finding> res = new LinkedList<Finding>();
		for (Iterator iter = content.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof Finding)
				res.add((Finding) o);
		}
		return res;
	}

}