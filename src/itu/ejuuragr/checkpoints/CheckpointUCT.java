package itu.ejuuragr.checkpoints;

import java.util.ArrayList;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointUCT extends SimpleMCTS {
	
	private static final int AIR = 0;
	private static final int CANNON = 14;
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
		super.clearRoot(obs);
		ArrayList<float[]> cp = calculateCheckpoints();
		((CheckpointNode)root).setCheckpoints(cp);
		
		MarioComponent.checkpoints = cp;
	}

	private ArrayList<float[]> calculateCheckpoints() {		
		byte[][] scene = observations.getLevelSceneObservationZ(0);
		float[] marioCoords = observations.getMarioFloatPos();
		
		ArrayList<float[]> checkpoints = new ArrayList<float[]>(5);
		// starting position (for progress towards first checkpoint)
		checkpoints.add(indexToCoordinates(11,11,marioCoords));
		
		// top of high towers
		checkpoints.addAll(findTowers(scene, marioCoords));
		
		// right side of gaps
		checkpoints.addAll(findGapEnds(scene, marioCoords));
		
		// top end of screen point
		checkpoints.add(findEnd(scene, marioCoords));
		
		manageCheckpoints(checkpoints);
		
		return checkpoints;
	}

	private void manageCheckpoints(ArrayList<float[]> checkpoints) {		
		// order them from left to right
		for (int i = 0; i < checkpoints.size();i++)
		{
			for (int j = i+1; j < checkpoints.size();j++)
			{
				if (checkpoints.get(i)[0] > checkpoints.get(j)[0])
				{
					//Swap
					float[] aux = checkpoints.get(i);
					checkpoints.set(i, checkpoints.get(j));
					checkpoints.set(j, aux);
				}
			}
		}
		
	}
	
	private boolean inFront(float[] pos, float[] marioCoords){
		return marioCoords[0] + CheckpointNode.CLEAR_DISTANCE < pos[0];
	}

	private ArrayList<float[]> findTowers(byte[][] scene, float[] marioCoords) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		
		for(int y = 0; y < scene.length - 3; y++){
			for(int x = 0; x < scene[y].length; x++){
				if(scene[y][x] == CANNON && scene[y+3][x] == TOWER_BASE){
					// there is a high tower
					float[] pos = indexToCoordinates(x, y-1, marioCoords);
					if(inFront(pos,marioCoords)){
						result.add(pos);
						//System.out.printf("Tower found! [%2d,%2d] (%2f,%2f)\n",x,y,pos[0],pos[1]);
					}
					
				}
			}
		}
		return result;
	}
	
	private ArrayList<float[]> findGapEnds(byte[][] scene, float[] marioCoords) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		
		boolean hasSeenGround = false;
		boolean lastWasGap = false;
		for(int x = 0; x < scene[0].length; x++){
			// is this a gap
			boolean isGap = isGap(x,scene);
			if(!isGap) hasSeenGround = true;

			// is it after gap
			if(hasSeenGround && lastWasGap && !isGap){
				float[] pos = indexToCoordinates(x, lowestAir(scene, x), marioCoords);
				if(inFront(pos,marioCoords)){
					result.add(pos);
					//System.out.printf("Gap found! [%2d,%2d] (%2f,%2f)\n",x,lowestAir(scene, x),pos[0],pos[1]);
				}
				
			}
			
			lastWasGap = isGap;
		}
		return result;
	}
	
	private boolean isGap(int x, byte[][] scene) {
		for(int y = 0; y < scene.length; y++){
			if(scene[y][x] != AIR){
				return false;
			}
		}
		return true;
	}

	private float[] findEnd(byte[][] scene, float[] marioCoords) {
		int x = 21;
		while(x > 0 && isGap(x,scene)) x--;
		return indexToCoordinates(x, lowestAir(scene, x), marioCoords);
	}
	
	private int lowestAir(byte[][] scene, int x){
		int y = 0;
		while( y < 22 && scene[y][x] == AIR) y++;
		return y-1;
	}
	
	private float[] indexToCoordinates(int x, int y, float[] marioCoords){
		return new float[]{marioCoords[0] + (x-11)*16, marioCoords[1] + (y-11)*16};
	}
}
