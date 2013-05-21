package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSAgent;
import itu.ejuuragr.MCTSTools;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.environments.Environment;

/**
 * This Agent for the Mario AI Benchmark is based on Monte Carlo Tree Search.
 * 
 * @author Emil Juul Jacbosen & Rasmus Greve
 */
public class SimpleMCTS extends KeyAdapter implements MCTSAgent<UCTNode> {
	
	protected static int TIME_PER_TICK = 39; //Milliseconds
	public static int ROLLOUT_CAP = 6;
	public static double cp = 0.1875;
	private boolean SAVE_NEXT_TREE = false; //Save next search tree as XML when it is complete
	
	//For simulation error correction
	private float lastX, lastY; 
	private int lastMode = 2;
	
	private String name = "BasicMCTS";
	protected int maxDepth = 0;
	protected UCTNode root = null;
	
	@Override
	public void reset() {
		MCTSTools.print("Agent Reset");
		root = null;
	}
	
	@Override
	public boolean[] getAction(Environment obs) {
		//Simulation error correction
		int m = obs.getMarioMode();
		if (m != lastMode)
			MCTSTools.print("Mode changed: " + m);
		lastMode = m;
		
		return search(obs);
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
	 * Identify and correct simulation errors if any
	 * @param obs The current observation to match the simulation to
	 */
	protected void fixSimulationErrors(Environment obs)
	{
		if (root != null && root.state != null)
		{
			float[] marioPos = obs.getMarioFloatPos();
			if (root.state.mario.x != marioPos[0] || root.state.mario.y != marioPos[1])
			{
				if (root.state.mario.x == marioPos[0])
					MCTSTools.print(String.format("CORRECTED POSITIONS! y: %s -> %s", root.state.mario.y, marioPos[1]));
				else if	(root.state.mario.y == marioPos[1])
					MCTSTools.print(String.format("CORRECTED POSITIONS! x: %s -> %s", root.state.mario.x, marioPos[0]));
				else
					MCTSTools.print(String.format("CORRECTED POSITIONS! x: %s -> %s, y: %s -> %s", root.state.mario.x, marioPos[0], root.state.mario.y, marioPos[1]));
			
				//Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
				//Some code borrowed from Robin Baumgarten
				root.state.mario.x = marioPos[0];
				root.state.mario.xa = (marioPos[0] - lastX) *0.89f;
				if (Math.abs(root.state.mario.y - marioPos[1]) > 0.1f)
					root.state.mario.ya = (marioPos[1] - lastY) * 0.85f;
				root.state.mario.y = marioPos[1];
			}
			if ((root.state.mario.fire && obs.getMarioMode() < 2) || (root.state.mario.large && obs.getMarioMode() < 1) || root.state.mario.deathTime > 0)
			{
				root.state.mario.fire = (obs.getMarioMode() == 2);
				root.state.mario.large = (obs.getMarioMode() >= 1);

				root.state.mario.deathTime = 0; //If he really was dead, getAction() wouldn't get called.
				MCTSTools.print(String.format("CORRECTED MARIO STATE! Fire: %s Large: %s", root.state.mario.fire, root.state.mario.large));
			}
		}
		lastX = obs.getMarioFloatPos()[0];
		lastY = obs.getMarioFloatPos()[1];
	}
	
	/**
	 * Performs MCTS for the most optimal move for 39 ms and
	 * returns the action that seems to lead to the best outcome.
	 * 
	 * @param obs The Environment just received from the game.
	 * @return The action that seems to be best.
	 */
	public boolean[] search(Environment obs){
		long startTime = System.currentTimeMillis();
		long endTime = startTime + TIME_PER_TICK;
		
		if(root == null) initRoot();
		fixSimulationErrors(obs);
		clearRoot(obs);  //reset root, add observation information
		
		maxDepth = 0;
		//Perform as many MCTS iterations as the time allows
		while(System.currentTimeMillis() < endTime){
			UCTNode v1 = treePolicy(root);
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		MCTSTools.print(String.format("Depth: %2d, at %4d nodes %3dms used",maxDepth,root.visited,System.currentTimeMillis() - startTime));
		
		if (SAVE_NEXT_TREE)
			MCTSTools.outputTree("Tree.xml", root);
		
		//Selecting action
		if(root.visited > 1){
			UCTNode choice = root.getBestChild(0);
			if (root.state.mario.fire != choice.state.mario.fire || root.state.mario.large != choice.state.mario.large || choice.state.mario.deathTime > root.state.mario.deathTime)
				MCTSTools.print("I'm gonna die and i know it! ("+choice.state.mario.fire+" , " + choice.state.mario.large + " , " + choice.state.mario.deathTime + ") Reward:" + choice.reward);
			
			if (MCTSTools.DEBUG)
				drawFuture(root);
			
			root = choice;
			return choice.action;
			
		}
		//Root wasn't expanded (no time given). Return default action
		return new boolean[5];
	}
	
	/**
	 * Private helper method for drawing all search nodes to the screen
	 * Collects positions and rewards of the given node and all descendants and store in xs, ys and values 
	 * @param v The node for which to recursively add its and its childrens values
	 * @param xs The list where X values are to be stored
	 * @param ys The list where Y values are to be stored
	 * @param values The list where rewards are to be stored
	 */
	private void addNodePositions(UCTNode v, ArrayList<Integer> xs, ArrayList<Integer> ys, ArrayList<Double> values)
	{
		if (v == null) return;
		xs.add((int)v.state.mario.x);
		ys.add((int)v.state.mario.y);
		values.add(v.reward);
		if (v.children != null)
		for (UCTNode c : v.children)
			addNodePositions(c, xs, ys, values);
	}
	
	/**
	 * Draw the information stored in a MCTS search tree to the screen when next frame is drawn
	 * All nodes in the tree are drawn as circles, and the best path down the tree is drawn as a line
	 * The drawing engine of the game has been modified to allow drawing of this information 
	 * @param v The search tree to draw 
	 */
	protected void drawFuture(UCTNode v)
	{
		//Positions
		ArrayList<Integer> pxs = new ArrayList<Integer>();
		ArrayList<Integer> pys = new ArrayList<Integer>();
		ArrayList<Double> pvalues = new ArrayList<Double>();
		addNodePositions(v, pxs, pys, pvalues);
		
		int[] prx = new int[pxs.size()];
		int[] pry = new int[pxs.size()];
		double[] pvals = new double[pvalues.size()];
		for (int i = 0; i < pxs.size(); i++)
		{
			prx[i] = pxs.get(i);
			pry[i] = pys.get(i);
			pvals[i] = pvalues.get(i);
		}
		MarioComponent.POSITIONS_XS = prx;
		MarioComponent.POSITIONS_YS = pry;
		MarioComponent.POSITIONS_VALUES = pvals;
		
		//Bestline
		ArrayList<Integer> xs = new ArrayList<Integer>();
		ArrayList<Integer> ys = new ArrayList<Integer>();
		
		while (v != null)
		{
			xs.add((int)v.state.mario.x);
			ys.add((int)v.state.mario.y);
			v = v.getBestChild(0);
			
		}
		int[] rx = new int[xs.size()];
		int[] ry = new int[xs.size()];
		for (int i = 0; i < xs.size(); i++)
		{
			rx[i] = xs.get(i);
			ry[i] = ys.get(i);
		}
		MarioComponent.BESTLINE_XS = rx;
		MarioComponent.BESTLINE_YS = ry;
	}
	
	/**
	 * Capture when the space key is pressed and store the current search tree as well as
	 * the frame being drawn to the screen for debugging purposes.
	 */
	public void keyPressed(KeyEvent e)
    {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
			System.out.println("Saving state");
			MarioComponent.SAVE_NEXT_FRAME = true;
			SAVE_NEXT_TREE = true;
        }
    }
	
	/**
	 * Create a new root node
	 * This method is used instead of just using the new keyword in order to make
	 * subtypes able to overwrite the decision of which type of node to create. 
	 */
	public UCTNode createRoot(LevelScene state){
		return new UCTNode(state, null, null);
	}
	
	/**
	 * Initializes the root node with the data in the given state (Environment).
	 */
	protected void initRoot(){
		LevelScene l = new LevelScene();
		l.init();
		l.level = new Level(1500,15);
		root = createRoot(l);
		root.state.tick();
	}
	
	/**
	 * Clear the root node, removing all children and updating it with the observation 
	 * @param obs The observation received from the game engine
	 */
	protected void clearRoot(Environment obs)
	{
		root.reset();
		root.state.setEnemies(obs.getEnemiesFloatPos());
		root.state.setLevelScene(obs.getLevelSceneObservationZ(0));
	}

	/**
	 * From the given MCTreeNode it will go up the tree and add
	 * the reward value to all parents.
	 * 
	 * @param v The MCTreeNode that has received the reward.
	 * @param reward The reward for the given node (how good it is).
	 */
	public void backup(UCTNode v, double reward) {
		int depth = 1;
		v = v.parent;
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
	public double defaultPolicy(UCTNode node) {
		return node.advance(ROLLOUT_CAP);
	}

	/**
	 * Goes through the tree and creates a new leaf somewhere choosing
	 * the path at each step by the bestChild-method (which can either
	 * be exploration or exploitation).
	 * 
	 * @param v The root of the tree.
	 * @return The new leaf.
	 */
	public UCTNode treePolicy(UCTNode v) {
		while(true){
			if(!v.isExpanded()){
				return v.expand();
			}else{
				v = v.getBestChild(cp);
			}
		}
	}
}
