package de.elog.evaluator;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class DisjointClassAxiomsToCheck implements AxiomsToCheck {
	public ArrayList<OWLAxiom> getAxiomsToCheck(Set<OWLClass> classes, Set<OWLObjectProperty> properties){
		OWLDataFactory factory = OWLDataFactoryImpl.getInstance();
		ArrayList<OWLAxiom> results = new ArrayList<OWLAxiom>();
		for(OWLClass c1 : classes){
			for(OWLClass c2 : classes){
				results.add(factory.getOWLDisjointClassesAxiom(c1, c2));
			}
		}
		return results;
	}

	@Override
	public String getName() {
		return "DisjointClassAxioms";
	}
}
