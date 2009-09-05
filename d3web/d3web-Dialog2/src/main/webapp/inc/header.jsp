<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://www.d3web.de/dialog2" prefix="d3"%>
<%@ taglib uri="https://ajax4jsf.dev.java.net/ajax" prefix="a4j"%>

<t:div id="header" forceId="true">
	<t:div id="headergutter" styleClass="headerGutter">

		<t:panelGrid id="headergrid" width="100%" columns="2" cellpadding="0"
			cellspacing="0" columnClasses="left,right">

			<t:div id="leftHeader" styleClass="buttonWrap">
				<t:commandLink id="moveToDialogLink"
					styleClass="button backicon buttonPaddingWithIcon" immediate="true"
					action="#{pageDisplay.moveToDialogPage}"
					value="#{msgs['toptogglemenu.dialogbutton.text']}"
					title="#{msgs['toptogglemenu.dialogbutton.description']}"
					rendered="#{dialogSettings.showDialogButton && pageDisplay.pageMode != 'dialogmode' && webDialog.theCase != null}" />
				<t:commandLink id="moveToManagementLink"
					styleClass="button backicon buttonPaddingWithIcon" immediate="true"
					action="#{pageDisplay.moveToManagementPage}"
					value="#{msgs['toptogglemenu.managementbutton.text']}"
					title="#{msgs['toptogglemenu.managementbutton.description']}"
					rendered="#{dialogSettings.showManagementButton && pageDisplay.pageMode != 'managementmode'}" />
			</t:div>

			<t:div id="rightHeader">
				<t:div id="progressBarContainer" rendered="#{dialogSettings.showProgressBar}" styleClass="progressBarContainer">
					<t:htmlTag value="p" id="progressBar" style="#{questionPage.progressBarStyle}" styleClass="progressBar" />
				</t:div>
			<h:panelGrid columns="1" styleClass="right" style="padding-right: 20px;" cellpadding="0" cellspacing="0">
				<h:panelGroup>
					<a4j:commandLink id="germanlocale" style="padding: 5px;"
						immediate="true" action="#{localeChanger.germanAction}"
						title="#{msgs['toggleGerman.title']}" reRender="wrap"
						rendered="#{dialogSettings.showCountryFlags}">
						<h:graphicImage value="/images/germanflag.gif" alt="deutsch"
							width="26" />
					</a4j:commandLink>
					<a4j:commandLink id="englishlocale" immediate="true"
						action="#{localeChanger.englishAction}"
						title="#{msgs['toggleEnglish.title']}" reRender="wrap"
						rendered="#{dialogSettings.showCountryFlags}">
						<h:graphicImage value="/images/englishflag.gif" alt="english"
							width="26" />
					</a4j:commandLink>
				</h:panelGroup>
				<t:div id="togglebuttons" style="position: relative; top: 3px;"
					styleClass="toggleButtons"
					rendered="#{dialogSettings.showRightPanelToggleButtons && pageDisplay.pageMode == 'dialogmode'}">
					<a4j:commandLink id="toggleRightFrame" immediate="true"
						actionListener="#{dialogSettings.toggleRightFrame}"
						reRender="main">
						<h:graphicImage id="showRight" value="/images/showRightFrame.gif"
							alt="showRightFrame"
							title="#{msgs['togglebuttons.showRightFrame']}"
							rendered="#{!dialogSettings.showRightPanel}" />
						<h:graphicImage id="hideRight" value="/images/hideRightFrame.gif"
							alt="hideRightFrame"
							title="#{msgs['togglebuttons.hideRightFrame']}"
							rendered="#{dialogSettings.showRightPanel}" />
					</a4j:commandLink>
				</t:div>
			</h:panelGrid>
		</t:div>
		</t:panelGrid>






	</t:div>
</t:div>

