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

package de.d3web.caserepository.addons.train.dom;
import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xpath.internal.XPathAPI;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.addons.train.Feature;
import de.d3web.caserepository.addons.train.MimeType;
import de.d3web.caserepository.addons.train.Multimedia;
import de.d3web.caserepository.addons.train.MultimediaItem;
import de.d3web.caserepository.addons.train.Region;
import de.d3web.caserepository.dom.AdditionalCaseObjectCreator;
import de.d3web.caserepository.dom.CaseObjectCreator;
import de.d3web.caserepository.dom.ChildrenIterator;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;


/**
 * Insert the type's description here.
 * Creation date: (21.06.2001 10:41:39)
 * @author: Christian Betz
 */
public class MultimediaCreator implements AdditionalCaseObjectCreator {
    
    private static Set createRegions(Node node) {
    	Set regions = new HashSet();
    	NodeList children = node.getChildNodes();
    	for (int i = 0; i < children.getLength(); i++) {
    		Node child = children.item(i);
    		if (child.getNodeName().equals("Regions")) {
		    	NodeList regionNs = child.getChildNodes();
		    	for (int j = 0; j < regionNs.getLength(); j++) {
		    		Node regionN = regionNs.item(j);
		    		if (regionN.getNodeName().equals("Region")) {

		    			Region region = new Region();
		    			
		    			boolean failure = false;

	    				if (regionN.getAttributes().getNamedItem("type").getNodeValue().equals("rectangle")) {
	    					region.setType(Region.REGIONTYPE_RECTANGLE);
	    				} else if (regionN.getAttributes().getNamedItem("type").getNodeValue().equals("ellipsis")) {
	    					region.setType(Region.REGIONTYPE_ELLIPSIS);
	    				} else if (regionN.getAttributes().getNamedItem("type").getNodeValue().equals("polygon")) {
	    					region.setType(Region.REGIONTYPE_POLYGON);
	    				} else {
							Logger.getLogger(MultimediaCreator.class.getName()).warning("Region: unknown type for region: " + regionN.getAttributes().getNamedItem("type").getNodeValue());
	    					failure = true;
	    				}

	    				try {
	    					region.setWeight(Double.parseDouble(regionN.getAttributes().getNamedItem("weight").getNodeValue()));
	    				} catch (Exception ex) {
							Logger.getLogger(MultimediaCreator.class.getName()).warning("Region: something is wrong with the weight");
	    					failure = true;
	    				}

						try {
	    					region.setX(Integer.parseInt(regionN.getAttributes().getNamedItem("x").getNodeValue()));
	    					region.setY(Integer.parseInt(regionN.getAttributes().getNamedItem("y").getNodeValue()));
		    				if (region.getType() != Region.REGIONTYPE_POLYGON) {
		    					region.setA(Integer.parseInt(regionN.getAttributes().getNamedItem("a").getNodeValue()));
		    					region.setB(Integer.parseInt(regionN.getAttributes().getNamedItem("b").getNodeValue()));
		    				} else {
								// [MISC]:aha:legacy code
		    					Node coordsParent = regionN;

								// normally there should be a Coords-node
		    					NodeList temp = regionN.getChildNodes();
		    					for (int t = 0; t < temp.getLength(); t++)
		    						if (temp.item(t).getNodeName().equals("Coords"))
		    							coordsParent = temp.item(t);
		    					
		    					Collection coordsC = new Vector();
		    					NodeList coordNs = coordsParent.getChildNodes();
		    					for (int k = 0; k < coordNs.getLength(); k++) {
		    						if (coordNs.item(k).getNodeName().equals("Coord")) {
		    							coordsC.add(coordNs.item(k).getAttributes().getNamedItem("x").getNodeValue());
		    							coordsC.add(coordNs.item(k).getAttributes().getNamedItem("y").getNodeValue());
		    						}
		    					}
		    					int[] coords = new int[coordsC.size()];
		    					int p = 0;
		    					Iterator iter = coordsC.iterator();
		    					while (iter.hasNext()) {
		    						coords[p++] = Integer.parseInt((String) iter.next());
		    					}
		    					region.setCoords(coords);
			    			}
						} catch (Exception ex) {
							Logger.getLogger(MultimediaCreator.class.getName()).warning("Region: something is wrong with the coordinates");
	    					failure = true;
						}
						
		    			if (!failure) regions.add(region);
		    		}
		    	}
    		}
    	}
    	return regions;
    }
    
	public void process(CaseObjectCreator creator, Node node, CaseObjectImpl caseObject) {
		MultimediaCreator._process(creator, node, caseObject);
	}
    
	public static void _process(CaseObjectCreator creator, Node node, CaseObjectImpl caseObject) {
		Multimedia multimedia = new Multimedia();
		try {
			ChildrenIterator iter = new ChildrenIterator(node);
			while (iter.hasNext()) {
				Node child = (Node) iter.next();
				String nodeName = child.getNodeName();
				if (nodeName.equals("Image") || nodeName.equals("Text") || nodeName.equals("WMPVideo")) {

					try {
						MultimediaItem mItem = new MultimediaItem();
						if (nodeName.equals("Image")) {
							mItem.setMimeType(MimeType.IMAGE);
						} else if (nodeName.equals("Text")) {
							mItem.setMimeType(MimeType.TEXT);
						} else if (nodeName.equals("WMPVideo")) {
							mItem.setMimeType(MimeType.WMPVIDEO);
						}

						mItem.setId(CaseObjectCreator.getAttribute(child, "id", "Multimedia: id is missing"));
						mItem.setURL(CaseObjectCreator.getAttribute(child, "url", "Multimedia: url is missing"));

						boolean hasRegions = false;

						ChildrenIterator childrenIter = new ChildrenIterator(child);
						while (childrenIter.hasNext()) {
							Node itemChild = (Node) childrenIter.next();

							if (itemChild.getNodeName().equals("Title")) {
								mItem.setTitle(CaseObjectCreator.getText(itemChild, "Multimedia: empty title"));
							}

							if (itemChild.getNodeName().equals("Features")) {
								ChildrenIterator featureIter = new ChildrenIterator(itemChild);
								while (featureIter.hasNext()) {
									Node featureNode = (Node) featureIter.next();
									Feature feature = null;
									if (featureNode.getNodeName().equals("Feature")) {

										feature = new Feature();
										feature.setMultimediaItem(mItem);

										// regions

										feature.setRegions(createRegions(featureNode));
										if (!feature.getRegions().isEmpty())
											hasRegions = true;

										String weightAttr = CaseObjectCreator.getAttribute(featureNode, "weight", null);
										try {
											float weight = Float.valueOf(weightAttr).floatValue();
											feature.setWeight(weight);
										} catch (Exception e) {
											// System.err.println("Error setting weight in " + idAttr);
										}

										String question =
											CaseObjectCreator.getAttribute(
												featureNode,
												"question",
												"Feature: Attribute question is missing");
										Question quest = creator.getKnowledgeBase().searchQuestion(question);
										if (quest == null) {
											Logger.getLogger(MultimediaCreator.class.getName()).warning(
												"MultimediaCreator.process: no question found for "
												+ question
												+ " in item with id "
												+ mItem.getId());
										} else {

											feature.setQASet(quest);

											String valueOfAnswer =
												CaseObjectCreator.getAttribute(featureNode, "answer", null);
											if (valueOfAnswer != null) {
												// we just assume here that we don't have to handle any answer unknown cases
												if (quest instanceof QuestionChoice) {
													feature.setAnswer(
														((QuestionChoice) quest).getAnswer(null, valueOfAnswer));
												} else if (quest instanceof QuestionNum) {
													if (featureNode.getAttributes().getNamedItem("answer") == null) {
														// we have an answerinterval
														// <AnswerInterval lowerBoundary="2.0" upperBoundary="3.0"/>
														String lower =
															XPathAPI
																.selectSingleNode(featureNode, "AnswerInterval")
																.getAttributes()
																.getNamedItem("lowerBoundary")
																.getNodeValue();
														String upper =
															XPathAPI
																.selectSingleNode(featureNode, "AnswerInterval")
																.getAttributes()
																.getNamedItem("upperBoundary")
																.getNodeValue();
														feature.setAnswerInterval(
															((QuestionNum) quest).getAnswer(null, new Double(lower)),
															((QuestionNum) quest).getAnswer(null, new Double(upper)));
													} else {
														feature.setAnswer(
															((QuestionNum) quest).getAnswer(
																null,
																new Double(valueOfAnswer)));
													}
												} else if (quest instanceof QuestionText) {
													feature.setAnswer(
														((QuestionText) quest).getAnswer(null, valueOfAnswer));
												} else {
													Logger.getLogger(MultimediaCreator.class.getName()).warning(
														"MultimediaCreator.process not implemented for features for questions of type "
														+ quest.getClass());
												}
											}

											mItem.addFeature(feature);
										}
									}

								}
							}
						}

						if (hasRegions) {
							String color =
								CaseObjectCreator.getAttribute(child, "color", "Multimedia: color is missing");
							if (!color.equals("")) {
								try {
									mItem.setColor(Integer.parseInt(color));
								} catch (NumberFormatException ex) {
									Logger.getLogger(MultimediaCreator.class.getName()).warning(
										"possible old String color-format: " + color);
									mItem.setColor(Color.WHITE.getRGB());
								}
							}
							String width =
								CaseObjectCreator.getAttribute(child, "width", "Multimedia: width is missing");
							mItem.setWidth(Integer.parseInt(width));
							String height =
								CaseObjectCreator.getAttribute(child, "height", "Multimedia: height is missing");
							mItem.setHeight(Integer.parseInt(height));
						}

						multimedia.addMultimediaItem(mItem);
					} catch (Exception e) {
						Logger.getLogger(MultimediaCreator.class.getName()).throwing(MultimediaCreator.class.getName(), "_process", e);
					}
				}
			}

			Node startNode = node.getAttributes().getNamedItem("start");
			String id = startNode != null ? startNode.getNodeValue() : null;
			multimedia.setStartItem(multimedia.getMultimediaItemFor(id));

			caseObject.setMultimedia(multimedia);

		} catch (Exception e) {
			Logger.getLogger(MultimediaCreator.class.getName()).throwing(MultimediaCreator.class.getName(), "_process", e);
		}
	}
}