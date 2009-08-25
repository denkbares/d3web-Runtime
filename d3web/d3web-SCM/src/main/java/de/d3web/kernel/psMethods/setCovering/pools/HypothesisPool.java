package de.d3web.kernel.psMethods.setCovering.pools;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.psMethods.setCovering.Hypothesis;

/**
 * This is a pool for Hypothesis-Objects It helps to reuse Hypotheses instead of
 * creating a new one every time a Hypothesis is needed.
 * 
 * @author bates
 */
public class HypothesisPool {

	public static final int GENERATION_COUNT = 100;

	private static HypothesisPool instance = null;
	private List hypotheses = null;

	private HypothesisPool() {
		hypotheses = new LinkedList();
		generateHypotheses();
	}

	/**
	 * @return the one and only instance of this singleton
	 */
	public static HypothesisPool getInstance() {
		if (instance == null) {
			instance = new HypothesisPool();
		}
		return instance;
	}

	/**
	 * Creates a new Hypothesis from the given array of elements. The Set for
	 * these elements will be taken from the SetPool, the Hypothesis from the
	 * internal stack.
	 * 
	 * @param scDiagnoses
	 *            Elements to fill the Hypothesis with
	 * @return new Hypothesis filled with the given Elements
	 */
	public Hypothesis getHypothesis(Object[] scDiagnoses) {
		Hypothesis hypothesis = getEmptyHypothesis();
		hypothesis.setSCDiagnoses(SetPool.getInstance().getFilledSet(scDiagnoses));
		return hypothesis;
	}

	/**
	 * Creates a new Hypothesis from the given Set of elements. The Hypothesis
	 * will ba taken from the internal stack.
	 * 
	 * @param scDiagnoses
	 *            Elements to fill the Hypothesis with
	 * @return new Hypothesis filled with the given Elements
	 */
	public Hypothesis getHypothesis(Set scDiagnoses) {
		Hypothesis hypothesis = getEmptyHypothesis();
		hypothesis.setSCDiagnoses(scDiagnoses);
		return hypothesis;
	}

	/**
	 * Creates an empty Hypothesis
	 * 
	 * @return an empty Hypothesis
	 */
	public Hypothesis getEmptyHypothesis() {
		if (hypotheses.isEmpty()) {
			generateHypotheses();
		}
		Hypothesis ret = (Hypothesis) hypotheses.get(0);
		hypotheses.remove(0);
		return ret;
	}

	private void generateHypotheses() {
		for (int i = 0; i < GENERATION_COUNT; ++i) {
			hypotheses.add(new Hypothesis());
		}
	}

	/**
	 * Empties the element set of the Hypothesis and gives it bach to the
	 * internal stack.
	 */
	public void free(Hypothesis hypothesis) {
		hypothesis.initialize();
	}

	/**
	 * @return the current size of the internal Hypothesis-stack
	 */
	public int getCurrentHypothesesCount() {
		return hypotheses.size();
	}

}
