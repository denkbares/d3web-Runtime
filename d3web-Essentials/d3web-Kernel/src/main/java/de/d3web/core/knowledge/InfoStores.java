/*
 * Copyright (C) 2020 denkbares GmbH, Germany
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

package de.d3web.core.knowledge;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.denkbares.utils.Triple;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * Utiloity class to provide some useful InfoStore-related methods.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 06.04.2020
 */
public class InfoStores {

	private static final InfoStore empty = new EmptyInfoStore();

	/**
	 * Returns the singleton instance of this empty info-store implementation.
	 *
	 * @return an empty, immutable InfoStore.
	 */
	@NotNull
	public static InfoStore empty() {
		return empty;
	}

	/**
	 * Returns an unmodifiable instance that maps to the specified info-store, but prevent any modification.
	 *
	 * @return an immutable InfoStore, mapped to the specified info-store
	 */
	@Contract("!null -> !null; null -> null")
	public static InfoStore unmodifiable(InfoStore delegate) {
		if (delegate == null) return null;
		return new UnmodifiableInfoStore(delegate);
	}

	/**
	 * InfoStore implementation, that is empty, and does not allow to add any values.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 06.04.2020
	 */
	private static final class EmptyInfoStore implements InfoStore {

		/**
		 * Private constructor to avoid that instances are created. Use {@link #empty()} instead.
		 */
		private EmptyInfoStore() {
		}

		@Override
		public <StoredType> StoredType getValue(Property<StoredType> key, Locale... language) {
			return key.getDefaultValue();
		}

		@Override
		public boolean remove(Property<?> key) {
			return false;
		}

		@Override
		public boolean remove(Property<?> key, Locale language) {
			return false;
		}

		@Override
		public boolean contains(Property<?> key) {
			return false;
		}

		@Override
		public boolean contains(Property<?> key, Locale language) {
			return false;
		}

		@Override
		public <T> void addValue(Property<? super T> key, T value) throws ClassCastException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addValue(Property<?> key, Locale language, Object value) throws ClassCastException {
			throw new UnsupportedOperationException();
		}

		@Override
		@NotNull
		public Collection<Triple<Property<?>, Locale, Object>> entries() {
			return Collections.emptySet();
		}

		@Override
		@NotNull
		public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
			return Collections.emptyMap();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	/**
	 * InfoStore implementation, that delegates to another info-store, but prevents any changes to the info store.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 06.04.2020
	 */
	private static final class UnmodifiableInfoStore implements InfoStore {

		private final InfoStore delegate;

		private UnmodifiableInfoStore(InfoStore delegate) {
			this.delegate = delegate;
		}

		@Override
		public <StoredType> StoredType getValue(Property<StoredType> key, Locale... language) {
			return delegate.getValue(key, language);
		}

		@Override
		public boolean remove(Property<?> key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Property<?> key, Locale language) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(Property<?> key) {
			return delegate.contains(key);
		}

		@Override
		public boolean contains(Property<?> key, Locale language) {
			return delegate.contains(key, language);
		}

		@Override
		public <T> void addValue(Property<? super T> key, T value) throws ClassCastException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addValue(Property<?> key, Locale language, Object value) throws ClassCastException {
			throw new UnsupportedOperationException();
		}

		@Override
		@NotNull
		public Collection<Triple<Property<?>, Locale, Object>> entries() {
			return Collections.unmodifiableCollection(delegate.entries());
		}

		@Override
		@NotNull
		public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
			return Collections.unmodifiableMap(delegate.entries(key));
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}
	}
}
