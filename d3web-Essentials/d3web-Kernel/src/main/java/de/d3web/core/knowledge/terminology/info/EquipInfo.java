/*
 * Copyright (C) 2017 denkbares GmbH, Germany
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

package de.d3web.core.knowledge.terminology.info;

/**
 * @author Jonas Müller
 * @created 18.12.17
 */
public class EquipInfo {

	private final String name;
	private final String model;
	private final String serial;

	public EquipInfo(String name, String model, String serial) {
		this.name = name;
		this.model = model;
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public String getModel() {
		return model;
	}

	public String getSerial() {
		return serial;
	}

	@Override
	public String toString() {
		return "EquipInfo{" +
				name + '|' +
				model + '|' +
				serial +
				'}';
	}

	public static EquipInfo valueOf(String s) {
		String[] split = s.substring(10, s.length() - 2).split("\\|");
		return new EquipInfo(split[0], split[1], split[2]);
	}
}
