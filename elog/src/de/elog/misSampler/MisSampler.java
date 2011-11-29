package de.elog.misSampler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


import com.clarkparsia.owlapi.explanation.PelletExplanation;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;



import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;


public class MisSampler {

	public static void main(String[] args) throws OWLOntologyCreationException {
		
		//the number of samples
		int numOfSamples = 100000;
		
		//number of explanations per incoherent class
		int numOfExplanations = 10;
		
		//path of the ontology file
		String filePath = args[args.length-1];
		
		//System.out.println(args[0]);
		
		switch (args.length) {
		
		case 1:
			break;
			
			case 2:
				if (args[0].startsWith("-s")) {
					numOfSamples = Integer.valueOf(args[0].substring(2));
				} else if (args[0].startsWith("-e")) {
					numOfExplanations = Integer.valueOf(args[0].substring(2));
				} else {
					System.err.println("Arguments -e or -s expected.");
					System.out.println("Arguments specific to the -sm reaosner:");
					System.out.println("-sNUMBER number of samples (optional)");
					System.out.println("-eNUMBER number of explanations per unsatisfiable class (optional)");
					System.out.println("filename of input ontology");
					System.out.println();
					System.out.println("Example: elog -sm \"data/input/ontology.owl\" ");
					System.out.println("Example: elog -sm -s1000 -e10 \"data/input/ontology.owl\"");
					return;
				}
				break;
				
			case 3:
				if (args[0].startsWith("-s")) {
					numOfSamples = Integer.valueOf(args[0].substring(2));
				} else if (args[0].startsWith("-e")) {
					numOfExplanations = Integer.valueOf(args[0].substring(2));
				} else {
					System.err.println("Arguments -e or -s expected.");
					System.out.println("Arguments specific to the -sm reaosner:");
					System.out.println("-sNUMBER number of samples (optional)");
					System.out.println("-eNUMBER number of explanations per unsatisfiable class (optional)");
					System.out.println("filename of input ontology");
					System.out.println();
					System.out.println("Example: elog -sm \"data/input/ontology1.owl\" ");
					System.out.println("Example: elog -sm -s1000 -e10 \"data/input/ontology2.owl\"");
					return;
				}
				if (args[1].startsWith("-s")) {
					numOfSamples = Integer.valueOf(args[1].substring(2));
				} else if (args[1].startsWith("-e")) {
					numOfExplanations = Integer.valueOf(args[1].substring(2));
				} else {
					System.err.println("Arguments -e or -s expected.");
					System.out.println("Arguments specific to the -sm reaosner:");
					System.out.println("-sNUMBER number of samples (optional)");
					System.out.println("-eNUMBER number of explanations per unsatisfiable class (optional)");
					System.out.println("filename of input ontology");
					System.out.println();
					System.out.println("Example: elog -sm \"data/input/ontology1.owl\" ");
					System.out.println("Example: elog -sm -s1000 -e10 \"data/input/ontology2.owl\"");
					return;
				}
				break;
				
			default:
				//error!
				System.out.println("Start the reasoner with 2, 3 or 4 arguments:");
				System.out.println("Arguments specific to the -sm reaosner:");
				System.out.println("-sNUMBER number of samples (optional)");
				System.out.println("-eNUMBER number of explanations per unsatisfiable class (optional)");
				System.out.println("filename of input ontology");
				System.out.println();
				System.out.println("Example: elog -sm \"data/input/ontology1.owl\" ");
				System.out.println("Example: elog -sm -s1000 -e10 \"data/input/ontology2.owl\"");
				return;
		}
		
		System.out.println("Starting axiom sampling process...");
		System.out.println("Number of samples: " + numOfSamples);
		System.out.println("Number of explanations per unsatisfiable class: " + numOfExplanations);
		
		if (!new File(filePath).exists())
		{
		   try {
			   throw new FileNotFoundException("File " + filePath + " does not exist.");
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		
		//built the file for the ontology to load
		String	file1	= "file:"+ filePath;
		
		MisSampler mSampler = new MisSampler();
		
		//the ontology manager (Pellet!)
		OWLOntologyManager manager = OWL.manager;
		
		System.out.println("Loading the axioms of the input ontology...");
		
		//load the first ontology WITH the annotations
		OWLOntology ontology1 = manager.loadOntology( IRI.create( file1 ) );
		
		//create the second ontology that only contains axioms without annotations
		OWLOntology ontology2 = manager.createOntology();
		
		//stores the axioms so be sampled
		ArrayList<OWLAxiom> sampleAxioms = new ArrayList<OWLAxiom>();
		
		//stores the "hard" axioms that we don't want to sample
		ArrayList<OWLAxiom> hardAxioms = new ArrayList<OWLAxiom>();
		
		//we need to built a set that counts the number of occurrences of the axioms in independent sets
		Hashtable<OWLAxiom,Integer> count = new Hashtable<OWLAxiom,Integer>();
		
		//stores the axioms with their confidence values
		Hashtable<OWLAxiom,Double> axiomConfidence = new Hashtable<OWLAxiom,Double>();
		
		//stores the axioms in the WeigtedAxiom format (for ranking purposes)
		ArrayList<SampleAxiom> axiomProbability = new ArrayList<SampleAxiom>();
		
		//stores the degree of each node
		Hashtable<OWLAxiom,Integer> degree = new Hashtable<OWLAxiom,Integer>();
		
		//the index for the conflicts (indexed by the axiom)
		HashMap<OWLAxiom, Set<Set<OWLAxiom>>> conflictIndex = new HashMap<OWLAxiom, Set<Set<OWLAxiom>>>();
					
		//iterate over all axioms in the loaded ontology
		Set<OWLAxiom> allAxioms = ontology1.getAxioms();
		for (OWLAxiom axiom : allAxioms) {
			
			//get the axiom without the annotation
			OWLAxiom axWithoutAnnotation = axiom.getAxiomWithoutAnnotations();
			//stores the confidence value of the current axiom
			Double confidence = 0.0;	
			//add to the sample sets only if it has confidence
			if ( (confidence = mSampler.getConfidenceValue(axiom)) != null) {
			
				//check if the axiom is already part of the ones to be sampled
				if (sampleAxioms.indexOf(axWithoutAnnotation) == -1) {
					//if not, add it to the set of axioms to be sampled
					sampleAxioms.add(axWithoutAnnotation);
					//set the count to zero
					count.put(axWithoutAnnotation, new Integer(0));
					//set the confidence to the given value
					axiomConfidence.put(axWithoutAnnotation, confidence);
				}
			} else {
				hardAxioms.add(axiom);
			}
			//add the axiom to the identical ontology without annotations
			manager.addAxiom(ontology2, axWithoutAnnotation);
		}
		
		System.out.println("Number of axioms to sample: " + sampleAxioms.size());
		
		//stores the conflicts as a set of axiom sets
		Set<Set<OWLAxiom>> conflictSet = new HashSet<Set<OWLAxiom>>();
		
		if (numOfExplanations >= 1) { 
		
			System.out.println("Computing set of unsatisfiable classes...");
					
			// Create the reasoner and load the ontology
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
			// Create an explanation generator
			PelletExplanation expGen = new PelletExplanation( reasoner );

			// Create the reasoner and load the ontology
			reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
			// Create an explanation generator
			expGen = new PelletExplanation( reasoner );

			//determine all incoherent classes in the ontology
			Set<OWLClass> incoherentClasses = reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom();
			System.out.println("There are " + incoherentClasses.size() + " unsatisfiable classes.");
			
			System.out.println("Computing minimial inconsistent subsets...");
			
			final long startTimeReasoning = System.nanoTime();
			final long endTimeReasoning;
			try {
		
				//iterate over all incoherent classes in the merged ontology
				for (OWLClass incoherentClass : incoherentClasses) {
					
					System.out.println("Computing explanations for unsatisfiable class " + incoherentClass + "...");
					
					Set<Set<OWLAxiom>> exp = expGen.getUnsatisfiableExplanations(incoherentClass, numOfExplanations);
					//iterate over all explanations found by the reasoner
					for (Set<OWLAxiom> axiomSet : exp) {
		
						//only keep the axioms that have a confidence value
						axiomSet.retainAll(sampleAxioms);
						System.out.println(axiomSet);

						//add the set of axioms (a conflict) to the set of conflicts
						conflictSet.add(axiomSet);
					}
				}

				//here we iterate over the conflict sets and remove 
				//conflicts of size 1 as long as there are only conflicts of size >= 2 left
				boolean somethingWasRemoved = true;
				while (somethingWasRemoved) {
					somethingWasRemoved = false;
					//only look at axioms that are not a conflict in themselves
					for (Set<OWLAxiom> axiomSet : conflictSet) {
						
						if (axiomSet.size() <= 1) {
							for (OWLAxiom currentAxiom : axiomSet) {
									sampleAxioms.remove(currentAxiom);
									somethingWasRemoved = true;
							}
							
							conflictSet.remove(axiomSet);
							break;
						}
					}

				}

				//here we iterate over the set one more time to store 
				//the degree of the axiom and the index of the axiom
				for (Set<OWLAxiom> axiomSet : conflictSet) {
					for (OWLAxiom currentAxiom : axiomSet) {

						//stores the degree of the axiom for statistics and convergence tests
						degree.put(currentAxiom, new Integer(0));
						
						//build an index that maps axioms to theiry conflicts
						Set<Set<OWLAxiom>> currentConflictSet = conflictIndex.get(currentAxiom);
						if (currentConflictSet != null) {
							currentConflictSet.add(axiomSet);
							conflictIndex.put(currentAxiom, currentConflictSet);
						} else {
							//build the emptys index here (will be filled later)
							Set<Set<OWLAxiom>> newConflictSet = new HashSet<Set<OWLAxiom>>();
							newConflictSet.add(axiomSet);
							conflictIndex.put(currentAxiom, newConflictSet);
						}
					}
				}
	
			} finally {
				  endTimeReasoning = System.nanoTime();
				}
			final long durationReasoning = endTimeReasoning - startTimeReasoning;
			
			System.out.println("...computation finished in " + (double)((double)durationReasoning/1000000000.0) + " seconds!");
			System.out.println(conflictSet.size() + " minimal inconsistent subsets found.");
			//System.out.println(conflictSet);
		
			System.out.println(conflictIndex);
		
			//stores the maximum size of an edge in the hypergraph
			int max_edge_size = 0;
			
			//stores the max degree of a vertex in the hypergraph
			int max_degree = 0;
			OWLAxiom maxDegreeAxiom = null;
				
			//compute the size of the largest hyperedge and the maximal degree of a vertex
			for (Set<OWLAxiom> axiomSet : conflictSet) {
									
				int sz = axiomSet.size();
				if (sz > max_edge_size) {
					max_edge_size = sz;
				}
				
				for (OWLAxiom vertex : axiomSet) {
	
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
	
			//stores one individual sample
			HashSet<OWLAxiom> sample = new HashSet<OWLAxiom>();
			
			//burn in iterations
			int burn_in = 10;
			
			System.out.println("Sampling process started...");
			
			final long startTimeSampling = System.nanoTime();
			final long endTimeSampling;
			try {
	
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
						//sample does NOT contain candidate
						//stores the unique conflict (edge in the HG) that the candidate causes
						Set<OWLAxiom> uniqueConflict = new HashSet<OWLAxiom>();
						
						//add the element to the sample set
						sample.add(candidate);
						
						//check whether the solution is actually an independent set
						boolean independentSet = true;
						//number of conflicts created by adding the candidate
						int numOfConflicts = 0;
						
						//iterate over all conflict sets (edges in the HG)
						if (conflictIndex.containsKey(candidate)) {
							for (Set<OWLAxiom> currentMIS : conflictIndex.get(candidate)) {
																	
								//System.out.println(conflictIndex.get(candidate).size());
								
								//check if the current sample violates the independent set property
								if (sample.containsAll(currentMIS)) {
									independentSet = false;
									numOfConflicts++;
									
									//if we have more than one conflict, we can stop here
									if (numOfConflicts > 1) {
										break;
									}
									
									//uniqueConflict is the edge that would cause a unique conflict without the candidate
									uniqueConflict = new HashSet<OWLAxiom>(currentMIS);
									uniqueConflict.remove(candidate);
								}
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
											//remove that axiom
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
						for (OWLAxiom sAx : sample) {
							count.put(sAx, new Integer(count.get(sAx)+1));
						}
					}
				}// end for loop
			
			} finally {
				endTimeSampling = System.nanoTime();
			}
			final long durationSampling = endTimeSampling - startTimeSampling;
			
			System.out.println("Sampling process finished in " + (double)((double)durationSampling/1000000000.0) + " seconds.");
			
			//compute the probabilities form the generated samples
			for (OWLAxiom currentAxiom : axiomConfidence.keySet()) {
				axiomProbability.add(new SampleAxiom(currentAxiom, (double)count.get(currentAxiom)/(double)(numOfSamples-burn_in)));
			}
		
		} else {
			
			//compute the probabilities form the generated samples
			for (OWLAxiom currentAxiom : axiomConfidence.keySet()) {
				double tau = Math.exp(axiomConfidence.get(currentAxiom));
				double p = tau / (1 + tau);
				axiomProbability.add(new SampleAxiom(currentAxiom, p));
			}	
		}
		
		//sort the axioms using the probabilities
        Collections.sort(axiomProbability);
        
        //we need the factory to build annotations for the axioms
        OWLDataFactory factory = manager.getOWLDataFactory();
        
        //create the second ontology that only contains axioms without annotations
      	OWLOntology outputOntology = manager.createOntology();
        
      	//the annotation property we will use for the probabilities
      	OWLAnnotationProperty annotationProbability = factory.getOWLAnnotationProperty(IRI.create("http://elog#probability"));
      	
      	//iterate over the sorted axioms with their probability
        for (int i = 0; i < axiomProbability.size(); i++) {
        	
        	SampleAxiom wAxiom = axiomProbability.get(i);
        	
        	//build an axiom with the annotation "probability"
        	OWLAnnotation b = factory.getOWLAnnotation(annotationProbability, factory.getOWLLiteral(wAxiom.getWeight()));
        	HashSet<OWLAnnotation> annotationSet = new HashSet<OWLAnnotation>();
        	annotationSet.add(b);
        	OWLAxiom annotatedAxiom = wAxiom.getAxiom().getAnnotatedAxiom(annotationSet);
        	manager.addAxiom(outputOntology, annotatedAxiom);
        	//print the output of the samplet
        	System.out.println(wAxiom.getAxiom().toString() + "   " + wAxiom.getWeight());
        }
        
        for (OWLAxiom axiom : hardAxioms) {
        	manager.addAxiom(outputOntology, axiom);
        }
        
        //store the probabilities in an OWL file
        File file = new File("data/output/test.owl");
        try {
			manager.saveOntology(outputOntology, IRI.create(file.toURI()));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
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
