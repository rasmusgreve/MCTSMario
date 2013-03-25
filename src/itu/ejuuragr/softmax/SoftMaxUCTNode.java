package itu.ejuuragr.softmax;

import java.util.ArrayList;
import java.util.List;

import competition.cig.robinbaumgarten.astar.LevelScene;

import itu.ejuuragr.MCTSTools;
import itu.ejuuragr.UCT.UCTNode;

public class SoftMaxUCTNode extends UCTNode {
	
	private static final double Q = 0.25; // 0.0 is average only, 1.0 is max only
	public ArrayList<Double> rewards = new ArrayList<Double>(64);
	public double maxReward = -1;

	public SoftMaxUCTNode(LevelScene state, boolean[] action, SoftMaxUCTNode parent) {
		super(state, action, parent);
		this.rewards.add(this.reward); // reward is own-reward from super(...)
		this.maxReward = this.reward;
	}

	@Override
	public double calculateConfidence(double cp){		
		// copy-pasted
		if(reward <= TERMINAL_MARGIN) return 0.0;
		
		double max = Q*maxReward;
		double avg = (1.0 - Q)*average(rewards);
		double exploitation = max + avg; // softmax
		
		//System.out.printf("max: %.2f avg: %.2f tot: %.2f\n",max,avg,exploitation);
		
		double exploration = cp*Math.sqrt((2*Math.log(parent.visited))/this.visited);
		//System.out.printf("Exploit: %f Explore: %f\n", exploitation, exploration);
		return exploitation + exploration;
	}

	private double average(List<Double> list){
		double result = 0;
		for(Double d : list) result += d;
		return result/list.size();
	}

	@Override
	public UCTNode createChild(boolean[] action) {
		UCTNode child = new SoftMaxUCTNode(MCTSTools.advanceStepClone(state, action),action,this);
		children[MCTSTools.actionToIndex(action)] = child;
		numChildren++;
		
		return child;
	}
}
