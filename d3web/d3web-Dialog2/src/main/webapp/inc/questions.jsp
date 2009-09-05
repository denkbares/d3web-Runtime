<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3" %>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<d3:questionPage id="qPage" minanswrap="#{dialogSettings.questionMinAnsWrap}" >
	<f:facet name="button">
		<a4j:commandButton 
			id="q_ok" 
			onclick="cursor_wait(); saveClickPosition()" 
			oncomplete="cursor_clear(); setPopupPosition();" 
			styleClass="okButton" action="#{questionPage.clearLastClickandSubmitAction}" 
			value="#{msgs['dialog.questionbutton']}" 
			title="#{msgs['dialog.globalbuttons.send.descr']}" 
			reRender="progressBarContainer, main"/>
	</f:facet>
	<a4j:commandButton id="send_hidden" styleClass="invis" 
		onclick="cursor_wait(); saveClickPosition();" 
		oncomplete="cursor_clear(); setPopupPosition();" 
		action="#{questionPage.submitAction}" 
		value="#{msgs['dialog.globalbuttons.send']}" 
		title="#{msgs['dialog.globalbuttons.send.descr']}" 
		reRender="progressBarContainer, main" />
</d3:questionPage>

<t:div styleClass="oqButtons" rendered="#{dialogSettings.dialogMode == 'OQ'}">
	<a4j:commandButton id="oq_oneback" styleClass="button buttonPadding" disabled="#{!questionPage.canMoveBack}" value="#{msgs['dialog.oq.oneback']}" title="#{msgs['dialog.oq.oneback.title']}" actionListener="#{questionPage.oneQBack}" onclick="cursor_wait();" oncomplete="cursor_clear();" reRender="main" />
	<a4j:commandButton id="oq_oneforward" styleClass="button buttonPadding" disabled="#{!questionPage.canMoveForward}" value="#{msgs['dialog.oq.oneforward']}" title="#{msgs['dialog.oq.oneforward.title']}" actionListener="#{questionPage.oneQForward}" onclick="cursor_wait();" oncomplete="cursor_clear();" reRender="main" />
	<a4j:commandButton id="oq_newest" styleClass="button buttonPadding" disabled="#{!questionPage.canMoveForward}" value="#{msgs['dialog.oq.actual']}" title="#{msgs['dialog.oq.actual.title']}" actionListener="#{questionPage.moveToNewestQ}" onclick="cursor_wait();" oncomplete="cursor_clear();" reRender="main" />
</t:div>


				
				
				