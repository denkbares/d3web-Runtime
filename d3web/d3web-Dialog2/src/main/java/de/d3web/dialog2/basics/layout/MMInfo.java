/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

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
