package de.elog.elConverter.transformators;

import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyRangeAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;

import de.elog.elConverter.Constants;
import de.elog.elConverter.ELOntology;


/**
 * Transformator for property range. 
 * 
 * The outcome only consists of "subclass", "objectSomeOf", and "intersection" components.
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.

 * 
 * Must be called as last converter with all other converted axioms ax arguments.
 * @author jan
 *
 */
public class PropertyRangeTransformator implements Transformator{

	private HashSet<OWLAxiom> axioms;
	
	public PropertyRangeTransformator(HashSet<OWLAxiom> normalizedAxiomsOfWholeOntology) {
		this.axioms = normalizedAxiomsOfWholeOntology;
	}

	/**
	 * for all range(r) subclassof A axioms do:
	 * 1. add X subclassof A (X comes from the C subclassof r.X axioms)
	 * 2. if \epsilon subclassof r then add TOP subclassof A.
	 */
	@Override
	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory, ELOntology ontology) {
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		if(axiom instanceof OWLObjectPropertyRangeAxiomImpl){
			OWLObjectPropertyRangeAxiom range = (OWLObjectPropertyRangeAxiom) axiom;
			OWLClassExpression a = range.getRange();
			OWLObjectPropertyExpression rExpr = range.getProperty();
			if(rExpr instanceof OWLObjectProperty){
				OWLObjectProperty r = (OWLObjectProperty) rExpr;
				//1. add X subclassof A (X comes from the C subclassof r.X axioms)
				HashSet<OWLClass> xs = this.getAllX(r);
				for(OWLClass x :xs){
					if(x.getIRI().toString().contains(Constants.NEW_OPSUP_CONCEPT_IRI)){
						OWLSubClassOfAxiom newAxiom = factory.getOWLSubClassOfAxiom(x, a);
						result.add(newAxiom);
					}
				}
				// 2. if \epsilon subclassof r then add TOP subclassof A.
				if(this.existsAnEpsilon(r)){
					OWLSubClassOfAxiom newAxiom = factory.getOWLSubClassOfAxiom(factory.getOWLThing(), a);
					result.add(newAxiom);
				}
				return result;
			}			
		}
		result.add(axiom);
		return result;
	}

	/**
	 * Tests if there is an epsilon (from Transitive Property) 
	 * which subsumes the given r.
	 * 
	 * @param r
	 * @return
	 */
	private boolean existsAnEpsilon(OWLObjectProperty r){
		for(OWLAxiom axiom :axioms){
			if(axiom instanceof OWLSubObjectPropertyOfAxiomImpl){
				OWLSubObjectPropertyOfAxiom axiomSubPropertyOf = (OWLSubObjectPropertyOfAxiom) axiom;
				OWLObjectPropertyExpression leftObjectProperty =  axiomSubPropertyOf.getSubProperty();
				OWLObjectPropertyExpression rightObjectProperty =  axiomSubPropertyOf.getSuperProperty();
				if(rightObjectProperty  instanceof OWLObjectPropertyImpl && leftObjectProperty  instanceof OWLObjectPropertyImpl){
					OWLObjectProperty epsilon = (OWLObjectProperty) leftObjectProperty;
					OWLObjectProperty rCheck = (OWLObjectProperty) rightObjectProperty;
					
					if(epsilon.getIRI().toString().contains(Constants.NEW_REFLEXIVE_ROLE_SUB_IRI)
						&& rCheck.equals(r)){
						return true;
					}
					
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets all x from the c subclassof exists r.X axioms, 
	 * where the given r equals the one in the expression.
	 * 
	 * @param r
	 * @return
	 */
	private HashSet<OWLClass> getAllX(OWLObjectProperty r){
		HashSet<OWLClass> result = new HashSet<OWLClass>();
		for(OWLAxiom axiom : axioms){
			if(axiom instanceof OWLSubClassOfAxiomImpl){
				OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
				OWLClassExpression rightSide = axiomSubClass.getSuperClass();
				OWLClassExpression leftSideE = axiomSubClass.getSubClass();
				if(rightSide instanceof OWLObjectSomeValuesFromImpl){
					OWLObjectSomeValuesFrom rightPair = (OWLObjectSomeValuesFrom) rightSide;
					OWLObjectPropertyExpression includedPropertyE =   rightPair.getProperty();
					OWLClassExpression includedClassE = rightPair.getFiller();
					if((leftSideE instanceof OWLClassImpl)&&(includedClassE instanceof OWLClassImpl)&&(includedPropertyE instanceof OWLObjectPropertyImpl)){
						OWLClass X = (OWLClass) includedClassE;
						OWLObjectProperty rControl = (OWLObjectProperty) includedPropertyE;
						// if rControl equals the given r, then add
						if(rControl.equals(r)){
							result.add(X);
						}
					}
				}
			}
		}
		return result;
	}
	
	
}
