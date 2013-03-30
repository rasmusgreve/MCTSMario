package itu.ejuuragr.UCT;

import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class MacroActions extends SimpleMCTS {

	public static final int ACTION_SIZE = 2;
	int moveCount = Integer.MAX_VALUE; //Start invalid every time
	boolean[] curAction;
	
	
	public MacroActions()
	{
		UCTNode.REPETITIONS = ACTION_SIZE; //Not so pretty (sorry)
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
			moveCount = 0;
			//System.out.println("Calculated");
		}
		else
		{
			long startTime = System.currentTimeMillis();
			long endTime = startTime + TIME_PER_TICK;
			while(System.currentTimeMillis() < endTime){
				UCTNode v1 = treePolicy(root);
				double reward = defaultPolicy(v1);
				backup(v1,reward);
			}
			
			
			moveCount++;
			//System.out.println("Didn't calculate");
		}
		return curAction;
	}
}
