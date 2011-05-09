package de.elog.elGreedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.owlapi.OWLFunctionalObjectPropertyAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSymmetricObjectPropertyAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;
import de.elog.Constants;
import de.elog.elConverter.ELOntology;


public class OWLGreedyReader {

	private ArrayList<OWLAxiom> hardAxioms = new ArrayList<OWLAxiom>();
	private HashMap<OWLAxiom,Double> softAxioms = new HashMap<OWLAxiom,Double>();
	
	/**
	 * Gets the confidence value via the "confidence" annotation property. 
	 * 
	 * If no confidence value exist, the function returns "null".
	 * 
	 * @param axiom
	 * @return
	 */
	private Double getConfidenceValue(OWLAxiom axiom){
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
		ELOntology ontology = new ELOntology();
		HashSet<OWLAxiom> elAxioms = ontology.loadOntology(ontologyFilePath);
		
		for(OWLAxiom axiom : elAxioms){			
			// data properties are not considered. Only object properties.
			// All annotation assertions do not contain important information for the ontology. Therefore, they are not considered.
			// The symmetric, transitive and functional object properties are already considered as "normal" object properties. Only the additional infromation about transitivity and functional is lost.
			if(!axiom.toString().contains("DataProperty") 
				&& !axiom.toString().contains("AnnotationAssertion")
				&& !axiom.toString().contains("Declaration")
				&& !(axiom instanceof OWLTransitiveObjectPropertyAxiomImpl)
				&& !(axiom instanceof OWLFunctionalObjectPropertyAxiomImpl)
				&& !(axiom instanceof OWLSymmetricObjectPropertyAxiomImpl)
			){
				Double conf = this.getConfidenceValue(axiom);
				if(conf==null){
					this.hardAxioms.add(axiom);
				}else{
					this.softAxioms.put(axiom, conf);
				}
				
			}
		}
		
		return ontology.getOntologyId();
	}
	public ArrayList<OWLAxiom> getHardAxioms() {
		return hardAxioms;
	}

	public HashMap<OWLAxiom,Double> getSoftAxioms() {
		return softAxioms;
	}
	public ArrayList<String> convertHard(String predName, ArrayList<String[]> varsList){
		ArrayList<String> result = new ArrayList<String>();
		for(String[] vars : varsList){
			StringBuilder sb = new StringBuilder();
			sb.append(predName);
			for(String s : vars){
				sb.append("|").append(s);
			}
			result.add(sb.toString());
		}
		return result;
	}
	
	public double convertSoft(String predName, ArrayList<String[]> varsList, ArrayList<String> result){
		double confidence = 0;
		for(String[] vars : varsList){
			StringBuilder sb = new StringBuilder();
			sb.append(predName);
			for(int i =0; i<vars.length-1 ;i++){
				sb.append("|").append(vars[i]);
			}
			confidence = Double.parseDouble(vars[vars.length-1]);
			result.add(sb.toString());
		}
		return confidence;
	}
}
