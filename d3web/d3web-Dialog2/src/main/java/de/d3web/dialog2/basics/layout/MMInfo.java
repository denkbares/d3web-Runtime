package de.d3web.dialog2.basics.layout;

public class MMInfo {

    public static final String ALIGN_LEFT = "left";

    public static final String ALIGN_CENTER = "center";

    public static final String ALIGN_RIGHT = "right";

    public static final String MOUSEEVENT_ONCLICK = "onclick";

    public static final String MOUSEEVENT_ONMOUSEOVER = "onmouseover";

    public static final String POSITION_HEADLINE = "headline";

    public static final String POSITION_TOP = "top";

    public static final String POSITION_BOTTOM = "bottom";

    public static final String POSITION_HIDDEN = "hidden";

    public static String[] getExtraTags() {
	return new String[] { "More" };
    }

    protected String align = ALIGN_LEFT;

    protected String position = POSITION_HEADLINE;

    protected String mouseEvent = MOUSEEVENT_ONCLICK;

    protected String text;

    protected String padding = "2px";

    protected int tooltipWidth = 0;

    public String getAlign() {
	return align;
    }

    public String getMouseEvent() {
	return mouseEvent;
    }

    public String getPadding() {
	return padding;
    }

    public String getPosition() {
	return position;
    }

    public String getText() {
	return text;
    }

    public int getTooltipWidth() {
	return tooltipWidth;
    }

    public void setAlign(String align) {
	this.align = align;
    }

    public void setMouseEvent(String mouseEvent) {
	this.mouseEvent = mouseEvent;
    }

    public void setPadding(String padding) {
	this.padding = padding;
    }

    public void setPosition(String position) {
	this.position = position;
    }

    public void setText(String text) {
	this.text = text;
    }

    public void setTooltipWidth(int tooltipWidth) {
	this.tooltipWidth = tooltipWidth;
    }

}
