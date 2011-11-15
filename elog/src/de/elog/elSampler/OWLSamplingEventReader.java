package de.elog.elSampler;

import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;
import de.elog.elConverter.ELOntology;
import de.elog.elConverter.GroundAxiom;


public class OWLSamplingEventReader {
	
	private ArrayList<String> event0 = new ArrayList<String>();
	private ArrayList<String> event1 = new ArrayList<String>();
	
	/**
	 * Gets the polarity of an event via the annotation property.
	 * 
	 * Returns true if the event is positive, false if it is negative.
	 * 
	 * If no annotation property value exist but a confidence, the
	 * method assumes that the axiom is supposed to be sampled
	 * 
	 * @param axiom
	 * @return
	 * @throws Exception 
	 */
	private Boolean isPositiveEvent(OWLAxiom axiom) {
		
		boolean hasConfidenceValue = false;
		
		for(OWLAnnotation annotation : axiom.getAnnotations()){
			if(annotation.getProperty().getIRI().getFragment().toString().equalsIgnoreCase(Constants.ANNOTATION_PROPERTY_FOR_SAMPLING_POSITIVE_EVENT)){
				OWLAnnotationValue annValue = annotation.getValue();
				if(annValue instanceof OWLLiteralImpl){
					OWLLiteral literalValue = (OWLLiteral) annValue;
					if(literalValue.isBoolean()) {
						return literalValue.parseBoolean();
					}					
				}
			}
			if(annotation.getProperty().getIRI().getFragment().toString().equalsIgnoreCase(Constants.ANNOTATION_PROPERTY_FOR_REASONING_CONFIDENCE_VALUE)) {
				hasConfidenceValue = true;			
			}
		}

		if (hasConfidenceValue) {
			System.err.println("The boolean annotation-property " + Constants.ANNOTATION_PROPERTY_FOR_SAMPLING_POSITIVE_EVENT + " is missing. " +
				"The application will assume that the axiom " + axiom + " with confidence value is supposed to be sampled.");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Builds variable name of form: subsumes|a|b|c.
	 * 
	 * @param axiom
	 * @param elOntology
	 */
	private String buildVariable(OWLAxiom axiom, ELOntology elOntology){
		String type = null;
		String[] stringValue = null;
		GroundAxiom newAxiom = elOntology.get_C1_subclassof_d(axiom);
		if(newAxiom!=null){
			type="subsumes";
			stringValue = newAxiom.getValue();
			
		}
		newAxiom = elOntology.get_C1_and_c2_subclassof_d(axiom);
		if(newAxiom!=null){
			type="intersection";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_Exists_r_c1_subclassof_d(axiom);
		if(newAxiom!=null){
			type="opsub";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_C1_subclassof_exists_r_c2(axiom);
		if(newAxiom!=null){
			type="opsup";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_R_subpropertyof_s(axiom);
		if(newAxiom!=null){
			type="psubsumes";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_R1_com_r2_subpropertyof_s(axiom);
		if(newAxiom!=null){
			type="pcom";
			stringValue = newAxiom.getValue();
		}		
		if(stringValue==null){
			return null;
		}else{
			StringBuilder returnValue = new StringBuilder();
			returnValue.append(type);
			for(int i=0;i<stringValue.length;i++){
				returnValue.append("|").append(stringValue[i]);
			}
			return returnValue.toString();
		}
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

		ELOntology elOntology = new ELOntology();
		HashSet<OWLAxiom> originalAxioms = elOntology.loadOntology(ontologyFilePath);
		HashSet<OWLAxiom> prenormalizedOntology = elOntology.normalizeAll();

		
		for(OWLAxiom axiom : originalAxioms){
			boolean eventPositive = false;
			if(this.isPositiveEvent(axiom)) eventPositive=true;
			
			HashSet<OWLAxiom> specificNormalizationAxioms = elOntology.normalizeAxiom(axiom, prenormalizedOntology);
			
			String variableName;
			for(OWLAxiom specificAxioms:specificNormalizationAxioms){
				variableName = this.buildVariable(specificAxioms, elOntology);
				if(variableName !=null){
					if(eventPositive){
						event1.add(variableName);
					}else{
						event0.add(variableName);
					}
				}
			}

		}
		
		return elOntology.getOntologyId();
	}

	public ArrayList<String> getEvent0() {
		return event0;
	}

	public ArrayList<String> getEvent1() {
		return event1;
	}
}
