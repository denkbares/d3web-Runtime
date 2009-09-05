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
	<title><d3:outputTextWithoutId value="#{msgs['cbr.title']}" /></title>
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
	<script type="text/javascript" src="javascript/tooltip/wz_tooltip.js"></script>
	<script type="text/javascript" src="javascript/tooltip/tip_balloon.js"></script>
	<h:form id="compareCaseForm">
		<a4j:region id="cbrRegion">
			<a4j:keepAlive beanName="compareCase" />
			<t:inputHidden id="compType" value="simple" />
			<t:inputHidden id="compID" value="-" />

			<t:div id="comparecase" forceId="true">
				<t:htmlTag id="cbr_headline" value="h2">
					<h:outputText value="#{msgs['cbr.title']}" />
				</t:htmlTag>

				<t:htmlTag id="cbr_compmode" value="p">
					<h:panelGroup>
						<h:outputLabel for="selectcmode" value="#{msgs['cbr.cmode']}: " />
						<h:selectOneMenu id="selectcmode" value="#{compareCase.compMode}">
							<a4j:support event="onchange"
								action="#{compareCase.changeCompMode}" reRender="comparecase" />

							<f:selectItem itemValue="0" itemLabel="#{msgs['cbr.cmode.no']}" />
							<f:selectItem itemValue="1"
								itemLabel="#{msgs['cbr.cmode.query']}" />
							<f:selectItem itemValue="2"
								itemLabel="#{msgs['cbr.cmode.retrieve']}" />
							<f:selectItem itemValue="3" itemLabel="#{msgs['cbr.cmode.both']}" />
						</h:selectOneMenu>
					</h:panelGroup>
				</t:htmlTag>

				<d3:compareCasePage id="compareTable" value="#{param.comptype}">
					<f:facet name="showUnknown">
						<t:div styleClass="showUnknownWrap">
							<a4j:commandLink action="#{compareCase.goShowUnknown}"
								reRender="comparecase"
								value="#{msgs['cbr.detailled.showunknown']}"
								styleClass="button showUnknown"
								title="#{msgs['cbr.detailled.showunknown']}" />
						</t:div>
					</f:facet>
					<f:facet name="hideUnknown">
						<t:div styleClass="showUnknownWrap">
							<a4j:commandLink action="#{compareCase.goHideUnknown}"
								reRender="comparecase" styleClass="button showUnknown"
								value="#{msgs['cbr.detailled.hideunknown']}"
								title="#{msgs['cbr.detailled.hideunknown']}" />
						</t:div>
					</f:facet>
					<f:facet name="backbutton">
						<a4j:commandLink id="cc_backbutton"
							styleClass="button backicon buttonPaddingWithIcon"
							onclick="startCBRSimple()" reRender="comparecase"
							value="#{msgs['cbr.backbutton']}"
							title="#{msgs['cbr.backbutton']}" />
					</f:facet>
					<f:facet name="startContainerButton">
						<a4j:commandLink id="compLink_container" reRender="comparecase"
							styleClass="button buttonPadding"
							title="#{msgs['cbr.simple.containerbutton.description']}"
							value="#{msgs['cbr.simple.containerbutton.text']}" />
					</f:facet>
					<f:facet name="startDetailledButton">
						<a4j:commandLink id="compLink_detailled" reRender="comparecase"
							styleClass="button buttonPadding"
							title="#{msgs['cbr.simple.detailledbutton.description']}"
							value="#{msgs['cbr.simple.detailledbutton.text']}" />
					</f:facet>
				</d3:compareCasePage>

			</t:div>

			<a4j:status startStyleClass="progress" for="cbrRegion" stopText=" ">
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

		</a4j:region>
	</h:form>
	</body>
	</html>
</f:view>