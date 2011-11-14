package de.elog.elConverter.transformators;

import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
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
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
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
 * Must be called as last converter with all other converted axioms as arguments.
 * @author jan
 *
 */
public class PropertyRangeTransformator implements Transformator{

	private HashSet<OWLAxiom> axioms;
	private PelletReasoner pelletReasoner;
	
	public PropertyRangeTransformator(HashSet<OWLAxiom> normalizedAxiomsOfWholeOntology, PelletReasoner pelletReasoner) {
		this.axioms = normalizedAxiomsOfWholeOntology;
		this.pelletReasoner = pelletReasoner;
	}

	/**
	 * This method takes a range restriction and returns a set of cbox axioms
	 * according to the rules given in the OWLED paper by Baader et al.
	 * 
	 * for all range(r) subclassof A axioms do:
	 * 1. add X subclassof A (X comes from the C subclassof r.X axioms)
	 * 2. if \epsilon subclassof r then add TOP subclassof A.
	 */
	@Override
	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory, ELOntology ontology) {
		
		//the result set of axioms for this range restriction
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		
		// get all classes of the current ontology
		HashSet<OWLClass> allClasses = (HashSet<OWLClass>) ontology.getClasses();

		//is the axiom a range restriction?
		if(axiom instanceof OWLObjectPropertyRangeAxiomImpl) {
			//cast the axiom into the range property type
			OWLObjectPropertyRangeAxiom rangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
			
			//the property is the object property 
			OWLObjectPropertyExpression rExpr = rangeAxiom.getProperty();
			
			//is the property an object property?
			if(rExpr instanceof OWLObjectProperty) {
				//r is the object property of the range restriction
				OWLObjectProperty r = (OWLObjectProperty) rExpr;
				
				//iterate over all axioms in the (so far) normalized ontology
				HashSet<OWLAxiom> normAxioms = ontology.getAxioms();
				for(OWLAxiom currentNormalizedAxiom : normAxioms) {
					//check whether the current axiom is of type subclassOf
					//with the second component \exists r.D
					if(currentNormalizedAxiom instanceof OWLSubClassOfAxiomImpl) {
						OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) currentNormalizedAxiom;
						OWLClassExpression rightSide = axiomSubClass.getSuperClass();
						OWLClassExpression leftSideE = axiomSubClass.getSubClass();
						//right side of the expression (i.e., \exists r.D)
						if(rightSide instanceof OWLObjectSomeValuesFromImpl) {
							//get the property r and the filler (i.e. class) D
							OWLObjectSomeValuesFrom rightPair = (OWLObjectSomeValuesFrom) rightSide;
							OWLObjectPropertyExpression includedPropertyE = rightPair.getProperty();
							OWLClassExpression includedClassE = rightPair.getFiller();
							if((leftSideE instanceof OWLClassImpl) 
									&& (includedClassE instanceof OWLClassImpl)
									    && (includedPropertyE instanceof OWLObjectPropertyImpl) 
									       && (r.equals(includedPropertyE))){
								
								//build a new class that is not yet part of the ontology
								OWLClass XrD = getNextClass(factory, ontology);
								
								//build a new opsup axiom
								OWLAxiom opsupAxiom = factory.getOWLSubClassOfAxiom(leftSideE, factory.getOWLObjectSomeValuesFrom(r, XrD));
								result.add(opsupAxiom);
								
								//add the following axiom to the ontology (X_{r,D} \sqsubseteq D=includedClassE)
								OWLSubClassOfAxiom newAxiom = factory.getOWLSubClassOfAxiom(XrD, includedClassE);
								result.add(newAxiom);
								
								//iterate over all classes in the ontology!
								for(OWLClass currentClass : allClasses) {
									//test whether currentClass is a range restriction on property r
									OWLAxiom testRangeAxiom = factory.getOWLObjectPropertyRangeAxiom(r, currentClass);
									if (pelletReasoner.isEntailed(testRangeAxiom)) {
										//if this is the case, add the dollowing axiom to the ontology
										newAxiom = factory.getOWLSubClassOfAxiom(XrD, currentClass);
										result.add(newAxiom);
									}
								}
							}
						}
					}
				}
				
				
				// 2. if \epsilon subclassof r then add TOP subclassof A.
				if(this.existsAnEpsilon(r)) {
					//iterate over all classes in the ontology!
					for(OWLClass currentClass : allClasses) {
						//test whether currentClass is a range restriction on property r
						OWLAxiom testRangeAxiom = factory.getOWLObjectPropertyRangeAxiom(r, currentClass);
						if (pelletReasoner.isEntailed(testRangeAxiom)) {
							//if this is the case, add the dollowing axiom to the ontology
							OWLSubClassOfAxiom newAxiom = factory.getOWLSubClassOfAxiom(factory.getOWLThing(), currentClass);
							result.add(newAxiom);
						}
					}
				}
				
				//System.out.println(result);
				
				return result;
			}
			
		}
		result.add(axiom);
		return result;
	}
		
		/**
		 * Build a new (free) class and adds it to the ontology
		 * @param factory
		 * @return
		 */
	private OWLClass getNextClass(OWLDataFactory factory, ELOntology ontology){
		StringBuilder sb=new StringBuilder();
		sb.append(Constants.NEW_CLASS_IRI).append(TransformatorManager.getNextFreeVariableCounter());
		OWLClass result = factory.getOWLClass(IRI.create(sb.toString()));
		ontology.addClass(result);
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

	
}
