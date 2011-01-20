package de.elog.elReasoner;

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


public class OWLReader {

	private ArrayList<String[]> objectProperty = new ArrayList<String[]>(); // Done
	private ArrayList<String[]> concept = new ArrayList<String[]>(); // done
	
	private ArrayList<String[]> subsumesEvidence = new ArrayList<String[]>(); // done
	private ArrayList<String[]> intersectionEvidence = new ArrayList<String[]>(); // partly done
		// Exists R.C subclassOf D : Range und suBBB
	private ArrayList<String[]> opsubEvidence = new ArrayList<String[]>(); 
	// D subclassOf Exists R.C : suPPP
	private ArrayList<String[]> opsupEvidence = new ArrayList<String[]>();
	private ArrayList<String[]> psubsumesEvidence = new ArrayList<String[]>();
	private ArrayList<String[]> pcomEvidence = new ArrayList<String[]>();


	private ArrayList<String[]> subsumesHard = new ArrayList<String[]>();
	private ArrayList<String[]> intersectionHard = new ArrayList<String[]>();
	private ArrayList<String[]> opsubHard = new ArrayList<String[]>();
	private ArrayList<String[]> opsupHard = new ArrayList<String[]>();
	private ArrayList<String[]> psubsumesHard = new ArrayList<String[]>();
	private ArrayList<String[]> pcomHard = new ArrayList<String[]>();

	
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
		
		AxiomConverter converter = new AxiomConverter();
		
		// add bottom and top concepts
		converter.addBottomConcept(concept);
		converter.addTopConcept(concept);
		
		// do not print the added axioms
		converter.setPrintAddedAxioms(false);
		System.out.println("Not converted axioms:");
		for(OWLAxiom axiom : ontology.getAxioms()){
			if(converter.addConcept(axiom, concept)){
				// the add is already performed in the if part.
			}else if(converter.addObjectProperty(axiom, objectProperty)){
				// the add is already performed in the if part.				
			}else if(converter.addSubsumes(axiom, subsumesHard, subsumesEvidence)){
				// the add is already performed in the if part.				
			}else if(converter.addIntersection(axiom, intersectionHard, intersectionEvidence)){
				// the add is already performed in the if part.				
			}else if(converter.addOpsub(axiom, opsubHard, opsubEvidence)){
				// the add is already performed in the if part.		
			}else if(converter.addOpsup(axiom, opsupHard, opsupEvidence)){
				// the add is already performed in the if part.		
			}else if(converter.addPsubsumes(axiom, psubsumesHard, psubsumesEvidence)){
				// the add is already performed in the if part.		
			}else if(converter.addPcom(axiom, pcomHard, pcomEvidence)){
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
		System.out.println("Number of subsumes axioms: hard " + this.getSubsumesHard().size() + " soft " + this.getSubsumesEvidence().size());
		System.out.println("Number of intersection axioms: hard " + this.getIntersectionHard().size() + " soft " + this.getIntersectionEvidence().size());
		System.out.println("Number of opsub axioms: hard " + this.getOpsubHard().size() + " soft " + this.getOpsubEvidence().size());
		System.out.println("Number of opsup axioms: hard " + this.getOpsupHard().size() + " soft " + this.getOpsupEvidence().size());
		System.out.println("Number of psubsumes axioms: hard " + this.getPsubsumesHard().size() + " soft " + this.getOpsupEvidence().size());
		System.out.println("Number of pcom axioms: hard " + this.getPcomHard().size() + " soft " + this.getPcomEvidence().size());
		return id.getOntologyIRI();
	}
	
	/**
	 * Returns all object properties.
	 * 
	 * @return property stored at position 0 of the String Array
	 */
	public ArrayList<String[]> getObjectProperty() {
		return objectProperty;
	}

	/**
	 * Returns all concepts.
	 * 
	 * @return concept stored at position 0 of the String Array
	 */
	public ArrayList<String[]> getConcept() {
		return concept;
	}

	/**
	 * Returns SubClassOf(C1 C2) (subsumes)
	 * 
	 * @return C1 at position 0, D at position 1, evidence value at position 2 of the array.
	 */
	public ArrayList<String[]> getSubsumesEvidence() {
		return subsumesEvidence;
	}


	/**
	 * Returns SubClassOf(IntersectionOf(C1 C2) D). (intersection)
	 * 
	 * Also deals with: DisjointClasses(<http://confOf#City> <http://confOf#Topic>) 
	 *  
	 * @return C1 at position 0, C2 at position 1, D at position 2, evidence value at position 3.
	 */
	public ArrayList<String[]> getIntersectionEvidence() {
		return intersectionEvidence;
	}

	/**
	 * SubClassOf(ObjectSomeValuesFrom(R C1) D)  -> opsub
	 * 
	 * Also deals with ObjectPropertyRange(R D).
	 * 
	 * @return R at position 0, C1 at position 1, D at position 2, evidence value at position 3 of the array.
	 */
	public ArrayList<String[]> getOpsubEvidence() {
		return opsubEvidence;
	}
	
	/**
	 * Returns SubClassOf(C1 ObjectSomeValuesFrom(R C2))
	 *  
	 * @return R at position 0, C2 at position 1, C1 at position 2, evidence value at position 3 of the array.
	 */
	public ArrayList<String[]> getOpsupEvidence() {
		return opsupEvidence;
	}

	/**
	 * SubObjectPropertyOf(R S). (psubsumes)
	 * 
	 * @return r at position 0, s at position 1, evidence value at position 2 of the array.
	 */
	public ArrayList<String[]> getPsubsumesEvidence() {
		return psubsumesEvidence;
	}
	/**
	 * Returns SubClassOf(C1 C2) (subsumes)
	 * 
	 * @return C1 at position 0, D at position 1 of the array.
	 */
	public ArrayList<String[]> getSubsumesHard() {
		return subsumesHard;
	}
	/**
	 * Returns SubClassOf(IntersectionOf(C1 C2) D). (intersection)
	 * 
	 * Also deals with: DisjointClasses(<http://confOf#City> <http://confOf#Topic>) 
	 *  
	 * @return C1 at position 0, C2 at position 1, D at position 2 of the array.
	 */
	public ArrayList<String[]> getIntersectionHard() {
		return intersectionHard;
	}
	/** 
	 * SubClassOf(ObjectSomeValuesFrom(R C1) D)  -> opsub
	 * 
	 * Also deals with ObjectPropertyRange(R D).
	 * 
	 * @return R at position 0, C1 at position 1, D at position 2 of the array.
	 */	
	public ArrayList<String[]> getOpsubHard() {
		return opsubHard;
	}
	
	/**
	 * Returns SubClassOf(C1 ObjectSomeValuesFrom(R C2)) (opsup)
	 * 
	 * @return R at position 0, C2 at position 1, C1 at position 2 of the array.
	 */
	public ArrayList<String[]> getOpsupHard() {
		return opsupHard;
	}
	/**
	 * SubObjectPropertyOf(R S). (psubsumes)
	 * 
	 * @return r at position 0, s at position 1 of the array.
	 */
	public ArrayList<String[]> getPsubsumesHard() {
		return psubsumesHard;
	}
	
	/**
	 * SubPropertyChain(R S T)
	 * 
	 * @return r at position 0, s at position 1, t at position 2, evidence value at positon 3 of the array.
	 */
	public ArrayList<String[]> getPcomEvidence() {
		return pcomEvidence;
	}
	/**
	 * SubPropertyChain(R S T)
	 * 
	 * @return r at position 0, s at position 1, t at position 2 of the array.
	 */
	public ArrayList<String[]> getPcomHard() {
		return pcomHard;
	}
}

