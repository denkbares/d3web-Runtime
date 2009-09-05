package de.d3web.dialog2.controller;

import de.d3web.dialog2.util.DialogUtils;

public class ProcessedQContainersController {

    public static final String QTEXTMODE_TEXT = "text";

    public static final String QTEXTMODE_PROMPT = "prompt";

    private boolean showAll;

    private boolean showUnknown;

    private int maxInput;

    private String qTextMode;

    private boolean showQContainerNames;

    public ProcessedQContainersController() {
	init();
    }

    public int getMaxInput() {
	return maxInput;
    }

    public String getQTextMode() {
	return qTextMode;
    }

    public void init() {
	showUnknown = DialogUtils.getDialogSettings().isProcessedShowUnknown();
	showAll = DialogUtils.getDialogSettings().isProcessedShowAll();
	maxInput = DialogUtils.getDialogSettings().getProcessedMaxInput();
	qTextMode = DialogUtils.getDialogSettings().getProcessedQTextMode();
	showQContainerNames = DialogUtils.getDialogSettings()
		.isProcessedShowQContainerNames();
    }

    public boolean isShowAll() {
	return showAll;
    }

    public boolean isShowQContainerNames() {
	return showQContainerNames;
    }

    public boolean isShowUnknown() {
	return showUnknown;
    }

    public String toggleQContainerNames() {
	showQContainerNames = !showQContainerNames;
	return "";
    }

    public String toggleShowAll() {
	showAll = !showAll;
	return "";
    }

    public String toggleShowUnknown() {
	showUnknown = !showUnknown;
	return "";
    }

}
