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
