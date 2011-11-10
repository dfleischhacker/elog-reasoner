package de.elog.pelletReasoner;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import de.elog.evaluator.DisjointClassAxiomsToCheck;
import de.elog.evaluator.Evaluator;
import de.elog.evaluator.SubClassAxiomsToCheck;
import de.unima.conn.ilpSolver.GurobiConnector;
import de.unima.exception.ParseException;
import de.unima.exception.ReadOrWriteToFileException;
import de.unima.exception.SolveException;


public class Reasoner {
	


	private static OWLOntology getOntologyWithHighestProbability(OWLReader reader) throws OWLOntologyCreationException, SQLException, ParseException, SolveException{

		//initialize Pellet's explanation engine
		PelletExplanation.setup();
		
		// Create an OWLAPI manager that allows to load an ontology file and
		// create OWLEntities
		OWLOntologyManager manager = OWL.manager;
		
		HashMap<String, OwnAxiom> axioms = reader.getAxioms();
		
			
		//initially load the ontology
		HashSet<OWLAxiom> ontAxioms = new HashSet<OWLAxiom>();
		for(OwnAxiom a : axioms.values()){
			ontAxioms.add(a.getAxiomWithoutAnnotation());
		}
		OWLOntology ontology = manager.createOntology(ontAxioms);
		
		// initialize the ILP
		GurobiConnector connector = GurobiConnector.getGurobiConnector();
		for(OwnAxiom a : axioms.values()){
			if(a.isHard()){
				connector.addHardConstraint(new String[]{a.getId()}, new boolean[]{true});
			}else{
				connector.updateObjectiveVariable(a.getId(), a.getConfidenceValue());
			}
		}
		ArrayList<String> result = connector.solve();
		System.err.println("Siiiiiiize of ress ONE :" + result.size());
		
		Set<Set<OWLAxiom>> conflictSet = new HashSet<Set<OWLAxiom>>();
		int counter = 0;
		do {
			// Create the reasoner and load the ontology
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
			// Create an explanation generator
			PelletExplanation expGen = new PelletExplanation( reasoner );
			
			//stores the conflicts as a set of axiom sets
			conflictSet = new HashSet<Set<OWLAxiom>>();
			
			//determine all incoherent classes in the ontology
			Set<OWLClass> incoherentAxioms = reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom();
			System.out.println("There are " + incoherentAxioms.size() + " unsatisfiable concepts.");
			
			//iterate over all incoherent classes in the merged ontology
			Iterator<OWLClass> flavoursIter = incoherentAxioms.iterator();
			while (flavoursIter.hasNext() ){
				//incoherent class
				OWLClass incoh = flavoursIter.next();
				
				//System.out.println("-----------> " + incoh + " <-----------");
				
				//Set<OWLAxiom> exp = expGen.getUnsatisfiableExplanation(incoh);
				Set<Set<OWLAxiom>> exp = expGen.getUnsatisfiableExplanations(incoh, 5);
				Iterator<Set<OWLAxiom>> explanationSet = exp.iterator();
				while ( explanationSet.hasNext() ) {
					Set<OWLAxiom> axiomSet = explanationSet.next();
					//exp.retainAll(alignment);
					//axiomSet.retainAll(alignment.keySet());
					//System.out.println(axiomSet);
					
					//add the axioms as ILP constraint
					TreeSet<String> variableNames = new TreeSet<String>();
					for(OWLAxiom a : axiomSet){
						variableNames.add(a.toString());
						if(!axioms.containsKey(a.toString())) {
							System.err.println("Axiom " + a.toString() + " of conflict set not found in original axioms.");
						}
					}
					connector.addCardinalityConstraint(variableNames, (variableNames.size()-1));
					
					//add the set of axioms (a conflict) to the set of conflicts
					conflictSet.add(axiomSet);
				}
			}
			System.out.println("Still " + conflictSet.size() + " conflicts remaining.");
			if(conflictSet.size()>0){
				result = connector.solve();
				System.err.println("Size of actual Result set :" + result.size());
				ontAxioms = new HashSet<OWLAxiom>();
				for(String r : result){
					OwnAxiom ownAxiom = axioms.get(r);
					if(ownAxiom!=null){
						ontAxioms.add(ownAxiom.getAxiomWithoutAnnotation());
					}else{
						System.err.println("Axiom " +r + " of ilp solution not found in original ont.");
					}
					
				}
				
				ontology = manager.createOntology(ontAxioms);
			}
			counter ++;
			System.out.println("Finished cutting plane loop number " + counter);
		} while (conflictSet.size()>0);
		return ontology;
		
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
			System.out.println("Example: elog -r2 \"data/input/ontology1.owl\" \"data/output/ontology1_reasoner.owl\"");
			System.out.println("Example: elog -r2 \"data/input/ontology2.owl\" \"data/output/ontology2_reasoner.owl\" \"data/input/goldStandard.owl\"");
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
			OWLOntology resultOntology = Reasoner.getOntologyWithHighestProbability(reader);
			
			System.out.println("Successfully reasoned in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			
			
			System.out.println("====================================================");
			System.out.println("Write ontology");
			System.out.println("====================================================");
			reader.write(resultOntology, args[1]);			
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

	
}
