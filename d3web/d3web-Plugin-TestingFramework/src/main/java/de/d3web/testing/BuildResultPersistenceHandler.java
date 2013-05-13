/*
 * Copyright (C) 2012 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.testing;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 13.06.2012
 */
public class BuildResultPersistenceHandler {

	private static final String DATE = "date";

	private static final String DURATION = "duration";

	private static final String SUCCESSES = "numberOfSuccessfullyTestedObjects";

	private static final String BUILD = "build";

	private static final String TEST_OBJECT = "testObject";

	private static final String MESSAGE = "message";

	private static final String TEXT = "text";

	private static final String TYPE = "type";

	private static final String CONFIGURATION = "configuration";

	private static final String NAME = "name";

	private static final String TEST = "test";

	private static final String XMLNS = "xmlns";

	private static final String DENKBARES = "http://www.denkbares.com";

	private static final String RESULT_SCHEMA_FILE = "build_result.xsd";

	private static final String XMLNS_XSI = "xmlns:xsi";

	private static final String XML_SCHEM_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

	private static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

	public static Document toXML(BuildResult build) throws IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		// create build root item
		Element root = document.createElement(BUILD);
		document.appendChild(root);

		// set namespaces for xsd
		root.setAttribute(XMLNS, DENKBARES);
		root.setAttribute(XMLNS_XSI, XML_SCHEM_NAMESPACE);
		root.setAttribute(XSI_SCHEMA_LOCATION, DENKBARES + " " + RESULT_SCHEMA_FILE);

		// required Attributes
		root.setAttribute(DURATION, String.valueOf(build.getBuildDuration()));
		root.setAttribute(DATE, DATE_FORMAT.format(build.getBuildDate()));

		// add child results for single tests
		for (TestResult result : build.getResults()) {

			// create test item
			Element test = document.createElement(TEST);
			root.appendChild(test);

			// add required test attributes
			test.setAttribute(NAME, result.getTestName());

			if (build.isVerbosePersistence()) {
				// write success messages verbose
				writeMessages(document, result, test, result.getTestObjectsWithExpectedOutcome());
			}
			else {
				// add required test attribute for aggregated persistence of
				// successful tests
				String numberOfSuccessfulTestsString = "" + result.getSuccessfullTestObjectRuns();
				test.setAttribute(SUCCESSES, numberOfSuccessfulTestsString);
			}

			// add optional test attributes
			if (result.getConfiguration() != null) {
				test.setAttribute(CONFIGURATION,
						TestParser.concatParameters(result.getConfiguration()));
			}

			// write unexpected messages
			writeMessages(document, result, test, result.getTestObjectsWithUnexpectedOutcome());

		}

		return document;
	}

	/**
	 * 
	 * @created 13.05.2013
	 * @param document
	 * @param result
	 * @param test
	 */
	private static void writeMessages(Document document, TestResult result, Element parent, Collection<String> testObjects) {
		for (String testObjectName : testObjects) {
			Message message = result.getMessageForTestObject(testObjectName);
			if (message == null) {
				Logger.getLogger(BuildResultPersistenceHandler.class.getName()).warning(
						"No message found for test object '" + testObjectName + "' in test '"
								+ result.getTestName() + "'.");
				continue;
			}

			Element messageElement = document.createElement(MESSAGE);
			messageElement.setAttribute(TYPE, message.getType().toString());
			messageElement.setAttribute(TEXT, message.getText());
			messageElement.setAttribute(TEST_OBJECT, testObjectName);
			parent.appendChild(messageElement);
		}
	}

	public static BuildResult fromXML(Document document) throws ParseException {
		Element root = (Element) document.getElementsByTagName(BUILD).item(0);

		// parse attributes
		long duration = Long.parseLong(root.getAttribute(DURATION));
		Date date = DATE_FORMAT.parse(root.getAttribute(DATE));
		int successfulTests = 0;

		// create test item
		// BuildResult build = new BuildResult(date);
		// build.setBuildDuration(duration);

		List<TestResult> resultList = new ArrayList<TestResult>();

		// parse single child tests
		NodeList testElements = document.getElementsByTagName(TEST);
		boolean verbosePersistence = false;
		for (int i = 0; i < testElements.getLength(); i++) {
			// parse every single test
			Element test = (Element) testElements.item(i);

			// read required attributes
			String testName = test.getAttribute(NAME);

			// read number of successful test object runs
			String numberOfSuccessfulRuns = test.getAttribute(SUCCESSES);
			if (numberOfSuccessfulRuns != null && numberOfSuccessfulRuns.length() != 0) {
				// when reading old build report this value does not exist
				try {
					successfulTests = Integer.parseInt(numberOfSuccessfulRuns);
				}
				catch (NumberFormatException e) {
					Logger.getLogger(BuildResultPersistenceHandler.class.getName()).warning(
							"NumberFormatException in build result persistence when loading number of succesful test object runs: "
									+ successfulTests);
				}
			}

			// read optional attributes
			String configuration = test.getAttribute(CONFIGURATION);

			// parse every single message
			NodeList messageElements = test.getElementsByTagName(MESSAGE);
			List<String> configParameters = TestParser.splitParameters(configuration);
			Map<String, Message> unexpectedMessages = Collections.synchronizedMap(new TreeMap<String, Message>());
			Map<String, Message> expectedMessages = Collections.synchronizedMap(new TreeMap<String, Message>());
			for (int j = 0; j < messageElements.getLength(); j++) {
				Element messageElement = (Element) messageElements.item(j);
				if (messageElement != null) {
					String typeString = messageElement.getAttribute(TYPE);
					Message.Type type = null;
					if (typeString != null && typeString.trim().length() > 0) {
						type = Message.Type.valueOf(typeString);
					}
					String text = messageElement.getAttribute(TEXT);
					if (text.length() == 0) {
						text = null;
					}
					if (typeString.equals(Message.Type.SUCCESS.toString())) {
						String testObjectName = messageElement.getAttribute(TEST_OBJECT);
						Message m = new Message(type, text);
						expectedMessages.put(testObjectName, m);
						successfulTests++;
					}
					else {
						String testObjectName = messageElement.getAttribute(TEST_OBJECT);
						Message m = new Message(type, text);
						unexpectedMessages.put(testObjectName, m);
					}
				}
			}
			if (successfulTests > 0) {
				// was stored non-verbose persistence
				resultList.add(TestResult.createTestResult(testName,
						configParameters.toArray(new String[configParameters.size()]),
						unexpectedMessages,
						successfulTests));
			}
			else {
				// was stored verbose persistence
				verbosePersistence = true;
				resultList.add(TestResult.createTestResult(testName,
						configParameters.toArray(new String[configParameters.size()]),
						unexpectedMessages,
						expectedMessages));
			}
		}

		return BuildResult.createBuildResult(duration, date, resultList,
				successfulTests, verbosePersistence);
	}
}
