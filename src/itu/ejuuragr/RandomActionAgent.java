package itu.ejuuragr;

import java.util.Random;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

public class RandomActionAgent implements Agent {

	private Agent actualAgent;
	private Random random;
	//Chance of random action: numerator/divisor	
	private int rn_numerator = 2, rn_divisor = 10;
	
	public RandomActionAgent(Agent actualAgent){
		this.actualAgent = actualAgent;
		
		random = new Random(System.currentTimeMillis());
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		actualAgent.reset();
	}
	
	private boolean[] getRandomAction(){
		return new boolean[] {
				random.nextBoolean(),
				random.nextBoolean(),
				random.nextBoolean(),
				random.nextBoolean(),
				random.nextBoolean()};
	}

	@Override
	public boolean[] getAction(Environment observation) {
		boolean[] actualAction = actualAgent.getAction(observation);
		boolean[] randomAction = getRandomAction();
		
		if (random.nextInt(rn_divisor) < rn_numerator)
			return randomAction;

		return actualAction;

	}

	@Override
	public AGENT_TYPE getType() {
		return actualAgent.getType();
	}

	@Override
	public String getName() {
		return "random " + actualAgent.getName();
	}

	@Override
	public void setName(String name) {
		actualAgent.setName(name);
	}

}
