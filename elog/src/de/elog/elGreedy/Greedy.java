
package de.elog.elGreedy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.semanticweb.owlapi.model.IRI;

import de.elog.evaluator.DisjointClassAxiomsToCheck;
import de.elog.evaluator.Evaluator;
import de.elog.evaluator.SubClassAxiomsToCheck;


public class Greedy {
	
	private Ontology m_ontology;
	private HashMap<OwnAxiom, Double> axioms;
	ArrayList<String> addedAxioms = new ArrayList<String>();

	
	public Greedy(HashMap<OwnAxiom, Double> softAxioms, ArrayList<OwnAxiom> hardAxioms, String filenameOfNewOnt, IRI iriofNewOnt) throws Exception {

		// Now create the ontology - we use the ontology IRI (not the physical URI)
		m_ontology = new Ontology();
		m_ontology.create(new File(filenameOfNewOnt), iriofNewOnt);
		
		// the soft axioms are those which will be used in the greedy method later.
		axioms = softAxioms;
		
		// the hard axioms are put initially into the ontology and in the string list of hard axioms for evaluation.
		System.out.println("Added " + hardAxioms.size() + " hard axioms.");
		addedAxioms = new ArrayList<String>();
		for(OwnAxiom hardAxiom : hardAxioms){
			addedAxioms.addAll(hardAxiom.getVariableNames());
			m_ontology.addAxiom(hardAxiom.getAxiom());
		}
		
	}

	
	public ArrayList<String> greedy() throws Exception {

		
		List<OwnAxiom> sortedAxioms = this.sort(axioms);
		
		System.out.println( "axioms: "+ axioms.size() );
		int i=0;
		long iCheckTime = 0;
		double sum = 0;
		for( OwnAxiom axiom: sortedAxioms )
		{
			for(String s :axiom.getVariableNames()){
				addedAxioms.add(s);
			}
			m_ontology.addAxiom( axiom.getAxiom() );
			
			
			long iCheckStart = System.currentTimeMillis();
			boolean bCoherent = m_ontology.isCoherent();
			long iCheckEnd = System.currentTimeMillis();
			iCheckTime += ( iCheckEnd - iCheckStart );
			
			if( !bCoherent )
			{
				
				//System.out.println( "remove ("+ i +"): "+ axiom.getAxiom() );
				m_ontology.removeAxiom( axiom.getAxiom() );
				addedAxioms.removeAll(axiom.getVariableNames());
			}else{
				System.out.println("add ("+i+"): " + axiom.getValue()+" "+axiom.getAxiom());
				m_ontology.removeAnnotations(axiom.getAxiom());
				sum=sum+axiom.getValue();
			}
			i++;
		}
		System.out.println( "axioms: "+ i );
		System.out.println("objective: "+ sum);
		m_ontology.save();
		return addedAxioms;
	}
	
	private List<OwnAxiom> sort( HashMap<OwnAxiom,Double> hmAxioms ){
		// Random ordering
		Random r = new Random();
		for(OwnAxiom a : hmAxioms.keySet()){
			double newValue = hmAxioms.get(a)+(r.nextDouble()*0.0001);
			a.setValue(newValue);
			hmAxioms.put(a, newValue);
		}
		
		List<OwnAxiom> axioms = new ArrayList<OwnAxiom>( hmAxioms.keySet() );
		Collections.sort( axioms, new AxiomComparator( hmAxioms ) );
		return axioms;
	}
	
	public class AxiomComparator implements Comparator<OwnAxiom> 
	{
		private HashMap<OwnAxiom,Double> hmAxioms;
		
		public AxiomComparator( HashMap<OwnAxiom,Double> hmAxioms ){
			this.hmAxioms = hmAxioms;
		}
		// TODO: ascending or descending order
		public int compare( OwnAxiom axiom1, OwnAxiom axiom2 ){
			Double d1 = hmAxioms.get( axiom1 );
			Double d2 = hmAxioms.get( axiom2 );
			return Double.compare( d2, d1 );
		}
		public boolean equals( Object object ){
			return false;
		}
	}
	
	
	
	public static void main( String args[] ) throws Exception {
		if(args.length!=2 && args.length!=3){
			System.out.println("Start the greedy algorithm with 2 or 3 arguments:");
			System.out.println("- existing filename of input ontology");
			System.out.println("- new filename of materialized output ontology");
			System.out.println("- filename of gold standard (optional)");
			System.out.println();
			System.out.println("Example: elog -g \"data/input/ontology1.owl\" \"data/output/ontology1_greedy.owl\"");
			System.out.println("Example: elog -g \"data/input/ontology2.owl\" \"data/output/ontology2_greedy.owl\" \"data/input/goldStandard.owl\"");
		}else{
			long startTime = System.currentTimeMillis();
			System.out.println("====================================================");
			System.out.println("Read ontology from file: " + args[0]);
			System.out.println("====================================================");
			OWLGreedyReader reader = new OWLGreedyReader();
			IRI ontologyIRI = reader.read(args[0]);
			System.out.println("Successfully read in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			System.out.println("====================================================");
			System.out.println("Greedy ontology: Get the most probable consistent ontology.");
			System.out.println("====================================================");
			Greedy greedy = new Greedy(reader.getSoftAxioms(), reader.getHardAxioms(),args[1],ontologyIRI);
			
			greedy.greedy();
			
			System.out.println("Successfully reasoned in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			
			
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
