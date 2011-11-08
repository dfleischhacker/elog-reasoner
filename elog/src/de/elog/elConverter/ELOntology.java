package de.elog.elConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubPropertyChainAxiomImpl;

import de.elog.elConverter.transformators.DisjointClassesTransformator;
import de.elog.elConverter.transformators.ElNormalizer;
import de.elog.elConverter.transformators.EquivalentClassesTransformator;
import de.elog.elConverter.transformators.PropertyDomainTransformator;
import de.elog.elConverter.transformators.PropertyRangeTransformator;
import de.elog.elConverter.transformators.ReflexiveRoleTransformator;
import de.elog.elConverter.transformators.TransformatorManager;
import de.elog.elConverter.transformators.TransitiveRoleTransformator;

/**
 * Main class for the EL-Converter tool. Combines all the transformators in the 
 * correct order and performs downsizing to EL++ and normalization to the 6
 * ground axioms of EL++.
 * 
 * It also normalizes Range restrictions.
 * 
 * @author jan
 *
 */

public class ELOntology{
	private OWLDataFactory factory ;
	private HashSet<OWLAxiom> axioms;

	private Set<OWLClass> classes;
	private Set<OWLObjectProperty> properties;
	
	private TransformatorManager converterManager;
	private PropertyRangeTransformator rangeConverter;;
	
	private OWLOntology owlOntology;
	
	private OWLOntologyManager manager;


	public void addClass(OWLClass concept){
		this.classes.add(concept);
	}
	

	public void addProperty(OWLObjectProperty property){
		this.properties.add(property);
	}
	
	public OWLOntologyManager getManager() {
		return manager;
	}


	public ELOntology() {
		converterManager = this.getNormalizePipeline();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IRI getOntologyId(){
		return this.owlOntology.getOntologyID().getOntologyIRI();
	}
	
	/**
	 * Reads an ontology from the given path and downsize it to EL++.
	 * 
	 * Normalization is NOT performed by this method.
	 * 
	 * @param path
	 * @throws OWLOntologyCreationException
	 */
	public HashSet<OWLAxiom> loadOntology(String path) throws OWLOntologyCreationException{
		manager = OWLManager.createOWLOntologyManager();
		this.factory =OWLManager.getOWLDataFactory();
		owlOntology = manager.loadOntologyFromOntologyDocument(new File(path));
		// set classes and properties
		this.classes =owlOntology.getClassesInSignature();
		this.properties =owlOntology.getObjectPropertiesInSignature();
		
		axioms = new HashSet<OWLAxiom>();
		
		axioms.addAll(owlOntology.getAxioms());
		System.out.println("Axioms:" + axioms.size());
		OWL2ELProfile profile = new OWL2ELProfile();
		OWLProfileReport report = profile.checkOntology(owlOntology);
		System.out.println("The following axioms are more expressive than OWL EL. They are deleted:");
		for(OWLProfileViolation v :report.getViolations()){
			System.out.println(v);
			
			if(v.getAxiom()!=null){
				axioms.remove(v.getAxiom());
				RemoveAxiom r = new RemoveAxiom(owlOntology, v.getAxiom());
				manager.applyChange(r);
			}
		}
		return axioms;
	}


	private TransformatorManager getNormalizePipeline(){
		TransformatorManager converterManager = new TransformatorManager();
		converterManager.addConverter(new PropertyDomainTransformator());
		converterManager.addConverter(new DisjointClassesTransformator());
		converterManager.addConverter(new EquivalentClassesTransformator());
		converterManager.addConverter(new TransitiveRoleTransformator());
		converterManager.addConverter(new ReflexiveRoleTransformator());
		converterManager.addConverter(new ElNormalizer());
		return converterManager;
	}
	
	/**
	 * Converts ALL the given Axioms to a set of normalized axioms. 
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
	 */
	public HashSet<OWLAxiom> normalizeAll(){
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		// normalize everything without range
		for(OWLAxiom axiom:axioms){
			result.addAll(this.normalizeWithoutRange(axiom));
		}
		// normalize range (now the range axioms have been transformed)
		this.axioms=result;
		
		result = new HashSet<OWLAxiom>();
		for(OWLAxiom axiom:axioms){
			result.addAll(this.normalizeRange(axiom, result));
		}
		// normalize everything (inclusive transformed range axioms). This should work very fast.
		this.axioms=result;
		result = new HashSet<OWLAxiom>();
		for(OWLAxiom axiom:axioms){
			result.addAll(this.normalizeWithoutRange(axiom));
		}
		
		this.axioms=result;
		return axioms;
	}

	/**
	 * Normalizes one given axiom and returns the normalized axioms in a set.
	 * 
	 * Does not normalize the Range of the axiom.
	 * 
	 * @param axiom
	 * @return
	 */
	private HashSet<OWLAxiom> normalizeWithoutRange(OWLAxiom axiom){
		return converterManager.convert(axiom, this.getFactory(), this);
	}
	
	/**
	 * Normalizes the Range of the axiom. Therefore, a list with all other (already normalized) 
	 * axioms in the ontology is needed.
	 * 
	 * @param axiom
	 * @param allOtherNormalizedAxioms
	 * @return
	 */
	private HashSet<OWLAxiom> normalizeRange(OWLAxiom axiom, HashSet<OWLAxiom> allOtherNormalizedAxioms){
		this.rangeConverter = new PropertyRangeTransformator(allOtherNormalizedAxioms);
		return rangeConverter.convert(axiom, this.getFactory(),this);
	}
	
	public HashSet<OWLAxiom> normalizeAxiom(OWLAxiom axiom, HashSet<OWLAxiom> allOtherNormalizedAxioms){
		HashSet<OWLAxiom> temp = new HashSet<OWLAxiom>();
		HashSet<OWLAxiom> results = new HashSet<OWLAxiom>();
		results.add(axiom);
		// normalize everything without range
		for(OWLAxiom a:results){
			temp.addAll(this.normalizeWithoutRange(a));
		}
		// normalize range (now the range axioms have been transformed)
		results=temp;
		
		temp = new HashSet<OWLAxiom>();
		for(OWLAxiom a:results){
			temp.addAll(this.normalizeRange(a, temp));
		}
		// normalize everything (inclusive transformed range axioms). This should work very fast.
		results=temp;
		temp = new HashSet<OWLAxiom>();
		for(OWLAxiom a:results){
			temp.addAll(this.normalizeWithoutRange(a));
		}
		results = temp;
		return results;
		
		
		
	}
	
	public OWLDataFactory getFactory() {
		return factory;
	}
	
	public HashSet<OWLAxiom> getAxioms() {
		return axioms;
	}

	public GroundAxiom get_C1_subclassof_d(OWLAxiom axiom) {
		if(axiom instanceof OWLSubClassOfAxiomImpl){
			OWLSubClassOfAxiom axiomSubClass = (OWLSubClassOfAxiom) axiom;
			OWLClassExpression superSide = axiomSubClass.getSuperClass();
			OWLClassExpression subSide = axiomSubClass.getSubClass();
				if(superSide instanceof OWLClassImpl && subSide instanceof OWLClassImpl){
					OWLClass superClass = (OWLClass) superSide;
					OWLClass subClass = (OWLClass) subSide;
					String[] output = new String[] {
						toString(subClass), toString(superClass)
					};
					
					return new GroundAxiom(output, axiom);		
				}
			}
		return null;
	}
	
	public GroundAxiom get_C1_and_c2_subclassof_d(OWLAxiom axiom) {
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
							String[] result = new String[] {
									toString(subClass1), toString(subClass2), toString(superClass)
							};
							return new GroundAxiom(result, axiom);
						}
					}
				}
			}
	
		
		return null;
	}

	/**
	 * other name: opSup
	 * 
	 * @param axiom
	 * @return
	 */

	public GroundAxiom get_C1_subclassof_exists_r_c2(OWLAxiom axiom) {
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
					String[] result = new String[]{
								toString(includedProperty),
								toString(includedClass),
								toString(leftClass)
					};
					return new GroundAxiom(result, axiom);
				}
			}
		}
		return null;
	}

	/**
	 * Other name: opsub.
	 * 
	 * @param axiom
	 * @return
	 */
	public GroundAxiom get_Exists_r_c1_subclassof_d(OWLAxiom axiom) {
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
					String[] result =new String[]{
								toString(includedProperty),
								toString(includedClass),
								toString(leftClass)
						};
					return new GroundAxiom(result, axiom);
				}
			}
		}
		return null;
	}


	public GroundAxiom get_R_subpropertyof_s(OWLAxiom axiom) {
		if(axiom instanceof OWLSubObjectPropertyOfAxiomImpl){
			OWLSubObjectPropertyOfAxiom axiomSubPropertyOf = (OWLSubObjectPropertyOfAxiom) axiom;
			OWLObjectPropertyExpression leftObjectProperty =  axiomSubPropertyOf.getSubProperty();
			OWLObjectPropertyExpression rightObjectProperty =  axiomSubPropertyOf.getSuperProperty();
			if(rightObjectProperty  instanceof OWLObjectPropertyImpl && leftObjectProperty  instanceof OWLObjectPropertyImpl){
				OWLObjectProperty leftOP = (OWLObjectProperty) leftObjectProperty;
				OWLObjectProperty rightOP = (OWLObjectProperty) rightObjectProperty;
		
				String[] output = new String[]{
						toString(leftOP), toString(rightOP)
				};
				return new GroundAxiom(output, axiom);
			}
		}
		return null;
	}


	public GroundAxiom get_R1_com_r2_subpropertyof_s(OWLAxiom axiom) {
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
					String[] output =new String[]{
								toString(op1), toString(op2), toString(opSuper)
					};
					return new GroundAxiom(output, axiom);
				}
			}
			
		}
		
		return null;
	}
	public ArrayList<GroundAxiom> getAll_C1_subclassof_d() {
		ArrayList<GroundAxiom> result = new ArrayList<GroundAxiom>();
		for(OWLAxiom a : axioms){
			GroundAxiom temp = this.get_C1_subclassof_d(a);
			if(temp!=null) result.add(temp);
		}
		return result;
	}
		

	public ArrayList<GroundAxiom> getAll_C1_and_c2_subclassof_d() {
		ArrayList<GroundAxiom> result = new ArrayList<GroundAxiom>();
		for(OWLAxiom a : axioms){
			GroundAxiom temp = this.get_C1_and_c2_subclassof_d(a);
			if(temp!=null) result.add(temp);
		}
		return result;
	}
	
	public ArrayList<GroundAxiom> getAll_C1_subclassof_exists_r_c2() {
		ArrayList<GroundAxiom> result = new ArrayList<GroundAxiom>();
		for(OWLAxiom a : axioms){
			GroundAxiom temp = this.get_C1_subclassof_exists_r_c2(a);
			if(temp!=null) result.add(temp);
		}
		return result;
	}

	public ArrayList<GroundAxiom> getAll_Exists_r_c1_subclassof_d() {
		ArrayList<GroundAxiom> result = new ArrayList<GroundAxiom>();
		for(OWLAxiom a : axioms){
			GroundAxiom temp = this.get_Exists_r_c1_subclassof_d(a);
			if(temp!=null) result.add(temp);
		}
		return result;
	}

	public ArrayList<GroundAxiom> getAll_R_subpropertyof_s() {
		ArrayList<GroundAxiom> result = new ArrayList<GroundAxiom>();
		for(OWLAxiom a : axioms){
			GroundAxiom temp = this.get_R_subpropertyof_s(a);
			if(temp!=null) result.add(temp);
		}
		return result;
	}

	public ArrayList<GroundAxiom> getAll_R1_com_r2_subpropertyof_s() {
		ArrayList<GroundAxiom> result = new ArrayList<GroundAxiom>();
		for(OWLAxiom a : axioms){
			GroundAxiom temp = this.get_R1_com_r2_subpropertyof_s(a);
			if(temp!=null) result.add(temp);
		}
		return result;
	}
	
	


	public Set<OWLClass> getClasses() {
		return classes;
	}

	public Set<OWLObjectProperty> getProperties() {
		return properties;
	}

	
	public static String toString(OWLClass concept){
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
	
	/*public static String toString(EntityPair ep){
		StringBuilder sb=new StringBuilder();
		if(ep.getEntity1() instanceof OWLClass){
			OWLClass class1 = (OWLClass) ep.getEntity1();
			OWLClass class2 = (OWLClass) ep.getEntity2();
			sb.append("cmap|").append(toString(class1)).append("|").append(toString(class2));
			return sb.toString();
		}else{
			OWLObjectProperty p1 = (OWLObjectProperty) ep.getEntity1();
			OWLObjectProperty p2 = (OWLObjectProperty) ep.getEntity2();
			sb.append("pmap|").append(toString(p1)).append("|").append(toString(p2));
		}
		return sb.toString();
	}*/
	
	public static String toString(OWLEntity concept){
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
	
	public static String toString(String iri){
		String output2 = iri.replace("<", "");
		String output3 = output2.replace(">", "");
		String[] split = output3.split("/");
		return split[split.length-1];
	}
	
	public static String toString(OWLObjectProperty property){
		String output = property.toString();
		String output2 = output.replace("<", "");
		String output3 = output2.replace(">", "");
		String[] split = output3.split("/");
		return split[split.length-1];
	}
	public OWLOntology getOwlOntology() {
		return owlOntology;
	}
}
