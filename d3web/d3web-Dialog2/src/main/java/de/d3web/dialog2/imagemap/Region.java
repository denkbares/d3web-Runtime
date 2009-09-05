package de.d3web.dialog2.imagemap;

public class Region {

    private String questionID;

    private String shape;

    private String coords;

    private boolean rotate;

    private boolean isMC;

    private String answerID;

    private boolean useOrigin; // Use origin coordinates of region for answer
			       // image

    private String textCoords;

    public Region() {
	questionID = "";
	shape = "";
	coords = "";
	rotate = false;
	isMC = false;
	answerID = "";
	useOrigin = false;
	textCoords = "";
    }

    public String getAnswerID() {
	return answerID;
    }

    public String getCoords() {
	return coords;
    }

    public String getQuestionID() {
	return questionID;
    }

    public String getShape() {
	return shape;
    }

    public String getTextCoords() {
	return textCoords;
    }

    public boolean isMC() {
	return isMC;
    }

    public boolean isRotate() {
	return rotate;
    }

    public boolean isUseOrigin() {
	return useOrigin;
    }

    public void setAnswerID(String answerID) {
	this.answerID = answerID;
    }

    public void setCoords(String coords) {
	this.coords = coords;
    }

    public void setMC(boolean isMC) {
	this.isMC = isMC;
    }

    public void setQuestionID(String questionID) {
	this.questionID = questionID;
    }

    public void setRotate(boolean rotate) {
	this.rotate = rotate;
    }

    public void setShape(String shape) {
	this.shape = shape;
    }

    public void setTextCoords(String textCoords) {
	this.textCoords = textCoords;
    }

    public void setUseOrigin(boolean useOrigin) {
	this.useOrigin = useOrigin;
    }

    @Override
    public String toString() {
	return "<Region questionID=" + questionID + " shape=" + shape
		+ " coords=" + coords + " rotate=" + rotate + " isMC=" + isMC
		+ " answerID=" + answerID + " useOrigin=" + useOrigin
		+ " textCoords=" + textCoords + " />";
    }

}
