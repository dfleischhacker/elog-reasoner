package de.elog.misSampler;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;

public class MisSampler {
	public static void main(String[] args) {
		if(args.length!=2 && args.length!=3){
			System.out.println("Start the reasoner with 2 or 3 arguments:");
			System.out.println("- existing filename of input ontology");
			System.out.println("- new filename of materialized output ontology");
			System.out.println("- reference Ontology / Gold standard (optional)");
			System.out.println();
			System.out.println("Example: elog -r2 \"data/input/ontology1.owl\" \"data/output/ontology1_reasoner.owl\"");
			System.out.println("Example: elog -r2 \"data/input/ontology2.owl\" \"data/output/ontology2_reasoner.owl\" \"data/input/goldStandard.owl\"");
		}else{
			System.out.println("start program");
			
			
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
	
}
