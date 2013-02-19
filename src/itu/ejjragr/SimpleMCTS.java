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
 * @author Emil & Rasmus
 *
 */
public class SimpleMCTS extends KeyAdapter implements Agent {
	
	private static int TIME_PER_TICK = 39; // milliseconds
	private static final double cp = 1.0/Math.sqrt(2);
	
	private int maxDepth = 0;
	
	private float lastX, lastY; //For simulation error correction
	private int lastMode = 2;
	
	private String name = "BasicMCTS";
	private MCTreeNode root;

	@Override
	public void reset() {
		System.out.println("Agent Reset");
		
		root = null;
	}
	
	@Override
	public boolean[] getAction(Environment obs) {		
		int m = obs.getMarioMode();
		if (m != lastMode)
			System.out.println("Mode changed: " + m);
		lastMode = m;
		
		MarioComponent.MARIOPOS = obs.getMarioFloatPos();
		MarioComponent.MONSTERS = obs.getEnemiesFloatPos();
		System.out.println("Monsters: " + MarioComponent.MONSTERS.length);
		
		return MCTSSearch(obs);
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
		{
			float[] marioPos = obs.getMarioFloatPos();
			if (root.state.mario.x != marioPos[0] || root.state.mario.y != marioPos[1])
			{
				//if (marioPos[0] == lastX && marioPos[1] == lastY)
				//	System.out.println("Game won!?");
				if (root.state.mario.x == marioPos[0])
					System.out.println(String.format("CORRECTED POSITIONS! y: %s -> %s", root.state.mario.y, marioPos[1]));
				else if	(root.state.mario.y == marioPos[1])
					System.out.println(String.format("CORRECTED POSITIONS! x: %s -> %s", root.state.mario.x, marioPos[0]));
				else
					System.out.println(String.format("CORRECTED POSITIONS! x: %s -> %s, y: %s -> %s", root.state.mario.x, marioPos[0], root.state.mario.y, marioPos[1]));
			
				// Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
				root.state.mario.x = marioPos[0];
				root.state.mario.xa = (marioPos[0] - lastX) *0.89f;
				if (Math.abs(root.state.mario.y - marioPos[1]) > 0.1f)
					root.state.mario.ya = (marioPos[1] - lastY) * 0.85f; //+ 3f;
				root.state.mario.y = marioPos[1];
			}
			if (root.state.mario.fire && obs.getMarioMode() < 2 || root.state.mario.large && obs.getMarioMode() < 1)
			{
				if (obs.getMarioMode() == 2)
				{
					root.state.mario.fire = true;
					root.state.mario.large = true;
				}
				else if (obs.getMarioMode() == 1)
				{
					root.state.mario.fire = false;
					root.state.mario.large = true;
				}
				else
				{
					root.state.mario.fire = false;
					root.state.mario.large = false;
				}
				root.state.mario.deathTime = 0;
				System.out.println(String.format("CORRECTED MARIO STATE! Fire: %s Large: %s", root.state.mario.fire, root.state.mario.large));
			}
		}
		lastX = obs.getMarioFloatPos()[0];
		lastY = obs.getMarioFloatPos()[1];
		

		if (root == null)
			initRoot(obs);
		else
			clearRoot(obs); 
		
		maxDepth = 0;
		int c = 1000;
		//while (c-- > 0) {
		while(System.currentTimeMillis() < endTime){
			MCTreeNode v1 = treePolicy(root);
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		
		//System.out.println(String.format("Depth: %2d, at %4d nodes %3dms used",maxDepth,root.visited,System.currentTimeMillis() - startTime));
		
		if(root.visited != 0){
			MCTreeNode choice = root.bestChild(0);
			if (root.state.mario.fire != choice.state.mario.fire || root.state.mario.large != choice.state.mario.large || choice.state.mario.deathTime > root.state.mario.deathTime)
				System.out.println("I'm gonna die and i know it! ("+choice.state.mario.fire+" , " + choice.state.mario.large + " , " + choice.state.mario.deathTime + ") Reward:" + choice.reward);
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
		l.tick(); //TODO: NOTE! First move is always empty

		root = new MCTreeNode(l, null, null);
	}
	
	/**
	 * Clear the (newly selected) root node of all children 
	 * and get it ready for searching 
	 * @param obs
	 */
	private void clearRoot(Environment obs)
	{
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
