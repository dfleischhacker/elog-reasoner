package de.elog.elConverter.transformators;

import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import de.elog.elConverter.ELOntology;

/**
 * Transformator for equivalent classes. 
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */
public class EquivalentClassesTransformator implements Transformator {

	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory, ELOntology ontology) {
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		
		if(axiom instanceof OWLEquivalentClassesAxiom){
			OWLEquivalentClassesAxiom equivalentClasses = (OWLEquivalentClassesAxiom) axiom;
			List<OWLClassExpression> listDisClasses = equivalentClasses.getClassExpressionsAsList();
			for(int i=0; i<listDisClasses.size();i++){
				for(int j=i; j<listDisClasses.size();j++){
					if(i!=j){
						OWLSubClassOfAxiom subClass1 = factory.getOWLSubClassOfAxiom(listDisClasses.get(i), listDisClasses.get(j));
						OWLSubClassOfAxiom subClass2 = factory.getOWLSubClassOfAxiom(listDisClasses.get(j), listDisClasses.get(i));
						result.add(subClass1);
						result.add(subClass2);			
						return result;
						
					}
				}
			}
		}
		result.add(axiom);
		return result;
	}
}
