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

package de.d3web.persistence.tests;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeBaseManagement;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.psMethods.xclPattern.XCLModel;
import de.d3web.kernel.psMethods.xclPattern.XCLRelation;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.xml.BasicPersistenceHandler;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.XCLModelPersistenceHandler;

/**
 * Test to check persistence implementation of XCLModel
 * 
 * @author kazamatzuri
 *
 */
public class XCLModelTest extends TestCase{

		XCLModel model;
		KnowledgeBase k ;
		KnowledgeBaseManagement kbm=KnowledgeBaseManagement.createInstance();
		Diagnosis  terminator = new Diagnosis();
		
		QuestionOC genre = new QuestionOC();
		AnswerChoice action; 
		AnswerChoice love;
		
		QuestionOC player = new QuestionOC();
		AnswerChoice arnold;
		AnswerChoice will;
		
		QuestionOC rated = new QuestionOC();
		AnswerChoice plus18;
		AnswerChoice baby;

		
		
	/**
	 * rein funktionaler test, -> keine exceptions, passt 
	 * keinerlei semantische korrektur
	 */
	public void testXMLWriter()  {		
		BasicPersistenceHandler bph=new BasicPersistenceHandler();
		XCLModelPersistenceHandler xmph=new XCLModelPersistenceHandler();
		xmph.save(k);		
		
	}
	
	
	/**
	 * we're testing for the correct readin of an knowledgebase with an xclmodel via the xclmodelproperties
	 * 
	 * @throws Exception
	 */
	public void testPersistence()throws Exception{		
		XCLModelPersistenceHandler xmph=new XCLModelPersistenceHandler();
		PersistenceManager pm=PersistenceManager.getInstance();		
		pm.addPersistenceHandler(xmph);				
		// URL url= new URL("file:/tmp/test.jar");
		URL url= new URL("file://"+System.getProperty("java.io.tmpdir")+"/test.jar");
		pm.save(k,url);									
		KnowledgeBase k2=pm.load(url);
		Collection<KnowledgeSlice> col=k2.getAllKnowledgeSlicesFor(PSMethodXCL.class);
		for (Object current:col){
			if (current instanceof de.d3web.kernel.psMethods.xclPattern.XCLModel){
				XCLModel model=(XCLModel) current;
				assertEquals(0.3,model.getMinSupport());
				assertEquals(0.7,model.getSuggestedThreshold());
				assertEquals(0.8,model.getEstablishedThreshold());
			}
		}
		
	}

		
		protected void setUp() throws Exception {
			super.setUp();
			createKnowledgeBase();
			
			
		}

		private void createKnowledgeBase() {
			k=kbm.getKnowledgeBase();
			Diagnosis rootdia=k.getRootDiagnosis();
			QContainer rootContainer = (QContainer) k.getRootQASet();
			
			// init diagnosis "Terminator"
			kbm.createDiagnosis("Terminator", rootdia );
			terminator=kbm.findDiagnosis("Terminator");			
			Properties p=new Properties();
			p.setProperty(Property.DIAGNOSIS_TYPE, new String("xclpattern"));
			k.setProperties(p);
			
			QContainer container=rootContainer;
			
			// the question "Genre"
			genre.setId("GENRE");
			genre.setText("Genre");
			action = AnswerFactory.createAnswerChoice("Ga1", "action");
			love = AnswerFactory.createAnswerChoice("Ga2", "love");
			List<Answer> alternatives = new ArrayList<Answer>();
			alternatives.add(action);
			alternatives.add(love);
			genre.setAlternatives(alternatives);
			k.add(genre);
			container.addChild(genre);
			
			// the question "player"
			player.setId("PLAYER");
			player.setText("Player");
			arnold = AnswerFactory.createAnswerChoice("Ga1", "arnold");
			will = AnswerFactory.createAnswerChoice("Ga2", "will");
			List<Answer> alt = new ArrayList<Answer>();
			alt.add(arnold);
			alt.add(will);
			player.setAlternatives(alt);
			k.add(player);
			container.addChild(player);
			
			// the question "rated"
			rated.setId("RATED");
			rated.setText("Rated");
			List<Answer> aPlayer = new ArrayList<Answer>();
			plus18 = AnswerFactory.createAnswerChoice("Ra1", "18+");
			baby = AnswerFactory.createAnswerChoice("Ra2", "baby");
			aPlayer.add(plus18);
			aPlayer.add(baby);
			rated.setAlternatives(aPlayer);
			k.add(rated);
			container.addChild(rated);
			// XCL for Terminator
			//   Genre = action
			//   Player = Arnold
			//   Rated  = 18+	 weight 2
			model = new XCLModel(terminator);
			model.addRelation(XCLRelation.createXCLRelation("genre", genre, action));
			model.addRelation(XCLRelation.createXCLRelation(player, arnold));
			XCLRelation temprel=XCLRelation.createXCLRelation(rated, plus18);
			temprel.setWeight(2);
			model.addRelation(temprel);
			model.setMinSupport(0.3);
			model.setSuggestedThreshold(0.7);
			model.setEstablishedThreshold(0.8);
			terminator.addKnowledge(PSMethodXCL.class, model, XCLModel.XCLMODEL);			
			k.add(terminator);
			k.getRootDiagnosis().addChild(terminator);
		}





}
