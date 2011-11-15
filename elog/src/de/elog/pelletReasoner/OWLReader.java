package de.elog.pelletReasoner;

import java.io.File;
import java.util.HashMap;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


import com.clarkparsia.owlapiv3.OWL;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;

public class OWLReader {

	private OWLOntology owlOntology;
	private HashMap<String, OwnAxiom> axioms = new HashMap<String,OwnAxiom>();	
	private OWLOntologyManager manager;
	
	OWLReader() {
		manager = OWL.manager;
	}

	public IRI read(String filename) throws OWLOntologyCreationException{
		
		axioms = new HashMap<String, OwnAxiom>();	
		owlOntology = manager.loadOntologyFromOntologyDocument(new File(filename));
		
		for(OWLAxiom axiom : owlOntology.getAxioms()){
			Double conf = this.getConfidenceValue(axiom);
			OwnAxiom ownAxiom = null;
			OWLAxiom axiomWithoutAnnotations = axiom.getAxiomWithoutAnnotations();
			if(conf==null){
				ownAxiom = new OwnAxiom(axiomWithoutAnnotations.toString(), axiom, axiomWithoutAnnotations, 0, true);
			}else{
				ownAxiom = new OwnAxiom(axiomWithoutAnnotations.toString(), axiom, axiomWithoutAnnotations, conf, false);
			}
			axioms.put(axiomWithoutAnnotations.toString(), ownAxiom);
		}
		return this.owlOntology.getOntologyID().getOntologyIRI();
	}
	
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
	
	public void write (OWLOntology ontology, String filePath) {
		File file = new File(filePath);
		try {
			manager.saveOntology(ontology, IRI.create(file.toURI()));
		} catch (OWLOntologyStorageException e) {
			System.err.println("ELOG could not write to the ontology file.");
			e.printStackTrace();
		}
	}
	
	public HashMap<String, OwnAxiom> getAxioms() {
		return axioms;
	}

}
