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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.values.Choice;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.inference.PSMethodXCL;
import de.d3web.xcl.io.XCLModelPersistenceHandler;

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
		Solution  terminator = new Solution("D1");
		
		QuestionOC genre = new QuestionOC("GENRE");
		Choice action; 
		Choice love;
		
		QuestionOC player = new QuestionOC("PLAYER");
		Choice arnold;
		Choice will;
		
		QuestionOC rated = new QuestionOC("RATED");
		Choice plus18;
		Choice baby;

		
		
	/**
	 * rein funktionaler test, -> keine exceptions, passt 
	 * keinerlei semantische korrektur
	 * @throws IOException 
	 */
	public void testXMLWriter() throws IOException  {		
		XCLModelPersistenceHandler xmph=new XCLModelPersistenceHandler();
		xmph.write(k, new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		}, new DummyProgressListener());		
		
	}
	
	
	/**
	 * we're testing for the correct readin of an knowledgebase with an xclmodel via the xclmodelproperties
	 * 
	 * @throws Exception
	 */
	public void testPersistence()throws Exception{
		File file = new File("target/kbs");
		if (!file.isDirectory()) {
			file.mkdir();
		}
		PersistenceManager pm=PersistenceManager.getInstance();		
		File file2 = new File("target/kbs/test.jar");
		pm.save(k, file2);
		KnowledgeBase k2=pm.load(file2);
		Collection<KnowledgeSlice> col=k2.getAllKnowledgeSlicesFor(PSMethodXCL.class);
		for (Object current:col){
			if (current instanceof de.d3web.xcl.XCLModel){
				XCLModel model=(XCLModel) current;
				assertEquals(0.3,model.getMinSupport());
				assertEquals(0.7,model.getSuggestedThreshold());
				assertEquals(0.8,model.getEstablishedThreshold());
			}
		}
		
	}

		
		protected void setUp() throws Exception {
			super.setUp();
			try {
				InitPluginManager.init();
			}
			catch (IOException e1) {
				assertTrue("Error initialising plugin framework", false);
			}
			createKnowledgeBase();
		}

		private void createKnowledgeBase() {
			k=kbm.getKnowledgeBase();
			Solution rootdia=k.getRootDiagnosis();
			QContainer rootContainer = (QContainer) k.getRootQASet();
			
			// init diagnosis "Terminator"
			kbm.createDiagnosis("Terminator", rootdia );
			terminator=kbm.findDiagnosis("Terminator");			
			Properties p=new Properties();
			p.setProperty(Property.DIAGNOSIS_TYPE, new String("xclpattern"));
			k.setProperties(p);
			
			QContainer container=rootContainer;
			
			// the question "Genre"
			genre.setName("Genre");
			action = AnswerFactory.createAnswerChoice("Ga1", "action");
			love = AnswerFactory.createAnswerChoice("Ga2", "love");
			List<Choice> alternatives = new ArrayList<Choice>();
			alternatives.add(action);
			alternatives.add(love);
			genre.setAlternatives(alternatives);
			k.add(genre);
			container.addChild(genre);
			
			// the question "player"
			player.setName("Player");
			arnold = AnswerFactory.createAnswerChoice("Ga1", "arnold");
			will = AnswerFactory.createAnswerChoice("Ga2", "will");
			List<Choice> alt = new ArrayList<Choice>();
			alt.add(arnold);
			alt.add(will);
			player.setAlternatives(alt);
			k.add(player);
			container.addChild(player);
			
			// the question "rated"
			rated.setName("Rated");
			List<Choice> aPlayer = new ArrayList<Choice>();
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
