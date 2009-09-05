package de.d3web.dialog2;

import java.util.HashMap;
import java.util.Map;

public class LastClickedAnswer {

	private final Map<String, String> cases2LastClickedAnswerID;
	private static LastClickedAnswer instance;

	public static LastClickedAnswer getInstance() {
		if (instance == null) {
			instance = new LastClickedAnswer();
		}
		return instance;
	}

	public LastClickedAnswer() {
		cases2LastClickedAnswerID = new HashMap<String, String>();
	}

	public String getLastClickedAnswerID(String aCaseId) {
		return cases2LastClickedAnswerID.get(aCaseId);
	}

	public void setLastClickedAnswerID(String lastClickedAnswerID, String aCaseId) {
		cases2LastClickedAnswerID.put(aCaseId, lastClickedAnswerID);
	}

}
