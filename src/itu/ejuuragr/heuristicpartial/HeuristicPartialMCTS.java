package itu.ejuuragr.heuristicpartial;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
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
		HeuristicPartialNode w = (HeuristicPartialNode) v;
		MCTSTools.Tuple<HeuristicPartialNode,Boolean> p;
		do{
			p = w.getBestChildPair(cp);
			w = p.first;
		}
		while(!p.second); // until it is a leaf
		return w;
	}

}
