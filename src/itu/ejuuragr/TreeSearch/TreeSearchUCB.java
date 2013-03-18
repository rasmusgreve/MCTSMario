package itu.ejuuragr.TreeSearch;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UTC.SimpleMCTS;

public class TreeSearchUCB extends SimpleMCTS {

	public TreeSearchUCB()
	{
		cp = 1.5/8;
		
		setName("Tree Search UCB");
		RANDOM_SAMPLES_LIMIT = 4;
		/*MCTSTools.buttons = new boolean[]{false, true, false, true, false};
		MCTSTools.defBtns = new boolean[]{false, false, false, false, true};
		MCTSTools.CHILDREN = MCTSTools.possibleActionsCount();*/
	}
}
