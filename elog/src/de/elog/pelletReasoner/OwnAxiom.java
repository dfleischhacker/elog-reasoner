package de.elog.pelletReasoner;

import org.semanticweb.owlapi.model.OWLAxiom;

public class OwnAxiom {
	private OWLAxiom axiom;
	private OWLAxiom axiomWithoutAnnotation;
	private String id;
	private double confidenceValue;
	private boolean hard;
	
	
	public OwnAxiom(String id,OWLAxiom axiom, OWLAxiom axiomWithoutAnnotation,
			double confidenceValue, boolean hard) {
		super();
		this.axiom = axiom;
		this.axiomWithoutAnnotation = axiomWithoutAnnotation;
		this.confidenceValue = confidenceValue;
		this.hard = hard;
	}
	public OWLAxiom getAxiom() {
		return axiom;
	}
	public void setAxiom(OWLAxiom axiom) {
		this.axiom = axiom;
	}
	public OWLAxiom getAxiomWithoutAnnotation() {
		return axiomWithoutAnnotation;
	}
	public void setAxiomWithoutAnnotation(OWLAxiom axiomWithoutAnnotation) {
		this.axiomWithoutAnnotation = axiomWithoutAnnotation;
	}
	public double getConfidenceValue() {
		return confidenceValue;
	}
	public void setConfidenceValue(double confidenceValue) {
		this.confidenceValue = confidenceValue;
	}
	public boolean isHard() {
		return hard;
	}
	public void setHard(boolean hard) {
		this.hard = hard;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	
}
