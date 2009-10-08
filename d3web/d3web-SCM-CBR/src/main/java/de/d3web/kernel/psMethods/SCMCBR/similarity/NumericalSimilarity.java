package de.d3web.kernel.psMethods.SCMCBR.similarity;

import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

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
	public double computeSimilarity(XPSCase theCase) {
		
		//TODO
		
		return 0;
	}

}
