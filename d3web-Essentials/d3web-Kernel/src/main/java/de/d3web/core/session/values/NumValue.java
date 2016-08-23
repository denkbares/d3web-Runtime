/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.d3web.core.session.values;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * Represents a numerical value (internally stored as a {@link Double}).
 *
 * @author joba
 */
public class NumValue implements QuestionValue {

	private final double value;

	/**
	 * Constructs a new NumValue
	 *
	 * @param value the double for which a new NumValue should be instantiated
	 */
	public NumValue(double value) {
		this.value = value;
	}

	/**
	 * @return the {@link Double} of this num value
	 */
	@Override
	@NotNull
	public Double getValue() {
		return value;
	}

	public double getDouble() {
		return value;
	}

	@Override
	public int compareTo(@NotNull Value o) {
		if (o instanceof NumValue) {
			return Double.compare(value, ((NumValue) o).value);
		}
		return -1;
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NumValue)) return false;
		NumValue numValue = (NumValue) o;
		return Double.compare(numValue.value, value) == 0;
	}
}
