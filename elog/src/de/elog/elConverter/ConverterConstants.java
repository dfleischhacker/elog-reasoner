package de.elog.elConverter;

/**
 * Some constants for the converter.
 * 
 * @author jan
 *
 */
public class ConverterConstants {
	public static final String TOP_ELEMENT = "owl:Thing";
	public static final String BOTTOM_ELEMENT = "owl:Nothing";
	
	public static final String NEW_NAMESPACE = "elog_axiom_";
	public static final String NEW_CLASS_IRI = "http://" + NEW_NAMESPACE+ "class";
	public static final String NEW_PROP_IRI = "http://" + NEW_NAMESPACE+ "prop";
	public static final String NEW_REFLEXIVE_ROLE_SUB_IRI = "http://" + NEW_NAMESPACE+ "reflex";
	public static final String NEW_OPSUP_CONCEPT_IRI = "http://" + NEW_NAMESPACE+ "opsup";
	
}
