/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.records.DefaultSessionRepository;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.records.SessionRepository;
import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.SessionFactory;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 08.03.2011
 */
public class FilterTest {

	private long time;
	private SessionRepository repository;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		repository = new DefaultSessionRepository();
		time = System.currentTimeMillis();
		DefaultSession session1 = SessionFactory.createSession(
				"session1", kb, new Date(time));
		session1.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.GERMAN, "Die erste Session");
		DefaultSession session2 = SessionFactory.createSession(
				"session2", kb, new Date(time + 1));
		String session2Description = "This is the second session";
		session2.getInfoStore().addValue(MMInfo.DESCRIPTION, session2Description);
		DefaultSession session3 = SessionFactory.createSession(
				"session3", kb, new Date(time + 2));
		session3.setName("Session 3");
		repository.add(SessionConversionFactory.copyToSessionRecord(session1));
		repository.add(SessionConversionFactory.copyToSessionRecord(session2));
		repository.add(SessionConversionFactory.copyToSessionRecord(session3));
	}

	@Test
	public void testCreationDate() {
		Collection<SessionRecord> records = repository.getSessionRecords(new CreationDateFilter(
				new Date(time), null, false, true));
		Assert.assertEquals(2, records.size());
		records = repository.getSessionRecords(new CreationDateFilter(
				new Date(time), null, true, true));
		Assert.assertEquals(3, records.size());
		records = repository.getSessionRecords(new CreationDateFilter(
				new Date(time), new Date(time + 2), true, true));
		Assert.assertEquals(3, records.size());
		records = repository.getSessionRecords(new CreationDateFilter(
				new Date(time), new Date(time + 2), false, false));
		Assert.assertEquals(1, records.size());
		Assert.assertEquals("session2", records.iterator().next().getId());
	}

	@Test
	public void testName() {
		Collection<SessionRecord> records = repository.getSessionRecords(new NameFilter("Session 3"));
		Assert.assertEquals(1, records.size());
		Assert.assertEquals("session3", records.iterator().next().getId());
		records = repository.getSessionRecords(new NameFilter("Session 2"));
		Assert.assertEquals(0, records.size());
		records = repository.getSessionRecords(new NameFilter("Session \\d"));
		Assert.assertEquals(1, records.size());
	}

	@Test
	public void testProperty() {
		Collection<SessionRecord> records = repository.getSessionRecords(new PropertyFilter<>(
				MMInfo.DESCRIPTION, "This is the second session"));
		Assert.assertEquals(1, records.size());
		Assert.assertEquals("session2", records.iterator().next().getId());
		records = repository.getSessionRecords(new PropertyFilter<>(
				MMInfo.DESCRIPTION, Locale.GERMAN, "This is the second session"));
		// a German version will not be found, but the general (NoLanguage) will
		// be taken
		Assert.assertEquals(1, records.size());
		records = repository.getSessionRecords(new PropertyFilter<>(MMInfo.DESCRIPTION,
				"This is the first session"));
		Assert.assertEquals(0, records.size());
		records = repository.getSessionRecords(new StringPropertyFilter(
				MMInfo.DESCRIPTION, Locale.GERMAN, ".*erste.*"));
		Assert.assertEquals(1, records.size());
		Assert.assertEquals("session1", records.iterator().next().getId());
		records = repository.getSessionRecords(new StringPropertyFilter(
				MMInfo.DESCRIPTION, ".*erste.*"));
		Assert.assertEquals(0, records.size());
		// testing that no description is assigned
		records = repository.getSessionRecords(new PropertyFilter<>(
				MMInfo.DESCRIPTION, Locale.GERMAN, null));
		Assert.assertEquals(1, records.size());
		Assert.assertEquals("session3", records.iterator().next().getId());

	}

	@Test
	public void testComplex() {
		// after time
		Filter f1 = new CreationDateFilter(new Date(time), null, false, false);
		// before time+2
		Filter f2 = new CreationDateFilter(null, new Date(time + 2), false, false);
		Filter and = new AndFilter(f1, f2);
		Filter or = new OrFilter(f1, f2);
		Collection<SessionRecord> records = repository.getSessionRecords(and);
		Assert.assertEquals(1, records.size());
		Assert.assertTrue(and.accept(records.iterator().next()));
		records = repository.getSessionRecords(or);
		Assert.assertEquals(3, records.size());
		Assert.assertTrue(or.accept(records.iterator().next()));
		// after time +1
		f1 = new CreationDateFilter(new Date(time + 1), null, false, false);
		// before time +1
		f2 = new CreationDateFilter(null, new Date(time + 1), false, false);
		and = new AndFilter(f1, f2);
		or = new OrFilter(f1, f2);
		records = repository.getSessionRecords(and);
		Assert.assertEquals(0, records.size());
		records = repository.getSessionRecords(or);
		Assert.assertEquals(2, records.size());
		records = repository.getSessionRecords(new AndFilter(or, new TestFilter(2)));
		Assert.assertEquals(2, records.size());
		records = repository.getSessionRecords(new AndFilter(and, new TestFilter(0)));
		Assert.assertEquals(0, records.size());
		// the first Testfilter should not be called, this ensures that
		// subfilters of ors are also optimized, the second one should be called
		// three times, which is assured by the following assert
		records = repository.getSessionRecords(new OrFilter(new AndFilter(and, new TestFilter(0)),
				new TestFilter(3)));
		Assert.assertEquals(3, records.size());
	}

	/**
	 * Testclass to ensure that the Filter is not called more often than
	 * specified, it matches everything
	 * 
	 * @author Markus Friedrich (denkbares GmbH)
	 * @created 08.03.2011
	 */
	private static class TestFilter implements Filter {

		private int count;

		public TestFilter(int count) {
			this.count = count;
		}

		@Override
		public boolean accept(SessionRecord record) {
			count--;
			if (count < 0) throw new AssertionFailedError("Filter was called to often.");
			return true;
		}
	}

}
