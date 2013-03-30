package itu.ejuuragr.UCT;

import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class MacroActions extends SimpleMCTS {

	public static final int ACTION_SIZE = 3; //How many times to repeat each action
	int moveCount = Integer.MAX_VALUE; //Start invalid every time
	boolean[] curAction;
	
	
	public MacroActions()
	{
		UCTNode.REPETITIONS = ACTION_SIZE; //Ugly but easy (sorry)
	}
	
	@Override
	public UCTNode createRoot(LevelScene state)
	{
		return new MacroActionsNode(state, null, null);
	}
	
	@Override
	public boolean[] getAction(Environment obs)
	{
		if (moveCount++ >= ACTION_SIZE) //Done
		{
			curAction = super.getAction(obs);
			moveCount = 1;
		}
		else
		{
			//Continue work on current tree without changing anything else
			long startTime = System.currentTimeMillis();
			long endTime = startTime + TIME_PER_TICK;
			while(System.currentTimeMillis() < endTime){
				UCTNode v1 = treePolicy(root);
				double reward = defaultPolicy(v1);
				backup(v1,reward);
			}
		}
		return curAction;
	}
}
