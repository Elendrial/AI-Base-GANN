package me.hii488;

import java.io.Serializable;

import me.hii488.Settings.NeuralSettings;

//Hierarchy : child -> layers -> nodes -> node-weights
public class NeuralNetwork implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1508254350729328366L;
	public NeuralSettings settings;
	
	public static boolean areSimilar(Child a, Child b){
		if(a.layers.length != b.layers.length) return false;
		
		for(int layer = 0; layer < a.layers.length; layer++){
			if(a.layers[layer].nodes.length != b.layers[layer].nodes.length) return false;
			for(int node = 0; node < a.layers[layer].nodes.length; node++){
				if(a.layers[layer].nodes[node].weights.length != b.layers[layer].nodes[node].weights.length) return false;
				for(int weight = 0; weight < a.layers[layer].nodes[node].weights.length; weight++){
					if(a.layers[layer].nodes[node].weights[weight] != b.layers[layer].nodes[node].weights[weight]) return false;
				}
			}
		}
		
		return true;
	}
	
	public class Child implements Serializable{
		private static final long serialVersionUID = -5046688404207330509L;
		public Layer[] layers;
		public float fitness = 0;
		
		public Child(){
			layers = new Layer[settings.nodesInHiddenLayers.length+1];
			for(int i = 0; i < layers.length; i++){
				layers[i] = new Layer(i);
			}
		}
		
		private Child(Child c){
			layers = new Layer[c.layers.length];
			for(int i = 0; i < layers.length; i++){
				layers[i] = c.layers[i].clone();
			}
			fitness = c.fitness;
		}
		
		@Override
		public Child clone(){
			return new Child(this);
		}
		
		public boolean equals(Object o){
			if(!(o instanceof Child)) return false;
			if(((Child) o).layers.length != this.layers.length) return false;
			
			for(int layer = 0; layer < ((Child) o).layers.length; layer++){
				if(((Child) o).layers[layer].nodes.length != this.layers[layer].nodes.length) return false;
				for(int node = 0; node < ((Child) o).layers[layer].nodes.length; node++){
					if(((Child) o).layers[layer].nodes[node].weights.length != this.layers[layer].nodes[node].weights.length) return false;
					for(int weight = 0; weight < ((Child) o).layers[layer].nodes[node].weights.length; weight++){
						if(((Child) o).layers[layer].nodes[node].weights[weight] != this.layers[layer].nodes[node].weights[weight]) return false;
					}
				}
			}
			
			return true;
		}
		
	}
	
	public class Layer implements Serializable{
		private static final long serialVersionUID = 1309078945334240516L;
		public Node[] nodes;
		public final int layerLevel;
		
		public Layer(int level){
			layerLevel = level;
			
			if(layerLevel != settings.nodesInHiddenLayers.length){
				nodes = new Node[settings.nodesInHiddenLayers[level]];
			}
			else{
				nodes = new Node[settings.outputs.length];
			}
			
			for(int i = 0; i < nodes.length; i++){
				nodes[i] = new Node(layerLevel);
			}
		}
		
		private Layer(Layer l){
			layerLevel = l.layerLevel;
			
			nodes = new Node[l.nodes.length];
			for(int i = 0; i < nodes.length; i++){
				nodes[i] = l.nodes[i].clone();
			}
		}
		
		@Override
		public Layer clone(){
			return new Layer(this);
		}
	}
	
	public class Node implements Serializable{
		private static final long serialVersionUID = -4074893277872952213L;
		public float[] weights;
		public float[] weightDiffs; // for backprop
		public float lastOutput = 0f;
		
		public Node(int level){
			weights = new float[(level != 0 ? settings.nodesInHiddenLayers[level-1] : settings.inputs) + 1]; // The +1 is for the threshold value.
			weightDiffs = new float[weights.length];
		}
		
		private Node(Node n){
			weights = n.weights.clone();
		}
		
		@Override
		public Node clone(){
			return new Node(this);
		}
		
		
		// Checks if the node will output a 0 or 1
		public float activated(float[] inputs){
			float temp = weights[weights.length-1]; // Set temp to the original threshold.
			
			for(int i = 0; i < inputs.length; i++){
				temp += inputs[i] * weights[i];
			}
			
			// Sigmoid function
			lastOutput = (float) (1/(1 + Math.pow(Math.E, -temp)));
			return lastOutput;
		}
	}
	
	public void saveToFile(String path){
		FileIO.openSerialize(path);
		FileIO.serialize(this);
		FileIO.endSerialize();
	}
	
	public static NeuralNetwork readFromFile(String filename){
		try {
			return (NeuralNetwork) FileIO.deserialize(filename);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
