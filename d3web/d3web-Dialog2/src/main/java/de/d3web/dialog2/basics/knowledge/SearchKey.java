package de.d3web.dialog2.basics.knowledge;

import org.apache.log4j.Logger;

/**
 * Insert the type's description here. Creation date: (27.01.2002 17:05:17)
 * 
 * @author: Norman Brümmer
 */
public class SearchKey {

    private String first = null;

    private String second = null;

    public static Logger logger = Logger.getLogger(SearchKey.class);

    /**
     * UserCasesSearchKey constructor comment.
     */
    public SearchKey(String first, String second) {
	super();

	this.first = first;
	this.second = second;

    }

    /**
     * @return boolean
     * @param o
     *            java.lang.Object
     */
    @Override
    public boolean equals(Object o) {

	logger.info("UserCasesSearchKey::equals(): " + toString() + "|"
		+ o.toString());

	if (!(o instanceof SearchKey))
	    return false;

	SearchKey other = (SearchKey) o;

	return other.getFirst().equals(first)
		&& other.getSecond().equals(second);

    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getFirst() {
	return first;
    }

    /**
     * @return java.lang.String
     */
    public java.lang.String getSecond() {
	return second;
    }

    /**
     * @return int
     */
    @Override
    public int hashCode() {
	return first.hashCode() * 1000 + second.hashCode();
    }

    /**
     * @return java.lang.String
     */
    @Override
    public String toString() {
	return "<" + first + ", " + second + ">\n";

    }
}