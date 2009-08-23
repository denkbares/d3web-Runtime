package de.d3web.caserepository.sax;

import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.ISolutionContainer;
import de.d3web.caserepository.sax.ID2CaseMapper;
import de.d3web.kernel.domainModel.KnowledgeBase;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class AbstractTagReader {
	
	private static ISolutionContainer solutionContainer = null;

	private CaseObjectImpl caseObject = null;
	private KnowledgeBase knowledgeBase = null;
	
	private String textBetweenCurrentTag = null;
	private boolean active = false;
	
//	private List tagNames = null;
	
	private String id = null;
    private ID2CaseMapper id2caseMapper;
	
	protected AbstractTagReader(String id) {
		this.id = id;	
//		tagNames = new LinkedList();
	}
	
	public String getId() {
		return id;	
	}
	
	public static AbstractTagReader getInstance() {
		Logger.getLogger(AbstractTagReader.class.getName()).severe(
			"WARNING! You have to override 'getInstance()' in your class extending AbstractTagReader!!!"
		);
		
		// has to be overridden!!
		return null;
	}

	public CaseObjectImpl getCaseObject() {
		return caseObject;
	}

	public void initialize(KnowledgeBase knowledgeBase, CaseObjectImpl caseObject) {
		this.caseObject = caseObject;
		this.knowledgeBase = knowledgeBase;
		setSolutionContainer(caseObject);
	}
	
	protected void setSolutionContainer(ISolutionContainer sc) {
		solutionContainer = sc;
	}
	
	protected void addSolution(CaseObject.Solution s) {
		solutionContainer.addSolution(s);
	}

	protected String checkAttribute(String name, String isValue, String defaultValue) {

		if (isValue == null) {
			Logger.getLogger(this.getClass().getName()).warning("Attribute '" + name + "' not set. setting to: '" + defaultValue + "'.");
			return defaultValue;
		} else {
			return isValue;
		}
	}
	
	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	
	public String getTextBetweenCurrentTag() {
		// for compatibility with the DOMCaseRepository stuff
		// text in empty tags yields a String with zero length ("") 
		if (textBetweenCurrentTag != null) {
			return textBetweenCurrentTag.trim();
		} else
			return "";
	}
	
	/**
	 * @param string
	 */
	public void setTextBetweenCurrentTag(String string) {
		textBetweenCurrentTag = string;
	}


	public void characters(char[] chars, int start, int length) {
		if (!active) {
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int pos = (start + i) % chars.length; 
			sb.append(chars[pos]);
		}

		if (textBetweenCurrentTag == null) {
			textBetweenCurrentTag = sb.toString();
		} else {
			textBetweenCurrentTag += sb.toString();
		}
				
	}

	
	public final void activateAndStartElement(String uri, String localName, String qName, Attributes attributes) {
		setTextBetweenCurrentTag(null);
		active = true;
		startElement(uri, localName, qName, attributes);
	}
	
	public final void deactivateAndEndElement(String uri, String localName, String qName) {
		endElement(uri, localName, qName);
		active = false;
		setTextBetweenCurrentTag(null);
	}
	
	protected abstract void startElement(String uri, String localName, String qName, Attributes attributes);
	
	protected abstract void endElement(String uri, String localName, String qName);

	public abstract List getTagNames();

    /**
     * @param me
     */
    public void setID2CaseMapper(ID2CaseMapper id2caseMapper) {
        this.id2caseMapper = id2caseMapper;
    }

    protected ID2CaseMapper getID2CaseMapper() {
        return id2caseMapper;
    }

}
