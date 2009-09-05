package de.d3web.dialog2.basics.usermanaging;

import org.apache.log4j.Logger;

/**
 * @author: Norman Brümmer
 */
public class User {

    private String name;

    private String password;

    private String email;

    private String forename;

    private boolean admin = false;

    public static Logger logger = Logger.getLogger(User.class);

    /**
     * User constructor comment.
     */
    public User() {
	super();
	email = "";
	password = "";
	forename = "";
	name = "";

    }

    /**
     * @return boolean
     * @param o
     *            java.lang.Object
     */
    @Override
    public boolean equals(Object o) {
	if (o == null)
	    return false;
	if (o instanceof User) {
	    User u = (User) o;

	    logger.info("User1: " + u);
	    logger.info("User2: " + this);

	    return u.getEmail().equals(email)
		    && ((u.getPassword().equals(password)) || (u.getName()
			    .equals(name)));

	}
	return false;
    }

    public String getCompleteName() {
	String ret = forename + " " + name;
	return ret.trim();
    }

    /**
     * @return String
     */
    public String getEmail() {
	return email;
    }

    /**
     * @return String
     */
    public String getForename() {
	return forename;
    }

    /**
     * @return String
     */
    public String getName() {
	return name;
    }

    /**
     * @return String
     */
    public String getPassword() {
	return password;
    }

    public String getXMLString() {
	return "<User name='" + name + "' forename='" + forename + "' pass='"
		+ password + "' email='" + email + "' admin='" + admin + "' />";
    }

    public boolean isAdmin() {
	return admin;
    }

    /**
     * @param newAdmin
     *            boolean
     */
    public void setAdmin(boolean newAdmin) {
	admin = newAdmin;
    }

    /**
     * @param newEmail
     *            String
     */
    public void setEmail(String newEmail) {
	email = newEmail;
    }

    /**
     * @param newForename
     *            String
     */
    public void setForename(String newForename) {
	forename = newForename;
    }

    /**
     * @param newName
     *            String
     */
    public void setName(String newName) {
	name = newName;
    }

    /**
     * @param newPassword
     *            String
     */
    public void setPassword(String newPassword) {
	password = newPassword;
    }

    /**
     * 
     * @return String
     */
    @Override
    public String toString() {
	return "User: \n  Name: " + name + "\n Fname: " + forename
		+ "\n Pass: " + password + "\n  Mail: " + email + "\n\n";
    }

}