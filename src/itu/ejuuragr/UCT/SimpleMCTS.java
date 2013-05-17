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
import ch.idsia.scenarios.Stats;
/**
 * This Agent for the Mario AI Benchmark is based on the UTC MCTS algorithm
 * (Upper Confidence Bound for Trees Monte Carlo Tree Search).
 * 
 * @author Emil & Rasmus
 *
 */
public class SimpleMCTS extends KeyAdapter implements MCTSAgent<UCTNode> {
	
	protected static int TIME_PER_TICK = 39; // milliseconds

	public static int RANDOM_SAMPLES_LIMIT = 6;
	public static double cp = 0.25;
	private boolean SAVE_NEXT_TREE = false;
	protected int maxDepth = 0;
	
	private float lastX, lastY; //For simulation error correction
	private int lastMode = 2;
	
	private String name = "BasicMCTS";
	protected UCTNode root = null;
	
	public SimpleMCTS()
	{
		if (Stats.ARGUMENTS != null && Stats.ARGUMENTS.length >= 4)
		{
			System.out.println("Testing " + getClass().getName());
			RANDOM_SAMPLES_LIMIT = Integer.parseInt(Stats.ARGUMENTS[2]);
			cp = Double.parseDouble(Stats.ARGUMENTS[3]);
			System.out.println("Setting RANDOM_SAMPLES_LIMIT = " + RANDOM_SAMPLES_LIMIT);
			System.out.println("Setting CP = " + cp);
		}
	}
	
	@Override
	public void reset() {
		MCTSTools.print("Agent Reset");
		root = null;
	}
	
	@Override
	public boolean[] getAction(Environment obs) {
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
			if (root.state.mario.fire && obs.getMarioMode() < 2 || root.state.mario.large && obs.getMarioMode() < 1 || root.state.mario.deathTime > 0)
			{
				root.state.mario.fire = (obs.getMarioMode() == 2);
				root.state.mario.large = (obs.getMarioMode() >= 1);

				root.state.mario.deathTime = 0;
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
	 * @param obs The Environment just recieved from the game.
	 * @return The action that seems to be best.
	 */
	public boolean[] search(Environment obs){
		long startTime = System.currentTimeMillis();
		long endTime = startTime + TIME_PER_TICK;
		
		if(root == null) initRoot();
		
		fixSimulationErrors(obs);
		//Before searching
		clearRoot(obs);  //reset root, add observation information
		
		
		//Search
		maxDepth = 0;
		while(System.currentTimeMillis() < endTime){
			UCTNode v1 = treePolicy(root);
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		
		MCTSTools.print(String.format("Depth: %2d, at %4d nodes %3dms used",maxDepth,root.visited,System.currentTimeMillis() - startTime));
		
		

		if (SAVE_NEXT_TREE)
			root.outputTree("Tree.xml");
		
		//Selecting action
		if(root.visited != 1){
			UCTNode choice = root.getBestChild(0);
			if (root.state.mario.fire != choice.state.mario.fire || root.state.mario.large != choice.state.mario.large || choice.state.mario.deathTime > root.state.mario.deathTime)

				MCTSTools.print("I'm gonna die and i know it! ("+choice.state.mario.fire+" , " + choice.state.mario.large + " , " + choice.state.mario.deathTime + ") Reward:" + choice.reward);
			
			if (MCTSTools.DEBUG)
			{
				drawFuture(root);
			}
			
			root = choice;
			return choice.action;
			
		}
		//Root wasn't expanded (no time given). Return default action
		return new boolean[5];
	}
	
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
	
	public void keyPressed (KeyEvent e)
    {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
			System.out.println("Saving state");
			MarioComponent.SAVE_NEXT_FRAME = true;
			SAVE_NEXT_TREE = true;
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
	
	public UCTNode createRoot(LevelScene state){
		return new UCTNode(state, null, null);
	}
	
	/**
	 * Creates a new root node (MCTreeNode) with the data in
	 * the given state (Environment).
	 * 
	 * @param obs The current state of the game to simulate from.
	 */
	protected void initRoot(){
		LevelScene	l = new LevelScene();
		l.init();
		l.level = new Level(1500,15);
		
		root = createRoot(l);
		root.state.tick();
	}
	
	/**
	 * Clear the (newly selected) root node of all children 
	 * and get it ready for searching 
	 * @param obs
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
	 * @param v The MCTreeNode that has recieved the reward.
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
		return node.advanceXandReward(RANDOM_SAMPLES_LIMIT);
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
