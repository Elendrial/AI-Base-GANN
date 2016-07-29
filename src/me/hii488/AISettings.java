package me.hii488;

import java.util.Random;

public class AISettings {
	
	public static Random rand = new Random();
	
	public NeuralSettings neuralSettings = this.new NeuralSettings();
	public GenerationSettings generationSettings = this.new GenerationSettings();
	
	public class NeuralSettings{
		public int[] nodesPerLayer = {};
		public int inputs = 0;
		public String[] outputs = {};
		public float cutoffThreshhold = 0;
	}
	
	public class GenerationSettings{
		public int childrenPerGeneration = 0;
		public int additionalTopChildrenKept = 0;
		public float mutationChance = 0;
	}
	
	public class LoggingSettings{
		public boolean printAnything = true;
		public boolean printAll = true;
		public boolean printTop = true;
		public int topAmount = 10;
	}
	
}
