package itu.ejuuragr.UCT;


import itu.ejuuragr.MCTSTools;
import ch.idsia.mario.environments.Environment;
import ch.idsia.scenarios.Stats;
import competition.cig.robinbaumgarten.astar.LevelScene;

public class EnhancementTester extends SimpleMCTS {
	
	public static boolean USE_SOFTMAX = false;
	public static boolean USE_MACRO_ACTIONS = false;
	public static boolean USE_PARTIAL_EXPANSION = false;
	public static boolean USE_ROULETTE_WHEEL_SELECTION = true;
	
	public static boolean USE_HOLE_DETECTION = false;
	public static boolean USE_LIMITED_ACTIONS = false;
	
	//Softmax
	public static double Q = (USE_SOFTMAX) ? 0.25 : 0.0; // 0 = avg, 1 = max
	//Macro actions
	public static final int MACRO_ACTION_SIZE = (USE_MACRO_ACTIONS) ? 3 : 1; //How many times to repeat each action
	
	
	
	public static final int MONSTER_DANGER_DISTANCE_BACKWARD = 2; //Threshold distance backwards of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_FORWARD = 4; //Threshold distance forwards of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_DOWN = 5; //Threshold distance down of monsters for switching to micro actions
	public static final int MONSTER_DANGER_DISTANCE_UP = 5; //Threshold distance up of monsters for switching to micro actions
	public static final int HOLE_DANGER_DISTANCE = 3; //Threshold distance to holes for switching to micro actions
	
	public static int CURRENT_ACTION_SIZE = MACRO_ACTION_SIZE; //The current action size (1 when in danger)
	int moveCount = Integer.MAX_VALUE-1;
	boolean[] curAction;
	
	
	public EnhancementTester()
	{
		
		if (Stats.ARGUMENTS != null && Stats.ARGUMENTS.length >= 10)
		{
			USE_SOFTMAX = "1".equals(Stats.ARGUMENTS[4]);
			USE_MACRO_ACTIONS = "1".equals(Stats.ARGUMENTS[5]);
			USE_PARTIAL_EXPANSION = "1".equals(Stats.ARGUMENTS[6]);
			USE_ROULETTE_WHEEL_SELECTION = "1".equals(Stats.ARGUMENTS[7]);
			USE_HOLE_DETECTION = "1".equals(Stats.ARGUMENTS[8]);
			USE_LIMITED_ACTIONS = "1".equals(Stats.ARGUMENTS[9]);
			System.out.println("Settings: " + ((USE_SOFTMAX) ? "sof " : "") + ((USE_MACRO_ACTIONS) ? "mac " : "" ) + ((USE_PARTIAL_EXPANSION) ? "par " : "" ) + ((USE_ROULETTE_WHEEL_SELECTION) ? "rou " : "" ) + ((USE_HOLE_DETECTION) ? "hol " : "" ) + ((USE_LIMITED_ACTIONS) ? "lim " : "" ));
		}
		
		setName(((USE_SOFTMAX) ? "sof " : "") + ((USE_MACRO_ACTIONS) ? "mac " : "" ) + ((USE_PARTIAL_EXPANSION) ? "par " : "" ) + ((USE_ROULETTE_WHEEL_SELECTION) ? "rou " : "" ) + ((USE_HOLE_DETECTION) ? "hol " : "" ) + ((USE_LIMITED_ACTIONS) ? "lim " : "" ));
		
		
	}
	
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
		else
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
	

	@Override
	public UCTNode createRoot(LevelScene state) {
		return new EnhancementTesterNode(state,null,null);
	}

	@Override
	public void backup(UCTNode v, double reward) {
		EnhancementTesterNode w = (EnhancementTesterNode)v;
		int depth = 0;
		while(w != null){
			w.visited++;
			w.rewards.add(reward);
			if(reward > w.maxReward) w.maxReward = reward;
			
			w = (EnhancementTesterNode) w.parent;
			depth++;
		}
		maxDepth = Math.max(maxDepth, depth);
	}

	@Override
	public UCTNode treePolicy(UCTNode v) {
		if (!USE_PARTIAL_EXPANSION)
			return super.treePolicy(v);
		
		EnhancementTesterNode w = (EnhancementTesterNode) v;
		MCTSTools.Tuple<EnhancementTesterNode,Boolean> p;
		do{
			p = w.getBestChildTuple(cp);
			w = p.first;
		}
		while(!p.second); // until it is a leaf
		return w;
	}
	
	
}
