package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Random;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

import itu.ejuuragr.MCTSTools;


/**
 * A single Node of the Monte Carlo Tree where you can traverse both
 * up (parent) and down (children) from. Each node contains the state
 * that it covers, the action used to get to the state, the total 
 * reward of itself and its children and finally a number of how many
 * times it has been visited (the number of nodes beneath it).
 * 
 * @author Emil
 *
 */
public class UCTNode implements MCTSNode{
	
	//private static int CHILDREN = 16;
	//private static final int REPETITIONS = 1; //Unused?
	
	public static Random rand = new Random(1337);
	protected static double TERMINAL_MARGIN = 0.0;
	
	public LevelScene state = null;
	public boolean[] action = new boolean[MCTSTools.CHILDREN];
	public UCTNode parent = null;
	public UCTNode[] children = new UCTNode[MCTSTools.CHILDREN];
	public double reward = 0;
	public int visited = 1;
	
	// for stats
	public int numChildren = 0;

	/**
	 * Constructor for the MCTreeNode.
	 * 
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
	
	@Override
	public UCTNode expand() {
		/*if (MCTSTools.DEBUG && parent != null && calculateReward(state) == 0)
		{
			MCTSTools.print("Expanding a node with 0 reward!!" + MCTSTools.actionToXML(action));
			UCTNode gp = this;
			for (UCTNode p = this; p != null; p = p.parent) gp = p;
			gp.outputTree("ZeroNodeExpanded.xml");
		}*/
		ArrayList<Integer> spaces = getUnexpanded();
		return createChild(MCTSTools.indexToAction(spaces.get(rand.nextInt(spaces.size()))));
	}
	
	/**
	 * Tells if the node has all its possible children created.
	 * 
	 * @return True if no more children can be created, else false.
	 */
	public boolean isExpanded(){
		for(int i = 0; i < MCTSTools.CHILDREN; i++){
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
		
		children = new UCTNode[MCTSTools.CHILDREN];
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
	public double calculateConfidence(double cp){ //TODO: FUCKING DYRT
		if(reward <= TERMINAL_MARGIN) return reward;
		
		double exploitation = reward/this.visited;
		double exploration = cp*Math.sqrt((2*Math.log(parent.visited))/this.visited); // Det er SQRT's SKYLD! :(
		//System.out.printf("Exploit: %f Explore: %f\n", exploitation, exploration);
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
		int best = -1;
		double score = -1;
		for(int i = 0; i < MCTSTools.CHILDREN; i++){
			if(children[i] != null){
				double curScore = children[i].calculateConfidence(cp);
				if(curScore > score || (curScore == score && rand.nextBoolean())){
					score = curScore;
					best = i;
				}
			}
		}
		return best > -1 ? children[best] : null;
	}
	
	public double advanceXandReward(int ticks){
		// check for immediate death
		double curReward = this.calculateReward(state);
		if(curReward <= TERMINAL_MARGIN) return curReward; // no need to check further
		
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
	private ArrayList<Integer> getUnexpanded(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < MCTSTools.CHILDREN; i++){
			if(children[i] == null) result.add(i);
		}
		return result;
	}
	
	private void advanceStep(LevelScene state, boolean[] action){
		state.mario.setKeys(action);
		//for(int i = 0; i < REPETITIONS; i++){
			state.tick();
		//}
	}
	
	protected boolean[] getRandomAction(){
		return MCTSTools.indexToAction(rand.nextInt(MCTSTools.CHILDREN));
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
		if(parent == null) System.out.println("No Parent");
		double reward;
		if(state.mario.deathTime > 0){
			reward = 0.0;
		}
		else if (MCTSTools.marioShrunk(parent.state.mario, state.mario) > 1.0) {
			reward = 0.0; //Almost as bad is dying (but preferred)
		}
		else{
			reward = 0.5 + ((state.mario.x - parent.state.mario.x)/((1+SimpleMCTS.RANDOM_SAMPLES_LIMIT)*11.0))/2.0;

			if(MCTSTools.isInGap(state)) reward /= 10;
			if (reward < 0 || reward > 1) 
			{
				MCTSTools.print("Reward: " + reward);
				MCTSTools.print("X dif: " + (state.mario.x - parent.state.mario.x));
			}
		}
		//System.out.println("reward: " + reward);
		return reward;
	}
	
	/**
	 * Output the tree under this node as XML
	 * @param filename Where to store the generated XML
	 */
	public void outputTree(String filename)
	{
		try {
			String xml = MCTSTools.getXMLRepresentation(this);
			File f = new File(filename);
			FileWriter fw = new FileWriter(f);
			fw.write(xml);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
