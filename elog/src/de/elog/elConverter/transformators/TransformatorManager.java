package de.elog.elConverter.transformators;

import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import de.elog.elConverter.ELOntology;

/**
 * This class manages Transformators. You can register, delete and execute (with convert()) them. 
 * 
 * Use ELOntology class for downsizing and normalizing OWL Ontologies to EL++.
 * 
 * @author jan
 *
 */
public class TransformatorManager {
	private static int freeVariablesCounter = 0;
	
	private ArrayList<Transformator> converters = new ArrayList<Transformator>();
	
	public HashSet<OWLAxiom> convert(OWLAxiom axiom, OWLDataFactory factory, ELOntology ontology){
		HashSet<OWLAxiom> result = new HashSet<OWLAxiom>();
		result.add(axiom);
		for(Transformator c : converters){
			// every converter needs the input of the last one.
			HashSet<OWLAxiom> tempResult = new HashSet<OWLAxiom>();
			for(OWLAxiom a : result){
				tempResult.addAll(c.convert(a, factory, ontology));
			}
			result = tempResult;
		}
		return result;
	}
	
	public void addConverter(Transformator converter){
		this.converters.add(converter);
	}
	
	public void setConverters(ArrayList<Transformator> converters) {
		this.converters = converters;
	}

	public ArrayList<Transformator> getConverters() {
		return converters;
	}
	
	public static int getNextFreeVariableCounter(){
		return freeVariablesCounter++;
	}
	public static void resetFreeVariableCounter(){
		freeVariablesCounter=0;
	}
	
}
