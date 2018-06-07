/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.session;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.blackboard.SessionObject;

/**
 * Provides a mapping between Class name of a PSMethod (psMethod.getClass().getName()) and the instance of this {@link
 * PSMethod} used in the session of this {@link SessionObject}.
 * Don't use this Mapping during Session initialization, since mapping might not yet be complete.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 07.06.18
 */
public class PSMethodMapping implements SessionObject, SessionObjectSource<PSMethodMapping> {

	private static PSMethodMapping instance = null;
	private Map<String, PSMethod> psMethods = null;

	public PSMethodMapping(Map<String, PSMethod> psMethods) {
		this.psMethods = psMethods;
	}

	private PSMethodMapping() {
	}

	public static PSMethodMapping getInstance() {
		if (instance == null) instance = new PSMethodMapping();
		return instance;
	}

	@Override
	public PSMethodMapping createSessionObject(Session session) {
		Map<String, PSMethod> psMethods = new HashMap<>();
		for (PSMethod psMethod : session.getPSMethods()) {
			psMethods.put(psMethod.getClass().getName(), psMethod);
		}
		return new PSMethodMapping(psMethods);
	}

	/**
	 * Provides the instance of the {@link PSMethod} used in the session of this {@link SessionObject} and where the
	 * class name matches the given class name. If no such {@link PSMethod} exists in the session, <tt>null</tt> is
	 * returned.
	 *
	 * @param className the full class name of the {@link PSMethod}
	 * @return the {@link PSMethod} of the {@link Session} of this {@link SessionObject} with the given class name
	 */
	@Nullable
	public PSMethod getPSMethodForClassName(String className) {
		if (psMethods == null) {
			throw new IllegalStateException("Wrong usage of object, get use Session#getSessionObject(PSMethodMapping.getInstance()) to get valid mapping.");
		}
		return psMethods.get(className);
	}
}
