package me.hii488;

import me.hii488.AISettings.NeuralSettings;

//Hierarchy : child -> layers -> nodes -> node-weights
public class NeuralNetwork {
	
	public NeuralSettings settings;
	
	public class Child{
		public Layer[] layers;
		public int fitness = 0;
		
		public Child(){
			layers = new Layer[settings.nodesPerLayer.length];
			for(int i = 0; i < layers.length; i++){
				layers[i] = new Layer(i);
			}
		}
		
		private Child(Child c){
			layers = new Layer[c.layers.length];
			for(int i = 0; i < layers.length; i++){
				layers[i] = c.layers[i].clone();
			}
		}
		
		public Child clone(){
			return new Child(this);
		}
		
	}
	
	public class Layer{
		public Node[] nodes;
		public final int layerLevel;
		
		public Layer(int level){
			layerLevel = level;
			
			nodes = new Node[settings.nodesPerLayer[level]];
			for(int i = 0; i < nodes.length; i++){
				nodes[i] = new Node(layerLevel, i);
			}
		}
		
		private Layer(Layer l){
			layerLevel = l.layerLevel;
			
			nodes = new Node[l.nodes.length];
			for(int i = 0; i < nodes.length; i++){
				nodes[i] = l.nodes[i].clone();
			}
		}
		
		public Layer clone(){
			return new Layer(this);
		}
	}
	
	public class Node{
		public float[] weights;
		public final int nodeNumber;
		
		public Node(int level, int number){
			
			nodeNumber = number;
			
			weights = new float[(level != 0 ? settings.nodesPerLayer[level-1] : 100) + 1];
		}
		
		private Node(Node n){
			nodeNumber = n.nodeNumber;
			weights = n.weights;
		}
		
		public Node clone(){
			return new Node(this);
		}
		
		
		// Checks if the node will output a 0 or 1
		public float activated(float[] inputs){
			float temp = 0;
			
			for(int i = 0; i < inputs.length; i++){
				temp += inputs[i] * weights[i];
			}
			
			// Sigmoid function
			return (float) (1/(1 + Math.pow(Math.E, -temp)));
		}
	}
}
