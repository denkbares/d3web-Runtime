package de.d3web.kernel.psMethods.scmcbr2.similarity;

import java.util.List;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;

/**
 * 
 * @author Reinhard Hatko
 * Created: 25.09.2009
 *
 */
public class NumericalSimilarity implements ISimilarityMeasurer {
	
	private final QuestionNum question;
	private final List<NumericalInterval> intervals;
	private final List<Double> similarities;
	
	
	/**
	 * @param question
	 * @param intervals
	 * @param similarities
	 */
	public NumericalSimilarity(QuestionNum question,
			List<NumericalInterval> intervals, List<Double> similarities) {
		this.question = question;
		this.intervals = intervals;
		this.similarities = similarities;
		
		if (intervals.size() != similarities.size())
			throw new IllegalArgumentException();
		
		
	}




	@Override
	public double computeSimilarity(Session session) {
		
		//TODO
		
		return 0;
	}

}
