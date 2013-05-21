package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSTools;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class MacroActionsNode extends UCTNode {

	public MacroActionsNode(LevelScene state, boolean[] action, UCTNode parent) {
		super(state, action, parent);
		MAX_XDIF = ((MacroActions.CURRENT_ACTION_SIZE+SimpleMCTS.ROLLOUT_CAP*MacroActions.CURRENT_ACTION_SIZE)*11.0);
	}
	
	@Override
	public UCTNode createChild(boolean[] action){
		MacroActionsNode child = new MacroActionsNode(MCTSTools.advanceStepClone(state, action, MacroActions.CURRENT_ACTION_SIZE),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		child.REPETITIONS = REPETITIONS;
		return child;
	}
}
