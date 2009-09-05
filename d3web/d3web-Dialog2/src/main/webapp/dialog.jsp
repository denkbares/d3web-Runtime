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

<f:view>
	<html>
	<head>
	<title><d3:outputTextWithoutId value="#{msgs['dialog.title']}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />

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
	
	<!--[if lt IE 7]>
	<script src="http://ie7-js.googlecode.com/svn/version/2.0(beta3)/IE7.js" type="text/javascript"></script>
	<![endif]-->
	</head>
	<body onmouseup="mouseUpAction(event)"
		onkeypress="keyPressAction(event);">
		<script type="text/javascript" src="javascript/tooltip/wz_tooltip.js"></script>
	<script type="text/javascript" src="javascript/tooltip/tip_balloon.js"></script>


	<h:form id="dialogForm" enctype="multipart/form-data">
		<a4j:region id="outerRegion" selfRendered="true">
			<t:inputHidden id="clickedQASet" value="-" />

			<%-- DEBUG MSGS
			<h:commandLink immediate="true" action="#{webDialog.killSession}" value="RestartSession" />
			<h:outputText value="&nbsp;" escape="false"/>
			<h:outputLink value="http://localhost:6060/d3web-Dialog2/" >
				<h:outputText value="pagerefresh" />
			</h:outputLink>
			<h:outputText value="&nbsp;" escape="false"/>
			<h:outputLink value="http://localhost:6060/d3web-Dialog2/_temp.jsf">
				<h:outputText value="otherpage" />
			</h:outputLink>
			<h:outputText value="&nbsp;" escape="false"/>
			<h:commandLink value="exceptiontest" action="#{webDialog.missing}" />
			<a4j:outputPanel ajaxRendered="true">
				<h:messages />
			</a4j:outputPanel>
			--%>

			<t:div id="wrap" forceId="true">

				<f:subview id="headerSubview"
					rendered="#{dialogSettings.showPageHeader}">
					<jsp:include page="inc/header.jsp" />
				</f:subview>

				<t:div id="main" forceId="true">
					<t:div id="mainGutter" styleClass="mainGutter">


						<f:subview id="leftPanelSubView">
							<jsp:include page="inc/leftPanel.jsp" />
						</f:subview>

						<t:div id="middlePanel" styleClass="middlePanel"
							style="#{dialogSettings.middleWidth}"
							forceId="true">

						<f:subview id="managementSubView" rendered="#{pageDisplay.leftContent == 'left_management'}">
							<jsp:include page="inc/management.jsp" />
						</f:subview>							
							
							<t:div id="middlePanelGutter" styleClass="middlePanelGutter">

								<f:subview id="questions"
									rendered="#{pageDisplay.centerContent == 'questions'}">
									<jsp:include page="inc/questions.jsp" />
								</f:subview>

								<f:subview id="result"
									rendered="#{pageDisplay.centerContent == 'result'}">
									<jsp:include page="inc/result.jsp" />
								</f:subview>

								<f:subview id="saveCase"
									rendered="#{pageDisplay.centerContent == 'saveCase'}">
									<jsp:include page="inc/saveCase.jsp" />
								</f:subview>

								<f:subview id="kbUpload"
									rendered="#{pageDisplay.centerContent == 'kbupload'}">
									<jsp:include page="inc/kbupload.jsp" />
								</f:subview>

								<f:subview id="changeLanguage"
									rendered="#{pageDisplay.centerContent == 'changelanguage'}">
									<jsp:include page="inc/changelanguage.jsp" />
								</f:subview>

								<f:subview id="loadCase"
									rendered="#{pageDisplay.centerContent == 'loadcase'}">
									<jsp:include page="inc/loadcase.jsp" />
								</f:subview>

							</t:div>
							<%-- End gutter --%>
						</t:div>
						<%-- End middlePanel --%>



						<f:subview id="rightPanelSubView"
							rendered="#{dialogSettings.allowRightPanel}">
							<jsp:include page="inc/rightPanel.jsp" />
						</f:subview>

					</t:div>
					<%-- end main gutter --%>
				</t:div>
				<%-- end main --%>


				<f:subview id="footerSubView"
					rendered="#{dialogSettings.showPageFooter}">
					<jsp:include page="inc/footer.jsp" />
				</f:subview>

				<a4j:status startStyleClass="progress" for="outerRegion"
					stopText=" ">
					<f:facet name="start">
						<t:panelGroup id="progressmsg">
							<t:htmlTag id="corners_top" styleClass="corners" value="span">
								<t:htmlTag styleClass="corner1" value="span" />
								<t:htmlTag styleClass="corner2" value="span" />
								<t:htmlTag styleClass="corner3" value="span" />
								<t:htmlTag styleClass="corner4" value="span" />
								<t:htmlTag styleClass="corner4" value="span" />
							</t:htmlTag>
							<h:outputText styleClass="progress_inner"
								value="#{msgs['dialog.progress']}" />
							<t:htmlTag id="corners_bottom" styleClass="corners" value="span">
								<t:htmlTag styleClass="corner4" value="span" />
								<t:htmlTag styleClass="corner4" value="span" />
								<t:htmlTag styleClass="corner3" value="span" />
								<t:htmlTag styleClass="corner2" value="span" />
								<t:htmlTag styleClass="corner1" value="span" />
							</t:htmlTag>
						</t:panelGroup>
					</f:facet>
				</a4j:status>

			</t:div>
			<%-- ende wrap --%>

		</a4j:region>
	</h:form>
	</body>
	</html>

</f:view>