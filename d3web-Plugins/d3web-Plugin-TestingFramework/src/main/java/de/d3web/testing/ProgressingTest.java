package de.d3web.testing;

import com.denkbares.progress.ProgressListener;

/**
 * A Test that tracks its progress.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 04.08.16
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ProgressingTest {

	float DEFAULT_COMPUTATION_COMPLEXITY = 1f;

	/**
	 * Returns a value approximating the computational complexity of this test, giving an idea about how long this test
	 * may run in contrast to other tests. This value will be used for progress calculations in the context of a test
	 * suite. The default complexity is 1. Make sure all test complexities for a certain test object class are
	 * comparable to one another, so a test that is about 10 times faster than the default case should have the
	 * complexity, 0.1f, a test that is 10 times slow should have a complexity of 10 and so forth.
	 *
	 * @return the computational complexity of this test
	 */
	default float getComputationalComplexity() {
		return DEFAULT_COMPUTATION_COMPLEXITY;
	}

	/**
	 * Sets the {@link ProgressListener} to be used by this test. Set it before executing the test!
	 *
	 * @param progressListener the {@link ProgressListener} to be used by this test
	 */
	void setProgressListener(ProgressListener progressListener);

}
