package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSTools;
import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class MacroActions extends SimpleMCTS {

	public static final int MACRO_ACTION_SIZE = 3; //How many times to repeat each action
	public static final int MONSTER_DANGER_DISTANCE_BACKWARD = 2; //Threshold distance backwards of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_FORWARD = 4; //Threshold distance forwards of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_DOWN = 3; //Threshold distance down of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_UP = 3; //Threshold distance up of monsters for switching to micro actions
	public static final int HOLE_DANGER_DISTANCE = 3; //Threshold distance to holes for switching to micro actions
	
	public static int CURRENT_ACTION_SIZE = MACRO_ACTION_SIZE; //The current action size (1 when in danger)
	int moveCount = Integer.MAX_VALUE-1;
	boolean[] curAction;
	
	public MacroActions()
	{
		setName("MacroActions (" + CURRENT_ACTION_SIZE + ")");
	}
	
	@Override
	public UCTNode createRoot(LevelScene state)
	{
		MacroActionsNode n = new MacroActionsNode(state, null, null);
		n.REPETITIONS = CURRENT_ACTION_SIZE;
		return n;
	}
	
	private boolean isInDanger(Environment obs)
	{
		//If a monster is close
		byte[][] enemies = obs.getEnemiesObservation();
		for (int i = 11-MONSTER_DANGER_DISTANCE_UP; i <= 11+MONSTER_DANGER_DISTANCE_DOWN; i++)
		{
			for (int j = 11-MONSTER_DANGER_DISTANCE_BACKWARD; j < 11+MONSTER_DANGER_DISTANCE_FORWARD; j++)
			{
				if (enemies[i][j] == 0) continue; //Air
				if (enemies[i][j] == 25) continue; //Fireball
				return true;
			}
		}
		//If a hole is close in front
		byte[][] map = obs.getLevelSceneObservationZ(0);
		for (int j = 11; j <= 11+HOLE_DANGER_DISTANCE; j++)
		{
			boolean allair = true;
			for (int i = 11; i < 22; i++)
			{
				if (map[i][j] == 0) continue; //Air
				allair = false;
				break;
			}
			if (allair) return true;
		}
		return false;
	}
	
	@Override
	public boolean[] getAction(Environment obs)
	{
		if (moveCount++ >= CURRENT_ACTION_SIZE) //Calculate next move
		{
			if (isInDanger(obs))
			{
				CURRENT_ACTION_SIZE = 1;
			}
			else
			{
				CURRENT_ACTION_SIZE = MACRO_ACTION_SIZE;
			}
			curAction = super.getAction(obs);
			moveCount = 1;
		}
		else
		{
			//Continue work on current tree
			long startTime = System.currentTimeMillis();
			long endTime = startTime + TIME_PER_TICK;
			while(System.currentTimeMillis() < endTime){
				UCTNode v1 = treePolicy(root);
				double reward = defaultPolicy(v1);
				backup(v1,reward);
			}
			if (MCTSTools.DEBUG)
			{
				drawFuture(root);
			}
		}
		return curAction;
	}
}
