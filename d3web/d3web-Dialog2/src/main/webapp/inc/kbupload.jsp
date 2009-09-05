<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<a4j:keepAlive beanName="kbUploadBean" />
<h:panelGrid id="kbuploadTable" cellpadding="0" cellspacing="0"
	styleClass="panelBox">
	<f:facet name="header">
		<t:htmlTag id="headline" value="h2"
			styleClass="panelBoxCenteredHeadline">
			<t:outputText id="headlinetext" value="#{msgs['kbupload.title']}" />
		</t:htmlTag>
	</f:facet>
	<t:panelGrid id="grid" columns="2" cellpadding="0" cellspacing="0">
		<h:outputText value="#{msgs['kbupload.name.text']}" />
		<t:panelGroup>
			<h:inputText id="upname" value="#{kbUploadBean.name}" required="true"
				size="24" requiredMessage="#{msgs['kbupload.validationerror.name']}" />
			<h:message for="upname" errorClass="validationerror" />
		</t:panelGroup>


		<h:outputText value="#{msgs['kbupload.file.text']}" />
		<t:panelGroup>
			<t:inputFileUpload id="fileupload" value="#{kbUploadBean.upFile}"
				storage="file" required="true" size="25" />
			<h:message for="fileupload" errorClass="validationerror" />
		</t:panelGroup>

		<t:outputText value="&nbsp;" escape="false" />
		<t:commandButton id="kbuploadsubmit"
			value="#{msgs['kbupload.nextbutton.text']}"
			title="#{msgs['kbupload.title']}"
			actionListener="#{kbUploadBean.upload}"
			action="#{pageDisplay.moveToManagementPage}" />

	</t:panelGrid>


</h:panelGrid>

