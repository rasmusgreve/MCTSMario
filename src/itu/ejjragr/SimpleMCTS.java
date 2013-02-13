package itu.ejjragr;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.environments.Environment;

/**
 * This Agent for the Mario AI Benchmark is based on the UTC MCTS algorithm
 * (Upper Confidence Bound for Trees Monte Carlo Tree Search).
 * 
 * @author Emil
 *
 */
public class SimpleMCTS extends KeyAdapter implements Agent {
	
	private static int TIME_PER_TICK = 39; // milliseconds
	private static final double cp = 1.0/Math.sqrt(2);
	
	private int maxDepth = 0;
	private boolean picture = false;
	
	private LevelScene ls;
	private boolean[] oldAction = new boolean[5];
	private float lastX, lastY;
	
	private String name = "SimpleMCTS";
	private MCTreeNode root;

	@Override
	public void reset() {
		System.out.println("Agent Reset");
		
		ls = new LevelScene();
		ls.init();
		ls.level = new Level(1500, 15);
		
		root = null;
	}

	public void printDifs(LevelScene ls, Environment obs)
	{
		//System.out.println(String.format("%s / %s -- %s / %s",ls.mario.x,obs.getMarioFloatPos()[0],ls.mario.y,obs.getMarioFloatPos()[1]));
		System.out.println(String.format("Difs: %s %s",obs.getMarioFloatPos()[0]-ls.mario.x,obs.getMarioFloatPos()[1]-ls.mario.y));
	}
	
	@Override
	public boolean[] getAction(Environment obs) {
		
		
		ls.mario.setKeys(oldAction);
		ls.tick();
		
		//printDifs(ls,obs);
		
		/*if (ls.mario.x != obs.getMarioFloatPos()[0] || ls.mario.y != obs.getMarioFloatPos()[1])
		{
			System.out.println("CORRECTED POSITIONS!\nCORRECTED POSITIONS!\nCORRECTED POSITIONS!\n");
			// Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
			ls.mario.x = obs.getMarioFloatPos()[0];
			ls.mario.xa = (obs.getMarioFloatPos()[0] - lastX) *0.89f;
			if (Math.abs(ls.mario.y - obs.getMarioFloatPos()[1]) > 0.1f)
				ls.mario.ya = (obs.getMarioFloatPos()[1] - lastY) * 0.85f;// + 3f;

			ls.mario.y = obs.getMarioFloatPos()[1];
		}*/
		
		//lastX = obs.getMarioFloatPos()[0];
		//lastY = obs.getMarioFloatPos()[1];
		
		ls.setLevelScene(obs.getLevelSceneObservationZ(0));
		ls.setEnemies(obs.getEnemiesFloatPos());
		
		boolean[] action = MCTSSearch(obs);
		oldAction = action;
		return action;
	}

	@Override
	public AGENT_TYPE getType() {
		return Agent.AGENT_TYPE.AI;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Performs MCTS for the most optimal move for 39 ms and
	 * returns the action that seems to lead to the best outcome.
	 * It will reuse the tree for MAX_REUSES times and then
	 * create a brand new tree with the given Environment.
	 * 
	 * @param obs The Environment just recieved from the game.
	 * @return The action that seems to be best.
	 */
	private boolean[] MCTSSearch(Environment obs){
		long startTime = System.currentTimeMillis();
		long endTime = startTime + TIME_PER_TICK;
		
		if (root != null && root.state != null)
		printDifs(root.state,obs); //Print
		if (root != null && root.state != null)
		{
			if (root.state.mario.x != obs.getMarioFloatPos()[0] || root.state.mario.y != obs.getMarioFloatPos()[1])
			{
				System.out.println("CORRECTED POSITIONS!");
				// Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
				root.state.mario.x = obs.getMarioFloatPos()[0];
				root.state.mario.xa = (obs.getMarioFloatPos()[0] - lastX) *0.89f;
				if (Math.abs(root.state.mario.y - obs.getMarioFloatPos()[1]) > 0.1f)
					root.state.mario.ya = (obs.getMarioFloatPos()[1] - lastY) * 0.85f;// + 3f;
	
				root.state.mario.y = obs.getMarioFloatPos()[1];
			}
		}
		lastX = obs.getMarioFloatPos()[0];
		lastY = obs.getMarioFloatPos()[1];
		System.out.println(lastX);
		
		if (root == null)
			initRoot(obs); //new LevelScene
		else
			clearRoot(obs); //setEnemies, setLevelScene
		
		
		
		maxDepth = 0;
	
		while(System.currentTimeMillis() < endTime){
			//setKeys(action);
			//tick();
			MCTreeNode v1 = treePolicy(root); //CreateRandChild, CreateChild, advanceStepClone, advanceStep
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		
		
		System.out.println(String.format("Depth: %s, at %s nodes %sms used",maxDepth,root.visited,TIME_PER_TICK));
			
		if(root.visited != 0){
			MCTreeNode choice = root.bestChild(0);
			root = choice;
			
			return choice.action;
		}else{
			return new boolean[5];
		}
		
	}
	
	private void logState(){
		MarioComponent.SAVE_NEXT_FRAME = true;
		root.outputTree("Tree.xml");
	}
	
	public void keyPressed (KeyEvent e)
    {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
			System.out.println("Saving state");
			logState();
        }
		else if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			TIME_PER_TICK += 10;
			System.out.println("Time pr tick: " + TIME_PER_TICK);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) 
		{
			TIME_PER_TICK -= 10;
			while (TIME_PER_TICK < 0) TIME_PER_TICK += 10;
			System.out.println("Time pr tick: " + TIME_PER_TICK);
		}
    }
	
	/**
	 * Creates a new root node (MCTreeNode) with the data in
	 * the given state (Environment).
	 * 
	 * @param obs The current state of the game to simulate from.
	 */
	private void initRoot(Environment obs){
		LevelScene	l = new LevelScene();
		l.init();
		l.level = new Level(1500,15);

		/*l.setLevelScene(obs.getLevelSceneObservationZ(0));
		l.setEnemies(obs.getEnemiesFloatPos());
		l.mario.x = obs.getMarioFloatPos()[0];
		l.mario.y = obs.getMarioFloatPos()[1];
		l.mario.fire = true;
		l.mario.large = true;
		 */
		root = new MCTreeNode(l, null, null);
	}
	
	private void clearRoot(Environment obs)
	{
		//root.state.mario.setKeys(root.action);
		//root.state.tick();
		root.reset();
		
		root.state.setEnemies(obs.getEnemiesFloatPos());
		root.state.setLevelScene(obs.getLevelSceneObservationZ(0));
	}

	/**
	 * From the given MCTreeNode it will go up the tree and add
	 * the reward value to all parents.
	 * 
	 * @param v The MCTreeNode that has recieved the reward.
	 * @param reward The reward for the given node (how good it is).
	 */
	private void backup(MCTreeNode v, double reward) {
		int depth = 0;
		while(v != null){
			v.visited++;
			v.reward += reward;
			v = v.parent;
			depth++;
		}
		if(depth > maxDepth) maxDepth = depth;
	}

	/**
	 * Performs a random simulation on the current node to see how
	 * viable it is.
	 * 
	 * @param node The node to simulate random actions on.
	 * @return The final reward for the node after the simulations.
	 */
	private double defaultPolicy(MCTreeNode node) {
		//double result = node.calculateReward(node.state);
		//return result;
		return node.advanceXandReward(4);
	}

	/**
	 * Goes through the tree and creates a new leaf somewhere choosing
	 * the path at each step by the bestChild-method (which can either
	 * be exploration or exploitation).
	 * 
	 * @param v The root of the tree.
	 * @return The new leaf.
	 */
	private MCTreeNode treePolicy(MCTreeNode v) { // may not be right
		while(true){
			if(!v.isExpanded()){
				return v.createRandomChild();
			}else{
				v = v.bestChild(cp);
			}
		}
	}
}
