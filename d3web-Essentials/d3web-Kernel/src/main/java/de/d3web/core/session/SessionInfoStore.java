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
package de.d3web.core.session;

import java.util.Locale;

import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * Special InfoStore for SessionHeaders, touches the defined SessionHeader when
 * something in the InfoStore changes.
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.10.2010
 */
public class SessionInfoStore extends DefaultInfoStore {

	public final SessionHeader session;

	public SessionInfoStore(SessionHeader session) {
		this.session = session;
	}

	@Override
	public boolean remove(Property<?> key) {
		if (super.remove(key)) {
			session.touch();
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		if (super.remove(key, language)) {
			session.touch();
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public <T> void addValue(Property<? super T> key, T value) {
		super.addValue(key, value);
		session.touch();
	}

	@Override
	public void addValue(Property<?> key, Locale language, Object value) {
		addValue(key, language, value, true);
	}

	public void addValue(Property<?> key, Locale language, Object value, boolean shouldTouch) {
		super.addValue(key, language, value);
		if (shouldTouch) {
			session.touch();
		}
	}

	public <T> void addValue(Property<? super T> key, T value, boolean touchSession) {
		addValue(key, Locale.ROOT, value, touchSession);
	}
}
