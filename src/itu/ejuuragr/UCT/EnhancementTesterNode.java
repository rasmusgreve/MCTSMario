package itu.ejuuragr.UCT;

import itu.ejuuragr.MCTSTools;

import java.util.ArrayList;
import java.util.List;

import competition.cig.robinbaumgarten.astar.LevelScene;

public class EnhancementTesterNode extends UCTNode {
	
	public ArrayList<Double> rewards = new ArrayList<Double>(64);
	public double maxReward = -1;
	
	private int[] actionScores;
	private int scoreSum;
	
	//public boolean closed = false;

	public EnhancementTesterNode(LevelScene state, boolean[] action, EnhancementTesterNode parent) {
		super(state, action, parent);
		this.rewards.add(this.reward); // reward is own-reward from super(...)
		this.maxReward = this.reward;
		MAX_XDIF = ((EnhancementTester.CURRENT_ACTION_SIZE+SimpleMCTS.RANDOM_SAMPLES_LIMIT*EnhancementTester.CURRENT_ACTION_SIZE)*11.0);
		setScores();
		
		// if end, close
		//if(this.reachedEnd(state)) closed = true;
	}

	@Override
	public double calculateConfidence(double cp){		
		if(reward <= TERMINAL_MARGIN) return 0.0;
		
		double max = EnhancementTester.Q * maxReward;
		double avg = (1.0 - EnhancementTester.Q) * average(rewards);
		double exploitation = max + avg; 
		
		double exploration = cp*Math.sqrt((2*Math.log(parent.visited))/this.visited);
		
		return exploitation + exploration;
	}

	private double average(List<Double> list){
		double result = 0;
		for(Double d : list) result += d;
		return result/list.size();
	}

	@Override
	public UCTNode createChild(boolean[] action) {
		EnhancementTesterNode child = new EnhancementTesterNode(MCTSTools.advanceStepClone(state, action, EnhancementTester.CURRENT_ACTION_SIZE), action, this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		child.REPETITIONS = REPETITIONS;
		
		// check if should be closed
		//checkClosed();
		
		return child;
	}
	
	/*protected void checkClosed() {
		if(!this.isExpanded()) return;
		
		for(UCTNode child : children){
			if(child == null) continue;
			if(!((EnhancementTesterNode)child).closed) return;
		}
		
		// should be closed
		this.closed = true;
		MCTSTools.print("ParentNode closed");
		((EnhancementTesterNode) this.parent).checkClosed();
		return;
	}*/

	@Override
	public double calculateReward(LevelScene state) {
		if (EnhancementTester.USE_HOLE_DETECTION && MCTSTools.isInGap(state))
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
	
	private void setScores(){
		this.actionScores = !EnhancementTester.USE_LIMITED_ACTIONS ? new int[]{14,20,17,1,0,0,0,0,48,28,23,1,0,0,0,0,19,14,172,1,0,0,0,0,29,9,242,1,0,0,0,0} 
		: new int[]{0,20,17,0,0,0,0,0,48,28,23,0,0,0,0,0,0,14,172,0,0,0,0,0,29,9,242,0,0,0,0,0};
		
		this.scoreSum =  !EnhancementTester.USE_LIMITED_ACTIONS ? 639 : 602;

		if (!EnhancementTester.USE_ROULETTE_WHEEL_SELECTION)
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
	
	public MCTSTools.Tuple<EnhancementTesterNode,Boolean> getBestChildTuple(double cp) {
		double newScore = calculateConfidenceNew(cp);
		int best = -1;
		double score = -1;
		for(int i = 0; i < children.length; i++){
			if(children[i] != null /*&& !((EnhancementTesterNode)children[i]).closed*/){
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
			
			return new MCTSTools.Tuple<EnhancementTesterNode,Boolean>((EnhancementTesterNode)createChild(MCTSTools.indexToAction(index)),true);
		}
		
		/*if(best == -1 && !closed){
			System.out.println("should be closed");
		}*/
		
		return new MCTSTools.Tuple<EnhancementTesterNode,Boolean>((EnhancementTesterNode)children[best],false); // existing node
	}

	private double calculateConfidenceNew(double cp){		
		double exploitation = 0.50;
		double exploration = cp*Math.sqrt(2*Math.log(this.visited)/(this.numChildren+1)); // this is same has dividing by 1 because the new node has obviously never been visited
		//System.out.printf("Exploit: %f Explore: %f\n", exploitation, exploration);
		return exploitation + exploration;
	}
	
	private boolean reachedEnd(LevelScene state){
		return state.mario.winTime > 0 || state.mario.x - 176 >= rootX();
	}
	
	private float rootX(){
		UCTNode cur = this;
		while(cur.parent != null){
			cur = cur.parent;
		}
		return cur.state.mario.x;
	}
	
}
