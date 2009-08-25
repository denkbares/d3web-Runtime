/*
 * AbstractExplainTest.java
 *
 * Created on 25. MÃ¤rz 2002, 16:57
 */

package de.d3web.explain.test;

import junit.framework.TestCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.QASet;

/**
 *
 * @author  betz
 */
public abstract class AbstractExplainTest  extends TestCase {
    
    /** Creates a new instance of AbstractExplainTest */
    public AbstractExplainTest(String name) {
        super(name);
    }
    
    protected void log(String log) {
    	System.out.println(log);
    }
    
    public static QASet findQ(String text, KnowledgeBase kb) {
        return kb.searchQASet(text);
    }
    public static Diagnosis findD(String text, KnowledgeBase kb) {
        return kb.searchDiagnosis(text);
    }
    
}
