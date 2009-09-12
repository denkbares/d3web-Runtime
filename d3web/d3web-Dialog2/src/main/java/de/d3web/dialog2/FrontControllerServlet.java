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

package de.d3web.dialog2;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.d3web.dialog2.basics.knowledge.KnowledgeBaseRepository;
import de.d3web.dialog2.controller.KBLoadController;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;

public class FrontControllerServlet extends HttpServlet {

    private static final long serialVersionUID = -8843390105172759363L;

    private void doAnswerFrequentness(FacesContext jsfContext, String kbid)
	    throws IOException {
	// invalidate the old session...
	doRestart(jsfContext);
	// check if kb with this id is available...
	WebDialog dia = new WebDialog();
	if (KnowledgeBaseRepository.getInstance().containsId(kbid)) {
	    DialogUtils.setExpression(dia, "#{webDialog}");
	    // save WebDialog Instance in Session...
	    (jsfContext.getExternalContext().getSessionMap()).put("webDialog",
		    dia);

	    KBLoadController kbLoadBean = new KBLoadController();
	    DialogUtils.setExpression(kbLoadBean, "#{kbLoadBean}");
	    // put kbLoadBean into requestmap... (otherwise the kb will not be
	    // loaded if an old bean is in request scope
	    (jsfContext.getExternalContext().getSessionMap()).put("kbLoadBean",
		    kbLoadBean);

	    kbLoadBean.setKbID(kbid);
	    kbLoadBean.loadKB();
	}

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	FacesContext context = FacesContext.getCurrentInstance();
	HttpServletResponse facesResp = (HttpServletResponse) context
		.getExternalContext().getResponse();
	if (req.getParameter("restart") != null) {
	    doRestart(context);
	    facesResp.sendRedirect(req.getContextPath() + "/dialog.jsf");
	} else if (req.getParameter("ansfreq") != null) {
	    doAnswerFrequentness(context, req.getParameter("ansfreq"));
	    facesResp.sendRedirect(req.getContextPath()
		    + "/answerFrequentness.jsf");
	} else if (req.getParameter("kbid") != null) {
	    doKbid(context, req.getParameter("kbid"));
	    facesResp.sendRedirect(req.getContextPath() + "/dialog.jsf");
	} else if (req.getParameter("id") != null) {
	    doKnowWE(req, context, req.getParameter("id"));
	} else {
	    facesResp.sendRedirect(req.getContextPath() + "/dialog.jsf");
	}
    }

    private void doKbid(FacesContext jsfContext, String kbid)
	    throws IOException {
	// check if kb with this id is available...
	WebDialog dia = new WebDialog();
	if (KnowledgeBaseRepository.getInstance().containsId(kbid)) {
	    DialogUtils.setExpression(dia, "#{webDialog}");
	    // save WebDialog Instance in Session...
	    (jsfContext.getExternalContext().getSessionMap()).put("webDialog",
		    dia);

	    KBLoadController kbLoadBean = new KBLoadController();
	    DialogUtils.setExpression(kbLoadBean, "#{kbLoadBean}");
	    // put kbLoadBean into requestmap... (otherwise the kb will note be
	    // loaded if an old bean is in request scope
	    (jsfContext.getExternalContext().getSessionMap()).put("kbLoadBean",
		    kbLoadBean);

	    kbLoadBean.setKbID(kbid);
	    kbLoadBean.loadKB();
	}
    }

    private void doKnowWE(HttpServletRequest req,
	    FacesContext jsfContext, String id) throws IOException {
	// HACK: make sure that all contexts are crossContext-enabled..
	// open server.xml in tomcat config folder
	// add crossContext="true" to all <Context /> tags!!!
	// ServletContext wikicontext = ((ServletContext)
	// jsfContext.getExternalContext().getContext())
	// .getContext("/KWiki");
	ServletContext servletContext = (ServletContext) jsfContext
		.getExternalContext().getContext();

	Map<String, XPSCase> sessionToCaseMap = (Map) servletContext
		.getAttribute("sessionToCaseMap");

	XPSCase theCase = sessionToCaseMap.get(id);

	WebDialog dia = new WebDialog();
	DialogUtils.setExpression(dia, "#{webDialog}");
	Map sessionMap = jsfContext.getExternalContext().getSessionMap();
	sessionMap.put("webDialog", dia);

	dia.setTheCase(theCase);

	DialogUtils.getQuestionPageBean().init();
	KBLoadController kbLoadBean = DialogUtils.getKBLoadBean();
	kbLoadBean.setKbID(theCase.getKnowledgeBase().getId());
	kbLoadBean.checkMultimediaFiles();

	if (req.getParameter("knowwe") != null) {
	    DialogUtils.getPageDisplay().moveToQuestionPage();
	    // redirect to dialog-page
	    ((HttpServletResponse) jsfContext.getExternalContext()
		    .getResponse()).sendRedirect(req.getContextPath()
		    + "/dialog.jsf");
	} else if (req.getParameter("knowweexplanation") != null) {
	    String toExplain = req.getParameter("toexplain");
	    // redirect to explanation-page
	    ((HttpServletResponse) jsfContext.getExternalContext()
		    .getResponse()).sendRedirect(req.getContextPath()
		    + "/explanation.jsf?expl=explainReason&diag=" + toExplain
		    + "");

	} else if (req.getParameter("knowwescm") != null) {
	    String toExplain = req.getParameter("toexplain");
	    // redirect to scm-page
	    ((HttpServletResponse) jsfContext.getExternalContext()
		    .getResponse()).sendRedirect(req.getContextPath()
		    + "/scm.jsf?diagID=" + toExplain + "");
	}
    }

    private void doRestart(FacesContext jsfContext) throws IOException {
	HttpSession session = (HttpSession) jsfContext.getExternalContext()
		.getSession(false);
	if (session != null) {
	    session.invalidate();
	}
    }

}
