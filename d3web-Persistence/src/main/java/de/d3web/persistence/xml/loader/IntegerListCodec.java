package de.d3web.persistence.xml.loader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.IntegerList;
import de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec;

public class IntegerListCodec extends PropertyCodec {

	public final static Class INTEGER_LIST_CLASS = IntegerList.class;
	public final static String TAG = "IntegerList";

	private static IntegerListCodec instance = new IntegerListCodec();
	
	public static IntegerListCodec getInstance() {
		return instance;
	}

	private IntegerListCodec() {
		super(INTEGER_LIST_CLASS);
	}

	@Override
	public String encode(Object o) {
		StringBuffer sb = new StringBuffer();
		sb.append("<" + TAG + ">");
		if(o instanceof IntegerList) {
			sb.append(((IntegerList)o).toParseableString(" "));
		}
		sb.append("</" + TAG + ">");
		return sb.toString();
	}

	@Override
	public Object decode(Node n) {
		NodeList nl = n.getChildNodes();
		for (int j = 0; j < nl.getLength(); j++) {
			Node node = nl.item(j);
			if (node.getNodeName().equals(TAG)) {
				return readIntegerList(node);
			}
		}
		return null;
	}

	private Object readIntegerList(Node node) {
		List list = new LinkedList();
		String text = node.getTextContent();
		Iterator iter = Arrays.asList(text.split(" ")).iterator();
		while (iter.hasNext()) {
			String element = (String) iter.next();
			list.add(Integer.parseInt(element));
		}
		return new IntegerList(list);
	}

}
