package de.d3web.dialog2.basics.layout;

public class HtmlTextLayout {

    public static String[] getAllExtraTags() {
	return new String[] { "QPrompt", "QText", "MMInfo", "Image" };
    }

    private int posXStart;

    private int posYStart;

    private int colspan;

    private int rowspan;

    private String text;

    private String questionBinding;

    private String additionalCSSStyle;

    private String additionalCSSClass;

    public HtmlTextLayout(String posX, String posY) {
	if (posX.indexOf("-") == -1) {
	    posXStart = Integer.parseInt(posX);
	    colspan = 0;
	} else {
	    String[] positions = posX.split("-");
	    int xStart = Integer.parseInt(positions[0]);
	    int xEnd = Integer.parseInt(positions[1]);
	    posXStart = xStart;
	    colspan = xEnd - xStart + 1;
	}

	if (posY.indexOf("-") == -1) {
	    posYStart = Integer.parseInt(posY);
	    rowspan = 0;
	} else {
	    String[] positions = posY.split("-");
	    int yStart = Integer.parseInt(positions[0]);
	    int yEnd = Integer.parseInt(positions[1]);
	    posYStart = yStart;
	    rowspan = yEnd - yStart + 1;
	}
    }

    public String getAdditionalCSSClass() {
	return additionalCSSClass;
    }

    public String getAdditionalCSSStyle() {
	return additionalCSSStyle;
    }

    public int getColspan() {
	return colspan;
    }

    public int getPosXStart() {
	return posXStart;
    }

    public int getPosYStart() {
	return posYStart;
    }

    public String getQuestionBinding() {
	return questionBinding;
    }

    public int getRowspan() {
	return rowspan;
    }

    public String getText() {
	return text;
    }

    public void setAdditionalCSSClass(String additionalCSSClass) {
	this.additionalCSSClass = additionalCSSClass;
    }

    public void setAdditionalCSSStyle(String additionalCSSStyle) {
	this.additionalCSSStyle = additionalCSSStyle;
    }

    public void setQuestionBinding(String questionBinding) {
	this.questionBinding = questionBinding;
    }

    public void setText(String text) {
	this.text = text;
    }

    @Override
    public String toString() {
	return "\n  HtmlTextLayout posXStart=" + posXStart + "; posYStart="
		+ posYStart + "; colspan=" + colspan + "; rowspan=" + rowspan
		+ "; text=" + text + ";";

    }

}
