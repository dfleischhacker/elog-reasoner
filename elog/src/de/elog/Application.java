package de.elog;

import de.elog.elGreedy.Greedy;
import de.elog.elReasoner.Reasoner;
import de.elog.elSampler.Sampler;

public class Application {
	public static void main(String[] args) throws Exception {
		if((args.length==0) || !(args[0].equals("-r") || !(args[0].equals("-r2")) || !args[0].equals("-s") || !args[0].equals("-g"))){
			System.out.println("The first parameter of the application must be:");
			System.out.println("-r for the el reasoner");
			System.out.println("-r2 for the pellet-based reasoner");
			System.out.println("-g for the greedy approach");
			System.out.println("-s for the sampling");
			System.out.println("You will get further help when you type the correct parameters.");
			System.out.println("Example:");
			System.out.println("elog -r");
			System.out.println("Actual input: ");
			System.out.print("elog ");
			for(String s : args){
				System.out.print(s + " ");
			}
			System.out.println();
		}else {
			
			String[] remainingInputs = new String[args.length-1];
			for(int i=0; i<remainingInputs.length;i++){
				remainingInputs[i] = args[i+1];
			}
			if(args[0].equals("-r")){
				de.elog.elReasoner.Reasoner.main(remainingInputs);
			}else if(args[0].equals("-r2")){
				de.elog.pelletReasoner.Reasoner.main(remainingInputs);
			}else if(args[0].equals("-s")){
				Sampler.main(remainingInputs);
			}else if(args[0].equals("-g")){
				Greedy.main(remainingInputs);
			}
		}
	}
}
