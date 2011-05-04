package de.elog.elReasoner;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.elog.Constants;
import de.elog.evaluator.DisjointClassAxiomsToCheck;
import de.elog.evaluator.Evaluator;
import de.elog.evaluator.SubClassAxiomsToCheck;
import de.unima.app.grounder.StandardGrounder;
import de.unima.app.solver.StandardSolver;
import de.unima.exception.ParseException;
import de.unima.exception.ReadOrWriteToFileException;
import de.unima.exception.SolveException;
import de.unima.helper.LogFileWriter;
import de.unima.javaAPI.Model;
import de.unima.javaAPI.formulars.FormularHard;
import de.unima.javaAPI.formulars.FormularObjective;
import de.unima.javaAPI.formulars.expressions.impl.EqualStringExpression;
import de.unima.javaAPI.formulars.expressions.impl.EqualVariableExpression;
import de.unima.javaAPI.formulars.expressions.impl.PredicateExpression;
import de.unima.javaAPI.formulars.expressions.impl.ThresholdExpression;
import de.unima.javaAPI.formulars.variables.impl.VariableDouble;
import de.unima.javaAPI.formulars.variables.impl.VariableType;
import de.unima.javaAPI.predicates.Predicate;
import de.unima.javaAPI.predicates.PredicateDouble;
import de.unima.javaAPI.types.Type;

public class Reasoner {

	private boolean useCuttingPlaneInference = true;
	
	public Model generateModel(
			ArrayList<String[]> objectProperty,ArrayList<String[]> concept, ArrayList<String[]> subsumesEvidence,
			ArrayList<String[]> intersectionEvidence,ArrayList<String[]> opsubEvidence,ArrayList<String[]> opsupEvidence,
			ArrayList<String[]> psubsumesEvidence, ArrayList<String[]> pcomEvidence,
			ArrayList<String[]> subsumesHard, ArrayList<String[]> intersectionHard,
			ArrayList<String[]> opsubHard, ArrayList<String[]> opsupHard, ArrayList<String[]> psubsumesHard,
			ArrayList<String[]> pcomHard) throws ParseException{
		
		double threshold = 0.0;
		
		Type objectPropertyT = new Type("Objectproperty");
		Type conceptT = new Type("Concept");
		
		// Predicate (observed)
		Predicate conceptP = new Predicate("concept", false, conceptT);conceptP.setGroundValues(concept);
		Predicate objectPropertyP = new Predicate("objectproperty", false, objectPropertyT);objectPropertyP.setGroundValues(objectProperty);
		// Predicate (observed): evidence
		PredicateDouble subsumesEvidenceP = new PredicateDouble("subsumesEvidence",false, conceptT, conceptT);subsumesEvidenceP.setGroundValues(subsumesEvidence);
		PredicateDouble intersectionEvidenceP = new PredicateDouble("intersectionEvidence",false, conceptT, conceptT, conceptT);intersectionEvidenceP.setGroundValues(intersectionEvidence);
		PredicateDouble opsubEvidenceP = new PredicateDouble("opsubEvidence",false, objectPropertyT, conceptT, conceptT);opsubEvidenceP.setGroundValues(opsubEvidence);
		PredicateDouble opsupEvidenceP = new PredicateDouble("opsupEvidence",false, objectPropertyT, conceptT, conceptT);opsubEvidenceP.setGroundValues(opsubEvidence);
		PredicateDouble psubsumesEvidenceP = new PredicateDouble("psubsumesEvidence", false, objectPropertyT, objectPropertyT);psubsumesEvidenceP.setGroundValues(psubsumesEvidence);
		PredicateDouble pcomEvidenceP = new PredicateDouble("pcomEvidence", false, objectPropertyT, objectPropertyT, objectPropertyT);pcomEvidenceP.setGroundValues(pcomEvidence);

		// Predicate (observed): hard
		Predicate subsumesHardP = new Predicate("subsumesHard",false, conceptT, conceptT);subsumesHardP.setGroundValues(subsumesHard);
		Predicate intersectionHardP = new Predicate("intersectionHard",false, conceptT, conceptT, conceptT);intersectionHardP.setGroundValues(intersectionHard);
		Predicate opsubHardP = new Predicate("opsubHard",false, objectPropertyT, conceptT, conceptT);opsubHardP.setGroundValues(opsubHard);
		Predicate opsupHardP = new Predicate("opsupHard",false, objectPropertyT, conceptT, conceptT);opsupHardP.setGroundValues(opsupHard);
		Predicate psubsumesHardP = new Predicate("psubsumesHard", false, objectPropertyT, objectPropertyT);psubsumesHardP.setGroundValues(psubsumesHard);
		Predicate pcomHardP = new Predicate("pcomHard", false, objectPropertyT, objectPropertyT,objectPropertyT);pcomHardP.setGroundValues(pcomHard);
		
		// Predicate (hidden)
		Predicate subsumesP = new Predicate("subsumes", true, conceptT, conceptT);
		Predicate intersectionP = new Predicate("intersection", true, conceptT, conceptT, conceptT);
		Predicate opsubP = new Predicate("opsub", true, objectPropertyT, conceptT, conceptT);
		Predicate opsupP = new Predicate("opsup", true, objectPropertyT, conceptT, conceptT);
		Predicate psubsumesP = new Predicate("psubsumes", true, objectPropertyT, objectPropertyT);
		Predicate pcomP = new Predicate("pcom", true, objectPropertyT, objectPropertyT, objectPropertyT);
		
		
		// assign predicates to types
		objectPropertyT.setGroundValuesPredicate(objectPropertyP);
		conceptT.setGroundValuesPredicate(conceptP);
		
		// Variables
		VariableType c1 = new VariableType("c1", conceptT);
		VariableType c2 = new VariableType("c2", conceptT);
		VariableType c3 = new VariableType("c3", conceptT);
		VariableType c4 = new VariableType("c4", conceptT);
		VariableType p1 = new VariableType("p1", objectPropertyT);
		VariableType p2 = new VariableType("p2", objectPropertyT);
		VariableType p3 = new VariableType("p3", objectPropertyT);
		VariableDouble conf = new VariableDouble("conf");
		
		// Formulars
		// =========================================
		// CONNECTION HARD TO HIDDEN
		// =========================================
		FormularHard subsumesHardF = new FormularHard();
		subsumesHardF.useCuttingPlaneInference(false);
		subsumesHardF.setName("subsumesHardF");
		subsumesHardF.setForVariables(c1, c2);
		subsumesHardF.setIfExpressions(
				new PredicateExpression(true, subsumesHardP, c1, c2));
		subsumesHardF.setRestrictions(
				new PredicateExpression(true, subsumesP, c1, c2));
		
		FormularHard intersectionHardF = new FormularHard();
		intersectionHardF.useCuttingPlaneInference(false);
		intersectionHardF.setName("intersectionHardF");
		intersectionHardF.setForVariables(c1, c2, c3);
		intersectionHardF.setIfExpressions(
				new PredicateExpression(true, intersectionHardP, c1, c2, c3));
		intersectionHardF.setRestrictions(
				new PredicateExpression(true, intersectionP, c1, c2, c3));
		
		FormularHard opsubHardF = new FormularHard();
		opsubHardF.useCuttingPlaneInference(false);
		opsubHardF.setName("opsubHardF");
		opsubHardF.setForVariables(p1, c1, c2);
		opsubHardF.setIfExpressions(
				new PredicateExpression(true, opsubHardP, p1, c1, c2));
		opsubHardF.setRestrictions(
				new PredicateExpression(true, opsubP, p1, c1, c2));
		
		FormularHard opsupHardF = new FormularHard();
		opsupHardF.useCuttingPlaneInference(false);
		opsupHardF.setName("opsupHardF");
		opsupHardF.setForVariables(p1, c1, c2);
		opsupHardF.setIfExpressions(
				new PredicateExpression(true, opsupHardP, p1, c1, c2));
		opsupHardF.setRestrictions(
				new PredicateExpression(true, opsupP, p1, c1, c2));
		
		FormularHard psubsumesHardF = new FormularHard();
		
		psubsumesHardF.setName("psubsumesHardF");
		psubsumesHardF.setForVariables(p1, p2);
		psubsumesHardF.setIfExpressions(
				new PredicateExpression(true, psubsumesHardP, p1, p2));
		psubsumesHardF.setRestrictions(
				new PredicateExpression(true, psubsumesP, p1, p2));
		
		FormularHard pcomHardF = new FormularHard();
		pcomHardF.setName("pcomHardF");
		pcomHardF.setForVariables(p1, p2, p3);
		pcomHardF.setIfExpressions(
				new PredicateExpression(true, pcomHardP, p1, p2, p3));
		pcomHardF.setRestrictions(
				new PredicateExpression(true, pcomP, p1, p2, p3));
		
		// Formulars
		// =========================================
		// CONNECTION SOFT TO HIDDEN
		// =========================================
		FormularObjective subsumesEvidenceF = new FormularObjective();
		subsumesEvidenceF.setName("subsumesEvidenceF");
		subsumesEvidenceF.setForVariables(c1, c2, conf);
		subsumesEvidenceF.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, subsumesEvidenceP, c1,c2,conf),
				new ThresholdExpression(conf, false, threshold)
		);
		subsumesEvidenceF.setObjectiveExpression(
				new PredicateExpression(true, subsumesP, c1, c2));
		subsumesEvidenceF.setDoubleVariable(conf);
		
		FormularObjective intersectionEvidenceF = new FormularObjective();
		intersectionEvidenceF.setName("intersectionEvidenceF");
		intersectionEvidenceF.setForVariables(c1, c2, c3, conf);
		intersectionEvidenceF.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, conceptP, c3),
				new PredicateExpression(true, intersectionEvidenceP, c1, c2, c3, conf),
				new ThresholdExpression(conf, false, threshold)
		);		
		intersectionEvidenceF.setObjectiveExpression(
				new PredicateExpression(true, intersectionP, c1, c2, c3));
		intersectionEvidenceF.setDoubleVariable(conf);
		
		FormularObjective opsubEvidenceF = new FormularObjective();
		opsubEvidenceF.setName("opsubEvidenceF");
		opsubEvidenceF.setForVariables(c1, c2, p1, conf);
		opsubEvidenceF.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, objectPropertyP, p1),
				new PredicateExpression(true, opsubEvidenceP, p1, c1, c2, conf),
				new ThresholdExpression(conf, false, threshold)
		);
		opsubEvidenceF.setObjectiveExpression( 
				new PredicateExpression(true, opsubP, p1, c1, c2));
		opsubEvidenceF.setDoubleVariable(conf);
		
		FormularObjective opsupEvidenceF = new FormularObjective();
		opsupEvidenceF.setName("opsupEvidenceF");
		opsupEvidenceF.setForVariables(c1, c2, p1, conf);
		opsupEvidenceF.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, objectPropertyP, p1),
				new PredicateExpression(true, opsupEvidenceP, p1, c1, c2, conf),
				new ThresholdExpression(conf, false, threshold)
		);
		opsupEvidenceF.setObjectiveExpression( 
				new PredicateExpression(true, opsupP, p1, c1, c2));
		opsupEvidenceF.setDoubleVariable(conf);

		FormularObjective psubsumesEvidenceF = new FormularObjective();
		psubsumesEvidenceF.setName("psubsumesEvidenceF");
		psubsumesEvidenceF.setForVariables(p1, p2, conf);
		psubsumesEvidenceF.setIfExpressions(
				new PredicateExpression(true, objectPropertyP, p1),
				new PredicateExpression(true, objectPropertyP, p2), 
				new PredicateExpression(true, psubsumesEvidenceP, p1, p2, conf),
				new ThresholdExpression(conf, false, threshold)
		);
		psubsumesEvidenceF.setObjectiveExpression(
				new PredicateExpression(true, psubsumesP, p1, p2));
		psubsumesEvidenceF.setDoubleVariable(conf);
		
		FormularObjective pcomEvidenceF = new FormularObjective();
		pcomEvidenceF.setName("pcomEvidenceF");
		pcomEvidenceF.setForVariables(p1, p2, p3, conf);
		pcomEvidenceF.setIfExpressions(
				new PredicateExpression(true, objectPropertyP, p1),
				new PredicateExpression(true, objectPropertyP, p2), 
				new PredicateExpression(true, objectPropertyP, p3), 
				new PredicateExpression(true, pcomEvidenceP, p1, p2, p3, conf),
				new ThresholdExpression(conf, false, threshold)
		);
		pcomEvidenceF.setObjectiveExpression(
				new PredicateExpression(true, pcomP, p1, p2, p3));
		pcomEvidenceF.setDoubleVariable(conf);
		
		// =========================================
		// REASONING:
		// =========================================
		//every class subsumes itself
		FormularHard f1 = new FormularHard();
		f1.setName("F1");
		f1.setForVariables(c1, c2);
		f1.setIfExpressions(
			new PredicateExpression(true, conceptP, c1),
			new PredicateExpression(true, conceptP, c2),
			new EqualVariableExpression(c1, c2, true)
			);
		f1.setRestrictions(
			new PredicateExpression(true, subsumesP, c1, c2));
		
		//every class subsumes Thing
		FormularHard f2 = new FormularHard();
		f2.setName("F2");
		f2.setForVariables(c1, c2);
		f2.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new EqualStringExpression(c2, Constants.TOP_ELEMENT, true));
		f2.setRestrictions(
				new PredicateExpression(true, subsumesP, c1, c2));
		
		//transitivity of subsumption 
		FormularHard f3 = new FormularHard();
		f3.setName("F3");
		f3.setForVariables(c1, c2, c3);
		f3.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, conceptP, c3));
		f3.setRestrictions(
				new PredicateExpression(false, subsumesP, c1, c2),
				new PredicateExpression(false, subsumesP, c2, c3),
				new PredicateExpression(true, subsumesP, c1, c3));
		

		/*
		factor: for Class c1, Class c2, Class c3, Class c4
			if class(c1) & class(c2) & class(c3) & class(c4):
			subsumes(c1, c2) & subsumes(c1, c3) & intersection(c2, c3, c4) => subsumes(c1, c4);
		*/
		FormularHard f4 = new FormularHard();
		f4.setName("F4");
		f4.setForVariables(c1, c2, c3, c4);
		f4.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, conceptP, c3),
				new PredicateExpression(true, conceptP, c4));
		f4.setRestrictions(
				new PredicateExpression(false, subsumesP, c1, c2),
				new PredicateExpression(false, subsumesP, c1, c3),
				new PredicateExpression(false, intersectionP, c2, c3, c4),
				new PredicateExpression(true, subsumesP, c1, c4));
		
		/*
		factor: for Concept c1 , Concept c2, Concept c3, ObjectProperty p
			if concept(c1) & concept(c2) & concept(c3) & objectproperty(p):
			subsumes(c1, c2) & opsup(p, c3, c2) => opsup(p, c3, c1);
		*/
		FormularHard f5 = new FormularHard();
		f5.setName("F5");
		f5.setForVariables(c1, c2, c3, p1);
		f5.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, conceptP, c3),
				new PredicateExpression(true, objectPropertyP, p1));
		f5.setRestrictions(
				new PredicateExpression(false, subsumesP, c1, c2),
				new PredicateExpression(false, opsupP, p1, c3, c2),
				new PredicateExpression(true, opsupP, p1, c3, c1));
		
		/*
		//rsup(p1,c2,c1) and sub(c2,c3) and rsub(p1,c3,c4) => sub(c1,c4)
		*/
		FormularHard f6 = new FormularHard();
		f6.setName("F6");
		f6.setForVariables(c1, c2, c3, c4, p1);
		f6.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, conceptP, c3),
				new PredicateExpression(true, conceptP, c4),
				new PredicateExpression(true, objectPropertyP, p1));
		f6.setRestrictions(
				new PredicateExpression(false, opsupP, p1, c2, c1),
				new PredicateExpression(false, subsumesP, c2, c3),
				new PredicateExpression(false, opsubP, p1, c3, c4),
				new PredicateExpression(true, subsumesP, c1, c4));
		

		/*
		factor: for Concept c1, Concept c2, ObjectProperty r, ObjectProperty s
			if concept(c1) & concept(c2) & objectproperty(r) & objectproperty(s):
			opsup(r, c1, c2) & psub(r, s) => opsup(s, c1, c2);
		*/
		FormularHard f8 = new FormularHard();
		f8.setName("F8");
		f8.setForVariables(c1, c2, p1, p2);
		f8.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, objectPropertyP, p1),
				new PredicateExpression(true, objectPropertyP, p2));
		f8.setRestrictions(
				new PredicateExpression(false, opsupP, p1, c1, c2),
				new PredicateExpression(false, psubsumesP, p1, p2),
				new PredicateExpression(true, opsupP, p2, c1, c2));
		
		// !rsup(c1, p1, c2) or !rsup(p2, c3, c2) or !pcom( p1, p2, p3) or rsup(p3, c3, c1)
		FormularHard f9 = new FormularHard();
		f9.setName("F9");
		f9.setForVariables(c1,c2,c3,p1,p2,p3);
		f9.setIfExpressions(
				new PredicateExpression(true, conceptP, c1),
				new PredicateExpression(true, conceptP, c2),
				new PredicateExpression(true, conceptP, c3),
				new PredicateExpression(true, objectPropertyP, p1),
				new PredicateExpression(true, objectPropertyP, p2),
				new PredicateExpression(true, objectPropertyP, p3));
		f9.setRestrictions(
				new PredicateExpression(false, opsupP, c1, p1, c2),
				new PredicateExpression(false, opsupP, p2, c3, c2),
				new PredicateExpression(false, pcomP, p1, p2, p3),
				new PredicateExpression(true, opsupP, p3, c3, c1));
		
		//we don't want incoherent classes
		FormularHard f10 = new FormularHard();
		f10.setName("F10");
		f10.setForVariables(c1, c2);
		f10.setIfExpressions(
			new PredicateExpression(true, conceptP, c1),
			new PredicateExpression(true, conceptP, c2),
			new EqualStringExpression(c1, Constants.BOTTOM_ELEMENT, false),
			new EqualStringExpression(c2, Constants.BOTTOM_ELEMENT, true));
		f10.setRestrictions(
			new PredicateExpression(false, subsumesP, c1, c2));
		
		if(!useCuttingPlaneInference){
			f1.useCuttingPlaneInference(false);
			f2.useCuttingPlaneInference(false);
			f3.useCuttingPlaneInference(false);
			f4.useCuttingPlaneInference(false);
			f5.useCuttingPlaneInference(false);
			f6.useCuttingPlaneInference(false);
			f8.useCuttingPlaneInference(false);
			f9.useCuttingPlaneInference(false);
			f10.useCuttingPlaneInference(false);
		}
		
		
		// Model
		Model model = new Model(
				subsumesHardF, intersectionHardF, opsubHardF, opsupHardF, psubsumesHardF, pcomHardF,
				subsumesEvidenceF, intersectionEvidenceF, opsubEvidenceF, opsupEvidenceF, psubsumesEvidenceF, pcomEvidenceF,
				f1, f2, f3, f4, f5, f6,
				//f7, 
				f8, 
				f9,	
				f10
				);
		System.out.println(model.toString());
		return model;
	}
	
	public ArrayList<String> returnOntologyWithHighestProbability(Model model) throws ParseException, SolveException, SQLException, ReadOrWriteToFileException{
		LogFileWriter logFile = LogFileWriter.openNewLogFile("logFile.log");
		StandardGrounder grounder = new StandardGrounder();
		grounder.ground(model);
		System.out.println(new Date());
		StandardSolver solve = new StandardSolver(model);
		solve.setLogFileWriter(logFile);
		ArrayList<String> results = solve.solve();
		//EvaluationElement evaluation = new EvaluationElement(results, model);
		//System.out.println(evaluation.toString());
		System.out.println("Size of results " + results.size());
		return results;
	}
	
	
	/**
	 * 
	 * @param args sourceOntologyPath, targetOntologyPath, targetOntologyNameSpace
	 * @throws ParseException
	 * @throws SQLException
	 * @throws SolveException
	 * @throws ReadOrWriteToFileException
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void main(String[] args) throws ParseException, SQLException, SolveException, ReadOrWriteToFileException, OWLOntologyCreationException, OWLOntologyStorageException {
		if(args.length!=2 && args.length!=3){
			System.out.println("Start the reasoner with 2 or 3 arguments:");
			System.out.println("- existing filename of input ontology");
			System.out.println("- new filename of materialized output ontology");
			System.out.println("- reference Ontology / Gold standard (optional)");
			System.out.println();
			System.out.println("Example: elog -r \"data/input/ontology1.owl\" \"data/output/ontology1_reasoner.owl\"");
			System.out.println("Example: elog -r \"data/input/ontology2.owl\" \"data/output/ontology2_reasoner.owl\" \"data/input/goldStandard.owl\"");
		}else{
			long startTime = System.currentTimeMillis();
			System.out.println("====================================================");
			System.out.println("Read ontology from file: " + args[0]);
			System.out.println("====================================================");
			OWLReader reader = new OWLReader();
			IRI ontologyIRI = reader.read(args[0]);
			System.out.println("Successfully read in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			System.out.println("====================================================");
			System.out.println("Reason ontology: Get the most probable consistent ontology");
			System.out.println("====================================================");
			Reasoner reasoner = new Reasoner();
			//reasoner.setUseCuttingPlaneInference(false);
			Model model = reasoner.generateModel(
					reader.getObjectProperty(), 
					reader.getConcept(), 
					reader.getSubsumesEvidence(), 
					reader.getIntersectionEvidence(), 
					reader.getOpsubEvidence(), 
					reader.getOpsupEvidence(), 
					reader.getPcomEvidence(),
					reader.getPsubsumesEvidence(), 
					reader.getSubsumesHard(), 
					reader.getIntersectionHard(), 
					reader.getOpsubHard(), 
					reader.getOpsupHard(), 
					reader.getPsubsumesHard(),
					reader.getPcomHard());
			ArrayList<String> output = reasoner.returnOntologyWithHighestProbability(model);
			
			System.out.println("Successfully reasoned in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			
			
			System.out.println("====================================================");
			System.out.println("Write ontology");
			System.out.println("====================================================");
			OWLWriter writer = new OWLWriter();
			writer.write(ontologyIRI, args[1], output);
			System.out.println("Successfully written in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			
			if(args.length==3){
				System.out.println("====================================================");
				System.out.println("Evaluation");
				System.out.println("====================================================");
				Evaluator subsumesEvaluator = new Evaluator();
				subsumesEvaluator.setAxiomsToCheck(new SubClassAxiomsToCheck());
				subsumesEvaluator.evaluate(args[1], args[2]);
				System.out.println(subsumesEvaluator);
				
				Evaluator disjointEvaluator = new Evaluator();
				disjointEvaluator.setAxiomsToCheck(new DisjointClassAxiomsToCheck());
				disjointEvaluator.evaluate(args[1], args[2]);
				System.out.println(disjointEvaluator);
				
			}
		}
	}

	public void setUseCuttingPlaneInference(boolean useCuttingPlaneInference) {
		this.useCuttingPlaneInference = useCuttingPlaneInference;
	}

	public boolean isUseCuttingPlaneInference() {
		return useCuttingPlaneInference;
	}
	
}
