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

package de.d3web.empiricaltesting.casevisualization.jung;

import java.awt.Color;

/**
 * This class converts HTML Colors to Java AWT Colors.
 * 
 * @author Sebastian Furth
 * 
 */
public final class ColorConverter {

	/**
	 * Singleton instance.
	 */
	private static ColorConverter instance = new ColorConverter();

	/**
	 * Private Constructor (ensures noninstantiability).
	 */
	private ColorConverter() {
	}

	/**
	 * Returns an instance of ColorConverter.
	 * 
	 * @return ColorConverter instance
	 */
	public static ColorConverter getInstance() {
		return instance;
	}

	/**
	 * Converts a color formatted in HTML style to a JAVA AWT color.
	 * 
	 * For example #000000 is converted to Color.BLACK
	 * 
	 * If there is an exception during the conversion Color.BLACK is returned!
	 * 
	 * @param h String color in HTML style
	 * @return java.awt.Color which is the converted HTML color if the
	 *         conversion was successful otherwise it is Color.BLACK
	 */
	public Color convert(String h) {

		try {
			return new Color(hexToR(h), hexToG(h), hexToB(h));
		}
		catch (Exception e) {
			return Color.BLACK;
		}

	}

	private int hexToR(String htmlcolor) {
		return Integer.parseInt((cutHex(htmlcolor)).substring(0, 2), 16);
	}

	private int hexToG(String htmlcolor) {
		return Integer.parseInt((cutHex(htmlcolor)).substring(2, 4), 16);
	}

	private int hexToB(String htmlcolor) {
		return Integer.parseInt((cutHex(htmlcolor)).substring(4, 6), 16);
	}

	private String cutHex(String h) {
		return h.charAt(0) == '#' ? h.substring(1, 7) : h;
	}

}
