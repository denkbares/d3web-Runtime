/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.dialog2.imagemap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.controller.KBLoadController;
import de.d3web.dialog2.util.DialogUtils;

public class ImageMapBean {

    private String srcDir;

    public static final String IMAGEMAP_FILE_STRING = "imagemap.xml";

    private List<Image> images;

    private Map<String, Image> qIDimageMap;

    public static Logger logger = Logger.getLogger(ImageMapBean.class);

    public Image getImageForQList(List<Question> qList) {
	for (Question question : qList) {
	    if (qIDimageMap.containsKey(question.getId())) {
		return qIDimageMap.get(question.getId());
	    }
	}
	return null;
    }

    public List<Image> getImages() {
	return images;
    }

    public String getSrcDir() {
	return srcDir;
    }

    public boolean hasImagesForQContainer(List<Question> qList) {
	if (images != null && images.size() > 0) {
	    for (Question question : qList) {
		if (qIDimageMap.containsKey(question.getId())) {
		    return true;
		}
	    }
	}
	return false;
    }

    public void init() {
	SAXBuilder builder;
	Document doc;
	Element root = null;

	KBLoadController kBLoadController = DialogUtils.getKBLoadBean();
	String destPath = ResourceRepository.getInstance()
		.getBasicSettingValue(ResourceRepository.MULTIMEDIAPATH)
		.replaceAll("\\$kbid\\$", kBLoadController.getKbID());

	srcDir = destPath.replaceAll("\\$webapp_path\\$/", "");

	File imageMapFile = new File(DialogUtils.getRealPath(destPath),
		IMAGEMAP_FILE_STRING);
	images = new ArrayList<Image>();
	qIDimageMap = new HashMap<String, Image>();
	try {
	    builder = new SAXBuilder();
	    doc = builder.build(imageMapFile);
	    root = doc.getRootElement();
	} catch (Exception e) {
	    logger.info("No Imagemap available for this KB.");
	    return;
	}

	List<Element> imageElements = root.getChild("Images").getChildren();
	for (Element imageElement : imageElements) {
	    Image image = new Image(imageElement.getAttributeValue("src"));

	    // Add Regions
	    List<Element> regionElements = imageElement.getChild("Regions")
		    .getChildren();
	    for (Element regionElement : regionElements) {
		Region region = new Region();

		region.setQuestionID(regionElement
			.getAttributeValue("questionID"));
		region.setShape(regionElement.getAttributeValue("shape"));
		region.setCoords(regionElement.getAttributeValue("coords"));

		if (regionElement.getAttribute("rotate") != null) {
		    if (regionElement.getAttributeValue("rotate")
			    .equalsIgnoreCase("YES")) {
			region.setRotate(true);
		    }
		}
		if (regionElement.getAttribute("isMC") != null) {
		    if (regionElement.getAttributeValue("isMC")
			    .equalsIgnoreCase("YES")) {
			region.setMC(true);
			region.setAnswerID(regionElement
				.getAttributeValue("answerID"));
		    }
		}

		if (regionElement.getAttribute("useOrigin") != null) {
		    if (regionElement.getAttributeValue("useOrigin")
			    .equalsIgnoreCase("YES")) {
			region.setUseOrigin(true);
		    }
		}

		if (regionElement.getAttribute("textCoords") != null) {
		    region.setTextCoords(regionElement
			    .getAttributeValue("textCoords"));
		}
		image.getRegions().add(region);
		// put this questionid->Image in map
		qIDimageMap.put(regionElement.getAttributeValue("questionID"),
			image);
	    }

	    // Add ImageMapAnswerIcons
	    if (imageElement.getChild("AnswerImages") != null) {
		List<Element> answerImageElements = imageElement.getChild(
			"AnswerImages").getChildren();
		for (Element answerImageElement : answerImageElements) {
		    ImageMapAnswerIcon answerImage = new ImageMapAnswerIcon(
			    answerImageElement.getAttributeValue("src"));
		    answerImage.setId(answerImageElement
			    .getAttributeValue("id"));
		    if (answerImageElement.getAttributeValue("coords") != null) {
			answerImage.setCoords(answerImageElement
				.getAttributeValue("coords"));
		    }
		    image.getAnswerImages().add(answerImage);
		}
	    }
	    images.add(image);
	}
    }

    public void reset() {
	images = new ArrayList<Image>();
	qIDimageMap = new HashMap<String, Image>();
    }
}
