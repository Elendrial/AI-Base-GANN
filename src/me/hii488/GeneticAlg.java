package me.hii488;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import me.hii488.NeuralNetwork.Child;

public class GeneticAlg extends LearningAlg{
	
	public ArrayList<Child> children = new ArrayList<Child>();
	ArrayList<Child> sortedChildren = new ArrayList<Child>();
	
	public int generation = 0;
	
	public void setup(){
		makeRandomGeneration();
	}
	
	// TODO: Switch all the for loops to foreach loops
	public void makeRandomGeneration(){
		generation = 0;
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		for(int i = 0; i < genSettings.childrenPerGeneration; i++){
			Child child = neuralNet.new Child();
			
			for(int layer = 0; layer < child.layers.length; layer++){
				for(int node = 0; node < child.layers[layer].nodes.length; node++){
					for(int weight = 0; weight < child.layers[layer].nodes[node].weights.length; weight++){
						child.layers[layer].nodes[node].weights[weight] = Settings.rand.nextFloat() - Settings.rand.nextFloat();
					}
				}
			}
			
			childPool.add(child);
		}
		children = childPool;
	}
	
	@Override
	public void iterate(Object o){
		nextGeneration();
	}
	
	public void nextGeneration(){
		generation++;
		sortedChildren = fitnessSortedChildren(children);
		
		
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		Child parentA;
		Child parentB;
		while(childPool.size() < genSettings.childrenPerGeneration){
			parentA = rouletteChoice(children);
			do{parentB = rouletteChoice(children);} while(NeuralNetwork.areSimilar(parentA, parentB));
			
			if(genSettings.debug)	System.out.println("Parent A: " + parentA.fitness + "\nParent B: " + parentB.fitness);
			
			Child child = spliceChildren(parentA, parentB);
			child = mutateChild(child);
			
			boolean different = true;
			if(genSettings.insureDifferent)  // Just to insure you're not doing the same thing twice
				for(int j = 0; j < childPool.size() && different; j++)
					if(NeuralNetwork.areSimilar(child, childPool.get(j))) different = false;
			
			if(different)
				for(int j = 0; j < genSettings.additionalTopChildrenKept && different; j++)
					if(NeuralNetwork.areSimilar(child, sortedChildren.get(sortedChildren.size() - 1 - j))) different = false;
			
			if(different) childPool.add(child);
		}
		
		for(int i = genSettings.additionalTopChildrenKept -1; i >= 0; i--)
			childPool.add(sortedChildren.get(sortedChildren.size() - 1 - i));
		
		children = childPool;
	}
	
	
	public Child rouletteChoice(ArrayList<Child> incChildren){
		ArrayList<Child> sChildren;
		if(genSettings.mixTop != -1)
			sChildren = new ArrayList<Child>(sortedChildren.subList(incChildren.size()-1-genSettings.mixTop, incChildren.size()));
		else 
			sChildren = incChildren;
		
		float totalFitness = 0;
		for(int i = 0; i < sChildren.size(); i++){
			totalFitness += sChildren.get(i).fitness;
		}
			
		double value = Settings.rand.nextDouble() * totalFitness;
			
		for(int i = 0; i < sChildren.size(); i++){
			value -= sChildren.get(i).fitness;
			if(value <= 0) return sChildren.get(i);
		}
			
		return sChildren.get(sChildren.size()-1);
	}
	
	public Child spliceChildren(Child a, Child b){
		Child child = a.clone();
		
		for(int i = 0; i < child.layers.length; i++){
			for(int j = 0; j < child.layers[i].nodes.length; j++){
				final int crosspoint = Settings.rand.nextInt(i == 0 ? neuralNet.settings.inputs : child.layers[i].nodes[j].weights.length-2)+1;
				if(genSettings.debug) System.out.println("Crosspoint: " + crosspoint);
				for(int k = 0; k < child.layers[i].nodes[j].weights.length; k++){
					if(k >= crosspoint){
						child.layers[i].nodes[j].weights[k] = b.layers[i].nodes[j].weights[k];
					}
				}
			}
		}
		
		return child;
	}
	
	public Child mutateChild(Child child){
		for(int i = 0; i < child.layers.length; i++){
			for(int j = 0; j < child.layers[i].nodes.length; j++){
				for(int k = 0; k < child.layers[i].nodes[j].weights.length; k++){
					if(Settings.rand.nextFloat() <= genSettings.mutationChance){
						child.layers[i].nodes[j].weights[k] += (Settings.rand.nextBoolean() == true) ? (Settings.rand.nextFloat()/5) * -1 : Settings.rand.nextFloat()/5;
					}
				}
			}
		}
		return child;
	}
	
	@Override
	public float[] getOutputs(float[] inputs, Object o){
		return recurrentOutput(inputs, (int)(((Object[]) o)[0]), 0);
	}
	
	public float[] recurrentOutput(float[] inputs, int child, int layer){
		float[] output;
		
		if(layer < neuralNet.settings.nodesInHiddenLayers.length)
			output = new float[neuralNet.settings.nodesInHiddenLayers[layer]];
		
		else
			output = new float[neuralNet.settings.outputs.length];
				
		for(int i = 0; i < output.length; i++){
			output[i] = children.get(child).layers[layer].nodes[i].activated(inputs);
		}
		
		return output;
	}
	
	
	public ArrayList<Child> fitnessSortedChildren(ArrayList<Child> children2) {
		ArrayList<Child> listToUse = new ArrayList<Child>();
		
		for(int i = 0; i < children2.size(); i++){
			listToUse.add(children2.get(i).clone());
		}
		
		int i,j;

		for (i = 1; i < listToUse.size(); i++) {
			Child c = listToUse.get(i);
			j = i;
			while((j > 0) && (listToUse.get(j - 1).fitness > c.fitness)) {
				listToUse.set(j,listToUse.get(j - 1));
				j--;
			}
			listToUse.set(j,c);
		}
		    
		return listToUse;
	}
	
	
	// MIGRATED FROM THE MAIN CLASS
	public float highestFitness = 0;
	
	public void setFitness(int child, float fitness){
		this.children.get(child).fitness = fitness;
		if(fitness > highestFitness) highestFitness = fitness;
	}
	
	@Override
	public void printUpdate(){
		System.out.println(getGenerationInfoAsString());
	}
	
	public String printFitnesses(){
		String s = ("Generation " + generation + " fitnesses:\n");
		float totalFitness = 0;
		for(int i = 0; i < this.children.size(); i++){
			s +=("Child " + i + ": " + this.children.get(i).fitness + "\n");
			totalFitness += this.children.get(i).fitness;
		}
		if(settings.loggingSettings.printAverage) s+=("Average Fitness: " + totalFitness / (this.children.size()));
		s += ("\n");
		return s;
	}
	
	public String printTop(int amount){
		String s = ("Generation " + generation + " top fitnesses:\n");
		ArrayList<Child> c = this.sortedChildren;
		for(int i = 0; i < amount; i++)	s += (i + " : " + c.get(c.size()-i-1).fitness);
		
		if(settings.loggingSettings.printAverage){
			float totalFitness =0;
			for(int i = 0; i < this.children.size(); i++){
				totalFitness += this.children.get(i).fitness;
			}
			s += ("Average Fitness: " + totalFitness / (this.children.size()+1)+"\n");
		}
		
		s +=("\n");
		return s;
	}
	
	public String printAverage() {
		float totalFitness =0;
		for(int i = 0; i < this.children.size(); i++){
			totalFitness += this.children.get(i).fitness;
		}
		return ("Generation #" + generation + " Average Fitness: " + totalFitness / (this.children.size()+1));
	}
	
	public String getGenerationInfoAsString(){
		if(settings.loggingSettings.printAll) return printFitnesses();
		else if(settings.loggingSettings.printTop) return printTop(settings.loggingSettings.topAmount);
		else if(settings.loggingSettings.printAverage) return printAverage();
		else return "";
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
	
	public String settingsToString(){
		String s = "";
		s += ("Children per Gen: " + genSettings.childrenPerGeneration + "\n");
		s += ("Children kept:" + genSettings.additionalTopChildrenKept + "\n");
		s += ("Mutation: " + genSettings.mutationChance + "\n");
		s += ("Top Mixed: " + genSettings.mixTop + "\n");
		s += ("Insure Different: " + genSettings.insureDifferent + "\n");
		return s;
	}
	
	public GenerationSettings genSettings = new GenerationSettings();
	public class GenerationSettings{
		public int childrenPerGeneration = 0;       // The amount of newly generated children, the greater the value, the faster the learning between generations, but longer time taken per gen.
		public int additionalTopChildrenKept = 0;   // The amount of children with highscores carried on between generations, to prevent possible accidental regression
		public float mutationChance = 0;            // The chance of each one of a new child's weights randomly changing, recommended is very small, max is 1f 
		public int mixTop = -1;						// The amount of children that can be mixed to make the next generation, -1 means all children can be
		public boolean insureDifferent = false;     // Insures that all children per generation are unique - not necessary, and possibly expensive to check
		
		public boolean debug = false;				// prints a load of stuff to console, would not recommend having on.
	}
	
}
