package de.d3web.kernel.psMethods.shared;
import java.util.Hashtable;

import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorYN;
/**
 * Helper class that saves Classes and their PSMethodContext
 * Creation date: (14.08.2001 14:55:11)
 * @author: Norman Brümmer
 */
public class PSContextFinder {
	private Abnormality abnorm = null;
	private Weight weight = null;
	private QuestionComparator qcomp = null;

	private Hashtable contextHash = null;

	private static PSContextFinder instance = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (14.08.2001 15:01:28)
	 */
	private PSContextFinder()
{
		abnorm = new Abnormality();
		qcomp = new QuestionComparatorYN();
		weight = new Weight();

		contextHash = new Hashtable();
		contextHash.put(Abnormality.class, abnorm.getProblemsolverContext());
		contextHash.put(QuestionComparator.class, qcomp.getProblemsolverContext());
		contextHash.put(Weight.class, weight.getProblemsolverContext());
	}

	/**
	 * finds the psmethod context to the given knowledgeslice class
	 * Creation date: (14.08.2001 15:01:09)
	 * @return java.lang.Class
	 * @param knowledgeSliceClass java.lang.Class
	 */
	public Class findPSContext(Class knowledgeSliceClass)
{
		return (Class) contextHash.get(knowledgeSliceClass);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (14.08.2001 15:17:52)
	 * @return de.d3web.kernel.psMethods.shared.PSContextFinder
	 */
	public static PSContextFinder getInstance()
{
		if (instance == null)
		{
			instance = new PSContextFinder();
		}
		return instance;
	}
}