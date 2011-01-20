package de.elog.evaluator;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public interface AxiomsToCheck {
	public String getName();
	
	public ArrayList<OWLAxiom> getAxiomsToCheck(Set<OWLClass> classes, Set<OWLObjectProperty> properties);
}
