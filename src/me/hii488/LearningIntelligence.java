package me.hii488;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import me.hii488.NeuralNetwork.Child;

public class LearningIntelligence {
	public AISettings settings = new AISettings();
	public NeuralNetwork neuralNet = new NeuralNetwork();
	public GeneticAlgorithm geneticAlg = new GeneticAlgorithm();
	
	public float highestFitness = 0;
	public int generation = 0;
	public int speciesNumber = 0; // Only used if going through each child separately.
	
	public float[] lastOutputValue = {};
	public String[] lastOutput = {};
	
	// Must run *AFTER* settings have been set.
	public void initialSetup(){
		neuralNet.settings = this.settings.neuralSettings;
		geneticAlg.settings = this.settings.generationSettings;
		
		geneticAlg.neuralNet = this.neuralNet;
		
		geneticAlg.makeRandomGeneration();
	}
	
	// Only use if going through each child separately.
	public void nextSpecies(){
		if(speciesNumber + 1 < geneticAlg.children.size()){
			speciesNumber++;
		}
		else{
			if(this.settings.loggingSettings.printTop) printTop(this.settings.loggingSettings.topAmount);
			if(this.settings.loggingSettings.printAll) printFitnesses();
			geneticAlg.nextGeneration();
			speciesNumber = 0;
			generation++;
		}
	}
	
	// -Only use if NOT going through each child separately.
	public void nextGeneration(){
		if(this.settings.loggingSettings.printAll && settings.loggingSettings.printAnything) printFitnesses();
		else if(this.settings.loggingSettings.printTop && settings.loggingSettings.printAnything) printTop(this.settings.loggingSettings.topAmount);
		else if(this.settings.loggingSettings.printAverage && settings.loggingSettings.printAnything) printAverage();
			
		geneticAlg.nextGeneration();
		generation++;
	}

	// Only use if going through each child separately.
	public String[] getOutputs(float[] inputs){
		return getOutputs(speciesNumber, inputs);
	}
	
	// -Only use if NOT going through each child separately.
	public String[] getOutputs(int child, float[] inputs){
		float[] f = geneticAlg.getOutputs(inputs, child);
		this.lastOutputValue = f;
		
		String[] s = outputValuesToString(f);
		this.lastOutput = s;
		
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
	
	// Only use if going through each child separately.
	public void setFitness(float fitness){
		geneticAlg.children.get(speciesNumber).fitness = fitness;
		if(fitness > highestFitness) highestFitness = fitness;
	}
	
	// -Only use if NOT going through each child separately.
	public void setFitness(int child, float fitness){
		geneticAlg.children.get(child).fitness = fitness;
		if(fitness > highestFitness) highestFitness = fitness;
	}
	
	public void printFitnesses(){
		System.out.println("Generation " + generation + " fitnesses:");
		int totalFitness = 0;
		for(int i = 0; i < geneticAlg.children.size(); i++){
			System.out.println("Child " + i + ": " + geneticAlg.children.get(i).fitness);
			totalFitness += geneticAlg.children.get(i).fitness;
		}
		if(settings.loggingSettings.printAverage) System.out.println("Average Fitness: " + totalFitness / geneticAlg.children.size());
		System.out.println("");
	}
	
	public void printTop(int amount){
		System.out.println("Generation " + generation + " top fitnesses:");
		ArrayList<Child> c = geneticAlg.fitnessSortedChildren((ArrayList<Child>) geneticAlg.children);
		for(int i = 0; i < amount; i++)	System.out.println(i + " : " + c.get(c.size()-i-1).fitness);
		
		if(settings.loggingSettings.printAverage){
			int totalFitness =0;
			for(int i = 0; i < geneticAlg.children.size(); i++){
				totalFitness += geneticAlg.children.get(i).fitness;
			}
			System.out.println("Average Fitness: " + totalFitness / geneticAlg.children.size());
		}
		
		System.out.println("");
	}
	
	public void printAverage() {
		int totalFitness =0;
		for(int i = 0; i < geneticAlg.children.size(); i++){
			totalFitness += geneticAlg.children.get(i).fitness;
		}
		System.out.println("Generation #" + generation + " Average Fitness: " + totalFitness / geneticAlg.children.size());
	}
	
	// Only use if going through each child separately.
	public void displayOutputs(Graphics g, int x, int y, int distanceBetween, boolean interpolate, boolean showIfNotAboveThreshhold){
		Color c = g.getColor();
		float[] f = lastOutputValue;
		if(interpolate){
			for(int i = 0; i < lastOutputValue.length; i++){
				g.setColor(new Color((int)(Color.red.getRed() * f[i] + Color.green.getRed() * (1-f[i])),(int)(Color.red.getGreen() * f[i] + Color.green.getGreen() * (1-f[i])),(int)(Color.red.getBlue() * f[i] + Color.green.getBlue() * (1-f[i]))));
				if(!showIfNotAboveThreshhold)g.drawString(lastOutput[i], x + i * distanceBetween, y);
				else g.drawString(neuralNet.settings.outputs[i], x + i * distanceBetween, y);
			}
		}
		else{
			for(int i = 0; i < lastOutputValue.length; i++){
				if(f[i] > neuralNet.settings.cutoffThreshhold) g.setColor(Color.green);
				else g.setColor(Color.red);
				
				if(!showIfNotAboveThreshhold)g.drawString(lastOutput[i], x + i * distanceBetween, y);
				else g.drawString(neuralNet.settings.outputs[i], x + i * distanceBetween, y);
			}
		}
		g.setColor(c);
	}
}
