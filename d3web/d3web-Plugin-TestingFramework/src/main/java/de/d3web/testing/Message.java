/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A message contains a type which is one of SUCCESS, FAILURE, or ERROR and an
 * (optional) message text.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 21.05.2012
 */
public class Message implements Comparable<Message> {

	/**
	 * General success message, can be returned by tests in case of success if
	 * no text message is needed
	 */
	public static final Message SUCCESS = new Message(Type.SUCCESS, null);

	private final Type type;
	private final String message;
	private Collection<MessageObject> objects = new ArrayList<MessageObject>();

	public Message(Type type) {
		this(type, null);
		if (type == null) throw new NullPointerException();
	}

	public Type getType() {
		return type;
	}

	public String getText() {
		return message;
	}

	public Message(Type type, String message) {
		this.type = type;
		this.message = message;
	}

	public Message(Type type, String message, Collection<MessageObject> objects) {
		this(type, message);
		setObjects(objects);

	}

	public boolean isSuccess() {
		return type == Type.SUCCESS;
	}

	public void setObjects(Collection<MessageObject> objects) {
		this.objects = objects;
	}

	public Collection<MessageObject> getObjects() {
		return Collections.unmodifiableCollection(objects);
	}

	public enum Type {
		FAILURE,
		ERROR,
		SUCCESS
	}

	@Override
	public String toString() {
		return getType().toString() + ": " + getText();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Message other = (Message) obj;
		if (message == null) {
			if (other.message != null) return false;
		}
		else if (!message.equals(other.message)) return false;
		if (type != other.type) return false;
		return true;
	}

	@Override
	public int compareTo(Message o) {
		if (this.type == Type.FAILURE && o.type != Type.FAILURE) return -1;
		if (this.type == Type.ERROR && o.type == Type.SUCCESS) return -1;
		if (this.type != Type.FAILURE && o.type == Type.FAILURE) return 1;
		if (this.type == Type.SUCCESS && o.type == Type.ERROR) return 1;
		if (this.message != null && o.message == null) return -1;
		if (this.message == null && o.message != null) return 1;
		if (this.message == null && o.message == null) return 0;
		return this.message.compareTo(o.message);
	}

}
