package de.d3web.dialog2.controller;

import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.util.DialogUtils;

public class ResultPageController {

    private WebDialog dia;

    public WebDialog getDia() {
	return dia;
    }

    public String getTimeUpToNow() {
	long time = Math.round(dia.getCaseProcessingTime() / 1000.0);
	return DialogUtils.toFormattedTimeString(time);
    }

    public void setDia(WebDialog dia) {
	this.dia = dia;
    }

    public void setTimeUpToNow(String timeUpToNow) {
    }

}
