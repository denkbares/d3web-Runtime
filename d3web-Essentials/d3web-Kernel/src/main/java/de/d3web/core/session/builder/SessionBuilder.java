/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.session.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.denkbares.collections.PriorityList;
import com.denkbares.plugin.Extension;
import com.denkbares.plugin.PluginManager;
import com.denkbares.utils.Pair;
import de.d3web.core.extensions.KernelExtensionPoints;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * This class is used for reloading a session record into a session object.
 * <p>
 * It is based on a build design pattern. Therefore it is constructed with a session record, and then optionally
 * configured. Finally it is executed using {@link #build()} to perform the replay and receive the resulting session
 * record.
 * <p>
 * Any instance of this class can only be used to build one session. Multiple tries to build a session will result in an
 * {@link IllegalStateException}.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 27.04.2018
 */
public class SessionBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionBuilder.class);

	private static final Map<Session, SessionBuilder> processing = new ConcurrentHashMap<>();

	// initial parameters
	private final KnowledgeBase base;

	// configuration settings
	@SuppressWarnings("rawtypes")
	private final PriorityList<Double, Pair<ProtocolExecutor, Class>> executors = new PriorityList<>(5.0);
	@SuppressWarnings("rawtypes")
	private final Set<Pair<ProtocolExecutor, Class>> pluggedExecutors = new HashSet<>();
	private Session session;
	private String id;
	private String name;
	private Date created = null;
	private Date changed = null;
	private List<ProtocolEntry> protocol = null;
	private InfoStore info = null;

	// build-time parameters
	private boolean built = false;
	private final Set<String> warnings = new LinkedHashSet<>();

	/**
	 * Creates an empty builder to create a session for the specified knowledge base.
	 *
	 * @param base the base to build the session for
	 */
	public SessionBuilder(KnowledgeBase base) {
		this.base = Objects.requireNonNull(base);
		initPluggedExecutors();
	}

	/**
	 * A session builder without the plugged executors
	 */
	public SessionBuilder unplugged() {
		pluggedExecutors.forEach(executors::remove);
		return this;
	}

	private void initPluggedExecutors() {
		for (Extension extension : PluginManager.getInstance().getExtensions(
				KernelExtensionPoints.PLUGIN_ID, KernelExtensionPoints.EXTENSIONPOINT_PROTOCOL_EXECUTOR)) {

			//noinspection rawtypes
			ProtocolExecutor executor = (ProtocolExecutor) extension.getNewInstance();
			String entryClassName = extension.getParameter("entryClass");
			//noinspection rawtypes
			Class entryClass;
			try {
				entryClass = Class.forName(entryClassName, true, executor.getClass().getClassLoader());
			}
			catch (ClassNotFoundException e) {
				throw new IllegalStateException("referenced protocol entry class not found: " + entryClassName, e);
			}

			//noinspection unchecked
			executor(extension.getPriority(), entryClass, executor);
			this.pluggedExecutors.add(new Pair<>(executor, entryClass));
		}
	}

	/**
	 * Sets the id that will be used to initialize the session.
	 *
	 * @param id the id of the session
	 * @return this instance to chain method calls
	 */
	public SessionBuilder id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Sets the name that will be used to initialize the session.
	 *
	 * @param name the name of the session
	 * @return this instance to chain method calls
	 */
	public SessionBuilder name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Sets the protocol that will be replayed to initialize the session's diagnostic content.
	 *
	 * @param protocol the protocol to restore the diagnostic content from
	 * @return this instance to chain method calls
	 */
	public SessionBuilder protocol(Protocol protocol) {
		return protocol(protocol.getProtocolHistory());
	}

	/**
	 * Sets the protocol that will be replayed to initialize the session's diagnostic content.
	 *
	 * @param protocol the protocol to restore the diagnostic content from
	 * @return this instance to chain method calls
	 */
	public SessionBuilder protocol(Collection<? extends ProtocolEntry> protocol) {
		this.protocol = new ArrayList<>(protocol);
		return this;
	}

	/**
	 * Sets the create date that will be used to initialize the session. If the session is explicitly specified, this
	 * parameter is ignored.
	 *
	 * @param creationDate the date the session is created at
	 * @return this instance to chain method calls
	 */
	public SessionBuilder created(Date creationDate) {
		this.created = creationDate;
		return this;
	}

	/**
	 * Sets the create date that will be used to set to the session as the last change date. If not specified, the last
	 * change date will be the date this builder is executed at.
	 *
	 * @param lastChangeDate the date the session is most recently changed
	 * @return this instance to chain method calls
	 */
	public SessionBuilder changed(Date lastChangeDate) {
		this.changed = lastChangeDate;
		return this;
	}

	/**
	 * Sets the info store that will be used to initialize the session.
	 *
	 * @param infoStore the source info store to copy the entries from
	 * @return this instance to chain method calls
	 */
	public SessionBuilder info(InfoStore infoStore) {
		this.info = infoStore;
		return this;
	}

	/**
	 * Sets the session that will be initialized. If this method is called, no new session will be created by this
	 * builder.
	 *
	 * @param target the target session to be initialized
	 * @return this instance to chain method calls
	 */
	public SessionBuilder target(Session target) {
		this.session = target;
		return this;
	}

	/**
	 * Adds a new executor for protocol entries to this replay instance.
	 *
	 * @param entryClass the protocol entry class to add the executor for
	 * @param executor   the executor to be added
	 * @return this instance to chain multiple methods calls
	 */
	public <T extends ProtocolEntry> SessionBuilder executor(double priority, Class<T> entryClass, ProtocolExecutor<T> executor) {
		executors.add(priority, new Pair<>(executor, entryClass));
		return this;
	}

	/**
	 * Returns true if the specified session is currently being replayed.
	 *
	 * @param session the session to be checked
	 * @return true if the session is replayed
	 */
	public static boolean isReplaying(Session session) {
		return processing.containsKey(session);
	}

	/**
	 * The target session that is created from the source record. during replay this method will already return the
	 * specific session. After replay the method will still return the most recently created one. Before the first
	 * replay, the method throws an {@link IllegalStateException}
	 *
	 * @return the replaying or replayed session
	 */
	@NotNull
	public Session getSession() {
		if (session == null) throw new IllegalStateException();
		return session;
	}

	/**
	 * Returns all warnings that have been created during replay, or an empty set if no warning has been raised.
	 *
	 * @return the warning messages
	 */
	@NotNull
	public Collection<String> getWarnings() {
		return Collections.unmodifiableSet(warnings);
	}

	private void assertNotBuilt() {
		if (built) {
			throw new IllegalStateException("replay is running");
		}
	}

	/**
	 * Builds / replays the session as configured in this instance.
	 *
	 * @return the initialized session
	 */
	public Session build() {
		assertNotBuilt();
		built = true;

		// create new session, if not explicitly specified
		if (session == null) {
			session = SessionFactory.createSession(id, base, (created == null) ? new Date() : created);
		}

		// initialize various fields
		if (name != null) session.setName(name);
		if (info != null) InfoStoreUtil.copyEntries(info, session.getInfoStore());

		try {
			processing.put(session, this);
			executors.stream().map(Pair::getA).forEach(executor -> executor.prepare(this));

			// restore the session contents by replayng the protocol
			if (protocol != null) replayProtocol();

			executors.stream().map(Pair::getA).forEach(executor -> executor.complete(this));
		}
		finally {
			processing.remove(session);
		}

		// replace protocol
		session.getProtocol().clear();
		session.getProtocol().addEntries(protocol);

		// this must be the last operation to overwrite all touches within propagation
		if (changed != null) session.touch(changed);
		return session;
	}

	private void replayProtocol() {
		// we need a list for each date, because there can be multiple equal entries at the same date,
		// and we want them all and in the same order as in the original protocol
		Map<Date, List<ProtocolEntry>> groups = new LinkedHashMap<>();
		for (ProtocolEntry entry : protocol) {
			groups.computeIfAbsent(entry.getDate(), k -> new ArrayList<>()).add(entry);
		}

		for (Map.Entry<Date, List<ProtocolEntry>> entry : groups.entrySet()) {
			executors.forEach(pair -> {
				// replay the recorded indications
				List<ProtocolEntry> matches = entry.getValue().stream()
						.filter(pair.getB()::isInstance).collect(Collectors.toList());
				if (!matches.isEmpty()) {
					//noinspection unchecked
					pair.getA().handle(this, entry.getKey(), matches);
				}
				session.touch(entry.getKey());
			});
		}
	}

	public void warn(String message) {
		if (warnings.add(message)) {
			LOGGER.warn("Session Replay: " + message);
		}
	}
}
