package itu.ejuuragr.heuristicpartial;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;

public class HeuristicPartialNode extends UCTNode {
	
	/*
	 * 	Actions by index:
		0: Move="Nothing"
		1: Move="Left "
		2: Move="Right "
		3: Move="Left Right "
		4: Move="Jump "
		5: Move="Left Jump "
		6: Move="Right Jump "
		7: Move="Left Right Jump "
		8: Move="Speed "
		9: Move="Left Speed "
		10: Move="Right Speed "
		11: Move="Left Right Speed "
		12: Move="Jump Speed "
		13: Move="Left Jump Speed "
		14: Move="Right Jump Speed "
		15: Move="Left Right Jump Speed "
	 * 
	 */
	
	private int[] actionScores = {9,22,17,0,19,15,20,0,18,20,223,0,20,16,213,0};
	private int scoreSum = 612;

	public HeuristicPartialNode(LevelScene state, boolean[] action,
			UCTNode parent) {
		super(state, action, parent);
	}

	@Override
	public void reset() {
		super.reset();
		this.actionScores = new int[]{9,22,17,0,19,15,20,0,18,20,223,0,20,16,213,0};
		this.scoreSum = 612;
	}

	@Override
	public UCTNode createChild(boolean[] action) {
		UCTNode child = new HeuristicPartialNode(MCTSTools.advanceStepClone(state, action),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}

	// This is where it REALLY happens!
	public MCTSTools.Tuple<HeuristicPartialNode,Boolean> getBestChildPair(double cp) {
		double newScore = calculateConfidenceNew(cp);
		int best = -1;
		double score = -1;
		for(int i = 0; i < MCTSTools.CHILDREN; i++){
			double curScore;
			if(children[i] != null){
				curScore = children[i].calculateConfidence(cp);
			}else if(actionScores[i] != 0){ // the difference
				curScore = newScore;
			}else{
				continue;
			}
			
			if(curScore > score || (curScore == score && rand.nextBoolean())){
				score = curScore;
				best = i;
			}
		}
		
		if(best > -1){
			if(children[best] != null) return new MCTSTools.Tuple<HeuristicPartialNode,Boolean>((HeuristicPartialNode)children[best],false); // existing node

			// find best child left (heuristically weighted random)
			int randomToken = rand.nextInt(scoreSum) + 1;
			int index = -1;
			while(randomToken > 0){
				index++;
				randomToken -= this.actionScores[index];
			}
			this.scoreSum -= this.actionScores[index];
			this.actionScores[index] = 0;
			
			return new MCTSTools.Tuple<HeuristicPartialNode,Boolean>((HeuristicPartialNode)createChild(MCTSTools.indexToAction(index)),true);
		}
		System.err.println("Returning null, no best found");
		System.err.println("numChildren: "+this.numChildren);
		System.err.print("actionScores: ");
		for(int i : actionScores) System.err.print(i + ",");
		System.err.println();
		return null;
	}
	
	private double calculateConfidenceNew(double cp){		
		double exploitation = 0.5;
		double exploration = cp*Math.sqrt(2*Math.log(this.visited)); // this is same has dividing by 1 because the new node has obviously never been visited
		//System.out.printf("Exploit: %f Explore: %f\n", exploitation, exploration);
		return exploitation + exploration;
	}
}
