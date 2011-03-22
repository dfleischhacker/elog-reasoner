package de.elog.elConverter.transformators;

import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * Interface for Transformators.
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */

public interface Transformator {
	
	public HashSet<OWLAxiom> convert(OWLAxiom axiom,OWLDataFactory factory);
}
