package me.hii488;

public class ArtificialIntelligence {
	public Settings settings = new Settings();
	public NeuralNetwork neuralNet = new NeuralNetwork();
	public LearningAlg learningAlg;
	
	public ArtificialIntelligence(){
		settings.parent = this;
	}
	
	// Must run *AFTER* settings have been set.
	public void initialSetup(){
		neuralNet.settings = this.settings.neuralSettings;
		learningAlg.settings = this.settings;
		
		learningAlg.neuralNet = this.neuralNet;
		
		learningAlg.setup();
	}
	
	public void iterate(Object ...o){
		learningAlg.iterate(o);
		if(settings.loggingSettings.printAnything) learningAlg.printUpdate();
	}
	
	public String[] getOutputs(float[] inputs, Object ...o){
		float[] f = learningAlg.getOutputs(inputs, o);
		learningAlg.lastOutputValue = f;
		
		String[] s = outputValuesToString(f);
		learningAlg.lastOutput = s;
		
		return s;
	}
	
 	public String[] outputValuesToString(float[] outputValues){
		String[] s = new String[neuralNet.settings.outputs.length];
		
		if(!settings.neuralSettings.outputsAsFloats)
			for(int i = 0; i < neuralNet.settings.outputs.length; i++)
				if(outputValues[i] >= neuralNet.settings.cutoffThreshhold) s[i] = (neuralNet.settings.outputs[i]);
				else s[i] = "";
		else for(int i = 0; i < neuralNet.settings.outputs.length; i++) s[i] = "" + outputValues[i];
		
		return s;
	}
}
