<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>



<t:div id="footerBreak" styleClass="break" />

<t:div id="footer" forceId="true">
	<t:div id="footerGutter" styleClass="footerGutter">
	<t:panelGrid id="qPageButtons" styleClass="qPageButtons" style="text-align: left; float: left; width: 60%" columnClasses="left width33p, centered width33p, right width33p" columns="3">
			<h:panelGroup>
				<a4j:commandLink id="send" styleClass="button buttonPadding" onclick="doSubmit();return false;" value="#{msgs['dialog.globalbuttons.send']}" title="#{msgs['dialog.globalbuttons.send.descr']}" rendered="#{dialogSettings.showQuestionPageAnswerButton}" />
			</h:panelGroup>
	
			<t:div>
				<a4j:commandLink id="result" styleClass="button buttonPadding" onclick="cursor_wait();" oncomplete="cursor_clear();" action="#{questionPage.moveToResultPage}" value="#{msgs['dialog.globalbuttons.result']}" title="#{msgs['dialog.globalbuttons.result.descr']}" reRender="main" rendered="#{dialogSettings.showQuestionPageResultButton}" />
			</t:div>
			<t:div>
				<a4j:commandLink id="setunknown" styleClass="button buttonPadding globalunknown" onclick="setUnknownHiddenfield();cursor_wait();" oncomplete="cursor_clear();" title="#{msgs['dialog.globalbuttons.unknown.descr']}" action="#{questionPage.submitAction}" reRender="main" rendered="#{dialogSettings.showQuestionPageUnknownButton}">
					<h:outputText value="&nbsp;&nbsp;&nbsp;&nbsp;"  escape="false" />
				</a4j:commandLink>	
			</t:div>
	</t:panelGrid>
	
	<t:div style="text-align: right; float: right;">
			<h:outputLink value="http://www.d3web.de" title="d3web-Homepage">
				<t:graphicImage value="/images/logo.gif" alt="d3web-logo" width="25" />
			</h:outputLink>
		</t:div>
	</t:div>
	<%-- Ende #footer .gutter --%>
</t:div>
<%-- Ende #footer --%>



