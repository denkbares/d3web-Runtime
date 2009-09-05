package de.d3web.dialog2.imagemap;

public class ImageMapAnswerIcon {

    private String id;

    private String coords;

    private String src;

    public ImageMapAnswerIcon(String src) {
	this.src = src;
	id = "";
	coords = "";
    }

    public String getCoords() {
	return coords;
    }

    public String getId() {
	return id;
    }

    public String getSrc() {
	return src;
    }

    public void setCoords(String coords) {
	this.coords = coords;
    }

    public void setId(String id) {
	this.id = id;
    }

    public void setSrc(String src) {
	this.src = src;
    }

    @Override
    public String toString() {
	return "<AnswerImage id=" + id + " coords=" + coords + " src=" + src
		+ " />";
    }

}
