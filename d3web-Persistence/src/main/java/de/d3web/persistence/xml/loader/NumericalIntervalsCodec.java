package de.d3web.persistence.xml.loader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec;

/**
 * A codec to en- and decode NumericalIntervals.
 * @author Georg
 */
public class NumericalIntervalsCodec extends PropertyCodec {

	public final static Class NUMERICAL_INTERVALL_CLASS = NumericalInterval.class;

	private static NumericalIntervalsCodec instance = null;
	
	public static NumericalIntervalsCodec getInstance() {
		if(instance == null) {
			instance = new NumericalIntervalsCodec();
		}
		return instance;
	}

	private NumericalIntervalsCodec() {
		super(NUMERICAL_INTERVALL_CLASS);
	}
	
	/**
	 * @param o the object to encode (that may be a List of NumericalIntervals or a single
	 * NumericalInterval)
	 */
	public String encode(Object o) {
		StringBuffer sb = new StringBuffer();
		
		if (o instanceof List) {
			sb.append("<" + NumericalIntervalsUtils.GROUPTAG + ">");
			Iterator iter = ((List) o).iterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				sb.append(encode(element));
			}
			sb.append("</" + NumericalIntervalsUtils.GROUPTAG + ">");
			
		} else if (o instanceof NumericalInterval) {
			NumericalInterval i = (NumericalInterval) o;
			sb.append("<" + NumericalIntervalsUtils.TAG + " "
				+ NumericalIntervalsUtils.interval2lowerAttribute(i) + " "
				+ NumericalIntervalsUtils.interval2upperAttribute(i) + " "
				+ NumericalIntervalsUtils.interval2typeAttribute(i) + " "
				+ " />");
		}

		return sb.toString();
	}

	
	/**
	 * @param n Property-node
	 * @return Object which is a List of NumericalInterval (or null)
	 */
	public Object decode(Node n) {
		NodeList nl = n.getChildNodes();
		for (int j = 0; j < nl.getLength(); j++) {
			Node node = nl.item(j);
			if (node.getNodeName().equals(NumericalIntervalsUtils.GROUPTAG)) {
				return readNumericalIntervals(node);
			} else if (node.getNodeName().equals(NumericalIntervalsUtils.TAG)) {
				return readNumericalInterval(node);
			}
		}
		return null;
	}
	
	public List readNumericalIntervals(Node n) {
		List intervals = new LinkedList();
		NodeList nodeList = n.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(NumericalIntervalsUtils.TAG)) {
				Object o = readNumericalInterval(node);
				if (o != null) {
					intervals.add(o);
				}
			}
		}
		return intervals;
	}
	
	public NumericalInterval readNumericalInterval(Node n) {
		try {
			boolean[] types = NumericalIntervalsUtils.node2booleanTypes(n);
			NumericalInterval i = new NumericalInterval(
				NumericalIntervalsUtils.node2lower(n),
				NumericalIntervalsUtils.node2upper(n),
				types[0],
				types[1]);
			return i;
		} catch (NumericalIntervalsUtils.NumericalIntervalException e) {
			Logger.getLogger(this.getClass().getName()).warning("Exception while reading NumericalInterval");
		}
		return null;
	}

}
