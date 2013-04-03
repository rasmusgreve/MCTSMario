package itu.ejuuragr.checkpoints;

import java.util.ArrayList;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;
import itu.ejuuragr.heuristicpartial.HeuristicPartialNode;

public class CheckpointNode extends UCTNode {
	
	private static final float CLEAR_DISTANCE = 8.0f; // the distance needed to get a checkpoint

	private ArrayList<float[]> checkpoints; // [[x,y],[x,y], ... ] Ordered by x-value
	private int cleared;
	
	public CheckpointNode(LevelScene state, boolean[] action, UCTNode parent, ArrayList<float[]> checkpoints, int cleared) {
		super(state, action, parent);
		this.checkpoints = checkpoints;
		this.cleared = cleared;
		
		// check for completed checkpoint
		if(distToPoint(checkpoints.get(cleared)) <= CLEAR_DISTANCE){
			cleared++; // progress
		}
		this.reward = calculateReward(state); 
	}

	public void setCheckpoints(ArrayList<float[]> arrayList) {
		this.checkpoints = arrayList;
		this.cleared = 1;
	}

	@Override
	public double calculateReward(LevelScene state) {
		if(checkpoints == null) return 0.0;
		
		double checkpointValue = 0.75 / checkpoints.size(); // value for each checkpoint
		
		double result = 0.25; // default value
		result += cleared * checkpointValue; // value for cleared checkpoints
		result += checkpointValue * // progress towards next
				(distToPoint(checkpoints.get(cleared)) / 
						distBetweenPoints(checkpoints.get(cleared - 1), checkpoints.get(cleared))); 
		
		return result;
	}
	
	@Override
	public UCTNode createChild(boolean[] action) {
		UCTNode child = new CheckpointNode(MCTSTools.advanceStepClone(state, action),action,this, checkpoints, cleared);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
	
	private double distToPoint(float[] fs){
		return MCTSTools.dist(state.mario.x, state.mario.y, fs[0], fs[1]);
	}
	
	private double distBetweenPoints(float[] fs, float[] fs2){
		return MCTSTools.dist(fs[0], fs[1], fs2[0], fs2[1]);
	}

}
