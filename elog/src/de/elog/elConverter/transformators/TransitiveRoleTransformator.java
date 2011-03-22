package de.elog.elConverter.transformators;

import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;

/**
 * Transformator for transitive properties. 
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */

public class TransitiveRoleTransformator implements Transformator {

	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory) {
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		
		if(axiom instanceof OWLTransitiveObjectPropertyAxiomImpl ){
			OWLTransitiveObjectPropertyAxiomImpl transitiveAxiom = (OWLTransitiveObjectPropertyAxiomImpl) axiom;
			ArrayList<OWLObjectPropertyExpression> properties = new ArrayList<OWLObjectPropertyExpression>();
			properties.add(transitiveAxiom.getProperty());
			properties.add(transitiveAxiom.getProperty());
			OWLSubPropertyChainOfAxiom newAxiom = factory.getOWLSubPropertyChainOfAxiom(properties, transitiveAxiom.getProperty());
			result.add(newAxiom);
			return result;
			
		}
		result.add(axiom);
		return result;
	}

}
