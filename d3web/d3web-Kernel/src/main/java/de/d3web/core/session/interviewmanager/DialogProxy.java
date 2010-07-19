/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.core.session.interviewmanager;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This is a by Proxy that you can get a list of Answers by giving the Question-ID
 * It asks all registered clients considering their priority and returns the first not-null-answer
 * Per priority there is only one client possible
 * @author Norman Brümmer
 */
public class DialogProxy {

	private Map<Integer, DialogClient> clients = null;

	public DialogProxy() {
		super();
		Comparator<Integer> c = new DialogClientComparator();
		clients = new TreeMap<Integer, DialogClient>(c);
	}

	/**
	 * registers clients
	 */
	public void addClient(DialogClient client) {
		clients.put(new Integer(client.getPriority()), client);
	}

	/**
	 * asks all (sorted) clients for answer
	 * @return first not-null-answer
	 */
	public Value getAnswers(String questionID) {
		if (questionID == null) {
			return null;
		}
		for (DialogClient client : getClients()) {
			Value value = client.getAnswers(questionID);
			if (value instanceof UndefinedValue || value == null) {
				;
			}
			else {
				return value;
			}
		}
		return UndefinedValue.getInstance();
	}

	/**
	 * @return an Iterator over all clients sorted by their priority
	 */
	public Iterator<?> getClientsIterator() {
		return clients.values().iterator();
	}
	
	/**
	 * @return a Collections over all clients sorted by their priority
	 */
	public Collection<DialogClient> getClients() {
		return clients.values();
	}

	
	
	/**
	 * puts the given Session to the Client that has the given Priority
	 * @return false if no such client exists, otherwise true.
	 * @see DialogClient#putCase(Session session)
	 */
	public boolean putCase(int prio, Session session) {
		try {
			DialogClient client = (DialogClient) clients.get(new Integer(prio));
			client.putCase(session);
			return true;
		} catch (ClassCastException x) {
			return false;
		}
		catch (NullPointerException x) {
			return false;
		}
	}
}