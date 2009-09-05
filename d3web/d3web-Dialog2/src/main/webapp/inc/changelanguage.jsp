<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>


<h:panelGrid id="kbuploadTable" cellpadding="0" cellspacing="0"
	styleClass="panelBox">
	<f:facet name="header">
		<t:htmlTag value="h2" styleClass="panelBoxCenteredHeadline">
			<t:outputText value="#{msgs['language.title']}" />
		</t:htmlTag>
	</f:facet>
	<t:selectOneRadio value="#{localeChanger.language}"
		layout="pageDirection" title="#{msgs['language.description']}">
		<f:selectItems value="#{localeChanger.languages}" />

		<a4j:support event="onchange"
			actionListener="#{localeChanger.changeLanguage}"
			action="#{pageDisplay.moveToManagementPage}" reRender="main" />
	</t:selectOneRadio>
</h:panelGrid>

