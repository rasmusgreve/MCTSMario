package itu.ejuuragr.checkpoints;

import java.util.ArrayList;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointNode extends UCTNode {
	
	public static final float CLEAR_DISTANCE = 8.0f; // the distance to be within a checkpoint to get it

	private ArrayList<float[]> checkpoints; // [[x,y],[x,y], ... ] Ordered by x-value
	public int cleared; //the currently cleared checkpoint
	
	/**
	 * Create a new checkpoint node
	 * @param state The simulation state that this node represents
	 * @param action The action leading to this node
	 * @param parent The parent of this node
	 * @param checkpoints The list of checkpoints that are currently being used
	 * @param cleared The id of the currently best cleared checkpoint
	 */
	public CheckpointNode(LevelScene state, boolean[] action, UCTNode parent, ArrayList<float[]> checkpoints, int cleared) {
		super(state, action, parent);
		this.checkpoints = checkpoints;
		this.cleared = cleared;
		
		// check for completed checkpoint
		double dist = distToPoint(checkpoints.get(cleared+1));
		if(dist <= CLEAR_DISTANCE){
			cleared++; // progress
		}
		this.reward = calculateReward(state); 
	}

	/**
	 * Update the list of checkpoint
	 * @param arrayList The new list of checkpoints
	 */
	public void setCheckpoints(ArrayList<float[]> arrayList) {
		this.checkpoints = arrayList;
		this.cleared = 0;
	}

	/**
	 * Calculate the reward of a checkpoint node.
	 */
	@Override
	public double calculateReward(LevelScene state) {
		if(checkpoints == null) return 0.0;
		if (parent != null && MCTSTools.marioShrunk(parent.state.mario, state.mario) || state.mario.deathTime > 0) return 0.0;

		double checkpointValue = 1.0 / (checkpoints.size()-1); // value for each checkpoint
		
		double clearedValue = cleared * checkpointValue; // value for cleared checkpoints
		
		double progress = Math.max(0.0, 1.0 - (distToPoint(checkpoints.get(cleared+1)) / 
				distBetweenPoints(checkpoints.get(cleared), checkpoints.get(cleared+1))));
		double progressValue = checkpointValue * progress; // progress towards next
		
		return clearedValue + progressValue;
	}
	
	/**
	 * Overrides the createChild method in order to create childs of the right type and give the correct parameters to the new child 
	 */
	@Override
	public UCTNode createChild(boolean[] action) {
		UCTNode child = new CheckpointNode(MCTSTools.advanceStepClone(state, action),action, this, checkpoints, cleared);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
	
	/**
	 * Calculate the direct distance from mario to a point
	 */
	private double distToPoint(float[] fs){
		return MCTSTools.dist(state.mario.x, state.mario.y, fs[0], fs[1]);
	}
	
	/**
	 * Calculate the direct distance between two points
	 */
	private double distBetweenPoints(float[] fs, float[] fs2){
		return MCTSTools.dist(fs[0], fs[1], fs2[0], fs2[1]);
	}

}
