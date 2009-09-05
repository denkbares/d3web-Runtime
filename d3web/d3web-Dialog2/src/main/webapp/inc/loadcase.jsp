<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>


<t:panelGrid id="loadCaseTable" styleClass="loadCasePanelBox"
	cellpadding="0" cellspacing="0" columns="1"
	rowClasses="loadCaseTableRow">
	<f:facet name="header">
		<t:htmlTag value="h2" styleClass="panelBoxCenteredHeadline">
			<d3:outputTextWithoutId value="#{msgs['caseselect.title']}" />
		</t:htmlTag>
	</f:facet>

	<d3:outputTextWithoutId value="#{msgs['caseselect.description']}" />

	<t:dataTable styleClass="panelBox" var="case" id="loadCaseSortedTable"
		sortable="true" value="#{loadCaseBean.cases}"
		sortColumn="#{loadCaseBean.sortColumn}"
		sortAscending="#{loadCaseBean.sortAscending}"
		binding="#{loadCaseBean.loadCaseDataTable}"
		rendered="#{loadCaseBean.hasData}">
		<t:column defaultSorted="true">
			<f:facet name="header">
				<h:outputText value="#{msgs['caseselect.columnheader.name']}" />
			</f:facet>
			<a4j:commandLink id="caseToLoad" action="#{loadCaseBean.loadCase}"
				value="#{case.title}" title="#{msgs['caseselect.title']}"
				reRender="wrap" onclick="cursor_wait();"
				oncomplete="cursor_clear();" />
		</t:column>
		<t:column>
			<f:facet name="header">
				<h:outputText value="#{msgs['caseselect.columnheader.date']}" />
			</f:facet>
			<h:outputText value="#{case.date}" />
		</t:column>
	</t:dataTable>
	<t:htmlTag value="p" rendered="#{!loadCaseBean.hasData}">
		<h:outputText value="#{msgs['caseselect.nocases']}" />
	</t:htmlTag>

</t:panelGrid>

