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
package de.d3web.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This KnowledgeSlice is used to store the configuration of the extensions at
 * the KnowledgeBase
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginConfig implements KnowledgeSlice {

	// TODO change
	private final Map<String, PluginEntry> entries = new HashMap<String, PluginEntry>();

	public static final KnowledgeKind<PluginConfig> KNOWLEDGE_KIND = new KnowledgeKind<PluginConfig>(
			"ExtensionConfig", PluginConfig.class);

	/**
	 * Creates a new PluginConfig and adds itself to the KnowledgeBase
	 * 
	 * @param kb
	 */
	public PluginConfig(KnowledgeBase kb) {
		super();
		kb.getKnowledgeStore().addKnowledge(KNOWLEDGE_KIND, this);
	}

	/**
	 * Adds a ExtensionEntry to this KnowledgeSlice
	 * 
	 * @param entry ExtensionEntry containing the configuration of one Extension
	 */
	public void addEntry(PluginEntry entry) {
		entries.put(entry.getPlugin().getPluginID(), entry);
	}

	/**
	 * Returns an unmodifiable list of all ExtensionEntries contained in this
	 * KnowledgeSlice
	 * 
	 * @return a list of ExtensionEntries
	 */
	public Collection<PluginEntry> getEntries() {
		return entries.values();
	}

	/**
	 * Returns the Pluginentry for the Plugin with the specified id
	 * 
	 * @param id of the Plugin
	 * @return Pluginentry of the Plugin
	 */
	public PluginEntry getPluginEntry(String id) {
		return entries.get(id);
	}

	/**
	 * Extracts a PluginConfig from a kb, if none is found one will be created
	 * 
	 * @param kb KnowledgeBase
	 * @return PluginConfig of the kb
	 */
	public static PluginConfig getPluginConfig(KnowledgeBase kb) {
		PluginConfig pc = kb.getKnowledgeStore().getKnowledge(KNOWLEDGE_KIND);
		// if there is no knowledge slice PluginConfiguration, create one
		if (pc == null) {
			pc = new PluginConfig(kb);
		}
		return pc;
	}

	/**
	 * Returns the Plugin Entry of the Plugin with the given id
	 * 
	 * @created 25.06.2010
	 * @param kb KnowledgeBase
	 * @param id ID of the Plugin
	 * @return PluginEntry
	 */
	public static PluginEntry getEntry(KnowledgeBase kb, String id) {
		return getPluginConfig(kb).getPluginEntry(id);
	}

	/**
	 * Adds a plugin entry to the KnowledgeBase
	 * 
	 * @created 25.06.2010
	 * @param kb KnowledgeBase
	 * @param entry PluginEntry
	 */
	public static void addPluginEntry(KnowledgeBase kb, PluginEntry entry) {
		getPluginConfig(kb).addEntry(entry);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + getEntries().toString();
	}

}
