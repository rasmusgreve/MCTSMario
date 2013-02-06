package itu.ejjragr;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class TestAgent implements Agent {

	private String name = "TestAgent";
	private boolean lastJ = false;
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean[] getAction(Environment observation) {
		boolean[] action = new boolean[5];
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_SPEED] = true;
		action[Mario.KEY_JUMP] = !lastJ;
		lastJ = !lastJ;
		return action;
	}

	@Override
	public AGENT_TYPE getType() {
		return AGENT_TYPE.AI;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
