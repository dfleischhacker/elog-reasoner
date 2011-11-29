
package de.elog.elGreedy;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


public class Ontology {
	
	private IRI m_logicalIRI;
	
	private IRI m_physicalIRI;
	
	OWLOntologyManager m_manager;
	
	OWLOntology m_ontology;
	
	OWLDataFactory m_factory;
	
	PelletReasoner m_reasoner;
	
	
    public Ontology() throws Exception {
		m_manager = OWLManager.createOWLOntologyManager();
		m_factory = m_manager.getOWLDataFactory();
	}
	
	public void create( File file, IRI iri ) throws Exception {
		m_logicalIRI = iri;
		m_physicalIRI = IRI.create( file.toURI() );
		//OWLOntologyID mapper = new OWLOntologyID( m_logicalIRI, m_physicalIRI );
		m_ontology = m_manager.createOntology( m_physicalIRI );
		m_reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner( m_ontology );
		// m_manager.addOntologyChangeListener( m_reasoner );
	}
	
	public void save() throws Exception {
		m_manager.saveOntology( m_ontology );
	}
	
	public void load( File file ) throws Exception {
		m_ontology = m_manager.loadOntologyFromOntologyDocument( IRI.create( file.toString() ) );
		m_reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner( m_ontology );
		// m_manager.addOntologyChangeListener( m_reasoner );
	}
	
	public boolean isCoherent() {
		Set<OWLClass> classes = m_ontology.getClassesInSignature();
		for( OWLClass c: classes )
		{
    		if( !isSatisfiable(c) )
			{
    			//System.out.println( "unsatisfiable: " + c.toString()  );
				// printExplanation(c);
    			return false;
    		}
    	}
    	return true;
	}
	
	public boolean isSatisfiable( OWLClass c ){
		// OWLAxiom axiom = m_factory.getOWLSubClassOfAxiom( c, m_factory.getOWLNothing() );
		// return m_reasoner.isEntailed( axiom );
		return m_reasoner.isSatisfiable(c);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for( OWLAxiom axiom: m_ontology.getAxioms() ){
			sb.append( axiom.toString() +"\n" );
		}
		return sb.toString();
	}
	
	public boolean entails( OWLAxiom axiom ) throws Exception {
		return m_reasoner.isEntailed( axiom );
	}
	
	public OWLAxiom createDomainAxiom( String sProp, String sClass ) throws Exception {
		OWLObjectProperty p = m_factory.getOWLObjectProperty( IRI.create( sProp ) );
		OWLClass c = m_factory.getOWLClass( IRI.create( sClass ) );
		return m_factory.getOWLObjectPropertyDomainAxiom( p, c );
	}
	
	public OWLAxiom createRangeAxiom( String sProp, String sClass ) throws Exception {
		OWLObjectProperty p = m_factory.getOWLObjectProperty( IRI.create( sProp ) );
		OWLClass c = m_factory.getOWLClass( IRI.create( sClass ) );
		return m_factory.getOWLObjectPropertyRangeAxiom( p, c );
	}
	
	public String getDomain( String sPropURI ) throws Exception {
		OWLObjectProperty prop = m_factory.getOWLObjectProperty( IRI.create( sPropURI ) );
		Set<OWLObjectPropertyDomainAxiom> axioms = m_ontology.getObjectPropertyDomainAxioms( prop );
		StringBuffer sb = new StringBuffer();
		Iterator<?> iter = axioms.iterator();
		while( iter.hasNext() )
		{
			OWLObjectPropertyDomainAxiom axiom = (OWLObjectPropertyDomainAxiom) iter.next();
			OWLClassExpression domain = axiom.getDomain();
			sb.append( domain.asOWLClass().toStringID() );
			if( iter.hasNext() ){
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
	
	public String getRange( String sPropURI ) throws Exception {
		OWLObjectProperty prop = m_factory.getOWLObjectProperty( IRI.create( sPropURI ) );
		Set<OWLObjectPropertyRangeAxiom> axioms = m_ontology.getObjectPropertyRangeAxioms( prop );
		StringBuffer sb = new StringBuffer();
		Iterator<?> iter = axioms.iterator();
		while( iter.hasNext() )
		{
			OWLObjectPropertyRangeAxiom axiom = (OWLObjectPropertyRangeAxiom) iter.next();
			OWLClassExpression range = axiom.getRange();
			sb.append( range.asOWLClass().toStringID() );
			if( iter.hasNext() ){
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
	
	public boolean subsumedBy( String sURI1, String sURI2 ) throws Exception {
		OWLClass c1 = m_factory.getOWLClass( IRI.create( sURI1 ) );
		OWLClass c2 = m_factory.getOWLClass( IRI.create( sURI2 ) );
		OWLAxiom axiom = m_factory.getOWLSubClassOfAxiom( c1, c2 );
		return m_reasoner.isEntailed( axiom );
	}
	
	
	public void addAxiom( OWLAxiom axiom ) throws Exception {
		AddAxiom addAxiom = new AddAxiom( m_ontology, axiom );
		m_manager.applyChange( addAxiom );
    }
	
	public void removeAnnotations(OWLAxiom axiom) throws Exception {
		//TODO		
	}
	
	public void removeAxiom( OWLAxiom axiom ) throws Exception {
		RemoveAxiom removeAxiom = new RemoveAxiom( m_ontology, axiom );
		m_manager.applyChange( removeAxiom );
	}
	
	public OWLAnnotation getAnnotation( String sAnnotation, double dValue ){
		OWLAnnotationProperty prop = m_factory.getOWLAnnotationProperty( IRI.create( m_logicalIRI +"#"+ sAnnotation ) );
		OWLAnnotation annotation = m_factory.getOWLAnnotation( prop, m_factory.getOWLLiteral( dValue ) );
		return annotation;
	} 
	
	public OWLAxiom get_c_sub_c_Axiom( String subURI, String superURI ){
		OWLClass c1 = m_factory.getOWLClass( IRI.create( subURI ) );
		OWLClass c2 = m_factory.getOWLClass( IRI.create( superURI ) );
		return m_factory.getOWLSubClassOfAxiom( c1, c2 );
	}
	
	public OWLAxiom get_c_sub_c_Axiom( String subURI, String superURI, double dConf ){
		OWLClass c1 = m_factory.getOWLClass( IRI.create( subURI ) );
		OWLClass c2 = m_factory.getOWLClass( IRI.create( superURI ) );
		OWLAnnotation annotation = getAnnotation( "confidence", dConf );
		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		anns.add( annotation );
		return m_factory.getOWLSubClassOfAxiom( c1, c2, anns );
	}

	public OWLAxiom get_c_dis_c_Axiom( String subURI1, String subURI2 ){
		OWLClass c1 = m_factory.getOWLClass( IRI.create( subURI1 ) );
		OWLClass c2 = m_factory.getOWLClass( IRI.create( subURI2 ) );
		return m_factory.getOWLDisjointClassesAxiom(c1, c2);
	}
	
	public OWLAxiom get_c_dis_c_Axiom( String subURI1, String subURI2, double dConf ){
		OWLClass c1 = m_factory.getOWLClass( IRI.create( subURI1 ) );
		OWLClass c2 = m_factory.getOWLClass( IRI.create( subURI2 ) );
		OWLAnnotation annotation = getAnnotation( "confidence", dConf );
		Set<OWLAnnotation> anns = new HashSet<OWLAnnotation>();
		anns.add( annotation );
		Set<OWLClass> classes = new HashSet<OWLClass>();
		classes.add( c1 );
		classes.add( c2 );
		return m_factory.getOWLDisjointClassesAxiom( classes, anns );
	}
}
