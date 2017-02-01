package me.hii488;

import java.util.Arrays;
import java.util.Random;

public class Settings {
	
	public static Random rand = new Random();
	
	public NeuralSettings neuralSettings = this.new NeuralSettings();
	public LoggingSettings loggingSettings = this.new LoggingSettings();
	
	public ArtificialIntelligence parent;
	
	public class NeuralSettings{
		public int[] nodesInHiddenLayers = {};		// The amount of nodes each hidden layer of the net will have, limits depend on capabilities of system
		public int inputs = 0;              		// The amount of inputs the neural net will be given
		public String[] outputs = {};       		// The outputs you wish the neural net to give out
		public float cutoffThreshhold = 0;  		// How high a value has to be to count as "pressed", recommended is > 0.5f, max is 1f
		public boolean outputsAsFloats = false;		// Makes the program ignore the 'output' mappings and cutoff and give the raw float outputs (outputs still needs to have the length of the # of outputs wanted)
	}
	
	public class LoggingSettings{
		public boolean printAnything = true;
		public boolean printAll = true;
		public boolean printAverage = true;
		public boolean printTop = true;
		public int topAmount = 10;
	}
	
	public void printSettings(boolean neural, boolean generation, boolean logging){
		System.out.println(settingsAsString(neural, generation, logging));
	}
	
	public String settingsAsString(boolean neural, boolean learningAlg, boolean logging){
		String s = "";
		if(neural){
			s += ("Nodes in hidden layers: " + Arrays.toString(neuralSettings.nodesInHiddenLayers) + "\n");
			if(!neuralSettings.outputsAsFloats) s +=  ("outputs: " + Arrays.toString(neuralSettings.outputs) + "\n");
			s += ("cutoffThreshhold: " + neuralSettings.cutoffThreshhold + "\n");
		}

		if(learningAlg){
			s+= parent.learningAlg.settingsToString();
		}

		if(logging){
			s += ("Print at all: " + loggingSettings.printAnything + "\n");
			if(loggingSettings.printAnything){
				s += ("Print average: " + loggingSettings.printAverage + "\n");
				s += ("Print all: " + loggingSettings.printAll + "\n");
				s += ("Print top: " + loggingSettings.printTop + "\n");
				if(loggingSettings.printTop) s += ("Top printed: " + loggingSettings.topAmount + "\n");
			}
		}
		
		return s;
	}
	
}
