package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSTools;

import java.util.ArrayList;
import java.util.List;

import competition.cig.robinbaumgarten.astar.LevelScene;

/**
 * A single Node of the Monte Carlo search tree with enhancements where 
 * you can traverse both up (parent) and down (children) from. Each 
 * node contains the state that it covers, the action used to get to 
 * the state, the total reward of itself and its children and finally 
 * a number of how many times it has been visited (the number of nodes 
 * beneath it). It will read the enabled enhancements from MCTSEnhancementAgent
 * and behave accordingly.
 */
public class MCTSEnhancementNode extends UCTNode {
	
	public ArrayList<Double> rewards = new ArrayList<Double>(64);
	public double maxReward = -1;
	public double expl;
	private int[] actionScores;
	private int scoreSum;

	/**
	 * Creates a new MCTSEnhancementNode starting from the given arguments.
	 * @param state The game state of this node.
	 * @param action The action that led to this node.
	 * @param parent The parent node where this should be hanged under in the tree.
	 */
	public MCTSEnhancementNode(LevelScene state, boolean[] action, MCTSEnhancementNode parent) {
		super(state, action, parent);
		this.rewards.add(this.reward); // reward is own-reward from super(...)
		this.maxReward = this.reward;
		MAX_XDIF = ((MCTSEnhancementAgent.CURRENT_ACTION_SIZE+SimpleMCTS.ROLLOUT_CAP*MCTSEnhancementAgent.CURRENT_ACTION_SIZE)*11.0);
		setScores();
	}

	@Override
	public double calculateConfidence(double cp){		
		if(reward <= TERMINAL_MARGIN) return 0.0;
		
		double max = MCTSEnhancementAgent.Q * maxReward;
		double avg = (1.0 - MCTSEnhancementAgent.Q) * average(rewards);
		double exploitation = max + avg; 
		expl = exploitation;
		double exploration = cp*Math.sqrt((2*Math.log(parent.visited))/this.visited);
		
		return exploitation + exploration;
	}

	/**
	 * Find the average value of a list of numbers.
	 * @param list The list of numbers.
	 * @return The average value of the numbers.
	 */
	private double average(List<Double> list){
		double result = 0;
		for(Double d : list) result += d;
		return result/list.size();
	}

	@Override
	public UCTNode createChild(boolean[] action) {
		MCTSEnhancementNode child = new MCTSEnhancementNode(MCTSTools.advanceStepClone(state, action, MCTSEnhancementAgent.CURRENT_ACTION_SIZE), action, this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		child.REPETITIONS = REPETITIONS;
		
		// check if should be closed
		//checkClosed();
		
		return child;
	}

	@Override
	public double calculateReward(LevelScene state) {
		if (MCTSEnhancementAgent.USE_HOLE_DETECTION && MCTSTools.isInGap(state))
			return super.calculateReward(state) /10;
		return super.calculateReward(state);
	}

	@Override
	public void reset() {
		super.reset();
		setScores();
	}
	
	@Override
	public UCTNode expand()
	{
		int randomToken = rand.nextInt(scoreSum) + 1;
		int index = -1;
		while(randomToken > 0){
			index++;
			randomToken -= this.actionScores[index];
		}
		scoreSum -= actionScores[index];
		actionScores[index] = 0;
		return createChild(MCTSTools.indexToAction(index));
	}
	
	@Override
	public boolean isExpanded()
	{
		for (int i = 0; i < actionScores.length; i++)
		{
			if (actionScores[i] > 0) return false;
		}
		return true;
	}
	
	/**
	 * Sets the scores for actions in the Roulette Wheel, and sets all of them to 1 if Roulette Wheel Selection is disabled.
	 */
	private void setScores(){
		this.actionScores = !MCTSEnhancementAgent.USE_LIMITED_ACTIONS ? new int[]{14,20,17,1,0,0,0,0,48,28,23,1,0,0,0,0,19,14,172,1,0,0,0,0,29,9,242,1,0,0,0,0} 
		: new int[]{0,20,17,0,0,0,0,0,48,28,23,0,0,0,0,0,0,14,172,0,0,0,0,0,29,9,242,0,0,0,0,0};
		
		this.scoreSum =  !MCTSEnhancementAgent.USE_LIMITED_ACTIONS ? 639 : 602;

		if (!MCTSEnhancementAgent.USE_ROULETTE_WHEEL_SELECTION)
		{
			this.scoreSum = 0;
			for (int i = 0; i < actionScores.length; i++)
			{
				if (actionScores[i] > 0) 
				{
					actionScores[i] = 1; //Flatten
					this.scoreSum++;
				}
			}
		}
	}
	
	/**
	 * Finds the child with the best confidence or expands a new 
	 * child if the value for a new node is higher than that of
	 * any existing child.
	 * @param cp The Cp constant to calculate the confidence for the 
	 * individual nodes.
	 * @return A Tuple containing the chosen node and a boolean telling
	 * if the given node was new or an existing one.
	 */
	public MCTSTools.Tuple<MCTSEnhancementNode,Boolean> getBestChildTuple(double cp) {
		double newScore = calculateConfidenceNew(cp);
		int best = -1;
		double score = -1;
		for(int i = 0; i < children.length; i++){
			if(children[i] != null){
				double curScore = children[i].calculateConfidence(cp);
				
				if(curScore > score || (curScore == score && rand.nextBoolean())){
					score = curScore;
					best = i;
				}
			}
		}
		
		if(scoreSum > 0 && newScore > score){
			// find best child left (heuristically weighted random)			
			int randomToken = rand.nextInt(scoreSum) + 1;
			int index = -1;
			while(randomToken > 0){
				index++;
				randomToken -= this.actionScores[index];
			}
			this.scoreSum -= this.actionScores[index];
			this.actionScores[index] = 0;
			
			return new MCTSTools.Tuple<MCTSEnhancementNode,Boolean>((MCTSEnhancementNode)createChild(MCTSTools.indexToAction(index)),true);
		}
		
		return new MCTSTools.Tuple<MCTSEnhancementNode,Boolean>((MCTSEnhancementNode)children[best],false); // existing node
	}

	/**
	 * Calculates the confidence in expanding a new node, used to 
	 * choose between a new instead of using an existing one.
	 * @param cp The Cp constant to calculate the confidence for the 
	 * individual nodes.
	 * @return The confidence for expanding a new child node.
	 */
	private double calculateConfidenceNew(double cp){		
		double exploitation = 0.50;
		double exploration = cp*Math.sqrt(2*Math.log(this.visited)/(this.numChildren+1)); // this is same has dividing by 1 because the new node has obviously never been visited
		return exploitation + exploration;
	}	
}
