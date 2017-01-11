package me.hii488;

import java.util.Arrays;
import java.util.Random;

public class AISettings {
	
	public static Random rand = new Random();
	
	public NeuralSettings neuralSettings = this.new NeuralSettings();
	public GenerationSettings generationSettings = this.new GenerationSettings();
	public LoggingSettings loggingSettings = this.new LoggingSettings();
	
	public class NeuralSettings{
		public int[] nodesPerLayer = {};			// The amount of nodes each layer of the net will have, limits depend on capabilities of system. Last layer should be same length as # of outputs
		public int inputs = 0;              		// The amount of inputs the neural net will be given
		public String[] outputs = {};       		// The outputs you wish the neural net to give out
		public float cutoffThreshhold = 0;  		// How high a value has to be to count as "pressed", recommended is > 0.5f, max is 1f
		public boolean outputsAsFloats = false;		// Makes the program ignore the 'output' mappings and cutoff and give the raw float outputs (outputs still needs to have the length of the # of outputs wanted)
	}
	
	public class GenerationSettings{
		public int childrenPerGeneration = 0;       // The amount of newly generated children, the greater the value, the faster the learning between generations, but longer time taken per gen.
		public int additionalTopChildrenKept = 0;   // The amount of children with highscores carried on between generations, to prevent possible accidental regression
		public float mutationChance = 0;            // The chance of each one of a new child's weights randomly changing, recommended is very small, max is 1f 
		public int mixTop = -1;						// The amount of children that can be mixed to make the next generation, -1 means all children can be
	}
	
	public class LoggingSettings{
		public boolean printAnything = true;
		public boolean printAll = true;
		public boolean printAverage = true;
		public boolean printTop = true;
		public int topAmount = 10;
	}
	
	
	public void printSettings(boolean neural, boolean generation, boolean logging){
		if(neural){
			System.out.println("Nodes per Layer: " + Arrays.toString(neuralSettings.nodesPerLayer));
			if(!neuralSettings.outputsAsFloats) System.out.println("outputs: " + Arrays.toString(neuralSettings.outputs));
			System.out.println("cutoffThreshhold: " + neuralSettings.cutoffThreshhold);
		}
		
		if(generation){
			 System.out.println("Children per Gen: " + generationSettings.childrenPerGeneration);
			 System.out.println("Children kept:" + generationSettings.additionalTopChildrenKept);
			 System.out.println("Mutation: " + generationSettings.mutationChance);
			 System.out.println("Top Mixed: " + generationSettings.mixTop);
		}
		
		if(logging){
			System.out.println("Print at all: " + loggingSettings.printAnything);
			if(loggingSettings.printAnything){
				System.out.println("Print average: " + loggingSettings.printAverage);
				System.out.println("Print all: " + loggingSettings.printAll);
				System.out.println("Print top: " + loggingSettings.printTop);
				if(loggingSettings.printTop) System.out.println("Top printed: " + loggingSettings.topAmount);
			}
		}
	}
	
}
