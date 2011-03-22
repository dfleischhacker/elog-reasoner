package de.elog.elSampler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.elog.elReasoner.OWLReader;
import de.elog.elReasoner.Reasoner;
import de.unima.app.grounder.StandardGrounder;
import de.unima.app.sampler.StandardSampler;
import de.unima.exception.ParseException;
import de.unima.exception.ReadOrWriteToFileException;
import de.unima.exception.SolveException;
import de.unima.helper.LogFileWriter;
import de.unima.javaAPI.Model;

public class Sampler {
	
	public double sample(Model model, ArrayList<String> events1, ArrayList<String> events0, int numberOfLargeSamplings) throws ReadOrWriteToFileException, SolveException, SQLException, ParseException{
		StandardGrounder grounder = new StandardGrounder();
		grounder.ground(model);
		System.out.println(new Date());
		
		StandardSampler sampler = new StandardSampler(model);
		
		sampler.setConditions0(new ArrayList<String>());
		sampler.setConditions1(new ArrayList<String>());
		sampler.setEvents0(events0);
		sampler.setEvents1(events1);
		
		sampler.setNumberOfRounds(numberOfLargeSamplings);
		sampler.setNumberOfSmallGIPSSamplingRounds(0);
		
		LogFileWriter logfilewriter = LogFileWriter.openNewLogFile("sampling.log");
		sampler.setLogFileWriter(logfilewriter);
		sampler.setResetILPForEachLargeRun(false);
		
		return sampler.sample();		
	}
	
	/**
	 * 
	 * @param args sourceOntologyPath, targetOntologyPath, targetOntologyNameSpace
	 * @throws ParseException
	 * @throws SQLException
	 * @throws SolveException
	 * @throws ReadOrWriteToFileException
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void main(String[] args) throws ParseException, SQLException, SolveException, ReadOrWriteToFileException, OWLOntologyCreationException, OWLOntologyStorageException {
		if(args.length!=3){
			System.out.println("Start the sampler with 3 arguments:");
			System.out.println("- existing filename of input ontology");
			System.out.println("- existing filename of ontology with positive and negative events");
			System.out.println("- number of samples l");
			System.out.println();
			System.out.println("Example: elog -s \"data/input/ontology1.owl\" \"data/input/events.owl\" 200");
		}else{
			long startTime = System.currentTimeMillis();
			int numberOfSamplingRounds = Integer.parseInt(args[2]);
			System.out.println("====================================================");
			System.out.println("Read ontology from file: " + args[0]);
			System.out.println("====================================================");
			//Read ontology axioms
			OWLReader reader = new OWLReader();
			//IRI ontologyIRI = 
			reader.read(args[0]);
			//Read event axioms
			OWLSamplingEventReader eventReader = new OWLSamplingEventReader();
			eventReader.read(args[1]);
			System.out.println("Successfully read in " + (System.currentTimeMillis()-startTime) + " milliseconds.");
			System.out.println("====================================================");
			System.out.println("Reason ontology: Get the most probable consistent ontology");
			System.out.println("====================================================");
			// get the model.
			Reasoner reasoner = new Reasoner();
			Model model = reasoner.generateModel(
					reader.getObjectProperty(), 
					reader.getConcept(), 
					reader.getSubsumesEvidence(), 
					reader.getIntersectionEvidence(), 
					reader.getOpsubEvidence(), 
					reader.getOpsupEvidence(), 
					reader.getPcomEvidence(),
					reader.getPsubsumesEvidence(), 
					reader.getSubsumesHard(), 
					reader.getIntersectionHard(), 
					reader.getOpsubHard(), 
					reader.getOpsupHard(), 
					reader.getPsubsumesHard(),
					reader.getPcomHard());
			Sampler sampler = new Sampler();
			
			double result = sampler.sample(model, eventReader.getEvent1(), eventReader.getEvent0(), numberOfSamplingRounds);
			
			System.out.println("=================================================");
			System.out.println("=== SAMPLED =====================================");
			System.out.println("Event1 (true): " + eventReader.getEvent1());
			System.out.println("Event0 (false): " + eventReader.getEvent0());
			System.out.println("=== WITH PROBABILITY = " + result);
			
		}
	}
}
