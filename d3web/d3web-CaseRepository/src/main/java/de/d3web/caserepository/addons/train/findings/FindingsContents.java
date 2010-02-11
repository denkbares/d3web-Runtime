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

/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository.addons.train.findings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.d3web.caserepository.addons.IContents;
import de.d3web.core.terminology.QASet;

/**
 * 24.09.2003 11:16:43
 * 
 * @author hoernlein
 */
public class FindingsContents implements IContents {

	/**
	 * overridden method - this one removes all given Paragraphs for a qaSet and
	 * stores a single new CaseParagraph containing only the text, but no
	 * additional information.
	 * 
	 * @see de.d3web.caserepository.addons.IContents#setContent(de.d3web.core.terminology.QASet,
	 *      java.lang.String)
	 */
	public void setContent(QASet q, String content) {
		List<CaseParagraph> paragraphs = new Vector<CaseParagraph>(1);
		CaseParagraph caseParagraph = new CaseParagraph();
		caseParagraph.addContent(content);
		paragraphs.add(caseParagraph);
		setContent(q, paragraphs);
	}

	/**
	 * This one adds a new paragraph at the end of the list of existing
	 * paragraphs.
	 * 
	 * @param qaSet
	 * @param caseParagraph
	 */
	public void addCaseParagraph(QASet qaSet, CaseParagraph caseParagraph) {
		getCaseParagraphs(qaSet).add(caseParagraph);
	}

	/**
	 * HashMap containing a map of relations from a <code>QContainer</code> to
	 * a <code>List</code> of <code>CaseParagraphs</code> s
	 */
	private Map<QASet, List<CaseParagraph>> c = new HashMap<QASet, List<CaseParagraph>>();
    private Boolean hasFindings = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#getContent(de.d3web.kernel.domainModel.QASet)
	 */
	public List<CaseParagraph> getCaseParagraphs(QASet q) {
        List<CaseParagraph> res = c.get(q);
        if (res == null) {
            res = new LinkedList<CaseParagraph>();
            setContent(q, res);
        }
        return res;
	}

	/**
	 * This implements the getContent method by combining all the textual
	 * information distributed all over the paragraphs.
	 * 
	 * @see de.d3web.caserepository.addons.IContents#getContent(de.d3web.core.terminology.QASet)
	 */
	public String getContent(QASet q) {
		StringBuffer sb = new StringBuffer();
		List<CaseParagraph> paragraphs = getCaseParagraphs(q);
		if (paragraphs != null) {
			Iterator<CaseParagraph> paragraphIter = paragraphs.iterator();
			while (paragraphIter.hasNext()) {
				CaseParagraph caseParagraph = paragraphIter.next();
				sb.append("<p>");
				sb.append(caseParagraph.toString());
				sb.append("</p>");
			}
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.addons.IContents#setContent(de.d3web.kernel.domainModel.QASet,
	 *      java.lang.String)
	 */
	public void setContent(QASet q, List<CaseParagraph> paragraphs) {
        if (q != null)
            c.put(q, paragraphs);
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
			List<CaseParagraph> paragraphs = getCaseParagraphs(q);
			if (paragraphs != null) {
				sb.append("<Content id=\"" + q.getId() + "\">");
				Iterator<CaseParagraph> paragraphIter = paragraphs.iterator();
				while (paragraphIter.hasNext())
					sb.append(paragraphIter.next().getXMLCode());
				sb.append("</Content>\n");
			}
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
		if (obj == null || !(obj instanceof FindingsContents))
			return false;
		if (obj == this)
			return true;

		FindingsContents other = (FindingsContents) obj;
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

    public boolean hasFindings() { 
        if (hasFindings == null) {
            boolean myHasFindings = false;
            for (Iterator viter = c.values().iterator(); viter.hasNext() && !myHasFindings; ) {
                Object o = viter.next();
                if (o instanceof List)
                    for (Iterator iter = ((List) o).iterator(); iter.hasNext() && !myHasFindings; )
                        if (((CaseParagraph) iter.next()).hasRealFindings())
                            myHasFindings = true;
            }
            hasFindings = new Boolean(myHasFindings);
        }
        return hasFindings.booleanValue();
    }

}