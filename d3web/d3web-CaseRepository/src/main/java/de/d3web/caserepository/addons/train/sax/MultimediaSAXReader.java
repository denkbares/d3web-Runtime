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

/*
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.addons.train.Feature;
import de.d3web.caserepository.addons.train.MimeType;
import de.d3web.caserepository.addons.train.Multimedia;
import de.d3web.caserepository.addons.train.MultimediaItem;
import de.d3web.caserepository.addons.train.Region;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.QuestionNum;
import de.d3web.core.terminology.QuestionText;

/**
 * 22.09.2003 18:09:45
 * @author hoernlein
 */
public class MultimediaSAXReader extends AbstractTagReader {
	
	private Multimedia m = null;
	private String start = null;
	private MultimediaItem mi = null;
	private Feature f = null;
	private QASet q = null;
	private Set regions = null;
	private Region r = null;
	private List c = null;
	
	private int warnFeatureTag = 0;

	protected MultimediaSAXReader(String id) { super(id); }
	private static MultimediaSAXReader instance;
	private MultimediaSAXReader() { this("MultimediaSAXReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new MultimediaSAXReader();
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"Multimedia",
			"Image",
			"Text",
			"WMPVideo",
			"Title",
			"Features",
			"Feature",
			"AnswerInterval",
			"Regions",
			"Region",
			"Coord"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("Multimedia"))
			startMultimedia(attributes);
		else if (qName.equals("Image"))
			startImage(attributes);
		else if (qName.equals("Text"))
			startText(attributes);
		else if (qName.equals("WMPVideo"))
			startWMPVideo(attributes);
		else if (qName.equals("Title"))
			; // do nothing
		else if (qName.equals("Features"))
			; // do nothing
		else if (qName.equals("Feature"))
			startFeature(attributes);
		else if (qName.equals("AnswerInterval"))
			startAnswerInterval(attributes);
		else if (qName.equals("Regions"))
			startRegions(attributes);
		else if (qName.equals("Region"))
			startRegion(attributes);
		else if (qName.equals("Coord"))
			startCoord(attributes);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("Multimedia"))
			endMultimedia();
		else if (qName.equals("Image") || qName.equals("WMPVideo") || qName.equals("Text"))
			endItem();
		else if (qName.equals("Title"))
			endTitle();
		else if (qName.equals("Features"))
			; // do nothing
		else if (qName.equals("Feature"))
			endFeature();
		else if (qName.equals("AnswerInterval"))
			; // do nothing
		else if (qName.equals("Regions"))
			endRegions();
		else if (qName.equals("Region"))
			endRegion();
		else if (qName.equals("Coord"))
			; // do nothing
	}

	private void startMultimedia(Attributes attributes) {
		m = new Multimedia();
		start = attributes.getValue("start");
	}

	private void endMultimedia() {
		if (start != null)
			m.setStartItem(m.getMultimediaItemFor(start));
		getCaseObject().setMultimedia(m);
		m = null;
		if (warnFeatureTag > 0) {
		    Logger.getLogger(this.getClass().getName()).warning(warnFeatureTag + " Feature-tags used old 'question'-attribute");
		    warnFeatureTag = 0;
		}
	}

	private void startItem(Attributes attributes) {
		String id = attributes.getValue("id");
		mi.setId(id);
		
		try {
			int color = Integer.parseInt(attributes.getValue("color"));
			mi.setColor(color);
		} catch (NumberFormatException ex) { /* default color is ok */ }
		
		try {
			int width = Integer.parseInt(attributes.getValue("width"));
			mi.setWidth(width);
		} catch (NumberFormatException ex) { /* default width is ok */ }
		
		try {
			int height = Integer.parseInt(attributes.getValue("height"));
			mi.setHeight(height);
		} catch (NumberFormatException ex) { /* default height is ok */ }
		
		String url = attributes.getValue("url");
		mi.setURL(url);
		
		try {
		    int n = Integer.parseInt(attributes.getValue("orderNumber"));
		    mi.setOrderNumber(n);
		} catch (NumberFormatException ex) { /* default value is ok */ }
	}

	private void startImage(Attributes attributes) {
		mi = new MultimediaItem();
		mi.setMimeType(MimeType.IMAGE);
		startItem(attributes);
	}

	private void startText(Attributes attributes) {
	    if (m == null)
	        return;
	    else {
			mi = new MultimediaItem();
			mi.setMimeType(MimeType.TEXT);
			startItem(attributes);
	    }
	}

	private void startWMPVideo(Attributes attributes) {
		mi = new MultimediaItem();
		mi.setMimeType(MimeType.WMPVIDEO);
		startItem(attributes);
	}

	private void endItem() {
	    if (m == null)
	        return;
	    else {
			m.addMultimediaItem(mi);
			mi = null;
	    }
	}

	private void endTitle() {
		if (mi == null)
			return;
		else
			mi.setTitle(getTextBetweenCurrentTag());
	}

	private void startFeature(Attributes attributes) {
		f = new Feature();
		f.setMultimediaItem(mi);
		
		try {
			float weight = Float.parseFloat(attributes.getValue("weight"));
			f.setWeight(weight);
		} catch (Exception ex) {
		    f.setWeight(1);
		}
		
		String qS = attributes.getValue("qaset");
		if (qS == null) {
		    qS = attributes.getValue("question");
		    warnFeatureTag++;
		}
		
		q = getKnowledgeBase().searchQuestion(qS);
		if (q == null) {
		    q = getKnowledgeBase().searchQContainers(qS);
		    if (q == null)
		        Logger.getLogger(this.getClass().getName()).warning("can't resolve qaset " + qS);
		} else {

		    String avalue = attributes.getValue("answer");
		    Answer a = null;
		    if (q instanceof QuestionChoice)
		        a = ((QuestionChoice) q).getAnswer(null, avalue);
		    else if (q instanceof QuestionText)
		        a = ((QuestionText) q).getAnswer(null, avalue);
		    else if (q instanceof QuestionNum) {
		        if (avalue != null) {
		            a = ((QuestionNum) q).getAnswer(null, Double.valueOf(avalue));
		        }
		    } else {
		        Logger.getLogger(this.getClass().getName()).warning("no way to process features for questions of type " + q.getClass());
		    }
		    if (a != null)
		        f.setAnswer(a);
		    
		}
		    
		f.setQASet(q);
		
	}

	private void endFeature() {
		mi.addFeature(f);
		q = null;
		f = null;
	}

	private void startAnswerInterval(Attributes attributes) {
		Double lower = Double.valueOf(attributes.getValue("lowerBoundary"));
		Double upper = Double.valueOf(attributes.getValue("upperBoundary"));
		f.setAnswerInterval(((QuestionNum) q).getAnswer(null, lower),
		((QuestionNum) q).getAnswer(null, upper));
	}

	private void startRegions(Attributes attributes) {
		regions = new HashSet();
	}

	private void endRegions() {
		f.setRegions(regions);
	}

	private void startRegion(Attributes attributes) {
		r = new Region();
		
		String type = attributes.getValue("type");
		if ("rectangle".equals(type))
			r.setType(Region.REGIONTYPE_RECTANGLE);
		else if ("ellipsis".equals(type))
			r.setType(Region.REGIONTYPE_ELLIPSIS);
		else if ("polygon".equals(type))
			r.setType(Region.REGIONTYPE_POLYGON);
		else
		Logger.getLogger(this.getClass().getName()).warning("no way to process Regions of type " + type);
		
		double weight = Double.parseDouble(attributes.getValue("weight"));
		r.setWeight(weight);
		
		int x = Integer.parseInt(attributes.getValue("x"));
		r.setX(x);
		
		int y = Integer.parseInt(attributes.getValue("y"));
		r.setY(y);
		
		if (r.getType() == Region.REGIONTYPE_RECTANGLE || r.getType() == Region.REGIONTYPE_ELLIPSIS) {
			int a = Integer.parseInt(attributes.getValue("a"));
			r.setA(a);
			
			int b = Integer.parseInt(attributes.getValue("b"));
			r.setB(b);
		} else
			c = new LinkedList();
		
	}

	private void endRegion() {
		if (c != null) {
			int[] coords = new int[c.size()];
			int i = 0;
			Iterator iter = c.iterator();
			while (iter.hasNext())
				coords[i++] = ((Integer) iter.next()).intValue();
			r.setCoords(coords);
			c = null;
		}
		regions.add(r);
		r = null;
	}
	
	private void startCoord(Attributes attributes) {
		Integer x = Integer.valueOf(attributes.getValue("x"));
		Integer y = Integer.valueOf(attributes.getValue("y"));
		c.add(x);
		c.add(y);
	}
		
}
