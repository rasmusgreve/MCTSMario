package itu.ejuuragr.checkpoints;

import java.util.ArrayList;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointNode extends UCTNode {
	
	public static final float CLEAR_DISTANCE = 8.0f; // the distance needed to get a checkpoint

	private ArrayList<float[]> checkpoints; // [[x,y],[x,y], ... ] Ordered by x-value
	public int cleared;
	
	public CheckpointNode(LevelScene state, boolean[] action, UCTNode parent, ArrayList<float[]> checkpoints, int cleared) {
		super(state, action, parent);
		this.checkpoints = checkpoints;
		this.cleared = cleared;
		
		// check for completed checkpoint
		double dist = distToPoint(checkpoints.get(cleared));
		if(dist <= CLEAR_DISTANCE){
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
		if (parent != null && MCTSTools.marioShrunk(parent.state.mario, state.mario) > 1.0) return 0.0;

		
		double checkpointValue = 0.75 / (checkpoints.size()-1); // value for each checkpoint
		
		double baseValue = 0.25; // default value
		double clearedValue = (cleared-1) * checkpointValue; // value for cleared checkpoints
		double progress = Math.max(0.0, 1.0 - (distToPoint(checkpoints.get(cleared)) / 
				distBetweenPoints(checkpoints.get(cleared - 1), checkpoints.get(cleared))));
		double progressValue = checkpointValue * progress; // progress towards next
		
		double result = baseValue + clearedValue + progressValue;
		if(MCTSTools.isInGap(state)) result /= 10;
				 
		//MCTSTools.print("Reward: "+(baseValue + clearedValue + progressValue)+" cleared: "+clearedValue + " progress: "+progressValue + " ("+checkpoints.size() + " cps)");
		return result;
	}
	
	@Override
	public UCTNode createChild(boolean[] action) {
		UCTNode child = new CheckpointNode(MCTSTools.advanceStepClone(state, action),action, this, checkpoints, cleared);
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
