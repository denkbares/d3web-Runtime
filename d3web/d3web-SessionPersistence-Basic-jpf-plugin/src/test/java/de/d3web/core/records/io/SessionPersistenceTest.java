/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.records.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.records.DefaultSessionRecord;
import de.d3web.core.records.DefaultSessionRepository;
import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.SessionRepository;
import de.d3web.core.records.io.fragments.DateValueHandler;
import de.d3web.core.records.io.fragments.UndefinedHandler;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.file.records.io.SingleXMLSessionRepository;
import de.d3web.folder.records.io.MultipleXMLSessionRepository;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.Score;

/**
 * Tests for SessionPersistence. Creates a KB and Session, puts them into a
 * repository, saves and reloads them.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 21.09.2010
 */
public class SessionPersistenceTest {

	private static final NumValue NUMVALUE = new NumValue(-29.2);
	private static final TextValue TEXTVALUE = new TextValue("Text <mit> fiesen\\bösen </Sachen>");
	private SessionRecord sessionRecord;
	private SessionRecord sessionRecord2;
	private KnowledgeBase kb;
	private QuestionOC questionOC;
	private QuestionMC questionMC;
	private QuestionDate questionDate;
	private QuestionText questionText;
	private QuestionNum questionNum;
	private Choice[] choices;
	private Choice[] choices2;
	private Date startDate;
	private String sessionID;
	private String session2ID;
	private Solution solution;
	private Solution solution2;
	private File directory;
	private Date creationDate;
	private Date lastChangeDate;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		startDate = new Date();
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement.createInstance();
		kb = kbm.getKnowledgeBase();
		kb.setId("TestKB");
		choices = new Choice[2];
		choices[0] = new Choice("Answer1");
		choices[1] = new Choice("Answer2");
		choices2 = new Choice[2];
		choices2[0] = new Choice("Answer1");
		choices2[1] = new Choice("Answer2");
		questionOC = kbm.createQuestionOC("Question",
				kb.getRootQASet(), choices);
		questionMC = kbm.createQuestionMC("Question2", kb.getRootQASet(), choices);
		questionDate = kbm.createQuestionDate("QuestionDate", kb.getRootQASet());
		questionText = kbm.createQuestionText("QuestionText", kb.getRootQASet());
		questionNum = kbm.createQuestionNum("QuestionNum", kb.getRootQASet());
		solution = kbm.createSolution("Solution");
		solution2 = kbm.createSolution("Solution2");
		RuleFactory.createHeuristicPSRule("R1", solution2, Score.P7, new CondNumLess(questionNum,
				0.0));
		RuleFactory.createHeuristicPSRule("R2", solution, Score.P7, new CondNumLess(questionNum,
				0.0));
		Session session = SessionFactory.createSession(kb);
		sessionID = session.getId();
		Blackboard blackboard = session.getBlackboard();
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionOC, new ChoiceValue(
				choices[0])));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionMC,
				MultipleChoiceValue.fromChoices(Arrays.asList(choices2))));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionDate, new DateValue(
				startDate)));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionText, TEXTVALUE));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(questionNum, NUMVALUE));
		blackboard.addValueFact(FactFactory.createUserEnteredFact(solution, new Rating(
				Rating.State.ESTABLISHED)));
		creationDate = session.getCreationDate();
		lastChangeDate = session.getLastChangeDate();
		sessionRecord = SessionConversionFactory.copyToSessionRecord(session);
		Session session2 = SessionFactory.createSession(kb);
		session2ID = session2.getId();
		session2.getPropagationManager().openPropagation();
		Blackboard blackboard2 = session2.getBlackboard();
		blackboard2.addValueFact(
				FactFactory.createUserEnteredFact(questionOC, Unknown.getInstance()));
		blackboard2.addInterviewFact(FactFactory.createUserEnteredFact(questionMC, new Indication(
				Indication.State.INDICATED)));
		blackboard2.addValueFact(FactFactory.createUserEnteredFact(questionNum, NUMVALUE));
		session2.getPropagationManager().commitPropagation();
		sessionRecord2 = SessionConversionFactory.copyToSessionRecord(session2);
		directory = new File("target/temp/directory");
		directory.mkdirs();
	}

	@Test
	public void testSingleXMLPersistence() throws IOException {
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);
		sessionRepository.add(sessionRecord2);
		File file = new File("target/temp/file.xml");
		sessionRepository.save(file);
		SingleXMLSessionRepository reloadedRepository = new SingleXMLSessionRepository();
		reloadedRepository.load(kb, file);
		// Test iterator
		Iterator<SessionRecord> iterator = reloadedRepository.iterator();
		Session session = SessionConversionFactory.copyToSession(iterator.next());
		Session session2 = SessionConversionFactory.copyToSession(iterator.next());
		// the sorting in the hashmap isn't stable, so we sort manually
		if (session.getLastChangeDate().before(session2.getLastChangeDate())) {
			checkValuesAfterReload(session, session2);
		}
		else {
			checkValuesAfterReload(session2, session);
		}

		// testing error behaviour
		SingleXMLSessionRepository errorTestingRepository = new SingleXMLSessionRepository();
		// tests if the repository stops loading when the kb is null
		boolean error = false;
		try {
			errorTestingRepository.load(null, file);
		}
		catch (NullPointerException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		// test if file is null
		try {
			errorTestingRepository.load(kb, null);
		}
		catch (NullPointerException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		try {
			errorTestingRepository.save(null);
		}
		catch (NullPointerException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		// Tests if file is a directory
		try {
			errorTestingRepository.load(kb, directory);
		}
		catch (IllegalArgumentException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		try {
			errorTestingRepository.save(directory);
		}
		catch (IllegalArgumentException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		// Test if file doesn't exist
		File noFile = new File("target/temp/noFile.file");
		noFile.delete();
		Assert.assertFalse(
				"Something manipulated test by creating a folder nofile.file in target/temp",
				noFile.isDirectory());
		try {
			errorTestingRepository.load(kb, directory);
		}
		catch (IllegalArgumentException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		// Test if an error occurs when the IDs of the KB are different
		KnowledgeBase kb2 = new KnowledgeBase();
		kb2.setId("Test2");
		try {
			errorTestingRepository.load(kb2, file);
		}
		catch (IOException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
	}

	@Test
	public void testMultiXMLPersistence() throws IOException, InterruptedException, ParseException {
		MultipleXMLSessionRepository sessionRepository = new MultipleXMLSessionRepository();
		sessionRepository.add(sessionRecord);
		sessionRepository.add(sessionRecord2);
		clearDirectory(directory);
		sessionRepository.save(directory);
		Assert.assertEquals(2, directory.listFiles().length);
		MultipleXMLSessionRepository reloadedRepository = new MultipleXMLSessionRepository();
		reloadedRepository.load(kb, directory);
		// Test copying - nothing has changed, the files should be simply copied
		for (File f : directory.listFiles()) {
			markXMLFile(f);
		}
		File directory2 = new File("target/temp/copiedFiles");
		directory2.mkdirs();
		clearDirectory(directory2);
		reloadedRepository.save(directory2);
		boolean allMarked = true;
		for (File f : directory2.listFiles()) {
			allMarked &= testMark(f);
		}
		Assert.assertTrue(allMarked);
		MultipleXMLSessionRepository rereloadedRepository = new MultipleXMLSessionRepository();
		rereloadedRepository.load(kb, directory2);
		File[] files = directory2.listFiles();
		Assert.assertEquals(2, files.length);
		// Test saving to the same location without modifing something (the
		// files should stay equal)
		rereloadedRepository.save(directory2);
		allMarked = true;
		for (File f : directory2.listFiles()) {
			allMarked &= testMark(f);
		}
		Assert.assertTrue(allMarked);
		// When adding facts, the files should change
		SessionRecord rereloadedSessionRecord = rereloadedRepository.getSessionRecordById(sessionID);
		SessionRecord rereloadedSessionRecord2 = rereloadedRepository.getSessionRecordById(session2ID);
		int factCountBeforeAdding = rereloadedSessionRecord.getValueFacts().size();
		FactRecord dummyFact = new FactRecord(questionNum, "psm", NUMVALUE);
		FactRecord dummyFact2 = new FactRecord(questionNum, "psm", new Indication(State.INDICATED));
		rereloadedSessionRecord.addValueFact(dummyFact);
		rereloadedSessionRecord2.addValueFact(dummyFact);
		rereloadedSessionRecord2.addInterviewFact(dummyFact2);
		Assert.assertEquals(1, rereloadedSessionRecord.getValueFacts().size()
				- factCountBeforeAdding);
		// Fact already contained, should be ignored
		rereloadedSessionRecord.addValueFact(dummyFact);
		Assert.assertEquals(1, rereloadedSessionRecord.getValueFacts().size()
				- factCountBeforeAdding);
		// now the saved files should be newly created
		rereloadedRepository.save(directory2);
		boolean nomarked = false;
		for (File f : directory2.listFiles()) {
			nomarked |= testMark(f);
		}
		Assert.assertFalse(nomarked);

		// Test getting Records by Session id, getting them with iterator has a
		// random order (depending on the alphabetical order of the Session ids)
		Session session = SessionConversionFactory.copyToSession(reloadedRepository.getSessionRecordById(sessionID));
		Session session2 = SessionConversionFactory.copyToSession(reloadedRepository.getSessionRecordById(session2ID));
		checkValuesAfterReload(session, session2);

		// Test error behaviour
		MultipleXMLSessionRepository errorTestingRepository = new MultipleXMLSessionRepository();
		// tests if the repository stops loading when the kb is null
		boolean error = false;
		try {
			errorTestingRepository.load(null, directory);
		}
		catch (NullPointerException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		// testing folder = null
		try {
			errorTestingRepository.load(kb, null);
		}
		catch (NullPointerException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		try {
			errorTestingRepository.save(null);
		}
		catch (NullPointerException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		// testing file instead of a folder
		File file = new File("target/temp/aFile.error");
		file.createNewFile();
		try {
			errorTestingRepository.load(kb, file);
		}
		catch (IllegalArgumentException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
		try {
			errorTestingRepository.save(file);
		}
		catch (IllegalArgumentException e) {
			error = true;
		}
		Assert.assertTrue(error);
		error = false;
	}

	/**
	 * Marks a file by setting a comment "original"
	 * 
	 * @created 23.09.2010
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void markXMLFile(File f) throws IOException, FileNotFoundException {
		FileInputStream istream = new FileInputStream(f);
		try {
			Document doc = Util.streamToDocument(istream);
			doc.appendChild(doc.createComment("original"));
			FileOutputStream ostream = new FileOutputStream(f);
			try {
				Util.writeDocumentToOutputStream(doc, ostream);
			}
			finally {
				ostream.close();
			}
		}
		finally {
			istream.close();
		}
	}

	/**
	 * Checks if a file was marked with method markXMLFile
	 * 
	 * @created 23.09.2010
	 * @param f File
	 * @return true if marked, false otherwise
	 * @throws IOException
	 */
	private boolean testMark(File f) throws IOException {
		FileInputStream istream = new FileInputStream(f);
		boolean marked = false;
		try {
			Document doc = Util.streamToDocument(istream);
			for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
				Node child = doc.getChildNodes().item(i);
				if (child instanceof Comment && child.getTextContent().equals("original")) {
					marked = true;
				}
			}

		}
		finally {
			istream.close();
		}
		return marked;
	}

	private void clearDirectory(File directory) throws IOException {
		for (File f : directory.listFiles()) {
			Assert.assertFalse(
					"Something has corrupted this test by putting a folder in "
							+ directory.getCanonicalPath().toString(),
					f.isDirectory());
			f.delete();
		}
	}

	private void checkValuesAfterReload(Session session, Session session2) throws IOException {
		Assert.assertEquals(sessionID, session.getId());
		Assert.assertEquals(session2ID, session2.getId());
		Assert.assertEquals(creationDate, session.getCreationDate());
		Assert.assertEquals(lastChangeDate, session.getLastChangeDate());
		Blackboard blackboard = session.getBlackboard();
		ChoiceValue value = (ChoiceValue) blackboard.getValue(questionOC);
		Assert.assertEquals(choices[0], value.getValue());
		MultipleChoiceValue value2 = (MultipleChoiceValue) blackboard.getValue(questionMC);
		Collection<?> values = (Collection<?>) value2.getValue();
		Assert.assertTrue(values.size() == 2 && values.contains(new ChoiceValue(choices2[0]))
				&& values.contains(new ChoiceValue(choices2[1])));
		Assert.assertEquals(startDate, blackboard.getValue(questionDate).getValue());
		Assert.assertEquals(TEXTVALUE, blackboard.getValue(questionText));
		Assert.assertEquals(NUMVALUE, blackboard.getValue(questionNum));
		Assert.assertTrue(blackboard.getRating(solution).hasState(Rating.State.ESTABLISHED));
		Blackboard blackboard2 = session2.getBlackboard();
		Assert.assertEquals(Unknown.getInstance(), blackboard2.getValue(questionOC));
		Assert.assertTrue(blackboard2.getIndication(questionMC).hasState(Indication.State.INDICATED));
		Assert.assertFalse(blackboard2.getIndication(questionNum).hasState(
				Indication.State.INDICATED));
		Rating rating = blackboard.getRating(solution2);
		Assert.assertTrue(rating instanceof HeuristicRating);
		Assert.assertTrue(rating.hasState(Rating.State.ESTABLISHED));
	}

	/**
	 * This tests provokes errors and checks if they occur
	 * 
	 * @throws IOException
	 * @created 22.09.2010
	 */
	@Test
	public void errorHandlingTests() throws IOException {
		// Testing the behavior when someone edited the xml file and the Date
		// hasn't the correct format any more
		Document doc = Util.createEmptyDocument();
		Element element = doc.createElement("element");
		element.setTextContent("no Date");
		doc.appendChild(element);
		DateValueHandler handler = new DateValueHandler();
		Throwable expected = null;
		try {
			handler.read(null, element);
		}
		catch (IOException e) {
			expected = e.getCause();
		}
		Assert.assertTrue(expected instanceof ParseException);
		File file = new File("src/test/resources/parseException.xml");
		expected = null;
		try {
			SessionPersistenceManager.getInstance().loadSessions(file, new DummyProgressListener(),
					kb);
		}
		catch (IOException e) {
			expected = e.getCause();
		}
		Assert.assertTrue(expected instanceof ParseException);
	}

	/**
	 * The UndefinedHandler will only be needed, when the merge of two psm (at
	 * least one of them setting a value different from undefined) results in an
	 * undefined value. This will not happen with the default psm, to ensure the
	 * functionality, this test is used.
	 * 
	 * @throws IOException
	 * @created 22.09.2010
	 */
	@Test
	public void undefinedHandlerTest() throws IOException {
		Document doc = Util.createEmptyDocument();
		Element element = doc.createElement(UndefinedHandler.elementName);
		doc.appendChild(element);
		SessionPersistenceManager spm = SessionPersistenceManager.getInstance();
		Object readFragment = spm.readFragment(
				element, null);
		Assert.assertTrue(readFragment instanceof UndefinedValue);
		Element writeFragment = spm.writeFragment(UndefinedValue.getInstance(), doc);
		Assert.assertTrue(element.isEqualNode(writeFragment));
	}

	@Test
	public void testDefaultSessionRepository() {
		SessionRepository repository = new DefaultSessionRepository();
		Assert.assertNull(repository.getKnowledgeBase());
		repository.add(sessionRecord);
		repository.add(sessionRecord);
		countRecords(1, repository);
		Assert.assertNotNull(repository.getSessionRecordById(sessionID));
		Assert.assertNotNull(repository.getKnowledgeBase());
		DefaultSessionRecord newSessionRecordWithSameID = new DefaultSessionRecord(
				sessionRecord.getId(), sessionRecord.getKnowledgeBase(),
				sessionRecord.getCreationDate(), sessionRecord.getLastChangeDate());
		repository.add(newSessionRecordWithSameID);
		countRecords(1, repository);
		// is not contained any more, nothing will be removed
		repository.remove(sessionRecord);
		countRecords(1, repository);
		// when removing this record, the repository is empty
		repository.remove(newSessionRecordWithSameID);
		countRecords(0, repository);
		Assert.assertNull(repository.getSessionRecordById(sessionID));
	}

	private void countRecords(int expected, SessionRepository repository) {
		Iterator<SessionRecord> iterator = repository.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		Assert.assertEquals(expected, count);
	}

	@Test(expected = NullPointerException.class)
	public void addingANullRecord() {
		SessionRepository repository = new DefaultSessionRepository();
		repository.add(null);
	}

	@Test(expected = NullPointerException.class)
	public void removingANullRecord() {
		SessionRepository repository = new DefaultSessionRepository();
		repository.remove(null);
	}

	@Test
	public void removingANotConatainedRecord() {
		SessionRepository repository = new DefaultSessionRepository();
		Assert.assertFalse(repository.remove(sessionRecord));
	}

	@Test(expected = IllegalArgumentException.class)
	public void gettingWhiteSpaceID() {
		SessionRepository repository = new DefaultSessionRepository();
		repository.getSessionRecordById("   ");
	}

	@Test(expected = NullPointerException.class)
	public void gettingNullID() {
		SessionRepository repository = new DefaultSessionRepository();
		repository.getSessionRecordById(null);
	}

	@Test
	public void testDefaultSessionRecord() throws InterruptedException {
		// Testing methods not used in other Tests yet
		SessionRecord record = new DefaultSessionRecord(kb);
		Thread.sleep(1);
		Date later = new Date();
		Assert.assertFalse(later.equals(record.getLastChangeDate()));
		record.touch(later);
		Assert.assertEquals(later, record.getLastChangeDate());
	}

	@Test(expected = IOException.class)
	public void missingProblemSolver() throws IOException {
		sessionRecord.addValueFact(new FactRecord(questionNum, "fantasyPSM", new NumValue(5)));
		SessionConversionFactory.copyToSession(sessionRecord);
	}

	/**
	 * When adding facts of more than one psm for an object, the globally merged
	 * fact is inserted additionally
	 * 
	 * @throws IOException
	 * 
	 * @created 22.09.2010
	 */
	@Test
	public void globalFacts() throws IOException {
		Session session = SessionConversionFactory.copyToSession(sessionRecord2);
		Blackboard blackboard = session.getBlackboard();
		blackboard.addValueFact(FactFactory.createFact(questionOC, Unknown.getInstance(),
				this, session.getPSMethodInstance(PSMethodAbstraction.class)));
		// Number of Facts should have increased by 2
		SessionRecord sessionRecord2extended = SessionConversionFactory.copyToSessionRecord(session);
		Assert.assertEquals(2,
				sessionRecord2extended.getValueFacts().size()
						- sessionRecord2.getValueFacts().size());
		blackboard.addInterviewFact(FactFactory.createFact(questionMC, new Indication(
				State.CONTRA_INDICATED), this, session.getPSMethodInstance(PSMethodStrategic.class)));
		sessionRecord2extended = SessionConversionFactory.copyToSessionRecord(session);
		Assert.assertEquals(2,
				sessionRecord2extended.getValueFacts().size()
						- sessionRecord2.getValueFacts().size());
		blackboard.addValueFact(FactFactory.createUserEnteredFact(solution2, new Rating(
				Rating.State.EXCLUDED)));
		sessionRecord2extended = SessionConversionFactory.copyToSessionRecord(session);
		Assert.assertEquals(4,
				sessionRecord2extended.getValueFacts().size()
						- sessionRecord2.getValueFacts().size());
		// just to gain 100% coverage:
		new SessionConversionFactory();
	}

	@Test
	public void noRecordsFolder() throws IOException, ParseException {
		File directory = new File("src/test/resources/noRecordsFolder");
		MultipleXMLSessionRepository repository = new MultipleXMLSessionRepository();
		repository.load(kb, directory);
		Assert.assertFalse(repository.iterator().hasNext());
	}

	@Test(expected = IllegalArgumentException.class)
	public void fileWithMoreRecords() throws IOException, ParseException {
		File directory = new File("src/test/resources/FileWithMoreRecords");
		MultipleXMLSessionRepository repository = new MultipleXMLSessionRepository();
		repository.load(kb, directory);
		SessionConversionFactory.copyToSession(repository.iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void fileWithNoRecords() throws IOException, ParseException {
		File directory = new File("src/test/resources/FileWithNoRecords");
		MultipleXMLSessionRepository repository = new MultipleXMLSessionRepository();
		repository.load(kb, directory);
		SessionConversionFactory.copyToSession(repository.iterator().next());
	}

	@Test
	public void testSingleXMLPersistenceFolderCreation() throws IOException {
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);
		File file = new File("temp/folder3/file.xml");
		file.getParentFile().mkdirs();
		clearDirectory(file.getParentFile());
		Assert.assertTrue(file.getParentFile().delete());
		sessionRepository.save(file);
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testMultipleXMLPersistenceFolderCreation() throws IOException {
		MultipleXMLSessionRepository sessionRepository = new MultipleXMLSessionRepository();
		sessionRepository.add(sessionRecord);
		File folder = new File("temp/folder2/folder");
		folder.mkdirs();
		clearDirectory(folder);
		Assert.assertTrue(folder.delete());
		clearDirectory(folder.getParentFile());
		Assert.assertTrue(folder.getParentFile().delete());
		sessionRepository.save(folder);
		Assert.assertTrue(folder.exists());
	}
}