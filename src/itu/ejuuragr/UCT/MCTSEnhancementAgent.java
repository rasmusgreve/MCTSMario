package itu.ejuuragr.UCT;


import itu.ejuuragr.MCTSTools;
import ch.idsia.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class MCTSEnhancementAgent extends SimpleMCTS {

	public static boolean USE_SOFTMAX 					= true;
	public static boolean USE_MACRO_ACTIONS 			= true;
	public static boolean USE_PARTIAL_EXPANSION 		= true;
	public static boolean USE_ROULETTE_WHEEL_SELECTION  = true;

	public static boolean USE_HOLE_DETECTION  			= true;
	public static boolean USE_LIMITED_ACTIONS 			= true;
	
	//Softmax
	public static double Q = 0.125; // 0 = avg, 1 = max
	//Macro actions
	public static int MACRO_ACTION_SIZE = 2; //How many times to repeat each action
	
	public static final int MONSTER_DANGER_DISTANCE_BACKWARD = 2; //Threshold distance backwards of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_FORWARD = 4; //Threshold distance forwards of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_DOWN = 5; //Threshold distance down of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_UP = 5; //Threshold distance up of monsters for switching to micro actions
	public static final int HOLE_DANGER_DISTANCE = 3; //Threshold distance to holes for switching to micro actions
	
	
	public static int CURRENT_ACTION_SIZE = MACRO_ACTION_SIZE; //The current action size (1 when in danger)
	int moveCount = Integer.MAX_VALUE-1;
	boolean[] curAction;
	
	/**
	 * Create a new MCTS Enhancement Agent
	 */
	public MCTSEnhancementAgent()
	{
		MCTSTools.buttons = new boolean[]{true,true,true,true,true};
		MCTSTools.buildActionsFromButtons();
		Q = (USE_SOFTMAX) ? Q : 0.0;
		MACRO_ACTION_SIZE = (USE_MACRO_ACTIONS) ? MACRO_ACTION_SIZE : 1;
		updateName();
	}
	
	@Override
	public void reset()
	{
		super.reset();
		moveCount = Integer.MAX_VALUE-1; //Reset macro actions counter
	}
	
	/**
	 * Update the name of the agent to tell which enhancements are in use
	 */
	private void updateName()
	{
		setName("uct " + ((USE_SOFTMAX) ? "sof " : "") + ((USE_MACRO_ACTIONS) ? "mac " : "" ) + ((USE_PARTIAL_EXPANSION) ? "par " : "" ) + ((USE_ROULETTE_WHEEL_SELECTION) ? "rou " : "" ) + ((USE_HOLE_DETECTION) ? "hol " : "" ) + ((USE_LIMITED_ACTIONS) ? "lim " : "" ));
	}
	
	/**
	 * Enable or disable softmax
	 */
	public void setSoftmax(boolean v)
	{
		USE_SOFTMAX = v;
		Q = (USE_SOFTMAX) ? 0.125 : 0.0;
		updateName();
	}
	
	/**
	 * Enable or disable macro actions
	 */
	public void setMacro(boolean v)
	{
		USE_MACRO_ACTIONS = v;
		MACRO_ACTION_SIZE = (USE_MACRO_ACTIONS) ? 3 : 1; //How many times to repeat each action
		updateName();
	}
	
	/**
	 * Enable or disable partial expansion
	 */
	public void setPartial(boolean v)
	{
		USE_PARTIAL_EXPANSION = v;
		updateName();
	}
	
	/**
	 * Enable or disable roulette wheel selection
	 */
	public void setRoulette(boolean v)
	{
		USE_ROULETTE_WHEEL_SELECTION = v;
		updateName();
	}
	
	/**
	 * Enable or disable hole detection
	 */
	public void setHole(boolean v)
	{
		USE_HOLE_DETECTION = v;
		updateName();
	}
	
	/**
	 * Enable or disable limited actions
	 */
	public void setLimited(boolean v)
	{
		USE_LIMITED_ACTIONS = v;
		updateName();
	}

	
	/**
	 * Returns true if mario is in danger in the observation.
	 * A dangerous position is close to a monster or near a hole
	 */
	private boolean isInDanger(Environment obs)
	{
		//If a monster is close
		byte[][] enemies = obs.getEnemiesObservation();
		for (int i = 11-MONSTER_DANGER_DISTANCE_UP; i <= 11+MONSTER_DANGER_DISTANCE_DOWN; i++)
		{
			for (int j = 11-MONSTER_DANGER_DISTANCE_BACKWARD; j < 11+MONSTER_DANGER_DISTANCE_FORWARD; j++)
			{
				if (enemies[i][j] == 0) continue; //Air
				if (enemies[i][j] == 25) continue; //Fireball
				return true;
			}
		}
		//If a hole is close in front
		byte[][] map = obs.getLevelSceneObservationZ(0);
		for (int j = 11; j <= 11+HOLE_DANGER_DISTANCE; j++)
		{
			boolean allair = true;
			for (int i = 11; i < 22; i++)
			{
				if (map[i][j] == 0) continue; //Air
				allair = false;
				break;
			}
			if (allair) return true;
		}
		return false;
	}
	
	@Override
	public boolean[] getAction(Environment obs)
	{
		if (moveCount++ >= CURRENT_ACTION_SIZE) //Calculate next move
		{
			if (isInDanger(obs))
			{
				CURRENT_ACTION_SIZE = 1;
			}
			else
			{
				CURRENT_ACTION_SIZE = MACRO_ACTION_SIZE;
			}
			curAction = super.getAction(obs);
			moveCount = 1;
		}
		else //Macro actions
		{
			//Continue work on current tree
			long startTime = System.currentTimeMillis();
			long endTime = startTime + TIME_PER_TICK;
			while(System.currentTimeMillis() < endTime){
				UCTNode v1 = treePolicy(root);
				double reward = defaultPolicy(v1);
				backup(v1,reward);
			}
			if (MCTSTools.DEBUG)
			{
				drawFuture(root);
			}
		}
		return curAction;
	}
	

	/**
	 * Override the base method to create nodes of the MCTSEnhancementNode type.
	 */
	@Override
	public UCTNode createRoot(LevelScene state) {
		return new MCTSEnhancementNode(state,null,null);
	}

	/**
	 * Backup the calculated reward through the tree
	 */
	@Override
	public void backup(UCTNode v, double reward) {
		MCTSEnhancementNode w = (MCTSEnhancementNode)v;
		int depth = 0;
		while(w != null){
			w.visited++;
			w.rewards.add(reward);
			if(reward > w.maxReward) w.maxReward = reward;
			
			w = (MCTSEnhancementNode) w.parent;
			depth++;
		}
		maxDepth = Math.max(maxDepth, depth);
	}

	/**
	 * Perform the MCTS tree policy from a given node
	 * Expands the tree by creating one new node: the most urgent one.
	 */
	@Override
	public UCTNode treePolicy(UCTNode vi) {
		MCTSEnhancementNode v = (MCTSEnhancementNode) vi;
		if (!USE_PARTIAL_EXPANSION)
		{
			while(true){
				if(!v.isExpanded()){
					return v.expand();
				}else{
					v = (MCTSEnhancementNode) v.getBestChild(cp);
				}
			}
		}
		//Partial expansion:
		MCTSEnhancementNode w = (MCTSEnhancementNode) v;
		MCTSTools.Tuple<MCTSEnhancementNode,Boolean> p;
		do{
			p = w.getBestChildTuple(cp);
			w = p.first;
		}
		while(!p.second); // until it is a leaf
		return w;
	}
	
	
}
