package itu.ejuuragr.highdk;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;

public class LimitedActions1Node extends UCTNode {

	public LimitedActions1Node(LevelScene state, boolean[] action, UCTNode parent) {
		super(state, action, parent);
		
		MCTSTools.buttons = new boolean[]{true, true, false, true, true};
		
	}


	@Override
	public UCTNode createChild(boolean[] action){
		UCTNode child = new LimitedActions1Node(MCTSTools.advanceStepClone(state, action),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
}
