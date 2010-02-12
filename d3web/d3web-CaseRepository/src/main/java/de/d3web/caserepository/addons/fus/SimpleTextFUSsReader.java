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
 * Created on 14.04.2004
 */
package de.d3web.caserepository.addons.fus;

import java.util.Arrays;
import java.util.List;

import org.xml.sax.Attributes;

import de.d3web.caserepository.ISolutionContainer;
import de.d3web.caserepository.SolutionContainerImpl;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.io.utilities.XMLTools;

/**
 * SimpleTextFUSsReader (in )
 * de.d3web.caserepository.addons.fus
 * d3web-CaseRepository
 * @author hoernlein
 * @date 14.04.2004
 */
public class SimpleTextFUSsReader extends AbstractTagReader {
    
    protected SimpleTextFUSsReader(String id) { super(id); }
    private static SimpleTextFUSsReader instance;
    private SimpleTextFUSsReader() { this("SimpleTextFUSsReader"); }
    public static AbstractTagReader getInstance() {
        if (instance == null)
            instance = new SimpleTextFUSsReader();
        return instance;
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
     */
    public List getTagNames() {
        return Arrays.asList(new String[] {
                "SimpleTextFUSs",
                "SimpleTextFUS",
                "Name",
                "Text"
        });
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    protected void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("SimpleTextFUSs"))
            startSimpleTextFUSs(attributes);
        else if (qName.equals("SimpleTextFUS"))
            startSimpleTextFUS(attributes);
        else if (qName.equals("Name"))
            ; // do nothing
        else if (qName.equals("Text"))
            ; // do nothing
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    protected void endElement(String uri, String localName, String qName) {
        if (qName.equals("SimpleTextFUSs"))
            endSimpleTextFUSs();
        else if (qName.equals("SimpleTextFUS"))
            endSimpleTextFUS();
        else if (qName.equals("Name"))
            endName();
        else if (qName.equals("Text"))
            endText();
    }

    private SimpleTextFUSs stfs = null;
    
    private void startSimpleTextFUSs(Attributes attributes) {
        stfs = new SimpleTextFUSs();
    }

    private void endSimpleTextFUSs() {
        getCaseObject().setSimpleTextFUSs(stfs);
        stfs = null;
    }

    private String name = null;
    private String text = null;
    private String dtrid = null;
    private String ttrid = null;
    private ISolutionContainer sc = null;
    
    private void startSimpleTextFUS(Attributes attributes) {
        sc = new SolutionContainerImpl();
        setSolutionContainer(sc);
        dtrid = attributes.getValue("dtrid");
        ttrid = attributes.getValue("ttrid");
    }

    private void endSimpleTextFUS() {
        stfs.addSTF(new SimpleTextFUS(name, text, sc, dtrid, ttrid));
        name = null;
        text = null;
        dtrid = null;
        ttrid = null;
        sc = null;
    }

    private void endName() {
        if (stfs != null)
            name = XMLTools.prepareFromCDATA(getTextBetweenCurrentTag());
    }

    private void endText() {
        if (stfs != null)
            text = XMLTools.prepareFromCDATA(getTextBetweenCurrentTag());
    }

}
