package itu.ejuuragr;

import itu.ejuuragr.UCT.EnhancementTester;

public class RandomMCTSAgent extends RandomActionAgent {

	public RandomMCTSAgent() {
		super(new EnhancementTester());
	}

}
