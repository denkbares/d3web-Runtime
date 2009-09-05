<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>



<t:div id="rightPanel" forceId="true"
	style="#{dialogSettings.rightWidth}"
	rendered="#{dialogSettings.showRightPanel}">
	<t:div id="rightPanelGutter" styleClass="rightPanelGutter">

		<f:subview id="right_dialog"
			rendered="#{pageDisplay.rightContent == 'right_dialog'}">
			<d3:heuristicDiagnoses id="right_diag"
				rendered="#{dialogSettings.showHeuristicDiagnoses}" />
			<d3:compareCaseBox id="right_cc"
				rendered="#{dialogSettings.showCompareCase}" />
			<d3:scmBox id="right_scm" rendered="#{dialogSettings.showSCM}" />
			<t:div id="right_processed"
				rendered="#{dialogSettings.showProcessedQContainers}">
				<d3:processedQContainersBox id="right_processedQContainers"
					rendered="#{dialogSettings.showProcessedQContainers}">
					<f:facet name="toggleLink">
						<a4j:commandLink id="right_processed_more"
							onclick="cursor_wait();" oncomplete="cursor_clear();"
							action="#{processedQContainersBean.toggleShowAll}"
							reRender="right_processed" />
					</f:facet>
					<f:facet name="unknownbox">
						<a4j:commandLink id="right_processed_unknown"
							styleClass="proc_pic proc_unk"
							onmouseover="Tip('#{msgs['processed.toggleUnknown']}', LEFT, true, CLOSEBTN, false, DELAY, 300, STICKY, false)"
							onclick="cursor_wait()" oncomplete="cursor_clear()"
							action="#{processedQContainersBean.toggleShowUnknown}" value=" "
							reRender="right_processed"
							rendered="#{dialogSettings.processedShowUnknownIcon}" />
					</f:facet>
					<f:facet name="link">
						<a4j:commandLink id="qcont_link" onclick="cursor_wait();"
							oncomplete="cursor_clear();"
							action="#{questionPage.jumpToContainer}"
							reRender="middlePanel, right_processed" />
					</f:facet>
					<f:facet name="qContNames">
						<h:panelGroup>
							<a4j:commandLink id="sort_alph" styleClass="proc_pic proc_alph"
								onclick="cursor_wait();" oncomplete="cursor_clear();"
								action="#{processedQContainersBean.toggleQContainerNames}"
								reRender="right_processed"
								onmouseover="Tip('#{msgs['processed.sortAlph']}', LEFT, true, CLOSEBTN, false, DELAY, 300, STICKY, false)"
								value=" "
								rendered="#{dialogSettings.processedShowQContainerNamesIcon && processedQContainersBean.showQContainerNames}" />
							<a4j:commandLink id="sort_tree" styleClass="proc_pic proc_tree"
								onclick="cursor_wait();" oncomplete="cursor_clear();"
								action="#{processedQContainersBean.toggleQContainerNames}"
								reRender="right_processed"
								onmouseover="Tip('#{msgs['processed.sortTree']}', LEFT, true, CLOSEBTN, false, DELAY, 300, STICKY, false)"
								value=" "
								rendered="#{dialogSettings.processedShowQContainerNamesIcon && !processedQContainersBean.showQContainerNames}" />
						</h:panelGroup>
					</f:facet>
				</d3:processedQContainersBox>
			</t:div>
			<d3:answerFrequentnessBox
				rendered="#{dialogSettings.showFrequentness && answerFrequentnessBean.dataAvailable}" />
		</f:subview>

	</t:div>
	<%-- End gutter --%>
</t:div>
<%-- End rightPanel --%>