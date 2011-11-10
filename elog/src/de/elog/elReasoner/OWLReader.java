package de.elog.elReasoner;

import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImpl;
import de.elog.Constants;
import de.elog.elConverter.ELOntology;
import de.elog.elConverter.GroundAxiom;
import de.unima.conn.ilpSolver.GurobiConnector;
import de.unima.exception.SolveException;


public class OWLReader {

	private ArrayList<String[]> objectProperty = new ArrayList<String[]>(); // Done
	private ArrayList<String[]> concept = new ArrayList<String[]>(); // done
	
	private ArrayList<String[]> subsumesEvidence = new ArrayList<String[]>(); // done
	private ArrayList<String[]> intersectionEvidence = new ArrayList<String[]>(); // partly done
		// Exists R.C subclassOf D : Range und suBBB
	private ArrayList<String[]> opsubEvidence = new ArrayList<String[]>(); 
	// D subclassOf Exists R.C : suPPP
	private ArrayList<String[]> opsupEvidence = new ArrayList<String[]>();
	private ArrayList<String[]> psubsumesEvidence = new ArrayList<String[]>();
	private ArrayList<String[]> pcomEvidence = new ArrayList<String[]>();


	private ArrayList<String[]> subsumesHard = new ArrayList<String[]>();
	private ArrayList<String[]> intersectionHard = new ArrayList<String[]>();
	private ArrayList<String[]> opsubHard = new ArrayList<String[]>();
	private ArrayList<String[]> opsupHard = new ArrayList<String[]>();
	private ArrayList<String[]> psubsumesHard = new ArrayList<String[]>();
	private ArrayList<String[]> pcomHard = new ArrayList<String[]>();


	private String[] putDoubleAfterValues(String[] values, Double conf){
		String[] result = new String[values.length+1];
		for(int i =0; i<values.length; i++){
			result[i] = values[i];
		}
		result[values.length] = conf.toString();
		return result;
	}
	
	/**
	 * Adds one single NORMALIZED soft to the axiom lists.
	 * 
	 * @param axiom
	 * @param elOntology
	 */
	private void addAxiomToCategorySoft(OWLAxiom axiom, Double confidenceValue, ELOntology elOntology){
		GroundAxiom newAxiom = elOntology.get_C1_subclassof_d(axiom);
		if(newAxiom!=null){
			this.subsumesEvidence.add(putDoubleAfterValues(newAxiom.getValue(),confidenceValue));
		}
		newAxiom = elOntology.get_C1_and_c2_subclassof_d(axiom);
		if(newAxiom!=null){
			this.intersectionEvidence.add(putDoubleAfterValues(newAxiom.getValue(),confidenceValue));
		}
		newAxiom = elOntology.get_Exists_r_c1_subclassof_d(axiom);
		if(newAxiom!=null){
			this.opsubEvidence.add(putDoubleAfterValues(newAxiom.getValue(),confidenceValue));
		}
		newAxiom = elOntology.get_C1_subclassof_exists_r_c2(axiom);
		if(newAxiom!=null){
			this.opsupEvidence.add(putDoubleAfterValues(newAxiom.getValue(),confidenceValue));
		}
		newAxiom = elOntology.get_R_subpropertyof_s(axiom);
		if(newAxiom!=null){
			this.psubsumesEvidence.add(putDoubleAfterValues(newAxiom.getValue(),confidenceValue));
		}
		newAxiom = elOntology.get_R1_com_r2_subpropertyof_s(axiom);
		if(newAxiom!=null){
			this.pcomEvidence.add(putDoubleAfterValues(newAxiom.getValue(),confidenceValue));
		}		
	}

	/**
	 * Builds variable name of form: subsumes|a|b|c.
	 * 
	 * @param axiom
	 * @param elOntology
	 */
	private String buildVariable(OWLAxiom axiom, ELOntology elOntology){
		String type = null;
		String[] stringValue = null;
		GroundAxiom newAxiom = elOntology.get_C1_subclassof_d(axiom);
		if(newAxiom!=null){
			type="subsumes";
			stringValue = newAxiom.getValue();
			
		}
		newAxiom = elOntology.get_C1_and_c2_subclassof_d(axiom);
		if(newAxiom!=null){
			type="intersection";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_Exists_r_c1_subclassof_d(axiom);
		if(newAxiom!=null){
			type="opsub";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_C1_subclassof_exists_r_c2(axiom);
		if(newAxiom!=null){
			type="opsup";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_R_subpropertyof_s(axiom);
		if(newAxiom!=null){
			type="psubsumes";
			stringValue = newAxiom.getValue();
		}
		newAxiom = elOntology.get_R1_com_r2_subpropertyof_s(axiom);
		if(newAxiom!=null){
			type="pcom";
			stringValue = newAxiom.getValue();
		}		
		if(stringValue==null){
			return null;
		}else{
			StringBuilder returnValue = new StringBuilder();
			returnValue.append(type);
			for(int i=0;i<stringValue.length;i++){
				returnValue.append("|").append(stringValue[i]);
			}
			return returnValue.toString();
		}
	}

	
	/**
	 * Adds one single NORMALIZED hard axiom to the axiom lists.
	 * 
	 * @param axiom
	 * @param elOntology
	 */
	private void addAxiomToCategoryHard(OWLAxiom axiom, ELOntology elOntology){
		GroundAxiom newAxiom = elOntology.get_C1_subclassof_d(axiom);
		if(newAxiom!=null){
			this.subsumesHard.add(newAxiom.getValue());
		}
		newAxiom = elOntology.get_C1_and_c2_subclassof_d(axiom);
		if(newAxiom!=null){
			this.intersectionHard.add(newAxiom.getValue());
		}
		newAxiom = elOntology.get_Exists_r_c1_subclassof_d(axiom);
		if(newAxiom!=null){
			this.opsubHard.add(newAxiom.getValue());
		}
		newAxiom = elOntology.get_C1_subclassof_exists_r_c2(axiom);
		if(newAxiom!=null){
			this.opsupHard.add(newAxiom.getValue());
		}
		newAxiom = elOntology.get_R_subpropertyof_s(axiom);
		if(newAxiom!=null){
			this.psubsumesHard.add(newAxiom.getValue());
		}
		newAxiom = elOntology.get_R1_com_r2_subpropertyof_s(axiom);
		if(newAxiom!=null){
			this.pcomHard.add(newAxiom.getValue());
		}		
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

	
	/**
	 * Reads the ontology and transforms it to the basic EL++ axioms:
	 * 
	 * - SubClassOf(C1 C2)            -> subsumes
	 * - (C1 and C2) subclassof D    -> intersection
	 * - C1 subclassof (Exists R.C2) -> opsup
	 * - (Exists R.C1) subclassof D  -> opsub
	 * - R  subclassof D  -> opsub
	 * 
	 * - r subpropertyof s           -> psubsumes
	 * 
	 * @param ontologyFilePath
	 * @return ontology IRI
	 * @throws OWLOntologyCreationException
	 * @throws SolveException 
	 */
	public IRI read(String ontologyFilePath) throws OWLOntologyCreationException, SolveException{
		
		ELOntology elOntology = new ELOntology();
		
		// Number of axioms schould stay constant.
		System.out.println("Loading ontology");
		HashSet<OWLAxiom> originalAxioms = elOntology.loadOntology(ontologyFilePath);
		HashSet<OWLAxiom> prenormalizedOntology = elOntology.normalizeAll();
		
		int numberOfComplexSoft = 0;
		
		// Concepts and Props
		for(OWLClass c : elOntology.getClasses()){
			String[] classArray = new String[1];
			classArray[0] = ELOntology.toString(c.toString());
			this.concept.add(classArray);
		}
		this.concept.add(new String[]{Constants.TOP_ELEMENT});
		this.concept.add(new String[]{Constants.BOTTOM_ELEMENT});
		// Concepts and Props
		for(OWLObjectProperty p : elOntology.getProperties()){
			String[] propArray = new String[1];
			propArray[0] = ELOntology.toString(p.toString());
			this.objectProperty.add(propArray);
		}
		
		for(OWLAxiom axiom : originalAxioms){
			// get confidence value (null if hard)
			
			Double confidenceValue = this.getConfidenceValue(axiom);

			// get normalization for this specific axiom
			HashSet<OWLAxiom> specificNormalizationAxioms = elOntology.normalizeAxiom(axiom, prenormalizedOntology);
			
			if(confidenceValue==null){
				for(OWLAxiom specAxiom : specificNormalizationAxioms){
					this.addAxiomToCategoryHard(specAxiom, elOntology);
				}
			}else {
				// if normalized axioms size == 1
				if(specificNormalizationAxioms.size()==1){
					for(OWLAxiom specAxiom : specificNormalizationAxioms){
						this.addAxiomToCategorySoft(specAxiom, confidenceValue, elOntology);
					}
				}else{
					numberOfComplexSoft++;
					GurobiConnector grbConnector = GurobiConnector.getGurobiConnector();

					String[] variableNames = new String[specificNormalizationAxioms.size()];
					boolean[] mustBePositive = new boolean[specificNormalizationAxioms.size()];
					int i = 0;					
					for( OWLAxiom specAxiom : specificNormalizationAxioms){
						variableNames[i] = this.buildVariable(specAxiom, elOntology);
						mustBePositive[i] = true;
						System.out.print(variableNames[i] + " + ");
						System.out.println();
						i++;
					}
					
					grbConnector.addSoftConstraint(confidenceValue, variableNames, mustBePositive, true);
				}
			}
		}

		
		System.out.println("Number of subsumes axioms: hard " + this.getSubsumesHard().size() + " soft " + this.getSubsumesEvidence().size());
		System.out.println("Number of intersection axioms: hard " + this.getIntersectionHard().size() + " soft " + this.getIntersectionEvidence().size());
		System.out.println("Number of opsub axioms: hard " + this.getOpsubHard().size() + " soft " + this.getOpsubEvidence().size());
		System.out.println("Number of opsup axioms: hard " + this.getOpsupHard().size() + " soft " + this.getOpsupEvidence().size());
		System.out.println("Number of psubsumes axioms: hard " + this.getPsubsumesHard().size() + " soft " + this.getOpsupEvidence().size());
		System.out.println("Number of pcom axioms: hard " + this.getPcomHard().size() + " soft " + this.getPcomEvidence().size());
		System.out.println("Number of complex soft axioms: " + numberOfComplexSoft);
		return elOntology.getOntologyId();
	}
	
	/**
	 * Returns all object properties.
	 * 
	 * @return property stored at position 0 of the String Array
	 */
	public ArrayList<String[]> getObjectProperty() {
		return objectProperty;
	}

	/**
	 * Returns all concepts.
	 * 
	 * @return concept stored at position 0 of the String Array
	 */
	public ArrayList<String[]> getConcept() {
		return concept;
	}

	/**
	 * Returns SubClassOf(C1 C2) (subsumes)
	 * 
	 * @return C1 at position 0, D at position 1, evidence value at position 2 of the array.
	 */
	public ArrayList<String[]> getSubsumesEvidence() {
		return subsumesEvidence;
	}


	/**
	 * Returns SubClassOf(IntersectionOf(C1 C2) D). (intersection)
	 * 
	 * Also deals with: DisjointClasses(<http://confOf#City> <http://confOf#Topic>) 
	 *  
	 * @return C1 at position 0, C2 at position 1, D at position 2, evidence value at position 3.
	 */
	public ArrayList<String[]> getIntersectionEvidence() {
		return intersectionEvidence;
	}

	/**
	 * SubClassOf(ObjectSomeValuesFrom(R C1) D)  -> opsub
	 * 
	 * Also deals with ObjectPropertyRange(R D).
	 * 
	 * @return R at position 0, C1 at position 1, D at position 2, evidence value at position 3 of the array.
	 */
	public ArrayList<String[]> getOpsubEvidence() {
		return opsubEvidence;
	}
	
	/**
	 * Returns SubClassOf(C1 ObjectSomeValuesFrom(R C2))
	 *  
	 * @return R at position 0, C2 at position 1, C1 at position 2, evidence value at position 3 of the array.
	 */
	public ArrayList<String[]> getOpsupEvidence() {
		return opsupEvidence;
	}

	/**
	 * SubObjectPropertyOf(R S). (psubsumes)
	 * 
	 * @return r at position 0, s at position 1, evidence value at position 2 of the array.
	 */
	public ArrayList<String[]> getPsubsumesEvidence() {
		return psubsumesEvidence;
	}
	/**
	 * Returns SubClassOf(C1 C2) (subsumes)
	 * 
	 * @return C1 at position 0, D at position 1 of the array.
	 */
	public ArrayList<String[]> getSubsumesHard() {
		return subsumesHard;
	}
	/**
	 * Returns SubClassOf(IntersectionOf(C1 C2) D). (intersection)
	 * 
	 * Also deals with: DisjointClasses(<http://confOf#City> <http://confOf#Topic>) 
	 *  
	 * @return C1 at position 0, C2 at position 1, D at position 2 of the array.
	 */
	public ArrayList<String[]> getIntersectionHard() {
		return intersectionHard;
	}
	/** 
	 * SubClassOf(ObjectSomeValuesFrom(R C1) D)  -> opsub
	 * 
	 * Also deals with ObjectPropertyRange(R D).
	 * 
	 * @return R at position 0, C1 at position 1, D at position 2 of the array.
	 */	
	public ArrayList<String[]> getOpsubHard() {
		return opsubHard;
	}
	
	/**
	 * Returns SubClassOf(C1 ObjectSomeValuesFrom(R C2)) (opsup)
	 * 
	 * @return R at position 0, C2 at position 1, C1 at position 2 of the array.
	 */
	public ArrayList<String[]> getOpsupHard() {
		return opsupHard;
	}
	/**
	 * SubObjectPropertyOf(R S). (psubsumes)
	 * 
	 * @return r at position 0, s at position 1 of the array.
	 */
	public ArrayList<String[]> getPsubsumesHard() {
		return psubsumesHard;
	}
	
	/**
	 * SubPropertyChain(R S T)
	 * 
	 * @return r at position 0, s at position 1, t at position 2, evidence value at positon 3 of the array.
	 */
	public ArrayList<String[]> getPcomEvidence() {
		return pcomEvidence;
	}
	/**
	 * SubPropertyChain(R S T)
	 * 
	 * @return r at position 0, s at position 1, t at position 2 of the array.
	 */
	public ArrayList<String[]> getPcomHard() {
		return pcomHard;
	}
}

