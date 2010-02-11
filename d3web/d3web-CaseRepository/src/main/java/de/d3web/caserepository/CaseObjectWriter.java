/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.d3web.caserepository.addons.IExaminationBlock;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.info.DCMarkup;
import de.d3web.core.terminology.info.Properties;
import de.d3web.core.terminology.info.Property;
import de.d3web.persistence.xml.loader.DCMarkupUtilities;
import de.d3web.persistence.xml.loader.PropertiesUtilities;

/**
 * 24.09.2003 15:09:22
 * @author hoernlein
 */
public class CaseObjectWriter implements XMLCodeGenerator {
    
    private CaseObject caseObject;
    
    private CaseObjectWriter() { /* hide empty constructor */ }
    public CaseObjectWriter(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    private final static class SourceSystemCodec extends PropertiesUtilities.PropertyCodec {

        public SourceSystemCodec(Class clazz) {
            super(clazz);
        }
        
        /* (non-Javadoc)
         * @see de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec#encode(java.lang.Object)
         */
        public String encode(Object o) {
            return ((CaseObject.SourceSystem) o).getName();
        }

        /* (non-Javadoc)
         * @see de.d3web.persistence.xml.loader.PropertiesUtilities.PropertyCodec#decode(org.w3c.dom.Node)
         */
        public Object decode(Node n) {
            throw new UnsupportedOperationException("the CaseObjectImpl.SourceSystemCodec should not be used for decoding.");
        }
        
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
     */
    public String getXMLCode() {
        preprocess();

        StringBuffer sb = new StringBuffer();

        sb.append("<Problem>\n");
        
        sb.append(toXML(caseObject.getDCMarkup()));

        sb.append(toXML(caseObject.getProperties()));
        
        sb.append("<Questions>\n");
        Iterator questionsIter = caseObject.getQuestions().iterator();
        while (questionsIter.hasNext()) {
            Question quest = (Question) questionsIter.next();
            // nur um weitermachen zu können!
            Collection answerColl = caseObject.getAnswers(quest);
            if (answerColl != null && !answerColl.isEmpty()) {
                sb.append("<Question id='" + quest.getId() + "'>\n");
                Iterator answerIter = answerColl.iterator();
                if (answerIter != null) {
                    while (answerIter.hasNext()) {
                        Answer ans = (Answer) answerIter.next();
                        if (ans != null) {
                            if (ans instanceof AnswerUnknown)
                                sb.append("<UnknownAnswer />\n");
                            else if (ans instanceof AnswerChoice)
                                sb.append("<Answer id='"  + ans.getId() + "'/>\n");
                            else if (ans instanceof AnswerNum)
                                sb.append("<Answer value='"  + ans.getValue(null).toString() + "'/>\n");
                            else if (ans instanceof AnswerText) {
                                sb.append("<Answer><![CDATA[" + ans.getValue(null).toString() + "]]></Answer>");
                            }else if (ans instanceof AnswerDate) {
                                sb.append("<Answer date='" + ((AnswerDate)ans).getDateString() + "'/>");
                            } else
                                Logger.getLogger(this.getClass().getName()).warning("no way to encode answers of type " + ans.getClass());
                        }
                    }
                }
                sb.append("</Question>\n");
            }
        }
        sb.append("</Questions>\n");

        sb.append(SolutionContainerImpl.getXMLCode(caseObject.getSolutions()));

        sb.append("</Problem>\n");

        return sb.toString();
    }
    
    /**
     * @param properties
     * @return
     */
    private Object toXML(Properties properties) {
        Collection c = Arrays.asList(new Property[] { 
                Property.CASE_COMMENT,
                Property.CASE_METADATA,
                Property.CASE_SOURCE_SYSTEM,
                Property.CASE_KNOWLEDGEBASE_DESCRIPTOR,
                Property.CASE_CRITIQUE_TEXT,
        });
        PropertiesUtilities pu = new PropertiesUtilities();
        pu.addCodec(new MetaDataImpl.Codec(MetaDataImpl.class));
        pu.addCodec(new SourceSystemCodec(CaseObject.SourceSystem.class));
        return pu.propertiesToString(properties, c);
    }

    /**
     * @param dcData
     * @return
     */
    private Object toXML(DCMarkup markup) {
        return DCMarkupUtilities.dcmarkupToString(markup);
    }

    private void preprocess() {

        // add all examinination blocks to qcontainers
        if (caseObject.getExaminationBlocks() != null) {
            Iterator iter = caseObject.getExaminationBlocks().getAllBlocks().iterator();
            while (iter.hasNext()) {
                IExaminationBlock exBlk = (IExaminationBlock) iter.next();
                Iterator citer = exBlk.getContents().iterator();
                while (citer.hasNext())
                    caseObject.getAppliedQSets().setApplied((QContainer) citer.next());
            }
        }

        if (caseObject.getContents() != null) {
            // add all qcontainers with content to qcontainers
            Iterator iter = caseObject.getContents().getAllWithContent().iterator();
            while (iter.hasNext()) {
                QASet qaset = (QASet) iter.next();
                if (qaset instanceof QContainer) {
                    caseObject.getAppliedQSets().setApplied((QContainer) qaset);
                } else if (qaset instanceof Question) {
                    Logger.getLogger(this.getClass().getName()).warning("Question with Content -/-> Parent QContainer is set as applied");
                }
            }
        }
        
    }

}
