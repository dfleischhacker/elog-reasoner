package de.elog.elGreedy;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLAxiom;

public class OwnAxiom {
	private ArrayList<String> variableNames;
	private OWLAxiom axiom;
	private double value;

	public OwnAxiom(ArrayList<String> variableNames, OWLAxiom axiom, double value) {
		super();
		this.variableNames = variableNames;
		this.axiom = axiom;
		this.value=value;
	}
	public void setAxiom(OWLAxiom axiom) {
		this.axiom = axiom;
	}
	public OWLAxiom getAxiom() {
		return axiom;
	}
	public void setVariableNames(ArrayList<String> variableNames) {
		this.variableNames = variableNames;
	}
	public ArrayList<String> getVariableNames() {
		return variableNames;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return value;
	}
	
}
