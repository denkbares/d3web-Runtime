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
 * Created on 16.08.2004 by Chris
 *  
 */
package de.d3web.caserepository.addons.train.findings;

public class Rating {
	public static Rating HIGH = new Rating("stark dafür", "P*");

	public static Rating NORMAL = new Rating("dafür", "P");

	public static Rating AGAINST = new Rating("dagegen", "N");

	private static final Rating[] all = new Rating[] { HIGH, NORMAL, AGAINST };

	public static Rating getRating(String symbol) {
		for (int i = 0; i < all.length; i++) {
			Rating anyRating = all[i];
			if (anyRating.getSymbol().equals(symbol)) {
				return anyRating;
			}
		}
		return null;
	}

	private String name;

	private String symbol;

	private Rating(String name, String symbol) {
		super();
		this.name = name;
		this.symbol = symbol;
	}

	public String toString() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

}