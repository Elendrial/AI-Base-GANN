package me.hii488;

abstract class LearningAlg {
	public NeuralNetwork neuralNet;
	public Settings settings;
	
	public float[] lastOutputValue = {};
	public String[] lastOutput = {};
	
	abstract public void setup();
	
	abstract public void iterate(Object o);
	
	abstract public float[] getOutputs(float[] inputs, Object o);

	abstract public void printUpdate();
}
