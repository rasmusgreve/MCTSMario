package itu.ejuuragr.checkpoints;

import java.util.ArrayList;

import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class CheckpointUCT extends SimpleMCTS {
	
	//IDs from the game engine
	private static final int AIR = 0;
	private static final int CANNON = 14;
	private static final int TOWER_BASE = 46;
	
	private Environment observations;

	@Override
	public String getName() {
		return "CheckpointUCT";
	}

	/**
	 * Overrides the default createRoot in order to make nodes be CheckpointNodes
	 */
	@Override
	public UCTNode createRoot(LevelScene state) {
		return new CheckpointNode(state,null,null,calculateCheckpoints(),0);
	}

	@Override
	public boolean[] getAction(Environment obs) {
		observations = obs;
		return super.getAction(obs);
	}

	@Override
	protected void clearRoot(Environment obs) {		
		super.clearRoot(obs);
		ArrayList<float[]> cp = calculateCheckpoints(); //Calculate the checkpoints from the current observation
		((CheckpointNode)root).setCheckpoints(cp);
		
		MarioComponent.checkpoints = cp;
	}

	/**
	 * Calculate the checkpoints in the current observation
	 * @return A list of positions of checkpoints ordered from left to right
	 */
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

	/**
	 * Order the checkpoints from left to right
	 * @param checkpoints
	 */
	private void manageCheckpoints(ArrayList<float[]> checkpoints) {		
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
	
	/**
	 * Returns true if a checkpoint is on the right side of mario
	 * @param pos The position of the checkpoint to test
	 * @param marioCoords The position of mario
	 */
	private boolean inFront(float[] pos, float[] marioCoords){
		return marioCoords[0] + CheckpointNode.CLEAR_DISTANCE < pos[0];
	}

	/**
	 * Finds the towers in the observation and return the position of the block just above them
	 * @param scene The scene to search
	 * @param marioCoords The position of Mario
	 */
	private ArrayList<float[]> findTowers(byte[][] scene, float[] marioCoords) {
		ArrayList<float[]> result = new ArrayList<float[]>();
		
		for(int y = 0; y < scene.length - 3; y++){
			for(int x = 0; x < scene[y].length; x++){
				if(scene[y][x] == CANNON && scene[y+3][x] == TOWER_BASE){ //If a cannon has a tower base 3 places below it must be tall
					// there is a high tower
					float[] pos = indexToCoordinates(x, y-1, marioCoords);
					if(inFront(pos,marioCoords)){
						result.add(pos);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Finds the right side of gaps and returns their positions as a list
	 * @param scene The observation to search
	 * @param marioCoords The position of mario
	 */
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
				}
			}
			lastWasGap = isGap;
		}
		return result;
	}
	
	/**
	 * Returns true if a x coordinate is on a gap
	 * @param x The x coordinate to check
	 * @param scene The observation to check in
	 */
	private boolean isGap(int x, byte[][] scene) {
		for(int y = 0; y < scene.length; y++){
			if(scene[y][x] != AIR){
				return false;
			}
		}
		return true;
	}

	/**
	 * Find the position of the right side of the screen
	 * Returns a position on top of the first block that is not air when looking top down
	 * @param scene The observation to search
	 * @param marioCoords The position of mario
	 */
	private float[] findEnd(byte[][] scene, float[] marioCoords) {
		int x = 21;
		while(x > 0 && isGap(x,scene)) x--;
		return indexToCoordinates(x, lowestAir(scene, x), marioCoords);
	}
	
	/**
	 * Get the y coordinate of the lowest air block on a given x position
	 * @param scene The observation to search in
	 * @param x The x coordinate for which to find the lowest air
	 */
	private int lowestAir(byte[][] scene, int x){
		int y = 0;
		while( y < 22 && scene[y][x] == AIR) y++;
		return y-1;
	}
	
	/**
	 * Convert a position in block coordinates (16x16 pixels) to positions in pixel coordinates
	 * @param x The x block coordinate
	 * @param y The y block coordinate
	 * @param marioCoords The position of mario
	 */
	private float[] indexToCoordinates(int x, int y, float[] marioCoords){
		return new float[]{marioCoords[0] + (x-11)*16, marioCoords[1] + (y-11)*16};
	}
}
