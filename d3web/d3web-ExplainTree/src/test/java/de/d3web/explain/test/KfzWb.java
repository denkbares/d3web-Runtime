package de.d3web.explain.test;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.Div;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.Mult;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.abstraction.formula.Sub;
import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionMC;
import de.d3web.core.terminology.QuestionNum;
import de.d3web.core.terminology.QuestionOC;
import de.d3web.core.terminology.info.DCElement;
import de.d3web.core.terminology.info.DCMarkup;
import de.d3web.core.terminology.info.Num2ChoiceSchema;
import de.d3web.core.terminology.info.Property;
import de.d3web.core.utilities.Utils;
import de.d3web.indication.inference.PSMethodNextQASet;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;
public class KfzWb extends KnowledgeBase { /* Hier kommen die Fragen */

	private QContainer Q000 = new QContainer();
	private QContainer Q16 = new QContainer();
	private QContainer Qcl16 = new QContainer();
	private QContainer Q17 = new QContainer();
	private QContainer Q18 = new QContainer();
	private QContainer Q19 = new QContainer();
	private QContainer Q20 = new QContainer();
	private QContainer Q21 = new QContainer();
	private QContainer Q56 = new QContainer();

	private Answer Mf2a1 = AnswerFactory.createAnswerChoice("Mf2a1", "schwarz");
	private Answer Mf2a2 = AnswerFactory.createAnswerChoice("Mf2a2", "bläulich");
	private Answer Mf2a3 = AnswerFactory.createAnswerChoice("Mf2a3", "farblos");
	private QuestionOC Mf2 = new QuestionOC();

	private Answer Mf3a1 = AnswerFactory.createAnswerChoice("Mf3a1", "braun");
	private Answer Mf3a2 = AnswerFactory.createAnswerChoice("Mf3a2", "grau");
	private Answer Mf3a3 = AnswerFactory.createAnswerChoice("Mf3a3", "hellgrau");
	private Answer Mf3a4 = AnswerFactory.createAnswerChoice("Mf3a4", "schwarzverrußt");
	private QuestionOC Mf3 = new QuestionOC();

	private Answer Msi21a1 = AnswerFactory.createAnswerChoice("Msi21a1", "normal");
	private Answer Msi21a2 = AnswerFactory.createAnswerChoice("Msi21a2", "abnorm");
	private QuestionOC Msi21 = new QuestionOC();

	private Answer Mf4a1 = AnswerFactory.createAnswerChoice("Mf4a1", "Diesel");
	private Answer Mf4a3 = AnswerFactory.createAnswerChoice("Mf4a3", "Normal oder Super");
	private QuestionOC Mf4 = new QuestionOC();

	private QuestionNum Mf5 = new QuestionNum();

	private Answer Msi4a1 = AnswerFactory.createAnswerChoice("Msi4a1", "normal");
	private Answer Msi4a2 = AnswerFactory.createAnswerChoice("Msi4a2", "leicht erhöht");
	private Answer Msi4a3 = AnswerFactory.createAnswerChoice("Msi4a3", "erhöht");
	private QuestionOC Msi4 = new QuestionOC();

	private QuestionNum Mf6 = new QuestionNum();

	private Answer Mf7a1 = AnswerFactory.createAnswerChoice("Mf7a1", "klopfen");
	private Answer Mf7a2 = AnswerFactory.createAnswerChoice("Mf7a2", "klingeln");
	private Answer Mf7a0 = AnswerFactory.createAnswerNo("Mf7a0", "Nein/Sonstiges");
	private QuestionMC Mf7 = new QuestionMC();

	private Answer Mf8a1 = AnswerFactory.createAnswerChoice("Mf8a1", "springt normal an");
	private Answer Mf8a2 = AnswerFactory.createAnswerChoice("Mf8a2", "springt schlecht an");
	private Answer Mf8a3 = AnswerFactory.createAnswerChoice("Mf8a3", "springt überhaupt nicht an");
	private QuestionOC Mf8 = new QuestionOC();

	private Answer Mf10a1 = AnswerFactory.createAnswerChoice("Mf10a1", "dreht durch");
	private Answer Mf10a2 = AnswerFactory.createAnswerChoice("Mf10a2", "dreht nicht richtig durch");
	private QuestionOC Mf10 = new QuestionOC();

	private Answer Mf9a1 = AnswerFactory.createAnswerChoice("Mf9a1", "verzögertes Anfahren");
	private Answer Mf9a2 = AnswerFactory.createAnswerChoice("Mf9a2", "schlechte Beschleunigung");
	private Answer Mf9a3 = AnswerFactory.createAnswerChoice("Mf9a3", "Leerlauf ist zu niedrig");
	private Answer Mf9a4 = AnswerFactory.createAnswerChoice("Mf9a4", "Leerlauf ist unregelmäßig");
	private Answer Mf9a5 = AnswerFactory.createAnswerChoice("Mf9a5", "zu wenig Leistung bei Teillast");
	private Answer Mf9a6 = AnswerFactory.createAnswerChoice("Mf9a6", "zu wenig Leistung bei Volllast");
	private Answer Mf9a0 = AnswerFactory.createAnswerNo("Mf9a0", "Nein/Sonstiges");
	private QuestionMC Mf9 = new QuestionMC();

	private Answer Mf13a1 = AnswerFactory.createAnswerChoice("Mf13a1", "ja");
	private Answer Mf13a0 = AnswerFactory.createAnswerChoice("Mf13a0", "nein");
	private QuestionOC Mf13 = new QuestionOC();

	private Answer Mf15a1 = AnswerFactory.createAnswerChoice("Mf15a1", "ja");
	private Answer Mf15a0 = AnswerFactory.createAnswerChoice("Mf15a0", "nein");
	private QuestionOC Mf15 = new QuestionOC();

	private Answer Mf17a1 = AnswerFactory.createAnswerChoice("Mf17a1", "ja");
	private Answer Mf17a0 = AnswerFactory.createAnswerChoice("Mf17a0", "nein");
	private QuestionOC Mf17 = new QuestionOC();

	private Answer Mf19a1 = AnswerFactory.createAnswerChoice("Mf19a1", "ja");
	private Answer Mf19a0 = AnswerFactory.createAnswerChoice("Mf19a0", "nein");
	private QuestionOC Mf19 = new QuestionOC();

	private Answer Mf11a1 = AnswerFactory.createAnswerChoice("Mf11a1", "ja");
	private Answer Mf11a0 = AnswerFactory.createAnswerChoice("Mf11a0", "nein");
	private QuestionOC Mf11 = new QuestionOC();

	private Answer Mf57a1 = AnswerFactory.createAnswerChoice("Mf57a1", "VW");
	private Answer Mf57a2 = AnswerFactory.createAnswerChoice("Mf57a2", "Opel");
	private Answer Mf57a3 = AnswerFactory.createAnswerChoice("Mf57a3", "Mercedes Benz");
	private Answer Mf57a4 = AnswerFactory.createAnswerChoice("Mf57a4", "BMW");
	private Answer Mf57a5 = AnswerFactory.createAnswerChoice("Mf57a5", "Porsche");
	private Answer Mf57a6 = AnswerFactory.createAnswerChoice("Mf57a6", "Fiat");
	private Answer Mf57a7 = AnswerFactory.createAnswerChoice("Mf57a7", "Toyota");
	private Answer Mf57a8 = AnswerFactory.createAnswerChoice("Mf57a8", "Mazda");
	private Answer Mf57a9 = AnswerFactory.createAnswerChoice("Mf57a9", "sonstige");
	private QuestionOC Mf57 = new QuestionOC();

	private QuestionNum Mf58 = new QuestionNum();

	/* Diagnoses ...*/

	private Diagnosis P000 = new Diagnosis();
	private Diagnosis P8 = new Diagnosis();
	private Diagnosis P13 = new Diagnosis();
	private Diagnosis P14 = new Diagnosis();
	private Diagnosis P15 = new Diagnosis();
	private Diagnosis P16 = new Diagnosis();

	private void setProperties0() {
		try {
			
			Q16.setPriority(new Integer(2));
			Qcl16.setPriority(new Integer(3));
			Q56.setPriority(new Integer(1));

			setCostVerbalization("Preis", "Kosten");
			Q16.getProperties().setProperty(Property.COST, new Double(16.0));
			Qcl16.getProperties().setProperty(Property.COST, new Double(16.1));
			Q17.getProperties().setProperty(Property.COST, new Double(17.0));
			Q18.getProperties().setProperty(Property.COST, new Double(18.0));
			Q19.getProperties().setProperty(Property.COST, new Double(19.0));
			Q20.getProperties().setProperty(Property.COST, new Double(20.0));
			Q21.getProperties().setProperty(Property.COST, new Double(21.0));
			Q56.getProperties().setProperty(Property.COST, new Double(56.0));

			setCostVerbalization("Risiko", "Risiko");
			Q16.getProperties().setProperty(Property.RISK, new Double(1.1));
			Qcl16.getProperties().setProperty(Property.RISK, new Double(1.2));
			Q17.getProperties().setProperty(Property.RISK, new Double(1.3));
			Q18.getProperties().setProperty(Property.RISK, new Double(1.4));
			Q19.getProperties().setProperty(Property.RISK, new Double(1.5));
			Q20.getProperties().setProperty(Property.RISK, new Double(1.6));
			Q21.getProperties().setProperty(Property.RISK, new Double(1.7));
			// keine Kosten: sonst Q56.setCosts("Risiko", new Double(56.0));

			Q000.setId("Q000");
			Q000.setText("Fragebögen");
            Q000.setKnowledgeBase(this);
			Q56.setId("Q56");
			Q56.setText("Allgemeines");
            Q56.setKnowledgeBase(this);
			Q56.setParents(Utils.createList(new NamedObject[] { Q000 }));
			Q16.setId("Q16");
			Q16.setText("Beobachtungen");
            Q16.setKnowledgeBase(this);
			Q16.setParents(Utils.createList(new NamedObject[] { Q000 }));
			Mf2.setId("Mf2");
			Mf2.setText("SI: Abgase");
            Mf2.setKnowledgeBase(this);
			Mf2.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf2.setAlternatives(Utils.createVector(new Object[] { Mf2a1, Mf2a2, Mf2a3 }));
			Mf3.setId("Mf3");
			Mf3.setText("SI: Auspuffrohrfarbe");
            Mf3.setKnowledgeBase(this);
			Mf3.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf3.setAlternatives(Utils.createVector(new Object[] { Mf3a1, Mf3a2, Mf3a3, Mf3a4 }));
			Msi21.setId("Msi21");
			Msi21.setText("SI: Bewertung Auspuffrohrfarbe");
            Msi21.setKnowledgeBase(this);
			Msi21.setAlternatives(Utils.createVector(new Object[] { Msi21a1, Msi21a2 }));
			Mf4.setId("Mf4");
			Mf4.setText("SI: Benzinart");
            Mf4.setKnowledgeBase(this);
			Mf4.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf4.setAlternatives(Utils.createVector(new Object[] { Mf4a1, Mf4a3 }));
			Mf5.setId("Mf5");
			Mf5.setText("SI: Üblicher Kraftstoffverbrauch/100km");
            Mf5.setKnowledgeBase(this);
			Mf5.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Msi4.setId("Msi4");
			Msi4.setText("SI: Bewertung Kraftstoffverbrauch");
            Msi4.setKnowledgeBase(this);
			Msi4.setAlternatives(Utils.createVector(new Object[] { Msi4a1, Msi4a2, Msi4a3 }));
			Num2ChoiceSchema schema = new Num2ChoiceSchema("Msi4_Schema00");
			schema.setSchemaArray(new Double[] { new Double(10), new Double(20)});
			Msi4.addKnowledge(PSMethodQuestionSetter.class, schema, PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);

			
			
			Mf6.setId("Mf6");
			Mf6.setText("SI: Tatsächlicher Kraftstoffverbrauch/100km");
            Mf6.setKnowledgeBase(this);
			Mf6.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf7.setId("Mf7");
			Mf7.setText("SI: Motorgeräusche");
            Mf7.setKnowledgeBase(this);
			Mf7.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf7.setAlternatives(Utils.createVector(new Object[] { Mf7a1, Mf7a2, Mf7a0 }));
			Mf8.setId("Mf8");
			Mf8.setText("SI: Verhalten bei Motorstart");
            Mf8.setKnowledgeBase(this);
			Mf8.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf8.setAlternatives(Utils.createVector(new Object[] { Mf8a1, Mf8a2, Mf8a3 }));
			Mf10.setId("Mf10");
			Mf10.setText("Wie verhält sich der Anlasser bei Motorstartversuchen?");
            Mf10.setKnowledgeBase(this);
			Mf10.setAlternatives(Utils.createVector(new Object[] { Mf10a1, Mf10a2 }));
			Mf9.setId("Mf9");
			Mf9.setText("SI: Fahrverhalten");
            Mf9.setKnowledgeBase(this);
			Mf9.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf9.setAlternatives(Utils.createVector(new Object[] { Mf9a1, Mf9a2, Mf9a3, Mf9a4, Mf9a5, Mf9a6, Mf9a0 }));

			Qcl16.setId("Qcl16");
			Qcl16.setText("Technische Untersuchungen");
            Qcl16.setKnowledgeBase(this);
			Qcl16.setParents(Utils.createList(new NamedObject[] { Q000 }));
			Q17.setId("Q17");
			Q17.setText("Untersuchung Leerlaufsystem");
            Q17.setKnowledgeBase(this);
			Q17.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q18.setId("Q18");
			Q18.setText("Untersuchung Ansaugsystem");
            Q18.setKnowledgeBase(this);
			Q18.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q19.setId("Q19");
			Q19.setText("Untersuchung Luftfiltereinsatz");
            Q19.setKnowledgeBase(this);
			Q19.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q20.setId("Q20");
			Q20.setText("Untersuchung Zündeinstellung");
            Q20.setKnowledgeBase(this);
			Q20.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q21.setId("Q21");
			Q21.setText("Untersuchung Batterie");
            Q21.setKnowledgeBase(this);
			Q21.setParents(Utils.createList(new NamedObject[] { Qcl16 }));

			Mf13.setId("Mf13");
			Mf13.setText("Ist ein Fehler im Leerlaufsystem?");
            Mf13.setKnowledgeBase(this);
			Mf13.setParents(Utils.createList(new NamedObject[] { Q17 }));
			Mf13.setAlternatives(Utils.createVector(new Object[] { Mf13a1, Mf13a0 }));
			Mf15.setId("Mf15");
			Mf15.setText("Ist ein Fehler im Ansaugsystem?");
            Mf15.setKnowledgeBase(this);
			Mf15.setParents(Utils.createList(new NamedObject[] { Q18 }));
			Mf15.setAlternatives(Utils.createVector(new Object[] { Mf15a1, Mf15a0 }));
			Mf17.setId("Mf17");
			Mf17.setText("Ist der Luftfilter verschmutzt?");
            Mf17.setKnowledgeBase(this);
			Mf17.setParents(Utils.createList(new NamedObject[] { Q19 }));
			Mf17.setAlternatives(Utils.createVector(new Object[] { Mf17a1, Mf17a0 }));

		} catch (Exception e) {
			System.out.println("" + e);
		}

	}

	private void setProperties1() {
		try {
			Mf19.setId("Mf19");
			Mf19.setText("Ist die Zündeinstellung fehlerhaft?");
            Mf19.setKnowledgeBase(this);
			Mf19.setParents(Utils.createList(new NamedObject[] { Q20 }));
			Mf19.setAlternatives(Utils.createVector(new Object[] { Mf19a1, Mf19a0 }));
			Mf11.setId("Mf11");
			Mf11.setText("Ist die Batterie leer?");
            Mf11.setKnowledgeBase(this);
			Mf11.setParents(Utils.createList(new NamedObject[] { Q21 }));
			Mf11.setAlternatives(Utils.createVector(new Object[] { Mf11a1, Mf11a0 }));
			Mf57.setId("Mf57");
			Mf57.setText("Automarke?");
            Mf57.setKnowledgeBase(this);
			Mf57.setParents(Utils.createList(new NamedObject[] { Q56 }));
			Mf57.setAlternatives(
				Utils.createVector(
					new Object[] { Mf57a1, Mf57a2, Mf57a3, Mf57a4, Mf57a5, Mf57a6, Mf57a7, Mf57a8, Mf57a9 }));
			Mf58.setId("Mf58");
			Mf58.setText("Baujahr des Autos?");
            Mf58.setKnowledgeBase(this);
			Mf58.setParents(Utils.createList(new NamedObject[] { Q56 }));
			P000.setId("P000");
			P000.setText("Klassifikation");
            P000.setKnowledgeBase(this);
			P8.setId("P8");
			P8.setText("Leerlaufsystem defekt");
            P8.setKnowledgeBase(this);
			P8.setParents(Arrays.asList(new NamedObject[] { P000 }));
			P13.setId("P13");
			P13.setText("Ansaugsystem undicht");
            P13.setKnowledgeBase(this);
			P13.setParents(Arrays.asList(new NamedObject[] { P000 }));

			P14.setId("P14");
			P14.setAprioriProbability(Score.P2);
            P14.setKnowledgeBase(this);
			P14.setText("Luftfiltereinsatz verschmutzt");
			P14.setParents(Arrays.asList(new NamedObject[] { P000 }));

			P15.setId("P15");
			P15.setText("Zündeinstellung falsch");
            P15.setKnowledgeBase(this);
			P15.setParents(Arrays.asList(new NamedObject[] { P000 }));

			P16.setId("P16");
			P16.setText("Batterie leer");
            P16.setKnowledgeBase(this);
			P16.setParents(Arrays.asList(new NamedObject[] { P000 }));

		} catch (Exception e) {
			System.out.println("" + e);
		}

	}

	private void setProperties() {
		try {
			setProperties0();
			setProperties1();
		} catch (Exception e) {
			System.out.println("" + e);
		}

	}

	private void addRules0() {

		/* Die unkonvertierten Regeln*/

		/* Die Folgefrage-Regeln */

		/* (RASK10) Und (($Or Mf8 2 3)) -> Mf10 */
		// RuleComplex RASK10 =
			RuleFactory.createIndicationRule(
				"RASK10",
				Utils.createList(new QASet[] { Mf10 }),
				new CondOr(Utils.createList(new AbstractCondition[] { new CondEqual(Mf8, Mf8a2), new CondEqual(Mf8, Mf8a3)})));

		/* Next von P000: rqsug14766
		*/
		// RuleComplex rqsug14766 =
			RuleFactory.createClarificationRule("rqsug14766", Utils.createList(new QASet[] {
		}), P000, new CondDState(P000, DiagnosisState.SUGGESTED));

		/* Frageklassen nach Etablierung von P000: Nil*/
		// RuleComplex rqetab14767 =
			RuleFactory.createRefinementRule("rqetab14767", Utils.createList(
					new QASet[] {}), P000, new CondDState(P000, DiagnosisState.ESTABLISHED));

		/* Next von P8: rqsug14769
		*/
		// RuleComplex rqsug14769 =
			RuleFactory.createClarificationRule(
				"rqsug14769",
				Utils.createList(new QASet[] { Q17 }),
				P8,
				new CondDState(P8, DiagnosisState.SUGGESTED));

		/* Frageklassen nach Etablierung von P8: (Q17)*/
		// RuleComplex rqetab14770 =
			RuleFactory.createRefinementRule(
				"rqetab14770",
				Utils.createList(new QASet[] { Q17 }),
				P8,
				new CondDState(P8, DiagnosisState.ESTABLISHED));

		/* Next von P13: rqsug14772
		*/
		// RuleComplex rqsug14772 =
			RuleFactory.createClarificationRule(
				"rqsug14772",
				Utils.createList(new QASet[] { Q18 }),
				P13,
				new CondDState(P13, DiagnosisState.SUGGESTED));

		/* Frageklassen nach Etablierung von P13: (Q18)*/
		// RuleComplex rqetab14773 =
			RuleFactory.createRefinementRule(
				"rqetab14773",
				Utils.createList(new QASet[] { Q18 }),
				P13,
				new CondDState(P13, DiagnosisState.ESTABLISHED));

		/* Next von P14: rqsug14775
		*/
		// RuleComplex rqsug14775 =
			RuleFactory.createClarificationRule(
				"rqsug14775",
				Utils.createList(new QASet[] { Q19 }),
				P14,
				new CondDState(P14, DiagnosisState.SUGGESTED));

		/* Frageklassen nach Etablierung von P14: (Q19)*/
		// RuleComplex rqetab14776 =
			RuleFactory.createRefinementRule(
				"rqetab14776",
				Utils.createList(new QASet[] { Q19 }),
				P14,
				new CondDState(P14, DiagnosisState.ESTABLISHED));

		/* Next von P15: rqsug14778
		*/
		// RuleComplex rqsug14778 =
			RuleFactory.createClarificationRule(
				"rqsug14778",
				Utils.createList(new QASet[] { Q20 }),
				P15,
				new CondDState(P15, DiagnosisState.SUGGESTED));

		/* Frageklassen nach Etablierung von P15: (Q20)*/
		// RuleComplex rqetab14779 =
			RuleFactory.createRefinementRule(
				"rqetab14779",
				Utils.createList(new QASet[] { Q20 }),
				P15,
				new CondDState(P15, DiagnosisState.ESTABLISHED));

		/* Next von P16: rqsug14781
		*/
		// RuleComplex rqsug14781 =
			RuleFactory.createClarificationRule(
				"rqsug14781",
				Utils.createList(new QASet[] { Q21 }),
				P16,
				new CondDState(P16, DiagnosisState.SUGGESTED));

		/* Frageklassen nach Etablierung von P16: (Q21)*/
		// RuleComplex rqetab14782 =
			RuleFactory.createRefinementRule(
				"rqetab14782",
				Utils.createList(new QASet[] { Q21 }),
				P16,
				new CondDState(P16, DiagnosisState.ESTABLISHED));

		/* Die Diagnose-Regeln */

		/*(R_FB_57) Und (($= Mf10 1)) -> P16, N4
		*/
		// RuleComplex R_FB_57 =
			RuleFactory.createHeuristicPSRule("R_FB_57", P16, Score.N4, new CondEqual(Mf10, Mf10a1));

		/*(R_FB_56) Und (($Or Mf8 3 2)) -> P16, P5
		*/
		// RuleComplex R_FB_56 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_56",
				P16,
				Score.P5,
				new CondOr(Utils.createList(new AbstractCondition[] { new CondEqual(Mf8, Mf8a3), new CondEqual(Mf8, Mf8a2)})));

		/*(R_FB_29) Und (($= Mf9 4)) -> P14, P4
		*/
		// RuleComplex R_FB_29 =
			RuleFactory.createHeuristicPSRule("R_FB_29", P14, Score.P4, new CondEqual(Mf9, Mf9a4));

		/*(R_FB_28) Und (($= Msi4 1)) -> P13, N4
		*/
		// RuleComplex R_FB_28 =
			RuleFactory.createHeuristicPSRule("R_FB_28", P13, Score.N4, new CondEqual(Msi4, Msi4a1));

		/*(R_FB_27) Und (($= Mf9 5)) -> P13, P3
		*/
		// RuleComplex R_FB_27 =
			RuleFactory.createHeuristicPSRule("R_FB_27", P13, Score.P3, new CondEqual(Mf9, Mf9a5));

		/*(R_FB_26) Und (($= Mf9 4)) -> P13, P4
		*/
		// RuleComplex R_FB_26 =
			RuleFactory.createHeuristicPSRule("R_FB_26", P13, Score.P4, new CondEqual(Mf9, Mf9a4));

		/*(R_FB_25) Und (($= Msi4 2)) -> P13, P3
		*/
		// RuleComplex R_FB_25 =
			RuleFactory.createHeuristicPSRule("R_FB_25", P13, Score.P3, new CondEqual(Msi4, Msi4a2));

		/*(R_FB_24) Und (($= Msi4 3)) -> P13, P4
		*/
		// RuleComplex R_FB_24 =
			RuleFactory.createHeuristicPSRule("R_FB_24", P13, Score.P4, new CondEqual(Msi4, Msi4a3));

		/*(R_FB_22) Und ((Non $= Mf10 1)) -> P14, N6
		*/
		// RuleComplex R_FB_22 =
			RuleFactory.createHeuristicPSRule("R_FB_22", P14, Score.N6, new CondNot(new CondEqual(Mf10, Mf10a1)));

		/*(R_FB_21) Und ((Non $Or Mf8 3 2)) -> P14, N4
		*/
		// RuleComplex R_FB_21 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_21",
				P14,
				Score.N4,
				new CondNot(
					new CondOr(
						Utils.createList(new AbstractCondition[] { new CondEqual(Mf8, Mf8a3), new CondEqual(Mf8, Mf8a2)}))));

		/*(R_FB_20) Und (($Or Mf8 3 2) ($= Mf10 1)) -> P14, P4
		*/
		// RuleComplex R_FB_20 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_20",
				P14,
				Score.P4,
				new CondAnd(
					Utils.createList(
						new AbstractCondition[] {
							new CondOr(
								Utils.createList(new AbstractCondition[] { new CondEqual(Mf8, Mf8a3), new CondEqual(Mf8, Mf8a2)})),
							new CondEqual(Mf10, Mf10a1)
		})));

		/*(R_FB_18) Und (($= Msi4 1)) -> P14, N4
		*/
		// RuleComplex R_FB_18 =
			RuleFactory.createHeuristicPSRule("R_FB_18", P14, Score.N4, new CondEqual(Msi4, Msi4a1));

		/*(R_FB_17) Und (($= Msi4 2)) -> P14, P3
		*/
		// RuleComplex R_FB_17 =
			RuleFactory.createHeuristicPSRule("R_FB_17", P14, Score.P3, new CondEqual(Msi4, Msi4a2));

		/*(R_FB_16) Und (($= Msi4 3)) -> P14, P4
		*/
		// RuleComplex R_FB_16 =
			RuleFactory.createHeuristicPSRule("R_FB_16", P14, Score.P4, new CondEqual(Msi4, Msi4a3));

		/*(R_FB_15) Und (($= Msi21 2)) -> P14, P5
		*/
		// RuleComplex R_FB_15 =
			RuleFactory.createHeuristicPSRule("R_FB_15", P14, Score.P5, new CondEqual(Msi21, Msi21a2));

		/*(R_FB_14) Und (($= Mf2 1)) -> P14, P5
		*/
		// RuleComplex R_FB_14 =
			RuleFactory.createHeuristicPSRule("R_FB_14", P14, Score.P5, new CondEqual(Mf2, Mf2a1));

		/*(R_FB_13) Und ((Non $Or Mf8 3 2)) -> P15, N5
		*/
		// RuleComplex R_FB_13 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_13",
				P15,
				Score.N5,
				new CondNot(
					new CondOr(
						Utils.createList(new AbstractCondition[] { new CondEqual(Mf8, Mf8a3), new CondEqual(Mf8, Mf8a2)}))));

		/*(R_FB_12) Und ((Non $Or Mf7 2 1)) -> P15, N3
		*/
		// RuleComplex R_FB_12 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_12",
				P15,
				Score.N3,
				new CondNot(
					new CondOr(
						Utils.createList(new AbstractCondition[] { new CondEqual(Mf7, Mf7a2), new CondEqual(Mf7, Mf7a1)}))));

		/*(R_FB_11) Und ((Non $= Mf10 1)) -> P15, N6
		*/
		// RuleComplex R_FB_11 =
			RuleFactory.createHeuristicPSRule("R_FB_11", P15, Score.N6, new CondNot(new CondEqual(Mf10, Mf10a1)));

		/*(R_FB_10) Und (($Or Mf8 3 2) ($= Mf10 1)) -> P15, P5
		*/
		// RuleComplex R_FB_10 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_10",
				P15,
				Score.P5,
				new CondAnd(
					Utils.createList(
						new AbstractCondition[] {
							new CondOr(
								Utils.createList(new AbstractCondition[] { new CondEqual(Mf8, Mf8a3), new CondEqual(Mf8, Mf8a2)})),
							new CondEqual(Mf10, Mf10a1)
		})));

		/*(R_FB_9) Und (($Or Mf7 2 1)) -> P15, P5
		*/
		// RuleComplex R_FB_9 =
			RuleFactory.createHeuristicPSRule(
				"R_FB_9",
				P15,
				Score.P5,
				new CondOr(Utils.createList(new AbstractCondition[] { new CondEqual(Mf7, Mf7a2), new CondEqual(Mf7, Mf7a1)})));

		/*(R_FB_8) Und (($= Mf9 4)) -> P15, P3
		*/
		// RuleComplex R_FB_8 =
			RuleFactory.createHeuristicPSRule("R_FB_8", P15, Score.P3, new CondEqual(Mf9, Mf9a4));

		/*(R_FB_7) Und (($= Mf9 1)) -> P15, P3
		*/
		// RuleComplex R_FB_7 =
			RuleFactory.createHeuristicPSRule("R_FB_7", P15, Score.P3, new CondEqual(Mf9, Mf9a1));

		/*(R_FB_6) Und ((Non $= Mf9 3)) -> P8, N3
		*/
		// RuleComplex R_FB_6 =
			RuleFactory.createHeuristicPSRule("R_FB_6", P8, Score.N3, new CondNot(new CondEqual(Mf9, Mf9a3)));

		/*(R_FB_5) Und ((Non $= Mf9 4)) -> P8, N4
		*/
		// RuleComplex R_FB_5 =
			RuleFactory.createHeuristicPSRule("R_FB_5", P8, Score.N4, new CondNot(new CondEqual(Mf9, Mf9a4)));

		/*(R_FB_4) Und (($= Mf8 2)) -> P8, P5
		*/
		// RuleComplex R_FB_4 =
			RuleFactory.createHeuristicPSRule("R_FB_4", P8, Score.P5, new CondEqual(Mf8, Mf8a2));

		/*(R_FB_3) Und (($= Mf9 3)) -> P8, P4
		*/
		// RuleComplex R_FB_3 =
			RuleFactory.createHeuristicPSRule("R_FB_3", P8, Score.P4, new CondEqual(Mf9, Mf9a3));

		/*(R_FB_2) Und (($= Mf9 4)) -> P8, P5
		*/
		// RuleComplex R_FB_2 =
			RuleFactory.createHeuristicPSRule("R_FB_2", P8, Score.P5, new CondEqual(Mf9, Mf9a4));

		/*(RFB21) Und (($Isvalue Mf19 True)) -> P15, P7
		*/
		// RuleComplex RFB21 =
			RuleFactory.createHeuristicPSRule("RFB21", P15, Score.P7, new CondEqual(Mf19, Mf19a1));

		/*(RFB20) Und (($Isvalue Mf19 False)) -> P15, N7
		*/
		// RuleComplex RFB20 =
			RuleFactory.createHeuristicPSRule("RFB20", P15, Score.N7, new CondEqual(Mf19, Mf19a0));

		/*(RFB19) Und (($Isvalue Mf17 True)) -> P14, P7
		*/
		// RuleComplex RFB19 =
			RuleFactory.createHeuristicPSRule("RFB19", P14, Score.P7, new CondEqual(Mf17, Mf17a1));

		/*(RFB18) Und (($Isvalue Mf17 False)) -> P14, N7
		*/
		// RuleComplex RFB18 =
			RuleFactory.createHeuristicPSRule("RFB18", P14, Score.N7, new CondEqual(Mf17, Mf17a0));

		/*(RFB17) Und (($Isvalue Mf15 True)) -> P13, P7
		*/
		// RuleComplex RFB17 =
			RuleFactory.createHeuristicPSRule("RFB17", P13, Score.P7, new CondEqual(Mf15, Mf15a1));

		/*(RFB16) Und (($Isvalue Mf15 False)) -> P13, N7
		*/
		// RuleComplex RFB16 =
			RuleFactory.createHeuristicPSRule("RFB16", P13, Score.N7, new CondEqual(Mf15, Mf15a0));

		/*(RFB15) Und (($Isvalue Mf13 True)) -> P8, P7
		*/
		// RuleComplex RFB15 =
			RuleFactory.createHeuristicPSRule("RFB15", P8, Score.P7, new CondEqual(Mf13, Mf13a1));

		/*(RFB14) Und (($Isvalue Mf13 False)) -> P8, N7
		*/
		// RuleComplex RFB14 =
			RuleFactory.createHeuristicPSRule("RFB14", P8, Score.N7, new CondEqual(Mf13, Mf13a0));

		/*(RFB13) Und (($Isvalue Mf11 True)) -> P16, P7
		*/
		// RuleComplex RFB13 =
			RuleFactory.createHeuristicPSRule("RFB13", P16, Score.P7, new CondEqual(Mf11, Mf11a1));

		/*(RFB12) Und (($Isvalue Mf11 False)) -> P16, N7
		*/
		// RuleComplex RFB12 =
			RuleFactory.createHeuristicPSRule("RFB12", P16, Score.N7, new CondEqual(Mf11, Mf11a0));

		/*(RFB11) Und (($= Mf10 2)) -> P16, P5
		*/
		// RuleComplex RFB11 =
			RuleFactory.createHeuristicPSRule("RFB11", P16, Score.P5, new CondEqual(Mf10, Mf10a2));

	}

	private void addRules1() {

		// Die Symptom-Interpretations-Regeln 

		// (Radd59) Und (($= Mf4 3) ($= Mf3 4)) -> Msi21
		Rule Radd59 = new Rule("Radd59");
		ActionSetValue a_Radd59 = new ActionSetValue();
		a_Radd59.setRule(Radd59);
		a_Radd59.setQuestion(Msi21);
		a_Radd59.setValues(new Object[] { Msi21a2 });
		Radd59.setAction(a_Radd59);
		Radd59.setCondition(
			new CondAnd(Utils.createList(new AbstractCondition[] { new CondEqual(Mf4, Mf4a3), new CondEqual(Mf3, Mf3a4)})));

		// (Radd4) Und (($Or Mf3 1 2 3)) -> Msi21
		Rule Radd4 = new Rule("Radd4");
		ActionSetValue a_Radd4 = new ActionSetValue();
		a_Radd4.setRule(Radd4);
		a_Radd4.setQuestion(Msi21);
		a_Radd4.setValues(new Object[] { Msi21a1 });
		Radd4.setAction(a_Radd4);
		Radd4.setCondition(
			new CondOr(
				Utils.createList(
					new AbstractCondition[] { new CondEqual(Mf3, Mf3a1), new CondEqual(Mf3, Mf3a2), new CondEqual(Mf3, Mf3a3)})));

		// (Radd2) Und (($= Mf3 4) ($= Mf4 1)) -> Msi21 
		Rule Radd2 = new Rule("Radd2");
		ActionSetValue a_Radd2 = new ActionSetValue();
		a_Radd2.setRule(Radd2);
		a_Radd2.setQuestion(Msi21);
		a_Radd2.setValues(new Object[] { Msi21a1 });
		Radd2.setAction(a_Radd2);
		Radd2.setCondition(
			new CondAnd(Utils.createList(new AbstractCondition[] { new CondEqual(Mf3, Mf3a4), new CondEqual(Mf4, Mf4a1)})));

		/* Die Symptom-Interpretations-Regeln (Numerische) */

		/* (Rdq4) Und (($> Mf5 0) ($Isvalue Mf6 True)) -> Msi4 */
		Rule Rdq4 = new Rule("Rdq4");
		ActionSetValue a_Rdq4 = new ActionSetValue();
		a_Rdq4.setRule(Rdq4);
		a_Rdq4.setQuestion(Msi4);

		FormulaExpression formula =
			new FormulaExpression(
				Msi4,
				new Mult(
					new Div(new Sub(new QNumWrapper(Mf6), new QNumWrapper(Mf5)), new QNumWrapper(Mf5)),
					new FormulaNumber(new Double(100))));

		a_Rdq4.setValues(new Object[] { formula
			//			 new Schema(
			//				new Mult(new Div(new Sub(new QNumWrapper(Mf6), new QNumWrapper(Mf5)), new QNumWrapper(Mf5)), new FormulaNumber(new Double(100))),
			//				((QuestionChoice) Msi4).getAllAlternatives(),
			//				new Double[] { new Double(10), new Double(20)})
		});
		Rdq4.setAction(a_Rdq4);
		Rdq4.setCondition(
			new CondAnd(Utils.createList(new AbstractCondition[] { new CondNumGreater(Mf5, new Double(0)), new CondKnown(Mf6)})));

		/*
		// (Radd59) Und (($= Mf4 3) ($= Mf3 4)) -> Msi21 
		RuleAddValue Radd59 = new RuleAddValue();
		Radd59.setId("Radd59");
		Radd59.setQuestion(Msi21);
		Radd59
			.setValues(new Object[] { new Schema(
				new FormulaNumber(new Double(2)),
				((QuestionChoice) Msi21).getAllAlternatives(),
				new Double[] {
			})
		});
		Radd59.setCondition(
			new CondAnd(
				Utils.createList(
					new Object[] { new CondEqual(Mf4, Mf4a3), new CondEqual(Mf3, Mf3a4)})));
		
		// (Radd4) Und (($Or Mf3 1 2 3)) -> Msi21
		RuleAddValue Radd4 = new RuleAddValue();
		Radd4.setId("Radd4");
		Radd4.setQuestion(Msi21);
		Radd4
			.setValues(new Object[] { new Schema(
				new FormulaNumber(new Double(1)),
				((QuestionChoice) Msi21).getAllAlternatives(),
				new Double[] {
			})
		});
		Radd4.setCondition(
			new CondOr(
				Utils.createList(
					new Object[] {
						new CondEqual(Mf3, Mf3a1),
						new CondEqual(Mf3, Mf3a2),
						new CondEqual(Mf3, Mf3a3)})));
		
		// (Radd2) Und (($= Mf3 4) ($= Mf4 1)) -> Msi21
		RuleAddValue Radd2 = new RuleAddValue();
		Radd2.setId("Radd2");
		Radd2.setQuestion(Msi21);
		Radd2
			.setValues(new Object[] { new Schema(
				new FormulaNumber(new Double(1)),
				((QuestionChoice) Msi21).getAllAlternatives(),
				new Double[] {
			})
		});
		Radd2.setCondition(
			new CondAnd(
				Utils.createList(
					new Object[] { new CondEqual(Mf3, Mf3a4), new CondEqual(Mf4, Mf4a1)})));
					*/
	}

	public KfzWb() {
		super();
		try {

			setProperties();
			addRules0();
			addRules1();

			/* setExplanation(); */

			/* Die Startfrageklassen */
			this.setInitQuestions(Utils.createVector(new QContainer[] { Q56, Q16 }));
			DCMarkup dcdata = new DCMarkup();
			dcdata.setContent(DCElement.TITLE, "KFZ-Wissensbasis");
			dcdata.setContent(DCElement.CREATOR, "d3Team");
			dcdata.setContent(DCElement.IDENTIFIER, "KFZWB1999");
			dcdata.setContent(DCElement.LANGUAGE, "german");
			this.setDCMarkup(dcdata);

		} catch (Exception e) {
			System.out.println("" + e);
		}
	}

	/**
	* Application: creates a knowledgbase and prints out its objects.
	* Creation date: (17.07.00 17:26:40)
	* @param args java.lang.String[]
	*/
	public static void main(String[] args) {
		KnowledgeBase theTestKb = new KfzWb();
		inspect(theTestKb);
	}
	
	/**
     * inspects the KB and prints out all objects the KB contains.
     */
    public static void inspect(KnowledgeBase kb) {
	System.out.println(kb.getQuestions().size() + " Fragen und "
		+ kb.getDiagnoses().size() + " Diagnosen");
	Iterator iter, secIter;
	iter = kb.getQuestions().iterator();
	while (iter.hasNext()) {
	    Question frage = (Question) iter.next();
	    System.out.println("<" + frage.getClass().getName() + " "
		    + frage.getId() + ": " + frage.getText() + ">");
	    secIter = null;
	    if (frage.getKnowledge(PSMethodHeuristic.class) != null)
		secIter = ((List) (frage.getKnowledge(PSMethodHeuristic.class)))
			.iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    Rule regel = (Rule) secIter.next();
		    System.out.println("  DiagnoseRegel: " + regel.getId()
			    + ": " + regel);
		}
	    secIter = null;
	    if (frage.getKnowledge(PSMethodNextQASet.class) != null)
		secIter = ((List) (frage.getKnowledge(PSMethodNextQASet.class)))
			.iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    Rule regel = (Rule) secIter.next();
		    System.out.println("  FolgefragenRegel: " + regel.getId()
			    + ": " + regel);
		}
	}
	iter = kb.getDiagnoses().iterator();
	while (iter.hasNext()) {
	    Diagnosis diagnose = (Diagnosis) iter.next();
	    System.out.println("<" + diagnose.getClass().getName() + " "
		    + diagnose.getId() + ": " + diagnose.getText() + ">");
	    secIter = null;
	    if (diagnose.getKnowledge(PSMethodHeuristic.class) != null)
		secIter = ((List) (diagnose
			.getKnowledge(PSMethodHeuristic.class))).iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    Rule regel = (Rule) secIter.next();
		    System.out.println("  DiagnoseRegel: " + regel.getId()
			    + ": " + regel);
		}
	    secIter = null;
	    if (diagnose.getKnowledge(PSMethodNextQASet.class) != null)
		secIter = ((List) (diagnose
			.getKnowledge(PSMethodNextQASet.class))).iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    Rule regel = (Rule) secIter.next();
		    System.out.println("  FolgefragenRegel: " + regel.getId()
			    + ": " + regel);
		}
	}
    }
}