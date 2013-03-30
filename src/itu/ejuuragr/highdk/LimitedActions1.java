package itu.ejuuragr.highdk;


import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.SimpleMCTS;

public class LimitedActions1 extends SimpleMCTS {

	public LimitedActions1()
	{
		setName("LimitedActions1(-down)");
		MCTSTools.buttons = new boolean[]{true, true, false, true, true};
		MCTSTools.buildActions();
	}
	
}
