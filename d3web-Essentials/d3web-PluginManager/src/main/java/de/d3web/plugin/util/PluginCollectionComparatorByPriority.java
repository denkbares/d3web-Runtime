/*
 * Copyright (C) 2009 denkbares GmbH
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

package de.d3web.plugin.util;

import java.util.Comparator;

import de.d3web.plugin.Extension;

/**
 * Compares Plugins by its priority. Using this Class to sort arrays or
 * collections will sort the plugins ascending. This means, plugins with a
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginCollectionComparatorByPriority implements Comparator<Extension> {

	@Override
	public int compare(Extension o1, Extension o2) {
		double p1;
		try {
			p1 = o1.getPriority();
		}
		catch (Exception e) {
			p1 = 0;
		}
		double p2;
		try {
			p2 = o2.getPriority();
		}
		catch (Exception e) {
			p2 = 0;
		}
		if (p1 > p2) {
			return 1;
		}
		else if (p1 < p2) {
			return -1;
		}
		else {
			return 0;
		}
	}

}
