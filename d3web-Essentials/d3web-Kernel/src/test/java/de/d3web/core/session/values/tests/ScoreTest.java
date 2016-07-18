package de.d3web.core.session.values.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.d3web.scoring.Score;

import static org.junit.Assert.assertEquals;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.07.16
 */
public class ScoreTest {

	@Test
	public void basic() {
		List<Score> allScores = new ArrayList<>(Score.getAllScores());
		Collections.reverse(allScores);
		ArrayList<Score> sortedScores = new ArrayList<>(allScores);
		Collections.sort(sortedScores);
		Collections.reverse(sortedScores);
		Collections.sort(sortedScores);

		assertEquals(allScores, sortedScores);

		assertEquals(1, Score.P7.getAPriori(), 0);
	}
}
