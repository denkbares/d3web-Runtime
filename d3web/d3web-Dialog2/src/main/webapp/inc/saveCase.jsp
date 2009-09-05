<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>


<t:div id="savecasepagediv" forceId="true">

	<t:panelGrid id="saveCaseGrid" styleClass="panelBox" columns="2"
		cellpadding="5" cellspacing="0">
		<f:facet name="header">
			<t:htmlTag value="h2" styleClass="panelBoxCenteredHeadline">
				<h:outputText value="#{msgs['savecase.title']}" />
			</t:htmlTag>
		</f:facet>
		<t:outputText value="#{msgs['savecase.name.text']}" />
		<t:inputText id="save_name" forceId="true" styleClass="saveinput"
			value="#{saveCaseBean.caseTitle}" />
		<t:outputText value="#{msgs['savecase.author.text']}" />
		<t:inputText id="save_author" forceId="true" styleClass="saveinput"
			value="#{saveCaseBean.caseAuthor}" />
		<t:outputText value="#{msgs['savecase.account.text']}" />
		<t:inputText id="save_account" forceId="true" styleClass="saveinput"
			disabled="true" value="#{saveCaseBean.userEmail}" />
		<t:outputText value="#{msgs['savecase.date.text']}" />
		<t:inputText id="save_date" forceId="true" styleClass="saveinput"
			disabled="true" value="#{saveCaseBean.caseDate}">
			<f:convertDateTime locale="#{localeChanger.locale}" type="both"
				timeZone="#{dialogSettings.timeZone}" dateStyle="medium"
				timeStyle="medium" />
		</t:inputText>
		<t:outputText value="#{msgs['savecase.comment.text']}" />
		<t:inputTextarea id="save_comment" forceId="true"
			styleClass="saveinput" value="#{saveCaseBean.caseComment}" />
	</t:panelGrid>


	<d3:correctionPage />


	<t:panelGrid id="saveButtonTable" columns="3" cellpadding="5"
		cellspacing="5">
		<h:commandLink id="savenew" styleClass="savecaseicon"
			action="#{saveCaseBean.saveCase}"
			title="#{msgs['savecase.savenewbutton.text']}">
			<h:outputText value="#{msgs['savecase.savenewbutton.text']}" />
		</h:commandLink>
		<h:commandLink id="override" styleClass="savecaseicon"
			action="#{saveCaseBean.overwriteCase}"
			title="#{msgs['savecase.overridebutton.text']}"
			rendered="#{webDialog.caseLoaded || webDialog.caseSaved}">
			<h:outputText value="#{msgs['savecase.overridebutton.text']}" />
		</h:commandLink>
		<h:commandLink id="dl" styleClass="savecaseicon"
			action="#{saveCaseBean.dlCase}"
			title="#{msgs['savecase.download.title']}">
			<h:outputText value="#{msgs['savecase.download']}" />
		</h:commandLink>
	</t:panelGrid>

</t:div>

