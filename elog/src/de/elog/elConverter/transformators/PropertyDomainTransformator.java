package de.elog.elConverter.transformators;

import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyDomainAxiomImpl;

/**
 * Transformator for property domain. 
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */

public class PropertyDomainTransformator implements Transformator {

	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory) {
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		
		if(axiom instanceof OWLObjectPropertyDomainAxiomImpl ){
			
			OWLObjectPropertyDomainAxiom domainAxiom = (OWLObjectPropertyDomainAxiom) axiom;
			OWLObjectPropertyExpression property = domainAxiom.getProperty();
			OWLClassExpression domain = domainAxiom.getDomain();
			OWLObjectSomeValuesFrom some = factory.getOWLObjectSomeValuesFrom(property, factory.getOWLThing());
			OWLSubClassOfAxiom newAxiom = factory.getOWLSubClassOfAxiom(some, domain);
			
			result.add(newAxiom);
			return result;
		}
		result.add(axiom);
		return result;
	}

}
