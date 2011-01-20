package de.elog.elReasoner;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import de.elog.Constants;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDeclarationAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyRangeAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

public class AxiomConverter {

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
	
	
	public boolean addTopConcept(ArrayList<String[]> output){
		output.add(new String[]{Constants.TOP_ELEMENT});
		return true;
	}
	
	public boolean addBottomConcept(ArrayList<String[]> output){
		output.add(new String[]{Constants.BOTTOM_ELEMENT});
		return true;
	}
	
	public boolean addConcept(OWLAxiom axiom, ArrayList<String[]> output){
		if(axiom instanceof OWLDeclarationAxiomImpl){
			OWLDeclarationAxiom axiomDeclaration = (OWLDeclarationAxiom) axiom;
			OWLEntity declaredEntity =  axiomDeclaration.getEntity();
			if(declaredEntity instanceof OWLClassImpl){
				OWLClass concept = (OWLClass) declaredEntity;
				if(printAddedAxioms) System.out.println("+ Concept: " + toString(concept));
				output.add(new String[]{toString(concept)});
				return true;
				}
			}
		return false;
	}

	public boolean addObjectProperty(OWLAxiom axiom, ArrayList<String[]> output){
		if(axiom instanceof OWLDeclarationAxiomImpl){
			OWLDeclarationAxiom axiomDeclaration = (OWLDeclarationAxiom) axiom;
			OWLEntity declaredEntity =  axiomDeclaration.getEntity();
			if((declaredEntity instanceof OWLObjectPropertyImpl)){
				OWLObjectProperty property = (OWLObjectProperty) declaredEntity;
				if(printAddedAxioms) System.out.println("+ Property: " + toString(property));
				output.add(new String[]{toString(property)});
				return true;
			}
		}
		return false;
	}
	
	public boolean addSubsumes(OWLAxiom axiom, ArrayList<String[]> hardOutput, ArrayList<String[]> softOutput){
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression superSide = axiomSubClass.getSuperClass();
			OWLClassExpression subSide = axiomSubClass.getSubClass();
				if(superSide instanceof OWLClassImpl && subSide instanceof OWLClassImpl){
					Double confidence = 
						this.getConfidenceValue(axiom);
					OWLClass superClass = (OWLClass) superSide;
					OWLClass subClass = (OWLClass) subSide;
					if(confidence == null){
						if(printAddedAxioms) System.out.println("+ SubsumesHard: " + toString(subClass) + " " + toString(superClass));
						hardOutput.add(new String[] {
								toString(subClass), toString(superClass)
							}
						);
					} else {
						if(printAddedAxioms) System.out.println("+ SubsumesSoft: " + toString(subClass) + " " + toString(superClass) + " " + confidence);
						softOutput.add(new String[] {
								toString(subClass), toString(superClass), confidence.toString()
							}
						);
					}
					return true;		
				}
			}
		return false;
	}
	
	public boolean addIntersection(OWLAxiom axiom, ArrayList<String[]> hardOutput, ArrayList<String[]> softOutput){
		if(axiom instanceof OWLDisjointClassesAxiomImpl){
			boolean everythingCouldBeUsed = true;
			OWLDisjointClassesAxiom axiomDisjointClass = (OWLDisjointClassesAxiom) axiom; 
			Set<OWLClassExpression> axiomDisjointClassExpressions = axiomDisjointClass.getClassExpressions();
			OWLClassExpression classExpression1 = axiomDisjointClassExpressions.iterator().next();
			for(OWLClassExpression classExpression2 : axiomDisjointClassExpressions){
				if(classExpression1 instanceof OWLClassImpl && classExpression2 instanceof OWLClassImpl){
					if(!classExpression1.equals(classExpression2)){
						Double confidence = this.getConfidenceValue(axiom);
						if(confidence==null){
							hardOutput.add(new String[]{
									toString((OWLClass) classExpression1),
									toString((OWLClass) classExpression2),
									Constants.BOTTOM_ELEMENT
							});
							if(printAddedAxioms) System.out.println("+ intersectionHard: " + toString((OWLClass) classExpression1)+" " +toString((OWLClass) classExpression2)+" "+ Constants.BOTTOM_ELEMENT);
						}else{
							softOutput.add(new String[]{
									toString((OWLClass) classExpression1),
									toString((OWLClass) classExpression2),
									Constants.BOTTOM_ELEMENT,
									confidence.toString()
							});
							if(printAddedAxioms) System.out.println("+ intersectionSoft: " + toString((OWLClass) classExpression1)+" " +toString((OWLClass) classExpression2)+" "+ Constants.BOTTOM_ELEMENT + " " + confidence);	
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
							Double confidence = this.getConfidenceValue(axiom);
							if(confidence==null){
								if(printAddedAxioms) System.out.println("+ intersectionHard: " + toString(subClass1) + " and " + toString(subClass2) + " subclassof " + toString(superClass));
								hardOutput.add(new String[] {
										toString(subClass1), toString(subClass2), toString(superClass)
									}
								);
							}else{
								if(printAddedAxioms) System.out.println("+ intersectionSoft: " + toString(subClass1) + " and " + toString(subClass2) + " subclassof " + toString(superClass) + " " + confidence);
								softOutput.add(new String[] {
										toString(subClass1), toString(subClass2), toString(superClass), confidence.toString()
									}
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
	public boolean addOpsub(OWLAxiom axiom, ArrayList<String[]> hardOutput, ArrayList<String[]> softOutput){
		if(axiom instanceof OWLObjectPropertyRangeAxiomImpl){
			OWLObjectPropertyRangeAxiom rangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
			if(rangeAxiom.getRange() instanceof OWLClassImpl && rangeAxiom.getProperty() instanceof OWLObjectPropertyImpl){
				OWLObjectProperty rangeProperty = (OWLObjectProperty) rangeAxiom.getProperty();
				OWLClass rangeClass = (OWLClass) rangeAxiom.getRange();
				Double confidence = this.getConfidenceValue(axiom);
				if(confidence==null){
					hardOutput.add(new String[]{
						toString(rangeProperty),
						Constants.TOP_ELEMENT,
						toString(rangeClass)
					});
					if(printAddedAxioms) System.out.println("+ opsubHard: Exists " + toString(rangeProperty) +"." + Constants.TOP_ELEMENT + " subclassof " + toString(rangeClass));
				} else {
					softOutput.add(new String[]{
							toString(rangeProperty),
							Constants.TOP_ELEMENT,
							toString(rangeClass),
							confidence.toString()
						});
					if(printAddedAxioms) System.out.println("+ opsubSoft: Exists " + toString(rangeProperty) +"." + Constants.TOP_ELEMENT + " subclassof " + toString(rangeClass) + " " + confidence);					
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
					Double confidence = this.getConfidenceValue(axiom);
					if(confidence==null){
						hardOutput.add(new String[]{
								toString(includedProperty),
								toString(includedClass),
								toString(leftClass)
						});
						if(printAddedAxioms) System.out.println("+ opsubHard: Exists " + toString(includedProperty) +"." + toString(includedClass) + " subclassof " + toString(leftClass));
					} else{
						softOutput.add(new String[]{
								toString(includedProperty),
								toString(includedClass),
								toString(leftClass),
								confidence.toString()
						});
						if(printAddedAxioms) System.out.println("+ opsubHard: Exists " + toString(includedProperty) +"." + toString(includedClass) + " subclassof " + toString(leftClass) + " " + confidence);
						
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
	public boolean addOpsup(OWLAxiom axiom, ArrayList<String[]> hardOutput, ArrayList<String[]> softOutput){
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
					Double confidence = this.getConfidenceValue(axiom);
					if(confidence==null){	
						hardOutput.add(new String[]{
								toString(includedProperty),
								toString(includedClass),
								toString(leftClass)
						});
						if(printAddedAxioms) System.out.println("+ opsupHard: Exists " + toString(includedProperty) +"." + toString(includedClass) + " superclassof " + toString(leftClass));
						
					}else{
						softOutput.add(new String[]{
								toString(includedProperty),
								toString(includedClass),
								toString(leftClass),
								confidence.toString()
						});
						if(printAddedAxioms) System.out.println("+ opsupSoft: Exists " + toString(includedProperty) +"." + toString(includedClass) + " superclassof " + toString(leftClass) + " " + confidence.toString());
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean addPsubsumes(OWLAxiom axiom, ArrayList<String[]> hardOutput, ArrayList<String[]> softOutput){
		if(axiom instanceof OWLSubObjectPropertyOfAxiomImpl){
			OWLSubObjectPropertyOfAxiom axiomSubPropertyOf = (OWLSubObjectPropertyOfAxiom) axiom;
			OWLObjectPropertyExpression leftObjectProperty =  axiomSubPropertyOf.getSubProperty();
			OWLObjectPropertyExpression rightObjectProperty =  axiomSubPropertyOf.getSuperProperty();
				if(rightObjectProperty  instanceof OWLObjectPropertyImpl && leftObjectProperty  instanceof OWLObjectPropertyImpl){
					OWLObjectProperty leftOP = (OWLObjectProperty) leftObjectProperty;
					OWLObjectProperty rightOP = (OWLObjectProperty) rightObjectProperty;
					Double confidence = this.getConfidenceValue(axiom);

					if(confidence == null){
						hardOutput.add(new String[]{
								toString(leftOP), toString(rightOP)
						});
						if(printAddedAxioms) System.out.println("+ psubsumesHard: " + toString(leftOP)+" " +toString(rightOP));
					}else{
						softOutput.add(new String[]{
								toString(leftOP), toString(rightOP), confidence.toString()
						});
						if(printAddedAxioms) System.out.println("+ psubsumesSoft: " + toString(leftOP)+" " +toString(rightOP) + " " + confidence);
					}
					return true;
				}
		}
		
		return false;
	}

	public boolean addPcom(OWLAxiom axiom, ArrayList<String[]> hardOutput, ArrayList<String[]> softOutput){
		if(axiom instanceof OWLSubPropertyChainAxiomImpl){
			OWLSubPropertyChainOfAxiom chainAxiom = (OWLSubPropertyChainOfAxiom) axiom;
			if(chainAxiom.getPropertyChain().size()==2){
				OWLObjectPropertyExpression opExp1 = chainAxiom.getPropertyChain().get(0);
				OWLObjectPropertyExpression opExp2 = chainAxiom.getPropertyChain().get(1);
				OWLObjectPropertyExpression opSuperExpr = chainAxiom.getSuperProperty();
				if(opExp1 instanceof OWLObjectPropertyImpl && opExp2 instanceof OWLObjectPropertyImpl && opSuperExpr instanceof OWLObjectPropertyImpl){
					Double confidence = this.getConfidenceValue(axiom);
					OWLObjectProperty op1 = (OWLObjectProperty) opExp1;
					OWLObjectProperty op2 = (OWLObjectProperty) opExp2;
					OWLObjectProperty opSuper = (OWLObjectProperty) opSuperExpr;
					if(confidence==null){						
						hardOutput.add(new String[]{
								toString(op1), toString(op2), toString(opSuper)
						});
						if(printAddedAxioms) System.out.println("+ pcomHard: " + toString(op1) +" concat " + toString(op2) + " subclassof " + toString(opSuper));
					} else {
						softOutput.add(new String[]{
								toString(op1), 
								toString(op2), 
								toString(opSuper),
								confidence.toString()
						});
						if(printAddedAxioms) System.out.println("+ pcomSoft: " + toString(op1) +" concat " + toString(op2) + " subclassof " + toString(opSuper) + " " + confidence);
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

	public boolean isPrintAddedAxioms() {
		return printAddedAxioms;
	}
	
	/**
	 * Gets the confidence value via the "confidence" annotation property. 
	 * 
	 * If no confidence value exist, the function returns "null".
	 * 
	 * @param axiom
	 * @return
	 */
	private Double getConfidenceValue(OWLAxiom axiom){
		for(OWLAnnotation annotation : axiom.getAnnotations()){
			if(annotation.getProperty().getIRI().getFragment().toString().equalsIgnoreCase(
					Constants.ANNOTATION_PROPERTY_FOR_REASONING_CONFIDENCE_VALUE)){
				OWLAnnotationValue annValue = annotation.getValue();
				if(annValue instanceof OWLLiteralImpl){
					OWLLiteral literalValue = (OWLLiteral) annValue;
					if(literalValue.isDouble()){
						return literalValue.parseDouble();
					}					
				}
			}			
		}
		return null;

	}
	
}

