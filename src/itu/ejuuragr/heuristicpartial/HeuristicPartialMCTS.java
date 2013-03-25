package itu.ejuuragr.heuristicpartial;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.UCT.SimpleMCTS;
import itu.ejuuragr.UCT.UCTNode;

public class HeuristicPartialMCTS extends SimpleMCTS{

	@Override
	public String getName() {
		return "HeuristicPartialMCTS";
	}

	@Override
	public UCTNode createRoot(LevelScene state) {
		return new HeuristicPartialNode(state,null,null);
	}

	@Override
	public UCTNode treePolicy(UCTNode v) {
		do{
			v = v.getBestChild(cp);
		}
		while(v.numChildren > 0); // is it new
		return v;
	}

}
