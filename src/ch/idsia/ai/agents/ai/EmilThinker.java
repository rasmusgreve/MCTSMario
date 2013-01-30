package ch.idsia.ai.agents.ai;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class EmilThinker extends BasicAIAgent {

	private boolean lastOnGround = false;
	
	public EmilThinker() {
		super("EmilSimple");
		reset();
	}

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_JUMP] = false;
        action[Mario.KEY_SPEED] = false;
    }
    
    private boolean enemyAhead(Environment obs){
    	byte[][] nme = obs.getEnemiesObservation();
    	for(int x = 12; x <= 13; x++){
    		for(int y = 10; y <= 12; y++){
    			if(nme[y][x] != 0 && nme[y][x] != 13) return true;
    		}
    	}
    	return false;
    }
    
    private boolean enemyAbove(Environment obs){
    	byte[][] nme = obs.getEnemiesObservation();
    	for(int x = 11; x <= 12; x++){
    		for(int y = 8; y <= 10; y++){
    			if(nme[y][x] != 0 && nme[y][x] != 13) return true;
    		}
    	}
    	return false;
    }
    
    private void shootOrJump(Environment obs){
    	action[Mario.KEY_SPEED] = true;
    	if(obs.getMarioMode() != 2){
    		action[Mario.KEY_JUMP] = true;
    	}
    	// else: fireball anyway
    }
    
    private boolean gapBelow(Environment obs){
    	for(int y = 12; y < 22; y++){
    		if(obs.getCompleteObservation()[y][11] != 0) return false;
    	}
    	return true;
    }
    
    private boolean gapAhead(Environment obs){
    	for(int x = 11; x < 13; x++){
    		boolean gap = true;
    		for(int y = 12; y < 22; y++){
    			if(obs.getCompleteObservation()[y][x] != 0){
    				gap = false;
    				break;
    			}
    			gap = true;
    		}
    		if(gap) return true;
    	}
    	return false;
    }
    
    private boolean blocked(Environment obs){
    	return obs.getCompleteObservation()[11][13] != 0 || obs.getCompleteObservation()[11][12] != 0;
    }
    
    public boolean[] getAction(Environment obs)
    {
    	action[Mario.KEY_RIGHT] = true;
    	action[Mario.KEY_SPEED] = false; // re-evaluate
    	action[Mario.KEY_JUMP] = false;
    	
    	boolean enemyAhead = enemyAhead(obs);
    	boolean gapAhead = gapAhead(obs);
    	boolean blocked = blocked(obs);
    	boolean enemyAbove = enemyAbove(obs);
    	//System.out.println("EnemyAhead: "+enemyAhead+",\tGap: "+gapAhead+",tBlocked: "+blocked+",tEnemyAbove: "+enemyAbove);
    	
    	// if enemy ahead, jump or shoot
    	if(enemyAhead(obs)) shootOrJump(obs);
    	
    	// if gap ahead or blocked jump long
    	if((gapAhead || blocked) && obs.mayMarioJump()){
    		action[Mario.KEY_JUMP] = true;
    		action[Mario.KEY_SPEED] = true;
    	}
    	
    	// stay in jump
    	if(!obs.isMarioOnGround()){
    		if(!gapBelow(obs) && gapAhead){
        		action[Mario.KEY_JUMP] = false;
        		action[Mario.KEY_SPEED] = false;
    		}else{
        		action[Mario.KEY_JUMP] = true;
        		action[Mario.KEY_SPEED] = true;
    		}
    	}
    	
    	// if enemy above, dont jump
    	if(enemyAbove){
    		action[Mario.KEY_JUMP] = false;
    		action[Mario.KEY_RIGHT] = false;
    	}
    	
    	lastOnGround = obs.isMarioOnGround();
    	
    	System.out.println("--------------");
    	//System.out.println(obs.getMarioFloatPos()[0]);
    	//print2DByte(obs.getEnemiesObservation());
    	printFloatArray(obs.getEnemiesFloatPos());
    	return action;
    }
    
    private void printByteArray(byte[] array){
		for(int x = 0; x < array.length; x++){
			System.out.print(array[x]+",");
		}
    }
    
    private void printFloatArray(float[] array){
		for(int x = 0; x < array.length; x++){
			System.out.print(array[x]+";");
		}
    }
    
    private void print2DByte(byte[][] array){
    	for(int y = 0; y < array.length; y++){
    		printByteArray(array[y]);
    		System.out.println();
    	}
    }
}
