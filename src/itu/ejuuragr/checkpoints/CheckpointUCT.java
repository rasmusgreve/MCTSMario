package itu.ejuuragr.checkpoints;

import java.util.ArrayList;

import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointUCT extends SimpleMCTS {

	@Override
	public String getName() {
		return "CheckpointUCT";
	}

	@Override
	public UCTNode createRoot(LevelScene state) {
		return new CheckpointNode(state,null,null,calculateCheckpoints(),0);
	}

	@Override
	protected void clearRoot(Environment obs) {
		super.clearRoot(obs);
		((CheckpointNode)root).setCheckpoints(calculateCheckpoints());
	}

	private int[][] calculateCheckpoints() {
		ArrayList<float[]> checkpoints = new ArrayList<float[]>(5);
		// starting position (for progress towards first checkpoint)
		checkpoints.add(new float[]{root.state.mario.x, root.state.mario.y});
		
		// top of high towers
		
		// right side of gaps
		
		// top end of screen point
		
		return checkpoints.toArray(new int[checkpoints.size()][2]);
	}
}
