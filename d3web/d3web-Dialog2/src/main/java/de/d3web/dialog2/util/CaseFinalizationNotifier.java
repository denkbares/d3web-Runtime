/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.dialog2.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

import de.d3web.dialog2.WebDialog;
import de.d3web.kernel.XPSCase;


public class CaseFinalizationNotifier implements HttpSessionListener {

    public static Logger logger = Logger
	    .getLogger(CaseFinalizationNotifier.class);

    public static void finalizeCase(XPSCase theCase) {
    	//TODO: Does nothing, should be removed...
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
