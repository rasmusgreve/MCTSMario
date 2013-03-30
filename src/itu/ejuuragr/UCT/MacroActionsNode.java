package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSTools;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class MacroActionsNode extends UCTNode {

	public MacroActionsNode(LevelScene state, boolean[] action, UCTNode parent) {
		super(state, action, parent);
		
	}

	
	@Override
	public UCTNode createChild(boolean[] action){
		MacroActionsNode child = new MacroActionsNode(MCTSTools.advanceStepClone(state, action, MacroActions.ACTION_SIZE),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
	
	@Override
	public double calculateReward(LevelScene state){
		double reward;
		if(state.mario.deathTime > 0){
			reward = 0.0;
		}
		else if (MCTSTools.marioShrunk(parent.state.mario, state.mario) > 1.0) {
			reward = 0.0; //Almost as bad is dying (but preferred)
		}
		else{
			reward = 0.5 + ((state.mario.x - parent.state.mario.x)/((2+MacroActions.ACTION_SIZE+SimpleMCTS.RANDOM_SAMPLES_LIMIT)*11.0))/2.0; //This is changed

			if(MCTSTools.isInGap(state)) reward /= 10;
			/*if (reward < 0 || reward > 1) 
			{
				MCTSTools.print("Reward: " + reward);
				MCTSTools.print("X dif: " + (state.mario.x - parent.state.mario.x));
			}*/
		}
		//System.out.println("reward: " + reward);
		return reward;
	}
}
