package de.elog.elGreedy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLFunctionalObjectPropertyAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSymmetricObjectPropertyAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;
import de.elog.elReasoner.AxiomConverter;


public class OWLGreedyReader {

	private ArrayList<OwnAxiom> hardAxioms = new ArrayList<OwnAxiom>();
	private HashMap<OwnAxiom,Double> softAxioms = new HashMap<OwnAxiom,Double>();
	
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
	 */
	public IRI read(String ontologyFilePath) throws OWLOntologyCreationException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		File file = new File(ontologyFilePath);
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		AxiomConverter converter = new AxiomConverter();
		
		// do not print the added axioms
		converter.setPrintAddedAxioms(false);
		System.out.println("Not converted axioms:");
		for(OWLAxiom axiom : ontology.getAxioms()){
			ArrayList<String[]> hardList = new ArrayList<String[]>();
			ArrayList<String[]> softList = new ArrayList<String[]>();
			
			if(converter.addSubsumes(axiom, hardList, softList)){
				if(hardList.size()!=0){
					this.hardAxioms.add(new OwnAxiom(this.convertHard("subsumes", hardList),axiom,0));
					
				}else{
					ArrayList<String> result = new ArrayList<String>();
					double value = this.convertSoft("subsumes", softList, result);
					this.softAxioms.put(new OwnAxiom(result,axiom,value),value);
				}
				
			}else if(converter.addIntersection(axiom, hardList, softList)){
				if(hardList.size()!=0){
					
					this.hardAxioms.add(new OwnAxiom(this.convertHard("intersection", hardList),axiom,0));
				}else{
					ArrayList<String> result = new ArrayList<String>();
					double value = this.convertSoft("intersection", softList, result);
					this.softAxioms.put(new OwnAxiom(result,axiom,value),value);
				}
			}else if(converter.addOpsub(axiom, hardList, softList)){
				if(hardList.size()!=0){
					this.hardAxioms.add(new OwnAxiom(this.convertHard("opsub", hardList),axiom,0));
				}else{
					ArrayList<String> result = new ArrayList<String>();
					double value = this.convertSoft("opsub", softList, result);
					this.softAxioms.put(new OwnAxiom(result,axiom,value),value);
				}
			}else if(converter.addOpsup(axiom, hardList, softList)){
				if(hardList.size()!=0){
					this.hardAxioms.add(new OwnAxiom(this.convertHard("opsup", hardList),axiom,0));
				}else{
					ArrayList<String> result = new ArrayList<String>();
					double value = this.convertSoft("opsup", softList, result);
					this.softAxioms.put(new OwnAxiom(result,axiom,value),value);
				}
				
			}else if(converter.addPsubsumes(axiom, hardList, softList)){
				if(hardList.size()!=0){
					this.hardAxioms.add(new OwnAxiom(this.convertHard("psubsumes", hardList),axiom,0));
				}else{
					ArrayList<String> result = new ArrayList<String>();
					double value = this.convertSoft("psubsumes", softList, result);
					this.softAxioms.put(new OwnAxiom(result,axiom,value),value);
				}
			}else if(converter.addPcom(axiom, hardList, softList)){
				if(hardList.size()!=0){
					this.hardAxioms.add(new OwnAxiom(this.convertHard("pcom", hardList),axiom,0));
				} else {
					ArrayList<String> result = new ArrayList<String>();
					double value = this.convertSoft("pcom", softList, result);
					this.softAxioms.put(new OwnAxiom(result,axiom,value),value);
				}
			}else{
				// data properties are not considered. Only object properties.
				// All annotation assertions do not contain important information for the ontology. Therefore, they are not considered.
				// The symmetric, transitive and functional object properties are already considered as "normal" object properties. Only the additional infromation about transitivity and functional is lost.
				if(!axiom.toString().contains("DataProperty") 
					&& !axiom.toString().contains("AnnotationAssertion")
					&& !axiom.toString().contains("Declaration")
					&& !(axiom instanceof OWLTransitiveObjectPropertyAxiomImpl)
					&& !(axiom instanceof OWLFunctionalObjectPropertyAxiomImpl)
					&& !(axiom instanceof OWLSymmetricObjectPropertyAxiomImpl)
				){
					System.out.println("- " + axiom);
				}
			}
		}
		OWLOntologyID id = ontology.getOntologyID();
		return id.getOntologyIRI();
	}
	public ArrayList<OwnAxiom> getHardAxioms() {
		return hardAxioms;
	}

	public HashMap<OwnAxiom,Double> getSoftAxioms() {
		return softAxioms;
	}
	public ArrayList<String> convertHard(String predName, ArrayList<String[]> varsList){
		ArrayList<String> result = new ArrayList<String>();
		for(String[] vars : varsList){
			StringBuilder sb = new StringBuilder();
			sb.append(predName);
			for(String s : vars){
				sb.append("|").append(s);
			}
			result.add(sb.toString());
		}
		return result;
	}
	
	public double convertSoft(String predName, ArrayList<String[]> varsList, ArrayList<String> result){
		double confidence = 0;
		for(String[] vars : varsList){
			StringBuilder sb = new StringBuilder();
			sb.append(predName);
			for(int i =0; i<vars.length-1 ;i++){
				sb.append("|").append(vars[i]);
			}
			confidence = Double.parseDouble(vars[vars.length-1]);
			result.add(sb.toString());
		}
		return confidence;
	}
}
