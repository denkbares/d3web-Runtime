/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.dialog2.basics.usermanaging;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Insert the type's description here. Creation date: (05.10.2001 15:58:58)
 * 
 * @author: Norman Brümmer
 */
public class UserManager {

	private static UserManager instance;

	/**
	 * @return testServlet.userManaging.UserManager
	 */
	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}

	private UserLoader userLoader = null;

	private boolean loaded = false;

	/**
	 * UserManager constructor comment.
	 */
	private UserManager() {
		setUserLoader(new UserLoader());

	}

	/**
	 * @param u testServlet.userManaging.User
	 */
	public void addUser(User u) {
		removeUser(u);
		userLoader.getUsers().add(u);
		saveUsers();
	}

	/**
	 * 
	 * @return boolean
	 * @param email java.lang.String
	 */
	public boolean containsEmail(String email) {
		Iterator<User> iter = userLoader.getUsers().iterator();
		while (iter.hasNext()) {
			User u = iter.next();
			if (u.getEmail().equals(email)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return java.util.List
	 */
	public List<User> getAllLoadedUsers() {
		List<User> ret = new LinkedList<User>();
		if (!loaded) {
			reset();
		}
		ret.addAll(userLoader.getUsers());
		return ret;
	}

	/**
	 * @return de.d3web.gui.html.usermanaging.User
	 * @param email java.lang.String
	 */
	public User getUserByEmail(String email) {
		Iterator<User> iter = getAllLoadedUsers().iterator();
		while (iter.hasNext()) {
			User u = iter.next();
			if (u.getEmail().equals(email)) {
				return u;
			}
		}
		return null;
	}

	/**
	 * @return boolean
	 */
	public boolean hasLoaded() {
		return loaded;
	}

	/**
	 * @param u testServlet.userManaging.User
	 */
	public void removeUser(User u) {
		userLoader.getUsers().remove(u);

		saveUsers();
	}

	public void reset() {
		userLoader.load();
		loaded = true;
	}

	public void saveUsers() {
		UserWriter.writeUsers(getAllLoadedUsers(), userLoader.getFileURL());
	}

	/**
	 * @param newUserLoader testServlet.userManaging.UserLoader
	 */
	public void setUserLoader(UserLoader newUserLoader) {
		userLoader = newUserLoader;
		userLoader.load();
	}

	/**
	 * @return boolean
	 * @param u testServlet.userManaging.User
	 */
	public boolean validateUser(User u) {
		if (u == null) return false;
		return userLoader.getUsers().contains(u);
	}
}