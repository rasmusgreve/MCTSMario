package itu.ejuuragr.UTCMax;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UTC.*;


/**
 * This Agent for the Mario AI Benchmark is based on the UTC MCTS algorithm
 * (Upper Confidence Bound for Trees Monte Carlo Tree Search).
 * 
 * @author Emil & Rasmus
 *
 */
public class SimpleMCTSMax extends SimpleMCTS {
	
	private String name = "SimpleMCTSMax";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * From the given MCTreeNode it will go up the tree and add
	 * the reward value to all parents.
	 * 
	 * @param v The MCTreeNode that has recieved the reward.
	 * @param reward The reward for the given node (how good it is).
	 */
	@Override
	public void backup(UTCNode v, double reward) {
		int depth = 0;
		while(v != null){
			v.visited++;
			v.reward = Math.max(v.reward, reward);
			v = v.parent;
			depth++;
		}
		if(depth > maxDepth) maxDepth = depth;
	}
	
	@Override
	public UTCNode createRoot(LevelScene state){
		return new UTCNodeMax(state, null, null);
	}
}
