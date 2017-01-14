package me.hii488;

import java.util.ArrayList;

import me.hii488.AISettings.GenerationSettings;
import me.hii488.NeuralNetwork.Child;

public class GeneticAlgorithm {
	public NeuralNetwork neuralNet;
	
	public GenerationSettings settings;
	
	public ArrayList<Child> children = new ArrayList<Child>();
	ArrayList<Child> sortedChildren = new ArrayList<Child>();
	
	// TODO: Switch all the for loops to foreach loops
	public void makeRandomGeneration(){
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		for(int i = 0; i < settings.childrenPerGeneration; i++){
			Child child = neuralNet.new Child();
			
			for(int layer = 0; layer < child.layers.length; layer++){
				for(int node = 0; node < child.layers[layer].nodes.length; node++){
					for(int weight = 0; weight < child.layers[layer].nodes[node].weights.length; weight++){
						child.layers[layer].nodes[node].weights[weight] = AISettings.rand.nextFloat() - AISettings.rand.nextFloat();
					}
				}
			}
			
			childPool.add(child);
		}
		children = childPool;
	}
	
	public void nextGeneration(){
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		Child parentA;
		Child parentB;
		while(childPool.size() < settings.childrenPerGeneration){
			parentA = rouletteChoice(children);
			do{parentB = rouletteChoice(children);} while(NeuralNetwork.areSimilar(parentA, parentB));
			
			if(this.settings.debug)	System.out.println("Parent A: " + parentA.fitness + "\nParent B: " + parentB.fitness);
			
			Child child = spliceChildren(parentA, parentB);
			child = mutateChild(child);
			
			boolean different = true;
			if(settings.insureDifferent)  // Just to insure you're not doing the same thing twice
				for(int j = 0; j < childPool.size() && different; j++)
					if(NeuralNetwork.areSimilar(child, childPool.get(j))) different = false;
			
			if(different)
				for(int j = 0; j < settings.additionalTopChildrenKept && different; j++)
					if(NeuralNetwork.areSimilar(child, sortedChildren.get(sortedChildren.size() - 1 - j))) different = false;
			
			if(different) childPool.add(child);
		}
		
		for(int i = settings.additionalTopChildrenKept -1; i >= 0; i--)
			childPool.add(sortedChildren.get(sortedChildren.size() - 1 - i));
		
		children = childPool;
	}
	
	
	public Child rouletteChoice(ArrayList<Child> incChildren){
		ArrayList<Child> sChildren;
		if(settings.mixTop != -1)
			sChildren = new ArrayList<Child>(sortedChildren.subList(incChildren.size()-1-settings.mixTop, incChildren.size()));
		else 
			sChildren = incChildren;
		
		float totalFitness = 0;
		for(int i = 0; i < sChildren.size(); i++){
			totalFitness += sChildren.get(i).fitness;
		}
			
		double value = AISettings.rand.nextDouble() * totalFitness;
			
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
				final int crosspoint = AISettings.rand.nextInt(i == 0 ? neuralNet.settings.inputs : child.layers[i].nodes[j].weights.length-2)+1;
				if(this.settings.debug) System.out.println("Crosspoint: " + crosspoint);
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
					if(AISettings.rand.nextFloat() <= settings.mutationChance){
						child.layers[i].nodes[j].weights[k] += (AISettings.rand.nextBoolean() == true) ? (AISettings.rand.nextFloat()/5) * -1 : AISettings.rand.nextFloat()/5;
					}
				}
			}
		}
		return child;
	}
	
	// Not strictly necessary
	public float[] getOutputs(float[] inputs, int child){
		return recurrentOutput(inputs, child, 0);
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
	
}
