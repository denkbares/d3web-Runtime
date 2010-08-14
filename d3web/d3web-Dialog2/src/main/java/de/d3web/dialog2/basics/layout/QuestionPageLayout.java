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

public class QuestionPageLayout {

	// attributes of <Default>, <QContainer>, <Question>
	private int gridgap = 0;
	private String padding = "2px";
	private String qContainerBorder = "1px solid #000";
	private String questionBorder = "1px solid #000";
	private String questionVerticalAlign = "top";
	private String additionalCSSStyle; // no default value
	private String additionalCSSClass; // no default value
	private String currentQuestionBackground = "#FFFFCC";
	private String answeredQuestionBackground = "#F0F0F0";
	private String unansweredQuestionBackground = "#DFFDDF";
	private int questionColumns = 2;

	// attributes of <QContainerHeadline>
	private Boolean showQContainerHeadline = true;
	private String qContainerHeadlineAltText; // no default value
	private String qContainerHeadlineBorder = "1px solid #000";
	private String qContainerHeadlineBackground = "#eee";
	private String qContainerHeadlineTextColor = "#000";
	private String qContainerHeadlineFont; // no default value
	private String qContainerHeadlinePadding = "5px";

	// attributes of <QuestionHeadline>
	private Boolean showQuestionHeadline = true;
	private Boolean showButton = true;
	private String headlineTextColor = "#000";
	private String headlineFont; // no default value
	private String headlineMargin = "0px 0px 5px 0px";
	private String headlineBackground = "transparent";
	private String headlineBorder; // no default value

	// attributes of <QuestionAnswers>
	private String answerChoiceType = "checkbox";
	private String answerTextColor = "#000";
	private String answerFont; // no default value
	private int answerColumns = 0;
	private String inputWidth = "200px";
	private String inputHeight = "100px";
	private Boolean fastAnswer = false;
	private String qTextDisplayMode = "textarea";
	private String questionAnswersMargin = "0px 5px";

	// attributes of <ImageMap>
	private String imageMapHeadlineText; // no default value
	private boolean showImageMapRegionOnMouseOver = true;

	// <MMInfo>
	private MMInfo mmInfo = new MMInfo();

	public String getAdditionalCSSClass() {
		return additionalCSSClass;
	}

	public String getAdditionalCSSStyle() {
		return additionalCSSStyle;
	}

	public String getAnswerChoiceType() {
		return answerChoiceType;
	}

	public int getAnswerColumns() {
		return answerColumns;
	}

	public String getAnsweredQuestionBackground() {
		return answeredQuestionBackground;
	}

	public String getAnswerFont() {
		return answerFont;
	}

	public String getAnswerTextColor() {
		return answerTextColor;
	}

	public String getCurrentQuestionBackground() {
		return currentQuestionBackground;
	}

	public Boolean getFastAnswer() {
		return fastAnswer;
	}

	public int getGridgap() {
		return gridgap;
	}

	public String getHeadlineBackground() {
		return headlineBackground;
	}

	public String getHeadlineBorder() {
		return headlineBorder;
	}

	public String getHeadlineFont() {
		return headlineFont;
	}

	public String getHeadlineMargin() {
		return headlineMargin;
	}

	public String getHeadlineTextColor() {
		return headlineTextColor;
	}

	public String getImageMapHeadlineText() {
		return imageMapHeadlineText;
	}

	public String getInputHeight() {
		return inputHeight;
	}

	public String getInputWidth() {
		return inputWidth;
	}

	public MMInfo getMmInfo() {
		return mmInfo;
	}

	public String getPadding() {
		return padding;
	}

	public String getQContainerBorder() {
		return qContainerBorder;
	}

	public String getQContainerHeadlineAltText() {
		return qContainerHeadlineAltText;
	}

	public String getQContainerHeadlineBackground() {
		return qContainerHeadlineBackground;
	}

	public String getQContainerHeadlineBorder() {
		return qContainerHeadlineBorder;
	}

	public String getQContainerHeadlineFont() {
		return qContainerHeadlineFont;
	}

	public String getQContainerHeadlinePadding() {
		return qContainerHeadlinePadding;
	}

	public String getQContainerHeadlineTextColor() {
		return qContainerHeadlineTextColor;
	}

	public String getQTextDisplayMode() {
		return qTextDisplayMode;
	}

	public String getQuestionAnswersMargin() {
		return questionAnswersMargin;
	}

	public String getQuestionBorder() {
		return questionBorder;
	}

	public int getQuestionColumns() {
		return questionColumns;
	}

	public String getQuestionVerticalAlign() {
		return questionVerticalAlign;
	}

	public Boolean getShowButton() {
		return showButton;
	}

	public Boolean getShowQContainerHeadline() {
		return showQContainerHeadline;
	}

	public Boolean getShowQuestionHeadline() {
		return showQuestionHeadline;
	}

	public String getUnansweredQuestionBackground() {
		return unansweredQuestionBackground;
	}

	public boolean isShowImageMapRegionOnMouseOver() {
		return showImageMapRegionOnMouseOver;
	}

	public void setAdditionalCSSClass(String additionalCSSClass) {
		this.additionalCSSClass = additionalCSSClass;
	}

	public void setAdditionalCSSStyle(String additionalCSSStyle) {
		this.additionalCSSStyle = additionalCSSStyle;
	}

	public void setAnswerChoiceType(String answerChoiceType) {
		this.answerChoiceType = answerChoiceType;
	}

	public void setAnswerColumns(int answerColumns) {
		this.answerColumns = answerColumns;
	}

	public void setAnsweredQuestionBackground(String answeredQuestionBackground) {
		this.answeredQuestionBackground = answeredQuestionBackground;
	}

	public void setAnswerFont(String answerFont) {
		this.answerFont = answerFont;
	}

	public void setAnswerTextColor(String answerTextColor) {
		this.answerTextColor = answerTextColor;
	}

	public void setCurrentQuestionBackground(String currentQuestionBackground) {
		this.currentQuestionBackground = currentQuestionBackground;
	}

	public void setFastAnswer(Boolean fastAnswer) {
		this.fastAnswer = fastAnswer;
	}

	public void setGridgap(int gridgap) {
		this.gridgap = gridgap;
	}

	public void setHeadlineBackground(String headlineBackground) {
		this.headlineBackground = headlineBackground;
	}

	public void setHeadlineBorder(String headlineBorder) {
		this.headlineBorder = headlineBorder;
	}

	public void setHeadlineFont(String headlineFont) {
		this.headlineFont = headlineFont;
	}

	public void setHeadlineMargin(String headlineMargin) {
		this.headlineMargin = headlineMargin;
	}

	public void setHeadlineTextColor(String headlineTextColor) {
		this.headlineTextColor = headlineTextColor;
	}

	public void setImageMapHeadlineText(String imageMapHeadlineText) {
		this.imageMapHeadlineText = imageMapHeadlineText;
	}

	public void setInputHeight(String inputHeight) {
		this.inputHeight = inputHeight;
	}

	public void setInputWidth(String inputWidth) {
		this.inputWidth = inputWidth;
	}

	public void setMmInfo(MMInfo mmInfo) {
		this.mmInfo = mmInfo;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

	public void setQContainerBorder(String containerBorder) {
		qContainerBorder = containerBorder;
	}

	public void setQContainerHeadlineAltText(String containerHeadlineAltText) {
		qContainerHeadlineAltText = containerHeadlineAltText;
	}

	public void setQContainerHeadlineBackground(
			String containerHeadlineBackground) {
		qContainerHeadlineBackground = containerHeadlineBackground;
	}

	public void setQContainerHeadlineBorder(String containerHeadlineBorder) {
		qContainerHeadlineBorder = containerHeadlineBorder;
	}

	public void setQContainerHeadlineFont(String containerHeadlineFont) {
		qContainerHeadlineFont = containerHeadlineFont;
	}

	public void setQContainerHeadlinePadding(String containerHeadlinePadding) {
		qContainerHeadlinePadding = containerHeadlinePadding;
	}

	public void setQContainerHeadlineTextColor(String containerHeadlineTextColor) {
		qContainerHeadlineTextColor = containerHeadlineTextColor;
	}

	public void setQTextDisplayMode(String textDisplayMode) {
		qTextDisplayMode = textDisplayMode;
	}

	public void setQuestionAnswersMargin(String questionAnswersMargin) {
		this.questionAnswersMargin = questionAnswersMargin;
	}

	public void setQuestionBorder(String questionBorder) {
		this.questionBorder = questionBorder;
	}

	public void setQuestionColumns(int questionColumns) {
		this.questionColumns = questionColumns;
	}

	public void setQuestionVerticalAlign(String questionVerticalAlign) {
		this.questionVerticalAlign = questionVerticalAlign;
	}

	public void setShowButton(Boolean showButton) {
		this.showButton = showButton;
	}

	public void setShowImageMapRegionOnMouseOver(
			boolean showImageMapRegionOnMouseOver) {
		this.showImageMapRegionOnMouseOver = showImageMapRegionOnMouseOver;
	}

	public void setShowQContainerHeadline(Boolean showQContainerHeadline) {
		this.showQContainerHeadline = showQContainerHeadline;
	}

	public void setShowQuestionHeadline(Boolean showQuestionHeadline) {
		this.showQuestionHeadline = showQuestionHeadline;
	}

	public void setUnansweredQuestionBackground(
			String unansweredQuestionBackground) {
		this.unansweredQuestionBackground = unansweredQuestionBackground;
	}

	@Override
	public String toString() {
		return "<QuestionPageLayout gridgap='" + gridgap + "' />";
	}

}
