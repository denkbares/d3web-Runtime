/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.session.builder;

import java.util.Date;
import java.util.List;

/**
 * Pluggable Interface to execute the contents of a protocol entry into a session. May also be used to get some
 * notifications for entries during protocol replay of the {@link SessionBuilder}.
 * <p>
 * For each replayed protocol/session, there will be a single, non-reused instance of the implementors.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 03.05.2018
 */
@FunctionalInterface
public interface ProtocolExecutor<T> {
	/**
	 * Method to be called before the first entry is processed by any executor.
	 *
	 * @param builder the session builder this executor is created for
	 */
	default void prepare(SessionBuilder builder) {
	}

	/**
	 * This method is called for any same-dated group of entries that matches this executor.
	 *
	 * @param builder the session builder to execute the entries for
	 * @param date    the (shared) date of the specified entries
	 * @param entries the entries of the date
	 */
	void handle(SessionBuilder builder, Date date, List<T> entries);

	/**
	 * Method to be called before after the last entry is processed by any executor.
	 *
	 * @param builder the session builder this executor is created for
	 */
	default void complete(SessionBuilder builder) {
	}
}
