package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSNode;

import java.util.ArrayList;
import java.util.Random;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;


/**
 * A single Node of the Monte Carlo search tree where you can traverse both
 * up (parent) and down (children) from. Each node contains the state
 * that it covers, the action used to get to the state, the total 
 * reward of itself and its children and finally a number of how many
 * times it has been visited (the number of nodes beneath it).
 */
public class UCTNode implements MCTSNode{	
	//Instantiate a new pseudo random number generator. If determinism is wanted, this should be seeded, and
	//the while loop performing the search in SimpleMCTS should be changed to a constant amount instead of time
	public static Random rand = new Random();
	
	protected static double TERMINAL_MARGIN = 0.0; //Margin under which a reward is considered a loss
	public LevelScene state = null; //The current game state (simulation)
	public boolean[] action = new boolean[MCTSTools.NUM_CHILDREN]; //The action leading to this node
	public UCTNode parent = null;
	public UCTNode[] children = new UCTNode[MCTSTools.NUM_CHILDREN];
	
	public double reward = 0; //Sum of rewards of nodes below this (this inclusive)
	public int visited = 1; //Count of how many times this node has been visited (equal to subtree size)
	public int REPETITIONS = 1; //How many times an action should be repeated when moving to a child node
	
	//The max x-distance it is possible to move in a single step
	public double MAX_XDIF = ((1+SimpleMCTS.ROLLOUT_CAP)*11.0);
	
	// For statistics
	public int numChildren = 0;

	/**
	 * Construct a new UCT node containing a simulation in a given state
	 * @param state The state that the node should have.
	 * @param action The action leading to the node's state.
	 * @param parent The parent of the new node or null if it is root.
	 */
	public UCTNode(LevelScene state, boolean[] action, UCTNode parent){
		this.state = state;
		this.action = action;
		this.parent = parent;
		if(parent != null) this.reward = this.calculateReward(state);
	}
	
	/**
	 * Expand this node by creating a random child that hasn't yet been added
	 */
	@Override
	public UCTNode expand() {
		ArrayList<Integer> spaces = getUnexpanded();
		return createChild(MCTSTools.indexToAction(spaces.get(rand.nextInt(spaces.size()))));
	}
	
	/**
	 * Tells if the node has all its possible children created.
	 * @return True if no more children can be created, else false.
	 */
	public boolean isExpanded(){
		for(int i = 0; i < MCTSTools.NUM_CHILDREN; i++){
			if(children[i] == null) return false;
		}
		return true;
	}
	
	/**
	 * Creates a new child beneath this node in the tree from the given action.
	 * 
	 * @param action The action to be performed on the current state leading to
	 * the new child.
	 * @return The child node containing the state resulting from performing the
	 * action on the current node.
	 */
	public UCTNode createChild(boolean[] action){
		UCTNode child = new UCTNode(MCTSTools.advanceStepClone(state, action),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
	
	/**
	 * Reset this node
	 */
	public void reset()
	{
		for (UCTNode n : children)
			if (n != null)
				n.parent = null;
		
		children = new UCTNode[MCTSTools.NUM_CHILDREN];
		action = null;
		parent = null;
		reward = 0;
		visited = 1;
		numChildren = 0;
	}
	
	/**
	 * Calculates the confidence in a given node depending on the average reward of
	 * this node (exploitation) (and children) and how neglected the node has been
	 * (exploration).
	 * 
	 * @param cp The constant applied to the exploration part of the equation.
	 * @return A value of how attractive the node is to look into.
	 */
	public double calculateConfidence(double cp){
		if(reward <= TERMINAL_MARGIN) return reward;
		
		double exploitation = reward/this.visited;
		double exploration = cp*Math.sqrt((2*Math.log(parent.visited))/this.visited);
		return exploitation + exploration;
	}
	
	/**
	 * Finds the best direct child by comparing their individual confidence, any ties
	 * are broken randomly.
	 * 
	 * @param cp The constant to use in the exploration part of the confidence equation.
	 * @return The direct child with the highest confidence value.
	 */
	public UCTNode getBestChild(double cp){
		ArrayList<Integer> best = new ArrayList<Integer>();
		double score = -1;
		for(int i = 0; i < MCTSTools.NUM_CHILDREN; i++){
			if(children[i] != null){
				double curScore = children[i].calculateConfidence(cp);
				if (curScore > score)
				{
					score = curScore;
					best.clear();
					best.add(i);
				}
				else if (curScore == score)
				{
					best.add(i);
				}
			}
		}
		return best.isEmpty() ? null : children[best.get(rand.nextInt(best.size()))];
	}
	
	/**
	 * Perform Monte Carlo rollout and calculate the final reward of this node
	 * @param ticks How many random actions to simulate before calculating the reward
	 * @return The calculated reward of the node
	 */
	public double advance(int ticks){
		// check for immediate death
		double curReward = this.calculateReward(state);
		if(curReward <= TERMINAL_MARGIN || curReward >= 1.0) return curReward; // no need to check further
		
		LevelScene copy = null;
		try {
			copy = (LevelScene) state.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return 0.0;
		}
		for(int i = 0; i < ticks; i++){
			advanceStep(copy, getRandomAction());
			if(this.calculateReward(copy) == 0.0) return 0.0;
		}
		return this.calculateReward(copy);
	}
	
	// --- Private Methods
	
	/**
	 * Calculates an ArrayList of the indices for any missing children. This array
	 * will have size = 0 if isExpanded() is true.
	 * 
	 * @return An ArrayList of the indices for any missing children.
	 */
	protected ArrayList<Integer> getUnexpanded(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < MCTSTools.NUM_CHILDREN; i++){
			if(children[i] == null) result.add(i);
		}
		return result;
	}
	
	/**
	 * Advance a step on a simulation
	 * @param state The simulation state to advance on
	 * @param action The action to simulate
	 */
	private void advanceStep(LevelScene state, boolean[] action){
		state.mario.setKeys(action);
		for(int i = 0; i < REPETITIONS; i++){
			state.tick();
		}
	}
	
	/**
	 * Get a random action
	 */
	protected boolean[] getRandomAction(){
		return MCTSTools.indexToAction(rand.nextInt(MCTSTools.NUM_CHILDREN));
	}
	
	/**
	 * Calculates the reward for this state. This can be done through different
	 * heuristics but generally it should rely heavily on how far Mario is through
	 * the level, and be very low if Mario dies or loses size (Mode). The value
	 * must be between 0 and 1 for the current calculateConfidence equation to work
	 * as intended.
	 * 
	 * @return A number between 0 and 1 telling how good the current state is, where
	 * 0 is worst and 1 is best.
	 */
	public double calculateReward(LevelScene state){
		if(state.mario.deathTime > 0 || MCTSTools.marioShrunk(parent.state.mario, state.mario)){
			return 0.0;
		}
		// 0.5 for standing still, 1 for sprinting right, 0 for sprinting left
		double reward = 0.5 + ((state.mario.x - parent.state.mario.x)/MAX_XDIF)/2.0;
		if (reward < 0 || reward > 1){
			MCTSTools.print("Warning! Reward out of bounds: " + reward);
		}
		
		return reward;
	}
}
