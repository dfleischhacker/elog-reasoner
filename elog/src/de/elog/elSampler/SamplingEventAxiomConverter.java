package de.elog.elSampler;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyRangeAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;
import de.elog.Constants;

public class SamplingEventAxiomConverter {

	private boolean printAddedAxioms = true;
	
	private String toString(OWLClass concept){
		if(concept.equals(OWLDataFactoryImpl.getInstance().getOWLNothing())){
			return Constants.BOTTOM_ELEMENT;
		}else if(concept.equals(OWLDataFactoryImpl.getInstance().getOWLThing())){
			return Constants.TOP_ELEMENT;
		}else{
			String output = concept.toString();
			String output2 = output.replace("<", "");
			String output3 = output2.replace(">", "");
			String[] split = output3.split("/");
			return split[split.length-1];
		}
	}
	
	private String toString(OWLObjectProperty property){
		String output = property.toString();
		String output2 = output.replace("<", "");
		String output3 = output2.replace(">", "");
		String[] split = output3.split("/");
		return split[split.length-1];
	}	
	
	protected boolean addSubsumes(OWLAxiom axiom, ArrayList<String> event1, ArrayList<String> event0){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression superSide = axiomSubClass.getSuperClass();
			OWLClassExpression subSide = axiomSubClass.getSubClass();
				if(superSide instanceof OWLClassImpl && subSide instanceof OWLClassImpl){
					OWLClass superClass = (OWLClass) superSide;
					OWLClass subClass = (OWLClass) subSide;
					System.out.println("axiom: " + axiom);
					System.out.println("annotations " + axiom.getAnnotations());
					if(this.isPositiveEvent(axiom)){
						if(printAddedAxioms) System.out.println("+ SubsumesPositive: " + toString(subClass) + " " + toString(superClass));
						event1.add(
								"subsumes|" + toString(subClass)+"|"+toString(superClass)
						);
					} else {
						if(printAddedAxioms) System.out.println("+ SubsumesNegative: " + toString(subClass) + " " + toString(superClass));
						event0.add(
								"subsumes|" + toString(subClass)+"|"+toString(superClass)
						);
					}
					return true;		
				}
			}
		return false;
	}
	
	protected boolean addIntersection(OWLAxiom axiom, ArrayList<String> event1, ArrayList<String> event0){
		if(axiom instanceof OWLDisjointClassesAxiomImpl){
			boolean everythingCouldBeUsed = true;
			OWLDisjointClassesAxiom axiomDisjointClass = (OWLDisjointClassesAxiom) axiom; 
			Set<OWLClassExpression> axiomDisjointClassExpressions = axiomDisjointClass.getClassExpressions();
			OWLClassExpression classExpression1 = axiomDisjointClassExpressions.iterator().next();
			for(OWLClassExpression classExpression2 : axiomDisjointClassExpressions){
				if(classExpression1 instanceof OWLClassImpl && classExpression2 instanceof OWLClassImpl){
					if(!classExpression1.equals(classExpression2)){
						
						if(this.isPositiveEvent(axiom)){
							event1.add("intersection|"+
									toString((OWLClass) classExpression1) + "|" + 
									toString((OWLClass) classExpression2) + "|" + 
									Constants.BOTTOM_ELEMENT
							);
							if(printAddedAxioms) System.out.println("+ intersectionPositive: " + toString((OWLClass) classExpression1)+" " +toString((OWLClass) classExpression2)+" "+ Constants.BOTTOM_ELEMENT);
						}else{
							event0.add("intersection|"+
									toString((OWLClass) classExpression1) + "|" + 
									toString((OWLClass) classExpression2) + "|" + 
									Constants.BOTTOM_ELEMENT
							);
							if(printAddedAxioms) System.out.println("+ intersectionNegative: " + toString((OWLClass) classExpression1)+" " +toString((OWLClass) classExpression2)+" "+ Constants.BOTTOM_ELEMENT);	
						}
					}
				} else {
					everythingCouldBeUsed = false;
				}
			}
			return everythingCouldBeUsed;
			}
		// (A and B) subclassof C ... where C is not Bottom.
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression superSide = axiomSubClass.getSuperClass();
			OWLClassExpression subSide = axiomSubClass.getSubClass();
				if(superSide instanceof OWLClassImpl && subSide instanceof OWLObjectIntersectionOfImpl){
					OWLObjectIntersectionOf subSideIntersection = (OWLObjectIntersectionOf) subSide;
					if(subSideIntersection.getOperands().size()==2){
						OWLClass superClass = (OWLClass) superSide;
						OWLClassExpression subClassExpr1 = subSideIntersection.getOperandsAsList().get(0);
						OWLClassExpression subClassExpr2 = subSideIntersection.getOperandsAsList().get(1);
						if(subClassExpr1 instanceof OWLClassImpl && subClassExpr2 instanceof OWLClassImpl){
							OWLClass subClass1 = (OWLClass) subClassExpr1;
							OWLClass subClass2 = (OWLClass) subClassExpr2;
							
							if(this.isPositiveEvent(axiom)){
								if(printAddedAxioms) System.out.println("+ intersectionPositive: " + toString(subClass1) + " and " + toString(subClass2) + " subclassof " + toString(superClass));
								event1.add("intersection|"+
										toString(subClass1) + "|" + toString(subClass2) + "|" + toString(superClass)
								);
							}else{
								if(printAddedAxioms) System.out.println("+ intersectionNegative: " + toString(subClass1) + " and " + toString(subClass2) + " subclassof " + toString(superClass));
								event0.add("intersection|"+
										toString(subClass1) + "|" + toString(subClass2) + "|" + toString(superClass)
								);
							}
							return true;
						}
					}
				}
			}
		return false;
	}
	
	/**
	 * Exists R.C subclassOf D : Range and suBBB
	 * 
	 * @return
	 */
	protected boolean addOpsub(OWLAxiom axiom, ArrayList<String> event1, ArrayList<String> event0){
		if(axiom instanceof OWLObjectPropertyRangeAxiomImpl){
			OWLObjectPropertyRangeAxiom rangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
			if(rangeAxiom.getRange() instanceof OWLClassImpl && rangeAxiom.getProperty() instanceof OWLObjectPropertyImpl){
				OWLObjectProperty rangeProperty = (OWLObjectProperty) rangeAxiom.getProperty();
				OWLClass rangeClass = (OWLClass) rangeAxiom.getRange();
				
				if(this.isPositiveEvent(axiom)){
					event1.add("opsub|"+
						toString(rangeProperty) + "|" + 
						Constants.TOP_ELEMENT + "|" + 
						toString(rangeClass)
					);
					if(printAddedAxioms) System.out.println("+ opsubPositive: Exists " + toString(rangeProperty) +"." + Constants.TOP_ELEMENT + " subclassof " + toString(rangeClass));
				} else {
					event0.add("opsub|"+
							toString(rangeProperty) + "|" + 
							Constants.TOP_ELEMENT + "|" + 
							toString(rangeClass)
					);
					if(printAddedAxioms) System.out.println("+ opsubNegative: Exists " + toString(rangeProperty) +"." + Constants.TOP_ELEMENT + " subclassof " + toString(rangeClass));					
				}
				return true;
			}
		}
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression rightSideExp = axiomSubClass.getSuperClass();
			OWLClassExpression leftSideExp = axiomSubClass.getSubClass();
			if(leftSideExp instanceof OWLObjectSomeValuesFromImpl){
				OWLObjectSomeValuesFrom leftPair = (OWLObjectSomeValuesFrom) leftSideExp;
				OWLObjectPropertyExpression includedPropertyE =   leftPair.getProperty();
				OWLClassExpression includedClassE = leftPair.getFiller();
				if((rightSideExp instanceof OWLClassImpl)&&(includedClassE instanceof OWLClassImpl)&&(includedPropertyE instanceof OWLObjectPropertyImpl)){
					OWLClass includedClass = (OWLClass) includedClassE;
					OWLObjectProperty includedProperty = (OWLObjectProperty) includedPropertyE;
					OWLClass leftClass = (OWLClass) rightSideExp;
					
					if(this.isPositiveEvent(axiom)){
						event1.add("opsub|"+
								toString(includedProperty) + "|" + 
								toString(includedClass) + "|" + 
								toString(leftClass)
						);
						if(printAddedAxioms) System.out.println("+ opsubPositive: Exists " + toString(includedProperty) +"." + toString(includedClass) + " subclassof " + toString(leftClass));
					} else{
						event0.add("opsub|"+
								toString(includedProperty) + "|" + 
								toString(includedClass) + "|" + 
								toString(leftClass)
						);
						if(printAddedAxioms) System.out.println("+ opsubPositive: Exists " + toString(includedProperty) +"." + toString(includedClass) + " subclassof " + toString(leftClass));
						
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * D subclassOf Exists R.C : suPPP
	 * 
	 * @return
	 */
	protected boolean addOpsup(OWLAxiom axiom, ArrayList<String> event1, ArrayList<String> event0){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression rightSide = axiomSubClass.getSuperClass();
			OWLClassExpression leftSideE = axiomSubClass.getSubClass();
			if(rightSide instanceof OWLObjectSomeValuesFromImpl){
				OWLObjectSomeValuesFrom rightPair = (OWLObjectSomeValuesFrom) rightSide;
				OWLObjectPropertyExpression includedPropertyE =   rightPair.getProperty();
				OWLClassExpression includedClassE = rightPair.getFiller();
				if((leftSideE instanceof OWLClassImpl)&&(includedClassE instanceof OWLClassImpl)&&(includedPropertyE instanceof OWLObjectPropertyImpl)){
					OWLClass includedClass = (OWLClass) includedClassE;
					OWLObjectProperty includedProperty = (OWLObjectProperty) includedPropertyE;
					OWLClass leftClass = (OWLClass) leftSideE;
					
					if(this.isPositiveEvent(axiom)){	
						event1.add("opsup|"+
								toString(includedProperty) + "|" + 
								toString(includedClass) + "|" + 
								toString(leftClass)
						);
						if(printAddedAxioms) System.out.println("+ opsupPositive: Exists " + toString(includedProperty) +"." + toString(includedClass) + " superclassof " + toString(leftClass));
						
					}else{
						event0.add("opsup|"+
								toString(includedProperty) + "|" + 
								toString(includedClass) + "|" + 
								toString(leftClass)
						);
						if(printAddedAxioms) System.out.println("+ opsupNegative: Exists " + toString(includedProperty) +"." + toString(includedClass) + " superclassof " + toString(leftClass).toString());
					}
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean addPsubsumes(OWLAxiom axiom, ArrayList<String> event1, ArrayList<String> event0){
		if(axiom instanceof OWLSubObjectPropertyOfAxiomImpl){
			OWLSubObjectPropertyOfAxiom axiomSubPropertyOf = (OWLSubObjectPropertyOfAxiom) axiom;
			OWLObjectPropertyExpression leftObjectProperty =  axiomSubPropertyOf.getSubProperty();
			OWLObjectPropertyExpression rightObjectProperty =  axiomSubPropertyOf.getSuperProperty();
				if(rightObjectProperty  instanceof OWLObjectPropertyImpl && leftObjectProperty  instanceof OWLObjectPropertyImpl){
					OWLObjectProperty leftOP = (OWLObjectProperty) leftObjectProperty;
					OWLObjectProperty rightOP = (OWLObjectProperty) rightObjectProperty;
					if(this.isPositiveEvent(axiom)){
						event1.add("psubsumes|"+
								toString(leftOP) + "|"+ toString(rightOP)
						);
						if(printAddedAxioms) System.out.println("+ psubsumesPositive: " + toString(leftOP)+" " +toString(rightOP));
					}else{
						event0.add("psubsumes|"+
								toString(leftOP) + "|"+ toString(rightOP)
						);
						if(printAddedAxioms) System.out.println("+ psubsumesNegative: " + toString(leftOP)+" " +toString(rightOP));
					}
					return true;
				}
		}
		
		return false;
	}

	protected boolean addPcom(OWLAxiom axiom, ArrayList<String> event1, ArrayList<String> event0){
		if(axiom instanceof OWLSubPropertyChainAxiomImpl){
			OWLSubPropertyChainOfAxiom chainAxiom = (OWLSubPropertyChainOfAxiom) axiom;
			if(chainAxiom.getPropertyChain().size()==2){
				OWLObjectPropertyExpression opExp1 = chainAxiom.getPropertyChain().get(0);
				OWLObjectPropertyExpression opExp2 = chainAxiom.getPropertyChain().get(1);
				OWLObjectPropertyExpression opSuperExpr = chainAxiom.getSuperProperty();
				if(opExp1 instanceof OWLObjectPropertyImpl && opExp2 instanceof OWLObjectPropertyImpl && opSuperExpr instanceof OWLObjectPropertyImpl){
					
					OWLObjectProperty op1 = (OWLObjectProperty) opExp1;
					OWLObjectProperty op2 = (OWLObjectProperty) opExp2;
					OWLObjectProperty opSuper = (OWLObjectProperty) opSuperExpr;
					if(this.isPositiveEvent(axiom)){						
						event1.add("pcom|"+
								toString(op1) + "|" +  toString(op2) + "|"+ toString(opSuper)
						);
						if(printAddedAxioms) System.out.println("+ pcomPositive: " + toString(op1) +" concat " + toString(op2) + " subclassof " + toString(opSuper));
					} else {
						event0.add("pcom|"+
								toString(op1) + "|" +  toString(op2) + "|"+ toString(opSuper)
						);
						if(printAddedAxioms) System.out.println("+ pcomNegative: " + toString(op1) +" concat " + toString(op2) + " subclassof " + toString(opSuper));
					}
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	public void setPrintAddedAxioms(boolean printAddedAxioms) {
		this.printAddedAxioms = printAddedAxioms;
	}

	protected boolean isPrintAddedAxioms() {
		return printAddedAxioms;
	}
	
	/**
	 * Gets the polarity of an event via the annotation property.
	 * 
	 * Returns true if the event is positive, false if it is negative.
	 * 
	 * If no confidence value exist, the function returns "null".
	 * 
	 * @param axiom
	 * @return
	 * @throws Exception 
	 */
	private Boolean isPositiveEvent(OWLAxiom axiom) {
		for(OWLAnnotation annotation : axiom.getAnnotations()){
			if(annotation.getProperty().getIRI().getFragment().toString().equalsIgnoreCase(Constants.ANNOTATION_PROPERTY_FOR_SAMPLING_POSITIVE_EVENT)){
				OWLAnnotationValue annValue = annotation.getValue();
				if(annValue instanceof OWLLiteralImpl){
					OWLLiteral literalValue = (OWLLiteral) annValue;
					if(literalValue.isBoolean()){
						return literalValue.parseBoolean();
					}					
				}
			}			
		}
		System.err.println("The boolean annotation-property " + Constants.ANNOTATION_PROPERTY_FOR_SAMPLING_POSITIVE_EVENT + " is missing. " +
				"The application will assume that the axiom " + axiom + " is a positive event.");
		return true;
	}
	
}

