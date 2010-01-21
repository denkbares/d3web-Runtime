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
 * Created on 24.02.2004
 */
package de.d3web.caserepository.addons.train;

import java.util.*;

import de.d3web.caserepository.*;
import de.d3web.caserepository.addons.*;
import de.d3web.caserepository.addons.fus.FUSConfiguration;
import de.d3web.config.Config;
import de.d3web.kernel.domainModel.*;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.supportknowledge.*;
import de.d3web.kernel.supportknowledge.Properties;

/**
 * CaseObjectTemplateSessionProxy (in ) de.d3web.caserepository
 * d3web-CaseRepository
 * 
 * @author hoernlein
 * @date 24.02.2004
 */
public class CaseObjectTemplateSessionProxy implements CaseObject {

	public CaseObjectTemplateSessionProxy(ITemplateSession templateSession) {
		setTemplateSession(templateSession);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getKnowledgeBase()
	 */
	public KnowledgeBase getKnowledgeBase() {
		return getTemplateSession().getCaseObject().getKnowledgeBase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getQuestions()
	 */
	public Set getQuestions() {
		return getTemplateSession().getCaseObject().getQuestions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#visibility(de.d3web.kernel.domainModel.QASet)
	 */
	public Boolean visibility(QASet item) {
		return getTemplateSession().getCaseObject().visibility(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getAnswers(de.d3web.kernel.domainModel.qasets.Question)
	 */
	public Collection getAnswers(Question question) {
		return getTemplateSession().getCaseObject().getAnswers(question);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getCorrectSystemDiagnoses()
	 */
	public Set getCorrectSystemDiagnoses() {
		return getTemplateSession().getCaseObject().getCorrectSystemDiagnoses();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getSystemDiagnoses()
	 */
	public Set getSystemDiagnoses() {
		return getTemplateSession().getCaseObject().getSystemDiagnoses();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getExaminationBlocks()
	 */
	public IExaminationBlocks getExaminationBlocks() {
		return getTemplateSession().getCaseObject().getExaminationBlocks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setExaminationBlocks(de.d3web.caserepository.addons.IExaminationBlocks)
	 */
	public void setExaminationBlocks(IExaminationBlocks eb) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getMultimedia()
	 */
	public IMultimedia getMultimedia() {
		return getTemplateSession().getCaseObject().getMultimedia();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setMultimedia(de.d3web.caserepository.addons.IMultimedia)
	 */
	public void setMultimedia(IMultimedia newMM) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getAppliedQSets()
	 */
	public IAppliedQSets getAppliedQSets() {
		return getTemplateSession().getCaseObject().getAppliedQSets();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setAppliedQSets(de.d3web.caserepository.addons.IAppliedQSets)
	 */
	public void setAppliedQSets(IAppliedQSets aq) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getContents()
	 */
	public IContents getContents() {
		return getTemplateSession().getCaseObject().getContents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setContents(de.d3web.caserepository.addons.IContents)
	 */
	public void setContents(IContents c) {
		throw new UnsupportedOperationException();
	}

	private IAdditionalTrainData atd = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getAdditionalTrainData()
	 */
	public IAdditionalTrainData getAdditionalTrainData() {
		if (atd == null)
			setAdditionalTrainData(new AdditionalTrainData());
		return atd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setAdditionalTrainData(de.d3web.caserepository.addons.IAdditionalTrainData)
	 */
	public void setAdditionalTrainData(IAdditionalTrainData atd) {
		this.atd = atd;
	}

	private IFUSConfiguration fc = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getFUSConfiguration()
	 */
	public IFUSConfiguration getFUSConfiguration() {
		if (fc == null)
			setFUSConfiguration(new FUSConfiguration());
		return fc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setFUSConfiguration(de.d3web.caserepository.addons.IFUSConfiguration)
	 */
	public void setFUSConfiguration(IFUSConfiguration fusc) {
		this.fc = fusc;
	}

	private ITemplateSession ts = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getTemplateSession()
	 */
	public ITemplateSession getTemplateSession() {
		return ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setTemplateSession(de.d3web.caserepository.addons.ITemplateSession)
	 */
	public void setTemplateSession(ITemplateSession ts) {
		this.ts = ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getXMLCode()
	 */
	public String getXMLCode() {
		return new CaseObjectWriter(this).getXMLCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getSolution(de.d3web.kernel.domainModel.Diagnosis,
	 *      java.lang.Class)
	 */
	public Solution getSolution(Diagnosis d, Class psMethodClass) {
		return getTemplateSession().getCaseObject().getSolution(d, psMethodClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions()
	 */
	public Set getSolutions() {
		return getTemplateSession().getCaseObject().getSolutions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions(java.lang.Class)
	 */
	public Set getSolutions(Class psMethodClass) {
		return getTemplateSession().getCaseObject().getSolutions(psMethodClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void addSolution(Solution s) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#removeSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void removeSolution(Solution s) {
		throw new UnsupportedOperationException();
	}

	private DCMarkup dc = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.DCMarkedUp#getDCMarkup()
	 */
	public DCMarkup getDCMarkup() {
		if (dc == null)
			return getTemplateSession().getCaseObject().getDCMarkup();
		else
			return dc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.DCMarkedUp#setDCDMarkup(de.d3web.kernel.supportknowledge.DCMarkup)
	 */
	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dc = dcMarkup;
	}

	private Properties props = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.PropertiesContainer#getProperties()
	 */
	public Properties getProperties() {
		if (props == null)
			return getTemplateSession().getCaseObject().getProperties();
		else
			return props;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.PropertiesContainer#setProperties(de.d3web.kernel.supportknowledge.Properties)
	 */
	public void setProperties(Properties properties) {
		this.props = properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.IDReference#getId()
	 */
	public String getId() {
		return getDCMarkup().getContent(DCElement.IDENTIFIER);
	}

	private Config config = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.config.ConfigContainer#getConfig()
	 */
	public Config getConfig() {
		if (config == null)
			setConfig(new Config(Config.TYPE_CASE, getId()));
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.config.ConfigContainer#setConfig(de.d3web.config.Config)
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getMultimediaSimpleQuestions()
	 */
	public ISimpleQuestions getMultimediaSimpleQuestions() {
		return getTemplateSession().getCaseObject().getMultimediaSimpleQuestions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setMultimediaSimpleQuestions(de.d3web.caserepository.addons.IMultimediaSimpleQuestions)
	 */
	public void setMultimediaSimpleQuestions(ISimpleQuestions mmsq) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getSimpleTextFUSs()
	 */
	public ISimpleTextFUSs getSimpleTextFUSs() {
		return getTemplateSession().getCaseObject().getSimpleTextFUSs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setSimpleTextFUSs(de.d3web.caserepository.addons.ISimpleTextFUSs)
	 */
	public void setSimpleTextFUSs(ISimpleTextFUSs stf) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#addAdditionalData(de.d3web.caserepository.CaseObject.AdditionalDataKey,
	 *      java.lang.Object)
	 */
	public void addAdditionalData(AdditionalDataKey key, Object data) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getAdditionalData(de.d3web.caserepository.CaseObject.AdditionalDataKey)
	 */
	public Object getAdditionalData(AdditionalDataKey key) {
		return getTemplateSession().getCaseObject().getAdditionalData(key);
	}

    /* (non-Javadoc)
     * @see de.d3web.caserepository.CaseObject#getTherapyConfiguration()
     */
    public ITherapyConfiguration getTherapyConfiguration() {
        return getTemplateSession().getCaseObject().getTherapyConfiguration();
    }

    /* (non-Javadoc)
     * @see de.d3web.caserepository.CaseObject#setTherapyConfiguration(de.d3web.caserepository.addons.ITherapyConfiguration)
     */
    public void setTherapyConfiguration(ITherapyConfiguration tc) {
        throw new UnsupportedOperationException();
    }

    public void addQuestionAndAnswers(Question question, Collection answers) {
        throw new UnsupportedOperationException();      
    }

}