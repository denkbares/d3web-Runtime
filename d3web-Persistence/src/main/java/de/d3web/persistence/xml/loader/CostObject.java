package de.d3web.persistence.xml.loader;

/**
 * Realizes a static representation of costs of an IDObject.
 * This is needed because the costs must be set after parsing the IDObject.<br>
 * Creation date: (03.07.2001 15:40:43)
 * @author Norman Brümmer
 */
public class CostObject {

	private String id;
	private Double value;
	private String verbalization;
	private String unit;

	/**
	 * Creates a new CostObject with the given object ID and its value
	 */
	public CostObject(String id, Double value) {
		super();
		this.id = id;
		this.value = value;
	}

	/**
	 * Creates a new CostObject with the given object ID, the costs verbalization and its unit
	 */
	public CostObject(String id, String verbalization, String unit) {
		super();
		this.id = id;
		this.verbalization = verbalization;
		this.unit = unit;
	}

	/**
	 * Creation date: (03.07.2001 15:41:11)
	 * @return the id of the knowledge base object for which the costs are encapsulated here
	 */
	public String getId() {
		return id;
	}

	/**
	 * Creation date: (02.08.2001 15:24:35)
	 * @return the unit of the incapsulated costs
	 */
	public java.lang.String getUnit() {
		return unit;
	}

	/**
	 * Creation date: (03.07.2001 15:41:25)
	 * @return the value of the encapsulated costs
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Creation date: (03.07.2001 15:41:25)
	 * @return the verbalization of the encapsulated costs
	 */
	public String getVerbalization() {
		return verbalization;
	}

	public String toString() {
		return "(" + id + ")[" + value.doubleValue() + "]";
	}
}