package itu.ejuuragr.UTCPlanning;

import ch.idsia.mario.environments.Environment;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;

import itu.ejuuragr.MCTSAgent;
import itu.ejuuragr.UTC.*;

import java.util.*;
public class PlanningMCTS extends SimpleMCTS {
	private Queue<boolean[]> plan = new LinkedList<boolean[]>();
	private LinkedList<Integer> plan_xs = new LinkedList<Integer>();
	private LinkedList<Integer> plan_ys = new LinkedList<Integer>();

	private String name = "PlanningMCTS";
	
	@Override
	public void reset()
	{
		LevelScene	l = new LevelScene();
		l.init();
		l.level = new Level(1500,15);
		

		root = createRoot(l);
		root.state.tick();
		plan.add(new boolean[5]);
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
		
		if (plan.isEmpty())
		{
			//Extract plan
			LinkedList<boolean[]> temp = new LinkedList<boolean[]>();
			UTCNode next = root.getBestChild(0);
			UTCNode goal = null;
			/*int c = 0;
			while (next != null && c++ < 1)
			{
				temp.add(next.action);
				if (root.state.mario.fire != next.state.mario.fire || root.state.mario.large != next.state.mario.large || next.state.mario.deathTime > root.state.mario.deathTime)
					System.out.println("I'm gonna die and i know it! ("+next.state.mario.fire+" , " + next.state.mario.large + " , " + next.state.mario.deathTime + ") Reward:" + next.reward);
				
				next = next.getBestChild(0);
				
				if (next != null) goal = next;
			}*/
			//plan.addAll(temp);
			plan.add(next.action);
			if (goal != null)
				root = goal;
			root.reset();
			root.state.setEnemies(obs.getEnemiesFloatPos());
			root.state.setLevelScene(obs.getLevelSceneObservationZ(0));
			
		}

		long endTime = System.currentTimeMillis() + TIME_PER_TICK;
		maxDepth = 0;
		while(System.currentTimeMillis() < endTime){

			UTCNode v1 = treePolicy(root);
			double reward = defaultPolicy(v1);
			backup(v1,reward);
		}
		drawFuture(root);
		System.out.println(String.format("Depth: %2d, at %4d nodes",maxDepth,root.visited));
		System.out.println("Plan size: " + plan.size());
		return plan.poll();
	}
	
	private void drawPlan()
	{

	}
}
