package itu.ejuuragr;

import competition.cig.robinbaumgarten.AStarAgent;

public class RandomAStarAgent extends RandomActionAgent {

	public RandomAStarAgent() {
		super(new AStarAgent());
	}

}
