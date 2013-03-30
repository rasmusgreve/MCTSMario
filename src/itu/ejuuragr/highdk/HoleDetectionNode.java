package itu.ejuuragr.highdk;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class HoleDetectionNode extends UCTNode {

	public HoleDetectionNode(LevelScene state, boolean[] action, UCTNode parent) {
		super(state, action, parent);
	}
	
	@Override
	public UCTNode createChild(boolean[] action){
		HoleDetectionNode child = new HoleDetectionNode(MCTSTools.advanceStepClone(state, action),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
	
	public double calculateReward(LevelScene state){
		double reward;
		if(state.mario.deathTime > 0){
			reward = 0.0;
		}
		else if (MCTSTools.marioShrunk(parent.state.mario, state.mario) > 1.0) {
			reward = 0.0;
		}
		else{
			reward = 0.5 + ((state.mario.x - parent.state.mario.x)/((1+SimpleMCTS.RANDOM_SAMPLES_LIMIT)*11.0))/2.0;

			
			if(HoleDetection.USE_HOLE_DETECTION && MCTSTools.isInGap(state)) reward /= 10; //This is changed
			
			if (reward < 0 || reward > 1) 
			{
				MCTSTools.print("Reward: " + reward);
				MCTSTools.print("X dif: " + (state.mario.x - parent.state.mario.x));
			}
		}
		//System.out.println("reward: " + reward);
		return reward;
	}

}
