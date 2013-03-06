package itu.ejuuragr.UTCMax;

import itu.ejuuragr.MCTSNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UTC.SimpleMCTS;
import itu.ejuuragr.UTC.UTCNode;


/**
 * A single Node of the Monte Carlo Tree where you can traverse both
 * up (parent) and down (children) from. Each node contains the state
 * that it covers, the action used to get to the state, the total 
 * reward of itself and its children and finally a number of how many
 * times it has been visited (the number of nodes beneath it).
 * 
 * @author Emil
 *
 */
public class UTCNodeMax extends UTCNode{
	
	public UTCNodeMax(LevelScene state, boolean[] action, UTCNode parent) {
		super(state, action, parent);
	}

	@Override
	public double calculateConfidence(double cp){ //TODO: FUCKING DYRT
		if(reward <= 0.0001) return 0.0;
		
		double exploitation = reward; // /this.visited;
		double exploration = cp*Math.sqrt((2*Math.log(parent.visited))/this.visited); // Det er SQRT's SKYLD! :(
		//System.out.printf("Exploit: %f Explore: %f\n", exploitation, exploration);
		return exploitation + exploration;
	}	
	
	@Override
	public UTCNode createChild(boolean[] action){
		UTCNode child = new UTCNodeMax(MCTSTools.advanceStepClone(state, action),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
	
}
