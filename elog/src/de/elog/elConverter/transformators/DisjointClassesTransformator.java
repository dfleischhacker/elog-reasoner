package de.elog.elConverter.transformators;

import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import de.elog.elConverter.ELOntology;

import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;

/**
 * Transformator for disjoint classes. 
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */
public class DisjointClassesTransformator implements Transformator {

	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory, ELOntology ontology) {
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		if(axiom instanceof OWLDisjointClassesAxiomImpl){
			OWLDisjointClassesAxiom disjointClasses = (OWLDisjointClassesAxiom) axiom;
			List<OWLClassExpression> listDisClasses = disjointClasses.getClassExpressionsAsList();
			for(int i=0; i<listDisClasses.size();i++){
				for(int j=i; j<listDisClasses.size();j++){
					if(i!=j){
						OWLObjectIntersectionOf intersection = factory.getOWLObjectIntersectionOf(listDisClasses.get(i), listDisClasses.get(j));
						OWLSubClassOfAxiom newAxiom = factory.getOWLSubClassOfAxiom(intersection, factory.getOWLNothing());
						result.add(newAxiom);
						return result;
						
					}
				}
			}
		}
		result.add(axiom);
		return result;
	}

}
