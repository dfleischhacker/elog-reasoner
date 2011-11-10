package de.elog.misSampler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;



import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;


public class MisSampler {

	public static void main(String[] args) throws OWLOntologyCreationException {
		
		if (args.length != 1 ) {
			
			System.out.println("Start the reasoner with 2 or 3 arguments:");
			System.out.println("- existing filename of input ontology");
			//System.out.println("- new filename of materialized output ontology");
			//System.out.println("- reference Ontology / Gold standard (optional)");
			System.out.println();
			System.out.println("Example: elog -ms \"data/input/ontology1.owl\" ");
			System.out.println("Example: elog -ms \"data/input/ontology2.owl\" ");
			
		} else {
			
			System.out.println("start program");
			
			MisSampler mSampler = new MisSampler();
			
			OWLOntologyManager manager = OWL.manager;
			
			//built the file for the ontology to load
			String	file1	= "file:"+ args[0];
			
			//load the first ontology WITH the annotations
			OWLOntology ontology1 = manager.loadOntology( IRI.create( file1 ) );
			
			//create the second ontology that only contains axioms without annotations
			OWLOntology ontology2 = manager.createOntology();
			
			//stores the axioms so be sampled
			ArrayList<OWLAxiom> sampleAxioms = new ArrayList<OWLAxiom>();
			
			//we need to built a set that counts the number of occurrences of the axioms in independent sets
			Hashtable<OWLAxiom,Integer> count = new Hashtable<OWLAxiom,Integer>();
			
			//stores the axioms with their confidence values
			Hashtable<OWLAxiom,Double> axiomConfidence = new Hashtable<OWLAxiom,Double>();
			
			//stores the axioms in the WeigtedAxiom format (for ranking purposes)
			ArrayList<SampleAxiom> axiomProbability = new ArrayList<SampleAxiom>();
			
			//stores the degree of each node
			Hashtable<OWLAxiom,Integer> degree = new Hashtable<OWLAxiom,Integer>();
						
			//load all axioms with a confidence value
			Set<OWLAxiom> allAxioms = ontology1.getAxioms();
			Iterator<OWLAxiom> axiomIter = allAxioms.iterator();
			while (axiomIter.hasNext() ){
				OWLAxiom axiom = axiomIter.next();
				//if the axioms has a confidence value, add it to the sample axioms
				
				OWLAxiom axWithoutAnnotation = axiom.getAxiomWithoutAnnotations();
				//System.out.println(axWithoutAnnotation);
				
				//add to the sample sets only if it has confidence
				if (  mSampler.getConfidenceValue(axiom) != null) {
					double confidence = mSampler.getConfidenceValue(axiom);
									
					if (sampleAxioms.indexOf(axWithoutAnnotation) == -1) {
						sampleAxioms.add(axWithoutAnnotation);
						count.put(axWithoutAnnotation, new Integer(0));
						axiomConfidence.put(axWithoutAnnotation, confidence);
					}
				}

				manager.addAxiom(ontology2, axWithoutAnnotation);
			}
			
			System.out.println("Number of sample Axioms: " + sampleAxioms.size());
			
			
			// Create the reasoner and load the ontology
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
			// Create an explanation generator
			PelletExplanation expGen = new PelletExplanation( reasoner );
			
			//stores the conflicts as a set of axiom sets
			Set<Set<OWLAxiom>> conflictSet = new HashSet<Set<OWLAxiom>>();
			
			// Create the reasoner and load the ontology
			reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
			// Create an explanation generator
			expGen = new PelletExplanation( reasoner );
					
			
			//determine all incoherent classes in the ontology
			Set<OWLClass> incoherentClasses = reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom();
			System.out.println("There are " + incoherentClasses.size() + " unsatisfiable concepts.");
			
			//System.exit(0);
			
			System.out.println("Computing minimial inconsistent subsets...");
			
			
			//iterate over all incoherent classes in the merged ontology
			Iterator<OWLClass> incohClassIter = incoherentClasses.iterator();
			while (incohClassIter.hasNext() ){
				//incoherent class
				OWLClass incoh = incohClassIter.next();
				
				System.out.println("Computing explanations for incoherent class " + incoh + "...");
				
				//Set<OWLAxiom> exp = expGen.getUnsatisfiableExplanation(incoh);
				Set<Set<OWLAxiom>> exp = expGen.getUnsatisfiableExplanations(incoh, 5);
				Iterator<Set<OWLAxiom>> explanationSet = exp.iterator();
				while ( explanationSet.hasNext() ) {
					Set<OWLAxiom> axiomSet = explanationSet.next();
					//axiomSet.retainAll(sampleAxioms);
					//System.out.println(axiomSet);
					
					Iterator<OWLAxiom> axiomIterator = axiomSet.iterator();
					while ( axiomIterator.hasNext() ) {
						OWLAxiom currentAxiom = axiomIterator.next();
						degree.put(currentAxiom, new Integer(0));
						
						//remove axiom from ontology if it is the only one
						if (axiomSet.size() <= 1) {
							manager.removeAxiom(ontology2, currentAxiom);
						}
						
					}
					
					//add the set of axioms (a conflict) to the set of conflicts
					conflictSet.add(axiomSet);
				}
			}
			
			System.out.println("...computation finished!");
			System.out.println(conflictSet.size() + " MIS found.");
			//System.out.println(conflictSet);
			
			//stores the maximum size of an edge in the hypergraph
			int max_edge_size = 0;
			
			//stores the max degree of a vertex in the hypergraph
			int max_degree = 0;
			OWLAxiom maxDegreeAxiom = null;
			Iterator<Set<OWLAxiom>> MIS = conflictSet.iterator();
			while ( MIS.hasNext() ) {
				
				Set<OWLAxiom> axiomSet = MIS.next();
				
				int sz = axiomSet.size();
				if (sz > max_edge_size) {
					max_edge_size = sz;
				}
				
				
				Iterator<OWLAxiom> conflictMember = axiomSet.iterator();
				while ( conflictMember.hasNext() ) {
					OWLAxiom vertex = conflictMember.next();
					int new_degree = degree.get(vertex)+1;
					if (new_degree > max_degree) { 
						max_degree = new_degree;
						maxDegreeAxiom = vertex;
					}
					degree.put(vertex, new Integer(new_degree));
				}
				
			}
			
			
			System.out.println("Maximum edge size: " + max_edge_size);
			System.out.println("Maximum degree: " + max_degree + "  axiom: " + maxDegreeAxiom);
		
			
			//generate random number between 0 and size of correpsondences - 1
			
			Set<OWLAxiom> sample = new HashSet<OWLAxiom>();
			
			//burn in iterations
			int burn_in = 1000;
			//the number of samples
			int numOfSamples = 100000;
			
			System.out.println("Sampling process started...");
			
			//take numOfSamples samples
			for (int s = 0; s < numOfSamples; s++) {
				
				//System.out.println("Sample number: " + s);
				
				//pick a random element (correspondence)
				int item = new Random().nextInt(sampleAxioms.size());
				OWLAxiom candidate = sampleAxioms.get(item);
				
				//check if the sample already contains the candidate
				if (sample.contains(candidate)) {
					
					//remove the candidate with probability 1 / (1 + t)
					double r = new Random().nextDouble();
					if (r <= (1 / (1 + Math.exp(axiomConfidence.get(candidate))))) {
						sample.remove(candidate);
					}
					
				} else {
					//sample is does NOT contain candidate
					//stores the unique conflict (edge in the HG) that the candidate causes
					Set<OWLAxiom> uniqueConflict = new HashSet<OWLAxiom>();
					
					//add the element to the sample set
					sample.add(candidate);
					
					//check whether the solution is actually an independent set
					boolean independentSet = true;
					//number of conflicts created by adding the candidate
					int numOfConflicts = 0;
					
					//iterate over all conflict sets (edges in the HG)
					MIS = conflictSet.iterator();
					while ( MIS.hasNext() ) {
						
						//the current MIS we are investigating (edge in the HG)
						Set<OWLAxiom> currMIS = MIS.next();
						
						//check if the current sample violates the independent set property
						if (sample.containsAll(currMIS)) {
							independentSet = false;
							numOfConflicts++;
							
							//if we have more than one conflict, we can stop here
							if (numOfConflicts > 1) {
								break;
							}
							
							//uniqueConflict is the edge that would cause a unique conflict without the candidate
							uniqueConflict = new HashSet<OWLAxiom>(currMIS);
							uniqueConflict.remove(candidate);
						}
					}
										
					//it is NOT an independent set, i.e., there's a conflict
					if (!independentSet) {
						
					//we have exactly one conflict
						if (numOfConflicts == 1 && (uniqueConflict.size() > 0)) {
							double r = new Random().nextDouble();
							double tau = Math.exp(axiomConfidence.get(candidate));
							double m = (double)max_edge_size;
							double p = ((m-1)*tau) / (2*m*(tau+1));
							
							//with probability p choose an axiom in the conflict uniformly at random and remove it
							if (r <= p) {
							
								//choose a random number between 0 and the size of the unique conflict minus 1
								int n = new Random().nextInt(uniqueConflict.size());
								//use an iterator and a variable (equal) to choose the axiom at random
								Iterator<OWLAxiom> axm = uniqueConflict.iterator();
								int equal = 0;
								while ( axm.hasNext() ) {
									OWLAxiom axiom = axm.next();
									if (equal == n) {
										//remove than axiom
										sample.remove(axiom);
										break;
									}
									equal++;
								}
							} else {
								//we have that r > p and, thus, we remove the candidate (i.e., we leave the sample as it is)
								sample.remove(candidate);
							}						
							
						} else {
							//we have more than one conflict caused by the candidate 
							//and, thus, we remove the candidate (i.e., we leave the sample as it is)
							sample.remove(candidate);
						}
						
						
					} else {
						//no conflict, with probability t / (1 + t) we add the candidate (i.e., do not remove the candidate)
						double r = new Random().nextDouble();
						double tau = Math.exp(axiomConfidence.get(candidate));
						if (r > (tau / (1 + tau))) {
							sample.remove(candidate);
						}
					}
					
				}
				
				//update counts (after burn-in iterations)
				if (s >= burn_in) {
					for (int i = 0; i < sampleAxioms.size(); i++) {
						
						if (sample.contains(sampleAxioms.get(i))) {
						
							OWLAxiom currAx = sampleAxioms.get(i);
							count.put(sampleAxioms.get(i), new Integer(count.get(currAx)+1));
						}
					}
				}
			}
			
			System.out.println("Sampling process finished.");
			
			
			for (int i = 0; i < sampleAxioms.size(); i++) {
				
				OWLAxiom currAx = sampleAxioms.get(i);
				axiomProbability.add(new SampleAxiom(currAx, (double)count.get(currAx)/(double)(numOfSamples-burn_in)));
			}	
			
			
	        Collections.sort(axiomProbability);
	        
	        for (int i = 0; i < axiomProbability.size(); i++) {
	        	SampleAxiom wAxiom = axiomProbability.get(i);
	        	System.out.println(wAxiom.getAxiom().toString() + "   " + wAxiom.getWeight());
	        }
		}
		
	}
	
	
	/**
	 * Gets the confidence value via the "confidence" annotation property. 
	 * 
	 * If no confidence value exist, the function returns "null".
	 * 
	 * @param axiom
	 * @return
	 */
	public Double getConfidenceValue(OWLAxiom axiom){
		for(OWLAnnotation annotation : axiom.getAnnotations()){
			if(annotation.getProperty().getIRI().getFragment().toString().equalsIgnoreCase(
					Constants.ANNOTATION_PROPERTY_FOR_REASONING_CONFIDENCE_VALUE)){
				OWLAnnotationValue annValue = annotation.getValue();
				if(annValue instanceof OWLLiteralImpl){
					OWLLiteral literalValue = (OWLLiteral) annValue;
					if(literalValue.isDouble()){
						return literalValue.parseDouble();
					}					
				}
			}			
		}
		return null;
	}
	
}
