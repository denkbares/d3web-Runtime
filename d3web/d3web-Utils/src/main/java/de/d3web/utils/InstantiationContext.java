/*
 * Copyright (C) 2015 denkbares GmbH
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
package de.d3web.utils;

/**
 * Should be used in combination with @Link{Instantion} to specifiy the
 * context of a certain instantiation. Therefor the @Link{getOrigin} method
 * should return an appropriate verbalization of the instantiation's origin.
 * This can be a plugin definition or an entry in a configuration file.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 08.06.15
 */
public interface InstantiationContext {

	/**
	 * Returns a textual description of the instantiation's origin, e.g. a plugin identifier,
	 * a manifest file etc.
	 *
	 * @return The origin of the instantiation.
	 */
	String getOrigin();

}
