<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>




<t:panelGrid id="resultTable" styleClass="resultPanelBox"
	cellpadding="0" cellspacing="0" columns="1"
	rowClasses="resultpageTableRow">
	<f:facet name="header">
		<t:panelGroup id="resultTableHeader">
			<t:htmlTag value="h2" styleClass="panelBoxCenteredHeadline">
				<t:outputText value="#{msgs['result.title']}" />
			</t:htmlTag>
			<t:htmlTag value="p" styleClass="smaller" style="text-align: right;">
				<t:outputText
					value="#{msgs['result.time.temporary']} #{resultPageBean.timeUpToNow}" />
			</t:htmlTag>
			<t:htmlTag value="p" styleClass="resultPageSubHeadline">
				<t:outputText value="#{msgs['result.diagnoses.title.exist']}" />
			</t:htmlTag>
		</t:panelGroup>
	</f:facet>

	<d3:heuristicDiagnoses id="result_diag"
		rendered="#{dialogSettings.showHeuristicDiagnoses}" />
	<d3:compareCaseBox id="result_cc"
		rendered="#{dialogSettings.showCompareCase}" />
	<d3:scmBox id="result_scm" rendered="#{dialogSettings.showSCM}" />
	<t:div id="result_processed"
		rendered="#{dialogSettings.showProcessedQContainers}">
		<d3:processedQContainersBox id="result_processedQContainers"
			rendered="#{dialogSettings.showProcessedQContainers}">
			<f:facet name="toggleLink">
				<a4j:commandLink id="result_processed_more" onclick="cursor_wait();"
					oncomplete="cursor_clear();"
					action="#{processedQContainersBean.toggleShowAll}"
					reRender="result_processed" title="#{msgs['processed.showall']}" />
			</f:facet>
			<f:facet name="unknownbox">
				<a4j:commandLink id="right_processed_unknown"
					styleClass="proc_pic proc_unk"
					onmouseover="Tip('#{msgs['processed.toggleUnknown']}', CLOSEBTN, false, DELAY, 300, STICKY, false)"
					onclick="cursor_wait()" oncomplete="cursor_clear()"
					action="#{processedQContainersBean.toggleShowUnknown}" value=" "
					reRender="result_processed"
					rendered="#{dialogSettings.processedShowUnknownIcon}" />
			</f:facet>
			<f:facet name="link">
				<a4j:commandLink id="qcont_link" onclick="cursor_wait();"
					oncomplete="cursor_clear();"
					action="#{questionPage.jumpToContainer}" reRender="middlePanel" />
			</f:facet>
			<f:facet name="qContNames">
				<h:panelGroup>
					<a4j:commandLink id="sort_alph" styleClass="proc_pic proc_alph"
						onclick="cursor_wait();" oncomplete="cursor_clear();"
						action="#{processedQContainersBean.toggleQContainerNames}"
						reRender="result_processed"
						onmouseover="Tip('#{msgs['processed.sortAlph']}', CLOSEBTN, false, DELAY, 300, STICKY, false)"
						value=" "
						rendered="#{dialogSettings.processedShowQContainerNamesIcon && processedQContainersBean.showQContainerNames}" />
					<a4j:commandLink id="sort_tree" styleClass="proc_pic proc_tree"
						onclick="cursor_wait();" oncomplete="cursor_clear();"
						action="#{processedQContainersBean.toggleQContainerNames}"
						reRender="result_processed"
						onmouseover="Tip('#{msgs['processed.sortTree']}', 	CLOSEBTN, false, DELAY, 300, STICKY, false)"
						value=" "
						rendered="#{dialogSettings.processedShowQContainerNamesIcon && !processedQContainersBean.showQContainerNames}" />
				</h:panelGroup>
			</f:facet>
		</d3:processedQContainersBox>
	</t:div>

</t:panelGrid>







<h:panelGrid columns="3" cellpadding="5" cellspacing="5">
	<a4j:commandLink id="res_savecase" styleClass="result_saveCase"
		immediate="true" action="#{pageDisplay.moveToSaveCasePage}"
		title="#{msgs['cases_menu.savecase.description']}" reRender="main">
		<h:outputText value="#{msgs['cases_menu.savecase.title']}" />
	</a4j:commandLink>
	<h:outputLink id="print" value="javascript:window.print()"
		styleClass="result_printCase" title="#{msgs['result.print']}">
		<h:outputText value="#{msgs['result.print']}" />
	</h:outputLink>
	<a4j:commandLink id="res_newcase" styleClass="result_newCase"
		immediate="true" action="#{webDialog.startNewCase}"
		title="#{msgs['cases_menu.newcase.description']}" reRender="wrap">
		<h:outputText value="#{msgs['cases_menu.newcase.title']}" />
	</a4j:commandLink>
</h:panelGrid>


