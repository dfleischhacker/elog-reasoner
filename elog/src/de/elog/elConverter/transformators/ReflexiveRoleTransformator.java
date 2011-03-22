package de.elog.elConverter.transformators;

import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;

import de.elog.elConverter.Constants;

import uk.ac.manchester.cs.owl.owlapi.OWLReflexiveObjectPropertyAxiomImpl;


/**
 * Transformator for reflexive properties. 
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */

public class ReflexiveRoleTransformator implements Transformator{

	/**
	 * Converts reflexive properties to
	 * 
	 * epsilon subclassof r.
	 */
	@Override
	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory) {
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		if(axiom instanceof OWLReflexiveObjectPropertyAxiomImpl){
			OWLReflexiveObjectPropertyAxiom reflexive = (OWLReflexiveObjectPropertyAxiom) axiom;
			OWLObjectPropertyExpression r = reflexive.getProperty();
			OWLObjectProperty epsilon = this.getNextEpsilonProperty(factory);
			result.add(factory.getOWLSubObjectPropertyOfAxiom(epsilon, r));
			return result;
		}
		result.add(axiom);
		return result;
	}
	
	private OWLObjectProperty getNextEpsilonProperty(OWLDataFactory factory){
		StringBuilder sb=new StringBuilder();
		sb.append(Constants.NEW_REFLEXIVE_ROLE_SUB_IRI).append(TransformatorManager.getNextFreeVariableCounter());
		return factory.getOWLObjectProperty(IRI.create(sb.toString()));
	}

}
