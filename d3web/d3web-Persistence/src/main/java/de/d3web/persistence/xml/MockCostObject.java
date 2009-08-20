package de.d3web.persistence.xml;
/**
 * This class encapsulates the output-relevant attributes of a Cost-object.
 * Creation date: (08.06.2001 14:57:03)
 * @author Michael Scharvogel
 */
public class MockCostObject {
	private String ID = null;
	private String verbalization = null;
	private String unit = null;

	/**
	 * Default-constructor for MockCostObject
	 */
	public MockCostObject() {
	}

	/**
	 * Creates a new MockCostObject with the given parameters
	 */
	public MockCostObject(String ID, String verbalization, String unit) {
		this.ID = ID;
		this.verbalization = verbalization;
		this.unit = unit;
	}

	/**
	 * Creation date: (08.06.2001 16:18:31)
	 * @return the ID of the corresponding Cost-object
	 */
	public java.lang.String getID() {
		return ID;
	}

	/**
	 * Creation date: (08.06.2001 16:18:31)
	 * @return the verbalization of the corresponding Cost-object
	 */
	public java.lang.String getVerbalization() {
		return verbalization;
	}

	/**
	 * sets the ID-attribute
	 * Creation date: (08.06.2001 16:18:31)
	 */
	public void setID(String newID) {
		ID = newID;
	}

	/**
	 * sets the verbalization-attribute
	 * Creation date: (08.06.2001 16:18:31)
	 */
	public void setVerbalization(String newVerbalization) {
		verbalization = newVerbalization;
	}

	/**
	 * @return the unit of the corresponding Cost-object
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit-attribute
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}