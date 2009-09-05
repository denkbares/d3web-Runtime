package de.d3web.dialog2.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import de.d3web.dialog2.WebDialog;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;

public class CaseFinalizationNotifier implements HttpSessionListener {

    public static Logger logger = Logger
	    .getLogger(CaseFinalizationNotifier.class);

    public static void finalizeCase(XPSCase theCase) {
	try {
	    PSMethodSetCovering.getInstance().removeXPSCase(theCase);
	} catch (Exception e) {
	    logger.warn("Error while finalizing case...");
	}
    }

    public void sessionCreated(HttpSessionEvent e) {
	logger.info("Session created. It will expire in "
		+ e.getSession().getMaxInactiveInterval() + " seconds.");
    }

    public void sessionDestroyed(HttpSessionEvent e) {
	logger.info("Session destroyed...");
	HttpSession session = e.getSession();
	WebDialog dia = (WebDialog) session.getAttribute("webDialog");
	if (dia != null && dia.getTheCase() != null) {
	    finalizeCase(dia.getTheCase());
	}
    }

}
