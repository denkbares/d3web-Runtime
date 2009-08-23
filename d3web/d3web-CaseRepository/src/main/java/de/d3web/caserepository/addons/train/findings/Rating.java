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