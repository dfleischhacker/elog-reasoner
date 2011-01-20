package de.elog.elSampler;

import java.io.File;
import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLFunctionalObjectPropertyAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSymmetricObjectPropertyAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;


public class OWLSamplingEventReader {
	
	private ArrayList<String> event0 = new ArrayList<String>();
	private ArrayList<String> event1 = new ArrayList<String>();
	
	/**
	 * Reads the ontology and transforms it to the basic EL++ axioms:
	 * 
	 * - SubClassOf(C1 C2)            -> subsumes
	 * - (C1 and C2) subclassof D    -> intersection
	 * - C1 subclassof (Exists R.C2) -> opsup
	 * - (Exists R.C1) subclassof D  -> opsub
	 * - R  subclassof D  -> opsub
	 * 
	 * - r subpropertyof s           -> psubsumes
	 * 
	 * @param ontologyFilePath
	 * @return ontology IRI
	 * @throws OWLOntologyCreationException
	 */
	public IRI read(String ontologyFilePath) throws OWLOntologyCreationException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		File file = new File(ontologyFilePath);
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		SamplingEventAxiomConverter converter = new SamplingEventAxiomConverter();
		
		// do not print the added axioms
		converter.setPrintAddedAxioms(true);
		System.out.println("Not converted axioms:");
		for(OWLAxiom axiom : ontology.getAxioms()){
			if(converter.addSubsumes(axiom, event1, event0)){
				// the add is already performed in the if part.
			}else if(converter.addIntersection(axiom, event1, event0)){
				// the add is already performed in the if part.				
			}else if(converter.addOpsub(axiom, event1, event0)){
				// the add is already performed in the if part.		
			}else if(converter.addOpsup(axiom, event1, event0)){
				// the add is already performed in the if part.		
			}else if(converter.addPsubsumes(axiom, event1, event0)){
				// the add is already performed in the if part.		
			}else if(converter.addPcom(axiom, event1, event0)){
				// the add is already performed in the if part.		
			}else{
				// data properties are not considered. Only object properties.
				// All annotation assertions do not contain important information for the ontology. Therefore, they are not considered.
				// The symmetric, transitive and functional object properties are already considered as "normal" object properties. Only the additional infromation about transitivity and functional is lost.
				if(!axiom.toString().contains("DataProperty") 
					&& !axiom.toString().contains("AnnotationAssertion")
					&& !(axiom instanceof OWLTransitiveObjectPropertyAxiomImpl)
					&& !(axiom instanceof OWLFunctionalObjectPropertyAxiomImpl)
					&& !(axiom instanceof OWLSymmetricObjectPropertyAxiomImpl)
				){
					System.out.println("- " + axiom);
				}
			}
		}
		OWLOntologyID id = ontology.getOntologyID();
		return id.getOntologyIRI();
	}

	public ArrayList<String> getEvent0() {
		return event0;
	}

	public ArrayList<String> getEvent1() {
		return event1;
	}
}
