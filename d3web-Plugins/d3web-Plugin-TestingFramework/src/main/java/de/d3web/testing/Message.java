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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.Contract;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;

/**
 * A message contains a type which is one of SUCCESS, FAILURE, or ERROR and an (optional) message text.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 21.05.2012
 */
public class Message implements Comparable<Message> {

	/**
	 * General success message, can be returned by tests in case of success if no text message is needed
	 */
	public static final Message SUCCESS = new Message(Type.SUCCESS, null);

	private final Type type;
	private final String message;
	private Collection<MessageObject> objects = new ArrayList<>();
	private final MultiMap<Boolean, File> attachments = new DefaultMultiMap<>();

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

	public Message(Type type, String message, MessageObject... objects) {
		this(type, message, Arrays.asList(objects));
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

	/**
	 * The potential type of testing/build Messages. The individual messages are ordered by severity.
	 */
	public enum Type {
		/**
		 * The specific test could not been executed due to an error, e.g. a wrong configuration or compile errors.
		 */
		ERROR,
		/**
		 * The specific test has failed to read its pass criteria.
		 */
		FAILURE,
		/**
		 * The specific test has failed to read its pass criteria due to an minor failure.
		 */
		WARNING,
		/**
		 * The specific test was not executed, e.g. because no test objects are available to the test.
		 */
		SKIPPED,
		/**
		 * The specific test has been passed successfully.
		 */
		SUCCESS;

		/**
		 * Merges the two specified message types. If both are null, null is returned. If any is null, the non-null one
		 * is returned. Otherwise the method returns the type with the highest severity.
		 */
		@Contract("null, null -> null; _, !null -> !null; !null, _ -> !null")
		public static Type merge(Type t1, Type t2) {
			// as the enum is ordered by severity, we return the one with the lowest ordinal
			if (t1 == null) return t2;
			if (t2 == null) return t1;
			return (t1.ordinal() <= t2.ordinal()) ? t1 : t2;
		}
	}

	/**
	 * Attaches a file to this message. You may also specify if the file is a temp file and shall be deleted after usage
	 * (e.g. after copied to the target folder or attached to the wiki page). If the file shall be deleted and the
	 * folder becomes empty after that, the folder will be deleted as well.
	 *
	 * @param attachment the source file to be attached
	 * @param autoDelete if the source shall be deleted automatically
	 */
	public void addAttachment(File attachment, boolean autoDelete) {
		attachment = attachment.getAbsoluteFile();
		attachments.put(autoDelete, attachment);
		if (autoDelete) attachment.deleteOnExit();
	}

	public Collection<File> getAttachments() {
		return attachments.valueSet();
	}

	public void handleAutoDelete() {
		for (File file : attachments.getValues(true)) {
			if (file.delete()) {
				// if file is deleted, also delete empty parent directory
				file.getParentFile().delete();
			}
		}
	}

	@Override
	public String toString() {
		return getType() + ": " + getText();
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
		return type == other.type;
	}

	@Override
	public int compareTo(Message o) {
		if (this.type == Type.FAILURE && o.type != Type.FAILURE) return -1;
		if (this.type == Type.ERROR && o.type == Type.SUCCESS) return -1;
		if (this.type != Type.FAILURE && o.type == Type.FAILURE) return 1;
		if (this.type == Type.SUCCESS && o.type == Type.ERROR) return 1;
		//noinspection StringEquality
		if (this.message == o.message) return 0;
		if (this.message == null) return 1;
		if (o.message == null) return -1;
		return this.message.compareTo(o.message);
	}
}
