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
 * Created on 20.02.2004
 */
package de.d3web.caserepository.addons.train;

import java.rmi.server.UID;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.ISolutionContainer;
import de.d3web.caserepository.addons.IExaminationBlock;
import de.d3web.caserepository.addons.ITemplateSession;
import de.d3web.caserepository.addons.shared.AppliedQSets;
import de.d3web.caserepository.utilities.Utilities;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * TemplateSession (in )
 * de.d3web.caserepository.addons.train
 * d3web-CaseRepository
 * @author hoernlein
 * @date 20.02.2004
 */
public class TemplateSession implements ITemplateSession {
    
    public static class Type {
        private String name;
        private Type(String name) { this.name = name; }
        public String getName() { return name; }
    }
    
    public static Type ALTERNATIVES = new Type("alternatives");
    public static Type STACK = new Type("stack");
    public static Type SINGLE = new Type("single");

    public static Type typeForName(String typeS) {
        if (typeS.equals(ALTERNATIVES.getName()))
            return ALTERNATIVES;
        else if (typeS.equals(STACK.getName()))
            return STACK;
        else if (typeS.equals(SINGLE.getName()))
            return SINGLE;
        else
            return null;
    }
    
    private Type type;
    private List listOfTemplateSessions;
    private CaseObject caseObject;
    
    private TemplateSession() { /* hide empty constructor */ }
    public TemplateSession(CaseObject caseObject) {
        this.type = SINGLE;
        this.caseObject = caseObject;
    }
    public TemplateSession(Type type, List listOfTemplateSessions) {
        this.type = type;
        if (type == SINGLE) {
            this.caseObject = ((TemplateSession) listOfTemplateSessions.get(0)).getCaseObject();
        } else {
	        this.listOfTemplateSessions = listOfTemplateSessions;
        }
    }
    
    public CaseObject getCaseObject() {
        if (type == SINGLE) {
            return caseObject;
        } else if (type == ALTERNATIVES) {
            int n = (int) (Math.random() * listOfTemplateSessions.size());
            return ((ITemplateSession) listOfTemplateSessions.get(n)).getCaseObject();
        } else if (type == STACK) {
            Iterator iter = listOfTemplateSessions.iterator();
            CaseObject co1 = ((ITemplateSession) iter.next()).getCaseObject();
            while (iter.hasNext()) {
                CaseObject co2 = ((ITemplateSession) iter.next()).getCaseObject();
                co1 = merge(co1, co2);
            }
            return co1;
        } else
            throw new RuntimeException("not implemented for type '" + type + "'");
    }
    
    /**
     * @param co1 CaseObject
     * @param co2 CaseObject
     * @return CaseObject
     */
    private CaseObject merge(CaseObject co1, CaseObject co2) {
        CaseObjectImpl coi1 = null;
        CaseObjectImpl coi2 = null;
        try {
            coi1 = (CaseObjectImpl) co1;
        } catch (ClassCastException ex) {
            throw new RuntimeException("not implemented for CaseObjects of class " + co1.getClass().getName());
        }
        try {
            coi2 = (CaseObjectImpl) co2;
        } catch (ClassCastException ex) {
            throw new RuntimeException("not implemented for CaseObjects of class " + co2.getClass().getName());
        }
        
        CaseObjectImpl result = new CaseObjectImpl(coi1.getKnowledgeBase());
        Iterator iter = null;
        Set qasetsWithChangedValues = new HashSet();

        /* dcmarkup */
        
        // dcmarkup is replaced with generated
        
        DCMarkup dcm = new DCMarkup();
        dcm.setContent(DCElement.IDENTIFIER, Utilities.idify(new UID().toString()));
        dcm.setContent(DCElement.CREATOR, "TemplateSession algorithm");
        dcm.setContent(DCElement.DATE, DCElement.date2string(new Date(System.currentTimeMillis())));
        dcm.setContent(DCElement.TITLE, "generated");
        result.setDCMarkup(dcm);
        
        /* metadata */
        
        // metadata is ignored
        
        /* properties */
        
        // properties are replaced with generated
        
        result.getProperties().setProperty(Property.CASE_COMMENT, "merger of '" + coi1.getId() + "' and '" + coi2.getId() + "'");
        result.getProperties().setProperty(Property.CASE_SOURCE_SYSTEM, CaseObject.SourceSystem.TEMPLATES);
        
        /* questions and answers */
        
        iter = coi1.getQuestions().iterator();
        while (iter.hasNext()) {
            Question q = (Question) iter.next();
            result.addQuestionAndAnswers(q, coi1.getValue(q));
        }
        iter = coi2.getQuestions().iterator();
        while (iter.hasNext()) {
            Question q = (Question) iter.next();
            if (result.getQuestions().contains(q))
                qasetsWithChangedValues.add(q);
            result.addQuestionAndAnswers(q, coi2.getValue(q));
        }
        
        /* solutions */
        
        // all solutions of former CaseObject are ignored
        // solutions of generated CaseObject are those of latter CaseObject
        
        cloneSolutions(coi2, result);
        
        /* applied qsets */

        // applied qsets are merged
        // essential qsets are merged
        // start qsets are those of latter CaseObject
        
        AppliedQSets coi1AQS = (AppliedQSets) coi1.getAppliedQSets();
        iter = coi1AQS.getAllApplied().iterator();
        while (iter.hasNext())
            result.getAppliedQSets().setApplied((QContainer) iter.next());
        iter = coi1AQS.getAllEssential().iterator();
        while (iter.hasNext())
            result.getAppliedQSets().setEssential((QContainer) iter.next());
        
        AppliedQSets coi2AQS = (AppliedQSets) coi2.getAppliedQSets();
        iter = coi2AQS.getAllApplied().iterator();
        while (iter.hasNext())
            result.getAppliedQSets().setApplied((QContainer) iter.next());
        iter = coi1AQS.getAllEssential().iterator();
        while (iter.hasNext())
            result.getAppliedQSets().setEssential((QContainer) iter.next());
        iter = coi1AQS.getAllStart().iterator();
        while (iter.hasNext())
            result.getAppliedQSets().setStart((QContainer) iter.next());
        
        /* contents */
        
        Contents resContents = new Contents();
        if (coi1.getContents() != null) {
	        Contents coi1Contents = (Contents) coi1.getContents();
	        iter = coi1Contents.getAllWithContent().iterator();
	        while (iter.hasNext()) {
	            QASet q = (QASet) iter.next();
	            resContents.setContent(q, coi1Contents.getContent(q));
	        }
        }
        if (coi2.getContents() != null) {
	        Contents coi2Contents = (Contents) coi1.getContents();
	        iter = coi2Contents.getAllWithContent().iterator();
	        while (iter.hasNext()) {
	            QASet q = (QASet) iter.next();
	            if (resContents.getContent(q) != null)
	                qasetsWithChangedValues.add(q);
	            resContents.setContent(q, coi2Contents.getContent(q));
	        }
        }
        result.setContents(resContents);
        
        /* multimedia */
        
        // all features of items of former CaseObject that point to qasets which have changed are removed
        // featureless items of former CaseObject are ignored
        // all items of latter CaseObject are copied
        
        result.setMultimedia(new Multimedia());
        if (coi1.getMultimedia() != null && !((Multimedia) coi1.getMultimedia()).getMultimediaItems().isEmpty()) {
            iter = ((Multimedia) coi1.getMultimedia()).getMultimediaItems().iterator();
	        while (iter.hasNext()) {
	            MultimediaItem mmi = ((MultimediaItem) iter.next());
	            MultimediaItem res = mmi.cloneMe();
	            Iterator iiter = mmi.getFeatures().iterator();
	            while (iiter.hasNext()) {
	                Feature f = (Feature) iiter.next();
	                if (qasetsWithChangedValues.contains(f.getQASet()))
	                    res.getFeatures().remove(f);
	            }
	            if (!res.getFeatures().isEmpty())
	                ((Multimedia) result.getMultimedia()).addMultimediaItem(res);
	        }
        }
        if (coi2.getMultimedia() != null && !((Multimedia) coi2.getMultimedia()).getMultimediaItems().isEmpty()) {
            iter = ((Multimedia) coi2.getMultimedia()).getMultimediaItems().iterator();
	        while (iter.hasNext())
	            ((Multimedia) result.getMultimedia()).addMultimediaItem(((MultimediaItem) iter.next()).cloneMe());
        }
            
        /* examinationblocks */
        
        // all examinationblocks of former CaseObject are ignored
        // examinationblocks of generated CaseObject are those of latter CaseObject
        
        result.setExaminationBlocks(new ExaminationBlocks());
        if (coi2.getExaminationBlocks() != null) {
	        iter = ((ExaminationBlocks) coi2.getExaminationBlocks()).getAllBlocks().iterator();
	        while (iter.hasNext()) {
	            ExaminationBlock eb = (ExaminationBlock) iter.next();
	            IExaminationBlock add = coi2.getExaminationBlocks().addBlock();
	            Iterator citer = eb.getContents().iterator();
	            while (citer.hasNext())
	                add.addContent((QContainer) citer.next());
	            cloneSolutions(eb, add);
	        }
        }
        
        /* additional train data */
        
        // additional train data is ignored
        
        /* fus configuation */
        
        // fus configuration is ignored
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
     */
    public String getXMLCode() {
        StringBuffer sb = new StringBuffer();
        sb.append("<TemplateSession type=\"" + type.getName() + "\"");
        if (type == SINGLE) {
            sb.append(" case=\"" + getCaseObject().getId() + "\"/>\n");
        } else {
            sb.append(">\n");
            Iterator iter = listOfTemplateSessions.iterator();
            while (iter.hasNext())
                sb.append(((ITemplateSession) iter.next()).getXMLCode());
            sb.append("</TemplateSession>\n");
        }
        return sb.toString();
    }
    
    private void cloneSolutions(ISolutionContainer source, ISolutionContainer target) {

        Iterator iter = source.getSolutions().iterator();
        while (iter.hasNext()) {
            CaseObject.Solution cs = (CaseObject.Solution) iter.next();
            CaseObject.Solution add = new CaseObject.Solution();
            add.setDiagnosis(cs.getDiagnosis());
            add.setPSMethodClass(cs.getPSMethodClass());
            add.setWeight(cs.getWeight());
            add.setState(cs.getState());
            target.addSolution(add);
        }
        
    }

}
