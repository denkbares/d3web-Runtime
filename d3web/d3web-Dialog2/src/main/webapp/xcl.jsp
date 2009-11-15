<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<jsp:text>
	<![CDATA[<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">]]>
</jsp:text>

<jsp:directive.page contentType="text/html;charset=ISO-8859-1" />

<f:view locale="#{localeChanger.locale}">

	<html>
	<head>
	<title><d3:outputTextWithoutId
		value="#{msgs['xcl.explainD3.explanation']}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<link rel="stylesheet" type="text/css" href="css/pagelayout.css"
		media="screen" />
	<link rel="stylesheet" type="text/css" href="css/dialog.css"
		media="screen" />
	<t:stylesheet path="#{dialogSettings.styleSheetPath}" media="screen"
		rendered="#{dialogSettings.kbHasCSSFile}" />

	<script type="text/javascript" src="javascript/d3dialog2.js"></script>

	<script type="text/javascript" src="javascript/mmfunctions2.js"></script>
	<script type="text/javascript" src="javascript/prototype.js"></script>
	<script type="text/javascript"
		src="javascript/scriptaculous/scriptaculous.js"></script>
	</head>
	<body>
	<a4j:form>
		<script type="text/javascript" src="javascript/tooltip/wz_tooltip.js"></script>
		<script type="text/javascript" src="javascript/tooltip/tip_balloon.js"></script>
		<h:form id="xclForm">
			<t:div id="xcl" forceId="true">
				<t:htmlTag value="h2">
					<h:outputText value="#{msgs['xcl.explainD3.title']}" />
				</t:htmlTag>

				<d3:xclPage diag="#{param.diagID}" />
			</t:div>
		</h:form>
	</a4j:form>
	</body>
	</html>

</f:view>