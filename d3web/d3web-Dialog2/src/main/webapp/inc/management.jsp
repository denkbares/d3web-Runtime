<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3" %>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<t:panelTabbedPane id="mgmnttabpane" styleClass="width100p"
	activeTabStyleClass="activeTab" inactiveTabStyleClass="inactiveTab"
	binding="#{pageDisplay.managementTabPane}">

	<t:panelTab label="#{msgs['panelTab.kb']}" id="kb_tab">
		<t:div styleClass="mgmntPanelDiv">
			<t:htmlTag value="h2">
				<t:outputText value="#{msgs['kbselect.title']}" />
			</t:htmlTag>
			<d3:kBLoad>
				<f:facet name="loadKBbutton">
					<t:htmlTag value="p" styleClass="mgmntLoadKB mgmntheadline">
						<t:commandLink id="mgmntLoadKB" immediate="true"
							value="#{msgs['kbselect.load']}"
							title="#{msgs['kbselect.load']}" action="#{kbLoadBean.loadKB}" />
					</t:htmlTag>
				</f:facet>
				<f:facet name="uploadKBLink">
					<t:htmlTag value="p" styleClass="mgmntKBUpload mgmntheadline">
						<a4j:commandLink id="mgmntKBUpload" immediate="true"
							value="#{msgs['settings.kbupload.text']}"
							action="#{pageDisplay.moveToKBUploadPage}"
							title="#{msgs['settings.kbupload.description']}"
							reRender="main" onclick="cursor_wait();"
							oncomplete="cursor_clear();" />
					</t:htmlTag>
				</f:facet>
			</d3:kBLoad>
		</t:div>
	</t:panelTab>
	<t:panelTab label="#{msgs['panelTab.cases']}" id="case_tab"
		rendered="#{webDialog.theCase != null}">
		<t:div id="mgmnt_cases" styleClass="mgmntPanelDiv">
			<t:htmlTag id="title" value="h2">
				<t:outputText id="cases_headline"
					value="#{msgs['cases_menu.title']}" />
			</t:htmlTag>
			<t:panelGrid id="grid" cellpadding="0" cellspacing="2"
				styleClass="panelBox" columns="1">
				<t:panelGroup>
					<t:htmlTag value="p" styleClass="mgmntheadline mgmntNewCase">
						<a4j:commandLink immediate="true" id="mgmntNewCase"
							value="#{msgs['cases_menu.newcase.title']}"
							action="#{webDialog.startNewCase}"
							title="#{msgs['cases_menu.newcase.description']}"
							reRender="wrap" onclick="cursor_wait();"
							oncomplete="cursor_clear();" />
					</t:htmlTag>
					<t:outputText id="mgmntNewCaseText" styleClass="mgmnttext"
						value="#{msgs['cases_menu.newcase.description']}" />
				</t:panelGroup>
				<t:panelGroup>
					<t:htmlTag value="p" styleClass="mgmntheadline mgmntSaveCase">
						<a4j:commandLink immediate="true" id="mgmntSaveCase"
							value="#{msgs['cases_menu.savecase.title']}"
							action="#{pageDisplay.moveToSaveCasePage}"
							title="#{msgs['cases_menu.savecase.description']}"
							reRender="main" onclick="cursor_wait();"
							oncomplete="cursor_clear();" />
					</t:htmlTag>
					<t:outputText id="mgmntSaveCaseText" styleClass="mgmnttext"
						value="#{msgs['cases_menu.savecase.description']}" />
				</t:panelGroup>
				<t:panelGroup>
					<t:htmlTag value="p" styleClass="mgmntheadline mgmntLoadCase">
						<a4j:commandLink immediate="true" id="mgmntLoadCase"
							value="#{msgs['cases_menu.loadcase.title']}"
							action="#{pageDisplay.moveToLoadCasePage}"
							title="#{msgs['cases_menu.loadcase.description']}"
							reRender="middlePanel" onclick="cursor_wait();"
							oncomplete="cursor_clear();" />
					</t:htmlTag>
					<t:outputText id="mgmntLoadCaseText" styleClass="mgmnttext"
						value="#{msgs['cases_menu.loadcase.description']}" />
				</t:panelGroup>
			</t:panelGrid>
		</t:div>
	</t:panelTab>
	<t:panelTab label="#{msgs['panelTab.settings']}" id="settings_tab">
		<t:div styleClass="mgmntPanelDiv">
			<t:htmlTag value="h2">
				<t:outputText value="#{msgs['settings.title']}" />
			</t:htmlTag>
			<t:panelGrid cellpadding="0" cellspacing="2" styleClass="panelBox"
				columns="1">
				<t:panelGroup>
					<t:htmlTag value="p" styleClass="mgmntheadline mgmntKBUpload">
						<a4j:commandLink immediate="true" id="mgmntKBUpload"
							value="#{msgs['settings.kbupload.text']}"
							action="#{pageDisplay.moveToKBUploadPage}"
							title="#{msgs['settings.kbupload.description']}"
							reRender="main" onclick="cursor_wait();"
							oncomplete="cursor_clear();" />
					</t:htmlTag>
					<t:outputText styleClass="mgmnttext"
						value="#{msgs['settings.kbupload.description']}" />
				</t:panelGroup>
				<t:panelGroup>
					<t:htmlTag value="p" styleClass="mgmntheadline mgmntChangeLang">
						<a4j:commandLink immediate="true" id="mgmntChangeLang"
							value="#{msgs['settings.changelanguage.text']}"
							action="#{pageDisplay.moveToChangeLanguagePage}"
							title="#{msgs['settings.changelanguage.description']}"
							reRender="main" onclick="cursor_wait();"
							oncomplete="cursor_clear();" />
					</t:htmlTag>
					<t:outputText styleClass="mgmnttext"
						value="#{msgs['settings.changelanguage.description']}" />
				</t:panelGroup>
			</t:panelGrid>
		</t:div>
	</t:panelTab>
</t:panelTabbedPane>