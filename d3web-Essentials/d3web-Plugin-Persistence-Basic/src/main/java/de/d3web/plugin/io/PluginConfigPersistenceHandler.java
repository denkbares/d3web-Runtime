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
package de.d3web.plugin.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.plugin.Plugin;
import de.d3web.plugin.PluginConfig;
import de.d3web.plugin.PluginEntry;
import de.d3web.plugin.PluginManager;

/**
 * A KnowledgeReader/Writer for the configuration of the extensions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginConfigPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {

	private static final String ELEMENT_SETTINGS = "settings";
	private static final String ELEMENT_PLUGINS = "plugins";
	private static final String ELEMENT_PLUGIN = "plugin";
	private static final String ELEMENT_PSMETHODS = "psmethods";
	private static final String ATTRIBUTE_ID = "ID";
	private static final String ATTRIBUTE_AUTODETECT = "autodetect";
	private static final String ATTRIBUTE_REQUIRED = "required";

	@Override
	public void read(PersistenceManager manager, KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb, stream);
		Document doc = persistence.getDocument();

		PluginConfig pc = new PluginConfig(kb);
		Element root = doc.getDocumentElement();
		if (root.getNodeName().equals(ELEMENT_SETTINGS)) {
			for (Element father : XMLUtil.getElementList(root.getChildNodes())) {
				if (father.getNodeName().equals(ELEMENT_PLUGINS)) {
					List<Element> children = XMLUtil.getElementList(father.getChildNodes());
					for (Element e : children) {
						boolean required = Boolean.parseBoolean(e.getAttribute(ATTRIBUTE_REQUIRED));
						boolean autodetect = Boolean.parseBoolean(e.getAttribute(ATTRIBUTE_AUTODETECT));
						String id = e.getAttribute(ATTRIBUTE_ID);
						Plugin plugin = PluginManager.getInstance().getPlugin(id);
						if (plugin == null) {
							if (required) {
								throw new IOException("Required plugin " + id
										+ " is not available");
							}
							else {
								plugin = new DummyPlugin(id);
							}
						}
						pc.addEntry(new PluginEntry(plugin, required, autodetect));
					}
				}
				else if (father.getNodeName().equals(ELEMENT_PSMETHODS)) {
					List<Element> children = XMLUtil.getElementList(father.getChildNodes());
					for (Element e : children) {
						kb.addPSConfig((PSConfig) persistence.readFragment(e));
					}
				}
			}
		}
	}

	@Override
	public void write(PersistenceManager manager, KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Persistence<KnowledgeBase> persistence = new KnowledgeBasePersistence(manager, kb);
		Document doc = persistence.getDocument();

		Element root = doc.createElement(ELEMENT_SETTINGS);
		Element plugins = doc.createElement(ELEMENT_PLUGINS);
		doc.appendChild(root);
		root.appendChild(plugins);
		listener.updateProgress(0, "Saving plugin configuration");
		float count = 1;
		List<PluginEntry> entries = new LinkedList<PluginEntry>(getEntries(kb));
		Collections.sort(entries, new Comparator<PluginEntry>() {

			@Override
			public int compare(PluginEntry o1, PluginEntry o2) {
				return o1.getPlugin().getPluginID().compareTo(o2.getPlugin().getPluginID());
			}
		});
		if (entries != null) {
			float max = entries.size();
			for (PluginEntry conf : entries) {
				Element pluginElement = doc.createElement(ELEMENT_PLUGIN);
				Plugin plugin = conf.getPlugin();
				pluginElement.setAttribute(ATTRIBUTE_ID, plugin.getPluginID());
				pluginElement.setAttribute(ATTRIBUTE_REQUIRED, String.valueOf(conf.isRequired()));
				pluginElement.setAttribute(ATTRIBUTE_AUTODETECT,
						String.valueOf(conf.isAutodetect()));
				plugins.appendChild(pluginElement);
				listener.updateProgress(count++ / max, "Saving plugin configuration");
			}
		}
		Element psmethods = doc.createElement(ELEMENT_PSMETHODS);
		root.appendChild(psmethods);
		LinkedList<PSConfig> psconfigs = new LinkedList<PSConfig>(kb.getPsConfigs());
		Collections.sort(psconfigs);
		for (PSConfig ps : psconfigs) {
			psmethods.appendChild(persistence.writeFragment(ps));
		}
		XMLUtil.writeDocumentToOutputStream(doc, stream);
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		Collection<PluginEntry> entries = getEntries(kb);
		return (entries != null) ? entries.size() : 0;
	}

	private Collection<PluginEntry> getEntries(KnowledgeBase kb) {
		return PluginConfig.getPluginConfig(kb).getEntries();
	}

}
