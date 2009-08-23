/*
 * Created on 21.10.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.addons.ITemplateSession;
import de.d3web.caserepository.addons.train.TemplateSession;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * 21.10.2003 17:29:59
 * @author hoernlein
 */
public class TemplateSessionReader extends AbstractTagReader {

	protected TemplateSessionReader(String id) { super(id); }
	private static TemplateSessionReader instance;
	private TemplateSessionReader() { this("TemplateSessionReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new TemplateSessionReader();
		return instance;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"TemplateSession"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
	    if (qName.equals("TemplateSession")) {
		    startTemplateSession(attributes);
		}
	}

    /* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("TemplateSession")) {
			endTemplateSession();
		}
	}
	
	private Node root;
	private Node currentNode;
	
	private void startTemplateSession(Attributes attributes) {
	    String typeS = attributes.getValue("type");
	    TemplateSession.Type type = TemplateSession.typeForName(typeS);
	    Object o = null;
	    if (type == TemplateSession.SINGLE) {
	        String caseS = attributes.getValue("case");
	        CaseObject c = getID2CaseMapper().getCaseObject(caseS);
	        if (c == null)
	            Logger.getLogger(this.getClass().getName()).warning("no case with id '" + caseS + "'");
	        o = new TemplateSession(c);
	    } else
	        o = null;
	    Node newNode = new Node(o, type);
	    if (root == null) {
	        root = newNode;
	        currentNode = root;
	    } else {
	        currentNode.addChild(newNode);
	        currentNode = newNode;
	    }
	}

	private void endTemplateSession() {
	    currentNode = currentNode.getParent();
	    if (currentNode == root || currentNode == null) {
	        // at the top - now transform and set
	        getCaseObject().setTemplateSession(createFromNode(root));
	    }
	}
	
    private ITemplateSession createFromNode(Node node) {
        if (node.getType() == TemplateSession.SINGLE) {
            return (ITemplateSession) node.getObject();
        } else {
            List listOfTemplateSessions = new LinkedList();
            Iterator iter = node.getChildren().iterator();
            while (iter.hasNext()) {
                Node n = (Node) iter.next();
                listOfTemplateSessions.add(createFromNode(n));
            }
            return new TemplateSession(node.getType(), listOfTemplateSessions);
        }
    }
    
    /* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#initialize(de.d3web.kernel.domainModel.KnowledgeBase, de.d3web.caserepository.CaseObjectImpl)
	 */
	public void initialize(KnowledgeBase knowledgeBase, CaseObjectImpl caseObject) {
	    root = null;
	    currentNode = null;
		super.initialize(knowledgeBase, caseObject);
	}
	
	private static class Node {
	    private List children = new LinkedList();
	    private Node parent = null;
	    private Object o;
	    private TemplateSession.Type type;
	    private Node(Object o, TemplateSession.Type type) {
	        this.o = o;
	        this.type = type;
	    }
	    public Object getObject() { return o; }
	    public TemplateSession.Type getType() { return type; }
	    public List getChildren() { return children; }
	    public Node getParent() { return parent; }
	    public void addChild(Node child) {
	        children.add(children.size(), child);
	        child.parent = this;
	    }
	}

}
