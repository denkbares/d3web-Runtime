/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.plugin.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.plugin.PluginConfig;
import de.d3web.plugin.Plugin;
import de.d3web.plugin.PluginEntry;
import de.d3web.plugin.PluginManager;
/**
 * A KnowledgeReader/Writer for the configuration of the extensions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginConfigPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		PluginConfig pc = new PluginConfig(kb);
		List<Element> elementList = XMLUtil.getElementList(doc.getDocumentElement().getChildNodes());
		if (elementList.size()==1) {
			Element root = elementList.get(0);
			if (root.getNodeName().equals("settings")) {
				for (Element father: XMLUtil.getElementList(root.getChildNodes())) {
					if (father.getNodeName().equals("plugins")) {
						List<Element> children = XMLUtil.getElementList(father.getChildNodes());
						for (Element e: children) {
							boolean necessary = Boolean.parseBoolean(e.getAttribute("necesary"));
							boolean autodetect = Boolean.parseBoolean(e.getAttribute("autodetect"));
							String id = e.getAttribute("ID");
							Plugin plugin = PluginManager.getInstance().getPlugin(id);
							if (plugin == null) {
								if (necessary) {
									throw new IOException("Necessary plugin "+id+" is not available");
								} else {
									plugin = new DummyPlugin(id);
								}
							}
							pc.addEntry(new PluginEntry(plugin, necessary, autodetect));
						}
					} else if (father.getNodeName().equals("psmethods")) {
						//TODO
					}
				}
			}
		}
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("settings");
		Element plugins = doc.createElement("plugins");
		doc.appendChild(root);
		root.appendChild(plugins);
		listener.updateProgress(0, "Saving plugin configuration");
		float count = 1;
		Collection<PluginEntry> entries = getEntries(kb);
		if (entries != null) {
			int max = entries.size();
			for (PluginEntry conf: entries) {
				Element pluginElement = doc.createElement("plugin");
				Plugin plugin = conf.getPlugin();
				pluginElement.setAttribute("ID", plugin.getPluginID());
				pluginElement.setAttribute("necessary", ""+conf.isNecessary());
				pluginElement.setAttribute("autodetect", ""+conf.isAutodetect());
				plugins.appendChild(pluginElement);
				listener.updateProgress(count++/max, "Saving plugin configuration");
			}
		}
		Element psmethods = doc.createElement("psmethods");
		root.appendChild(psmethods);
		//TODO
		Util.writeDocumentToOutputStream(doc, stream);
	}


	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		Collection<PluginEntry> entries = getEntries(kb);
		return (entries != null) ? entries.size() : 0;
	}
	
	private Collection<PluginEntry> getEntries(KnowledgeBase kb) {
		Collection<KnowledgeSlice> pluginconfigs = kb.getAllKnowledgeSlicesFor(PSMethod.class);
		for (KnowledgeSlice ks: pluginconfigs) {
			if (ks instanceof PluginConfig) {
				PluginConfig pc = (PluginConfig) ks;
				return pc.getEntries();
			}
		}
		return null;
	}

}
