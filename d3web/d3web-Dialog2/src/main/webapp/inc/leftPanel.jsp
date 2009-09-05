<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>




<t:div id="leftPanel" forceId="true"
	styleClass="#{dialogSettings.positionFixedCSS}"
	style="#{dialogSettings.leftWidth}}">
	<t:div id="leftPanelGutter" styleClass="leftPanelGutter">

		<f:subview id="leftDiaSV"
			rendered="#{pageDisplay.leftContent == 'left_dialog'}">

			<t:panelTabbedPane id="diaTP" styleClass="width100p"
				serverSideTabSwitch="true" activeTabStyleClass="activeTab"
				inactiveTabStyleClass="inactiveTab"
				rendered="#{dialogSettings.showQASetTreeTab || dialogSettings.showQASetTreeTab}">

				<t:panelTab id="containertab" label="#{msgs['panelTab.containers']}"
					rendered="#{dialogSettings.showQASetTreeTab}">

					<t:div id="qasettree" forceId="true">
						<t:tree2 id="q_tree" clientSideToggle="false"
							value="#{qaSetTree.qaSetTreeModel}" var="node"
							showRootNode="false" binding="#{qaSetTree.qaSetHtmlTree}">
							<f:facet name="QASet">
								<t:panelGrid id="qaSet" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0"
									title="#{msgs['tree.qcontainerclick']}">
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();">
										<t:graphicImage value="/images/icon_qcontainer.gif" alt="x" />
									</a4j:commandLink>
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<d3:qContainerName qcontainerId="#{node.identifier}"
											value="#{node.description}" />
									</a4j:commandLink>
								</t:panelGrid>
							</f:facet>
							<f:facet name="selectedQASet">
								<t:panelGrid id="selectedQASet" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<t:graphicImage styleClass="currentQASet"
											value="/images/icon_qcontainer.gif" alt="x" />
									</a4j:commandLink>
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<d3:qContainerName styleClass="currentQASet"
											qcontainerId="#{node.identifier}" value="#{node.description}" />
									</a4j:commandLink>
								</t:panelGrid>
							</f:facet>
							<f:facet name="doneQASet">
								<t:panelGrid id="doneQASet" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<t:graphicImage styleClass="completeQASet"
											value="/images/icon_qcontainer.gif" alt="x" />
									</a4j:commandLink>
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<d3:qContainerName styleClass="completeQASet"
											value="#{node.description}" qcontainerId="#{node.identifier}" />
									</a4j:commandLink>
								</t:panelGrid>
							</f:facet>
							<f:facet name="tobeaskedQASet">
								<t:panelGrid id="tobeaskedQASet" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<t:graphicImage styleClass="incompleteQASet"
											value="/images/icon_qcontainer.gif" alt="x" />
									</a4j:commandLink>
									<a4j:commandLink actionListener="#{qaSetTree.changeContainer}"
										reRender="wrap" onclick="cursor_wait();"
										oncomplete="cursor_clear();"
										title="#{msgs['tree.qcontainerclick']}">
										<d3:qContainerName styleClass="incompleteQASet"
											value="#{node.description}" qcontainerId="#{node.identifier}" />
									</a4j:commandLink>
								</t:panelGrid>
							</f:facet>
						</t:tree2>
					</t:div>
				</t:panelTab>

				<t:panelTab id="diagtab" label="#{msgs['panelTab.diagnoses']}"
					rendered="#{dialogSettings.showQASetTreeTab && diagnosesTree.diagnosesAvailable}">
					<t:div id="diagtree" forceId="true">
						<t:tree2 id="diag_tree" clientSideToggle="false"
							value="#{diagnosesTree.diagTreeModel}" var="node"
							binding="#{diagnosesTree.diagTree}">
							<f:facet name="diagnosisTree">
								<t:panelGrid id="diagnosisTree" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<t:graphicImage styleClass="diagStandard"
										value="/images/icon_diagnosis.gif" alt="x" />
									<d3:diagWithInfo id="diagtab_standard"
										styleClass="diagStandard" value="#{node.identifier}" />
								</t:panelGrid>
							</f:facet>
							<f:facet name="diagExcl">
								<t:panelGrid id="diagExcl" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<t:graphicImage styleClass="diagExcl"
										value="/images/icon_diagnosis.gif" alt="x" />
									<d3:diagWithInfo id="diagtab_excl" styleClass="diagExcl"
										value="#{node.identifier}" />
								</t:panelGrid>
							</f:facet>
							<f:facet name="diagEstab">
								<t:panelGrid id="diagEstab" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<t:graphicImage styleClass="diagEstab"
										value="/images/icon_diagnosis.gif" alt="x" />
									<d3:diagWithInfo id="diagtab_estab" styleClass="diagEstab"
										value="#{node.identifier}" />
								</t:panelGrid>
							</f:facet>
							<f:facet name="diagSugg">
								<t:panelGrid id="diagSugg" forceId="true" columns="2"
									columnClasses="valigntop" cellpadding="0" cellspacing="0">
									<t:graphicImage styleClass="diagSugg"
										value="/images/icon_diagnosis.gif" alt="x" />
									<d3:diagWithInfo id="diagtab_sugg" styleClass="diagSugg"
										value="#{node.identifier}" />
								</t:panelGrid>
							</f:facet>
						</t:tree2>
					</t:div>
				</t:panelTab>
			</t:panelTabbedPane>

		</f:subview>


	</t:div>
	<%-- Ende gutter --%>
</t:div>
<%-- Ende leftPanel --%>