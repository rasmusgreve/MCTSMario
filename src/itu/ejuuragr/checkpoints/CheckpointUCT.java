package itu.ejuuragr.checkpoints;

import java.util.ArrayList;
import java.util.Collection;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointUCT extends SimpleMCTS {
	
	private static final int AIR = 0;
	private static final int CANNON = 14;
	private static final int CANNON_MOUNT = 30;
	private static final int TOWER_BASE = 46;
	
	private Environment observations;

	@Override
	public String getName() {
		return "CheckpointUCT";
	}

	@Override
	public UCTNode createRoot(LevelScene state) {
		return new CheckpointNode(state,null,null,calculateCheckpoints(),1);
	}

	@Override
	public boolean[] getAction(Environment obs) {
		observations = obs;
		return super.getAction(obs);
	}

	@Override
	protected void clearRoot(Environment obs) {
		MCTSTools.print("Root cleared = "+((CheckpointNode)root).cleared);
		System.out.println("root reward: "+root.calculateReward(root.state));
		
		super.clearRoot(obs);
		ArrayList<float[]> cp = calculateCheckpoints();
		((CheckpointNode)root).setCheckpoints(cp);
		
		MarioComponent.checkpoints = cp;
		
		// print positions
		MCTSTools.print("Mario pos: "+obs.getMarioFloatPos()[0] + " , "+obs.getMarioFloatPos()[1]);
		for(float[] fs : cp){
			MCTSTools.print("Checkpoint: "+fs[0] + " , "+fs[1]);
		}
	}

	private ArrayList<float[]> calculateCheckpoints() {		
		byte[][] scene = observations.getLevelSceneObservationZ(0);
		float[] marioCoords = observations.getMarioFloatPos();
		
		ArrayList<float[]> checkpoints = new ArrayList<float[]>(5);
		// starting position (for progress towards first checkpoint)
		//checkpoints.add(new float[]{marioCoords[0], marioCoords[1]});
		checkpoints.add(indexToCoordinates(11,11,marioCoords));
		
		// top of high towers
		checkpoints.addAll(findTowers(scene, marioCoords));
		
		// right side of gaps
		checkpoints.addAll(findGapEnds(scene, marioCoords));
		
		// top end of screen point
		checkpoints.add(findEnd(scene, marioCoords));
		
		return checkpoints;
	}

	private ArrayList<float[]> findTowers(byte[][] scene, float[] marioCoords) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		
		for(int y = 0; y < scene.length - 3; y++){
			for(int x = 0; x < scene[y].length; x++){
				if(scene[y][x] == CANNON && scene[y+3][x] == TOWER_BASE){
					// there is a high tower
					result.add(indexToCoordinates(x, y-1, marioCoords));
					System.out.println("Tower found!");
				}
			}
		}
		return result;
	}
	
	private ArrayList<float[]> findGapEnds(byte[][] scene, float[] marioCoords) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		
		boolean lastWasGap = false;
		for(int x = 0; x < scene[0].length; x++){
			boolean isGap = true;
			// is this a gap
			for(int y = 0; y < scene.length; y++){
				if(scene[y][x] != AIR){
					isGap = false;
					break;
				}
			}
			
			if(lastWasGap && !isGap){
				result.add(indexToCoordinates(x, lowestAir(scene, x), marioCoords));
				System.out.println("Gap end found!");
			}
			
			lastWasGap = isGap;
		}
		return result;
	}
	
	private float[] findEnd(byte[][] scene, float[] marioCoords) {
		return indexToCoordinates(21, lowestAir(scene, 21), marioCoords);
	}
	
	private int lowestAir(byte[][] scene, int x){
		for(int y = scene.length-1; y >= 0; y--){
			if(scene[y][x] == AIR){
				return y;
			}
		}
		return -1;
	}
	
	private float[] indexToCoordinates(int x, int y, float[] marioCoords){
		return new float[]{marioCoords[0] + (x-11)*16, marioCoords[1] + (y-11)*16};
	}
}
