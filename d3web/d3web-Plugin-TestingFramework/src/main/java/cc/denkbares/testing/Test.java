/*
 * Copyright (C) 2012 denkbares GmbH
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
package cc.denkbares.testing;

/**
 * 
 * @author jochenreutelshofer
 * @created 04.05.2012
 */
public interface Test<T> {

	// public static final String PLUGIN_ID = "d3web-Kernel-ExtensionPoints";

	public static final String PLUGIN_ID =
			"d3web-Plugin-TestingFramework-ExtensionPoints";
	public static final String EXTENSION_POINT_ID = "Test";

	public Message execute(T testObject, String[] args);

	public ArgsCheckResult checkArgs(String[] args);

	public Class<T> getTestObjectClass();

}
