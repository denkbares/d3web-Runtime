/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.empiricaltesting.casevisualization;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.session.blackboard.Fact;

/**
 * Class that represents the label to be used for the generated case
 * visualization
 * 
 * @author volker_belli
 * @created 21.07.2011
 */
public class Label {

	private final List<String> aboutKeys = new LinkedList<String>();
	private final Map<String, String> aboutEntries = new HashMap<String, String>();
	private final List<Fact> seedEntries = new LinkedList<Fact>();

	public Label() {
	}

	public void addAboutEntry(String key, String value) {
		if (!this.aboutEntries.containsKey(key)) {
			this.aboutKeys.add(key);
		}
		this.aboutEntries.put(key, value);
	}

	public void addSeedEntry(Fact seedFact) {
		this.seedEntries.add(seedFact);
	}

	public List<Fact> getSeedEntries() {
		return Collections.unmodifiableList(seedEntries);
	}

	public List<String> getAboutKeys() {
		return Collections.unmodifiableList(this.aboutKeys);
	}

	public String getAboutValue(String key) {
		return this.aboutEntries.get(key);
	}
}
