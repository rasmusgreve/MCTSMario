package itu.ejuuragr.UTCPlanning;

import ch.idsia.mario.environments.Environment;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;
import itu.ejuuragr.UTC.*;

import java.util.*;
public class PlanningMCTS extends SimpleMCTS {
	
	private static int PLAN_AHEAD = 5;
	
	private Queue<boolean[]> plan = new LinkedList<boolean[]>();

	private String name = "PlanningMCTS";
	
	@Override
	public void reset()
	{
		LevelScene	l = new LevelScene();
		l.init();
		l.level = new Level(1500,15);
		

		root = createRoot(l);
		root.state.mario.setKeys(new boolean[5]);
		for(int i = 0; i < PLAN_AHEAD; i++){
			plan.add(new boolean[5]);
			root.state.tick();
		}
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean[] getAction(Environment obs)
	{
		long endTime = System.currentTimeMillis() + TIME_PER_TICK;
		maxDepth = 0;
		while(System.currentTimeMillis() < endTime){

			UTCNode v1 = treePolicy(root);
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		//drawFuture(root);
		System.out.println(String.format("Depth: %2d, at %4d nodes",maxDepth,root.visited));
		System.out.println("Plan size: " + plan.size());
		
		if (plan.isEmpty())
		{
			//Extract plan
			UTCNode next = root.getBestChild(0);
			this.clearRoot(obs);

			for (int i = 0; i < PLAN_AHEAD; i++)
			{
				plan.add(next.action);
				if (root.state.mario.fire != next.state.mario.fire || root.state.mario.large != next.state.mario.large || next.state.mario.deathTime > root.state.mario.deathTime)
					System.out.println("I'm gonna die and i know it! ("+next.state.mario.fire+" , " + next.state.mario.large + " , " + next.state.mario.deathTime + ") Reward:" + next.reward);
				
				// tick the simulation on
				root.state.mario.setKeys(next.action);
				root.state.tick();
				
				// carry on
				UTCNode check = next.getBestChild(0);
				if(check != null){
					next = check;
				}else{
					break;
				}
			}
		}
		
		return plan.poll();
	}
}
