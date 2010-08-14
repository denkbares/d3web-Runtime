package de.d3web.caserepository.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import de.d3web.caserepository.CaseRepository;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.records.io.MultipleXMLCaseRepositoryHandler;
import de.d3web.core.records.io.SingleXMLCaseRepositoryHandler;
import de.d3web.plugin.test.InitPluginManager;

public class CaseRepositoryPersistenceTest extends TestCase {

	KnowledgeBase kb;

	public CaseRepositoryPersistenceTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		TestRunner.run(CaseRepositoryPersistenceTest.suite());
	}

	public static Test suite() {
		return new TestSuite(CaseRepositoryPersistenceTest.class);
	}

	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		// create tests folders
		new File("target/tests/multi").mkdirs();
		try {
			loadKnowledgeBase();
		}
		catch (IOException e2) {
			assertTrue("Error while loading KnowledgeBase", false);
		}
	}

	private void loadKnowledgeBase() throws IOException {
		PersistenceManager pm = PersistenceManager.getInstance();
		String kbPath = System.getProperty("user.dir") + "/src/test/resources/carDiagnosis.jar";
		kb = pm.load(new File(kbPath));
	}

	public void testSingleXMLLoading() throws Exception {
		File caseFile = new File(System.getProperty("user.dir")
				+ "/src/test/resources/carDiagnosis_cases.xml");
		CaseRepository repository = SingleXMLCaseRepositoryHandler.getInstance().load(kb, caseFile);
		assertNotNull("The loaded repository is null.", repository);
		assertNotNull("CaseObject with ID \"855859\" wasn't found.",
				repository.getCaseObjectById("8558590"));
		assertNotNull("CaseObject with ID \"9979645\" wasn't found.",
				repository.getCaseObjectById("9979645"));
		assertNotNull("CaseObject with ID \"10981489\" wasn't found.",
				repository.getCaseObjectById("10981489"));
	}

	public void testSingleXMLSaving() throws Exception {
		// Load the old file / repository
		File oldFile = new File(System.getProperty("user.dir")
				+ "/src/test/resources/carDiagnosis_cases.xml");
		CaseRepository repository = SingleXMLCaseRepositoryHandler.getInstance().load(kb, oldFile);

		// Save the new file / repository
		File newFile = new File("target/tests/carDiagnosis_cases_new.xml");
		SingleXMLCaseRepositoryHandler.getInstance().save(repository, newFile);

		// Load the new file / repository
		repository = SingleXMLCaseRepositoryHandler.getInstance().load(kb, newFile);

		assertNotNull("The loaded repository is null.", repository);
		assertNotNull("CaseObject with ID \"855859\" wasn't found.",
				repository.getCaseObjectById("8558590"));
		assertNotNull("CaseObject with ID \"9979645\" wasn't found.",
				repository.getCaseObjectById("9979645"));
		assertNotNull("CaseObject with ID \"10981489\" wasn't found.",
				repository.getCaseObjectById("10981489"));

		newFile.delete();
	}

	public void testMultipleXMLSaving() throws Exception {
		// Load the old file / repository
		File oldFile = new File(System.getProperty("user.dir")
				+ "/src/test/resources/carDiagnosis_cases.xml");
		CaseRepository repository = SingleXMLCaseRepositoryHandler.getInstance().load(kb, oldFile);

		// Save the repositories to multiple files
		File directory = new File("target/tests/multi");
		MultipleXMLCaseRepositoryHandler.getInstance().save(repository, directory);

		// Test the number of files in the directory
		int fileCounter = 0;
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".xml")) fileCounter++;
		}
		assertEquals("There should be three XML Files.", 3, fileCounter);
	}

	public void testMultipleXMLLoading() throws Exception {
		// Load the old files / repository (the files are created in the test
		// before)
		File directory = new File("target/tests/multi");
		CaseRepository repository = MultipleXMLCaseRepositoryHandler.getInstance().load(kb,
				directory);

		assertNotNull("The loaded repository is null.", repository);
		assertNotNull("CaseObject with ID \"855859\" wasn't found.",
				repository.getCaseObjectById("8558590"));
		assertNotNull("CaseObject with ID \"9979645\" wasn't found.",
				repository.getCaseObjectById("9979645"));
		assertNotNull("CaseObject with ID \"10981489\" wasn't found.",
				repository.getCaseObjectById("10981489"));

		// Delete the files in the directory
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".xml")) f.delete();
		}
	}

}
