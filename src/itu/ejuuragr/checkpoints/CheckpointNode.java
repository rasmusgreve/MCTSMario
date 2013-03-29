package itu.ejuuragr.checkpoints;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointNode extends UCTNode {
	
	private static final float CLEAR_DISTANCE = 8.0f; // the distance needed to get a checkpoint

	private int[][] checkpoints; // [[x,y],[x,y], ... ] Ordered by x-value
	private int cleared;
	
	public CheckpointNode(LevelScene state, boolean[] action, UCTNode parent, int[][] checkpoints, int cleared) {
		super(state, action, parent);
		this.checkpoints = checkpoints;
		this.cleared = cleared;
		
		// check for completed checkpoint
		if(distToPoint(checkpoints[cleared]) <= CLEAR_DISTANCE){
			cleared++;
			this.reward = calculateReward(state); // reward for true progress
		}
	}

	public void setCheckpoints(int[][] checkpoints) {
		this.checkpoints = checkpoints;
		this.cleared = 1; // shouldn't include checkpoints that are behind
	}

	@Override
	public double calculateReward(LevelScene state) {
		double checkpointValue = 0.75 / checkpoints.length; // value for each checkpoint
		
		double result = 0.25; // default value
		result += cleared * checkpointValue; // value for cleared checkpoints
		result += checkpointValue * // progress towards next
				(distToPoint(checkpoints[cleared]) / 
						distBetweenPoints(checkpoints[cleared - 1], checkpoints[cleared])); 
		
		return result;
	}
	
	private double distToPoint(int[] point){
		return MCTSTools.dist(state.mario.x, state.mario.y, point[0], point[1]);
	}
	
	private double distBetweenPoints(int[] p1, int[] p2){
		return MCTSTools.dist(p1[0], p1[1], p2[0], p2[1]);
	}

}
