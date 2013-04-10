package itu.ejuuragr.highdk;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class HoleDetection extends SimpleMCTS {
	
	public HoleDetection()
	{
		setName("HoleDetectionUCT");
	}
	
	@Override
	public UCTNode createRoot(LevelScene state)
	{
		return new HoleDetectionNode(state, null, null);
	}
}
