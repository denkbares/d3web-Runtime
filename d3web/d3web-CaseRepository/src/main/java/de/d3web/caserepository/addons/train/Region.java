package de.d3web.caserepository.addons.train;

import de.d3web.caserepository.XMLCodeGenerator;

public class Region implements XMLCodeGenerator {
	
	/* type is one of the region types
	 * weight is the weight of this region
	 * 
	 * when type is rectangle: x,y are top-left coords, a is the length of the top side, b is the length of the left side
	 * when type is ellipsis: x,y are center coords, a is horizontal radius, b is vertical radius
	 * when type is polygon: x,y are start coords, coords are all followup coords, the last point must not be x,y because the polygon is automatically closed
	 */
	
	private double weight;
	private int type;
	private int a;
	private int b;
	private int x;
	private int y;
	private int[] coords;
		
	public final static int REGIONTYPE_RECTANGLE = 0;
	public final static int REGIONTYPE_ELLIPSIS = 1;
	public final static int REGIONTYPE_POLYGON = 2;
	
	public Region cloneMe() {
	    Region res = new Region();
	    res.weight = this.weight;
	    res.type = this.type;
	    res.a = this.a;
	    res.b = this.b;
	    res.x = this.x;
	    res.y = this.y;
	    res.coords = new int[this.coords.length];
	    for (int i = 0; i < this.coords.length; i++) {
	        res.coords[i] = this.coords[i];
	    }
	    return res;
	}

	/**
	 * Returns the a.
	 * @return int
	 */
	public int getA() {
		return a;
	}

	/**
	 * Sets the a.
	 * @param a The a to set
	 */
	public void setA(int a) {
		this.a = a;
	}

	/**
	 * Returns the b.
	 * @return int
	 */
	public int getB() {
		return b;
	}

	/**
	 * Sets the b.
	 * @param b The b to set
	 */
	public void setB(int b) {
		this.b = b;
	}

	/**
	 * Returns the coords.
	 * @return int[]
	 */
	public int[] getCoords() {
		return coords;
	}

	/**
	 * Sets the coords.
	 * @param coords The coords to set
	 */
	public void setCoords(int[] coords) {
		this.coords = coords;
	}

	/**
	 * Returns the type.
	 * @return int
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Returns the x.
	 * @return int
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the y.
	 * @return int
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns the weight.
	 * @return double
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Sets the weight.
	 * @param weight The weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Region))
			return false;
			
		Region other = (Region) obj;
		
		if (getType() != other.getType()
			|| getA() != other.getA()
			|| getB() != other.getB()
			|| getX() != other.getX()
			|| getY() != other.getY()
			|| getWeight() != other.getWeight()
			)
			return false;
		
		if (getCoords() == null && other.getCoords() == null)
			return true;
		else {
			try {
				for (int i = 0; i < getCoords().length; i++) {
					if (getCoords()[i] != other.getCoords()[i])
						return false;
				}
			} catch (Exception ex) {
				return false;
			}
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer xmlCode = new StringBuffer();
		String type = "";
		if (getType() == Region.REGIONTYPE_RECTANGLE) {
			type="rectangle";
		} else if (getType() == Region.REGIONTYPE_ELLIPSIS) {
			type="ellipsis";
		} else if (getType() == Region.REGIONTYPE_POLYGON) {
			type="polygon";
		}
		xmlCode.append(
			"<Region" +			" type=\"" + type + "\"" +			" weight=\"" + getWeight() + "\"" +			" x=\"" + getX() + "\"" +			" y=\"" + getY() + "\""
		);
		if (getType() != Region.REGIONTYPE_POLYGON) {
			xmlCode.append(" a=\"" + getA() + "\" b=\"" + getB() + "\" />\n");
		} else {
			xmlCode.append(">\n");
			xmlCode.append("<Coords>\n");
			for (int k = 0; k < getCoords().length / 2; k++)
				xmlCode.append ("<Coord x=\"" + getCoords()[k*2] + "\" y=\"" + getCoords()[k*2+1] + "\"/>");
			xmlCode.append("</Coords>\n");
			xmlCode.append("</Region>\n");
		}
		return xmlCode.toString();
	}


}
