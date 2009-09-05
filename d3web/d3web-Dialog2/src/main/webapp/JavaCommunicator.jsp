
<%@page
	import="de.d3web.dialog2.LastClickedAnswer"%>
<%@ page import="de.d3web.dialog2.util.DialogUtils"%>

<%!String findParam(PageContext ctx, String key) {
	ServletRequest req = ctx.getRequest();
	String val = req.getParameter(key);

	if (val == null) {
	    val = (String) ctx.findAttribute(key);
	}
	return val;
    }%>
<%
    String caseID = findParam(pageContext, "caseId");
    if (caseID != null && caseID.length() > 0) {
		String answerID = findParam(pageContext, "answerId");
		if (answerID != null && answerID.length() > 0) {
		    LastClickedAnswer.getInstance().setLastClickedAnswerID(
			    answerID, caseID);
		}
    }
%>