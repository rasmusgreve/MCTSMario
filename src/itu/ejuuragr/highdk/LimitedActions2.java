package itu.ejuuragr.highdk;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.SimpleMCTS;

public class LimitedActions2 extends SimpleMCTS {

	public LimitedActions2()
	{
		MCTSTools.actions.clear();
		MCTSTools.actions.add(new boolean[]{false, true, false, true, true});
		MCTSTools.actions.add(new boolean[]{false, true, false, true, false});
		MCTSTools.actions.add(new boolean[]{false, true, false, false, true});
		MCTSTools.actions.add(new boolean[]{false, false, false, true, true});
		MCTSTools.actions.add(new boolean[]{false, false, false, false, true});
		MCTSTools.actions.add(new boolean[]{false, true, false, false, false});
		MCTSTools.actions.add(new boolean[]{true, false, false, true, false});
		MCTSTools.actions.add(new boolean[]{true, false, false, false, false});
	}
}
