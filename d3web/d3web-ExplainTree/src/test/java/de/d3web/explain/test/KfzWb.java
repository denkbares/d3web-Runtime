package de.d3web.explain.test;

import java.util.Arrays;
import java.util.Iterator;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.Operator;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.abstraction.formula.Operator.Operation;
import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.utilities.Utils;
import de.d3web.indication.inference.PSMethodNextQASet;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

public class KfzWb extends KnowledgeBase {

	/* Hier kommen die Fragen */
	private final QContainer Q000 = new QContainer("Q000");
	private final QContainer Q16 = new QContainer("Q16");
	private final QContainer Qcl16 = new QContainer("Qcl16");
	private final QContainer Q17 = new QContainer("Q17");
	private final QContainer Q18 = new QContainer("Q18");
	private final QContainer Q19 = new QContainer("Q19");
	private final QContainer Q20 = new QContainer("Q20");
	private final QContainer Q21 = new QContainer("Q21");
	private final QContainer Q56 = new QContainer("Q56");

	private final Choice Mf2a1 = AnswerFactory.createAnswerChoice("Mf2a1", "schwarz");
	private final Choice Mf2a2 = AnswerFactory.createAnswerChoice("Mf2a2", "bläulich");
	private final Choice Mf2a3 = AnswerFactory.createAnswerChoice("Mf2a3", "farblos");
	private final QuestionOC Mf2 = new QuestionOC("Mf2");

	private final Choice Mf3a1 = AnswerFactory.createAnswerChoice("Mf3a1", "braun");
	private final Choice Mf3a2 = AnswerFactory.createAnswerChoice("Mf3a2", "grau");
	private final Choice Mf3a3 = AnswerFactory.createAnswerChoice("Mf3a3", "hellgrau");
	private final Choice Mf3a4 = AnswerFactory.createAnswerChoice("Mf3a4", "schwarzverrußt");
	private final QuestionOC Mf3 = new QuestionOC("Mf3");

	private final Choice Msi21a1 = AnswerFactory.createAnswerChoice("Msi21a1", "normal");
	private final Choice Msi21a2 = AnswerFactory.createAnswerChoice("Msi21a2", "abnorm");
	private final QuestionOC Msi21 = new QuestionOC("Msi21");

	private final Choice Mf4a1 = AnswerFactory.createAnswerChoice("Mf4a1", "Diesel");
	private final Choice Mf4a3 = AnswerFactory.createAnswerChoice("Mf4a3", "Normal oder Super");
	private final QuestionOC Mf4 = new QuestionOC("Mf4");

	private final QuestionNum Mf5 = new QuestionNum("Mf5");

	private final Choice Msi4a1 = AnswerFactory.createAnswerChoice("Msi4a1", "normal");
	private final Choice Msi4a2 = AnswerFactory.createAnswerChoice("Msi4a2", "leicht erhöht");
	private final Choice Msi4a3 = AnswerFactory.createAnswerChoice("Msi4a3", "erhöht");
	private final QuestionOC Msi4 = new QuestionOC("Msi4");

	private final QuestionNum Mf6 = new QuestionNum("Mf6");

	private final Choice Mf7a1 = AnswerFactory.createAnswerChoice("Mf7a1", "klopfen");
	private final Choice Mf7a2 = AnswerFactory.createAnswerChoice("Mf7a2", "klingeln");
	private final Choice Mf7a0 = AnswerFactory.createAnswerNo("Mf7a0", "Nein/Sonstiges");
	private final QuestionMC Mf7 = new QuestionMC("Mf7");

	private final Choice Mf8a1 = AnswerFactory.createAnswerChoice("Mf8a1", "springt normal an");
	private final Choice Mf8a2 = AnswerFactory.createAnswerChoice("Mf8a2", "springt schlecht an");
	private final Choice Mf8a3 = AnswerFactory.createAnswerChoice("Mf8a3",
			"springt überhaupt nicht an");
	private final QuestionOC Mf8 = new QuestionOC("Mf8");

	private final Choice Mf10a1 = AnswerFactory.createAnswerChoice("Mf10a1", "dreht durch");
	private final Choice Mf10a2 = AnswerFactory.createAnswerChoice("Mf10a2",
			"dreht nicht richtig durch");
	private final QuestionOC Mf10 = new QuestionOC("Mf10");

	private final Choice Mf9a1 = AnswerFactory.createAnswerChoice("Mf9a1", "verzögertes Anfahren");
	private final Choice Mf9a2 = AnswerFactory.createAnswerChoice("Mf9a2",
			"schlechte Beschleunigung");
	private final Choice Mf9a3 = AnswerFactory.createAnswerChoice("Mf9a3",
			"Leerlauf ist zu niedrig");
	private final Choice Mf9a4 = AnswerFactory.createAnswerChoice("Mf9a4",
			"Leerlauf ist unregelmäßig");
	private final Choice Mf9a5 = AnswerFactory.createAnswerChoice("Mf9a5",
			"zu wenig Leistung bei Teillast");
	private final Choice Mf9a6 = AnswerFactory.createAnswerChoice("Mf9a6",
			"zu wenig Leistung bei Volllast");
	private final Choice Mf9a0 = AnswerFactory.createAnswerNo("Mf9a0", "Nein/Sonstiges");
	private final QuestionMC Mf9 = new QuestionMC("Mf9");

	private final Choice Mf13a1 = AnswerFactory.createAnswerChoice("Mf13a1", "ja");
	private final Choice Mf13a0 = AnswerFactory.createAnswerChoice("Mf13a0", "nein");
	private final QuestionOC Mf13 = new QuestionOC("Mf13");

	private final Choice Mf15a1 = AnswerFactory.createAnswerChoice("Mf15a1", "ja");
	private final Choice Mf15a0 = AnswerFactory.createAnswerChoice("Mf15a0", "nein");
	private final QuestionOC Mf15 = new QuestionOC("Mf15");

	private final Choice Mf17a1 = AnswerFactory.createAnswerChoice("Mf17a1", "ja");
	private final Choice Mf17a0 = AnswerFactory.createAnswerChoice("Mf17a0", "nein");
	private final QuestionOC Mf17 = new QuestionOC("Mf17");

	private final Choice Mf19a1 = AnswerFactory.createAnswerChoice("Mf19a1", "ja");
	private final Choice Mf19a0 = AnswerFactory.createAnswerChoice("Mf19a0", "nein");
	private final QuestionOC Mf19 = new QuestionOC("Mf19");

	private final Choice Mf11a1 = AnswerFactory.createAnswerChoice("Mf11a1", "ja");
	private final Choice Mf11a0 = AnswerFactory.createAnswerChoice("Mf11a0", "nein");
	private final QuestionOC Mf11 = new QuestionOC("Mf11");

	private final Choice Mf57a1 = AnswerFactory.createAnswerChoice("Mf57a1", "VW");
	private final Choice Mf57a2 = AnswerFactory.createAnswerChoice("Mf57a2", "Opel");
	private final Choice Mf57a3 = AnswerFactory.createAnswerChoice("Mf57a3", "Mercedes Benz");
	private final Choice Mf57a4 = AnswerFactory.createAnswerChoice("Mf57a4", "BMW");
	private final Choice Mf57a5 = AnswerFactory.createAnswerChoice("Mf57a5", "Porsche");
	private final Choice Mf57a6 = AnswerFactory.createAnswerChoice("Mf57a6", "Fiat");
	private final Choice Mf57a7 = AnswerFactory.createAnswerChoice("Mf57a7", "Toyota");
	private final Choice Mf57a8 = AnswerFactory.createAnswerChoice("Mf57a8", "Mazda");
	private final Choice Mf57a9 = AnswerFactory.createAnswerChoice("Mf57a9", "sonstige");
	private final QuestionOC Mf57 = new QuestionOC("Mf57");

	private final QuestionNum Mf58 = new QuestionNum("Mf58");

	/* Diagnoses ... */

	private final Solution P000 = new Solution("P000");
	private final Solution P8 = new Solution("P8");
	private final Solution P13 = new Solution("P13");
	private final Solution P14 = new Solution("P14");
	private final Solution P15 = new Solution("P15");
	private final Solution P16 = new Solution("P16");

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

			Q000.setName("Fragebögen");
			Q000.setKnowledgeBase(this);
			Q56.setName("Allgemeines");
			Q56.setKnowledgeBase(this);
			Q56.setParents(Utils.createList(new NamedObject[] { Q000 }));
			Q16.setName("Beobachtungen");
			Q16.setKnowledgeBase(this);
			Q16.setParents(Utils.createList(new NamedObject[] { Q000 }));
			Mf2.setName("SI: Abgase");
			Mf2.setKnowledgeBase(this);
			Mf2.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf2.setAlternatives(Utils.createVector(new Choice[] {
					Mf2a1, Mf2a2, Mf2a3 }));
			Mf3.setName("SI: Auspuffrohrfarbe");
			Mf3.setKnowledgeBase(this);
			Mf3.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf3.setAlternatives(Utils.createVector(new Choice[] {
					Mf3a1, Mf3a2, Mf3a3, Mf3a4 }));
			Msi21.setName("SI: Bewertung Auspuffrohrfarbe");
			Msi21.setKnowledgeBase(this);
			Msi21.setAlternatives(Utils.createVector(new Choice[] {
					Msi21a1, Msi21a2 }));
			Mf4.setName("SI: Benzinart");
			Mf4.setKnowledgeBase(this);
			Mf4.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf4.setAlternatives(Utils.createVector(new Choice[] {
					Mf4a1, Mf4a3 }));
			Mf5.setName("SI: Üblicher Kraftstoffverbrauch/100km");
			Mf5.setKnowledgeBase(this);
			Mf5.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Msi4.setName("SI: Bewertung Kraftstoffverbrauch");
			Msi4.setKnowledgeBase(this);
			Msi4.setAlternatives(Utils.createVector(new Choice[] {
					Msi4a1, Msi4a2, Msi4a3 }));
			Num2ChoiceSchema schema = new Num2ChoiceSchema("Msi4_Schema00");
			schema.setSchemaArray(new Double[] {
					new Double(10), new Double(20) });
			Msi4.addKnowledge(PSMethodAbstraction.class, schema,
					PSMethodAbstraction.NUM2CHOICE_SCHEMA);

			Mf6.setName("SI: Tatsächlicher Kraftstoffverbrauch/100km");
			Mf6.setKnowledgeBase(this);
			Mf6.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf7.setName("SI: Motorgeräusche");
			Mf7.setKnowledgeBase(this);
			Mf7.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf7.setAlternatives(Utils.createVector(new Choice[] {
					Mf7a1, Mf7a2, Mf7a0 }));
			Mf8.setName("SI: Verhalten bei Motorstart");
			Mf8.setKnowledgeBase(this);
			Mf8.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf8.setAlternatives(Utils.createVector(new Choice[] {
					Mf8a1, Mf8a2, Mf8a3 }));
			Mf10.setName("Wie verhält sich der Anlasser bei Motorstartversuchen?");
			Mf10.setKnowledgeBase(this);
			Mf10.setAlternatives(Utils.createVector(new Choice[] {
					Mf10a1, Mf10a2 }));
			Mf9.setName("SI: Fahrverhalten");
			Mf9.setKnowledgeBase(this);
			Mf9.setParents(Utils.createList(new NamedObject[] { Q16 }));
			Mf9.setAlternatives(Utils.createVector(new Choice[] {
					Mf9a1, Mf9a2, Mf9a3, Mf9a4, Mf9a5, Mf9a6, Mf9a0 }));

			Qcl16.setName("Technische Untersuchungen");
			Qcl16.setKnowledgeBase(this);
			Qcl16.setParents(Utils.createList(new NamedObject[] { Q000 }));
			Q17.setName("Untersuchung Leerlaufsystem");
			Q17.setKnowledgeBase(this);
			Q17.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q18.setName("Untersuchung Ansaugsystem");
			Q18.setKnowledgeBase(this);
			Q18.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q19.setName("Untersuchung Luftfiltereinsatz");
			Q19.setKnowledgeBase(this);
			Q19.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q20.setName("Untersuchung Zündeinstellung");
			Q20.setKnowledgeBase(this);
			Q20.setParents(Utils.createList(new NamedObject[] { Qcl16 }));
			Q21.setName("Untersuchung Batterie");
			Q21.setKnowledgeBase(this);
			Q21.setParents(Utils.createList(new NamedObject[] { Qcl16 }));

			Mf13.setName("Ist ein Fehler im Leerlaufsystem?");
			Mf13.setKnowledgeBase(this);
			Mf13.setParents(Utils.createList(new NamedObject[] { Q17 }));
			Mf13.setAlternatives(Utils.createVector(new Choice[] {
					Mf13a1, Mf13a0 }));
			Mf15.setName("Ist ein Fehler im Ansaugsystem?");
			Mf15.setKnowledgeBase(this);
			Mf15.setParents(Utils.createList(new NamedObject[] { Q18 }));
			Mf15.setAlternatives(Utils.createVector(new Choice[] {
					Mf15a1, Mf15a0 }));
			Mf17.setName("Ist der Luftfilter verschmutzt?");
			Mf17.setKnowledgeBase(this);
			Mf17.setParents(Utils.createList(new NamedObject[] { Q19 }));
			Mf17.setAlternatives(Utils.createVector(new Choice[] {
					Mf17a1, Mf17a0 }));

		}
		catch (Exception e) {
			System.out.println("" + e);
		}

	}

	private void setProperties1() {
		try {
			Mf19.setName("Ist die Zündeinstellung fehlerhaft?");
			Mf19.setKnowledgeBase(this);
			Mf19.setParents(Utils.createList(new NamedObject[] { Q20 }));
			Mf19.setAlternatives(Utils.createVector(new Choice[] {
					Mf19a1, Mf19a0 }));
			Mf11.setName("Ist die Batterie leer?");
			Mf11.setKnowledgeBase(this);
			Mf11.setParents(Utils.createList(new NamedObject[] { Q21 }));
			Mf11.setAlternatives(Utils.createVector(new Choice[] {
					Mf11a1, Mf11a0 }));
			Mf57.setName("Automarke?");
			Mf57.setKnowledgeBase(this);
			Mf57.setParents(Utils.createList(new NamedObject[] { Q56 }));
			Mf57.setAlternatives(
					Utils.createVector(
							new Choice[] {
									Mf57a1, Mf57a2, Mf57a3, Mf57a4, Mf57a5, Mf57a6, Mf57a7, Mf57a8,
									Mf57a9 }));
			Mf58.setName("Baujahr des Autos?");
			Mf58.setKnowledgeBase(this);
			Mf58.setParents(Utils.createList(new NamedObject[] { Q56 }));
			P000.setName("Klassifikation");
			P000.setKnowledgeBase(this);
			P8.setName("Leerlaufsystem defekt");
			P8.setKnowledgeBase(this);
			P8.setParents(Arrays.asList(new NamedObject[] { P000 }));
			P13.setName("Ansaugsystem undicht");
			P13.setKnowledgeBase(this);
			P13.setParents(Arrays.asList(new NamedObject[] { P000 }));

			P14.setAprioriProbability(Score.P2);
			P14.setKnowledgeBase(this);
			P14.setName("Luftfiltereinsatz verschmutzt");
			P14.setParents(Arrays.asList(new NamedObject[] { P000 }));

			P15.setName("Zündeinstellung falsch");
			P15.setKnowledgeBase(this);
			P15.setParents(Arrays.asList(new NamedObject[] { P000 }));

			P16.setName("Batterie leer");
			P16.setKnowledgeBase(this);
			P16.setParents(Arrays.asList(new NamedObject[] { P000 }));

		}
		catch (Exception e) {
			System.out.println("" + e);
		}

	}

	private void setProperties() {
		try {
			setProperties0();
			setProperties1();
		}
		catch (Exception e) {
			System.out.println("" + e);
		}

	}

	private void addRules0() {

		/* Die unkonvertierten Regeln */

		/* Die Folgefrage-Regeln */

		/* (RASK10) Und (($Or Mf8 2 3)) -> Mf10 */
		// RuleComplex RASK10 =
		RuleFactory.createIndicationRule(
				"RASK10",
				Utils.createList(new QASet[] { Mf10 }),
				new CondOr(Utils.createList(new Condition[] {
						mce(Mf8, Mf8a2), mce(Mf8, Mf8a3) })));

		/*
		 * Next von P000: rqsug14766
		 */
		// RuleComplex rqsug14766 =
		RuleFactory.createClarificationRule("rqsug14766", Utils.createList(new QASet[] {
				}), P000, new CondDState(P000, new Rating(Rating.State.SUGGESTED)));

		/* Frageklassen nach Etablierung von P000: Nil */
		// RuleComplex rqetab14767 =
		RuleFactory.createRefinementRule("rqetab14767", Utils.createList(
				new QASet[] {}), P000, new CondDState(P000, new Rating(
				Rating.State.ESTABLISHED)));

		/*
		 * Next von P8: rqsug14769
		 */
		// RuleComplex rqsug14769 =
		RuleFactory.createClarificationRule(
				"rqsug14769",
				Utils.createList(new QASet[] { Q17 }),
				P8,
				new CondDState(P8, new Rating(Rating.State.SUGGESTED)));

		/* Frageklassen nach Etablierung von P8: (Q17) */
		// RuleComplex rqetab14770 =
		RuleFactory.createRefinementRule(
				"rqetab14770",
				Utils.createList(new QASet[] { Q17 }),
				P8,
				new CondDState(P8, new Rating(Rating.State.ESTABLISHED)));

		/*
		 * Next von P13: rqsug14772
		 */
		// RuleComplex rqsug14772 =
		RuleFactory.createClarificationRule(
				"rqsug14772",
				Utils.createList(new QASet[] { Q18 }),
				P13,
				new CondDState(P13, new Rating(Rating.State.SUGGESTED)));

		/* Frageklassen nach Etablierung von P13: (Q18) */
		// RuleComplex rqetab14773 =
		RuleFactory.createRefinementRule(
				"rqetab14773",
				Utils.createList(new QASet[] { Q18 }),
				P13,
				new CondDState(P13, new Rating(Rating.State.ESTABLISHED)));

		/*
		 * Next von P14: rqsug14775
		 */
		// RuleComplex rqsug14775 =
		RuleFactory.createClarificationRule(
				"rqsug14775",
				Utils.createList(new QASet[] { Q19 }),
				P14,
				new CondDState(P14, new Rating(Rating.State.SUGGESTED)));

		/* Frageklassen nach Etablierung von P14: (Q19) */
		// RuleComplex rqetab14776 =
		RuleFactory.createRefinementRule(
				"rqetab14776",
				Utils.createList(new QASet[] { Q19 }),
				P14,
				new CondDState(P14, new Rating(Rating.State.ESTABLISHED)));

		/*
		 * Next von P15: rqsug14778
		 */
		// RuleComplex rqsug14778 =
		RuleFactory.createClarificationRule(
				"rqsug14778",
				Utils.createList(new QASet[] { Q20 }),
				P15,
				new CondDState(P15, new Rating(Rating.State.SUGGESTED)));

		/* Frageklassen nach Etablierung von P15: (Q20) */
		// RuleComplex rqetab14779 =
		RuleFactory.createRefinementRule(
				"rqetab14779",
				Utils.createList(new QASet[] { Q20 }),
				P15,
				new CondDState(P15, new Rating(Rating.State.ESTABLISHED)));

		/*
		 * Next von P16: rqsug14781
		 */
		// RuleComplex rqsug14781 =
		RuleFactory.createClarificationRule(
				"rqsug14781",
				Utils.createList(new QASet[] { Q21 }),
				P16,
				new CondDState(P16, new Rating(Rating.State.SUGGESTED)));

		/* Frageklassen nach Etablierung von P16: (Q21) */
		// RuleComplex rqetab14782 =
		RuleFactory.createRefinementRule(
				"rqetab14782",
				Utils.createList(new QASet[] { Q21 }),
				P16,
				new CondDState(P16, new Rating(Rating.State.ESTABLISHED)));

		/* Die Diagnose-Regeln */

		/*
		 * (R_FB_57) Und (($= Mf10 1)) -> P16, N4
		 */
		// RuleComplex R_FB_57 =
		RuleFactory.createHeuristicPSRule("R_FB_57", P16, Score.N4, mce(Mf10, Mf10a1));

		/*
		 * (R_FB_56) Und (($Or Mf8 3 2)) -> P16, P5
		 */
		// RuleComplex R_FB_56 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_56",
				P16,
				Score.P5,
				new CondOr(Utils.createList(new Condition[] {
						mce(Mf8, Mf8a3), mce(Mf8, Mf8a2) })));

		/*
		 * (R_FB_29) Und (($= Mf9 4)) -> P14, P4
		 */
		// RuleComplex R_FB_29 =
		RuleFactory.createHeuristicPSRule("R_FB_29", P14, Score.P4, mce(Mf9, Mf9a4));

		/*
		 * (R_FB_28) Und (($= Msi4 1)) -> P13, N4
		 */
		// RuleComplex R_FB_28 =
		RuleFactory.createHeuristicPSRule("R_FB_28", P13, Score.N4, mce(Msi4, Msi4a1));

		/*
		 * (R_FB_27) Und (($= Mf9 5)) -> P13, P3
		 */
		// RuleComplex R_FB_27 =
		RuleFactory.createHeuristicPSRule("R_FB_27", P13, Score.P3, mce(Mf9, Mf9a5));

		/*
		 * (R_FB_26) Und (($= Mf9 4)) -> P13, P4
		 */
		// RuleComplex R_FB_26 =
		RuleFactory.createHeuristicPSRule("R_FB_26", P13, Score.P4, mce(Mf9, Mf9a4));

		/*
		 * (R_FB_25) Und (($= Msi4 2)) -> P13, P3
		 */
		// RuleComplex R_FB_25 =
		RuleFactory.createHeuristicPSRule("R_FB_25", P13, Score.P3, mce(Msi4, Msi4a2));

		/*
		 * (R_FB_24) Und (($= Msi4 3)) -> P13, P4
		 */
		// RuleComplex R_FB_24 =
		RuleFactory.createHeuristicPSRule("R_FB_24", P13, Score.P4, mce(Msi4, Msi4a3));

		/*
		 * (R_FB_22) Und ((Non $= Mf10 1)) -> P14, N6
		 */
		// RuleComplex R_FB_22 =
		RuleFactory.createHeuristicPSRule("R_FB_22", P14, Score.N6, new CondNot(mce(Mf10,
				Mf10a1)));

		/*
		 * (R_FB_21) Und ((Non $Or Mf8 3 2)) -> P14, N4
		 */
		// RuleComplex R_FB_21 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_21",
				P14,
				Score.N4,
				new CondNot(
						new CondOr(
								Utils.createList(new Condition[] {
										mce(Mf8, Mf8a3), mce(Mf8, Mf8a2) }))));

		/*
		 * (R_FB_20) Und (($Or Mf8 3 2) ($= Mf10 1)) -> P14, P4
		 */
		// RuleComplex R_FB_20 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_20",
				P14,
				Score.P4,
				new CondAnd(
						Utils.createList(
								new Condition[] {
										new CondOr(
												Utils.createList(new Condition[] {
														mce(Mf8, Mf8a3), mce(Mf8, Mf8a2) })),
										mce(Mf10, Mf10a1)
						})));

		/*
		 * (R_FB_18) Und (($= Msi4 1)) -> P14, N4
		 */
		// RuleComplex R_FB_18 =
		RuleFactory.createHeuristicPSRule("R_FB_18", P14, Score.N4, mce(Msi4, Msi4a1));

		/*
		 * (R_FB_17) Und (($= Msi4 2)) -> P14, P3
		 */
		// RuleComplex R_FB_17 =
		RuleFactory.createHeuristicPSRule("R_FB_17", P14, Score.P3, mce(Msi4, Msi4a2));

		/*
		 * (R_FB_16) Und (($= Msi4 3)) -> P14, P4
		 */
		// RuleComplex R_FB_16 =
		RuleFactory.createHeuristicPSRule("R_FB_16", P14, Score.P4, mce(Msi4, Msi4a3));

		/*
		 * (R_FB_15) Und (($= Msi21 2)) -> P14, P5
		 */
		// RuleComplex R_FB_15 =
		RuleFactory.createHeuristicPSRule("R_FB_15", P14, Score.P5, mce(Msi21, Msi21a2));

		/*
		 * (R_FB_14) Und (($= Mf2 1)) -> P14, P5
		 */
		// RuleComplex R_FB_14 =
		RuleFactory.createHeuristicPSRule("R_FB_14", P14, Score.P5, mce(Mf2, Mf2a1));

		/*
		 * (R_FB_13) Und ((Non $Or Mf8 3 2)) -> P15, N5
		 */
		// RuleComplex R_FB_13 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_13",
				P15,
				Score.N5,
				new CondNot(
						new CondOr(
								Utils.createList(new Condition[] {
										mce(Mf8, Mf8a3), mce(Mf8, Mf8a2) }))));

		/*
		 * (R_FB_12) Und ((Non $Or Mf7 2 1)) -> P15, N3
		 */
		// RuleComplex R_FB_12 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_12",
				P15,
				Score.N3,
				new CondNot(
						new CondOr(
								Utils.createList(new Condition[] {
										mce(Mf7, Mf7a2), mce(Mf7, Mf7a1) }))));

		/*
		 * (R_FB_11) Und ((Non $= Mf10 1)) -> P15, N6
		 */
		// RuleComplex R_FB_11 =
		RuleFactory.createHeuristicPSRule("R_FB_11", P15, Score.N6, new CondNot(mce(Mf10,
				Mf10a1)));

		/*
		 * (R_FB_10) Und (($Or Mf8 3 2) ($= Mf10 1)) -> P15, P5
		 */
		// RuleComplex R_FB_10 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_10",
				P15,
				Score.P5,
				new CondAnd(
						Utils.createList(
								new Condition[] {
										new CondOr(
												Utils.createList(new Condition[] {
														mce(Mf8, Mf8a3), mce(Mf8, Mf8a2) })),
										mce(Mf10, Mf10a1)
						})));

		/*
		 * (R_FB_9) Und (($Or Mf7 2 1)) -> P15, P5
		 */
		// RuleComplex R_FB_9 =
		RuleFactory.createHeuristicPSRule(
				"R_FB_9",
				P15,
				Score.P5,
				new CondOr(Utils.createList(new Condition[] {
						mce(Mf7, Mf7a2), mce(Mf7, Mf7a1) })));

		/*
		 * (R_FB_8) Und (($= Mf9 4)) -> P15, P3
		 */
		// RuleComplex R_FB_8 =
		RuleFactory.createHeuristicPSRule("R_FB_8", P15, Score.P3, mce(Mf9, Mf9a4));

		/*
		 * (R_FB_7) Und (($= Mf9 1)) -> P15, P3
		 */
		// RuleComplex R_FB_7 =
		RuleFactory.createHeuristicPSRule("R_FB_7", P15, Score.P3, mce(Mf9, Mf9a1));

		/*
		 * (R_FB_6) Und ((Non $= Mf9 3)) -> P8, N3
		 */
		// RuleComplex R_FB_6 =
		RuleFactory.createHeuristicPSRule("R_FB_6", P8, Score.N3, new CondNot(mce(Mf9,
				Mf9a3)));

		/*
		 * (R_FB_5) Und ((Non $= Mf9 4)) -> P8, N4
		 */
		// RuleComplex R_FB_5 =
		RuleFactory.createHeuristicPSRule("R_FB_5", P8, Score.N4, new CondNot(mce(Mf9,
				Mf9a4)));

		/*
		 * (R_FB_4) Und (($= Mf8 2)) -> P8, P5
		 */
		// RuleComplex R_FB_4 =
		RuleFactory.createHeuristicPSRule("R_FB_4", P8, Score.P5, mce(Mf8, Mf8a2));

		/*
		 * (R_FB_3) Und (($= Mf9 3)) -> P8, P4
		 */
		// RuleComplex R_FB_3 =
		RuleFactory.createHeuristicPSRule("R_FB_3", P8, Score.P4, mce(Mf9, Mf9a3));

		/*
		 * (R_FB_2) Und (($= Mf9 4)) -> P8, P5
		 */
		// RuleComplex R_FB_2 =
		RuleFactory.createHeuristicPSRule("R_FB_2", P8, Score.P5, mce(Mf9, Mf9a4));

		/*
		 * (RFB21) Und (($Isvalue Mf19 True)) -> P15, P7
		 */
		// RuleComplex RFB21 =
		RuleFactory.createHeuristicPSRule("RFB21", P15, Score.P7, mce(Mf19, Mf19a1));

		/*
		 * (RFB20) Und (($Isvalue Mf19 False)) -> P15, N7
		 */
		// RuleComplex RFB20 =
		RuleFactory.createHeuristicPSRule("RFB20", P15, Score.N7, mce(Mf19, Mf19a0));

		/*
		 * (RFB19) Und (($Isvalue Mf17 True)) -> P14, P7
		 */
		// RuleComplex RFB19 =
		RuleFactory.createHeuristicPSRule("RFB19", P14, Score.P7, mce(Mf17, Mf17a1));

		/*
		 * (RFB18) Und (($Isvalue Mf17 False)) -> P14, N7
		 */
		// RuleComplex RFB18 =
		RuleFactory.createHeuristicPSRule("RFB18", P14, Score.N7, mce(Mf17, Mf17a0));

		/*
		 * (RFB17) Und (($Isvalue Mf15 True)) -> P13, P7
		 */
		// RuleComplex RFB17 =
		RuleFactory.createHeuristicPSRule("RFB17", P13, Score.P7, mce(Mf15, Mf15a1));

		/*
		 * (RFB16) Und (($Isvalue Mf15 False)) -> P13, N7
		 */
		// RuleComplex RFB16 =
		RuleFactory.createHeuristicPSRule("RFB16", P13, Score.N7, mce(Mf15, Mf15a0));

		/*
		 * (RFB15) Und (($Isvalue Mf13 True)) -> P8, P7
		 */
		// RuleComplex RFB15 =
		RuleFactory.createHeuristicPSRule("RFB15", P8, Score.P7, mce(Mf13, Mf13a1));

		/*
		 * (RFB14) Und (($Isvalue Mf13 False)) -> P8, N7
		 */
		// RuleComplex RFB14 =
		RuleFactory.createHeuristicPSRule("RFB14", P8, Score.N7, mce(Mf13, Mf13a0));

		/*
		 * (RFB13) Und (($Isvalue Mf11 True)) -> P16, P7
		 */
		// RuleComplex RFB13 =
		RuleFactory.createHeuristicPSRule("RFB13", P16, Score.P7, mce(Mf11, Mf11a1));

		/*
		 * (RFB12) Und (($Isvalue Mf11 False)) -> P16, N7
		 */
		// RuleComplex RFB12 =
		RuleFactory.createHeuristicPSRule("RFB12", P16, Score.N7, mce(Mf11, Mf11a0));

		/*
		 * (RFB11) Und (($= Mf10 2)) -> P16, P5
		 */
		// RuleComplex RFB11 =
		RuleFactory.createHeuristicPSRule("RFB11", P16, Score.P5, mce(Mf10, Mf10a2));

	}

	private void addRules1() {

		// Die Symptom-Interpretations-Regeln

		// (Radd59) Und (($= Mf4 3) ($= Mf3 4)) -> Msi21
		Rule Radd59 = new Rule("Radd59", PSMethodAbstraction.class);
		ActionSetValue a_Radd59 = new ActionSetValue();
		a_Radd59.setQuestion(Msi21);
		a_Radd59.setValue(new ChoiceValue(Msi21a2));
		Radd59.setAction(a_Radd59);
		Radd59.setCondition(
				new CondAnd(Utils.createList(new Condition[] {
						mce(Mf4, Mf4a3), mce(Mf3, Mf3a4) })));

		// (Radd4) Und (($Or Mf3 1 2 3)) -> Msi21
		Rule Radd4 = new Rule("Radd4", PSMethodAbstraction.class);
		ActionSetValue a_Radd4 = new ActionSetValue();
		a_Radd4.setQuestion(Msi21);
		a_Radd4.setValue(new ChoiceValue(Msi21a1));
		Radd4.setAction(a_Radd4);
		Radd4.setCondition(
				new CondOr(
						Utils.createList(
								new Condition[] {
										mce(Mf3, Mf3a1), mce(Mf3, Mf3a2), mce(Mf3, Mf3a3) })));

		// (Radd2) Und (($= Mf3 4) ($= Mf4 1)) -> Msi21
		Rule Radd2 = new Rule("Radd2", PSMethodAbstraction.class);
		ActionSetValue a_Radd2 = new ActionSetValue();
		a_Radd2.setQuestion(Msi21);
		a_Radd2.setValue(new ChoiceValue(Msi21a1));
		Radd2.setAction(a_Radd2);
		Radd2.setCondition(
				new CondAnd(Utils.createList(new Condition[] {
						mce(Mf3, Mf3a4), mce(Mf4, Mf4a1) })));

		/* Die Symptom-Interpretations-Regeln (Numerische) */

		/* (Rdq4) Und (($> Mf5 0) ($Isvalue Mf6 True)) -> Msi4 */
		Rule Rdq4 = new Rule("Rdq4", PSMethodAbstraction.class);
		ActionSetValue a_Rdq4 = new ActionSetValue();
		a_Rdq4.setQuestion(Msi4);

		FormulaExpression formula =
				new FormulaExpression(
						Msi4,
						new Operator(
								new Operator(new Operator(new QNumWrapper(Mf6),
										new QNumWrapper(Mf5), Operation.Sub), new QNumWrapper(Mf5),
										Operation.Div),
								new FormulaNumber(new Double(100)), Operation.Mult));

		a_Rdq4.setValue(formula
						// new Schema(
						// new Mult(new Div(new Sub(new QNumWrapper(Mf6), new
						// QNumWrapper(Mf5)),
						// new QNumWrapper(Mf5)), new FormulaNumber(new
						// Double(100))),
						// ((QuestionChoice) Msi4).getAllAlternatives(),
						// new Double[] { new Double(10), new Double(20)})
						);
		Rdq4.setAction(a_Rdq4);
		Rdq4.setCondition(
				new CondAnd(Utils.createList(new Condition[] {
						new CondNumGreater(Mf5, new Double(0)), new CondKnown(Mf6) })));

		/*
		 * // (Radd59) Und (($= Mf4 3) ($= Mf3 4)) -> Msi21 RuleAddValue Radd59
		 * = new RuleAddValue(); Radd59.setId("Radd59");
		 * Radd59.setQuestion(Msi21); Radd59 .setValues(new Object[] { new
		 * Schema( new FormulaNumber(new Double(2)), ((QuestionChoice)
		 * Msi21).getAllAlternatives(), new Double[] { }) });
		 * Radd59.setCondition( new CondAnd( Utils.createList( new Object[] {
		 * new CondEqual(Mf4, Mf4a3), new CondEqual(Mf3, Mf3a4)})));
		 * 
		 * // (Radd4) Und (($Or Mf3 1 2 3)) -> Msi21 RuleAddValue Radd4 = new
		 * RuleAddValue(); Radd4.setId("Radd4"); Radd4.setQuestion(Msi21); Radd4
		 * .setValues(new Object[] { new Schema( new FormulaNumber(new
		 * Double(1)), ((QuestionChoice) Msi21).getAllAlternatives(), new
		 * Double[] { }) }); Radd4.setCondition( new CondOr( Utils.createList(
		 * new Object[] { new CondEqual(Mf3, Mf3a1), new CondEqual(Mf3, Mf3a2),
		 * new CondEqual(Mf3, Mf3a3)})));
		 * 
		 * // (Radd2) Und (($= Mf3 4) ($= Mf4 1)) -> Msi21 RuleAddValue Radd2 =
		 * new RuleAddValue(); Radd2.setId("Radd2"); Radd2.setQuestion(Msi21);
		 * Radd2 .setValues(new Object[] { new Schema( new FormulaNumber(new
		 * Double(1)), ((QuestionChoice) Msi21).getAllAlternatives(), new
		 * Double[] { }) }); Radd2.setCondition( new CondAnd( Utils.createList(
		 * new Object[] { new CondEqual(Mf3, Mf3a4), new CondEqual(Mf4,
		 * Mf4a1)})));
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
			this.setInitQuestions(Utils.createVector(new QContainer[] {
					Q56, Q16 }));
			DCMarkup dcdata = new DCMarkup();
			dcdata.setContent(DCElement.TITLE, "KFZ-Wissensbasis");
			dcdata.setContent(DCElement.CREATOR, "d3Team");
			dcdata.setContent(DCElement.IDENTIFIER, "KFZWB1999");
			dcdata.setContent(DCElement.LANGUAGE, "german");
			this.setDCMarkup(dcdata);

		}
		catch (Exception e) {
			System.out.println("" + e);
		}
	}

	/**
	 * Application: creates a knowledgbase and prints out its objects. Creation
	 * date: (17.07.00 17:26:40)
	 * 
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
				+ kb.getSolutions().size() + " Diagnosen");
		Iterator<Question> iter = kb.getQuestions().iterator();
		while (iter.hasNext()) {
			Question frage = iter.next();
			System.out.println("<" + frage.getClass().getName() + " "
					+ frage.getId() + ": " + frage.getName() + ">");
			KnowledgeSlice knowledge = frage.getKnowledge(PSMethodHeuristic.class,
					MethodKind.FORWARD);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				for (Rule regel : rs.getRules()) {
					System.out.println("  DiagnoseRegel: " + regel.getId()
							+ ": " + regel);
				}
			}
			knowledge = frage.getKnowledge(PSMethodNextQASet.class, MethodKind.FORWARD);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				for (Rule regel : rs.getRules()) {
					System.out.println("  DiagnoseRegel: " + regel.getId()
							+ ": " + regel);
				}
			}
		}
		Iterator<Solution> iter2 = kb.getSolutions().iterator();
		while (iter.hasNext()) {
			Solution diagnose = iter2.next();
			System.out.println("<" + diagnose.getClass().getName() + " "
					+ diagnose.getId() + ": " + diagnose.getName() + ">");
			KnowledgeSlice knowledge = diagnose.getKnowledge(PSMethodHeuristic.class,
					MethodKind.FORWARD);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				for (Rule regel : rs.getRules()) {
					System.out.println("  DiagnoseRegel: " + regel.getId()
							+ ": " + regel);
				}
			}
			knowledge = diagnose.getKnowledge(PSMethodNextQASet.class, MethodKind.FORWARD);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				for (Rule regel : rs.getRules()) {
					System.out.println("  FolgefragenRegel: " + regel.getId()
							+ ": " + regel);
				}
			}
		}
	}

	/**
	 * Make CondEqual
	 */
	private CondEqual mce(QuestionChoice q, Choice a) {
		return new CondEqual(q, new ChoiceValue(a));
	}

}