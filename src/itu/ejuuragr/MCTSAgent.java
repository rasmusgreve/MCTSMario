package itu.ejuuragr;

import competition.cig.robinbaumgarten.astar.LevelScene;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.environments.Environment;

public interface MCTSAgent<T extends MCTSNode> extends Agent{

	public boolean[] search(Environment obs);
	public T createRoot(LevelScene state);
	public T treePolicy(T v);
	public double defaultPolicy(T v);
	public void backup(T v, double reward);
}
