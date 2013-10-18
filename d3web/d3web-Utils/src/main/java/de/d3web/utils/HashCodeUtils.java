/*
 * The following utility class allows simple construction of an effective
 * hashCode method. It is based on the recommendations of Effective Java, by
 * Joshua Bloch.
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
package de.d3web.utils;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * 
 * Example use case:
 * 
 * <pre>
 * <!-- --> public int hashCode() {
 * 	int result = HashCodeUtil.SEED;
 * 	// collect the contributions of various fields
 * 	result = HashCodeUtil.hash(result, myPrimitive);
 * 	result = HashCodeUtil.hash(result, myString);
 * 	result = HashCodeUtil.hash(result, myObject);
 * 	result = HashCodeUtil.hash(result, myArray);
 * 	return result;
 * }
 * </pre>
 * 
 * @author volker_belli
 * @created 19.10.2010
 */
public final class HashCodeUtils {

	/**
	 * An initial value for a <code>hashCode</code>, to which is added
	 * contributions from fields. Using a non-zero value decreases collisions of
	 * <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	/**
	 * Prime number to multiply first terms before adding the additional term.
	 */
	private static final int PRIME_MULTIPLIER = 31;

	/**
	 * Appends a boolean value onto the specified seed.
	 */
	public static int hash(int aSeed, boolean aBoolean) {
		return firstTerm(aSeed) + (aBoolean ? 1 : 0);
	}

	/**
	 * Appends a character value onto the specified seed.
	 */
	public static int hash(int aSeed, char aChar) {
		return firstTerm(aSeed) + (int) aChar;
	}

	/**
	 * Appends an integer value onto the specified seed.
	 */
	public static int hash(int aSeed, int aInt) {
		return firstTerm(aSeed) + aInt;
	}

	/**
	 * Appends a long value onto the specified seed.
	 */
	public static int hash(int aSeed, long aLong) {
		return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
	}

	/**
	 * Appends a float value onto the specified seed.
	 */
	public static int hash(int aSeed, float aFloat) {
		return hash(aSeed, Float.floatToIntBits(aFloat));
	}

	/**
	 * Appends a double value onto the specified seed.
	 */
	public static int hash(int aSeed, double aDouble) {
		return hash(aSeed, Double.doubleToLongBits(aDouble));
	}

	/**
	 * Appends a object value onto the specified seed. <code>aObject</code> is a
	 * possibly-null object, and possibly an array. If <code>aObject</code> is
	 * an array, then each element may be a primitive or a (possibly-null)
	 * object.
	 */
	public static int hash(int aSeed, Object aObject) {
		int result = aSeed;
		if (aObject == null) {
			result = hash(result, 0);
		}
		else if (!isArray(aObject)) {
			result = hash(result, aObject.hashCode());
		}
		else {
			int length = Array.getLength(aObject);
			for (int idx = 0; idx < length; ++idx) {
				Object item = Array.get(aObject, idx);
				// recursive call!
				result = hash(result, item);
			}
		}
		return result;
	}

	private static int firstTerm(int aSeed) {
		return PRIME_MULTIPLIER * aSeed;
	}

	private static boolean isArray(Object aObject) {
		return aObject.getClass().isArray();
	}

	/**
	 * Appends an unordered set of objects onto the specified seed. This will
	 * result on the same hash if the objects are equal, independent of the
	 * order of the objects in the specified collection.
	 */
	public static int hashUnordered(int aSeed, Collection<?> aCollection) {
		int hash = 0;
		// hashcode is independent from the order of the list
		if (aCollection != null) {
			for (Object object : aCollection) {
				hash += (object == null) ? 1 : object.hashCode();
			}
		}
		return hash(aSeed, hash);
	}

	/**
	 * Appends an ordered collection of objects onto the specified seed. This
	 * will result on the same hash if the objects are equal, and the object
	 * have the same order when iterating the specified collection.
	 */
	public static int hashOrdered(int aSeed, Collection<?> aCollection) {
		if (aCollection != null) {
			for (Object object : aCollection) {
				aSeed = hash(aSeed, object);
			}
		}
		return aSeed;
	}
}
