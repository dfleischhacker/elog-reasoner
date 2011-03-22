package de.elog.elConverter;


import java.util.Arrays;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Datastructure for the ELConverter. 
 * 
 * Use class ELOntology for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */

public class GroundAxiom {
	private String[] value;
	private OWLAxiom axiom;
	
	
	public GroundAxiom(String[] value, OWLAxiom axiom) {
		super();
		this.value = value;
		this.axiom = axiom;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GroundAxiom [value=");
		builder.append(Arrays.toString(value));
		builder.append(", axiom=");
		builder.append(axiom);
		builder.append("]");
		return builder.toString();
	}
	public void setValue(String[] value) {
		this.value = value;
	}
	public String[] getValue() {
		return value;
	}
	public void setAxiom(OWLAxiom axiom) {
		this.axiom = axiom;
	}
	public OWLAxiom getAxiom() {
		return axiom;
	}

}
