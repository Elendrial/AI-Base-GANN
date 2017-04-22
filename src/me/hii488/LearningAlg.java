package me.hii488;

abstract class LearningAlg {
	protected NeuralNetwork neuralNet; // Should be taken from the main AI class to create less confusion.
	protected Settings settings;
	
	public float[] lastOutputValue = {};
	public String[] lastOutput = {};
	
	abstract public void setup();
	
	abstract public void iterate(Object o);
	
	abstract public float[] getOutputs(float[] inputs, Object o);

	abstract public void printUpdate();

	abstract public String settingsToString();
	
}
