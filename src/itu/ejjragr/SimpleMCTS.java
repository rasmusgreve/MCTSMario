package itu.ejjragr;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

/**
 * This Agent for the Mario AI Benchmark is based on the UTC MCTS algorithm
 * (Upper Confidence Bound for Trees Monte Carlo Tree Search).
 * 
 * @author Emil
 *
 */
public class SimpleMCTS implements Agent {
	
	private static final int TIME_PER_TICK = 39; // milliseconds
	private static final double cp = 1.0/Math.sqrt(2);
	
	private int maxDepth = 0;
	private int delayOutput = 0;
	
	private String name = "SimpleMCTS";
	private MCTreeNode root;

	@Override
	public void reset() {
		System.out.println("Agent Reset");
	}

	@Override
	public boolean[] getAction(Environment obs) {
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
		
		clearRoot(obs);
		
		//System.out.println("start: "+root.visited);
		while(System.currentTimeMillis() < endTime){
			MCTreeNode v1 = treePolicy(root);
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		
		System.out.println("size:   "+root.visited);
		System.out.println("depth: "+maxDepth);
		
		if (delayOutput++ > 50)
			root.outputTree("Tree.xml");
		
		if(root.visited != 0){
			MCTreeNode choice = root.bestChild(0);
			root = choice;
			choice.parent = null;
			return choice.action;
		}else{
			return null;
		}
		
	}
	
	/**
	 * Creates a new root node (MCTreeNode) with the data in
	 * the given state (Environment).
	 * 
	 * @param obs The current state of the game to simulate from.
	 */
	private void clearRoot(Environment obs){
		LevelScene l;
		if(root == null){
			l = new LevelScene();
			l.init();
			l.level = new Level(1500,15);
		}else{
			l = root.state;
		}

		l.setLevelScene(obs.getEnemiesObservationZ(0));
		l.setEnemies(obs.getEnemiesFloatPos());
		l.mario.x = obs.getMarioFloatPos()[0];
		l.mario.y = obs.getMarioFloatPos()[1]; // we dont set mario.xa or xy anywhere, may be necessary
		l.mario.large = true;
		l.mario.fire = true;
		
		root = new MCTreeNode(l,null, null);
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
		return node.calculateReward(node.state);
		//return node.advanceXandReward(8);
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
