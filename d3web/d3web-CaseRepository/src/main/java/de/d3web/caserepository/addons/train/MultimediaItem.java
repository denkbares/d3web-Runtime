package de.d3web.caserepository.addons.train;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import de.d3web.caserepository.XMLCodeGenerator;

/**
 * 
 * @author: Christian Betz
 */
public class MultimediaItem implements XMLCodeGenerator {
	
	/**
	 * @deprecated use MimeTypes instead
	 */
	public static final int IMAGE = 0;
	
	/**
	 * @deprecated use MimeTypes instead
	 */
	public static final int TEXT = 1;
	
	private int color = 0;
	private int width = 0;
	private int height = 0;
	private String id = "";
	private String title = "";
	private String url = "";
	private String comment = "";
	private Collection<Feature> features = new LinkedList<Feature>();
	private MimeType mimeType = null;
	private int orderNumber = -1;
	
	public MultimediaItem cloneMe() {
	    MultimediaItem res = new MultimediaItem();
	    res.color = this.color;
	    res.width = this.width;
	    res.height = this.height;
	    res.id = this.id;
	    res.title = this.title;
	    res.url = this.url;
	    res.comment = this.comment;
	    res.mimeType = this.mimeType;
	    res.features = new LinkedList<Feature>();
	    Iterator iter = this.features.iterator();
	    while (iter.hasNext())
	        res.features.add(((Feature) iter.next()).cloneMe());
	    return res;
	}

	/**
	 * @param feature Feature
	 */
	public void addFeature(Feature feature) {
		features.add(feature);
	}
	
	/**
	 * @param i int
	 */
	public void removeFeature(int i) {
		features.remove(i);
	}

	/**
	 * @return Collection
	 */
	public Collection<Feature> getFeatures() {
		return features;
	}

	/**
	 * @return String
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id String
	 */
	public void setId(String id) {
		this.id = id;
		if (Character.isDigit(this.id.charAt(0)))
		    this.id = "m" + this.id;
	}

	/**
	 * @return int
	 * @deprecated Use getMimeType instead
	 */
	public int getType() {
		if (getMimeType().isSubtypeOf(MimeType.IMAGE)) {
			return IMAGE;
		} else if (getMimeType().isSubtypeOf(MimeType.TEXT)) {
			return TEXT;
		} else
			return -1;
	}

	/**
	 * @return MimeType
	 */
	public MimeType getMimeType() {
		return mimeType;
	}

	/**
	 * @param newMimeType MimeType
	 */
	public void setMimeType(MimeType newMimeType) {
		mimeType = newMimeType;
	}

	/**
	 * @return String
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title String
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param title String
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getURL() {
		return url;
	}

	/**
	 * @param url String
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * Returns the color.
	 * @return int
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Sets the color.
	 * @param color The color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * Returns the height.
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height.
	 * @param height The height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns the width.
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return Returns the orderNumber.
	 */
	public int getOrderNumber() {
	    return orderNumber;
	}
	
	/**
	 * @param orderNumber The orderNumber to set.
	 */
	public void setOrderNumber(int orderNumber) {
	    this.orderNumber = orderNumber;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MultimediaItem))
			return false;
		if (obj == this)
			return true;

		MultimediaItem other = (MultimediaItem) obj;
		if (!getId().equals(other.getId())
			|| getType() != other.getType()
			|| !getTitle().equals(other.getTitle())
			|| getOrderNumber() != other.getOrderNumber()
			|| !getComment().equals(other.getComment())
			|| !getURL().equals(other.getURL())
			|| getColor() != other.getColor()
			|| getHeight() != other.getHeight()
			|| getWidth() != other.getWidth()
			|| !getMimeType().equals(other.getMimeType())
			|| !getFeatures().containsAll(other.getFeatures())
			|| !other.getFeatures().containsAll(getFeatures())
			)
			return false;
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer xmlCode = new StringBuffer();
		MimeType mime = getMimeType(); 
		String temp = getURL();
		if (temp.indexOf('/')>=0) {
			temp = temp.substring(temp.lastIndexOf('/')+1);
		} 
		if (mime == MimeType.IMAGE) {
			xmlCode.append(
				"<Image" +
				" id=\"" + getId() + "\"" +
				" url=\"" + temp + "\"" +
				" width=\"" + getWidth() + "\"" +
				" height=\"" + getHeight() + "\"" +
				" color=\"" + getColor() + "\"" +
				" orderNumber=\"" + getOrderNumber() + "\"" +
				">\n"
			);
		} else if (mime == MimeType.TEXT)
			xmlCode.append ("<Text" +
					" id=\"" + getId() + "\"" +
					" url=\"" + temp + "\"" +
					" orderNumber=\"" + getOrderNumber() + "\"" +
					">\n");
		else if (mime == MimeType.WMPVIDEO)
			xmlCode.append ("<WMPVideo" +
					" id=\"" + getId() + "\"" +
					" url=\"" + temp + "\"" +
					" orderNumber=\"" + getOrderNumber() + "\"" +
					">\n");
		else {
			Logger.getLogger(this.getClass().getName()).warning("can't handle MimeType '" + mime + "'");
			return "";
		}

		xmlCode.append("<Title>" + getTitle() + "</Title>\n");
		
		xmlCode.append("<Features>\n");
		Iterator iter = getFeatures().iterator();
		while (iter.hasNext())
			xmlCode.append(((Feature) iter.next()).getXMLCode());
		xmlCode.append("</Features>\n");
		
		if (mime == MimeType.IMAGE)
			xmlCode.append("</Image>\n");
		else if (mime == MimeType.TEXT)
			xmlCode.append("</Text>\n");
		else if (mime == MimeType.WMPVIDEO)
			xmlCode.append("</WMPVideo>\n");
		
		return xmlCode.toString();
	}

}