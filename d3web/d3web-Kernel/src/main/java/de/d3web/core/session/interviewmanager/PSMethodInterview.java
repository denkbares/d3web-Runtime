package de.d3web.core.session.interviewmanager;

import java.util.Collection;

import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;

/**
 * This PSMethod is used to notify the {@link Interview} of new facts added to
 * the {@link Session}, i.e., new (contra-)indications and values added to the
 * {@link Blackboard}.
 * 
 * @author joba
 * 
 */
public class PSMethodInterview extends PSMethodAdapter {

	private static PSMethodInterview instance;

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
		for (PropagationEntry change : changes) {
			session.getInterview().notifyFactChange(change);
		}
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		for (Fact fact : facts) {
			System.out.println(fact);
		}
		// TODO Auto-generated method stub
		return facts[0];
	}

	public static PSMethodInterview getInstance() {
		if (instance == null) {
			instance = new PSMethodInterview();
		}
		return instance;
	}

}
