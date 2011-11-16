package de.elog.elConverter.transformators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import de.elog.elConverter.ConverterConstants;
import de.elog.elConverter.ELOntology;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

public class ElNormalizer implements Transformator{

	private ELOntology ontology;
	
	/**
	 * Converts the given Axioms to a set of normalized axioms. 
	 * 
	 * The normalized axioms are:
	 * - C1 subclassof D
	 * - C1 and C2 subclassof D
	 * - C1 subclassof Exists r C2
	 * - Exists r.C1 subclassof D
	 * - r subpropertyof s
	 * - r1 chain r2 subpropertyof s
	 * 
	 * Currently, it does not normalize Literals.
	 * 
	 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
	 * 
	 */
	@Override
	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory, ELOntology ontology) {
		this.ontology=ontology;
		// Loop1 NF1-NF4:
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		result.add(axiom);
		HashSet<OWLAxiom> resultTemp = null;
		boolean continueLoop = false;
		do{
			continueLoop = false;
			HashSet<OWLAxiom> resultNew = new HashSet<OWLAxiom>();
			for(OWLAxiom a : result){
				boolean axiomWasAdded = false;
				// NF1
				resultTemp = this.nf1(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// NF2
				resultTemp = this.nf2(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// NF3
				resultTemp = this.nf3(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// NF4
				resultTemp = this.nf4(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// axiom was not processed by any of NF1-NF4
				if(!axiomWasAdded){
					resultNew.add(a);
				}
			}
			result = resultNew;			
		}while(continueLoop);

		
		do{
			continueLoop = false;
			HashSet<OWLAxiom> resultNew = new HashSet<OWLAxiom>();
			for(OWLAxiom a : result){
				boolean axiomWasAdded = false;
				// NF5
				resultTemp = this.nf5(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// NF6
				resultTemp = this.nf6(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// NF7
				resultTemp = this.nf7(a, factory);
				if(resultTemp!=null){
					continueLoop = true;
					axiomWasAdded = true;
					if(resultTemp.size()>0) resultNew.addAll(resultTemp);
				}
				// axiom was not processed by any of NF5-NF7
				if(!axiomWasAdded){
					resultNew.add(a);
				}
			}
			result = resultNew;			
		}while(continueLoop);
		
		
		return result;
	}
	
	/**
	 * NF1 from publication: Pushing the EL Envelope.
	 * 
	 * r_1 x r_2 x ... x r_k subclassof s 
	 * --> r_1 x ... x r_k-1 subclassof u, u x r_k subclassof s
	 */
	private HashSet<OWLAxiom> nf1(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubPropertyChainAxiomImpl){
			HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
			OWLSubPropertyChainOfAxiom chainAxiom = (OWLSubPropertyChainOfAxiom) axiom;
			List<OWLObjectPropertyExpression> listChains = chainAxiom.getPropertyChain();
			if(listChains.size()>2){
				OWLObjectProperty u = this.getNextObjectProperty(factory);
				//r_1 x ... x r_k-1 subclassof u,
				ArrayList<OWLObjectPropertyExpression> newList1 = new ArrayList<OWLObjectPropertyExpression>();
				for(int i=0;i<listChains.size()-1; i++){
					newList1.add(listChains.get(i));
				}
				OWLSubPropertyChainOfAxiom newAxiom1 = factory.getOWLSubPropertyChainOfAxiom(newList1, u);
				// u x r_k subclassof s
				ArrayList<OWLObjectPropertyExpression> newList2 = new ArrayList<OWLObjectPropertyExpression>();
				newList2.add(u);
				newList2.add(listChains.get(listChains.size()-1));
				OWLSubPropertyChainOfAxiom newAxiom2 = factory.getOWLSubPropertyChainOfAxiom(newList2, chainAxiom.getSuperProperty());

				result.add(newAxiom1);
				result.add(newAxiom2);
				return result;
			}
		}
		return null;
	}
	
	/**
	 * C and D^ subclassof E 
	 * --> D^ subclassof A, C and A subclassof E.
	 * 
	 * @param axiom
	 * @param factory
	 * @return
	 */
	private HashSet<OWLAxiom> nf2(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			if(subClassAxiom.getSubClass() instanceof OWLObjectIntersectionOfImpl){
				HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
				OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) subClassAxiom.getSubClass();
				List<OWLClassExpression> listOperands = intersection.getOperandsAsList();
				if(listOperands.size()>2){
					OWLClassExpression c = listOperands.get(0);
							
					// D^ subclassof A
					OWLClass a = this.getNextClass(factory);
					HashSet<OWLClassExpression> newSet1 = new HashSet<OWLClassExpression>();
					for(int j=1; j<listOperands.size(); j++){
						newSet1.add(listOperands.get(j));
					}
					
					OWLObjectIntersectionOf newIntersection1 = factory.getOWLObjectIntersectionOf(newSet1);
					OWLSubClassOfAxiom axiom1 = factory.getOWLSubClassOfAxiom(newIntersection1, a);
					// C and A subclassof E
					OWLObjectIntersectionOf newIntersection2 = factory.getOWLObjectIntersectionOf(c, a);
					OWLSubClassOfAxiom axiom2 = factory.getOWLSubClassOfAxiom(newIntersection2, subClassAxiom.getSuperClass());

					result.add(axiom1);
					result.add(axiom2);
					return result;
				}else if (listOperands.size()==2){
					for(int i = 0; i<listOperands.size(); i++){
						OWLClassExpression d = listOperands.get(i);
						if(!this.isBasicConcept(d)){
							int j=0;
							if(i == 0) j=1;
							OWLClassExpression c = listOperands.get(j);
							OWLClass a = this.getNextClass(factory);
							OWLClassExpression e = subClassAxiom.getSuperClass();
							// D^ subclassof A
							OWLSubClassOfAxiom axiom1 = factory.getOWLSubClassOfAxiom(d, a);
							// C and A subclassof E
							OWLObjectIntersectionOf newIntersection2 = factory.getOWLObjectIntersectionOf(c, a);
							OWLSubClassOfAxiom axiom2 = factory.getOWLSubClassOfAxiom(newIntersection2, e);
							result.add(axiom1);
							result.add(axiom2);
							return result;
						}
					}
				}else{
					// only 1 operand. delete intersection!
					OWLSubClassOfAxiom axiom1 = factory.getOWLSubClassOfAxiom(listOperands.get(0), subClassAxiom.getSuperClass());
					result.add(axiom1);
					return result;
				}
				
			}
		}
		return null;
	}
		
	/**
	 * exists r.C^ subclassof D
	 * --> C^ subclassof A, Exists r.A subclassof D
	 * 
	 * @param axiom
	 * @param factory
	 * @return
	 */
	private HashSet<OWLAxiom> nf3(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			if(subClassAxiom.getSubClass() instanceof OWLObjectSomeValuesFromImpl){
				OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subClassAxiom.getSubClass();
				OWLClassExpression c = some.getFiller();
				if(!this.isBasicConcept(c)){
					
					OWLObjectPropertyExpression r = some.getProperty();
					OWLClassExpression d = subClassAxiom.getSuperClass();
					OWLClass a = this.getNextClass(factory);
					//C^ subclassof A
					OWLSubClassOfAxiom newAxiom1 = factory.getOWLSubClassOfAxiom(c, a);
					//Exists r.A subclassof D
					OWLObjectSomeValuesFrom newSome2 = factory.getOWLObjectSomeValuesFrom(r, a);
					OWLSubClassOfAxiom newAxiom2 = factory.getOWLSubClassOfAxiom(newSome2, d);

					HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
					result.add(newAxiom1);
					result.add(newAxiom2);
					return result;
				}
				
			}
		}
		return null;
	}
	
	/**
	 * Bottom SubClassOf D --> nothing
	 * 
	 * @param axiom
	 * @param factory
	 * @return
	 */
	private HashSet<OWLAxiom> nf4(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			if(subClassAxiom.getSubClass().equals(factory.getOWLNothing())){
				return new HashSet<OWLAxiom>();
			}
		}
		return null;
	}

	/**
	 * C^ subclassof D^ 
	 * --> C^ subclassof A, A subclassof D^
	 * 
	 * @param axiom
	 * @param factory
	 * @return
	 */
	private HashSet<OWLAxiom> nf5(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression c = subClassAxiom.getSubClass();
			OWLClassExpression d = subClassAxiom.getSuperClass();
			if(!this.isBasicConcept(c)&& !this.isBasicConcept(d)){
				OWLClass a = this.getNextClass(factory);
				//C^ subclassof A
				OWLSubClassOfAxiom newAxiom1 = factory.getOWLSubClassOfAxiom(c, a);
				//A subclassof D^
				OWLSubClassOfAxiom newAxiom2 = factory.getOWLSubClassOfAxiom(a, d);
				HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
				result.add(newAxiom1);
				result.add(newAxiom2);
				return result;
			}
		}
		return null;
	}

	/**
	 * Step1:
	 * 
	 * B subclassof exists r.C^ 
	 * --> B subclassof exists r.A, A subclassof C^
	 * 
	 * 
	 * Step2:
	 * Additionally, it introduces a new concept X for every 
	 * grounded nf6 axiom. Pushing the EL envelope further.
	 * 
	 * B subclassof exists r.C
	 * --> B subclassof exists r.X, X subclassof C.
	 * 
	 * 
	 * @param axiom
	 * @param factory
	 * @return
	 */
	private HashSet<OWLAxiom> nf6(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			if(subClassAxiom.getSuperClass() instanceof OWLObjectSomeValuesFromImpl){
				OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subClassAxiom.getSuperClass();
				OWLClassExpression c = some.getFiller();
				// step1
				if(!this.isBasicConcept(c)){
					
					OWLObjectPropertyExpression r = some.getProperty();
					OWLClassExpression b = subClassAxiom.getSubClass();
					OWLClass a = this.getNextClass(factory);
					//A subclassof C^
					OWLSubClassOfAxiom newAxiom1 = factory.getOWLSubClassOfAxiom(a, c);
					//B subclassof exists r.A
					OWLObjectSomeValuesFrom newSome2 = factory.getOWLObjectSomeValuesFrom(r, a);
					OWLSubClassOfAxiom newAxiom2 = factory.getOWLSubClassOfAxiom(b, newSome2);

					HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
					result.add(newAxiom1);
					result.add(newAxiom2);
					return result;
				}	
				// step2
				// TODO Something wrong maybe!!
				if(this.isBasicConcept(c)){
					OWLClass cBase = (OWLClass) c;
					// if we did not transform it already.
					if(!cBase.getIRI().toString().contains(ConverterConstants.NEW_OPSUP_CONCEPT_IRI) && !cBase.getIRI().toString().contains("Thing")){
						OWLClass x = this.getNextOpsupClass(factory);
						OWLObjectPropertyExpression r = some.getProperty();
						OWLClassExpression b = subClassAxiom.getSubClass();
						//B subclassof exists r.X
						OWLObjectSomeValuesFrom newSome1 = factory.getOWLObjectSomeValuesFrom(r, x);
						OWLSubClassOfAxiom newAxiom1 = factory.getOWLSubClassOfAxiom(b, newSome1);
						//X subclassof C
						OWLSubClassOfAxiom newAxiom2 = factory.getOWLSubClassOfAxiom(x, c);

						HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
						result.add(newAxiom1);
						result.add(newAxiom2);
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * B subclassof C and D 
	 * --> B subclassof C, B subclassof D.
	 * 
	 * @param axiom
	 * @param factory
	 * @return
	 */
	private HashSet<OWLAxiom> nf7(OWLAxiom axiom, OWLDataFactory factory){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
			if(subClassAxiom.getSuperClass() instanceof OWLObjectIntersectionOfImpl){
				HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
				OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) subClassAxiom.getSuperClass();
				List<OWLClassExpression> listOperands = intersection.getOperandsAsList();
				if(listOperands.size()>2){
					OWLClassExpression c = listOperands.get(0);
					OWLClassExpression b = subClassAxiom.getSubClass();
					
					// B subclassof D
					HashSet<OWLClassExpression> dSet = new HashSet<OWLClassExpression>();
					for(int j=1; j<listOperands.size(); j++){
						dSet.add(listOperands.get(j));
					}
					OWLObjectIntersectionOf d = factory.getOWLObjectIntersectionOf(dSet);
					OWLSubClassOfAxiom newAxiom1 = factory.getOWLSubClassOfAxiom(b, d);
					// B subclassof C
					OWLSubClassOfAxiom newAxiom2 = factory.getOWLSubClassOfAxiom(b, c);

					result.add(newAxiom1);
					result.add(newAxiom2);
					return result;
				}else if (listOperands.size()==2){
					for(int i = 0; i<listOperands.size(); i++){
						OWLClassExpression d = listOperands.get(i);
						if(!this.isBasicConcept(d)){
							int j=0;
							if(i == 0) j=1;
							OWLClassExpression c = listOperands.get(j);
							OWLClassExpression b = subClassAxiom.getSubClass();
							// B subclassof D
							OWLSubClassOfAxiom axiom1 = factory.getOWLSubClassOfAxiom(b, d);
							// B subclassof C
							OWLSubClassOfAxiom axiom2 = factory.getOWLSubClassOfAxiom(b, c);
							result.add(axiom1);
							result.add(axiom2);
							return result;
						}
					}
				}else{
					// only 1 operand. delete intersection!
					OWLSubClassOfAxiom axiom1 = factory.getOWLSubClassOfAxiom(listOperands.get(0), subClassAxiom.getSubClass());
					result.add(axiom1);
					return result;
				}
				
			}
		}
		return null;
	}

	
	private OWLClass getNextClass(OWLDataFactory factory){
		StringBuilder sb=new StringBuilder();
		sb.append(ConverterConstants.NEW_CLASS_IRI).append(TransformatorManager.getNextFreeVariableCounter());
		OWLClass result = factory.getOWLClass(IRI.create(sb.toString()));
		ontology.addClass(result);
		return result;
	}
	
	private OWLClass getNextOpsupClass(OWLDataFactory factory){
		StringBuilder sb=new StringBuilder();
		sb.append(ConverterConstants.NEW_OPSUP_CONCEPT_IRI).append(TransformatorManager.getNextFreeVariableCounter());
		OWLClass result = factory.getOWLClass(IRI.create(sb.toString()));
		ontology.addClass(result);
		return result;
	}
	
	private OWLObjectProperty getNextObjectProperty(OWLDataFactory factory){
		StringBuilder sb=new StringBuilder();
		sb.append(ConverterConstants.NEW_PROP_IRI).append(TransformatorManager.getNextFreeVariableCounter());
		OWLObjectProperty result = factory.getOWLObjectProperty(IRI.create(sb.toString()));
		ontology.addProperty(result);
		return result;
	}
	
	private boolean isBasicConcept(OWLClassExpression expression){
		if(expression instanceof OWLClassImpl){
			return true;
		}
		return false;
	}
	
	
}
