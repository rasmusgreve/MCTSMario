package itu.ejuuragr.highdk;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class LimitedActions1 extends SimpleMCTS {

	public LimitedActions1()
	{
		setName("LimitedActions1(-down)");
	}
	

	@Override
	public UCTNode createRoot(LevelScene state){
		return null;
		//return new UCTNodeMax(state, null, null); SORRY :D
	}
}
