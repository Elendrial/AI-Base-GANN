package me.hii488;

import me.hii488.NeuralNetwork.Child;
import me.hii488.NeuralNetwork.Layer;
import me.hii488.NeuralNetwork.Node;

public class BackpropAlg extends LearningAlg{

	public Child c;
	public float momentum = 0.01f;
	public float learningRate = 0.01f;
	
	public float[][] thresholdDiff;
	public float[][] error;

	@SuppressWarnings("unused")
	@Override
	public void setup() {
		c = neuralNet.new Child(); 
		for(Layer l : c.layers){
			for(Node n : l.nodes){
				for(float w : n.weights){
					w = (float) (-1+2*Math.random());
				}
			}
		}
		
		error = new float[c.layers.length][];
		for(int i = 0; i < error.length; i++){
			error[i] = new float[c.layers[i].nodes.length];
			for(int j = 0; j < error[i].length; j++) error[i][j] = 0;
		}
	}

	@Override
	public void iterate(Object o) {
		getOutputs((float[])(((Object[]) o)[0]), null);
		updateNodes((float[])(((Object[]) o)[1]));
	}

	@Override
	public float[] getOutputs(float[] inputs, Object o){
		return recurrentOutput(inputs, 0);
	}
	
	public float[] recurrentOutput(float[] inputs, int layer){
		float[] output;
		
		if(layer < neuralNet.settings.nodesInHiddenLayers.length)
			output = new float[neuralNet.settings.nodesInHiddenLayers[layer]];
		
		else
			output = new float[neuralNet.settings.outputs.length];
				
		for(int i = 0; i < output.length; i++){
			output[i] = c.layers[layer].nodes[i].activated(inputs);
		}
		
		return layer < neuralNet.settings.nodesInHiddenLayers.length ? recurrentOutput(output, layer+1) : output;
	}

	public void updateNodes(float[] expectedOutputs){
		calcErrorOfNodes(expectedOutputs);
		backpropagateError();
	}
	
	public void calcErrorOfNodes(float[] expectedOutputs){
		// Calc output signal error:
		for(int i = 0; i < settings.neuralSettings.outputs.length; i++){
			error[c.layers.length-1][i] = (expectedOutputs[i] - c.layers[c.layers.length-1].nodes[i].lastOutput) * c.layers[c.layers.length-1].nodes[i].lastOutput * (1-c.layers[c.layers.length-1].nodes[i].lastOutput);
		}
		
		float sum;
		// Calculate signal error for all nodes in the hidden layer
		// (back propagate the errors)
		for(int i = c.layers.length - 2; i >= 0; i--){
			for(int j = 0; j < c.layers[i].nodes.length; j++){
				sum = 0;
				
				for(int k = 0; k < c.layers[i+1].nodes.length-1; k++){
					sum += sum + c.layers[i+1].nodes[k].weights[j] * error[i+1][k];
				}
				
				error[i][j] = sum * c.layers[i].nodes[j].lastOutput * (1 - c.layers[i].nodes[j].lastOutput);
			}
		}
		
	}
	
	public void backpropagateError(){
		
		for(int i = c.layers.length - 1; i > 0; i--){
			for(int j = 0; j > c.layers[i].nodes.length; j++){
				thresholdDiff[i][j] = learningRate * error[i][j] + momentum * thresholdDiff[i][j];
				
				c.layers[i].nodes[j].weights[c.layers[i].nodes[j].weights.length-1] = thresholdDiff[i][j];
				
				for(int k = 0; k < c.layers[i].nodes[j].weights.length-1; k++){
					c.layers[i].nodes[j].weightDiffs[k] = learningRate * error[i][j] * c.layers[i-1].nodes[j].lastOutput + momentum * c.layers[i].nodes[j].weightDiffs[k];
					
					c.layers[i].nodes[j].weights[k] += c.layers[i].nodes[j].weightDiffs[k];
				}
			}
		}
		
	}
	
	@Override
	public void printUpdate() {
		
	}

	@Override
	public String settingsToString() {
		return null;
	}

}
