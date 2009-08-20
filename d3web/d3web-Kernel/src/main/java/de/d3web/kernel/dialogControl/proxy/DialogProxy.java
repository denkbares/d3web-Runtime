package de.d3web.kernel.dialogControl.proxy;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import de.d3web.kernel.XPSCase;

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
		Comparator c = new DialogClientComparator();
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
	public Collection getAnswers(String questionID) {
		if (questionID == null) {
			return null;
		}
		for (DialogClient client : getClients()) {
			Collection answers = client.getAnswers(questionID);
			if (answers != null) {
				return answers;
			}
		}
		return null;
	}

	/**
	 * @return an Iterator over all clients sorted by their priority
	 */
	public Iterator getClientsIterator() {
		return clients.values().iterator();
	}
	
	/**
	 * @return a Collections over all clients sorted by their priority
	 */
	public Collection<DialogClient> getClients() {
		return clients.values();
	}

	
	
	/**
	 * puts the given XPSCase to the Client that has the given Priority
	 * @return false if no such client exists, otherwise true.
	 * @see DialogClient#putCase(XPSCase theCase)
	 */
	public boolean putCase(int prio, XPSCase theCase) {
		try {
			DialogClient client = (DialogClient) clients.get(new Integer(prio));
			client.putCase(theCase);
			return true;
		} catch (Exception x) {
			return false;
		}
	}
}