package de.d3web.dialog2.imagemap;

import java.util.ArrayList;
import java.util.List;

public class Image {

    private String src;

    private List<Region> regions;

    private List<ImageMapAnswerIcon> answerImages;

    public Image(String src) {
	this.src = src;
	regions = new ArrayList<Region>();
	answerImages = new ArrayList<ImageMapAnswerIcon>();
    }

    public List<ImageMapAnswerIcon> getAnswerImages() {
	return answerImages;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public String getSrc() {
	return src;
    }

    public void setSrc(String src) {
	this.src = src;
    }

    @Override
    public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("<Image src=" + src + ">");
	for (Region r : regions) {
	    buf.append("\n  " + r.toString());
	}
	for (ImageMapAnswerIcon a : answerImages) {
	    buf.append("\n  " + a.toString());
	}

	buf.append("\n</Image>\n");
	return buf.toString();
    }
}
