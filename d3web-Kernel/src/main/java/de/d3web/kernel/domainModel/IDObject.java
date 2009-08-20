package de.d3web.kernel.domainModel;


/**
 * Abstract class for knowledge base objects, which provide
 * an ID for retrieval. Nearly all knowledge base objects
 * should extend this class.
 * 
 * @author joba, Christian Betz
 * @see NamedObject
 */
abstract public class IDObject
	extends EventSource
	implements java.io.Serializable, IDReference {
	private String id;

	protected IDObject() {
	    super();
	}
	
	public IDObject(String id) {
	    this();
	    this.id = id;
	}
	
	/**
	 * Checks, if other object is an IDObject and if
	 * it contains the same ID.
	 * @return true, if equal
	 * @param other Object to compare for equality
	 */
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			IDObject otherIDO = (IDObject) other;
			if ((getId() != null) && (otherIDO.getId() != null)) {
				return getId().equals(otherIDO.getId());
			} else {
				return super.equals(other);
			}
		}
	}

	/**
	 * @return the unique identifier of this object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the hash code of the ID (String).
	 */
	public int hashCode() {
		if (getId() != null)
			return getId().hashCode();
		else
			return super.hashCode();
	}

	/**
	 * Sets the new identifier for this object. This id must 
	 * be unique but there is no check by the system of its uniqueness.
	 * 
	 * COMMENT(jochen): uniqueness now check by hashes, when changeID method is used --> deprecated
	 * TODO: this method should be package visible only - lots of tests would have to be refactored.
	 *   
	 * @param newId java.lang.String a new identifier
	 */
	@Deprecated
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the ID of the object.
	 */
	public String toString() {
		String className = this.getClass().getName();
		try {
			className = className.substring(className.lastIndexOf(".") + 1);
		} catch (Exception ex) {
		}
		return className + " " + getId();
	}
}
