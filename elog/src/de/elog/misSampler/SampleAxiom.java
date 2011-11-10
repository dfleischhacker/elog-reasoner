package de.elog.misSampler;

import org.semanticweb.owlapi.model.OWLAxiom;


public class SampleAxiom implements Comparable<SampleAxiom> {

	OWLAxiom axiom;
	double weight;
	double confidence;
	
	SampleAxiom(OWLAxiom a, double w) {
		axiom = a;
		weight = w;
		confidence = 0;
	}
	
	public OWLAxiom getAxiom() {
		return axiom;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public double getConfidence() {
		return confidence;
	}
		
	public int compareTo(SampleAxiom arg0) {
		
		if ( ((SampleAxiom)arg0).getWeight() > weight) {
			return 1;
		} else {
			return -1;
		}
	}
	
	
	
}
