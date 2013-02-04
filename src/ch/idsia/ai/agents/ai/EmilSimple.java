package ch.idsia.ai.agents.ai;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class EmilSimple extends BasicAIAgent {

	private boolean lastOnGround = false;
	
	public EmilSimple() {
		super("EmilSimple");
		reset();
	}

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_JUMP] = true;
        action[Mario.KEY_SPEED] = true;
    }
    
    public boolean[] getAction(Environment observation)
    {
    	action[Mario.KEY_JUMP] = lastOnGround || !observation.isMarioOnGround();
    	lastOnGround = observation.isMarioOnGround();

    	//System.out.println(observation.getMarioMode());
    	//print2DByte(observation.getCompleteObservation());
    	return action;
    }
    
    private void print2DByte(byte[][] array){
    	for(int x = 0; x < array.length; x++){
    		for(int y = 0; y < array[0].length; y++){
    			System.out.print(array[x][y]+",");
    		}
    		System.out.println();
    	}
    }
}
