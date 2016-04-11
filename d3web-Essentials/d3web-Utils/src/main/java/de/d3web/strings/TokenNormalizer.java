/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.strings;

/**
 * Interface to convert a string token (term) into a normalized form.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 17.04.2015
 */
public interface TokenNormalizer {
	/**
	 * Returns the normalized token for the specified term. The term string should consist only of
	 * a single term, otherwise the results are not predictable.
	 *
	 * @param term the term to be normalized to a token
	 * @return the normalized token
	 * @throws NullPointerException if the specified term is null
	 */
	String normalize(String term);
}
