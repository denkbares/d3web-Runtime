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

package de.d3web.dialog2.component.html;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

import de.d3web.core.session.XPSCase;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.render.DialogRenderUtils;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.psMethods.compareCase.CompareCaseException;
import de.d3web.kernel.psMethods.compareCase.comparators.CompareMode;
import de.d3web.kernel.psMethods.compareCase.facade.ComparisonResultRepository;
import de.d3web.kernel.psMethods.compareCase.facade.SimpleResult;

public class UICompareCaseBox extends UIOutput {

    public static final String COMPONENT_TYPE = "de.d3web.dialog2.CompareCaseBox";

    private static final String DEFAULT_RENDERER_TYPE = null;

    public static Logger logger = Logger.getLogger(UICompareCaseBox.class);

    public UICompareCaseBox() {
	setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
	ResponseWriter writer = context.getResponseWriter();

	if (isRenderable()) {
	    DialogRenderUtils.renderTableWithClass(writer, this, "panelBox");
	    writer.writeAttribute("id", this.getClientId(context), "id");
	    writer.startElement("tr", this);
	    writer.startElement("th", this);
	    writer.writeText(DialogUtils.getMessageFor("cbr.title"), "value");
	    writer.endElement("th");
	    writer.endElement("tr");

	    writer.startElement("tr", this);
	    writer.startElement("td", this);

	    writer.startElement("a", this);
	    writer.writeAttribute("onclick", "openCCPopup(); return false;",
		    "onclick");
	    writer.writeAttribute("href", "#", "href");
	    writer.writeAttribute("id", "start_cc", "id");
	    writer.writeAttribute("title", DialogUtils
		    .getMessageFor("cbr.description"), "title");
	    writer.writeText(DialogUtils.getMessageFor("cbr.description"),
		    "value");
	    writer.endElement("a");

	    writer.endElement("td");
	    writer.endElement("tr");

	    writer.endElement("table");
	}

    }

    @SuppressWarnings( { "deprecation", "unchecked" })
    public boolean isRenderable() {
	XPSCase theCase = DialogUtils.getDialog().getTheCase();
	ComparisonResultRepository crepos = new ComparisonResultRepository();
	crepos.setCurrentCase(theCase);
	crepos.setCompareMode(CompareMode.BOTH_FILL_UNKNOWN);
	List<SimpleResult> cases = null;
	try {
	    cases = crepos.getSimpleResults(CaseManager.getInstance()
		    .getCasesForKb(theCase.getKnowledgeBase().getId()));
	} catch (CompareCaseException e) {
	    logger.error(e);
	}
	if ((cases != null) && !cases.isEmpty()) {
	    return true;
	}
	return false;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
    }

    @Override
    public Object saveState(FacesContext context) {
	Object values[] = new Object[1];
	values[0] = super.saveState(context);
	return ((values));
    }

}