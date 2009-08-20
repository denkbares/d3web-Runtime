package de.d3web.persistence.xml.loader;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec;

/**
 * A codec to en- and decode java.util.LinkedList. The objects contained in the
 * list can only be en- and decoded, if there exists a codec for them.
 * 
 * @author Georg
 */
public class LinkedListCodec extends PropertyCodec {

    private PropertiesUtilities propertiesUtilities = null;

    public LinkedListCodec(PropertiesUtilities propertiesUtilities) {
        super(LinkedList.class);

        this.propertiesUtilities = propertiesUtilities;
        if (propertiesUtilities == null) {
            propertiesUtilities = new PropertiesUtilities();
        }
    }

    public String encode(Object o) {
        if (!(o instanceof LinkedList)) {
            return "";
        }
        List list = (List) o;

        StringBuffer sb = new StringBuffer();
        sb.append("<List>\n");

        for (Object entry : list) {
            sb
                    .append("<Entry class=\"" + entry.getClass().getName()
                            + "\" >\n");

            PropertyCodec pc = propertiesUtilities.findCodecFor(entry);
            if (pc != null) {
                sb.append(pc.encode(entry));
            }

            sb.append("</Entry>\n");
        }

        sb.append("</List>\n");
        return sb.toString();
    }

    public Object decode(Node n) {
        List list = new LinkedList();

        Node listNode = null;
        NodeList ns = n.getChildNodes();
        for (int i = 0; i < ns.getLength(); i++) {
            if (ns.item(i).getNodeName().equals("List")) {
                listNode = ns.item(i);
                break;
            }
        }

        if (listNode != null) {
            NodeList children = listNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node entry = children.item(i);
                if ((entry != null) && (entry.getNodeName().equals("Entry"))) {
                    String classname = entry.getAttributes().getNamedItem(
                            "class").getNodeValue();
                    PropertyCodec pc = propertiesUtilities
                            .findCodecFor(classname);

                    // [MISC]:aha:obsolete after supportknowledge refactoring is
                    // propagated
                    if (pc == null)
                        pc = propertiesUtilities.fuzzyFindCodecFor(classname);

                    Object o = pc.decode(entry);
                    if (o != null) {
                        list.add(o);
                    }
                }
            }
        }

        return list;
    }

}
