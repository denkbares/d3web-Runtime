/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository.addons.train;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import de.d3web.caserepository.addons.IContents;
import de.d3web.kernel.domainModel.QASet;

/**
 * 24.09.2003 11:16:43
 * 
 * @author hoernlein
 */
public class Contents implements IContents {

	/**
	 * 
	 * @deprecated: use FindingsContents instead
	 */
	public Contents() { /* ... */ }

	private Map<QASet, String> c = new HashMap<QASet, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#getContent(de.d3web.kernel.domainModel.QASet)
	 */
	public String getContent(QASet q) {
		return c.get(q);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#setContent(de.d3web.kernel.domainModel.QASet,
	 *      java.lang.String)
	 */
	public void setContent(QASet q, String content) {
        if (q != null)
            c.put(q, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#hasContent(de.d3web.kernel.domainModel.QASet)
	 */
	public boolean hasContent(QASet q) {
		return c.get(q) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#getAllWithContent()
	 */
	public Set<QASet> getAllWithContent() {
        Set<QASet> res = new HashSet<QASet>();
        for (QASet q : c.keySet())
            if (getContent(q) != null && !getContent(q).trim().equals("")) // [MISC]:aha:why do I have to check this??
                res.add(q);
        return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Contents>\n");
		Iterator<QASet> iter = getAllWithContent().iterator();
		while (iter.hasNext()) {
			QASet q = iter.next();
			sb.append("<Content id=\"" + q.getId() + "\"><![CDATA["
					+ getContent(q) + "]]></Content>\n");
		}
		sb.append("</Contents>\n");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Contents))
			return false;
		if (obj == this)
			return true;

		Contents other = (Contents) obj;
		if (!c.keySet().containsAll(other.c.keySet())
				|| !other.c.keySet().containsAll(c.keySet()))
			return false;

		Iterator iter = c.keySet().iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (!c.get(o).equals(other.c.get(o)))
				return false;
		}

		return true;
	}

}