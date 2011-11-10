package de.elog.pelletReasoner;

import java.io.File;
import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;

public class OWLReader {

	private OWLOntology owlOntology;
	private OWLOntologyManager manager;
	private HashMap<String, OwnAxiom> axioms = new HashMap<String,OwnAxiom>();	
	

	public IRI read(String filename) throws OWLOntologyCreationException{
		
		axioms = new HashMap<String, OwnAxiom>();	
		manager = OWLManager.createOWLOntologyManager();
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
	
	public void write (OWLOntology ont, String path) throws OWLOntologyStorageException{
		this.manager.saveOntology(ont, IRI.create(new File(path)));
	}
	
	public HashMap<String, OwnAxiom> getAxioms() {
		return axioms;
	}

}
