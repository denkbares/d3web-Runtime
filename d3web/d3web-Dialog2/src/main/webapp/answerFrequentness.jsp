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
		value="#{msgs['freq.pagetitle']}" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<link rel="stylesheet" type="text/css" href="css/pagelayout.css"
		media="screen" />
	<link rel="stylesheet" type="text/css" href="css/dialog.css"
		media="screen" />
	<t:stylesheet path="#{dialogSettings.styleSheetPath}" media="screen"
		rendered="#{dialogSettings.kbHasCSSFile}" />

	<script type="text/javascript" src="javascript/d3dialog2.js"></script>

	</head>
	<body>
	<script type="text/javascript" src="javascript/tooltip/wz_tooltip.js"></script>
	<script type="text/javascript" src="javascript/tooltip/tip_balloon.js"></script>
	<h:form id="answersFrequentnessForm">
		<a4j:region id="answersFrequentnessRegion">

			<t:htmlTag value="h2">
				<h:outputText value="#{msgs['freq.title']}" />
			</t:htmlTag>

			<h:panelGrid id="ansFreqGrid" columns="2"
				columnClasses="ansFreqGridCols,ansFreqGridCols">
				<h:panelGrid>
					<h:selectManyListbox value="#{answerFrequentnessBean.selectedData}"
						styleClass="freqList">
						<f:selectItems value="#{answerFrequentnessBean.selectData}" />
					</h:selectManyListbox>
					<a4j:commandButton id="submit" value="#{msgs['freq.analyse']}"
						reRender="answerFreqTable" onclick="cursor_wait();"
						oncomplete="cursor_clear();" />
				</h:panelGrid>


				<t:div id="answerFreqTable" styleClass="freqTable">
					<d3:frequentnessTable
						value="#{answerFrequentnessBean.dataGroupWithFrequentnessData}" />
				</t:div>
			</h:panelGrid>


			<a4j:status startStyleClass="progress"
				for="answersFrequentnessRegion" stopText=" ">
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