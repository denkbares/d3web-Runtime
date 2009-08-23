package de.d3web.kernel.shared.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.psMethods.shared.Abnormality;
import de.d3web.kernel.psMethods.shared.AbnormalityNum;
public class AbnormalityNumTest extends TestCase {

	/**
	 * Constructor for AbnormalityNumTest.
	 * @param arg0
	 */
	public AbnormalityNumTest(String arg0) {
		super(arg0);
	}

	public static Test suite() {
		return new TestSuite (AbnormalityNumTest.class);
	}
	
	public void testXML() {
		
		AbnormalityNum an = new AbnormalityNum();

		try {
			an.addValue(0, 1, 0.1, true, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}

		try {
			an.addValue(-1,0, 0.5, false, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}
		
		String xml = an.getXMLString(true);
		String xml2 =
			"<KnowledgeSlice type=\"abnormality\">\n"
			+ "<question ID=\"\"/>\n"
			+ "<Intervals>\n"
			+ "<Interval lower=\"0.0\" upper=\"1.0\" value=\"A1\" type=\"LeftOpenRightClosedInterval\" />\n"
			+ "<Interval lower=\"-1.0\" upper=\"0.0\" value=\"A4\" type=\"LeftClosedRightClosedInterval\" />\n"
			+ "</Intervals>\n"
			+ "</KnowledgeSlice>\n"
		;
		assertEquals(xml2 + "\n <=/=> \n"+xml, xml, xml2);
		
	}
	
	public void testConstruction() {
		AbnormalityNum an = new AbnormalityNum();
		
		try {
			an.addValue(1, 0, 0.5, true, true);
			assertTrue("missing: exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}

		try {
			an.addValue(1, 1, 0.5, true, true);
			assertTrue("missing: exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}


		try {
			an.addValue(0, 0, 0.5, true ,true);
			assertTrue("missing: exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}

		try {
			an.addValue(0, 1, 0.5, true, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}
		
	}
	
	public void testValues() {
		AbnormalityNum an = new AbnormalityNum();

		try {
			an.addValue(0, 1, 0.1, true, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}

		try {
			an.addValue(1, 2, 0.5, false, true);
			assertTrue("missing exception", false);
		} catch (NumericalInterval.IntervalException e) {
			System.out.println("" + e);
		}

		try {
			an.addValue(-1,0, 0.5, false, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception", false);
		}

		try {
			an.addValue(- Double.MAX_VALUE, -2, Double.MAX_VALUE, false, false);
		} catch (NumericalInterval.IntervalException e) {
			assertTrue("exception " + e, false);
		} catch (Exception ex) {
			assertTrue("exception " + ex, false);
		}
		
		assertTrue("" + an.getValue(0.5) + "should be 0.1", an.getValue(0.5) == 0.1);
		assertTrue("" + an.getValue(-0.5) + "should be 0.5", an.getValue(-0.5) == 0.5);
		assertTrue("" + an.getValue(0) + "should be 0.5", an.getValue(0) == 0.5);
		assertTrue("" + an.getValue(1) + "should be 0.1", an.getValue(1) == 0.1);
		assertTrue("" + an.getValue(1.1) + "should be A0", an.getValue(1.1) == Abnormality.A0);
		assertTrue("" + an.getValue(-10) + "should be >= A5", an.getValue(-10) >= Abnormality.A5);
		
	}
	
}