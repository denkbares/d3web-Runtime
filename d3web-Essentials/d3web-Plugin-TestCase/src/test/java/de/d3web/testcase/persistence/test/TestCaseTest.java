package de.d3web.testcase.persistence.test;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import de.d3web.testcase.model.DefaultCheckTemplate;
import de.d3web.testcase.model.DefaultFindingTemplate;
import de.d3web.testcase.model.DefaultTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.07.16
 */
public class TestCaseTest {

	@Test
	public void basic() {

		DefaultTestCase defaultTestCaseA = getDefaultTestCase();
		DefaultTestCase defaultTestCaseB = getDefaultTestCase();

		assertEquals(defaultTestCaseA, defaultTestCaseB);
		assertNotEquals(defaultTestCaseA, new DefaultTestCase());
		assertEquals(defaultTestCaseA.hashCode(), defaultTestCaseB.hashCode());
		assertNotEquals(defaultTestCaseA.hashCode(), new DefaultTestCase().hashCode());
	}

	@NotNull
	private DefaultTestCase getDefaultTestCase() {
		DefaultTestCase defaultTestCase = new DefaultTestCase();
		defaultTestCase.addFinding(new Date(0), new DefaultFindingTemplate("A", "B"));
		defaultTestCase.addCheck(new Date(0), new DefaultCheckTemplate("A", "B"));
		defaultTestCase.addDescription(new Date(0), "First");
		defaultTestCase.addFinding(new Date(10), new DefaultFindingTemplate("A", "C"));
		defaultTestCase.addCheck(new Date(10), new DefaultCheckTemplate("A", "C"));
		defaultTestCase.addDescription(new Date(0), "Second");
		return defaultTestCase;
	}
}
