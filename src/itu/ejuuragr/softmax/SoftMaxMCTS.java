package itu.ejuuragr.softmax;

import ch.idsia.scenarios.Stats;
import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class SoftMaxMCTS extends SimpleMCTS {
	
	public SoftMaxMCTS()
	{
		if (Stats.ARGUMENTS != null && Stats.ARGUMENTS.length >= 5)
		{
			SoftMaxUCTNode.Q = Double.parseDouble(Stats.ARGUMENTS[4]);
			System.out.println("Setting Q = " + SoftMaxUCTNode.Q);
		}
		setName("SoftMaxMCTS (Q=" + SoftMaxUCTNode.Q + ")");
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
		maxDepth = Math.max(maxDepth, depth);
	}

}
