package me.hii488;

import java.io.Serializable;
import java.util.ArrayList;

import me.hii488.NeuralNetwork.Child;

public class GeneticAlgB extends GeneticAlg {
	/* Should be a slightly more efficient (over time) version of GeneticAlg
	 * 
	 * How and why:
	 * 		Allows mixing of children with lower fitnesses, to keep some more unpredictable behaviour.
	 * 		This unpredictable behaviour is sometimes favourable when used in conjunction with behaviour that
	 * 			grants higher fitnesses earlier, improving overall perfomance.
	 * 		
	 * 		This is more efficient than simply waiting to strike lucky with mutation or hoping to develop a
	 * 			gene-line that already incorporates this from the outset.
	 * 
	 * 		Examples where this would be useful:
	 * 		- In the simple game 2048, a genetic-alg is quick to learn that it should only use two directions
	 * 			most of the time. However it is still necessary to move in the other directions at times to
	 * 			achieve a significant score.
	 * 			The rate at which the fitness increases due to only moving in two directions is so great that
	 * 			any children that move in other directions are quickly lost, and so without manual forwarding
	 * 			of initially bad genes, the likelihood of moving in more than 2 directions is very low.
	 * 
	 * 		Specifically, I will:
	 * 			- Add <n> random children into the mix, which have a proportionally lower chance of being chosen.
	 * 			- Keep <n> random children from the lowest scoring <x> as they are from one generation to the next
	 * 	
	 * Additional:
	 * 		Also implements a "heavyMutate(n)" method, which mutates every single value by the normal mutateMultiplier/n
	 *  
	 *  	Annoying that I have to copy entire methods just to add/change a very small amount of code, maybe I should
	 *  		split up the methods... (I probably wont)
	 */
	

	private static final long serialVersionUID = -1666909929529921193L;

	@Override
	public Child rouletteChoice(ArrayList<Child> incChildren){
		ArrayList<Child> sChildren;
		if(genSettings.mixTop != -1){
			sChildren = new ArrayList<Child>(sortedChildren.subList(incChildren.size()-1-genSettings.mixTop, incChildren.size()));
			for(int i = 0; i < genSettingsB.additionalToMix; i++)
				sChildren.add(sortedChildren.get(Settings.rand.nextInt(sortedChildren.size()-genSettings.mixTop) + genSettings.mixTop));
		}
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
	
	@Override
	public void nextGeneration(){
		generation++;
		if(!alreadySorted) sortedChildren = fitnessSortedChildren(children);
		
		ArrayList<Child> childPool = new ArrayList<Child>();
		
		Child parentA;
		Child parentB;
		Child child;
		
		boolean different;
		while(childPool.size() < genSettings.childrenPerGeneration){
			parentA = rouletteChoice(children);
			do{	parentB = rouletteChoice(children);	} 
			while(NeuralNetwork.areSimilar(parentA, parentB));
			
			if(genSettings.debug)	System.out.println("Parent A: " + parentA.fitness + "\nParent B: " + parentB.fitness);
			
			child = spliceChildren(parentA, parentB);
			child = mutateChild(child);
			
			different = true;
			if(genSettings.insureDifferent)
				for(int j = 0; j < childPool.size() && different; j++)
					if(NeuralNetwork.areSimilar(child, childPool.get(j))) different = false;
			
			if(different)
				for(int j = 0; j < genSettings.additionalTopChildrenKept && different; j++)
					if(NeuralNetwork.areSimilar(child, sortedChildren.get(sortedChildren.size() - 1 - j))) different = false;
			
			if(different) childPool.add(child);
		}
		
		for(int i = genSettings.additionalTopChildrenKept -1; i >= 0; i--)
			childPool.add(sortedChildren.get(sortedChildren.size() - 1 - i));
		
		for(int i = 0; i < genSettingsB.additionalRandKept; i++){
			child = (sortedChildren.get(sortedChildren.size() - Settings.rand.nextInt(genSettingsB.lowestXPotentiallyKept)-1));
			different = true;
			for(int j = 0; j < childPool.size() && different; j++)
				if(NeuralNetwork.areSimilar(child, childPool.get(j))) different = false;
			
			if(different) childPool.add(child);
			else i--;
		}
		
		children = childPool;
		alreadySorted = false;
	}
	
	public Child heavyMutateChild(Child child){
		for(int i = 0; i < child.layers.length; i++){
			for(int j = 0; j < child.layers[i].nodes.length; j++){
				for(int k = 0; k < child.layers[i].nodes[j].weights.length; k++){
					child.layers[i].nodes[j].weights[k] += ((Settings.rand.nextBoolean() == true) ? (Settings.rand.nextFloat()/5) * -1 : Settings.rand.nextFloat()/5)*genSettings.mutateMultiplier;
				}
			}
		}
		return child;
	}
	
	@Override
	public String settingsToString(){
		String s = super.settingsToString();
		
		s += ("Additional To Mix: " + genSettingsB.additionalToMix + "\n");
		s += ("Additional Random Kept: " + genSettingsB.additionalRandKept + "\n");
		s += ("Lowest <x> Potentially Kept:" + genSettingsB.lowestXPotentiallyKept + "\n");
		
		return s;
	}
	
	public GenerationSettingsB genSettingsB = new GenerationSettingsB();
	public class GenerationSettingsB implements Serializable{
		private static final long serialVersionUID = -1786754969232358323L;
		public int additionalToMix = 10;
		public int additionalRandKept = 5;
		public int lowestXPotentiallyKept = 20;
	}

}
