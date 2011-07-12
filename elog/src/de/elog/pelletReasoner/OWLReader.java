package de.elog.pelletReasoner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;

public class OWLReader {

	private OWLOntology owlOntology;
	private OWLDataFactory factory ;
	private OWLOntologyManager manager;
	private HashMap<String, OwnAxiom> axioms = new HashMap<String,OwnAxiom>();	
	
	public ArrayList<String[]> getSoftAxioms(){
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(OwnAxiom a : axioms.values()){
			if(!a.isHard()){
				String[] s = new String[]{
						a.getId(), Double.toString(a.getConfidenceValue())
				};
				result.add(s);
			}
		}
		return result;	
	}
	
	public ArrayList<String[]> getHardAxioms(){
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(OwnAxiom a : axioms.values()){
			if(a.isHard()){
				String[] s = new String[]{
						a.getId()
				};
				result.add(s);
			}
		}
		return result;	
	}
	
	public IRI read(String filename) throws OWLOntologyCreationException{
		axioms = new HashMap<String, OwnAxiom>();	
		manager = OWLManager.createOWLOntologyManager();
		this.factory =OWLManager.getOWLDataFactory();
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
