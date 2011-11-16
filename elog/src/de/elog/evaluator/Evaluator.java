package de.elog.evaluator;

import de.elog.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import de.elog.elConverter.ConverterConstants;

public class Evaluator {
	
	private int falseNegatives = 0;
	private int falsePositives = 0;
	private int truePositives = 0;
	
	private AxiomsToCheck axiomsToCheck;
	
	/**
	 * They are not in the gold standard, but my system does get them (My System is incorrect -> false).
	 * 
	 * @return
	 */
	public int getFalseNegatives() {
		return falseNegatives;
	}

	/**
	 * They are in the gold standard, but my system does not get them (My System is incorrect -> false).
	 * @return
	 */
	public int getFalsePositives() {
		return falsePositives;
	}

	/**
	 * They are in the gold standard and my system gets them (My System is correct -> true).
	 * 
	 * @return
	 */
	public int getTruePositives() {
		return truePositives;
	}

	public void evaluate(String pathGeneratedOntology, String pathGoldStandard) throws OWLOntologyCreationException{
		if(axiomsToCheck==null){
			System.err.println("Define first an axiomsToCheck.");
		}else{
			// create generated Ontology
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File(pathGeneratedOntology);
			OWLOntology generated = manager.loadOntologyFromOntologyDocument(file);
			//OWLReasonerConfiguration config = new SimpleConfiguration();
			
			// create generatedReasoner
			OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
			OWLReasoner reasonerGenerated = reasonerFactory.createReasoner(generated);
			reasonerGenerated.precomputeInferences();
			
			// create gold Ontology
			//OWLOntologyManager managerGold = OWLManager.createOWLOntologyManager();
			File fileGold = new File(pathGoldStandard);
			OWLOntology gold = manager.loadOntologyFromOntologyDocument(fileGold);
			
			// create goldReasoner
			OWLReasoner reasonerGold = reasonerFactory.createReasoner(gold);
			reasonerGold.precomputeInferences();
			
			// get axioms
			ArrayList<OWLAxiom> axioms = axiomsToCheck.getAxiomsToCheck(this.getClasses(gold, generated), this.getObjectProperties(generated, gold));

			
			for(OWLAxiom axiom: axioms){
				boolean entailedInGold = reasonerGold.isEntailed(axiom);
				boolean entailedInGenerated = reasonerGenerated.isEntailed(axiom);
				
				if(entailedInGold && entailedInGenerated){
					//System.out.println("tp;"+axiom);
					truePositives++;
				}else if(entailedInGold && !entailedInGenerated){
					//System.out.println("fn;"+axiom);
					falseNegatives++;
				}else if(!entailedInGold && entailedInGenerated){
					//System.out.println("fp;"+axiom);
					falsePositives++;
				}else{
					//System.out.println("tn axiom:"+axiom);
				}
			}
			
			
		}
		
	}
	
	public double evaluateLogLikelihood(String pathGeneratedOntology, String pathGoldStandard) throws OWLOntologyCreationException{
		
		double logLikelihood = 0;
		
		if(axiomsToCheck==null){
			System.err.println("Define first an axiomsToCheck.");
		}else{
			// create generated Ontology
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File(pathGeneratedOntology);
			OWLOntology generated = manager.loadOntologyFromOntologyDocument(file);
			//OWLReasonerConfiguration config = new SimpleConfiguration();
			
			// create generatedReasoner
			OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
			OWLReasoner reasonerGenerated = reasonerFactory.createReasoner(generated);
			reasonerGenerated.precomputeInferences();
			
			// create gold Ontology
			//OWLOntologyManager managerGold = OWLManager.createOWLOntologyManager();
			File fileGold = new File(pathGoldStandard);
			OWLOntology gold = manager.loadOntologyFromOntologyDocument(fileGold);
			
			// create goldReasoner
			OWLReasoner reasonerGold = reasonerFactory.createReasoner(gold);
			reasonerGold.precomputeInferences();
			
			// get axioms
			ArrayList<OWLAxiom> axioms = axiomsToCheck.getAxiomsToCheck(this.getClasses(gold, generated), this.getObjectProperties(generated, gold));

			
			for(OWLAxiom axiom: axioms) {
				boolean entailedInGold = reasonerGold.isEntailed(axiom);
				boolean entailedInGenerated = reasonerGenerated.isEntailed(axiom);
				
				if(entailedInGold && entailedInGenerated){
					//System.out.println("tp;"+axiom);
					
					truePositives++;
				}else if(entailedInGold && !entailedInGenerated){
					//System.out.println("fn;"+axiom);

					falseNegatives++;
				}else if(!entailedInGold && entailedInGenerated){
					//System.out.println("fp;"+axiom);
					falsePositives++;
				}else{
					//System.out.println("tn axiom:"+axiom);
				}
			}
			
			
		}
		
		return logLikelihood;
		
	}
	
	
	/**
	 * Gets Precision according to the formular TP/(TP + FP)
	 * @return value between 0 and 1 (highest Precision)
	 */
	public double getPrecision(){	
		double denominator = (truePositives + falsePositives);
		if(denominator == 0){
			return 0;
		}else{
			return ((double)this.truePositives)/denominator;
		}
	}

	/**
	 * Gets Recall according to the formular TP/(TP + FN)
	 * 
	 * @return value between 0 and 1 (highest Precision)
	 */
	public double getRecall(){
		double denominator = (truePositives + falseNegatives);
		if(denominator == 0){
			return 0;
		}else{
			return ((double) truePositives)/denominator;
		}
	}
	public double getFMeasure(){
		return (2*this.getPrecision()*this.getRecall())/(this.getPrecision()+this.getRecall());
	}
		
	public Set<OWLClass> getClasses(OWLOntology ont1, OWLOntology ont2){
		Set<OWLClass> result1 = ont1.getClassesInSignature();
		Set<OWLClass> result2 = ont2.getClassesInSignature();
		
		Set<OWLClass> result = new HashSet<OWLClass>();
		for(OWLClass c : result2){
			if(!c.toString().contains(ConverterConstants.NEW_NAMESPACE)){
				result.add(c);
			}
		}
		
		for(OWLClass c1 : result1){
			if(result2.contains(c1)){
				// do nothing
			}else{
				result.add(c1);
			}
		}
		return result;
	}
	
	public Set<OWLObjectProperty> getObjectProperties(OWLOntology ont1, OWLOntology ont2){
		
		Set<OWLObjectProperty> result1 = ont1.getObjectPropertiesInSignature();
		Set<OWLObjectProperty> result2 = ont2.getObjectPropertiesInSignature();
		
		Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
		for(OWLObjectProperty p : result2){
			if(!p.toString().contains(ConverterConstants.NEW_NAMESPACE)){
				result.add(p);
			}
		}
		
		for(OWLObjectProperty c1 : result1){
			if(c1.toString().contains(ConverterConstants.NEW_NAMESPACE) || result2.contains(c1) ){
				// do nothing
			}else{
				result.add(c1);
			}
		}
		return result;
	}

	public void setAxiomsToCheck(AxiomsToCheck axiomsToCheck) {
		this.axiomsToCheck = axiomsToCheck;
	}

	public AxiomsToCheck getAxiomsToCheck() {
		return axiomsToCheck;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Evaluated: ").append(this.axiomsToCheck.getName())
			.append("\n- Precision: ").append(this.getPrecision())
			.append("\n- Recall: ").append(this.getRecall())
			.append("\n- FMeasure: ").append(this.getFMeasure())
			.append("\n\n- truePositives: ").append(this.getTruePositives())
			.append("\n- falsePositives: ").append(this.getFalsePositives())
			.append("\n- falseNegatives: ").append(this.getFalseNegatives());
		return sb.toString();
	}
	
	/**
	 * Gets the confidence value via the "confidence" annotation property. 
	 * 
	 * If no confidence value exist, the function returns "null".
	 * 
	 * @param axiom
	 * @return
	 */
	public Double getProbabilityValue(OWLAxiom axiom){
		for(OWLAnnotation annotation : axiom.getAnnotations()){
			if(annotation.getProperty().getIRI().getFragment().toString().equalsIgnoreCase(
					Constants.ANNOTATION_PROPERTY_FOR_REASONING_PROBABILITY_VALUE)){
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
