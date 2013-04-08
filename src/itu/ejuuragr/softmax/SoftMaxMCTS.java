package itu.ejuuragr.softmax;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class SoftMaxMCTS extends SimpleMCTS {

	public SoftMaxMCTS()
	{

	}
	
	@Override
	public String getName() {
		return "SoftMaxMCTS";
	}

	@Override
	public UCTNode createRoot(LevelScene state) {
		return new SoftMaxUCTNode(state,null,null);
	}

	@Override
	public void backup(UCTNode v, double reward) {
		SoftMaxUCTNode w = (SoftMaxUCTNode)v;
		int depth = 0;
		while(w != null){
			w.visited++;
			w.rewards.add(reward);
			if(reward > w.maxReward) w.maxReward = reward;
			
			w = (SoftMaxUCTNode) w.parent;
			depth++;
		}
		if(depth > maxDepth) maxDepth = depth;
	}

}
